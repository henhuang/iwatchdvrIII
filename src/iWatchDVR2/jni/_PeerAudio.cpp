#include "peer_sdk.h"

using namespace PeerSDK;

#define TAG "__PeerAudio-jni__"

struct fields_t {
	jfieldID    context;
};
static fields_t fields;

// class PeerAudioImpl
PeerAudioImpl::PeerAudioImpl(JNIEnv* env, jobject thiz, PeerAudio* instance)
{
	mInstance = instance;
}

PeerAudioImpl::~PeerAudioImpl()
{
}

PeerAudio* PeerAudioImpl::get()
{
	return mInstance;
}

//===================================================================

PeerAudioImpl* getPeerAudioImpl(JNIEnv* env, jobject thiz)
{
	return (PeerAudioImpl*)env->GetIntField(thiz, fields.context);
}

static PeerAudio* _PeerAudio(JNIEnv* env, jobject thiz)
{
	return getPeerAudioImpl(env, thiz)->get();
}


static void JNICALL peersdk_peer_PeerAudio_native_setup(JNIEnv* env, jobject thiz, jint nativeInstance)
{
	PeerAudioImpl* audio = new PeerAudioImpl(env, thiz, (PeerAudio*)nativeInstance);
	env->SetIntField(thiz, fields.context, (jint)audio);
}

static void JNICALL peersdk_peer_PeerAudio_native_finalize(JNIEnv* env, jobject thiz)
{
	PeerAudioImpl* audio = getPeerAudioImpl(env, thiz);
	delete audio;
}

static jboolean JNICALL peersdk_peer_PeerAudio_IsPresent(JNIEnv* env, jobject thiz)
{
	PeerAudio* audio = _PeerAudio(env, thiz);
	return (jboolean)audio->IsPresent();
}

/*
 * Table of methods associated with a single class.
 */
static JNINativeMethod gMethods[] = 
{
	{"native_setup",      "(I)V",      (void*)peersdk_peer_PeerAudio_native_setup },
	{"native_finalize",   "()V",       (void*)peersdk_peer_PeerAudio_native_finalize },
	{"IsPresent",         "()Z",       (void*)peersdk_peer_PeerAudio_IsPresent },
};

static const char* const kClassPathName = "peersdk/peer/PeerAudio";
int register_peersdk_peer_PeerAudio(JNIEnv* env)
{
	jclass clazz = env->FindClass(kClassPathName);
	if (clazz == NULL)
	{
        LOGE("Can't find %s", kClassPathName);
        return -1;
    }
	
	fields.context = env->GetFieldID(clazz, "mNativeContext", "I");
	if (fields.context == NULL)
	{
		LOGE("Can't find PeerAudio.mNativeContext");
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

