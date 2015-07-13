#include "peer_sdk.h"

#define TAG "__Peer-jni__"

using namespace PeerSDK;

struct fields_t {
	jfieldID    context;
	jfieldID    videoLossChangedListener;
	jfieldID    videoMotionChangedListener;
	jfieldID    errorOccurredListener;
};
static fields_t fields;

struct clazz_t {
	jclass ArrayList;      // java/util/ArrayList
	jclass PeerLog;        // peersdk/peer/PeerLog
	jclass PeerHDD;        // peersdk/peer/PeerHDD
	jclass PeerChannel;    // peersdk/peer/PeerChannel 
	jclass PeerRelay;      // peersdk/peer/PeerRelay
	jclass PeerRecorder;   // peersdk/peer/PeerRecorder
	jclass PeerRecordList; // peersdk/peer/PeerRecordList
	jclass PeerStream;     // peersdk/peer/PeerStream
	
	jclass VideoLossChangedListener;    // peersdk/peer/Peer$VideoLossChangedListener
	jclass VideoMotionChangedListener;  // peersdk/peer/Peer$VideoMotionChangedListener
	jclass ErrorOccurredListener;       // peersdk/peer/Peer$ErrorOccurredListener
	
	jclass VideoLossChangedEventArgs;   // peersdk/peer/event/VideoLossChangedEventArgs
	jclass VideoMotionChangedEventArgs; // peersdk/peer/event/VideoMotionChangedEventArgs
	jclass ErrorOccurredEventArgs;      // peersdk/peer/event/ErrorOccurredEventArgs
};
static clazz_t clazz;

struct methods_t {
	jmethodID ArrayList_init;
	jmethodID ArrayList_add;
	jmethodID ArrayList_clear;
	jmethodID PeerLog_init;
	jmethodID PeerHDD_init;
	jmethodID PeerChannel_init;
	jmethodID PeerRelay_init;
	jmethodID PeerRecorder_init;
	jmethodID PeerRecordList_init;
	
	jmethodID VideoLossChangedEventArgs_init;
	jmethodID VideoMotionChangedEventArgs_init;
	jmethodID ErrorOccurredEventArgs_init;
	
	jmethodID VideoLossChangedListener_OnVideoLossChanged;
	jmethodID VideoMotionChangedListener_OnVideoMotionChanged;
	jmethodID ErrorOccurredListener_OnErrorOccurred;
};
static methods_t methods;

///////////////////////////////////////////////////////////////////

// class PeerImpl
PeerImpl::PeerImpl(JNIEnv* env, jobject thiz)
{
	mInstance = new Peer();
	mObject = env->NewGlobalRef(thiz);
}

PeerImpl::~PeerImpl()
{
	delete mInstance;
	JNIEnv* env = GetJNIEnv();
	env->DeleteGlobalRef(mObject);
}

jobject PeerImpl::getObject()
{
	return mObject;
}

Peer* PeerImpl::get()
{
	return mInstance;
}

Peer** PeerImpl::_get()
{
	return &mInstance;
}

///////////////////////////////////////////////////////////////////

extern PeerStreamImpl* getPeerStreamImpl(JNIEnv* env, jobject obj);
extern PeerRecordListImpl* getPeerRecordListImpl(JNIEnv* env, jobject obj);

PeerImpl* getPeerImpl(JNIEnv* env, jobject obj)
{
	return (PeerImpl*)env->GetIntField(obj, fields.context);
}

static Peer* _Peer(JNIEnv* env, jobject obj)
{
	return getPeerImpl(env, obj)->get();
}

///////////////////////////////////////////////////////////////////

