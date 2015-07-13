#include "peer_sdk.h"

using namespace PeerSDK;

#define TAG "__PeerChannel-jni__"

struct fields_t {
	jfieldID    context;
};
static fields_t fields;

struct methods_t {
	jmethodID  PeerVideo_Init;
	jmethodID  PeerAudio_Init;
};
static methods_t methods;

struct clazz_t {
	jclass  PeerVideo;
	jclass  PeerAudio;
};
static clazz_t clazz;

// class PeerChannelImpl
PeerChannelImpl::PeerChannelImpl(JNIEnv* env, jobject thiz, PeerChannel* instance)
{
	mInstance = instance;
}

PeerChannelImpl::~PeerChannelImpl()
{
}

PeerChannel* PeerChannelImpl::get()
{
	return mInstance;
}

//===================================================================

PeerChannelImpl* getPeerChannelImpl(JNIEnv* env, jobject thiz)
{
	return (PeerChannelImpl*)env->GetIntField(thiz, fields.context);
}

static PeerChannel* _PeerChannel(JNIEnv* env, jobject thiz)
{
	return getPeerChannelImpl(env, thiz)->get();
}

static void JNICALL peersdk_peer_PeerChannel_native_setup(JNIEnv* env, jobject thiz, jint nativeInstace)
{
	PeerChannelImpl* channel = new PeerChannelImpl(env, thiz, (PeerChannel*)nativeInstace);
	env->SetIntField(thiz, fields.context, (jint)channel);
}

static void JNICALL peersdk_peer_PeerChannel_native_finalize(JNIEnv* env, jobject thiz)
{
	PeerChannelImpl* channel = getPeerChannelImpl(env, thiz);
	delete channel;
}

static jint JNICALL peersdk_peer_PeerChannel_Index(JNIEnv* env, jobject thiz)
{
	PeerChannel* channel = _PeerChannel(env, thiz);
	return (jint)channel->Index();
}

static jstring JNICALL peersdk_peer_PeerChannel_Name(JNIEnv* env, jobject thiz)
{
	PeerChannel* channel = _PeerChannel(env, thiz);
	
	String s = channel->Name();
	wchar_t const* name = (wchar_t const*) s;
	if (name == NULL)
		return NULL;

	int32 nameLen = channel->Name().Length();
	jchar tmp[nameLen + 1];
	memset(tmp, '\0', sizeof(tmp));

	for (int32 i = 0; i < nameLen; i++)
		tmp[i] = (jchar) name[i];
	jstring str = env->NewString(tmp, nameLen);

	if (str == NULL)
		jniThrowException(env, TAG, "Out of memory");

	return str;
}

static jobject JNICALL peersdk_peer_PeerChannel_Video(JNIEnv* env, jobject thiz)
{
	PeerChannel* channel = _PeerChannel(env, thiz);

	jobject obj = env->NewObject(clazz.PeerVideo, methods.PeerVideo_Init, channel->Video(), channel->Index());
	if (obj == NULL)
		jniThrowException(env, TAG, "Out of memory");
	return obj;	
}

static jobject JNICALL peersdk_peer_PeerChannel_Audio(JNIEnv* env, jobject thiz)
{
	PeerChannel* channel = _PeerChannel(env, thiz);

	jobject obj = env->NewObject(clazz.PeerAudio, methods.PeerAudio_Init, channel->Audio(), channel->Index());
	if (obj == NULL)
		jniThrowException(env, TAG, "Out of memory");
	return obj;	
}

/*
 * Table of methods associated with a single class.
 */
static JNINativeMethod gMethods[] = 
{
	{"native_setup",    "(I)V",                       (void*)peersdk_peer_PeerChannel_native_setup },
    {"native_finalize", "()V",                        (void*)peersdk_peer_PeerChannel_native_finalize },
	{"Index",           "()I",                        (void*)peersdk_peer_PeerChannel_Index },
	{"Name",            "()Ljava/lang/String;",       (void*)peersdk_peer_PeerChannel_Name },
	{"Video",           "()Lpeersdk/peer/PeerVideo;", (void*)peersdk_peer_PeerChannel_Video },
	{"Audio",           "()Lpeersdk/peer/PeerAudio;", (void*)peersdk_peer_PeerChannel_Audio },
};

static void InitJNIReference(JNIEnv* env)
{
	jniNewClassGlobalReference(env, clazz.PeerVideo,  "peersdk/peer/PeerVideo");
	jniNewClassGlobalReference(env, clazz.PeerAudio,  "peersdk/peer/PeerAudio");

	methods.PeerVideo_Init = env->GetMethodID(clazz.PeerVideo, "<init>","(I)V");
	methods.PeerAudio_Init = env->GetMethodID(clazz.PeerAudio, "<init>","(I)V");
}

static const char* const kClassPathName = "peersdk/peer/PeerChannel";
int register_peersdk_peer_PeerChannel(JNIEnv* env)
{
	InitJNIReference(env);

	jclass clazz = env->FindClass(kClassPathName);
	if (clazz == NULL)
	{
        LOGE("Can't find %s", kClassPathName);
        return -1;
    }
	
	fields.context = env->GetFieldID(clazz, "mNativeContext", "I");
	if (fields.context == NULL)
	{
		LOGE("Can't find PeerChannel.mNativeContext");
		return -1;
	}

	//////////////////////////////////////////////////////////////////////////////////////
	
	if (env->RegisterNatives(clazz, gMethods, sizeof(gMethods) / sizeof(gMethods[0])) < 0)
	{		
		LOGE("RegisterNatives failed for '%s'\n", kClassPathName);
		return -1;
	} 
	return 1;
}
