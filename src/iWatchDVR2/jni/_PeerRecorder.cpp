#include "peer_sdk.h"

using namespace PeerSDK;

#define TAG "__PeerRecorder-jni__"

struct fields_t {
	jfieldID    context;
};
static fields_t fields;

// class PeerRecorderImpl
PeerRecorderImpl::PeerRecorderImpl(JNIEnv* env, jobject thiz, PeerRecorder* instance)
{
	mInstance = instance;
}

PeerRecorderImpl::~PeerRecorderImpl()
{
}

PeerRecorder* PeerRecorderImpl::get()
{
	return mInstance;
}

//===================================================================

PeerRecorderImpl* getPeerRecorderImpl(JNIEnv* env, jobject thiz)
{
	return (PeerRecorderImpl*)env->GetIntField(thiz, fields.context);
}

static PeerRecorder* _PeerRecorder(JNIEnv* env, jobject thiz)
{
	return getPeerRecorderImpl(env, thiz)->get();
}


static void JNICALL peersdk_peer_PeerRecorder_native_setup(JNIEnv* env, jobject thiz, jint nativeInstance)
{
	PeerRecorderImpl* recorder = new PeerRecorderImpl(env, thiz, (PeerRecorder*)nativeInstance);
	env->SetIntField(thiz, fields.context, (jint)recorder);
}

static void JNICALL peersdk_peer_PeerRecorder_native_finalize(JNIEnv* env, jobject thiz)
{
	PeerRecorderImpl* recorder = getPeerRecorderImpl(env, thiz);
	delete recorder;
}

static jintArray JNICALL peersdk_peer_PeerRecorder_RecordStatus(JNIEnv* env, jobject thiz)
{
	PeerRecorder* recorder = _PeerRecorder(env, thiz);
	
	RecordingList list = recorder->RecordStatus();
	jintArray _list = env->NewIntArray(list.Count());
	jint* value = env->GetIntArrayElements(_list, 0);
	
	for (int i = 0; i < list.Count(); i++)
		value[i] =  (jint)list[i];
	env->ReleaseIntArrayElements(_list, value, 0);
	
	return _list;
}

/*
 * Table of methods associated with a single class.
 */
static JNINativeMethod gMethods[] = 
{
	{"native_setup",       "(I)V",           (void*)peersdk_peer_PeerRecorder_native_setup },
	{"native_finalize",    "()V",            (void*)peersdk_peer_PeerRecorder_native_finalize },
	{"RecordStatus",       "()[I",           (void*)peersdk_peer_PeerRecorder_RecordStatus },
};

static const char* const kClassPathName = "peersdk/peer/PeerRecorder";
int register_peersdk_peer_PeerRecorder(JNIEnv *env)
{
	jclass clazz = env->FindClass(kClassPathName);
	if (clazz == NULL)
	{
        LOGE("Can't find %s", kClassPathName);
        return -1;
    }
	
	fields.context = env->GetFieldID(clazz, "mNativeContext", "I");

	///////////////////////////////////////
	
	if (env->RegisterNatives(clazz, gMethods, sizeof(gMethods) / sizeof(gMethods[0])) < 0)
	{		
		LOGE("RegisterNatives failed for '%s'\n", kClassPathName);
		return -1;
	}

	return 0;
}


