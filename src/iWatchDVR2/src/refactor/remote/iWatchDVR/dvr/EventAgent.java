package refactor.remote.iWatchDVR.dvr;

import java.util.Date;

import peersdk.TimeRange;
import peersdk.TimeSpan;
import peersdk.media.MediaDispatcher;
import peersdk.peer.Peer;
import peersdk.peer.PeerChannel;
import peersdk.peer.PeerLog;
import peersdk.peer.PeerRecordList;
import peersdk.peer.PeerRelay;
import peersdk.peer.PeerStream;
import peersdk.peer.PeerStream.StreamConntectStatus;
import peersdk.peer.event.*;
import refactor.remote.iWatchDVR.application.Utility;
import refactor.remote.iWatchDVR.dvr.args.Channel;
import refactor.remote.iWatchDVR.dvr.args.Relay;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class EventAgent extends Thread {
    
    final String TAG = "__PeerDVR EventAgent__";
    PeerDVR mDVR;
    Handler mHandler;
    protected boolean mDisposing = false;
    protected boolean mDisposed = false;
    
    PeerRecordList mRecordList;
    
    public final static int Peer_Connect              = 0x30;
    public final static int Peer_Disconnect           = 0x31;

    public final static int Peer_Request_GetChannel   = 0x32;
    public final static int Peer_Request_GetChannels  = 0x33;
    public final static int Peer_Request_IntialConfig = 0x34;
    public final static int Peer_Request_Logs         = 0x35;
    
    public final static int DVR_Dispose               = 0x40; // dispose peer and stream
    
    public final static int Stream_Start              = 0x50;
    public final static int Stream_Create_Then_Start  = 0x51;
    public final static int Stream_Set_ChannelMask    = 0x52;
    public final static int Stream_Stop               = 0x54;
    public final static int Stream_Switch             = 0x55;
    public final static int RecordStream_Create_Then_Start = 0x56;
    
    public final static int Relay_Switch              = 0x60;
    public final static int Relay_Activate            = 0x61;
    
    public final static int PTZ_Move                  = 0x70;
    public final static int PTZ_ZoomIn                = 0x71;
    public final static int PTZ_ZoomOut               = 0x72;
    public final static int PTZ_IrisOpen              = 0x73;
    public final static int PTZ_IrisClose             = 0x74;
    public final static int PTZ_FocusIn               = 0x75;
    public final static int PTZ_FocusOut              = 0x76;
    public final static int PTZ_Go_Preset             = 0x77;
    public final static int PTZ_Set_Preset            = 0x78;
    public final static int PTZ_Stop                  = 0x79;
    public final static int PTZ_GetAvailablePreset    = 0x7a;  
    
    public final static int RecordList_CreateRecordList = 0x81;
    public final static int RecordList_GetRecordMinutesOfDay = 0x82;
    public final static int RecordList_GetRecordDaysOfMonth = 0x83;

    

    EventAgent(PeerDVR dvr) {
        mDVR = dvr;
    }
 
    
    @Override
    public void run() {

        Looper.prepare();
        
        synchronized (this) {
            mHandler = new Handler() {
                
                @Override
                public void handleMessage(Message msg) {

                    switch(msg.what) {
                    case Peer_Connect:
                        boolean result = _Connect((ConnectInfo) msg.obj);
                        Log.i(TAG, "Peer_Connect :" + mDVR.mInfo.host + "  " + new String(result ? "^_^" : "=_="));
                        if (result) {
                            int channelCount = mDVR.mPeer.Channels().length;
                            mDVR.mDispatcher = new MediaDispatcher(channelCount, Utility.GetAvailableProcessor());
                            mDVR.mListener.OnConnected(mDVR.mInfo.host, result); // callback
                        }
                        
                        break;
                    case Peer_Request_IntialConfig:
                        Log.i(TAG, "Peer_Request_IntialConfig");
                        _Get_InitialConfig((String)msg.obj);
                        break;
                        
                    case Peer_Request_GetChannel:
                        Log.i(TAG, "Peer_Request_GetChannel:" + msg.arg1);
                        _GetChannel(msg.arg1);
                        break;
                        
                    case Peer_Request_GetChannels:
                        Log.i(TAG, "Peer_Request_GetChannels");
                        _GetChannels();
                        break;
                        
                    case Peer_Request_Logs:
                    	Log.i(TAG, "Peer_Request_Logs");
                    	_GetLogs();
                        
                    case Peer_Disconnect:
                        Log.i(TAG, "Peer_Disconnect: " + mDVR.mInfo.host);
                        break;
                        
                    case DVR_Dispose:
                        Log.i(TAG, "DVR_Dispose: " + mDVR.mInfo.host);
                        Stop();
                        _Dispose();
                        Log.i(TAG, "DVR_Dispose:" + mDVR.mInfo.host + " done");
                        break;
                        
                    case Stream_Start:
                        Log.i(TAG, "Stream_Start: " + mDVR.mInfo.host + (msg.arg1 == 0 ? ", HQ" : ", LQ"));
                        //_StreamStart(msg.arg1);
                        break;
                        
                    case Stream_Create_Then_Start:
                        Log.i(TAG, "Stream_Create_Then_Start: " + mDVR.mInfo.host + " v=" + msg.arg1 + ", a=" + msg.arg2);
                        _StreamCreateStart(msg.arg1, msg.arg2);
                        break;
                        
                    case Stream_Stop:
                        Log.i(TAG, "Stream_Stop: "  + mDVR.mInfo.host);
                        _StreamStop();
                        break;
                        
                    case Stream_Switch:
                        Log.i(TAG, "Stream_Switch: " + mDVR.mInfo.host + (msg.arg1 == 0 ? ", HQ" : ", LQ"));
                        //_StreamStart(msg.arg1);
                        break;

                    case Stream_Set_ChannelMask:
                        Log.i(TAG, "Stream_Set_ChannelMask: " + msg.arg1 + ", " + msg.arg2);
                        _Set_ChannelMask(msg.arg1, msg.arg2);
                        break;
                        
                    case RecordStream_Create_Then_Start:
                        Log.i(TAG, "RecordStream_Create_Then_Start");
                        Date[] time = (Date[])msg.obj;
                        _RecordStream_Create_Then_Start(msg.arg1, msg.arg2, time[0], time[1]);
                        break;
                        
                    //case RecordStream_Create_Then_Start:
                        //Log.i(TAG, "Stream_Set_ChannelMask");
                        //break;
                        
                    case Relay_Switch:
                        Log.i(TAG, "Relay_Switch: " + msg.arg1 + ", " + msg.arg2);
                        _Relay_Switch(msg.arg1, msg.arg2);
                        break;
                        
                    case Relay_Activate:
                        Log.i(TAG, "Relay_Activate: " + msg.arg1 + ", " + msg.arg2);
                        _Relay_Activate(msg.arg1, msg.arg2);
                        break;
                        
                    case PTZ_Move:
                        Log.i(TAG, "PTZ_Move");
                        _PTZ_Move(msg.arg1, (PointF)msg.obj);
                        break;
                        
                    case PTZ_ZoomIn:
                        Log.i(TAG, "PTZ_ZoomIn");
                        _PTZ_ZoomIn(msg.arg1, 1f);
                        break;
                        
                    case PTZ_ZoomOut:
                        Log.i(TAG, "PTZ_ZoomOut");
                        _PTZ_ZoomOut(msg.arg1, -1f);
                        break;
                        
                    case PTZ_IrisOpen:
                        Log.i(TAG, "PTZ_IrisOpen");
                        _PTZ_IrisOpen(msg.arg1, -1f);
                        break;
                        
                    case PTZ_IrisClose:
                        Log.i(TAG, "PTZ_IrisClose");
                        _PTZ_IrisClose(msg.arg1, 1f);
                        break;
                        
                    case PTZ_FocusIn:
                        Log.i(TAG, "PTZ_FocusIn");
                        _PTZ_FocusIn(msg.arg1, -1f);
                        break;
                        
                    case PTZ_FocusOut:
                        Log.i(TAG, "PTZ_FocusOut");
                        _PTZ_FocusOut(msg.arg1, 1f);
                        break;
                        
                    case PTZ_Go_Preset:
                        Log.i(TAG, "PTZ_Go_Preset");
                        _PTZ_Go_Preset(msg.arg1, msg.arg2);
                        break;
                        
                    case PTZ_Set_Preset:
                        Log.i(TAG, "PTZ_Set_Preset");
                        _PTZ_Set_Preset(msg.arg1, msg.arg2);
                        break;
                        
                    case PTZ_Stop:
                        Log.i(TAG, "PTZ_Stop");
                        _PTZ_Stop(msg.arg1);
                        break;
                        
                    case PTZ_GetAvailablePreset:
                        Log.i(TAG, "PTZ_GetAvailablePreset: " + msg.arg1);
                        _PTZ_GetAvailablePreset(msg.arg1);
                        break;
                        
                    case RecordList_CreateRecordList:
                        Log.i(TAG, "_RecordList_CreateRecordList");
                        _RecordList_CreateRecordList();
                        break;
                        
                    case RecordList_GetRecordMinutesOfDay:
                        Log.i(TAG, "RecordList_GetRecordMinutesOfDay");
                        int[] arg = (int[])msg.obj;
                        _RecordList_GetRecordedMinutesOfDay(arg[0], arg[1], arg[2]);
                        break;
                        
                    case RecordList_GetRecordDaysOfMonth:
                        Log.i(TAG, "RecordList_GetRecordDaysOfMonth");
                        _RecordList_GetRecordedDaysOfMonth(msg.arg1, msg.arg2);
                        break;
                        
                    default:
                        Log.i(TAG, "Unsupported message: " + mDVR.mInfo.host);
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
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    protected void Stop() {
        mHandler.getLooper().quit(); // stop handling coming messages
    }
    
    public void RemoveMessage(int what) {
        mHandler.removeMessages(what);
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
    
    //========================================
    
    protected boolean _Connect(ConnectInfo info) {
        boolean result = mDVR.mPeer.Connect(info.host, info.port, info.user, info.pwd, "", true);

        mDVR.setStatus(result ? ConnectStatus.Connected : ConnectStatus.Disconnected);
        
        mDVR.mPeer.SetOnErrorOccurredListener(new Peer.ErrorOccurredListener() {
            
            @Override
            public void OnErrorOccurred(Peer peer, ErrorOccurredEventArgs arg) {
                Log.i(TAG, "OnErrorOccurred");
                mDVR.setStatus(ConnectStatus.Disconnected);
                mDVR.mListener.OnErrorOccurred(arg);
            }
        });
        
        mDVR.mPeer.SetOnVideoLossChangedListener(new Peer.VideoLossChangedListener() {
            
            @Override
            public void OnVideoLossChanged(Peer peer, VideoLossChangedEventArgs arg) {
                Log.i(TAG, "OnVideoLossChanged");
                mDVR.mListener.OnVideoLossChanged(arg);
            }
        });
        
        mDVR.mPeer.SetOnVideoMotionChangedListener(new Peer.VideoMotionChangedListener() {
            
            @Override
            public void OnVideoMotionChanged(Peer peer, VideoMotionChangedEventArgs arg) {
                Log.i(TAG, "OnVideoMotionChanged");
                mDVR.mListener.OnVideoMotionChanged(arg);
            }
        });

        return result;
    }

    protected void _Disconnect() {
        synchronized (this) {
            mDVR.mPeer.Dispose();
            mDVR.mStatus = ConnectStatus.Disconnected;
        }
    }
    
    protected void _Dispose() {
        synchronized (this) {
            if (mDVR.mStream != null)
                mDVR.mStream.Dispose();
            if (mDVR.mDispatcher != null)
                mDVR.mDispatcher.Dispose();
            if (mDVR.mPeer != null)
                mDVR.mPeer.Dispose();
            mDVR.mStatus = ConnectStatus.Disconnected;
        }    
    }
    
    protected boolean _StreamCreateStart(int videoMask, int audioMask) {
        synchronized (this) {
            
            if (mDVR.mStream != null) {
                mDVR.mStream.Dispose();
            }

            mDVR.mStream = PeerStream.CreateLive(mDVR.mPeer);
            if (mDVR.mStream == null) {
                Log.e(TAG, "PeerStream.CreateLive error");
                return false;
            }
            
            mDVR.mStream.SetChannelMask(videoMask, audioMask);
            
            mDVR.mStream.SetVideoArrivedListener(new PeerStream.VideoArrivedListener() {

                @Override
                public void OnVideoArrived(PeerStream stream,
                        VideoArrivedEventArgs arg) {
 
                    if (!stream.firstFrameGot) {
                        stream.firstFrameGot = true;
                        stream.connectStatus = StreamConntectStatus.Streaming;
                        ((PeerStreamListener)mDVR.mListener).OnVideoFirstFrameArrived(0xffffffff);
                    }

                    mDVR.mDispatcher.AddVideo(0, arg.type, arg.channel, arg.bufferLength, arg.nativeBufferPointer, 
                            arg.pts, arg.width, arg.height, arg.speed, arg.time.getTime());
                }
                
            });

            mDVR.mStream.SetErrorOccurredListener(new PeerStream.ErrorOccurredListener(){

                @Override
                public void OnErrorOccurred(PeerStream stream,
                        ErrorOccurredEventArgs arg) {
                    Log.i(TAG, "stream OnErrorOccurred");
                }
            
            });
        }

        ((PeerStreamListener)mDVR.mListener).OnStreamStartConnect(videoMask);
        mDVR.mStream.connectStatus = StreamConntectStatus.Prepare;
        boolean result = mDVR.mStream.Start();
        if (!result) {
            mDVR.mStream.connectStatus = StreamConntectStatus.Error;
            ((PeerStreamListener)mDVR.mListener).OnStreamErrorOccurred(new ErrorOccurredEventArgs(false, "stream start error"));
        }
        Log.i(TAG, "stream start=" + (result ? " ^_^" : " -_-"));
            
        return true;
    }
    
    protected boolean _StreamStart(int quality) {
        /*
        synchronized (this) {
            
            if (mDVR.mStream != null) {
                mDVR.mStream.Dispose();
            }

            mDVR.mStream = PeerStream.CreateLive(mDVR.mPeer);
            if (mDVR.mStream == null) {
                Log.e(TAG, "PeerStream.CreateLive error");
                return false;
            }
            
            
            mDVR.mStream.SetVideoArrivedListener(new PeerStream.VideoArrivedListener() {

                @Override
                public void OnVideoArrived(PeerStream stream,
                        VideoArrivedEventArgs arg) {
                    
                    if (!stream.firstFrameGot) {
                        stream.firstFrameGot = true;
                        // TODO: inform UI
                    }

                    mDVR.mDispatcher.AddVideo(0, arg.type, arg.channel, arg.bufferLength, arg.nativeBufferPointer, 
                            arg.pts, arg.width, arg.height, arg.speed, arg.time.getTime());
                }
                
            });

            mDVR.mStream.SetErrorOccurredListener(new PeerStream.ErrorOccurredListener(){

                @Override
                public void OnErrorOccurred(PeerStream stream,
                        ErrorOccurredEventArgs arg) {
                    // TODO Auto-generated method stub
                    
                }
            
            });
        }

        mDVR.mStream.Start();
        */
        return true;
    }
    
    protected void _StreamStop() {
        synchronized (this) {
            if (mDVR.mStream == null)
                return;
            mDVR.mStream.Dispose();
            mDVR.mStream = null;
        }
    }
    
    protected void _Set_ChannelMask(int videoMask, int audioMask) {
        synchronized (this) {
            if (mDVR.mStream == null)
                return;
            mDVR.mStream.SetChannelMask(videoMask, audioMask);
        }
    }
    
    protected boolean _RecordStream_Create_Then_Start(int videoMask, int audioMask, Date start, Date end) {
        synchronized (this) {
            if (mDVR.mStream != null) {
                mDVR.mStream.Dispose();
            }

            if (end == null)
                mDVR.mStream = PeerStream.CreateRecord(mDVR.mPeer, start);
            else
                mDVR.mStream = PeerStream.CreateRecord(mDVR.mPeer, start, end);
            
            if (mDVR.mStream == null) {
                Log.e(TAG, "PeerStream.CreateRecord error");
                return false;
            }
            
            mDVR.mStream.SetChannelMask(videoMask, audioMask);
            
            mDVR.mStream.SetVideoArrivedListener(new PeerStream.VideoArrivedListener() {

                @Override
                public void OnVideoArrived(PeerStream stream,
                        VideoArrivedEventArgs arg) {
 
                    if (!stream.firstFrameGot) {
                        stream.firstFrameGot = true;
                        stream.connectStatus = StreamConntectStatus.Streaming;
                        ((PeerStreamListener)mDVR.mListener).OnVideoFirstFrameArrived(0xffffffff);
                    }

                    mDVR.mDispatcher.AddVideo(0, arg.type, arg.channel, arg.bufferLength, arg.nativeBufferPointer, 
                            arg.pts, arg.width, arg.height, arg.speed, arg.time.getTime());
                }
                
            });

            mDVR.mStream.SetErrorOccurredListener(new PeerStream.ErrorOccurredListener(){

                @Override
                public void OnErrorOccurred(PeerStream stream,
                        ErrorOccurredEventArgs arg) {
                    Log.w(TAG, "RecordStream OnErrorOccurred");
                }
            
            });
        }
        return true;
    }
    
    protected void _GetChannel(int index) {
        synchronized (this) {
            PeerChannel obj = mDVR.mPeer.Channels()[index];
            Channel arg = new Channel();
            arg.id = obj.Index();
            arg.name = obj.Name();
            arg.ptzEnabled = obj.Video().PTZ().Enabled();
            arg.audioPresented = obj.Audio().IsPresent();
            arg.videoFormat = obj.Video().Format();
            arg.videoIsLoss = obj.Video().IsLoss();
            arg.videoIsMotion = obj.Video().IsMotion();
            mDVR.mListener.GetChannel(arg);

        }
    }
    
    protected void _GetChannels() {
        synchronized (this) {
            PeerChannel[] obj = mDVR.mPeer.Channels();
            Channel[] chs = new Channel[obj.length];
            int i = 0;
            for (PeerChannel o : obj) {
                chs[i] = new Channel();
                chs[i].id = o.Index();
                chs[i].name = o.Name();
                chs[i].ptzEnabled = o.Video().PTZ().Enabled();
                chs[i].audioPresented = o.Audio().IsPresent();
                chs[i].videoFormat = o.Video().Format();
                chs[i].videoIsLoss = o.Video().IsLoss();
                chs[i].videoIsMotion = o.Video().IsMotion();
                i++;
            }
            
            mDVR.mListener.GetChannels(chs);
        }
    }

    protected void _Get_InitialConfig(String token) {
        synchronized (this) {
            
            boolean hasRelayPermission = false;//mDVR.mPeer.
            boolean notifySupported = mDVR.mPeer.IsSupportedPushNotification();
            boolean notifyEnabled = notifySupported ? mDVR.mPeer.IsPushNotificationRegistered(token) : false;
            
            PeerChannel[] obj = mDVR.mPeer.Channels();
            
            synchronized (mDVR) {
                mDVR.mChannel = new Channel[obj.length];
                int i = 0;
                for (PeerChannel o : obj) {
                    mDVR.mChannel[i] = new Channel();
                    mDVR.mChannel[i].id = o.Index();
                    mDVR.mChannel[i].name = o.Name();
                    mDVR.mChannel[i].ptzEnabled = o.Video().PTZ().Enabled();
                    mDVR.mChannel[i].audioPresented = o.Audio().IsPresent();
                    mDVR.mChannel[i].videoFormat = o.Video().Format();
                    mDVR.mChannel[i].videoIsLoss = o.Video().IsLoss();
                    mDVR.mChannel[i].videoIsMotion = o.Video().IsMotion();
                    i++;
                }
            }

            PeerRelay[] relays = mDVR.mPeer.Relays();
            Relay[] rs = new Relay[relays.length];
            int i = 0;
            for (PeerRelay o : relays) {
                rs[i] = new Relay();
                rs[i].pole = o.Pole();
            }

            mDVR.mListener.GetInitialConfig(mDVR.mChannel, hasRelayPermission, rs, notifySupported, notifyEnabled);
        }
    }

    protected void _GetLogs() {
    	mDVR.mListener.GetLogs(mDVR.mPeer.GetLogList());
    }
    
    protected void _Relay_Switch(int index, int pole) {
        mDVR.mPeer.Relays()[index].Switch(pole);
    }
    
    protected void _Relay_Activate(int index, int seconds) {
        mDVR.mPeer.Relays()[index].Activate(seconds);
    }

    protected void _PTZ_Move(int index, PointF arg) {
        mDVR.GetPTZ(index).Move((double) arg.x, (double) arg.y, false);
    }
    
    protected void _PTZ_ZoomIn(int index, float arg) {
        mDVR.GetPTZ(index).DoZoom(1.0, false);
    }
    
    protected void _PTZ_ZoomOut(int index, float arg) {
        mDVR.GetPTZ(index).DoZoom(arg, false);
    }
    
    protected void _PTZ_IrisOpen(int index, float arg) {
        mDVR.GetPTZ(index).DoIris(arg, false);
    }
    
    protected void _PTZ_IrisClose(int index, float arg) {
        mDVR.GetPTZ(index).DoIris(arg, false);
    }
    
    protected void _PTZ_FocusIn(int index, float arg) {
        mDVR.GetPTZ(index).DoFocus(-1.0, false);
    }
    
    protected void _PTZ_FocusOut(int index, float arg) {
        mDVR.GetPTZ(index).DoFocus(arg, false);
    }
    
    protected void _PTZ_Go_Preset(int index, int arg) {
        mDVR.GetPTZ(index).GoPreset(arg);
    }
    
    protected void _PTZ_Set_Preset(int index, int arg) {
        mDVR.GetPTZ(index).SetPreset(arg);
    }
    
    protected void _PTZ_Stop(int index) {
        mDVR.GetPTZ(index).Stop();
    }
    
    protected void _PTZ_GetAvailablePreset(int index) {
        mDVR.mListener.GetPTZAvailablePreset(index, mDVR.GetPTZ(index).GetAvailablePreset());
    }
    
    protected void _RecordList_CreateRecordList() {
        mRecordList = mDVR.CreateRecordList();
        TimeRange[] arg = mRecordList.GetList();
        mDVR.mListener.GetRecordList(arg);
    }
    
    protected void _RecordList_GetRecordedMinutesOfDay(int year, int month, int day) {
        if (mRecordList == null)
            mRecordList = mDVR.CreateRecordList();
        mDVR.mListener.GetRecordedMinutesOfDay(mRecordList.GetRecordedMinutesOfDay(year, month, day));
            
    }
    
    protected void _RecordList_GetRecordedDaysOfMonth(int year, int month) {
        if (mRecordList == null)
            mRecordList = mDVR.CreateRecordList();
        mDVR.mListener.GetRecordedDaysOfMonth(mRecordList.GetRecordedDaysOfMonth(year, month));
    }
}