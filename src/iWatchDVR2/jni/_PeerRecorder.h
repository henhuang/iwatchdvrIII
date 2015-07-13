#pragma once

class PeerRecorderImpl
{
public:
	PeerRecorderImpl(JNIEnv* env, jobject thiz, PeerSDK::PeerRecorder* instance);
	virtual ~PeerRecorderImpl();
	
public:
	PeerSDK::PeerRecorder* get();

protected:
	PeerSDK::PeerRecorder* mInstance;
};
