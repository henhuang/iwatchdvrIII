#pragma once

class PeerRelayImpl
{
public:
	PeerRelayImpl(JNIEnv* env, jobject thiz, PeerSDK::PeerRelay* instance);
	virtual ~PeerRelayImpl();
	
public:
	PeerSDK::PeerRelay* get();

protected:
	PeerSDK::PeerRelay* mInstance;
};
