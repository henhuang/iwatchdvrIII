#include "peer_sdk.h"

#define TAG "__MediaDispatcher-jni__"
using namespace PeerSDK;
using namespace iCatch::Media;
using namespace iCatch;

struct fields_t {
	jfieldID    context;
};
static fields_t fields;

struct methods_t {
	jmethodID MediaDispatcher_CreateVideoBuffer;
	jmethodID MediaDispatcher_ReleaseVideoBuffer;
	jmethodID MediaDispatcher_GetVideoBuffer;
	jmethodID VideoFrameDecodedListener_OnVideoOneFrameDecoded;
	jmethodID Date_getTime;
};
static methods_t methods;

struct class_t {
	jclass Date;
	jclass VideoFrameDecodedListener;
};
static class_t clazz;

// class Mediadispatcher
MediaDispatcherImpl::MediaDispatcherImpl(JNIEnv* env, jobject thiz, int32 channels, int32 core)
{
	mInstance = new MediaDispatcher(channels, core);
	mInstance->VideoDecode += MakeAction(this, &MediaDispatcherImpl::OnVideoDecode);
	mInstance->AudioDecode += MakeAction(this, &MediaDispatcherImpl::OnAudioDecode);
		
	mObject = env->NewGlobalRef(thiz); 

	for (int32 i = 0; i < MAX_DVR_CHANNELS; i++)
	{
		mVideoDecoder[i] = NULL;
		mNeedWaitI[i]    = true;
		//mAudioDecoder[i] = NULL;
		//mAudioFrameCount[i] = 0;
	}
}

MediaDispatcherImpl::~MediaDispatcherImpl()
{
	delete mInstance;
		
	JNIEnv* env = GetJNIEnv();
	env->DeleteGlobalRef(mObject);
	
	for (int32 i = 0; i < MAX_DVR_CHANNELS; i++)
	{
		DisposeVideoDecoder(i);
		DisposeAudioDecoder(i);
	}
}

MediaDispatcher* MediaDispatcherImpl::get()
{
	return mInstance;
}

void MediaDispatcherImpl::CreateVideoDecoder(JNIEnv* env, int32 index, int32 width, int32 height)
{
	LOGD("CreateVideoDecoder: %d, %dx%d", index, width, height);
	mVideoDecoder[index] = new FFmpeg_H264VideoDecoder();
	env->CallVoidMethod(mObject, methods.MediaDispatcher_CreateVideoBuffer, index, width * height);
}
	
void MediaDispatcherImpl::DisposeVideoDecoder(int32 index)
{
	delete mVideoDecoder[index];
	mVideoDecoder[index] = NULL;
}
	
void MediaDispatcherImpl::DisposeAudioDecoder(int32 index)
{
}	

void MediaDispatcherImpl::OnVideoDecode(VideoDecodeEventArgs const& e)
{
	VideoSource const& s = e.Source();
	
	int32 channel = s.Channel(); 

	if (mNeedWaitI[channel])
	{
		if (s.Type() == VideoType_H264_IFrame)
			mNeedWaitI[channel] = false;
		else
			return;
	}
		
	if (mVideoMode[channel].Width() != s.Width() || mVideoMode[channel].Height() != s.Height())
	{
		mVideoMode[channel] = Size(s.Width(), s.Height());
		DisposeVideoDecoder(channel);
	}

	JNIEnv* env = GetJNIEnvAttachThread();

	if (mVideoDecoder[channel] == NULL)
		CreateVideoDecoder(env, channel, s.Width(), s.Height()); // create decoder and buffer

	
	jintArray outputSize = env->NewIntArray(3);
	jint* _outputSize = env->GetIntArrayElements(outputSize, 0);

	
	//
	
	jobjectArray videoBuffer2d = env->CallObjectMethod(mObject, methods.MediaDispatcher_GetVideoBuffer, channel);

	jbyteArray y = (jbyteArray) env->GetObjectArrayElement(videoBuffer2d, (jsize) 0);
	jbyteArray u = (jbyteArray) env->GetObjectArrayElement(videoBuffer2d, (jsize) 1);
	jbyteArray v = (jbyteArray) env->GetObjectArrayElement(videoBuffer2d, (jsize) 2);
	jbyte* _y = env->GetByteArrayElements(y, 0); // not copy array
	jbyte* _u = env->GetByteArrayElements(u, 0); // not copy array
	jbyte* _v = env->GetByteArrayElements(v, 0); // not copy array

	byte*  output[3] = { (byte*) _y, (byte*) _u, (byte*) _v };
	//
	
	Size      resolution;
	Thickness cropped;
	
	if (mVideoDecoder[channel]->Decode(s.Buffer(), s.BufferLength(), VideoPixelFormat_I420, output, _outputSize, resolution, cropped))
	{
		int32 dstW = resolution.Width() - cropped.Left() - cropped.Right();
		int32 dstH = resolution.Height() - cropped.Top() - cropped.Bottom();
		env->CallVoidMethod(mObject, methods.VideoFrameDecodedListener_OnVideoOneFrameDecoded, channel, 
						dstW, dstH, outputSize, VideoPixelFormat_I420, cropped.Left(), cropped.Top());
	}

	else
	{
		LOGE("decode error: %d", channel);
	}

	
	
	env->ReleaseByteArrayElements(y, _y, 0);
	env->ReleaseByteArrayElements(u, _u, 0);
	env->ReleaseByteArrayElements(v, _v, 0);
	env->DeleteLocalRef(y);
	env->DeleteLocalRef(u);
	env->DeleteLocalRef(v);
	env->DeleteLocalRef(videoBuffer2d);
	
	env->ReleaseIntArrayElements(outputSize, _outputSize, 0); // mode: copy back the content and free the elems buffer
	                                                          // MUST to free the elems buffer, because elems buffer holds
															  // a local reference, which needs to delete
	env->DeleteLocalRef(outputSize);
	
	JNIEnvDeattachThread();
}

