package refactor.remote.iWatchDVR;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

import peersdk.AudioType;
import android.media.AudioTrack;
import android.util.Log;

public class AudioRender {
    
    public static final String TAG = "__AudioRender__";

    AudioTrack mTrack;
    final ReentrantLock mLock = new ReentrantLock();
    
    boolean mPause          = false;

    ArrayList<ByteBuffer> mBuffer        = new ArrayList<ByteBuffer>();
    int                   mChannel = 0;
    
    AudioThread  mAudioThread;

    public boolean createTrack(final int index, int audioFormat) {
        mLock.lock();
        //Log.i(TAG, "createTrack=" + index);
        if (mAudioThread != null) {
            mBuffer.clear();
            mAudioThread.requestExit();

            try {
                mAudioThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        init(index, audioFormat);
        mLock.unlock();
        return true;
    }

    protected void init(int index, int audioFormat) {
        if (mTrack != null)
            stop();
        
        mChannel = index;
        mBuffer.clear();
        
        int format;
        switch (audioFormat)
        {
        case AudioType.AudioType_IMA_ADPCM_16Bits_8000Hz:
        case AudioType.AudioType_LPCM_16Bits_8000Hz:
            format = android.media.AudioFormat.ENCODING_PCM_16BIT;
            break;
        case AudioType.AudioType_LPCM_8Bits_8000Hz:
            format = android.media.AudioFormat.ENCODING_PCM_8BIT;
            break;
        default:
            return;
        }
        
        int minBufferSize = AudioTrack.getMinBufferSize(
                8000, android.media.AudioFormat.CHANNEL_OUT_MONO,
                android.media.AudioFormat.ENCODING_PCM_16BIT);

        mTrack = new AudioTrack(android.media.AudioManager.STREAM_MUSIC, 
                8000, 
                android.media.AudioFormat.CHANNEL_OUT_MONO, 
                format, 
                minBufferSize * 10,
                android.media.AudioTrack.MODE_STREAM);
        
        mAudioThread = new AudioThread(index);
        mAudioThread.start();
        mTrack.play();
    }

    public boolean beginAudio(int index, byte[] src, int size) {
        mLock.lock();
        if (mPause) {
            
            if (mBuffer.size() > 0)
                mBuffer.clear();
            mLock.unlock();
            return true;
        }
          
        mBuffer.add(ByteBuffer.allocate(size));
        int i = mBuffer.size() - 1;
        ByteBuffer dst = mBuffer.get(i);
        dst.put(src, 0, size);
   
        if (mAudioThread.needToWait() && mBuffer.size() > 20)
            mAudioThread.requestPlay();
        mLock.unlock();

        return true;
    }
    
    public void fill(ByteBuffer src) {
        if (mTrack == null)
            return;
        
        int numWitten = mTrack.write(src.array(), 0, src.capacity());
        if (numWitten != src.capacity()) {

            mTrack.flush();
            return;
        }
    }


    ///////////////////////////////////////
    
    public void play() {
        synchronized (this) {
            mPause = false;
        }
    }
    
    public void pause() {
        synchronized (this) {
            mPause = true;
        }

        if (mAudioThread != null)
            mAudioThread.requestWait();
    }
    
    protected void stop() {
        mTrack.flush();
        mTrack.release();
        mTrack = null;
    }
    
    public void release() {
        mLock.lock();

        mBuffer.clear();

        final AudioThread thread = mAudioThread;
        if (thread != null) {
            
            new Thread() {
               
                @Override
                public void run() {
    
                    thread.requestExit();
                }
            }.start();
        }

        mLock.unlock();
    }
    
    private class AudioThread extends Thread {
        
        boolean mRun      = false;
        boolean mWait     = true;
        
        AudioThread(int channel) {
            
            super();
            setPriority(Thread.NORM_PRIORITY);
            mChannel = channel;
        }

        @Override
        public void run() {
            
            mRun = true;
            boolean pausing = false;
            while(mRun) {
                
                try {

                    synchronized  (this)  {
                        
                        while (needToWait()) {

                            if (mPause) {
                                mTrack.pause();
                                mTrack.flush();
                                pausing = true;
                            }

                            wait();
                            if (!mRun)
                                break;
                            
                            if (pausing) {
                            
                                mTrack.play();
                                pausing = false;
                            }
                        }
                    }

                    mLock.lock();

                    ByteBuffer src = null;
                    if (mBuffer.size() == 0)
                    {
                        requestWait();
                        mLock.unlock();
                        continue;
                    }
                    
                    src = mBuffer.get(0);
                    mBuffer.remove(0);
                    mLock.unlock();
                    
                    fill(src);
                }
                catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
            
            if (mTrack != null) {
                
                mTrack.flush();
                mTrack.release();
            }
        }

        
        protected void requestExit() {
            synchronized(this) {

                mRun  = false;
                mWait = false;
                notifyAll(); // if thread is waiting....
            }
        }

        protected void requestPlay() {
            synchronized(this) {
                mWait = false;
                notify();
            }
        }
        
        protected boolean needToWait() {
            return mWait;
        }
        
        protected void requestWait() {
            synchronized(this) {
                mWait = true;
            }
        }

    }
}