// event callback
static void OnVideoLossChanged(void* tag, PeerSDK::VideoLossChangedEventArgs const& args)
{
	JNIEnv* env = GetJNIEnvAttachThread();
	
	PeerImpl* impl = (PeerImpl*)tag;
	jobject listener = env->GetObjectField(impl->getObject(), fields.videoLossChangedListener);
	if (listener == NULL) {
		LOGE("videoLossChangedListener is NULL");
		return;
	}
	
	//////////////////////////////////////////////////////////////
	IntList active      = args.Active();
	IntList deactive    = args.Deactive();
	jintArray _active   = env->NewIntArray(active.Count());
	jintArray _deactive = env->NewIntArray(deactive.Count());
	jint* _activePtr    = env->GetIntArrayElements(_active, 0);
	jint* _deactivePtr  = env->GetIntArrayElements(_deactive, 0);

	for (int32 i = 0; i < active.Count(); i++)
		_activePtr[i] =  (jint)active[i];
		
	for (int32 i = 0; i < deactive.Count(); i++)
		_deactivePtr[i] =  (jint)deactive[i];
	
	env->ReleaseIntArrayElements(_active, _activePtr, 0);
	env->ReleaseIntArrayElements(_deactive, _deactivePtr, 0);
	/////////////////////////////////////////////////////////////
	
	

	jobject _args = env->NewObject(clazz.VideoLossChangedEventArgs, 
	                               methods.VideoLossChangedEventArgs_init, 
							       _active, _deactive);
	env->CallVoidMethod(listener, methods.VideoLossChangedListener_OnVideoLossChanged, impl->getObject(), _args);

	JNIEnvDeattachThread();
}
	
static void OnVideoMotionChanged(void* tag, PeerSDK::VideoMotionChangedEventArgs const& args)
{
	JNIEnv* env = GetJNIEnvAttachThread();
	
	PeerImpl* impl = (PeerImpl*)tag;
	jobject listener = env->GetObjectField(impl->getObject(), fields.videoMotionChangedListener);
	if (listener == NULL) {
		LOGE("videoMotionChangedListener is NULL");
		return;
	}
	
	/////////////////////////////////////////////////
	IntList active      = args.Active();
	IntList deactive    = args.Deactive();
	jintArray _active   = env->NewIntArray(active.Count());
	jintArray _deactive = env->NewIntArray(deactive.Count());
	jint* _activePtr    = env->GetIntArrayElements(_active, 0);
	jint* _deactivePtr  = env->GetIntArrayElements(_deactive, 0);

	for (int32 i = 0; i < active.Count(); i++)
		_activePtr[i] =  (jint)active[i];
		
	for (int32 i = 0; i < deactive.Count(); i++)
		_deactivePtr[i] =  (jint)deactive[i];
	
	env->ReleaseIntArrayElements(_active, _activePtr, 0);
	env->ReleaseIntArrayElements(_deactive, _deactivePtr, 0);
	////////////////////////////////////////////////////

	jobject _args = env->NewObject(clazz.VideoMotionChangedEventArgs, 
	                               methods.VideoMotionChangedEventArgs_init, 
							       _active, _deactive);
	env->CallVoidMethod(listener, methods.VideoMotionChangedListener_OnVideoMotionChanged, impl->getObject(), _args);

	JNIEnvDeattachThread();
}
	
static void OnErrorOccurred(void* tag, PeerSDK::ErrorOccurredEventArgs const& args)
{
	JNIEnv* env = GetJNIEnvAttachThread();
	
	PeerImpl* impl = (PeerImpl*)tag;
	jobject listener = env->GetObjectField(impl->getObject(), fields.errorOccurredListener);
	if (listener == NULL) {
		LOGE("errorOccurredListener is NULL");
		return;
	}
	
	/////////////////////////////////////////////////////////////
	String msg = args.Error().Message();	
	wchar_t const* cmsg = (wchar_t const*)msg;
	jstring _msg = env->NewString((const jchar*)cmsg, msg.Length());
	/////////////////////////////////////////////////////////////

	jobject _args = env->NewObject(clazz.ErrorOccurredEventArgs, methods.ErrorOccurredEventArgs_init,  (bool)args.Error(), _msg);
	env->CallVoidMethod(listener, methods.ErrorOccurredListener_OnErrorOccurred, impl->getObject(), _args);

	JNIEnvDeattachThread();
}


static void JNICALL peersdk_peer_Peer_native_setup(JNIEnv* env, jobject thiz)
{
	LOGV("native_setup");
	PeerImpl* peer = new PeerImpl(env, thiz);
	env->SetIntField(thiz, fields.context, (jint)peer);
}

static void JNICALL peersdk_peer_Peer_native_finalize(JNIEnv* env, jobject thiz)
{
	LOGV("native_finalize");
	PeerImpl* peer = getPeerImpl(env, thiz);
	delete peer;
}

static void JNICALL peersdk_peer_Peer_native_setOnVideoLossChangedListener(JNIEnv *env, jobject thiz)
{
	PeerImpl* peerImpl = getPeerImpl(env, thiz);
	Peer* peer = peerImpl->get();
	peer->VideoLossChanged().Add((void*)peerImpl, &OnVideoLossChanged);
}