void MediaDispatcherImpl::OnAudioDecode(AudioDecodeEventArgs const& args)
{
}
//=================================================


static MediaDispatcherImpl* getMediaDispatcherImpl(JNIEnv* env, jobject thiz)
{
	return (MediaDispatcherImpl*)env->GetIntField(thiz, fields.context);
}

static MediaDispatcher* _MediaDispacher(JNIEnv* env, jobject thiz)
{
	return getMediaDispatcherImpl(env, thiz)->get();
}

static void JNICALL peersdk_media_MediaDispatcher_native_setup(JNIEnv* env, jobject thiz, jint channels, jint core)
{
	MediaDispatcherImpl* mdispatcher = new MediaDispatcherImpl(env, thiz, channels, core);
	env->SetIntField(thiz, fields.context, (jint)mdispatcher);
}

static void JNICALL peersdk_media_MediaDispatcher_native_finalize(JNIEnv* env, jobject thiz)
{
	MediaDispatcherImpl* mdispatcher = getMediaDispatcherImpl(env, thiz);
	delete mdispatcher;
}

static void JNICALL peersdk_media_MediaDispatcher_native_addAudio(JNIEnv* env, jobject thiz, jint tag, jint type, jint channel,
	jint bufferLength, jint buffer, jlong pts, jfloat speed, jlong time)
{

	MediaDispatcher* mdispatcher = _MediaDispacher(env, thiz);
	jlong ms = env->CallLongMethod(clazz.Date, methods.Date_getTime, time);
	mdispatcher->AddAudio(tag, type, channel, bufferLength, (byte const*)buffer, pts, speed, iCatch::DateTime(ms / 1000));
}

static void JNICALL peersdk_media_MediaDispatcher_native_addVideo(JNIEnv* env, jobject thiz, jint tag, jint type, jint channel, 
	jint bufferLength, jint buffer, jlong pts, jint width, jint height, jfloat speed, jlong time)
{
	MediaDispatcher* mdispatcher = _MediaDispacher(env, thiz);
	iCatch::DateTime _time = iCatch::DateTime(time / 1000);
	mdispatcher->AddVideo(tag, type, channel, bufferLength, (byte const*)buffer, pts, width, height, speed, _time);
}

//----------------------------------------------
/*
 * Table of methods associated with a single class.
 */
static JNINativeMethod gMethods[] = 
{
	// Method(declare in .class), Signature, FuncPtr
	{"native_setup",           "(II)V",                (void*)peersdk_media_MediaDispatcher_native_setup },
	{"native_finalize",        "()V",                  (void*)peersdk_media_MediaDispatcher_native_finalize },
	{"native_addAudio",        "(IIIIIJFJ)V",          (void*)peersdk_media_MediaDispatcher_native_addAudio },
	{"native_addVideo",        "(IIIIIJIIFJ)V",        (void*)peersdk_media_MediaDispatcher_native_addVideo },
};

static void InitJNIReference(JNIEnv* env)
{
	jniNewClassGlobalReference(env, clazz.VideoFrameDecodedListener, "peersdk/media/VideoFrameDecodedListener");
	methods.VideoFrameDecodedListener_OnVideoOneFrameDecoded = env->GetMethodID(clazz.VideoFrameDecodedListener, 
	                                                                            "OnVideoOneFrameDecoded", 
																				"(III[IIII)V");

	jniNewClassGlobalReference(env, clazz.Date, "java/util/Date");
	methods.Date_getTime = env->GetMethodID(clazz.Date, "getTime", "()J");
}

static const char* const kClassPathName = "peersdk/media/MediaDispatcher";
int register_peersdk_media_MediaDispatcher(JNIEnv *env)
{
	InitJNIReference(env);
	
	jclass clazz = env->FindClass(kClassPathName);
	if (clazz == NULL)
	{
        LOGE("Can't find %s", kClassPathName);
        return -1;
    }
	
	fields.context = env->GetFieldID(clazz, "mNativeContext", "I");

	methods.MediaDispatcher_CreateVideoBuffer = env->GetMethodID(clazz, "CreateVideoBuffer" , "(II)V");
	methods.MediaDispatcher_ReleaseVideoBuffer = env->GetMethodID(clazz, "ReleaseVideoBuffer", "(I)V");
	methods.MediaDispatcher_GetVideoBuffer = env->GetMethodID(clazz, "GetVideoBuffer", "(I)[[B");

	///////////////////////////////////////
	
	if (env->RegisterNatives(clazz, gMethods, sizeof(gMethods) / sizeof(gMethods[0])) < 0)
	{		
		LOGE("RegisterNatives failed for '%s'\n", kClassPathName);
		return -1;
	}

	return 0;
}