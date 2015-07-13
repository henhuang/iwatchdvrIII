package refactor.remote.iWatchDVR.dvr;

import java.sql.Date;

import android.graphics.PointF;
import android.os.Message;
import peersdk.media.MediaDispatcher;
import peersdk.peer.Peer;
import peersdk.peer.PeerPTZ;
import peersdk.peer.PeerRecordList;
import peersdk.peer.PeerStream;
import peersdk.peer.event.ErrorOccurredEventArgs;
import peersdk.peer.event.VideoLossChangedEventArgs;
import peersdk.peer.event.VideoMotionChangedEventArgs;
import refactor.remote.iWatchDVR.CacheBuffer;
import refactor.remote.iWatchDVR.RemoteDVRActivity;
import refactor.remote.iWatchDVR.application.CanvasView;
import refactor.remote.iWatchDVR.application.RemoteDVRApplication;
import refactor.remote.iWatchDVR.dvr.args.Channel;

public class PeerDVR {

    public final static String Connect_Start = "connected";
    public final static String HasRelayPermission = "hasRelayPermission";
    public final static String NotifySupported = "notifySupported";
    public final static String NotifyEnabled = "notifyEnabled";
    
    public final static String ID = "id";
    public final static String Channels = "channels";
    public final static String Relays = "relays";
    
    PeerDVRListener mListener;

    RemoteDVRApplication mApp;
    
    protected Peer mPeer;
    protected PeerStream mStream;
    protected MediaDispatcher mDispatcher;
    int mQuality = PeerStream.LQ; // LQ
    int mVideoMask;
    int mAudioMask;
    
    int mRecordQuality = PeerStream.HQ;
    int mRecordVideoMask;
    int mRecordAudioMask;
    Date mRecordStart;
    Date mRecordEnd;
    
    protected EventAgent mEventAgent;
    
    ConnectInfo mInfo;
    int mStatus = ConnectStatus.Disconnected;
    
    protected PeerPTZ mPTZ;
    protected int mPTZIndex = -1;

    
    public CacheBuffer mCache;
    
    public Channel[] mChannel;
 
    public PeerDVR(RemoteDVRApplication app) {
        mApp = app;
        mPeer = new Peer();
        mCache = new CacheBuffer();
        RegisterListener(app);
        mEventAgent = new EventAgent(this);
        mEventAgent.Start();
    }

    public void SetCanvas(CanvasView surface) {
        if (mDispatcher == null)
            return;
        mDispatcher.SetSurface(surface);
    }
    
    public void Dispose() {
        RemoveListener();
        mEventAgent.SendMessage(EventAgent.DVR_Dispose);
    }

    private void RegisterListener(PeerDVRListener listener) {
        synchronized (this) {
            if (mListener == listener)
                return;
            mListener = listener; 
        }
    }
    
    private void RemoveListener() {
        synchronized (this) {
            mListener = null; 
        }
    }

    protected void setStatus(int status) {
        synchronized (this) {
            mStatus = status;
        }
    }
    
    public Channel[] ChannelInfo() {
        return mChannel;
    }
    
    public Peer Peer() {
        return mPeer;
    }
    
    public PeerStream Stream() {
        return mStream;
    }
    
    public MediaDispatcher MediaDispatcher() {
        return mDispatcher;
    }

    protected PeerPTZ GetPTZ(int index) {
        synchronized (this) {
            if (mPTZIndex != index) {
                mPTZIndex = index;
                mPTZ = mPeer.Channels()[index].Video().PTZ();
            }
        }
        return mPTZ; 
    }
    
    protected PeerRecordList CreateRecordList() {
        synchronized (this) {
            PeerRecordList value = new PeerRecordList();
            mPeer.CreateRecordList(value);
            return value;
        }
    }
    
    public int getVideoMask() {
        return mVideoMask;
    }
    
    public int getAudilMask() {
        return mAudioMask;
    }
    
    public void ResumeStreamStart() {
        synchronized (this) {
            Message msg = Message.obtain();
            msg.what = EventAgent.Stream_Create_Then_Start;
            msg.arg1 = mVideoMask;
            msg.arg2 = mVideoMask;
            
            mEventAgent.RemoveMessage(msg.what);
            mEventAgent.SendMessage(msg);
        }
    }
    
    public void CreateStreamStart(int videoMask, int audioMask) {
        synchronized (this) {
            mVideoMask = videoMask;
            mAudioMask = audioMask;
            
            Message msg = Message.obtain();
            msg.what = EventAgent.Stream_Create_Then_Start;
            msg.arg1 = videoMask;
            msg.arg2 = audioMask;
            
            mEventAgent.RemoveMessage(msg.what);
            mEventAgent.SendMessage(msg);
        }
    }