static void JNICALL peersdk_peer_Peer_native_setOnVideoMotionChangedListener(JNIEnv *env, jobject thiz)
{
	PeerImpl* peerImpl = getPeerImpl(env, thiz);
	Peer* peer = peerImpl->get();
	peer->VideoMotionChanged().Add((void*)peerImpl, &OnVideoMotionChanged);
}

static void JNICALL peersdk_peer_Peer_native_setOnErrorOccurredListener(JNIEnv *env, jobject thiz)
{
	PeerImpl* peerImpl = getPeerImpl(env, thiz);
	Peer* peer = peerImpl->get();
	peer->ErrorOccurred().Add((void*)peerImpl, &OnErrorOccurred);
}

static jboolean JNICALL peersdk_peer_Peer_Connect(JNIEnv* env, jobject thiz, jstring host, jint port, jstring user, jstring password, jstring type, jboolean tryOthers)
{
	Peer* peer = _Peer(env, thiz);

	const char* _host = env->GetStringUTFChars(host, 0); // get Java string in UTF-8
	const char* _user = env->GetStringUTFChars(user, 0);
	const char* _password = env->GetStringUTFChars(password, 0);
	const char* _type = env->GetStringUTFChars(type, 0);
	
	PeerResult result = peer->Connect(_host, (int32)port, _user, _password, _type, (bool)tryOthers);

	env->ReleaseStringUTFChars(host, _host);
	env->ReleaseStringUTFChars(user, _user);	
	env->ReleaseStringUTFChars(password, _password);
	env->ReleaseStringUTFChars(type, _type);

	return (jboolean)(bool)result;
}

static jboolean JNICALL peersdk_peer_Peer_CreateLiveStream(JNIEnv* env, jobject thiz, jobject stream)
{
	Peer* peer = _Peer(env, thiz);
	PeerStream** s = getPeerStreamImpl(env, stream)->_get();
	
	return (jboolean)(bool)peer->CreateLiveStream(s);
}

static jboolean JNICALL peersdk_peer_Peer_CreateRecordedStream(JNIEnv* env, jobject thiz, 
	jint startYear, jint startMonth, jint startDay, jint startHour, jint startMinute, jint startSecond,
	jobject stream)
{
	Peer* peer = _Peer(env, thiz);
	PeerStream** s = getPeerStreamImpl(env, stream)->_get();
	
	//TODO
	DateTime startTime(startYear, startMonth, startDay, startHour, startMinute, startSecond, peer);
	return (jboolean)(bool) peer->CreateRecordedStream(startTime, s);
}

static jboolean JNICALL peersdk_peer_Peer_CreateRecordedStream2(JNIEnv* env, jobject thiz, 
	jint startYear, jint startMonth, jint startDay, jint startHour, jint startMinute, jint startSecond, 
	jint endYear, jint endMonth, jint endDay, jint endHour,jint endMinute, jint endSecond, 
	jobject stream)
{
	Peer* peer = _Peer(env, thiz);
	PeerStream** s = getPeerStreamImpl(env, stream)->_get();

	DateTime startTime(startYear, startMonth, startDay, startHour, startMinute, startSecond, peer);
	DateTime endTime(endYear, endMonth, endDay, endHour, endMinute, endSecond, peer);
	return (jboolean)(bool) peer->CreateRecordedStream(startTime, endTime, s);
}

static jboolean JNICALL peersdk_peer_Peer_CreateRecordList(JNIEnv* env, jobject thiz, jobject recordList)
{
	Peer* peer = _Peer(env, thiz);
	PeerRecordList** r = getPeerRecordListImpl(env, recordList)->_get();
	
	return (jboolean)(bool)peer->CreateRecordList(r);
}

static jobjectArray JNICALL peersdk_peer_Peer_GetLogList(JNIEnv* env, jobject thiz)
{
	Peer* peer = _Peer(env, thiz);
	
	PeerLogList list;
	peer->GetLogList(list);
	jobjectArray _list = env->NewObjectArray(list.Count(), clazz.PeerLog, NULL);
	
	for (int i = 0; i < list.Count(); i++)
	{
		jobject obj = env->NewObject(clazz.PeerLog, methods.PeerLog_init, (jint)&list[i]);
		env->SetObjectArrayElement(_list, i, obj);
		env->DeleteLocalRef(obj);
	}
	
	return _list;
}

