#include "peer_sdk.h"

using namespace PeerSDK;

#define TAG "__PeerHDD-jni__"

struct fields_t {
	jfieldID    context;
};
static fields_t fields;

// class PeerHDDImpl
PeerHDDImpl::PeerHDDImpl(JNIEnv* env, jobject thiz, PeerHDD* instance)
{
	mInstance = instance;
	mObject = env->NewGlobalRef(thiz); 
}

PeerHDDImpl::~PeerHDDImpl()
{
	delete mInstance;
}

PeerHDD* PeerHDDImpl::get()
{
	return mInstance;
}

//===================================================================

PeerHDDImpl* getPeerHDDImpl(JNIEnv* env, jobject thiz)
{
	return (PeerHDDImpl*)env->GetIntField(thiz, fields.context);
}

static PeerHDD* _PeerHDD(JNIEnv* env, jobject thiz)
{
	return getPeerHDDImpl(env, thiz)->get();
}

static void JNICALL peersdk_peer_PeerHDD_native_setup(JNIEnv* env, jobject thiz, jint nativeInstance)
{
	PeerHDDImpl* hdd = new PeerHDDImpl(env, thiz, (PeerHDD*)nativeInstance);
	env->SetIntField(thiz, fields.context, (jint)hdd);
}

static void JNICALL peersdk_peer_PeerHDD_native_finalize(JNIEnv* env, jobject thiz)
{
	PeerHDDImpl* hdd = getPeerHDDImpl(env, thiz);
	delete hdd;
}

static jlong JNICALL peersdk_peer_PeerHDD_Capacity(JNIEnv* env, jobject thiz)
{
	PeerHDD* hdd = _PeerHDD(env, thiz);
	return (jlong)hdd->Capacity();
}

static jlong JNICALL peersdk_peer_PeerHDD_Available(JNIEnv* env, jobject thiz)
{
	PeerHDD* hdd = _PeerHDD(env, thiz);
	return (jlong)hdd->Available();
}

/*
 * Table of methods associated with a single class.
 */
static JNINativeMethod gMethods[] = 
{
	{"native_setup",     "(I)V",     (void*)peersdk_peer_PeerHDD_native_setup },
	{"native_finalize",  "()V",      (void*)peersdk_peer_PeerHDD_native_finalize },
	{"Capacity",         "()J",      (void*)peersdk_peer_PeerHDD_Capacity },
	{"Available",        "()J",      (void*)peersdk_peer_PeerHDD_Available },
};

static const char* const kClassPathName = "peersdk/peer/PeerHDD";
int register_peersdk_peer_PeerHDD(JNIEnv* env)
{
	jclass clz = env->FindClass(kClassPathName);
	if (clz == NULL)
	{
        LOGE("Can't find %s", kClassPathName);
        return -1;
    }
	
	// get fieldID
	fields.context = env->GetFieldID(clz, "mNativeContext", "I");
	if (fields.context == NULL)
	{
		LOGE("Can't find PeerHDD.mNativeContext");
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
