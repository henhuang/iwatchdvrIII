#pragma once

class PeerLogImpl
{
public:
	PeerLogImpl(JNIEnv* env, jobject thiz, PeerSDK::PeerLog* instance);
	virtual ~PeerLogImpl();
	
public:
	PeerSDK::PeerLog*  get();
	
protected:
	PeerSDK::PeerLog* mInstance;
};
