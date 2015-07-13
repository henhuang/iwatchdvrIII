#pragma once

#define MAX_DVR_CHANNELS 32

class MediaDispatcherImpl
{
public:
	MediaDispatcherImpl(JNIEnv* env, jobject thiz, int32 channels, int32 core);
	virtual ~MediaDispatcherImpl();
	
public:
	iCatch::Media::MediaDispatcher*  get();
	
	jobject getObject();
	
protected:
	iCatch::Media::MediaDispatcher* mInstance;
	jobject mObject;
	
public:
	void SetCanvas(jobject weakCanvasView, jobject weakRender);

protected:
	jobject mCanvasViewWeak;

protected:
	void OnVideoDecode(iCatch::Media::VideoDecodeEventArgs const& e);
	void OnAudioDecode(iCatch::Media::AudioDecodeEventArgs const& e);
	
public:
	void CreateVideoDecoder(JNIEnv* env, int32 index, int32 width, int32 height);
	void DisposeVideoDecoder(int32 index);

	void DisposeAudioDecoder(int32 index);
	
protected:	
	iCatch::Media::FFmpeg_H264VideoDecoder* mVideoDecoder[MAX_DVR_CHANNELS];
	bool                     mNeedWaitI[MAX_DVR_CHANNELS];
	iCatch::Media::Size      mVideoMode[MAX_DVR_CHANNELS];
	iCatch::DateTime         mVideoTime[MAX_DVR_CHANNELS];
};