static jobjectArray JNICALL peersdk_peer_Peer_GetHDDList(JNIEnv* env, jobject thiz)
{
	Peer* peer = _Peer(env, thiz);

	PeerHDDList list;
	peer->GetHDDList(list);
	jobjectArray _list = env->NewObjectArray(list.Count(), clazz.PeerHDD, NULL);
	
	for (int i = 0; i < list.Count(); i++)
	{
		jobject obj = env->NewObject(clazz.PeerHDD, methods.PeerHDD_init, (jint)&list[i]);
		env->SetObjectArrayElement(_list, i, obj);
		env->DeleteLocalRef(obj);
	}

	return _list;
}

static void JNICALL peersdk_peer_Peer_Ack(JNIEnv* env, jobject thiz)
{
	Peer* peer = _Peer(env, thiz);
	peer->Ack();
}

static jboolean JNICALL peersdk_peer_Peer_IsAvailableVideoFormatDetection(JNIEnv *env, jobject thiz, jint detection)
{
	Peer* peer = _Peer(env, thiz);
	bool available;
	
	peer->IsAvailableVideoFormatDetection((VideoFormatDetection)detection, available);
	return (jboolean)available;
}

static void JNICALL peersdk_peer_Peer_SetVideoFormatDetectMethod(JNIEnv *env, jobject thiz, jint detection)
{
	Peer* peer = _Peer(env, thiz);
	peer->SetVideoFormatDetectMethod((VideoFormatDetection)detection);
}

static jboolean JNICALL peersdk_peer_Peer_IsSupportedDeviceUUID(JNIEnv* env, jobject thiz)
{
	Peer* peer = _Peer(env, thiz);
	bool result;
	
    peer->IsSupportedDeviceUUID(result);
    return (jboolean)result;
}

static jboolean JNICALL peersdk_peer_Peer_IsSupportedPushNotification(JNIEnv* env, jobject thiz)
{
	Peer* peer = _Peer(env, thiz);
	bool result;
	
    peer->IsSupportedPushNotification(PushNotificationDevice_Android, result);
    return (jboolean)result;;
}

static jboolean JNICALL peersdk_peer_Peer_RegisterPushNotification(JNIEnv *env, jobject thiz, jstring token)
{
	Peer* peer = _Peer(env, thiz);
	String _token(env->GetStringUTFChars(token, 0));
    return (jboolean)(bool)peer->RegisterPushNotification(PushNotificationDevice_Android, _token);
}

static jboolean JNICALL peersdk_peer_Peer_UnregisterPushNotification(JNIEnv* env, jobject thiz, jstring token)
{
	Peer* peer = _Peer(env, thiz);
	String _token(env->GetStringUTFChars(token, 0));
    return (jboolean)(bool)peer->UnregisterPushNotification(PushNotificationDevice_Android, _token);
}

static jboolean JNICALL peersdk_peer_Peer_IsPushNotificationRegistered(JNIEnv* env, jobject thiz, jstring token)
{
	Peer* peer = _Peer(env, thiz);
	String _token(env->GetStringUTFChars(token, 0));
    bool registered;
    
    peer->IsPushNotificationRegistered(PushNotificationDevice_Android, _token, registered);
    return (jboolean)registered;
}

static void JNICALL peersdk_peer_Peer_Startup(JNIEnv* env, jobject thiz)
{
	Peer* peer = _Peer(env, thiz);
	peer->Startup();
}

static void JNICALL peersdk_peer_Peer_Cleanup(JNIEnv* env, jobject thiz)
{
	Peer* peer = _Peer(env, thiz);
	peer->Cleanup();
}

static jstring JNICALL peersdk_peer_Peer_Version(JNIEnv* env, jobject thiz)
{
	Peer* peer = _Peer(env, thiz);
	
	String value = peer->Version();
    const wchar_t* version = (const wchar_t*) value;
	int32 versionLen = value.Length();

	jchar cstr[versionLen + 1];
	memset(cstr, '\0', sizeof(cstr));
	for (int32 i = 0; i < versionLen; i++)
		cstr[i] = (jchar)version[i];

	jstring _version = env->NewString(cstr, versionLen);
	if (_version == NULL)
		jniThrowException(env, TAG, "Out of memory");

	return _version;
}

static jint JNICALL peersdk_peer_Peer_VideoFormatDetectMethod(JNIEnv* env, jobject thiz)
{
	Peer* peer = _Peer(env, thiz);
	return (jint)peer->VideoFormatDetectMethod();
}

