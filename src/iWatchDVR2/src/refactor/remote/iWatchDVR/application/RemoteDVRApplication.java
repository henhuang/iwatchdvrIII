package refactor.remote.iWatchDVR.application;

import java.util.HashMap;
import java.util.Map;

import peersdk.TimeRange;
import peersdk.TimeSpan;
import peersdk.peer.PeerLog;
import peersdk.peer.event.ErrorOccurredEventArgs;
import peersdk.peer.event.VideoLossChangedEventArgs;
import peersdk.peer.event.VideoMotionChangedEventArgs;
import refactor.remote.iWatchDVR.CacheBuffer;
import refactor.remote.iWatchDVR.DVRProvider;
import refactor.remote.iWatchDVR.R;
import refactor.remote.iWatchDVR.dvr.ConnectInfo;
import refactor.remote.iWatchDVR.dvr.PeerDVRListener;
import refactor.remote.iWatchDVR.dvr.PeerDVR;
import refactor.remote.iWatchDVR.dvr.PeerStreamListener;
import refactor.remote.iWatchDVR.dvr.args.Channel;
import refactor.remote.iWatchDVR.dvr.args.Relay;
import android.app.Application;
import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

public class  RemoteDVRApplication extends Application implements PeerDVRListener, PeerStreamListener {
    
    static {
        System.loadLibrary("P2PTunnelAPIs");
        System.loadLibrary("peersdk-jni");
    }
    
    public static final String TAG = "__RemoteDVRApplication__";

    private Visuals    mVisuals;
    private PeerDVR    mDVR;
    private DVREventAgent mEvent;
    private Map<String, DVRListener> mListener = new HashMap<String, DVRListener>();

    
    public boolean CreatePeerDVR() {
        synchronized (this) {
            mListener.clear();
            
            if (mDVR != null) {
                mDVR.Dispose();
                mEvent.Dispose();
            }
            
            mDVR = new PeerDVR(this);
            mEvent = new DVREventAgent(this);
            mEvent.Start();
        }
        return true;
    }
    
    public void DisposePeerDVR() {
        synchronized (this) {
            mListener.clear();
            
            if (mDVR != null) {
                mDVR.Dispose();
                mEvent.Dispose();
            }
        }
    }
    
    public void CreateVisuals(Context context, int channelCount, int x, int y, int width, int height, int defaultDivide, int defaultVisualID) {
        synchronized (this) {
            mVisuals = new Visuals(context, channelCount, x, y, width, height, defaultDivide, defaultVisualID);
        }
    }
    
    public PeerDVR dvr() {
        return mDVR;
    }
    
    public Visuals getVisuals() {
        return mVisuals;
    }
    /////////////////////////////////////////////////////////////////////////////

    public boolean IsDualPane() {
        return getResources().getBoolean(R.bool.screenOrientationFixLandscape);
    }
    
    public Uri getURI() {
        return Uri.parse("content://" + getResources().getString(R.string.providerAuthority) + "/" + DVRProvider.DVR_PATH);
    }
    
    public Uri getURIById(int id) {
        return ContentUris.withAppendedId(
                Uri.parse("content://" + getResources().getString(R.string.providerAuthority) + "/" + DVRProvider.DVR_PATH),
                id);
    }
    
    public Uri getURIByUuid(String uuid) {
        return Uri.parse("content://" + getResources().getString(R.string.providerAuthority) + "/" + DVRProvider.DVR_PATH_UUID + "/" + uuid);
    }

    //////////////////////////////
    
    public boolean AddPeerListener(DVRListener listener, String tag) {
        synchronized(this) {
            if (mListener.containsKey(tag))
                return false;
            Log.i(TAG, "AddPeerListener: " + tag);
            mListener.put(tag, listener);
            return true;
        }
    }
    
    public boolean RemovePeerListener(String tag) {
        synchronized(this) {
            if (!mListener.containsKey(tag))
                return false;
            Log.i(TAG, "RemovePeerListener: " + tag);
            mListener.remove(tag);
            return true;
        }
    }
    
    public void RemovePeerListenerAll() {
        synchronized(this) {
            Log.i(TAG, "RemovePeerListenerAll");
            mListener.clear();
        }
    }
    
  //=================================================================


    @Override
    public void OnConnected(String host, boolean connected) {
        if (connected)
            mEvent.SendMessage(DVREventAgent.Event_Connect, host);
        else
            mEvent.SendMessage(DVREventAgent.Event_Disconnect, host);
    }

    @Override
    public void OnErrorOccurred(ErrorOccurredEventArgs arg) {
        mEvent.SendMessage(DVREventAgent.Event_ErrorOccurred, arg);
    }

    @Override
    public void OnVideoLossChanged(VideoLossChangedEventArgs arg) {
        mEvent.SendMessage(DVREventAgent.Event_VideoLossChanged, arg);
    }

