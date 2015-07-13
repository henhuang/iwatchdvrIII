package refactor.remote.iWatchDVR;

import peersdk.TimeRange;
import peersdk.TimeSpan;
import peersdk.peer.PeerLog;
import peersdk.peer.event.ErrorOccurredEventArgs;
import peersdk.peer.event.VideoLossChangedEventArgs;
import peersdk.peer.event.VideoMotionChangedEventArgs;
import refactor.remote.iWatchDVR.application.DVRListener;
import refactor.remote.iWatchDVR.application.RemoteDVRApplication;
import refactor.remote.iWatchDVR.dvr.args.Channel;
import refactor.remote.iWatchDVR.dvr.args.Relay;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;

public class BaseDVRActivity extends ActionBarActivity implements DVRListener {

    Handler mHandler;
    RemoteDVRApplication mApplication;
    
    final static int Msg_Peer_VideoMotionChanged = 0xa1;
    final static int Msg_Peer_VideoLossChanged = 0xa2;
    final static int Msg_Peer_ErrorOccurred = 0xa3;
    final static int Msg_Peer_IsSupportedPushNotification = 0xa6;
    final static int Msg_Peer_HasRelayPermission = 0xa7;
    final static int Msg_Peer_GetChannel = 0xa8;
    final static int Msg_Peer_GetChannelAll = 0xa9;
    final static int Msg_Stream_VideoFirstFrameArrived = 0xaa;
    final static int Msg_Stream_OnStreamStartConnect = 0xab;
    final static int Msg_Stream_StreamErrorOccurred = 0xac;
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApplication = (RemoteDVRApplication)getApplication();
    }
    
    protected void HandleMessage(Message msg) {
        
    }
    
    protected void InitUIHandler() {
        // Defines a Handler object that's attached to the UI thread
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                synchronized (this) {
                    HandleMessage(msg);
                }
            }
        };
    }

    protected void UIHandlerQuit() {
        if (mHandler != null)
            mHandler.getLooper().quit();
    }

    protected void SendMessage2UI(int what) {
        mHandler.sendEmptyMessage(what);
    }
    
    protected void SendMessage2UI(int what, Object arg) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = arg;
        mHandler.sendMessage(msg);
    }
    
    protected void SendMessage2UI(int what, int arg) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.arg1 = arg;
        mHandler.sendMessage(msg);
    }
    
    protected void SendMessage2UI(int what, int arg1, int arg2) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        mHandler.sendMessage(msg);
    }
    
    protected void PopupWaitingDialog() {
        new WaitingDialog().show(getSupportFragmentManager(), WaitingDialog.class.getName());
    }
    
    protected void DismissWaitingDialog() {
        DialogFragment dialog = (DialogFragment)getSupportFragmentManager().findFragmentByTag(WaitingDialog.class.getName());
        if (dialog != null)
            dialog.dismiss();
    }
   
    
    @Override
    public void OnConnected(String host) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void OnDisconnected(String host) {
        // TODO Auto-generated method stub
    }

    @Override
    public void OnVideoLossChanged(VideoLossChangedEventArgs arg) {
        // TODO Auto-generated method stub
        
    }

    
    @Override
    public void OnVideoMotionChanged(VideoMotionChangedEventArgs arg) {
        // TODO Auto-generated method stub
    }

    @Override
    public void OnErrorOccurred(ErrorOccurredEventArgs arg) {
        // TODO Auto-generated method stub
    }

    protected void HandleMessage() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void OnGetChannel(Channel arg) {
        // TODO Auto-generated method stub
    }

    @Override
    public void OnGetChannels(Channel[] arg) {
        // TODO Auto-generated method stub
    }

    @Override
	public void GetLogs(PeerLog[] arg) {
		// TODO Auto-generated method stub
		
	}
    
    @Override
    public void OnGetInitialConfig(Channel[] channels,
            boolean hasRelayPermission, Relay[] relays, boolean notifySupported,
            boolean notigyEnabled) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void OnGetPTZAvailablePreset(int index, int[] arg) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void OnGetRecordList(TimeRange[] arg) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void OnGetRecordedMinutesOfDay(TimeSpan[] arg) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void OnGetRecordedDaysOfMonth(int[] arg) {
        // TODO Auto-generated method stub
        
    }
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    
    @Override
    public void OnVideoFirstFrameArrived(int channel) {
        // TODO Auto-generated method stub
    }

    @Override
    public void OnStreamStartConnect(int channel) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void OnStreamErrorOccurred(ErrorOccurredEventArgs arg) {
        // TODO Auto-generated method stub
        
    }

}
