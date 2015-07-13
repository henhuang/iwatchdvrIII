package refactor.remote.iWatchDVR;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import peersdk.TimeRange;
import peersdk.peer.event.ErrorOccurredEventArgs;
import peersdk.peer.event.VideoLossChangedEventArgs;
import peersdk.peer.event.VideoMotionChangedEventArgs;
import refactor.remote.iWatchDVR.application.CanvasView;
import refactor.remote.iWatchDVR.application.ViewA;
import refactor.remote.iWatchDVR.application.VisualListener;
import refactor.remote.iWatchDVR.dvr.PeerDVR;
import refactor.remote.iWatchDVR.dvr.args.Channel;
import refactor.remote.iWatchDVR.dvr.args.Relay;
import refactor.remote.iWatchDVR.listener.OnVisualChangedListener;
import refactor.remote.iWatchDVR.ui.panel.LivePanelFragment;
import refactor.remote.iWatchDVR.ui.panel.PTZPanelFragment;
import refactor.remote.iWatchDVR.ui.panel.RelayPanelFragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


public class RemoteDVRActivity extends VisualActivity implements VisualListener {

    public static final String TAG = "__RemoteDVRActivity__";

    private static final int Msg_Pormpt_No_Record = 0xd0;
    private static final int Msg_Goto_TimeSearch = 0xd1;
    
    GLSurfaceView mGLSurfaceView;

    List<String> mPanelStack;
    
    final int Msg_Panel_Init = 0x00;

    Channel[] mChannel;
    Relay[] mRelay;

