#include "peer_sdk.h"

#define TAG "__PeerStream-jni__"

using namespace PeerSDK;

struct fields_t {
	jfieldID    context;
	jfieldID    videoArrivedListener;
	jfieldID    audioArrivedListener;
	jfieldID    errorOccurredListener;
	jfieldID    completedListener;
	jfieldID    progressChangedListener;
};
static fields_t fields;

struct methods_t {
	jmethodID AudioArrivedListener_OnAudioArrived;   // peersdk.peer.PeerStream$AudioArrivedListener_OnAudioArrived;
	jmethodID VideoArrivedListener_OnVideoArrived;   // peersdk.peer.PeerStream$VideoArrivedListener_OnVideoArrived;
	jmethodID ErrorOccurredListener_OnErrorOccurred; // peersdk.peer.PeerStream$ErrorOccurredListener_OnErrorOccurred;
	jmethodID CompletedListener_OnCompleted;
	jmethodID ProgressChangedListener_OnProgressChanged;
	
	jmethodID AudioArrivedEventArgs_Init;
	jmethodID VideoArrivedEventArgs_Init;
	jmethodID ErrorOccurredEventArgs_Init;
	jmethodID CompletedEventArgs_Init;
	jmethodID ProgressChangedEventArgs_Init;
	
	
	jmethodID Date_Init;  // java.util.Date: Date(int year, int month, int day, int hour, int minute, int second)
	jmethodID Date_Init2; // java.util.Date: Date(long milliseconds)
};
static methods_t methods;

struct clazz_t {
	jclass Date; // java.util.Date
	
	jclass AudioArrivedListener;     // peersdk.peer.PeerStream$AudioArrivedListener
	jclass VideoArrivedListener;     // peersdk.peer.PeerStream$VideoArrivedListener
	jclass ErrorOccurredListener;    // peersdk.peer.PeerStream$ErrorOccurredListener
	jclass CompletedListener;        // peersdk.peer.PeerStream$CompletedListener
	jclass ProgressChangedListener;  // peersdk.peer.PeerStream$ProgressChangedListener
	
	jclass AudioArrivedEventArgs;    // peersdk.peer.event.AudioArrivedEventArgs
	jclass VideoArrivedEventArgs;    // peersdk.peer.event.VideoArrivedEventArgs
	jclass ErrorOccurredEventArgs;   // peersdk.peer.event.ErrorOccurredEventArgs
	jclass CompletedEventArgs;       // peersdk.peer.event.CompletedEventArgs
	jclass ProgressChangedEventArgs; // peersdk.peer.event.ProgressChangedEventArgs
};
static clazz_t clazz;

// class PeerStreamImpl
PeerStreamImpl::PeerStreamImpl(JNIEnv* env, jobject thiz)
{
	mInstance = NULL; 
	mObject = env->NewGlobalRef(thiz);
}

PeerStreamImpl::~PeerStreamImpl()
{
	if (mInstance != NULL)
		mInstance->Dispose();
	JNIEnv* env = GetJNIEnv();
	env->DeleteGlobalRef(mObject);
}

PeerStream* PeerStreamImpl::get()
{
	return mInstance;
}

PeerStream** PeerStreamImpl::_get()
{
	return &mInstance;
}

jobject PeerStreamImpl::getObject()
{
	return mObject;
}

// Event callback
static void PEERSDK_CALLBACK OnVideoArrived(void* tag, PeerSDK::VideoArrivedEventArgs const& args)
{
	PeerStreamImpl* impl = (PeerStreamImpl*)tag;
	JNIEnv* env = GetJNIEnvAttachThread();
	
	jobject listener = env->GetObjectField(impl->getObject(), fields.videoArrivedListener);

	DateTime time = args.Time();
	jobject _time = env->NewObject(clazz.Date, methods.Date_Init, 
								time.Year(), time.Month(), time.Day(), time.Minute(), time.Second());

	jobject _args = env->NewObject(clazz.VideoArrivedEventArgs, methods.VideoArrivedEventArgs_Init, 
							args.Type(), args.Channel(), args.BufferLength(), (jint)args.Buffer(), 
							args.PTS(), args.Width(), args.Height(), 1.0, _time);

	env->CallVoidMethod(listener, methods.VideoArrivedListener_OnVideoArrived, impl->getObject(), _args);

	JNIEnvDeattachThread();
}

