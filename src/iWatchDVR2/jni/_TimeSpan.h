#pragma once

class TimeSpanImpl
{
public:
	TimeSpanImpl(JNIEnv* env, jobject thiz, PeerSDK::TimeSpan* instance);
	virtual ~TimeSpanImpl();
	
public:
	PeerSDK::TimeSpan const& get();

protected:
	PeerSDK::TimeSpan mInstance;	
};
