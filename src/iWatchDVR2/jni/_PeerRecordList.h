#pragma once

class PeerRecordListImpl
{
public:
	PeerRecordListImpl(JNIEnv* env, jobject thiz);
	virtual ~PeerRecordListImpl();
	
public:
	jobject getObject();
	PeerSDK::PeerRecordList*  get();
	PeerSDK::PeerRecordList** _get();
	
protected:
	jobject mObject;
	PeerSDK::PeerRecordList* mInstance;
};
