package refactor.remote.iWatchDVR;

import java.util.ArrayList;

import peersdk.peer.event.ErrorOccurredEventArgs;
import peersdk.peer.event.VideoLossChangedEventArgs;
import peersdk.peer.event.VideoMotionChangedEventArgs;
import refactor.remote.iWatchDVR.application.DVRListener;
import refactor.remote.iWatchDVR.application.RemoteDVRApplication;
import refactor.remote.iWatchDVR.dvr.ConnectInfo;
import refactor.remote.iWatchDVR.dvr.PeerDVRListener;
import refactor.remote.iWatchDVR.dvr.PeerDVR;
import refactor.remote.iWatchDVR.dvr.args.Channel;
import refactor.remote.iWatchDVR.dvr.args.Relay;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;


public class FunctionActivity extends BaseDVRActivity {

    public static final String TAG = "__FunctionActivity__";

    boolean mDualPane = false;
    boolean mInitDone = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_function_slider);

        InitUIHandler();
        /*
        View detailFrame = findViewById(R.id.detail);       
        mDualPane = detailFrame != null
                && detailFrame.getVisibility() == View.VISIBLE;
        */
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.pager, Fragment.instantiate(this, FunctionPagerFragment.class.getName()));
        ft.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((RemoteDVRApplication)getApplication()).AddPeerListener(this, FunctionActivity.class.getName()); 
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        ((RemoteDVRApplication)getApplication()).RemovePeerListener(FunctionActivity.class.getName()); 
    }
    
    @Override
    protected void HandleMessage(Message msg) {
        switch (msg.what) {
        case Msg_Connecting:
            Log.i(TAG, "Msg_Connecting");
            break;
        case Msg_Disconnected:
            Log.i(TAG, "Msg_Disconnected");
            MessageDialogFragment dialog = new MessageDialogFragment(true, false, R.string.error, R.string.connetFailed);
            dialog.show(getSupportFragmentManager(), null);
            break;
        }
    }

    public void AttachProfileNewLayout() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.pager, new ProfileNewFragment());
        ft.addToBackStack(null);
        ft.commit();
    }
    
    public void AttachProfileEditLayout(int id, String name, String host, String port, String user, String password) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.pager, new ProfileEditFragment(id, name, host, port, user, password));
        ft.addToBackStack(null);
        ft.commit();
    }
    
    public void SetConnectingProgressVisible(boolean visible) {
        findViewById(R.id.progressBar).setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }
    
    public void Connect(ConnectInfo info) {
        SetConnectingProgressVisible(true);
        
        RemoteDVRApplication app = (RemoteDVRApplication)getApplication();
        if (app.CreatePeerDVR()) {
            app.AddPeerListener(this, FunctionActivity.class.getName());
            app.dvr().Connect(false, info);
        }
    }

    @Override
    public void OnConnected(String host) {
        Log.i(TAG, "OnConnected:" + host);
        RemoteDVRApplication app = (RemoteDVRApplication)getApplication();
        app.dvr().GetIntialConfig("!_!");
    }

    @Override
    public void OnDisconnected(String host) {
        mHandler.sendEmptyMessage(Msg_Disconnected);
    }

    @Override
    public void OnGetInitialConfig(Channel[] channels, 
            boolean hasRelayPermission, Relay[] relays, boolean notifySupported,
            boolean notifyEnabled) {
        Intent intent = new Intent(getApplication(), RemoteDVRActivity.class);
        
        intent.putExtra(PeerDVR.Connect_Start, true);
        intent.putExtra(PeerDVR.HasRelayPermission, hasRelayPermission);
        intent.putExtra(PeerDVR.NotifySupported, notifySupported);
        intent.putExtra(PeerDVR.NotifyEnabled, notifyEnabled);

        ArrayList<Channel> chs = new ArrayList<Channel>();
        for (Channel obj : channels)
            chs.add(obj);
        
        ArrayList<Relay> rs = new ArrayList<Relay>();
        for (Relay obj : relays)
            rs.add(obj);    

        intent.putExtra(PeerDVR.ID, System.currentTimeMillis());
        intent.putExtra(PeerDVR.Channels, chs);
        intent.putExtra(PeerDVR.Relays, rs);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        //finish();
    }

    final int Msg_Connecting = 0x00;
    final int Msg_Disconnected = 0x01;
}


