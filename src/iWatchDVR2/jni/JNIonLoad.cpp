#include "JNIonLoad.h"
#include "AndroidLog.h"

#include <string.h>
#include <assert.h>
#include <stdio.h>

#define TAG "JNIOnLoad"

extern int register_peersdk_TimeRange(JNIEnv* env);
extern int register_peersdk_TimeSpan(JNIEnv* env);

extern int register_peersdk_peer_Peer(JNIEnv* env);
extern int register_peersdk_peer_PeerAudio(JNIEnv* env);
extern int register_peersdk_peer_PeerChannel(JNIEnv* env);
extern int register_peersdk_peer_PeerHDD(JNIEnv* env);
extern int register_peersdk_peer_PeerLog(JNIEnv* env);
extern int register_peersdk_peer_PeerPTZ(JNIEnv* env);
extern int register_peersdk_peer_PeerRelay(JNIEnv* env);
extern int register_peersdk_peer_PeerRecorder(JNIEnv* env);
extern int register_peersdk_peer_PeerRecordList(JNIEnv* env);
extern int register_peersdk_peer_PeerStream(JNIEnv* env);
extern int register_peersdk_peer_PeerVideo(JNIEnv* env);

extern int register_peersdk_media_MediaDispatcher(JNIEnv* env);


static JavaVM* gjvm = NULL;

jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
	LOGI("JNI_OnLoad");

	jint error = -1;

	gjvm = vm;
	JNIEnv* env = GetJNIEnv();

	if (register_peersdk_media_MediaDispatcher(env) < 0)
	{
		LOGE("ERROR: register_peersdk_media_MediaDispatcher native registration failed\n");
        return error;
	}

	if (register_peersdk_TimeRange(env) < 0)
	{
		LOGE("ERROR: register_peersdk_TimeRange native registration failed\n");
        return error;
	}

	if (register_peersdk_TimeSpan(env) < 0)
	{
		LOGE("ERROR: register_peersdk_TimeSpan native registration failed\n");
        return error;
	}
	
	if (register_peersdk_peer_Peer(env) < 0)
	{
		LOGE("ERROR: register_peersdk_peer_Peer native registration failed\n");
        return error;
	}

	if (register_peersdk_peer_PeerAudio(env) < 0)
	{
		LOGE("ERROR: register_peersdk_peer_PeerAudio native registration failed\n");
        return error;
	}
	
	if (register_peersdk_peer_PeerChannel(env) < 0)
	{
		LOGE("ERROR: register_peersdk_peer_PeerChannel native registration failed\n");
        return error;
	}

	if (register_peersdk_peer_PeerHDD(env) < 0)
	{
		LOGE("ERROR: register_peersdk_peer_PeerHDD native registration failed\n");
        return error;
	}
	
	if (register_peersdk_peer_PeerLog(env) < 0)
	{
		LOGE("ERROR: register_peersdk_peer_PeerLog native registration failed\n");
        return error;
	}
	
	if (register_peersdk_peer_PeerPTZ(env) < 0)
	{
		LOGE("ERROR: register_peersdk_peer_PeerPTZ native registration failed\n");
        return error;
	}
	
	if (register_peersdk_peer_PeerRecorder(env) < 0)
	{
		LOGE("ERROR: register_peersdk_peer_PeerRecorder native registration failed\n");
        return error;
	}
	
	if (register_peersdk_peer_PeerRecordList(env) < 0)
	{
		LOGE("ERROR: register_peersdk_peer_PeerRecordList native registration failed\n");
        return error;
	}
	
	if (register_peersdk_peer_PeerRelay(env) < 0)
	{
		LOGE("ERROR: register_peersdk_peer_PeerRelay native registration failed\n");
        return error;
	}
	
	if (register_peersdk_peer_PeerStream(env) < 0)
	{
		LOGE("ERROR: register_peersdk_peer_PeerStream native registration failed\n");
        return error;
	}
	
	if (register_peersdk_peer_PeerVideo(env) < 0)
	{
		LOGE("ERROR: register_peersdk_peer_PeerVideo native registration failed\n");
        return error;
	}
	
	return JNI_VERSION_1_4;
}



