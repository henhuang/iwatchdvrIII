#include "peer_sdk.h"

using namespace PeerSDK;

#define TAG "__TimeSpan-jni__"

struct fields_t {
	jfieldID    context;
};
static fields_t fields;

struct clazz_t {
	jclass Date; // java.util.Date
};
static clazz_t clazz;

struct methods_t {
	jmethodID  Date_Init;
};
static methods_t methods;

// class TimeRangeImpl
TimeRangeImpl::TimeRangeImpl(JNIEnv* env, jobject thiz, TimeRange* instance)
{
	mInstance = *instance;
}

TimeRangeImpl::~TimeRangeImpl()
{
}

TimeRange const& TimeRangeImpl::get()
{
	return mInstance;
}

//===================================================================

TimeRangeImpl* getTimeRangeImpl(JNIEnv* env, jobject thiz)
{
	return (TimeRangeImpl*)env->GetIntField(thiz, fields.context);
}

static TimeRange const& _TimeRange(JNIEnv* env, jobject thiz)
{
	return getTimeRangeImpl(env, thiz)->get();
}

static void JNICALL peersdk_TimeRange_native_setup(JNIEnv* env, jobject thiz, jint nativeInstance)
{
	TimeRangeImpl* ts = new TimeRangeImpl(env, thiz, (TimeRange*)nativeInstance);
	env->SetIntField(thiz, fields.context, (jint)ts);
}

static void JNICALL peersdk_TimeRange_native_finalize(JNIEnv* env, jobject thiz)
{
	TimeRangeImpl* ts = getTimeRangeImpl(env, thiz);
	delete ts;
}

static jobject JNICALL peersdk_TimeRange_Begin(JNIEnv* env, jobject thiz)
{
	TimeRange const& tr = _TimeRange(env, thiz);
	
	DateTime time = tr.Begin();
	LOGI("range begin= %d/%d %d:%d", time.Month(), time.Day(), time.Hour(), time.Minute());

    jobject obj = env->NewObject(clazz.Date, methods.Date_Init, time.Year()-1900, time.Month()-1, time.Day(),
												time.Hour(), time.Minute(), time.Second());

	if (obj == NULL)
		jniThrowException(env, TAG, "Out of memory");
	return obj;	
}

static jobject JNICALL peersdk_TimeRange_End(JNIEnv* env, jobject thiz)
{
	TimeRange const& tr = _TimeRange(env, thiz);
	
	DateTime time = tr.End();
	LOGI("range end= %d/%d %d:%d", time.Month(), time.Day(), time.Hour(), time.Minute());

    jobject obj = env->NewObject(clazz.Date, methods.Date_Init, time.Year()-1900, time.Month()-1, time.Day(),
												time.Hour(), time.Minute(), time.Second());

	if (obj == NULL)
		jniThrowException(env, TAG, "Out of memory");
	return obj;	
}


/*
 * Table of methods associated with a single class.
 */
static JNINativeMethod gMethods[] = 
{
	{"native_setup",    "(I)V",               (void*)peersdk_TimeRange_native_setup },
	{"native_finalize", "()V",                (void*)peersdk_TimeRange_native_finalize },
	{"Begin",           "()Ljava/util/Date;", (void*)peersdk_TimeRange_Begin },
	{"End",             "()Ljava/util/Date;", (void*)peersdk_TimeRange_End },
};

static void InitJNIReference(JNIEnv* env)
{
    jniNewClassGlobalReference(env, clazz.Date,  "java/util/Date");

    methods.Date_Init = env->GetMethodID(clazz.Date, "<init>","(IIIIII)V");
}

static const char* const kClassPathName = "peersdk/TimeRange";
int register_peersdk_TimeRange(JNIEnv* env)
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