static jint JNICALL peersdk_peer_Peer_OutputVideoFormat(JNIEnv* env, jobject thiz)
{
	Peer* peer = _Peer(env, thiz);
	return (jint)peer->OutputVideoFormat();
}

static jstring JNICALL peersdk_peer_Peer_DeviceUUID(JNIEnv* env, jobject thiz)
{
	Peer* peer = _Peer(env, thiz);
	
	String value = peer->DeviceUUID();
    const wchar_t* uuid = (const wchar_t*) value;
	int32 uuidLen = value.Length();
    
    jchar cstr[uuidLen + 1];
	memset(cstr, '\0', sizeof(cstr));
	for (int32 i = 0; i < uuidLen; i++)
		cstr[i] = (jchar)uuid[i];

	jstring _uuid = env->NewString(cstr, uuidLen);
	if (_uuid == NULL)
		jniThrowException(env, TAG, "Out of memory");
        
	return _uuid;
}

static jobjectArray JNICALL peersdk_peer_Peer_Channels(JNIEnv* env, jobject thiz)
{
	Peer* peer = _Peer(env, thiz);
	ChannelCollection& channels = peer->Channels();
    
	// create ArrayList instance
	jobjectArray _channels = env->NewObjectArray(channels.Count(), clazz.PeerChannel, NULL);

	for (int32 i = 0; i < channels.Count(); i++)
	{
		// create a PeerChannel object
		jobject obj = env->NewObject(clazz.PeerChannel, methods.PeerChannel_init, (jint)channels[i]);
		env->SetObjectArrayElement(_channels, i, obj);
		env->DeleteLocalRef(obj);
	}

	return _channels;
}

static jobjectArray JNICALL peersdk_peer_Peer_Relays(JNIEnv* env, jobject thiz)
{
	Peer* peer = _Peer(env, thiz);
	RelayCollection& relays = peer->Relays();

	// create ArrayList instance
	jobjectArray _relays = env->NewObjectArray(relays.Count(), clazz.PeerRelay, NULL);

	for (int32 i = 0; i < relays.Count(); i++)
	{
		// create a PeerRelay object
		jobject obj = env->NewObject(clazz.PeerRelay, methods.PeerRelay_init, (jint)relays[i]);

		env->SetObjectArrayElement(_relays, i, obj);
		env->DeleteLocalRef(obj);
	}

	return _relays;
}

static jobject JNICALL peersdk_peer_Peer_Recorder(JNIEnv* env, jobject thiz)
{
	Peer* peer = _Peer(env, thiz);
	return env->NewObject(clazz.PeerRecorder, methods.PeerRecorder_init, (PeerRecorder*)peer->Recorder());
}

//----------------------------------------------
/*
 * Table of methods associated with a single class.
 */
