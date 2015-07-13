#include "peer_sdk.h"

using namespace PeerSDK;

#define TAG "__PeerRecordList-jni__"

struct fields_t {
	jfieldID    context;
};
static fields_t fields;

struct methods_t {
	jmethodID  TimeRange_Init;
	jmethodID  TimeSpan_Init;
};
static methods_t methods;

struct clazz_t {
	jclass  TimeRange;
	jclass  TimeSpan;
};
static clazz_t clazz;

// class PeerRecordListImpl
PeerRecordListImpl::PeerRecordListImpl(JNIEnv* env, jobject thiz)
{
	mInstance = NULL;
	mObject = env->NewGlobalRef(thiz);
}

PeerRecordListImpl::~PeerRecordListImpl()
{
	if (mInstance != NULL)
		mInstance->Dispose();
	JNIEnv* env = GetJNIEnv();
	env->DeleteGlobalRef(mObject);
}

jobject PeerRecordListImpl::getObject()
{
	return mObject;
}

PeerRecordList* PeerRecordListImpl::get()
{
	return mInstance;
}

PeerRecordList** PeerRecordListImpl::_get()
{
	return &mInstance;
}

//===================================================================

PeerRecordListImpl* getPeerRecordListImpl(JNIEnv* env, jobject thiz)
{
	return (PeerRecordListImpl*)env->GetIntField(thiz, fields.context);
}

static PeerRecordList* _PeerRecordList(JNIEnv* env, jobject thiz)
{
	return getPeerRecordListImpl(env, thiz)->get();
}

static void JNICALL peersdk_peer_PeerRecordList_native_setup(JNIEnv* env, jobject thiz)
{
	PeerRecordListImpl* rlist = new PeerRecordListImpl(env, thiz);
	env->SetIntField(thiz, fields.context, (jint)rlist);
}

static void JNICALL peersdk_peer_PeerRecordList_native_finalize(JNIEnv* env, jobject thiz)
{
	PeerRecordListImpl* rlist = getPeerRecordListImpl(env, thiz);
	delete rlist;
}

static jintArray JNICALL peersdk_peer_PeerRecordList_GetRecordedDaysOfMonth(JNIEnv *env, jobject thiz, jint year, jint month)
{
	PeerRecordList* rlist = _PeerRecordList(env, thiz);

	IntList result;
	if (!rlist->GetRecordedDaysOfMonth((int32) year, (int32) month, result))
		return NULL;

	jintArray _result = env->NewIntArray(result.Count());
	jint* value = env->GetIntArrayElements(_result, 0);
	for (int32 i = 0; i < result.Count(); i++)
		value[i] =  (jint) result[i];
	env->ReleaseIntArrayElements(_result, value, 0);
	
	return _result;
}

static jobjectArray JNICALL peersdk_peer_PeerRecordList_GetRecordedMinutesOfDay(JNIEnv *env, jobject thiz, jint year, jint month, jint day)
{
	PeerRecordList* rlist = _PeerRecordList(env, thiz);
	
	TimeSpanList result;
	if (!rlist->GetRecordedMinutesOfDay((int32) year, (int32) month, (int32) day, result))
		return NULL;

	jobjectArray _result = env->NewObjectArray(result.Count(), clazz.TimeSpan, NULL);
	for (int32 i = 0; i < result.Count(); i++)
	{
		jobject obj = env->NewObject(clazz.TimeSpan, methods.TimeSpan_Init, (jint)&result[i]);
		env->SetObjectArrayElement(_result, i, obj);
		env->DeleteLocalRef(obj);
	}

	return _result;
}

static jobjectArray JNICALL peersdk_peer_PeerRecordList_GetList(JNIEnv *env, jobject thiz)
{
	PeerRecordList* rlist = _PeerRecordList(env, thiz);

	TimeRangeList result;
	if (!rlist->GetList(result))
		return NULL;

	jobjectArray _result = env->NewObjectArray(result.Count(), clazz.TimeRange, NULL);
	for (int32 i = 0; i < result.Count(); i++)
	{
		DateTime begin = result[i].Begin();
		DateTime end = result[i].End();
		jobject obj = env->NewObject(clazz.TimeRange, methods.TimeRange_Init, (jint)&result[i]);
		env->SetObjectArrayElement(_result, i, obj);
		env->DeleteLocalRef(obj);
	}

	return _result;
}


/*
 * Table of methods associated with a single class.
 */
static JNINativeMethod gMethods[] =  
{
	// Method, Signature, FuncPtr
	{"native_setup",            "()V",       (void*)peersdk_peer_PeerRecordList_native_setup },
	{"native_finalize",         "()V",       (void*)peersdk_peer_PeerRecordList_native_finalize },
	{"GetRecordedDaysOfMonth",  "(II)[I",    (void*)peersdk_peer_PeerRecordList_GetRecordedDaysOfMonth },
	{"GetRecordedMinutesOfDay", "(III)[Lpeersdk/TimeSpan;",
						                     (void*)peersdk_peer_PeerRecordList_GetRecordedMinutesOfDay },
	{"GetList",                 "()[Lpeersdk/TimeRange;",
						                     (void*)peersdk_peer_PeerRecordList_GetList },
};

static void InitJNIReference(JNIEnv* env)
{
	jniNewClassGlobalReference(env, clazz.TimeSpan,  "peersdk/TimeSpan");
	jniNewClassGlobalReference(env, clazz.TimeRange, "peersdk/TimeRange");

	methods.TimeSpan_Init = env->GetMethodID(clazz.TimeSpan, "<init>","(I)V");
	methods.TimeRange_Init = env->GetMethodID(clazz.TimeRange, "<init>","(I)V");
}

static const char* const kClassPathName = "peersdk/peer/PeerRecordList";
int register_peersdk_peer_PeerRecordList(JNIEnv* env)
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
		LOGE("Can't find PeerRecordList.mNativeContext");
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