    public void CreateStreamStart(int quality) {
        synchronized (this) {
            mQuality = quality;
        
            Message msg = Message.obtain();
            msg.what = EventAgent.Stream_Create_Then_Start;
            msg.arg1 = quality;
            
            mEventAgent.RemoveMessage(msg.what);
            mEventAgent.SendMessage(msg);
        }
    }

    public void ResumeRecordStream() {
        synchronized (this) {
            Message msg = Message.obtain();
            msg.what = EventAgent.RecordStream_Create_Then_Start;
            msg.arg1 = mRecordVideoMask;
            msg.arg2 = mRecordAudioMask;
            msg.obj = new Date[] { mRecordStart, mRecordEnd };
            
            mEventAgent.RemoveMessage(msg.what);
            mEventAgent.SendMessage(msg);
        }
    }
    
    public void CreateRecordStreamStart(int videoMask, int audioMask, Date start) {
        synchronized (this) {
            mRecordVideoMask = videoMask;
            mRecordAudioMask = audioMask;
            mRecordStart = start;
            mRecordEnd = null;
            
            Message msg = Message.obtain();
            msg.what = EventAgent.RecordStream_Create_Then_Start;
            msg.arg1 = videoMask;
            msg.arg2 = audioMask;
            msg.obj = new Date[] { start, null };
            
            mEventAgent.RemoveMessage(msg.what);
            mEventAgent.SendMessage(msg);
        }
    }

    public void CreateRecordStreamStart(int videoMask, int audioMask, Date start, Date end) {
        synchronized (this) {
            mRecordVideoMask = videoMask;
            mRecordAudioMask = audioMask;
            mRecordStart = start;
            mRecordEnd = end;
            
            Message msg = Message.obtain();
            msg.what = EventAgent.RecordStream_Create_Then_Start;
            msg.arg1 = videoMask;
            msg.arg2 = audioMask;
            msg.obj = new Date[] { start, end };
            
            mEventAgent.RemoveMessage(msg.what);
            mEventAgent.SendMessage(msg);
        }
    }

    
    /*
    public void StreamStart(int quality) {
        synchronized (this) {
            mQuality = quality;
        }
        Message msg = Message.obtain();
        msg.what = EventAgent.Stream_Start;
        msg.arg1 = mQuality;
        mEventAgent.SendMessage(msg);
    }
    */
    public void StreamStop() {
        mEventAgent.SendMessage(EventAgent.Stream_Stop);
    }
    
    /*
    public void StreamSwitch(int quality) {
        synchronized (this) {
            mQuality = quality;
        }
        Message msg = Message.obtain();
        msg.what = EventAgent.Stream_Switch;
        msg.arg1 = quality;
        mEventAgent.SendMessage(msg);
    }
    */
    
    public void SetChannelMask(int mask) {
        synchronized (this) {
            mVideoMask = mask;
            mAudioMask = mask;
        }
        
        Message msg = Message.obtain();
        msg.what = EventAgent.Stream_Set_ChannelMask;
        msg.arg1 = mVideoMask;
        msg.arg2 = mAudioMask;
        
        mEventAgent.RemoveMessage(msg.what);
        mEventAgent.SendMessage(msg);
    }
    
    public void SetChannelMask(int videoMask, int audioMask) {
        synchronized (this) {
            mVideoMask = videoMask;
            mAudioMask = audioMask;
        }
        
        Message msg = Message.obtain();
        msg.what = EventAgent.Stream_Set_ChannelMask;
        msg.arg1 = videoMask;
        msg.arg2 = audioMask;
        
        mEventAgent.RemoveMessage(msg.what);
        mEventAgent.SendMessage(msg);
    }
    
    public void SetVideoChannlMask(int videoMask) {
        synchronized (this) {
            mVideoMask = videoMask;
        }
        
        Message msg = Message.obtain();
        msg.what = EventAgent.Stream_Set_ChannelMask;
        msg.arg1 = mVideoMask;
        msg.arg2 = mAudioMask;
        
        mEventAgent.RemoveMessage(msg.what);
        mEventAgent.SendMessage(msg);
    }
    
    public void SetAudioChannelMask(int audioMask) {
        synchronized (this) {
            mAudioMask = audioMask;
        }
        
        Message msg = Message.obtain();
        msg.what = EventAgent.Stream_Set_ChannelMask;
        msg.arg1 = mVideoMask;
        msg.arg2 = mAudioMask;
        
        mEventAgent.RemoveMessage(msg.what);
        mEventAgent.SendMessage(msg);
    }

