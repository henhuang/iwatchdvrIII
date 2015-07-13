#pragma once

class TimeRangeImpl
{
public:
	TimeRangeImpl(JNIEnv* env, jobject thiz, PeerSDK::TimeRange* instance);
	virtual ~TimeRangeImpl();
	
public:
	PeerSDK::TimeRange const& get();

protected:
	PeerSDK::TimeRange mInstance;
};