////////////////////////////////////////////////////////////

// utility
wchar_t* jString2WString(JNIEnv* env, jstring string)
{
	if (string == NULL)
        return NULL;
    int len = env->GetStringLength(string);
    const jchar* raw = env->GetStringChars(string, NULL);
    if (raw == NULL)
        return NULL;

    wchar_t* wsz = new wchar_t[len+1];
    memcpy(wsz, raw, len*2);
    wsz[len] = 0;

    env->ReleaseStringChars(string, raw);

    return wsz;
}

////////////////////////////////////////////////////////////

/*
 * Get JavaVM Env
 */
JNIEnv* GetJNIEnv()
{
    JNIEnv* env = NULL;  
    if (gjvm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK)
	{  
        LOGE("Failed to obtain JNIEnv");  
        return NULL;  
    }  
    return env;  
}

JNIEnv* GetJNIEnvAttachThread() // HAVE TO DEATTACH THREAD IF ENV IS NOT NULL
{
    JNIEnv* env = NULL;  
    if (gjvm->AttachCurrentThread(&env, NULL) != JNI_OK)
	{  
        LOGE("Failed to obtain JNIEnv Attach Thread");  
        return NULL;  
    }  
    return env;  
}

void JNIEnvDeattachThread()
{
	gjvm->DetachCurrentThread(); 
}


/*
 * Register native JNI-callable methods.
 *
 * "className" looks like "java/lang/String".
 */
int jniRegisterNativeMethods(JNIEnv* env, const char* className,
    const JNINativeMethod* gMethods, int numMethods)
{
    jclass clazz;

    LOGV("Registering %s natives\n", className);
    clazz = env->FindClass(className);
    if (clazz == NULL) {
        LOGE("Native registration unable to find class '%s'\n", className);
        return -1;
    }
    if (env->RegisterNatives(clazz, gMethods, numMethods) < 0) {
        LOGE("RegisterNatives failed for '%s'\n", className);
        return -1;
    }
    return 0;
}

/*
 * Throw an exception with the specified class and an optional message.
 */
int jniThrowException(JNIEnv* env, const char* className, const char* msg)
{
    jclass exceptionClass;

    exceptionClass = env->FindClass(className);
    if (exceptionClass == NULL) {
        LOGE("Unable to find exception class %s\n", className);
        assert(0);      /* fatal during dev; should always be fatal? */
        return -1;
    }

    if (env->ThrowNew(exceptionClass, msg) != JNI_OK) {
        LOGE("Failed throwing '%s' '%s'\n", className, msg);
        assert(!"failed to throw");
    }
    return 0;
}

/*
 * Throw a java.lang.RuntimeException, with an optional message.
 */
int jniThrowRuntimeException(JNIEnv* env, const char* msg)
{
    return jniThrowException(env, "java/lang/RuntimeException", msg);
}

/*
 * Throw a java.IO.IOException, generating the message from errno.
 */
int jniThrowIOException(JNIEnv* env, int errnum)
{
    // note: glibc has a nonstandard
    // strerror_r that looks like this:
    // char *strerror_r(int errnum, char *buf, size_t n);

    const char* message;
    char buffer[80];
    char* ret;

    buffer[0] = 0;
    ret = (char*) strerror_r(errnum, buffer, sizeof(buffer));

    if (((int)ret) == 0) {
        //POSIX strerror_r, success
        message = buffer;
    } else if (((int)ret) == -1) {
        //POSIX strerror_r, failure

        snprintf (buffer, sizeof(buffer), "errno %d", errnum);
        message = buffer;
    } else {
        //glibc strerror_r returning a string
        message = ret;
    }

    return jniThrowException(env, "java/io/IOException", message);
}

void jniNewClassGlobalReference(JNIEnv* env, jclass& objectClazz, const char* className)
{
	jclass localRefCls = env->FindClass(className);
	objectClazz = (jclass) env->NewGlobalRef(localRefCls);
	env->DeleteLocalRef(localRefCls); 
}

jmethodID jniGetMethodID(JNIEnv* env, jclass clazzRef, const char* methodName, const char* methodSignture)
{
	return env->GetMethodID(clazzRef, methodName, methodSignture);	
}