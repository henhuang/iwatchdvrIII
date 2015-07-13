package refactor.remote.iWatchDVR.application;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import peersdk.peer.event.ErrorOccurredEventArgs;
import peersdk.peer.event.VideoLossChangedEventArgs;
import peersdk.peer.event.VideoMotionChangedEventArgs;
import refactor.remote.iWatchDVR.dvr.args.Channel;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class DVREventAgent extends Thread {
    
    final String TAG = "__App DVREventAgent__";

    Handler mHandler;
    protected boolean mDisposing = false;
    protected boolean mDisposed = false;
    
    public final static int Event_Connect    = 0x30;
    public final static int Event_Disconnect = 0x31;
    public final static int Event_VideoLossChanged = 0x32;
    public final static int Event_VideoMotionChanged = 0x33;
    public final static int Event_ErrorOccurred = 0x34;

    public final static int Response_GetChannel = 0x50;
    public final static int Response_GetChannels = 0x51;
    public final static int Response_GetLogs = 0x52;
    
    RemoteDVRApplication mApp;
    
    // Receive event or response from peer
    DVREventAgent(RemoteDVRApplication app) {
        mApp = app;
    }
 
    
    @Override
    public void run() {

        Looper.prepare();
        
        synchronized (this) {
            mHandler = new Handler() {
                
                @Override
                public void handleMessage(Message msg) {

                    switch(msg.what) {
                    case Event_Connect:
                        //Log.i(TAG, "Event_Connect: " + (String)msg.obj);
                        mApp.Raise_Connect((String)msg.obj);
                        break;
                        
                    case Event_Disconnect:
                        //Log.i(TAG, "Event_Disconnect: " + (String)msg.obj);
                        mApp.Raise_Disconnect((String)msg.obj);
                        break;
                        
                    case Event_VideoLossChanged:
                        //Log.i(TAG, "Event_VideoLossChanged");
                        mApp.Raise_VideoLossChanged((VideoLossChangedEventArgs)msg.obj);
                        break;
                        
                    case Event_VideoMotionChanged:
                        //Log.i(TAG, "Event_VideoMotionChanged");
                        mApp.Raise_VideoMotionChanged((VideoMotionChangedEventArgs)msg.obj);
                        break;
                        
                    case Event_ErrorOccurred:
                        //Log.i(TAG, "Event_ErrorOccurred");
                        mApp.Raise_ErrorOccurred((ErrorOccurredEventArgs)msg.obj);
                        break;
                        
                    case Response_GetChannel:
                        //Log.i(TAG, "Response_GetChannel");
                        mApp.Raise_GetChannel((Channel)msg.obj);
                        break;
                        
                    case Response_GetChannels:
                        //Log.i(TAG, "Response_GetChannels");
                        mApp.Raise_GetChannels((Channel[])msg.obj);
                        break;
                        
                    default:
                        Log.i(TAG, "Unsupported Event");
                        break;
                    }
                }
                
            };

            notifyAll();
        }
        
        Looper.loop();
    }
    
    //////////////////////////////////////////////////////////////////////////
    
    public void Start() {
        start();
        synchronized(this) {
            try {   
                wait(); // wait for handle initialization has done
                Log.i(TAG, "dvr event agnet is running");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void Dispose() {
        mHandler.getLooper().quit();
        Log.i(TAG, "Handler has quit");
        try {
            join();
            Log.i(TAG, "Thread has stop");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void SendMessage(int what) {
        mHandler.sendEmptyMessage(what);
    }
    
    public void SendMessage(Message msg) {
        mHandler.sendMessage(msg);
    }
    
    public void SendMessage(int what, Object data) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = data;
        mHandler.sendMessage(msg); 
    }
}