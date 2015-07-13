#include "peer_sdk.h"

using namespace PeerSDK;

#define TAG "__PeerLog-jni__"

struct fields_t {
	jfieldID    context;
};
static fields_t fields;

struct methods_t {
	jmethodID  DateTime_Init;
	jmethodID  TimeZone_Init;
};
static methods_t methods;

struct clazz_t {
	jclass  Date;     // java.util.Date
	jclass  TimeZone; // java.util.TimeZone
};
static clazz_t clazz;

// class PeerLogImpl
PeerLogImpl::PeerLogImpl(JNIEnv* env, jobject thiz, PeerLog* instance)
{
	mInstance = instance;
}

PeerLogImpl::~PeerLogImpl()
{
}

PeerLog* PeerLogImpl::get()
{
	return mInstance;
}

//===================================================================

PeerLogImpl* getPeerLogImpl(JNIEnv* env, jobject thiz)
{
	return (PeerLogImpl*)env->GetIntField(thiz, fields.context);
}

static PeerLog* _PeerLog(JNIEnv* env, jobject thiz)
{
	return getPeerLogImpl(env, thiz)->get();
}

static void JNICALL peersdk_peer_PeerLog_native_setup(JNIEnv* env, jobject thiz, jint nativeInstance)
{
	PeerLogImpl* log = new PeerLogImpl(env, thiz, (PeerLog*)nativeInstance);
	env->SetIntField(thiz, fields.context, (jint)log);
}

static void JNICALL peersdk_peer_PeerLog_native_finalize(JNIEnv* env, jobject thiz)
{
	PeerLogImpl* log = getPeerLogImpl(env, thiz);
	delete log;
}

static jobject JNICALL peersdk_peer_PeerLog_Time(JNIEnv* env, jobject thiz)
{
	PeerLog* log = _PeerLog(env, thiz);
	DateTime time = log->Time();
	
	// TODO:
	/*
	jobject item = env->NewObject(clazz.DateTime, methods.DateTime_Init, (jlong)&time, true);
	if (item == NULL)
		jniThrowException(env, TAG, "Out of memory");
	return item;
	*/
	return NULL;
}

static jint JNICALL peersdk_peer_PeerLog_Type(JNIEnv* env, jobject thiz)
{
	PeerLog* log = _PeerLog(env, thiz);
	return (jint)log->Type();
}

static jboolean JNICALL peersdk_peer_PeerLog_Playable(JNIEnv* env, jobject thiz)
{
	PeerLog* log = _PeerLog(env, thiz);
	return (jboolean)log->Playable();
}

static jboolean JNICALL peersdk_peer_PeerLog_HasChannel(JNIEnv* env, jobject thiz)
{
	PeerLog* log = _PeerLog(env, thiz);
	return (jboolean)log->HasChannel();
}

static jint JNICALL peersdk_peer_PeerLog_Channel(JNIEnv* env, jobject thiz)
{
	PeerLog* log = _PeerLog(env, thiz);
	return (jint)log->Channel();
}


/*
 * Table of methods associated with a single class.
 */
static JNINativeMethod gMethods[] = 
{
	{"native_setup",    "(I)V",               (void*)peersdk_peer_PeerLog_native_setup },
    {"native_finalize", "()V",                (void*)peersdk_peer_PeerLog_native_finalize },
	{"Time",            "()Ljava/util/Date;", (void*)peersdk_peer_PeerLog_Time },
	{"Type",            "()I",                (void*)peersdk_peer_PeerLog_Type },
	{"Playable",        "()Z",                (void*)peersdk_peer_PeerLog_Playable },
	{"HasChannel",      "()Z",                (void*)peersdk_peer_PeerLog_HasChannel },
	{"Channel",         "()I",                (void*)peersdk_peer_PeerLog_Channel },
};

static void InitJNIReference(JNIEnv* env)
{
	jniNewClassGlobalReference(env, clazz.Date,     "java/util/Date");
	jniNewClassGlobalReference(env, clazz.TimeZone, "java/util/TimeZone");
	//methods.Date_Init  = env->GetMethodID(clazz_Date, "<init>", "IIIIII");
	//methods.TimeZone_Init  = env->GetMethodID(clazz_TimeZone, "<init>", "IIIIII");
}

static const char* const kClassPathName = "peersdk/peer/PeerLog";
int register_peersdk_peer_PeerLog(JNIEnv* env)
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
		LOGE("Can't find PeerLog.mNativeContext");
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