static void PEERSDK_CALLBACK OnAudioArrived(void* tag, PeerSDK::AudioArrivedEventArgs const& args)
{
	JNIEnv* env = GetJNIEnvAttachThread();
	
	//////////////////////////////////////////////
	DateTime time = args.Time();
	jobject _time = env->NewObject(clazz.Date, methods.Date_Init, 
								time.Year(), time.Month(), time.Day(), time.Minute(), time.Second());

	jobject _args = env->NewObject(clazz.AudioArrivedEventArgs, methods.AudioArrivedEventArgs_Init, 
							args.Type(), args.Channel(), args.BufferLength(), (jint)args.Buffer(), 
							args.PTS(), _time);
	//////////////////////////////////////////////
	
	PeerStreamImpl* impl = (PeerStreamImpl*)tag;
	jobject listener = env->GetObjectField(impl->getObject(), fields.audioArrivedListener);	
	env->CallVoidMethod(listener, methods.AudioArrivedListener_OnAudioArrived, impl->getObject(), _args);

	JNIEnvDeattachThread();
}

static void PEERSDK_CALLBACK OnErrorOccurred(void* tag, PeerSDK::ErrorOccurredEventArgs const& args)
{
	LOGI("PeerSDK_CALLBACK OnErrorOccurred");
	PeerStreamImpl* impl = (PeerStreamImpl*)tag;
	JNIEnv* env = GetJNIEnvAttachThread();
	
	// UTF-8:  NewStringUTF
	// UTF-16: NewString
	// http://www.cnblogs.com/bluesky4485/archive/2011/12/13/2285802.html

	String msg = args.Error().Message();	
	wchar_t const* cmsg = (wchar_t const*)msg;
	jstring _msg = env->NewString((const jchar*)cmsg, msg.Length());
	
	LOGI("PeerSDK_CALLBACK OnErrorOccurred 0");
	jobject _args = env->NewObject(clazz.ErrorOccurredEventArgs, methods.ErrorOccurredEventArgs_Init, (bool)args.Error(), _msg);
	LOGI("PeerSDK_CALLBACK OnErrorOccurred 1");
	jobject listener = env->GetObjectField(impl->getObject(), fields.errorOccurredListener);
	LOGI("PeerSDK_CALLBACK OnErrorOccurred 2");
	env->CallVoidMethod(listener, methods.ErrorOccurredListener_OnErrorOccurred, impl->getObject(), _args);
	LOGI("PeerSDK_CALLBACK OnErrorOccurred done");
	JNIEnvDeattachThread();
}

static void PEERSDK_CALLBACK OnCompleted(void* tag, CompletedEventArgs const& args)
{
	JNIEnv* env = GetJNIEnvAttachThread();
		
	PeerStreamImpl* impl = (PeerStreamImpl*)tag;
	jobject listener = env->GetObjectField(impl->getObject(), fields.completedListener);	
	env->CallVoidMethod(listener, methods.CompletedListener_OnCompleted, impl->getObject(), args.IsTerminated());
	
	JNIEnvDeattachThread();
}

static void PEERSDK_CALLBACK OnProgressChanged(void* tag, ProgressChangedEventArgs const& args)
{
	JNIEnv* env = GetJNIEnvAttachThread();
		
	PeerStreamImpl* impl = (PeerStreamImpl*)tag;
	jobject listener = env->GetObjectField(impl->getObject(), fields.progressChangedListener);	
	env->CallVoidMethod(listener, methods.ProgressChangedListener_OnProgressChanged, impl->getObject(), args.Percentage());
	
	JNIEnvDeattachThread();
}
//===================================================================

PeerStreamImpl* getPeerStreamImpl(JNIEnv* env, jobject thiz)
{
	return (PeerStreamImpl*)env->GetIntField(thiz, fields.context);
}

static PeerStream* _PeerStream(JNIEnv* env, jobject thiz)
{
	return getPeerStreamImpl(env, thiz)->get();
}

static void JNICALL peersdk_peer_PeerStream_native_setup(JNIEnv* env, jobject thiz)
{
	PeerStreamImpl* stream = new PeerStreamImpl(env, thiz);
	env->SetIntField(thiz, fields.context, (jint)stream);
}

static void JNICALL peersdk_peer_PeerStream_native_finalize(JNIEnv* env, jobject thiz)
{
	PeerStreamImpl* stream = getPeerStreamImpl(env, thiz);
	delete stream;
}

static void JNICALL peersdk_peer_PeerStream_native_setOnVideoArrivedListener(JNIEnv* env, jobject thiz)
{
	PeerStream* stream = _PeerStream(env, thiz);
	stream->VideoArrived().Add(getPeerStreamImpl(env, thiz), &OnVideoArrived);
}

