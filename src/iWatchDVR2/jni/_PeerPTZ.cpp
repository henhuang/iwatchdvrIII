#include "peer_sdk.h"

using namespace PeerSDK;

#define TAG "__PeerPTZ-jni__"

struct fields_t {
	jfieldID    context;
};
static fields_t fields;

// class PeerPTZImpl
PeerPTZImpl::PeerPTZImpl(JNIEnv* env, jobject thiz, PeerPTZ* instance)
{
	mInstance = instance;
}

PeerPTZImpl::~PeerPTZImpl()
{
}

PeerPTZ* PeerPTZImpl::get()
{
	return mInstance;
}

//===================================================================

PeerPTZImpl* getPeerPTZImpl(JNIEnv* env, jobject thiz)
{
	return (PeerPTZImpl*)env->GetIntField(thiz, fields.context);
}

static PeerPTZ* _PeerPTZ(JNIEnv* env, jobject thiz)
{
	return getPeerPTZImpl(env, thiz)->get();
}

static void JNICALL peersdk_peer_PeerPTZ_native_setup(JNIEnv* env, jobject thiz, jint nativeInstance)
{
	PeerPTZImpl* ptz = new PeerPTZImpl(env, thiz, (PeerPTZ*)nativeInstance);
	env->SetIntField(thiz, fields.context, (jint)ptz);
}

static void JNICALL peersdk_peer_PeerPTZ_native_finalize(JNIEnv* env, jobject thiz)
{
	PeerPTZImpl* ptz = getPeerPTZImpl(env, thiz);
	delete ptz;
}

static jint JNICALL peersdk_peer_PeerPTZ_Stop(JNIEnv* env, jobject thiz)
{
	PeerPTZ* ptz = _PeerPTZ(env, thiz);
	return (jint)ptz->Stop();
}

static void JNICALL peersdk_peer_PeerPTZ_Move(JNIEnv* env, jobject thiz, jdouble x, jdouble y, jboolean stop)
{
	PeerPTZ* ptz = _PeerPTZ(env, thiz);
	ptz->Move((double)x, (double)y, (bool)stop);
}

static void JNICALL peersdk_peer_PeerPTZ_DoZoom(JNIEnv* env, jobject thiz, jdouble speed, jboolean stop)
{
	PeerPTZ* ptz = _PeerPTZ(env, thiz);
	ptz->DoZoom((double)speed, (bool)stop);
}

static void JNICALL peersdk_peer_PeerPTZ_DoFocus(JNIEnv* env, jobject thiz, jdouble speed, jboolean stop)
{
	PeerPTZ* ptz = _PeerPTZ(env, thiz);
	ptz->DoFocus((double)speed, (bool)stop);
}

static void JNICALL peersdk_peer_PeerPTZ_DoIris(JNIEnv* env, jobject thiz, jdouble speed, jboolean stop)
{
	PeerPTZ* ptz = _PeerPTZ(env, thiz);
	ptz->DoIris((double)speed, (bool)stop);
}

static void JNICALL peersdk_peer_PeerPTZ_DoAutoFocus(JNIEnv* env, jobject thiz)
{
	PeerPTZ* ptz = _PeerPTZ(env, thiz);
	ptz->DoAutoFocus();
}

static void JNICALL peersdk_peer_PeerPTZ_DoAutoIris(JNIEnv* env, jobject thiz)
{
	PeerPTZ* ptz = _PeerPTZ(env, thiz);
	ptz->DoAutoFocus();
}

static void JNICALL peersdk_peer_PeerPTZ_SetPreset(JNIEnv* env, jobject thiz, jint index)
{
	PeerPTZ* ptz = _PeerPTZ(env, thiz);
	ptz->SetPreset((int32)index);
}

static void JNICALL peersdk_peer_PeerPTZ_GoPreset(JNIEnv* env, jobject thiz, jint index)
{
	PeerPTZ* ptz = _PeerPTZ(env, thiz);
	ptz->GoPreset((int32)index);
}

static jboolean JNICALL peersdk_peer_PeerPTZ_CanZoom(JNIEnv* env, jobject thiz)
{
	PeerPTZ* ptz = _PeerPTZ(env, thiz);
	return (jboolean)ptz->CanZoom();
}

static jboolean JNICALL peersdk_peer_PeerPTZ_CanFocus(JNIEnv* env, jobject thiz)
{
	PeerPTZ* ptz = _PeerPTZ(env, thiz);
	return (jboolean)ptz->CanFocus();
}

