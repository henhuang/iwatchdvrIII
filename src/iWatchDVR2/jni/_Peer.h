#pragma once

class PeerImpl
{
public:
	PeerImpl(JNIEnv* env, jobject thiz);
	virtual ~PeerImpl();

public:
	jobject getObject();
	PeerSDK::Peer* get();
	PeerSDK::Peer** _get();
	
protected:
	jobject mObject;
	PeerSDK::Peer* mInstance;
};