static void JNICALL peersdk_peer_PeerStream_native_setOnAudioArrivedListener(JNIEnv* env, jobject thiz)
{
	PeerStream* stream = _PeerStream(env, thiz);
	stream->AudioArrived().Add(getPeerStreamImpl(env, thiz), &OnAudioArrived);
}

static void JNICALL peersdk_peer_PeerStream_native_setOnErrorOccurredListener(JNIEnv* env, jobject thiz)
{
	PeerStream* stream = _PeerStream(env, thiz);
	stream->ErrorOccurred().Add(getPeerStreamImpl(env, thiz), &OnErrorOccurred);
}

static void JNICALL peersdk_peer_PeerStream_native_setOnCompletedListener(JNIEnv* env, jobject thiz)
{
	PeerStream* stream = _PeerStream(env, thiz);
	stream->Completed().Add(getPeerStreamImpl(env, thiz), &OnCompleted);
}

static void JNICALL peersdk_peer_PeerStream_native_setOnProgressChangedListener(JNIEnv* env, jobject thiz)
{
	PeerStream* stream = _PeerStream(env, thiz);
	stream->BackupProgressChanged().Add(getPeerStreamImpl(env, thiz), &OnProgressChanged);
}

static jboolean JNICALL peersdk_peer_PeerStream_Start(JNIEnv* env, jobject thiz)
{
	PeerStream* stream = _PeerStream(env, thiz);
	stream->Start();
	return true;
}

static jboolean JNICALL peersdk_peer_PeerStream_SetActive(JNIEnv* env, jobject thiz, jint active)
{
	PeerStream* stream = _PeerStream(env, thiz);
	return stream->SetActive(active);
}

static jboolean JNICALL peersdk_peer_PeerStream_SetChannelMask(JNIEnv* env, jobject thiz, jint videoMask, jint audioMask)
{
	PeerStream* stream = _PeerStream(env, thiz);
	return stream->SetChannelMask(videoMask, audioMask);
}

static jboolean JNICALL peersdk_peer_PeerStream_Play(JNIEnv* env, jobject thiz)
{
	PeerStream* stream = _PeerStream(env, thiz);
	return stream->Play();
}

static jboolean JNICALL peersdk_peer_PeerStream_Pause(JNIEnv* env, jobject thiz)
{
	PeerStream* stream = _PeerStream(env, thiz);
	return stream->Pause();
}

static jint JNICALL peersdk_peer_PeerStream_Step(JNIEnv* env, jobject thiz)
{
	PeerStream* stream = _PeerStream(env, thiz);
	return stream->Step();
}


static jboolean JNICALL peersdk_peer_PeerStream_SetSpeed(JNIEnv* env, jobject thiz, jdouble speed)
{
	PeerStream* stream = _PeerStream(env, thiz);
	return stream->SetSpeed(speed);
}

static jlong JNICALL peersdk_peer_PeerStream_CalculateRequiredSpace(JNIEnv* env, jobject thiz)
{
	PeerStream* stream = _PeerStream(env, thiz);
	jlong result;
	stream->CalculateRequiredSpace(result);
	return result;
}

static jboolean JNICALL peersdk_peer_PeerStream_SetDownloadLimit(JNIEnv* env, jobject thiz, jlong limit)
{
	PeerStream* stream = _PeerStream(env, thiz);
	return stream->SetDownloadLimit(limit);
}

static jint JNICALL peersdk_peer_PeerStream_Available(JNIEnv* env, jobject thiz)
{
	PeerStream* stream = _PeerStream(env, thiz);
	return stream->Available();
}

static jint JNICALL peersdk_peer_PeerStream_Active(JNIEnv* env, jobject thiz)
{
	PeerStream* stream = _PeerStream(env, thiz);
	return stream->Active();
}

static jboolean JNICALL peersdk_peer_PeerStream_CanSeek(JNIEnv* env, jobject thiz)
{
	PeerStream* stream = _PeerStream(env, thiz);
	return stream->CanSeek();
}

static jdouble JNICALL peersdk_peer_PeerStream_Speed(JNIEnv* env, jobject thiz)
{
	PeerStream* stream = _PeerStream(env, thiz);
	return stream->Speed();
}

static jlong JNICALL peersdk_peer_PeerStream_DownloadLimit(JNIEnv* env, jobject thiz)
{
	PeerStream* stream = _PeerStream(env, thiz);
	return stream->DownloadLimit();
}