static jboolean JNICALL peersdk_peer_PeerPTZ_CanIris(JNIEnv* env, jobject thiz)
{
	PeerPTZ* ptz = _PeerPTZ(env, thiz);
	return (jboolean)ptz->CanIris();
}

static jboolean JNICALL peersdk_peer_PeerPTZ_CanAutoFocus(JNIEnv* env, jobject thiz)
{
	PeerPTZ* ptz = _PeerPTZ(env, thiz);
	return (jboolean)ptz->CanAutoFocus();
}

static jboolean JNICALL peersdk_peer_PeerPTZ_CanAutoIris(JNIEnv* env, jobject thiz)
{
	PeerPTZ* ptz = _PeerPTZ(env, thiz);
	return (jboolean) ptz->CanAutoIris();
}

static jboolean JNICALL peersdk_peer_PeerPTZ_CanPreset(JNIEnv* env, jobject thiz)
{
	PeerPTZ* ptz = _PeerPTZ(env, thiz);
	return (jboolean)ptz->CanPreset();
}

static jintArray JNICALL peersdk_peer_PeerPTZ_GetAvailablePreset(JNIEnv* env, jobject thiz)
{
	PeerPTZ* ptz = _PeerPTZ(env, thiz);
	IntList presets = ptz->AvailablePreset();

	jintArray _presets = env->NewIntArray(presets.Count());
	jint* ptr = env->GetIntArrayElements(_presets, 0);
	for (int32 i = 0; i < presets.Count(); i++)
		ptr[i] =  (jint) presets[i];
	env->ReleaseIntArrayElements(_presets, ptr, 0);
	
	return _presets;
}

static jboolean JNICALL peersdk_peer_PeerPTZ_Enabled(JNIEnv* env, jobject thiz)
{
	PeerPTZ* ptz = _PeerPTZ(env, thiz);
	return (jboolean)ptz->get_Enabled();
}

/*
 * Table of methods associated with a single class.
 */
static JNINativeMethod gMethods[] = 
{
	{"native_setup",       "(I)V",      (void*)peersdk_peer_PeerPTZ_native_setup },
	{"native_finalize",    "()V",       (void*)peersdk_peer_PeerPTZ_native_finalize },
	{"Stop",               "()I",       (void*)peersdk_peer_PeerPTZ_Stop },
	{"Move",               "(DDZ)V",    (void*)peersdk_peer_PeerPTZ_Move },
	{"DoZoom",             "(DZ)V",     (void*)peersdk_peer_PeerPTZ_DoZoom },
	{"DoFocus",            "(DZ)V",     (void*)peersdk_peer_PeerPTZ_DoFocus },
	{"DoIris",             "(DZ)V",     (void*)peersdk_peer_PeerPTZ_DoIris },
	{"DoAutoFocus",        "()V",       (void*)peersdk_peer_PeerPTZ_DoAutoFocus },
	{"DoAutoIris",         "()V",       (void*)peersdk_peer_PeerPTZ_DoAutoIris },
	{"SetPreset",          "(I)V",      (void*)peersdk_peer_PeerPTZ_SetPreset },
	{"GoPreset",           "(I)V",      (void*)peersdk_peer_PeerPTZ_GoPreset },
	{"CanZoom",            "()Z",       (void*)peersdk_peer_PeerPTZ_CanZoom },
	{"CanFocus",           "()Z",       (void*)peersdk_peer_PeerPTZ_CanFocus },
	{"CanIris",            "()Z",       (void*)peersdk_peer_PeerPTZ_CanIris },
	{"CanAutoFocus",       "()Z",       (void*)peersdk_peer_PeerPTZ_CanAutoFocus },
	{"CanAutoIris",        "()Z",       (void*)peersdk_peer_PeerPTZ_CanAutoIris },
	{"CanPreset",          "()Z",       (void*)peersdk_peer_PeerPTZ_CanPreset },
	{"GetAvailablePreset", "()[I",      (void*)peersdk_peer_PeerPTZ_GetAvailablePreset },
	{"Enabled",            "()Z",       (void*)peersdk_peer_PeerPTZ_Enabled },
};

static const char* const kClassPathName = "peersdk/peer/PeerPTZ";
int register_peersdk_peer_PeerPTZ(JNIEnv* env)
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
		LOGE("Can't find PeerPTZ.mNativeContext");
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
