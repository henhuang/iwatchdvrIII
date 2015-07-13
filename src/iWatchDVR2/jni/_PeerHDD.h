#pragma once

class PeerHDDImpl
{
public:
	PeerHDDImpl(JNIEnv* env, jobject thiz, PeerSDK::PeerHDD* instance);
	virtual ~PeerHDDImpl();
	
public:
	PeerSDK::PeerHDD*  get();
	jobject getObject();
	
protected:
	PeerSDK::PeerHDD* mInstance;
	jobject mObject;
};
