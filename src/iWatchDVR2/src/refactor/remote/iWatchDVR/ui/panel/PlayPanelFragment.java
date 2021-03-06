package refactor.remote.iWatchDVR.ui.panel;

import peersdk.peer.event.VideoLossChangedEventArgs;
import peersdk.peer.event.VideoMotionChangedEventArgs;
import refactor.remote.iWatchDVR.R;
import refactor.remote.iWatchDVR.VisualActivity;
import refactor.remote.iWatchDVR.dvr.args.Channel;
import refactor.remote.iWatchDVR.ui.popup.PopupWindow1x1Channel;
import refactor.remote.iWatchDVR.ui.popup.PopupWindow2x2Channels;
import refactor.remote.iWatchDVR.ui.popup.PopupWindow3x3Channels;
import refactor.remote.iWatchDVR.ui.popup.PopupWindow4x4Channels;
import refactor.remote.iWatchDVR.ui.popup.PopupWindowControl;
import refactor.remote.iWatchDVR.ui.popup.PopupWindowSearch;
import refactor.remote.iWatchDVR.ui.popup.PopupWindowStatus;
import refactor.remote.iWatchDVR.ui.popup.PopupWindowStream;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.TextView;

public class PlayPanelFragment extends Fragment {
    
    final static String TAG = "__PlayPanelFragment__";

    PopupWindow1x1Channel m1x1popup;
    PopupWindow2x2Channels m2x2popup;
    PopupWindow3x3Channels m3x3popup;
    PopupWindow4x4Channels m4x4popup;

    Channel[] mChannels;
    boolean mHasRelayPermission;
    boolean mNotifySupported;
    boolean mNotifyEnabled;
    boolean mInit = false;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        return inflater.inflate(R.layout.fragment_play_panel, null);
    }
    
    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setRetainInstance(true);
    }
    
    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "onActivityCreated");

        if (!mInit) {
            mInit = true;
        }
        else {
            InitializeDivide(mChannels);
            InitializeStream();
            UpdateDivideFunction(mChannels.length);
        }
    }

    public void InitializeDivide(Channel[] arg) {
        int channelCount = arg.length;
        View v = getView();
        VisualActivity context = (VisualActivity)getActivity();
        
        if (mChannels == null)
            mChannels = arg;
        
        if (m1x1popup == null) 
            m1x1popup = new PopupWindow1x1Channel(context, arg);
        if (m2x2popup == null) 
            m2x2popup = new PopupWindow2x2Channels(context, channelCount);
        if (m3x3popup == null) 
            m3x3popup = new PopupWindow3x3Channels(context, channelCount);
        if (m4x4popup == null) 
            m4x4popup = new PopupWindow4x4Channels(context, channelCount);
        
        UpdateDivideFunction(channelCount);

        TextView _1x1 = (TextView)v.findViewById(R.id._1x1);
        _1x1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                OnNextVisual(1);
            }
            
        });
        _1x1.setOnLongClickListener(new View.OnLongClickListener() {
            
            @Override
            public boolean onLongClick(View v) {
                m1x1popup.show(v);
                return true;
            }
        });
        
        
        
        TextView _2x2 = (TextView)v.findViewById(R.id._2x2);
        _2x2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                OnNextVisual(4);
            }
            
        });
        
        _2x2.setOnLongClickListener(new View.OnLongClickListener() {
            
            @Override
            public boolean onLongClick(View v) {
                m2x2popup.show(v);
                return true;
            }
        });
        
        TextView _3x3 = (TextView)v.findViewById(R.id._3x3);
        _3x3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                OnNextVisual(9);
            }
        });
        
        _3x3.setOnLongClickListener(new View.OnLongClickListener() {
            
            @Override
            public boolean onLongClick(View v) {
                m3x3popup.show(v);
                return true;
            }
        });
        
        TextView _4x4 = (TextView)v.findViewById(R.id._4x4);
        _4x4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                OnNextVisual(16);
            }
            
        });
        _4x4.setOnLongClickListener(new View.OnLongClickListener() {
            
            @Override
            public boolean onLongClick(View v) {
                m4x4popup.show(v);
                return true;
            }
        });
    }



    public void InitializeStream() {
        /**
        View v = getView();

        TextView mainStream = (TextView)v.findViewById(R.id.mainStream);
        mainStream.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                
            }
        });
        */
    }
    
    protected void UpdateDivideFunction(int channelCount) {
        View v = getView();
        v.findViewById(R.id._1x1).setEnabled(true);
        v.findViewById(R.id._2x2).setEnabled(channelCount >= 4);
        v.findViewById(R.id._3x3).setEnabled(channelCount >= 8);
        v.findViewById(R.id._4x4).setEnabled(channelCount >= 10);
    }

    protected void OnNextVisual(int divide) {
        ((VisualActivity)getActivity()).NextVisual(divide);
    }

}
