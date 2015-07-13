#pragma once

class PeerAudioImpl
{
public:
	PeerAudioImpl(JNIEnv* env, jobject thiz, PeerSDK::PeerAudio* instance);
	virtual ~PeerAudioImpl();
	
public:
	PeerSDK::PeerAudio* get();

protected:
	PeerSDK::PeerAudio* mInstance;
};
