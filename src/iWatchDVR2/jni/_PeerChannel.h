#pragma once

class PeerChannelImpl
{
public:
	PeerChannelImpl(JNIEnv* env, jobject thiz, PeerSDK::PeerChannel* instance);
	virtual ~PeerChannelImpl();
	
public:
	PeerSDK::PeerChannel* get();

protected:
	PeerSDK::PeerChannel* mInstance;
	PeerSDK::Peer* mPeer;
};
