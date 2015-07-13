#include "peer_sdk.h"

using namespace PeerSDK;

#define TAG "__PeerVideo-jni__"

struct fields_t {
	jfieldID    context;
};
static fields_t fields;

struct clazz_t {
	jclass PeerPTZ;
};
static clazz_t clazz;

struct methods_t {
	jmethodID  PeerPTZ_Init;
};
static methods_t methods;

// class PeerVideoImpl
PeerVideoImpl::PeerVideoImpl(JNIEnv* env, jobject thiz, PeerVideo* instance)
{
	mInstance = instance;
}

PeerVideoImpl::~PeerVideoImpl()
{
}

PeerVideo* PeerVideoImpl::get()
{
	return mInstance;
}

//===================================================================

PeerVideoImpl* getPeerVideoImpl(JNIEnv* env, jobject thiz)
{
	return (PeerVideoImpl*)env->GetIntField(thiz, fields.context);
}

static PeerVideo* _PeerVideo(JNIEnv* env, jobject thiz)
{
	return getPeerVideoImpl(env, thiz)->get();
}

static void JNICALL peersdk_peer_PeerVideo_native_setup(JNIEnv* env, jobject thiz, jint nativeInstance)
{
	PeerVideoImpl* video = new PeerVideoImpl(env, thiz, (PeerVideo*)nativeInstance);
	env->SetIntField(thiz, fields.context, (jint)video);
}

static void JNICALL peersdk_peer_PeerVideo_native_finalize(JNIEnv* env, jobject thiz)
{
	PeerVideoImpl* video = getPeerVideoImpl(env, thiz);
	delete video;
}

static jobject JNICALL peersdk_peer_PeerVideo_PTZ(JNIEnv* env, jobject thiz)
{
	PeerVideo* video = _PeerVideo(env, thiz);
	
	jobject obj  = env->NewObject(clazz.PeerPTZ, methods.PeerPTZ_Init, video->PTZ());
	if (obj == NULL)
		jniThrowException(env, TAG, "Out of memory");
	return obj;
}

static jboolean JNICALL peersdk_peer_PeerVideo_IsLoss(JNIEnv* env, jobject thiz)
{
	PeerVideo* video = _PeerVideo(env, thiz);
	return (jboolean)video->IsLoss();
}

static jboolean JNICALL peersdk_peer_PeerVideo_IsMotion(JNIEnv* env, jobject thiz)
{
	PeerVideo* video = _PeerVideo(env, thiz);
	return (jboolean)video->IsMotion();
}

static jint JNICALL peersdk_peer_PeerVideo_Format(JNIEnv* env, jobject thiz)
{
	PeerVideo* video = _PeerVideo(env, thiz);
	return (jboolean)video->Format();
}

/*
 * Table of methods associated with a single class.
 */
static JNINativeMethod gMethods[] = 
{
	{"native_setup",      "(I)V",      (void*)peersdk_peer_PeerVideo_native_setup },
	{"native_finalize",   "()V",       (void*)peersdk_peer_PeerVideo_native_finalize },
	{"PTZ",               "()Lpeersdk/peer/PeerPTZ;",
	                                   (void*)peersdk_peer_PeerVideo_PTZ },
	{"IsLoss",            "()Z",       (void*)peersdk_peer_PeerVideo_IsLoss },
	{"IsMotion",          "()Z",       (void*)peersdk_peer_PeerVideo_IsMotion },
	{"Format",            "()I",       (void*)peersdk_peer_PeerVideo_Format },
};

static void InitJNIReference(JNIEnv* env)
{
    jniNewClassGlobalReference(env, clazz.PeerPTZ,  "peersdk/peer/PeerPTZ");

    methods.PeerPTZ_Init = env->GetMethodID(clazz.PeerPTZ, "<init>","(I)V");
}

static const char* const kClassPathName = "peersdk/peer/PeerVideo";
int register_peersdk_peer_PeerVideo(JNIEnv* env)
{
	InitJNIReference(env);
	
	jclass clz = env->FindClass(kClassPathName);
	if (clz == NULL)
	{
        LOGE("Can't find %s", kClassPathName);
        return -1;
    }
	
	fields.context = env->GetFieldID(clz, "mNativeContext", "I");
	if (fields.context == NULL)
	{
		LOGE("Can't find PeerVideo.mNativeContext");
		return -1;
	}

	//////////////////////////////////////////////////////////////////////////////////////
	
	if (env->RegisterNatives(clz, gMethods, sizeof(gMethods) / sizeof(gMethods[0])) < 0)
	{		
		LOGE("RegisterNatives failed for '%s'\n", kClassPathName);
		return -1;
	} 
	return 1;
}
