package peersdk.media;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import peersdk.NativeObject;
import refactor.remote.iWatchDVR.application.CanvasRender;
import refactor.remote.iWatchDVR.application.CanvasView;


public class MediaDispatcher extends NativeObject implements VideoFrameDecodedListener {

    final String TAG = "__MediaDispatcher__";

    List<byte[][]> mVideoBuffer = new ArrayList<byte[][]>();

    CanvasView mCanvas;
    CanvasRender mRender;
    
    public MediaDispatcher(int channels, int core) {

        synchronized (this) {
            for (int i = 0; i < channels; i++)
                mVideoBuffer.add(null);
        }
        native_setup(channels, core);
    }
    
    public void Dispose() {
        native_finalize();
    }

    public void SetSurface(CanvasView surface) {
        synchronized(this) {
            mCanvas = surface;
            mRender = surface.getRender();
            if (mRender == null)
                throw new NullPointerException("CanvasView's render is null");
        }
    }


    native protected void native_setup(int channels, int core);
    native protected void native_finalize();
    
    // be invoked in native
    protected void CreateVideoBuffer(int channel, int bufferLength) {
        mVideoBuffer.set(channel, new byte[3][bufferLength]);
    }
    
    protected void ReleaseVideoBuffer(int channel) {
    }
    
    protected byte[][] GetVideoBuffer(int channel) {
        return mVideoBuffer.get(channel);
    }

    // decode and paste raw data on texture in native
    native private void native_addAudio(int tag, int audioType, int channel, int nativeBufferPointer, int bufferLength, long pts, float speed, long time);
    native private void native_addVideo(int tag, int videoType, int channel, int nativeBufferPointer, int BufferLength, long pts, int width, int height, float speed, long time);

    // ?
    public void AddAudio(int tag, int audioType, int channel, int bufferLength, int nativeBufferPointer, long pts, float speed, long time) {
        native_addAudio(tag, audioType, channel, bufferLength, nativeBufferPointer, pts, speed, time);
    }
    
    
    public void AddVideo(int tag, int videoType, int channel, int bufferLength, int nativeBufferPointer, long pts, int width, int height, float speed, long time) {
        native_addVideo(tag, videoType, channel, bufferLength , nativeBufferPointer, pts, width, height, speed, time);
    }

    
    @Override
    public void OnVideoOneFrameDecoded(int channel, int width, int height,
            int[] outputSize, int format, int croppedLeft, int croppedTop) {
        // be invoked in native

        
        int [] stride = new int[3];
        byte[][] dstBuffer = new byte[3][];
        if (!mRender.beginVideo(channel, width, height, format, dstBuffer, stride))
            return;
        
        int [] h = new int[]{ height, height / 2, height / 2 };
        
        for (int x = 0; x < 3; x++) {
            if (dstBuffer[x] == null) {
                mRender.endVideo(channel);
                break;
            }
            
            byte[] src = mVideoBuffer.get(channel)[x];
            byte[] dst = dstBuffer[x];
            int srcOffset = croppedLeft + croppedTop * stride[x];
            int dstOffset = 0;

            for (int i = 0; i < h[x]; i++)
            {
                System.arraycopy(src, srcOffset, dst, dstOffset, outputSize[x]);
                srcOffset += outputSize[x];
                dstOffset += stride[x];
            }
        }
        mRender.endVideo(channel);
        
        if (mCanvas == null) {
            return;
        }
        mCanvas.doRender();
        
    }
}