static jlong JNICALL peersdk_peer_PeerStream_DownloadRate(JNIEnv* env, jobject thiz)
{
	PeerStream* stream = _PeerStream(env, thiz);
	return stream->DownloadRate();
}

//----------------------------------------------
/*
 * Table of methods associated with a single class.
 */
static JNINativeMethod gMethods[] = 
{
	// Method(declare in .class), Signature, FuncPtr
	{"native_setup",                           "()V",       (void*)peersdk_peer_PeerStream_native_setup },
	{"native_finalize",                        "()V",       (void*)peersdk_peer_PeerStream_native_finalize },
	{"native_setOnVideoArrivedListener",       "()V",       (void*)peersdk_peer_PeerStream_native_setOnVideoArrivedListener },
	{"native_setOnAudioArrivedListener",       "()V",       (void*)peersdk_peer_PeerStream_native_setOnAudioArrivedListener },
	{"native_setOnErrorOccurredListener",      "()V",       (void*)peersdk_peer_PeerStream_native_setOnErrorOccurredListener },
	{"native_setOnCompletedListener",          "()V",       (void*)peersdk_peer_PeerStream_native_setOnCompletedListener },
	{"native_setOnProgressChangedListener",    "()V",       (void*)peersdk_peer_PeerStream_native_setOnProgressChangedListener },

	
	{"Start",                  "()Z",                       (void*)peersdk_peer_PeerStream_Start },
	{"SetActive",              "(I)Z",                      (void*)peersdk_peer_PeerStream_SetActive },
	{"SetChannelMask",         "(II)Z",                     (void*)peersdk_peer_PeerStream_SetChannelMask },
	{"Play",                   "()Z",                       (void*)peersdk_peer_PeerStream_Play },
	{"Pause",                  "()Z",                       (void*)peersdk_peer_PeerStream_Pause },
	{"Step",                   "()Z",                       (void*)peersdk_peer_PeerStream_Step },
	{"SetSpeed",               "(D)Z",                      (void*)peersdk_peer_PeerStream_SetSpeed },
	{"CalculateRequiredSpace", "()J",                       (void*)peersdk_peer_PeerStream_CalculateRequiredSpace },
	{"SetDownloadLimit",       "(J)Z",                      (void*)peersdk_peer_PeerStream_SetDownloadLimit },
	{"Available",              "()I",                       (void*)peersdk_peer_PeerStream_Available },
	{"Active",                 "()I",                       (void*)peersdk_peer_PeerStream_Active },
	{"CanSeek",                "()Z",                       (void*)peersdk_peer_PeerStream_CanSeek },
	{"Speed",                  "()D",                       (void*)peersdk_peer_PeerStream_Speed },
	{"DownloadLimit",          "()J",                       (void*)peersdk_peer_PeerStream_DownloadLimit },
	{"DownloadRate",           "()J",                       (void*)peersdk_peer_PeerStream_DownloadRate },
};

