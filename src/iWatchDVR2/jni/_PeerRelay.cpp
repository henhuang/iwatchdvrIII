#include "peer_sdk.h"

using namespace PeerSDK;

#define TAG "__PeerRelay-jni__"

struct fields_t {
	jfieldID    context;
};
static fields_t fields;

// class PeerRelayImpl
PeerRelayImpl::PeerRelayImpl(JNIEnv* env, jobject thiz, PeerRelay* instance)
{
	mInstance = instance;
}

PeerRelayImpl::~PeerRelayImpl()
{
}

PeerRelay* PeerRelayImpl::get()
{
	return mInstance;
}

//===================================================================

PeerRelayImpl* getPeerRelayImpl(JNIEnv* env, jobject thiz)
{
	return (PeerRelayImpl*)env->GetIntField(thiz, fields.context);
}

static PeerRelay* _PeerRelay(JNIEnv* env, jobject thiz)
{
	return getPeerRelayImpl(env, thiz)->get();
}

static void JNICALL peersdk_peer_PeerRelay_native_setup(JNIEnv* env, jobject thiz, jint nativeInstance)
{
	PeerRelayImpl* Relay = new PeerRelayImpl(env, thiz, (PeerRelay*)nativeInstance);
	env->SetIntField(thiz, fields.context, (jint)Relay);
}

static void JNICALL peersdk_peer_PeerRelay_native_finalize(JNIEnv* env, jobject thiz)
{
	PeerRelayImpl* Relay = getPeerRelayImpl(env, thiz);
	delete Relay;
}

static jboolean JNICALL peersdk_peer_PeerRelay_Activate(JNIEnv* env, jobject thiz, jint seconds)
{
	PeerRelay* relay = _PeerRelay(env, thiz);
	return (jboolean)(bool)relay->Activate((int32)seconds);
}

static jboolean JNICALL peersdk_peer_PeerRelay_Switch(JNIEnv* env, jobject thiz, jint pole)
{
	PeerRelay* relay = _PeerRelay(env, thiz);
	return (jboolean)(bool)relay->Switch((RelayPole)pole);
}

static jint JNICALL peersdk_peer_PeerRelay_Pole(JNIEnv* env, jobject thiz)
{
	PeerRelay* relay = _PeerRelay(env, thiz);
	return (jint)relay->Pole();
}

/*
 * Table of methods associated with a single class.
 */
static JNINativeMethod gMethods[] = 
{
	{"native_setup",       "(I)V",     (void*)peersdk_peer_PeerRelay_native_setup },
	{"native_finalize",    "()V",      (void*)peersdk_peer_PeerRelay_native_finalize },
	{"Activate",           "(I)Z",     (void*)peersdk_peer_PeerRelay_Activate },
	{"Switch",             "(I)Z",     (void*)peersdk_peer_PeerRelay_Switch },
	{"Pole",               "()I",      (void*)peersdk_peer_PeerRelay_Pole },
};

static const char* const kClassPathName = "peersdk/peer/PeerRelay";
int register_peersdk_peer_PeerRelay(JNIEnv* env)
{
	jclass clz = env->FindClass(kClassPathName);
	if (clz == NULL)
	{
        LOGE("Can't find %s", kClassPathName);
        return -1;
    }
	
	fields.context = env->GetFieldID(clz, "mNativeContext", "I");
	if (fields.context == NULL)
	{
		LOGE("Can't find PeerRelay.mNativeContext");
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