    @Override
    public void OnVideoMotionChanged(VideoMotionChangedEventArgs arg) {
        mEvent.SendMessage(DVREventAgent.Event_VideoMotionChanged, arg);
    }
    
    
    @Override
    public void GetChannel(Channel arg) {
        mEvent.SendMessage(DVREventAgent.Response_GetChannel, arg);
    }

    @Override
    public void GetChannels(Channel[] arg) {
        mEvent.SendMessage(DVREventAgent.Response_GetChannels, arg);
    }
    
    @Override
	public void GetLogs(PeerLog[] arg) {
    	mEvent.SendMessage(DVREventAgent.Response_GetLogs, arg);
	}
    
    //=================================================================
    
    protected void Raise_Connect(String host) {
        synchronized (this) {
            for (Map.Entry<String, DVRListener> e : mListener.entrySet()) {
                e.getValue().OnConnected(host);
            }
        }
    }
    
    protected void Raise_Disconnect(String host) {
        synchronized (this) {
            for (Map.Entry<String, DVRListener> e : mListener.entrySet())
                e.getValue().OnDisconnected(host);
        }
    }
    
    protected void Raise_VideoLossChanged(VideoLossChangedEventArgs arg) {
        synchronized (this) {
            for (Map.Entry<String, DVRListener> e : mListener.entrySet())
                e.getValue().OnVideoLossChanged(arg);
        }
    }
    
    protected void Raise_VideoMotionChanged(VideoMotionChangedEventArgs arg) {
        synchronized (this) {
            for (Map.Entry<String, DVRListener> e : mListener.entrySet())
                e.getValue().OnVideoMotionChanged(arg);
        }
    }
    
    protected void Raise_ErrorOccurred(ErrorOccurredEventArgs arg) {
        synchronized (this) {
            for (Map.Entry<String, DVRListener> e : mListener.entrySet())
                e.getValue().OnErrorOccurred(arg);
        }
    }
    
    protected void Raise_GetChannel(Channel arg) {
        synchronized (this) {
            for (Map.Entry<String, DVRListener> e : mListener.entrySet())
                e.getValue().OnGetChannel(arg);
        }
    }
    
    protected void Raise_GetChannels(Channel[] arg) {
        synchronized (this) {
            for (Map.Entry<String, DVRListener> e : mListener.entrySet())
                e.getValue().OnGetChannels(arg);
        }
    }
    
    protected void Raise_GetLogs(PeerLog[] arg) {
    	synchronized (this) {
    		for (Map.Entry<String, DVRListener> e : mListener.entrySet())
                e.getValue().GetLogs(arg);
    	}
    }

    @Override
    public void GetInitialConfig(Channel[] channels,
            boolean hasRelayPermission, Relay[] relays, boolean notifySupported,
            boolean notifyEnabled) {
        
        synchronized (this) {
            for (Map.Entry<String, DVRListener> e : mListener.entrySet())
                e.getValue().OnGetInitialConfig(channels, hasRelayPermission, relays, notifySupported, notifyEnabled);
        }
    }
    
    @Override
    public void GetPTZAvailablePreset(int index, int[] arg) {
        synchronized (this) {
            for (Map.Entry<String, DVRListener> e : mListener.entrySet())
                e.getValue().OnGetPTZAvailablePreset(index, arg);
        }
    }
    
    @Override
    public void GetRecordList(TimeRange[] arg) {
        synchronized (this) {
            for (Map.Entry<String, DVRListener> e : mListener.entrySet())
                e.getValue().OnGetRecordList(arg);
        }
    }

    @Override
    public void GetRecordedMinutesOfDay(TimeSpan[] arg) {
        synchronized (this) {
            for (Map.Entry<String, DVRListener> e : mListener.entrySet())
                e.getValue().OnGetRecordedMinutesOfDay(arg);
        }
    }

    @Override
    public void GetRecordedDaysOfMonth(int[] arg) {
        synchronized (this) {
            for (Map.Entry<String, DVRListener> e : mListener.entrySet())
                e.getValue().OnGetRecordedDaysOfMonth(arg);
        }
    }

    /////////// PeerStreamListener ////////////////////////
    @Override
    public void OnVideoFirstFrameArrived(int channel) {
        synchronized (this) {
            for (Map.Entry<String, DVRListener> e : mListener.entrySet())
                e.getValue().OnVideoFirstFrameArrived(channel);
        }
    }
    
    @Override
    public void OnStreamStartConnect(int channel) {
        synchronized (this) {
            for (Map.Entry<String, DVRListener> e : mListener.entrySet())
                e.getValue().OnStreamStartConnect(channel);
        }
    }

    @Override
    public void OnStreamErrorOccurred(ErrorOccurredEventArgs arg) {
        synchronized (this) {
            for (Map.Entry<String, DVRListener> e : mListener.entrySet())
                e.getValue().OnStreamErrorOccurred(arg);
        }
    }

	

    

}
