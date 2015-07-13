#pragma once

class PeerStreamImpl
{
public:
	PeerStreamImpl(JNIEnv* env, jobject thiz);
	virtual ~PeerStreamImpl();
	
public:
	PeerSDK::PeerStream*  get();
	PeerSDK::PeerStream** _get();
	jobject getObject();
	
protected:
	PeerSDK::PeerStream* mInstance;
	jobject mObject;
};