static JNINativeMethod gMethods[] = 
{
	// Method(declare in .class), Signature, FuncPtr
	{"native_setup",                           "()V",      (void*)peersdk_peer_Peer_native_setup },
	{"native_finalize",                        "()V",      (void*)peersdk_peer_Peer_native_finalize },
	{"native_setOnVideoLossChangedListener",   "()V",      (void*)peersdk_peer_Peer_native_setOnVideoLossChangedListener },
	{"native_setOnVideoMotionChangedListener", "()V",      (void*)peersdk_peer_Peer_native_setOnVideoMotionChangedListener },
	{"native_setOnErrorOccurredListener",      "()V",      (void*)peersdk_peer_Peer_native_setOnErrorOccurredListener },

	
	{"Connect",                           "(Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)Z",
			                                            (void*)peersdk_peer_Peer_Connect },
	{"CreateLiveStream",                  "(Lpeersdk/peer/PeerStream;)Z",
			                                            (void*)peersdk_peer_Peer_CreateLiveStream },
	{"CreateRecordedStream",              "(IIIIIILpeersdk/peer/PeerStream;)Z",
			                                            (void*)peersdk_peer_Peer_CreateRecordedStream },
	{"CreateRecordedStream",              "(IIIIIIIIIIIILpeersdk/peer/PeerStream;)Z",
			                                            (void*)peersdk_peer_Peer_CreateRecordedStream2 },
	{"CreateRecordList",                  "(Lpeersdk/peer/PeerRecordList;)Z",
			                                            (void*)peersdk_peer_Peer_CreateRecordList },
														
	{"GetLogList",                        "()[Lpeersdk/peer/PeerLog;",
			                                            (void*)peersdk_peer_Peer_GetLogList },
	{"GetHDDList",                        "()[Lpeersdk/peer/PeerHDD;",
			                                            (void*)peersdk_peer_Peer_GetHDDList },
	{"Ack",                               "()V",        (void*)peersdk_peer_Peer_Ack },
	{"IsAvailableVideoFormatDetection",   "(I)Z",       (void*)peersdk_peer_Peer_IsAvailableVideoFormatDetection },
	{"SetVideoFormatDetectMethod",        "(I)V",       (void*)peersdk_peer_Peer_SetVideoFormatDetectMethod },
    
    {"IsSupportedDeviceUUID",             "()Z",        (void*)peersdk_peer_Peer_IsSupportedDeviceUUID },
    {"IsSupportedPushNotification",       "()Z",        (void*)peersdk_peer_Peer_IsSupportedPushNotification },
    {"RegisterPushNotification",          "(Ljava/lang/String;)Z",   
	                                                    (void*)peersdk_peer_Peer_RegisterPushNotification },
    {"UnregisterPushNotification",        "(Ljava/lang/String;)Z",  
	                                                    (void*)peersdk_peer_Peer_UnregisterPushNotification },
    {"IsPushNotificationRegistered",      "(Ljava/lang/String;)Z",  
	                                                    (void*)peersdk_peer_Peer_IsPushNotificationRegistered },
    
	{"Startup",                           "()V",        (void*)peersdk_peer_Peer_Startup },
	{"Cleanup",                           "()V",        (void*)peersdk_peer_Peer_Cleanup },
	{"Version",                           "()Ljava/lang/String;",
	                                                    (void*)peersdk_peer_Peer_Version },
	{"VideoFormatDetectMethod",           "()I",        (void*)peersdk_peer_Peer_VideoFormatDetectMethod },
	{"OutputVideoFormat",                 "()I",        (void*)peersdk_peer_Peer_OutputVideoFormat },
	{"DeviceUUID",                        "()Ljava/lang/String;",
	                                                    (void*)peersdk_peer_Peer_DeviceUUID },
    
    {"Channels",                          "()[Lpeersdk/peer/PeerChannel;",
			                                            (void*)peersdk_peer_Peer_Channels },
	{"Relays",                            "()[Lpeersdk/peer/PeerRelay;",
			                                            (void*)peersdk_peer_Peer_Relays },
	{"Recorder",                          "()Lpeersdk/peer/PeerRecorder;",
			                                            (void*)peersdk_peer_Peer_Recorder },
};