    public void Connect(boolean reconnect, ConnectInfo info) {
        synchronized (this) {
            mInfo = info;
        }
        mEventAgent.SendMessage(EventAgent.Peer_Connect, info);
    }
    
    public void GetIntialConfig(String token) {
        Message msg = Message.obtain();
        msg.what = EventAgent.Peer_Request_IntialConfig;
        msg.obj = token;
        mEventAgent.SendMessage(msg);
    }

    public void GetChannel(int index) {
        mEventAgent.SendMessage(EventAgent.Peer_Request_GetChannel);
    }
    
    public void GetChannels() {
        mEventAgent.SendMessage(EventAgent.Peer_Request_GetChannels);
    }
    
    public void RelaySwitch(int index, int pole) {
        Message msg = Message.obtain();
        msg.what = EventAgent.Relay_Switch;
        msg.arg1 = index;
        msg.arg2 = pole;
        mEventAgent.SendMessage(msg);
    }
    
    public void RelayActivate(int index, int seconds) {
        Message msg = Message.obtain();
        msg.what = EventAgent.Relay_Activate;
        msg.arg1 = index;
        msg.arg2 = index;
        mEventAgent.SendMessage(msg);
    }

    public void PTZMove(int index, PointF arg) {
        Message msg = Message.obtain();
        msg.what = EventAgent.PTZ_Move;
        msg.arg1 = index;
        msg.obj = arg;
        mEventAgent.SendMessage(msg);
    }

    public void PTZZoomIn(int index) {
        Message msg = Message.obtain();
        msg.what = EventAgent.PTZ_ZoomIn;
        msg.arg1 = index;
        mEventAgent.SendMessage(msg);
    }
    
    public void PTZZoomOut(int index) {
        Message msg = Message.obtain();
        msg.what = EventAgent.PTZ_ZoomOut;
        msg.arg1 = index;
        mEventAgent.SendMessage(msg);
    }
    
    public void PTZIrisOpen(int index) {
        Message msg = Message.obtain();
        msg.what = EventAgent.PTZ_IrisOpen;
        msg.arg1 = index;
        mEventAgent.SendMessage(msg);
    }
    
    public void PTZIrisClose(int index) {
        Message msg = Message.obtain();
        msg.what = EventAgent.PTZ_IrisClose;
        msg.arg1 = index;
        mEventAgent.SendMessage(msg);
    }
    
    public void PTZFocusIn(int index) {
        Message msg = Message.obtain();
        msg.what = EventAgent.PTZ_FocusIn;
        msg.arg1 = index;
        mEventAgent.SendMessage(msg);
    }
    
    public void PTZFocusOut(int index) {
        Message msg = Message.obtain();
        msg.what = EventAgent.PTZ_FocusOut;
        msg.arg1 = index;
        mEventAgent.SendMessage(msg);
    }
    
    public void PTZGoPreset(int index, int arg) {
        Message msg = Message.obtain();
        msg.what = EventAgent.PTZ_Go_Preset;
        msg.arg1 = index;
        msg.arg2 = arg;
        mEventAgent.SendMessage(msg);
    }
    
    public void PTZSetPreset(int index, int arg) {
        Message msg = Message.obtain();
        msg.what = EventAgent.PTZ_Set_Preset;
        msg.arg1 = index;
        msg.arg2 = arg;
        mEventAgent.SendMessage(msg);
    }
    
    public void PTZStop(int index) {
        Message msg = Message.obtain();
        msg.what = EventAgent.PTZ_Stop;
        msg.arg1 = index;
        mEventAgent.SendMessage(msg);
    }
    
    public void PTZGetAvailablePreset(int index) {
        Message msg = Message.obtain();
        msg.what = EventAgent.PTZ_GetAvailablePreset;
        msg.arg1 = index;
        mEventAgent.SendMessage(msg);
    }
    
    public void GetRecordList() {
        Message msg = Message.obtain();
        msg.what = EventAgent.RecordList_CreateRecordList;
        mEventAgent.SendMessage(msg);
    }
    
    public void GetRecordedMinutesOfDay(int year, int month, int day) {
        Message msg = Message.obtain();
        msg.what = EventAgent.RecordList_GetRecordMinutesOfDay;
        msg.obj = new int[] { year, month, day };
        mEventAgent.SendMessage(msg);
    }
    
    public void GetRecordedDaysOfMonth(int year, int month) {
        Message msg = Message.obtain();
        msg.what = EventAgent.RecordList_GetRecordDaysOfMonth;
        msg.arg1 = year;
        msg.arg2 = month;
        mEventAgent.SendMessage(msg);
    }
    ///////////////////////////////////////

}
