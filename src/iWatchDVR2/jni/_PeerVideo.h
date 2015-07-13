#pragma once

class PeerVideoImpl
{
public:
	PeerVideoImpl(JNIEnv* env, jobject thiz, PeerSDK::PeerVideo* instance);
	virtual ~PeerVideoImpl();
	
public:
	PeerSDK::PeerVideo* get();

protected:
	PeerSDK::PeerVideo* mInstance;
};