    long mId;
    boolean mInit = false;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_dvr);

        //////////////////////////////////
        InitUIHandler();

        mPanelStack = new LinkedList<String>();
        
        InitializeCanvasView();

        if (savedInstanceState == null) {
            AttachLivePanel();
        }
        else {
            Log.i(TAG, "savedInstanceState is not null......");
        }
        
        // start TASK
        mApplication.AddPeerListener(this, RemoteDVRActivity.class.getName());
    }

    @Override
    public void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        Log.w(TAG, "onNewIntent .................................");
        setIntent(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        
        Intent intent = getIntent();

        boolean newConnected = false;
        long id = intent.getLongExtra(PeerDVR.ID, -1);
        Log.i(TAG, "new id=" + id);
        if (id > 0 && mId != id) {
            newConnected = true;
            mId = id;
        }
        
        if (newConnected &&intent.getBooleanExtra(PeerDVR.Connect_Start, false)) {
            Log.i(TAG, "PeerDVR.Connect_Start New_____________");
            
            List<Channel> channels = (ArrayList<Channel>) intent.getSerializableExtra(PeerDVR.Channels);
            List<Relay> relays = (ArrayList<Relay>) intent.getSerializableExtra(PeerDVR.Relays);
            synchronized (this) {
                mChannel = new Channel[channels.size()];
                mRelay = new Relay[relays.size()];
            }
            channels.toArray(mChannel);
            relays.toArray(mRelay);


            // to initialize panel
            SendMessage2UI(Msg_Panel_Init, intent);

            // start stream
            mApplication.dvr().CreateStreamStart(1, 0);
        }
        else {
            Log.e(TAG, "onResume: from background -___________________________________-");
            mApplication.dvr().ResumeStreamStart(); // resume stream
        }

    }
    
    @Override
    protected  void onPostResume() {
        super.onPostResume();
    }
    
    @Override
    public void onPause() {
        super.onPause();
        mApplication.dvr().StreamStop();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        mApplication.RemovePeerListener(FunctionActivity.class.getName());
        mApplication.dvr().Dispose();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //TODO: exit program normally
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //setContentView(R.layout.activity_remote_dvr);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {                                                     
        getMenuInflater().inflate(R.menu.activity_remote_dvr, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            if (mPanelStack.size() > 1) {
                PopupPanel();
            }
            break;
            
        case R.id.action_home:
            // TODO:
            Intent intent = new Intent(this, FunctionActivity.class);
            //startActivityForResult(intent, 0x123);
            startActivity(intent);
            break;
        }
        
        
        return (super.onOptionsItemSelected(item));
    }
    
    
    @Override
    protected void HandleMessage(Message msg) {
        switch (msg.what) {
        case Msg_Panel_Init:
            Log.i(TAG, "Msg_Panel_Init");
            Panel_Init((Intent)msg.obj);
            break;
            
        case BaseDVRActivity.Msg_Peer_GetChannel:
            Log.i(TAG, "Msg_Peer_GetChannel");
            //TODO:
            break;
            
        case BaseDVRActivity.Msg_Peer_GetChannelAll:
            Log.i(TAG, "Msg_Peer_GetChannelAll");
            //TODO:
            break;
            
        case BaseDVRActivity.Msg_Peer_VideoMotionChanged:
            Log.i(TAG, "Msg_Peer_VideoMotionChanged");
            Peer_VideoMotionChanged((VideoMotionChangedEventArgs) msg.obj);
            break;
            
        case BaseDVRActivity.Msg_Peer_VideoLossChanged:
            Log.i(TAG, "Msg_Peer_VideoLossChanged");
            Peer_VideoLossChanged((VideoLossChangedEventArgs) msg.obj);
            break;
            
        case BaseDVRActivity.Msg_Stream_VideoFirstFrameArrived:
            Log.i(TAG, "Msg_Stream_VideoFirstFrameArrived: ch" + msg.arg1);
            Stream_VideoFirstFrameArrived(msg.arg1);
            break;
            
        case BaseDVRActivity.Msg_Stream_OnStreamStartConnect:
            Log.i(TAG, "Msg_Stream_OnStreamStartConnect: ch" + msg.arg1);
            Stream_OnStreamStartConnect(msg.arg1);
            break;
            
        case BaseDVRActivity.Msg_Stream_StreamErrorOccurred:
            Log.i(TAG, "Msg_Stream_StreamErrorOccurred" + msg.obj);
            Stream_StreamErrorOccurred((ErrorOccurredEventArgs) msg.obj);
            break;
            
        case Msg_Pormpt_No_Record:
            Log.i(TAG, "Msg_Pormpt_No_Record");
            Pormpt_No_Record();
            break;
            
        case Msg_Goto_TimeSearch:
            Log.i(TAG, "Msg_Goto_TimeSearch");
            Goto_TimeSearch((TimeRange[])msg.obj);
            break;
        }
    }

    private void Panel_Init(Intent intent) {
        LivePanelFragment live = (LivePanelFragment) getSupportFragmentManager().findFragmentById(R.id.panelFragementContainer);
        live.InitializeDivide(mChannel);
        live.InitializeSearch();
        live.InitializeStatus(mChannel);
        live.InitializeStream();
        live.InitializeControl(intent.getBooleanExtra(PeerDVR.HasRelayPermission, false), 
                               intent.getBooleanExtra(PeerDVR.NotifySupported, false), 
                               intent.getBooleanExtra(PeerDVR.NotifyEnabled, false));
    }

    private void Peer_VideoMotionChanged(VideoMotionChangedEventArgs arg) {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.panelFragementContainer);
        if (f instanceof LivePanelFragment)
            ((LivePanelFragment)f).UpdateStatus_VideoMotion(arg);
    }
    
    private void Peer_VideoLossChanged(VideoLossChangedEventArgs arg) {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.panelFragementContainer);
        if (f instanceof LivePanelFragment)
            ((LivePanelFragment)f).UpdateStatus_VideoLoss(arg);
    }
    
    private void Stream_VideoFirstFrameArrived(int channel) {
        findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
        
        if (mChannel == null) {
            Log.w(TAG, "channels is NULL -_-");
            return;
        }

        if (channel >= 0) { // single channel
            ActionBar bar = getSupportActionBar();
            bar.setTitle(mChannel[channel].name);
        }
        else { // multiple channel
            //TODO:
        }
    }
    
    private void Stream_OnStreamStartConnect(int channel) {
        GetCanvasFragment().Canvas().Refresh();
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        GetCanvasFragment().Canvas().Refresh();
    }
    
    private void Stream_StreamErrorOccurred(ErrorOccurredEventArgs arg) {
        findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
    }
    
    private void Pormpt_No_Record() {
        Toast.makeText(this, R.string.noRecord, Toast.LENGTH_LONG).show();
    }
    
    private void Goto_TimeSearch(TimeRange[] arg) {
        Intent intent = new Intent();
        intent.setClass(this, TimeSearchActivity.class);
        ArrayList<TimeRange> ranges = new ArrayList<TimeRange>();
        for (TimeRange obj : arg)
            ranges.add(obj);    
        intent.putExtra("TimeRange", ranges);
        startActivity(intent);
    }
    
    //=====================================================
    
    private void InitializeCanvasView() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        CanvasFragment cf = new CanvasFragment();
        ft.replace(R.id.mainFragementContainer, cf);
        ft.commit();
    }

    private CanvasFragment GetCanvasFragment() {
        CanvasFragment c = (CanvasFragment)getSupportFragmentManager().findFragmentById(R.id.mainFragementContainer);
        return c;
    }
    
    public void AttachLivePanel() {
        SetPanel(LivePanelFragment.class, false);
    }
    
    public void AttachRelayPanel() {
        SetPanel(new RelayPanelFragment(mChannel, mRelay), true);
    }
    
    public void AttachPTZPanel() {
        SetPanel(new PTZPanelFragment(mVisual.getView().get(0).Channel()), true);
    }
    
    private void SetPanel(Fragment fragment, boolean addToBackStack) {
        synchronized (this) {
            mPanelStack.add(fragment.getClass().getName());
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.panelFragementContainer, fragment);
        if (addToBackStack)
            ft.addToBackStack(null);
        ft.commit();
    }
    
    protected <T> void SetPanel(Class<T> clz, boolean addToBackStack) {
        synchronized (this) {
            mPanelStack.add(clz.getName());
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.panelFragementContainer, Fragment.instantiate(this, clz.getName()));
        if (addToBackStack)
            ft.addToBackStack(null);
        ft.commit();
    }
    
    public void PopupPanel() {
        synchronized (this) {
            mPanelStack.remove(mPanelStack.size()-1);
        }
    }
    
    
    public void GetRecordList() {
        PopupWaitingDialog();
        mApplication.dvr().GetRecordList();
    }
    
    public void GotoLogSearch() {
        Intent intent = new Intent();
        intent.setClass(this, LogSearchActivity.class);
        startActivity(intent);
    }
    
    /////////////////// VisualActivity //////////////////////////////////
    public void SetVisual(CanvasView canvas) {
        if (mChannel == null)
            return;
        SetVisual(canvas, mChannel.length);
    }
    
    protected void SetVisual(CanvasView canvas, int channelCount) {
        synchronized (this) {
            if (mApplication.getVisuals() == null)
                mApplication.CreateVisuals(this, channelCount, (int)canvas.getX(), (int)canvas.getY(), (int)canvas.getWidth(), (int)canvas.getHeight(), mDivide, mVisualID);
            mVisual = mApplication.getVisuals();
            canvas.SetVisuals(mVisual);
        }
    }
    

    @Override
    public void Raise_VisualChanged(int divide) {
        Log.i(TAG, "Raise_VisualChanged=" + divide);
        
        FragmentManager fm = getSupportFragmentManager();
        
        // update visual view
        Fragment f = fm.findFragmentById(R.id.mainFragementContainer);
        ((OnVisualChangedListener)f).OnVisualChanged(divide);
        
        // update stream
        List<ViewA> v = mVisual.getView();
        int mask = 0;
        for (ViewA view : v) {
            Log.i(TAG, "view channel=" + view.Channel());
            if (view.Empty())
                continue;
            mask |= (1 << view.Channel());
        }

        PeerDVR dvr = (PeerDVR)mApplication.dvr();
        dvr.CreateStreamStart(mask, v.size() > 1 ? 0 : mask);

        f = fm.findFragmentById(R.id.panelFragementContainer);
        if (f instanceof LivePanelFragment) {
            //Log.i(TAG, "get visualid=" + mVisual.VisualID());
            //Log.i(TAG, "...." + Boolean.toString(mVisual.Divide() == 1 ? mChannel[mVisual.getView().get(0).Channel()].ptzEnabled : false));
            ((LivePanelFragment)f).UpdateControl_PTZ(mVisual.Divide() == 1 ? mChannel[v.get(0).Channel()].ptzEnabled : false);
        }
    }
    
    
    @Override
    public void Raise_VisualChanged() {
        Log.i(TAG, "Raise_VisualChanged*");
        
        FragmentManager fm = getSupportFragmentManager();
        
        // update visual view
        Fragment f = fm.findFragmentById(R.id.mainFragementContainer);
        ((OnVisualChangedListener)f).OnVisualChanged(mVisual.Divide());
        
        f = fm.findFragmentById(R.id.panelFragementContainer);
        if (f instanceof LivePanelFragment) {
            //Log.i(TAG, "get visualid=" + mVisual.VisualID());
            //Log.i(TAG, "...." + Boolean.toString(mVisual.Divide() == 1 ? mChannel[mVisual.getView().get(mVisual.VisualID()).Channel()].ptzEnabled : false));
            ((LivePanelFragment)f).UpdateControl_PTZ(mVisual.Divide() == 1 ? mChannel[mVisual.getView().get(mVisual.VisualID()).Channel()].ptzEnabled : false);
        }
    }
    
    @Override
    public void Raise_VisualChanged(int divide, boolean arg) {
        Log.i(TAG, "Raise_VisualChanged**");
    }
    
    

    //==================================================================================
    //
    // DVRListener
    //
    @Override
    public void OnErrorOccurred(ErrorOccurredEventArgs arg) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void OnVideoLossChanged(VideoLossChangedEventArgs arg) {
        SendMessage2UI(Msg_Peer_VideoLossChanged, arg);
    }

    @Override
    public void OnVideoMotionChanged(VideoMotionChangedEventArgs arg) {
        SendMessage2UI(Msg_Peer_VideoMotionChanged, arg);
    }

    @Override
    public void OnGetChannels(Channel[] arg) {
        synchronized (this) {
            mChannel = arg;
        }
        SendMessage2UI(Msg_Peer_GetChannelAll, arg);
    }
    
    @Override
    public void OnGetPTZAvailablePreset(int index, int[] arg) {
        Log.i(TAG, "OnGetPTZAvailablePreset ^_^");
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.panelFragementContainer);
        if (f instanceof PTZPanelFragment) {
            ((PTZPanelFragment)f).OnGetAvialbePreset(index, arg);
        }
    }
    
    
    
    @Override
    public void OnGetRecordList(TimeRange[] arg) {
        DismissWaitingDialog();
        if (arg == null || arg.length == 0) {
            SendMessage2UI(Msg_Pormpt_No_Record);
            return;
        }
        SendMessage2UI(Msg_Goto_TimeSearch,  arg);
     }
    
    @Override
    public void OnVideoFirstFrameArrived(int channel) {
        SendMessage2UI(Msg_Stream_VideoFirstFrameArrived, channel);
    }
    
    @Override
    public void OnStreamStartConnect(int channel) {
        SendMessage2UI(Msg_Stream_OnStreamStartConnect, channel);
    }
    
    @Override
    public void OnStreamErrorOccurred(ErrorOccurredEventArgs arg) {
        SendMessage2UI(Msg_Stream_StreamErrorOccurred, arg);
    }

    @Override
    public void onVisualCreated() {
        //this.GetCanvasFragment().NextVisual();
    }

    @Override
    public void onVisualChange(int divide) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onVisualChange(int divide, int defaultID) {
        // TODO Auto-generated method stub
        
    }

    
    
    
}