static void InitJNIReference(JNIEnv* env)
{
	// get class reference
	jniNewClassGlobalReference(env, clazz.ArrayList,       "java/util/ArrayList");
	jniNewClassGlobalReference(env, clazz.PeerLog,         "peersdk/peer/PeerLog");
	jniNewClassGlobalReference(env, clazz.PeerHDD,         "peersdk/peer/PeerHDD");
	jniNewClassGlobalReference(env, clazz.PeerChannel,     "peersdk/peer/PeerChannel");
	jniNewClassGlobalReference(env, clazz.PeerRelay,       "peersdk/peer/PeerRelay");
	jniNewClassGlobalReference(env, clazz.PeerRecorder,    "peersdk/peer/PeerRecorder");
	jniNewClassGlobalReference(env, clazz.PeerRecordList,  "peersdk/peer/PeerRecordList");		
	jniNewClassGlobalReference(env, clazz.PeerStream,      "peersdk/peer/PeerStream");

	jniNewClassGlobalReference(env, clazz.VideoLossChangedListener,    "peersdk/peer/Peer$VideoLossChangedListener");
	jniNewClassGlobalReference(env, clazz.VideoMotionChangedListener,  "peersdk/peer/Peer$VideoMotionChangedListener");
	jniNewClassGlobalReference(env, clazz.ErrorOccurredListener,       "peersdk/peer/Peer$ErrorOccurredListener");
	
	jniNewClassGlobalReference(env, clazz.VideoLossChangedEventArgs,   "peersdk/peer/event/VideoLossChangedEventArgs");
	jniNewClassGlobalReference(env, clazz.VideoMotionChangedEventArgs, "peersdk/peer/event/VideoMotionChangedEventArgs");
	jniNewClassGlobalReference(env, clazz.ErrorOccurredEventArgs,       "peersdk/peer/event/ErrorOccurredEventArgs");

	// get methodID
	methods.ArrayList_init     = env->GetMethodID(clazz.ArrayList,       "<init>", "()V");
	methods.ArrayList_add      = env->GetMethodID(clazz.ArrayList,       "add",    "(Ljava/lang/Object;)Z"); 
	methods.ArrayList_clear    = env->GetMethodID(clazz.ArrayList,       "clear",  "()V");
	methods.PeerLog_init       = env->GetMethodID(clazz.PeerLog,         "<init>", "(I)V");
	methods.PeerHDD_init       = env->GetMethodID(clazz.PeerHDD,         "<init>", "(I)V");
	methods.PeerChannel_init   = env->GetMethodID(clazz.PeerChannel,     "<init>", "(I)V");
	methods.PeerRelay_init     = env->GetMethodID(clazz.PeerRelay,       "<init>", "(I)V");
	methods.PeerRecorder_init  = env->GetMethodID(clazz.PeerRecorder,    "<init>", "(I)V");
	methods.PeerRecordList_init = env->GetMethodID(clazz.PeerRecordList, "<init>", "()V");
						  
	methods.VideoLossChangedEventArgs_init   = env->GetMethodID(clazz.VideoLossChangedEventArgs,  
	                                                          "<init>",
												              "([I[I)V");
	methods.VideoMotionChangedEventArgs_init = env->GetMethodID(clazz.VideoMotionChangedEventArgs,
                                                              "<init>",
															  "([I[I)V");
	methods.ErrorOccurredEventArgs_init       = env->GetMethodID(clazz.ErrorOccurredEventArgs,
	                                                          "<init>",
															  "(ZLjava/lang/String;)V");
	methods.VideoLossChangedListener_OnVideoLossChanged
	                                         = env->GetMethodID(clazz.VideoLossChangedListener,       
											                  "OnVideoLossChanged",
															  "(Lpeersdk/peer/Peer;Lpeersdk/peer/event/VideoLossChangedEventArgs;)V");
	methods.VideoMotionChangedListener_OnVideoMotionChanged 
	                                         = env->GetMethodID(clazz.VideoMotionChangedListener,
											                  "OnVideoMotionChanged",
															  "(Lpeersdk/peer/Peer;Lpeersdk/peer/event/VideoMotionChangedEventArgs;)V");
	methods.ErrorOccurredListener_OnErrorOccurred
	                                         = env->GetMethodID(clazz.ErrorOccurredListener,
											                  "OnErrorOccurred",
															  "(Lpeersdk/peer/Peer;Lpeersdk/peer/event/ErrorOccurredEventArgs;)V");	

}

static const char* const kClassPathName = "peersdk/peer/Peer";
int register_peersdk_peer_Peer(JNIEnv *env)
{
	InitJNIReference(env);
	
	jclass clz;
	
	clz = env->FindClass(kClassPathName);
	if (clz == NULL) {
        LOGE("Can't find %s", kClassPathName);
        return -1;
    }
	
	// get fieldID
	fields.context = env->GetFieldID(clz, "mNativeContext", "I");
	if (fields.context == NULL) {
		LOGE("Can't find Peer.mNativeContext");
		return -1;
	}
	
	fields.videoLossChangedListener = env->GetFieldID(clz, "mVideoLossChangedListener", "Lpeersdk/peer/Peer$VideoLossChangedListener;");
	if (fields.videoLossChangedListener == NULL) {
		LOGE("Can't find Peer.mVideoLossChangedListener");
		return -1;
	}
	
	fields.videoMotionChangedListener = env->GetFieldID(clz, "mVideoMotionChangedListener", "Lpeersdk/peer/Peer$VideoMotionChangedListener;");
	if (fields.videoMotionChangedListener == NULL) {
		LOGE("Can't find Peer.mVideoMotionChangedListener");
		return -1;
	}
	
	fields.errorOccurredListener = env->GetFieldID(clz, "mErrorOccurredListener", "Lpeersdk/peer/Peer$ErrorOccurredListener;");
	if (fields.errorOccurredListener == NULL) {
		LOGE("Can't find Peer.mErrorOccurredListener");
		return -1;
	}

	////////////////////////////////////////////////////////////////
	
	if (env->RegisterNatives(clz, gMethods, sizeof(gMethods) / sizeof(gMethods[0])) < 0)
	{		
		LOGE("RegisterNatives failed for '%s'\n", kClassPathName);
		return -1;
	} 
	return 1;
}
