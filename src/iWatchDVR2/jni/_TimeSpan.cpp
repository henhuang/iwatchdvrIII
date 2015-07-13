#include "peer_sdk.h"

using namespace PeerSDK;

#define TAG "__TimeSpan-jni__"

struct fields_t {
	jfieldID    context;
};
static fields_t fields;


// class TimeSpanImpl
TimeSpanImpl::TimeSpanImpl(JNIEnv* env, jobject thiz, TimeSpan* instance)
{
	mInstance = *instance;
}

TimeSpanImpl::~TimeSpanImpl()
{
}

TimeSpan const& TimeSpanImpl::get()
{
	return mInstance;
}

//===================================================================

TimeSpanImpl* getTimeSpanImpl(JNIEnv* env, jobject thiz)
{
	return (TimeSpanImpl*)env->GetIntField(thiz, fields.context);
}

static TimeSpan const& _TimeSpan(JNIEnv* env, jobject thiz)
{
	return getTimeSpanImpl(env, thiz)->get();
}


static void JNICALL peersdk_TimeSpan_native_setup(JNIEnv* env, jobject thiz, jint nativeInstance)
{
	TimeSpanImpl* ts = new TimeSpanImpl(env, thiz, (TimeSpan*)nativeInstance);
	env->SetIntField(thiz, fields.context, (jint)ts);
}

static void JNICALL peersdk_TimeSpan_native_finalize(JNIEnv* env, jobject thiz)
{
	TimeSpanImpl* ts = getTimeSpanImpl(env, thiz);
	delete ts;
}

static jint JNICALL peersdk_TimeSpan_Hours(JNIEnv* env, jobject thiz)
{
	TimeSpan const& ts = _TimeSpan(env, thiz);
	return (jint)ts.Hours();
}

static jint JNICALL peersdk_TimeSpan_Minutes(JNIEnv* env, jobject thiz)
{
	TimeSpan const& ts = _TimeSpan(env, thiz);
	return (jint)ts.Minutes();
}

static JNINativeMethod gMethods[] = 
{
	{"native_setup",      "(I)V",      (void*)peersdk_TimeSpan_native_setup },
	{"native_finalize",   "()V",       (void*)peersdk_TimeSpan_native_finalize },
	{"Hours",             "()I",       (void*)peersdk_TimeSpan_Hours },
	{"Minutes",           "()I",       (void*)peersdk_TimeSpan_Minutes },
};

static const char* const kClassPathName = "peersdk/TimeSpan";
int register_peersdk_TimeSpan(JNIEnv* env)
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
		LOGE("Can't find TimeSpan.mNativeContext");
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