static void InitJNIReference(JNIEnv* env)
{
	jniNewClassGlobalReference(env, clazz.ErrorOccurredListener,   "peersdk/peer/PeerStream$ErrorOccurredListener");
	jniNewClassGlobalReference(env, clazz.AudioArrivedListener,    "peersdk/peer/PeerStream$AudioArrivedListener");
	jniNewClassGlobalReference(env, clazz.VideoArrivedListener,    "peersdk/peer/PeerStream$VideoArrivedListener");
	jniNewClassGlobalReference(env, clazz.CompletedListener,       "peersdk/peer/PeerStream$CompletedListener");
	jniNewClassGlobalReference(env, clazz.ProgressChangedListener, "peersdk/peer/PeerStream$ProgressChangedListener");
	
	jniNewClassGlobalReference(env, clazz.VideoArrivedEventArgs,   "peersdk/peer/event/VideoArrivedEventArgs");
	jniNewClassGlobalReference(env, clazz.AudioArrivedEventArgs,   "peersdk/peer/event/AudioArrivedEventArgs");
	jniNewClassGlobalReference(env, clazz.ErrorOccurredEventArgs,  "peersdk/peer/event/ErrorOccurredEventArgs");
	jniNewClassGlobalReference(env, clazz.CompletedEventArgs,      "peersdk/peer/event/CompletedEventArgs");
	jniNewClassGlobalReference(env, clazz.ProgressChangedEventArgs,"peersdk/peer/event/ProgressChangedEventArgs");
	
	methods.VideoArrivedListener_OnVideoArrived   = env->GetMethodID(clazz.VideoArrivedListener,
	                                                "OnVideoArrived",
	                                                "(Lpeersdk/peer/PeerStream;Lpeersdk/peer/event/VideoArrivedEventArgs;)V");
	methods.AudioArrivedListener_OnAudioArrived   = env->GetMethodID(clazz.AudioArrivedListener,
	                                                "OnAudioArrived", 
	                                                "(Lpeersdk/peer/PeerStream;Lpeersdk/peer/event/AudioArrivedEventArgs;)V");
	methods.ErrorOccurredListener_OnErrorOccurred = env->GetMethodID(clazz.ErrorOccurredListener,
	                                                "OnErrorOccurred", 
	                                                "(Lpeersdk/peer/PeerStream;Lpeersdk/peer/event/ErrorOccurredEventArgs;)V");
	methods.CompletedListener_OnCompleted = env->GetMethodID(clazz.CompletedListener,
	                                                "OnCompleted", 
	                                                "(Lpeersdk/peer/PeerStream;Lpeersdk/peer/event/CompletedEventArgs;)V");
	methods.ProgressChangedListener_OnProgressChanged = env->GetMethodID(clazz.ProgressChangedListener,
	                                                "OnProgressChanged", 
	                                                "(Lpeersdk/peer/PeerStream;Lpeersdk/peer/event/ProgressChangedEventArgs;)V");
	
	
	methods.VideoArrivedEventArgs_Init  = env->GetMethodID(clazz.VideoArrivedEventArgs, 
	                                                      "<init>", 
														  "(IIIIJIIFLjava/util/Date;)V");
	methods.AudioArrivedEventArgs_Init  = env->GetMethodID(clazz.AudioArrivedEventArgs, 
	                                                      "<init>", 
														  "(IIIIJLjava/util/Date;)V");
	methods.ErrorOccurredEventArgs_Init = env->GetMethodID(clazz.ErrorOccurredEventArgs, 
	                                                      "<init>", 
														  "(ZLjava/lang/String;)V");
	methods.CompletedEventArgs_Init = env->GetMethodID(clazz.CompletedEventArgs, 
	                                                      "<init>", 
														  "(Z)V");
	methods.ProgressChangedEventArgs_Init = env->GetMethodID(clazz.ProgressChangedEventArgs, 
	                                                      "<init>", 
														  "(I)V");
														  
														  
	jniNewClassGlobalReference(env, clazz.Date,   "java/util/Date");

	methods.Date_Init  = env->GetMethodID(clazz.Date, "<init>", "(IIIIII)V");
	methods.Date_Init2 = env->GetMethodID(clazz.Date, "<init>", "(J)V");
}
static const char* const kClassPathName = "peersdk/peer/PeerStream";
int register_peersdk_peer_PeerStream(JNIEnv *env)
{
	InitJNIReference(env);

	//////////////////////////////////////////////////////////////////////////////////////////////
	
	jclass clz = env->FindClass(kClassPathName);
	if (clz == NULL)
	{
        LOGE("Can't find %s", kClassPathName);
        return -1;
    }
	
	// get fieldID
	fields.context = env->GetFieldID(clz, "mNativeContext", "I");

	fields.videoArrivedListener    = env->GetFieldID(clz, 
	                                             "mVideoArrivedListener", 
												 "Lpeersdk/peer/PeerStream$VideoArrivedListener;");
	fields.audioArrivedListener    = env->GetFieldID(clz, 
	                                             "mAudioArrivedListener", 
												 "Lpeersdk/peer/PeerStream$AudioArrivedListener;");
	fields.errorOccurredListener   = env->GetFieldID(clz, 
	                                             "mErrorOccurredListener", 
												 "Lpeersdk/peer/PeerStream$ErrorOccurredListener;");
	fields.completedListener       = env->GetFieldID(clz, 
	                                             "mCompletedListener", 
												 "Lpeersdk/peer/PeerStream$CompletedListener;");
	fields.progressChangedListener = env->GetFieldID(clz, 
	                                             "mProgressChangedListener", 
												 "Lpeersdk/peer/PeerStream$ProgressChangedListener;");
												   
	//////////////////////////////////////////////////////////////////////////////////////////////
	
	if (env->RegisterNatives(clz, gMethods, sizeof(gMethods) / sizeof(gMethods[0])) < 0)
	{		
		LOGE("RegisterNatives failed for '%s'\n", kClassPathName);
		return -1;
	} 

	return 1;
}
