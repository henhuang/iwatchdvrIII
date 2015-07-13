#pragma once

class PeerPTZImpl
{
public:
	PeerPTZImpl(JNIEnv* env, jobject thiz, PeerSDK::PeerPTZ* instance);
	virtual ~PeerPTZImpl();
	
public:
	PeerSDK::PeerPTZ* get();

protected:
	PeerSDK::PeerPTZ* mInstance;
};
