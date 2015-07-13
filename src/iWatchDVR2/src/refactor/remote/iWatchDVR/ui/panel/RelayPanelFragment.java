package refactor.remote.iWatchDVR.ui.panel;

import peersdk.RelayPole;
import refactor.remote.iWatchDVR.R;
import refactor.remote.iWatchDVR.VisualActivity;
import refactor.remote.iWatchDVR.application.Visuals;
import refactor.remote.iWatchDVR.dvr.args.Channel;
import refactor.remote.iWatchDVR.dvr.args.Relay;
import refactor.remote.iWatchDVR.ui.popup.PopupWindow1x1Channel;
import refactor.remote.iWatchDVR.ui.popup.PopupWindow2x2Channels;
import refactor.remote.iWatchDVR.ui.popup.PopupWindow3x3Channels;
import refactor.remote.iWatchDVR.ui.popup.PopupWindow4x4Channels;
import refactor.remote.iWatchDVR.ui.popup.PopupWindowRelay;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class RelayPanelFragment extends Fragment {
    
    final static protected String TAG = "__RelayPanelFragment__";
    
    PopupWindow1x1Channel m1x1popup;
    PopupWindow2x2Channels m2x2popup;
    PopupWindow3x3Channels m3x3popup;
    PopupWindow4x4Channels m4x4popup;
    PopupWindowRelay mRelayPopup;
    
    Channel[] mChannels;
    Relay[] mRelays;
    boolean mInit = false;
    int mSelected = -1;
    
    public RelayPanelFragment(Channel[] channels, Relay[] relays) {
        mChannels = channels;
        mRelays = relays;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        return inflater.inflate(R.layout.fragment_relay_panel, null);
    }
    
    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
        Log.i(TAG, "onActivityCreated");

        InitializeDivide(mChannels);
        InitializeRelay(mRelays);
        UpdateDivideFunction(mChannels.length);
    }
    
    private void InitializeRelay(Relay[] relays) {
        Activity context = getActivity();
        final int start = R.id.relay0;
        
        for (int i = 0; i < 4; i++) {
            TextView relay = (TextView) context.findViewById(start+i);
            if (i >= relays.length) {
                relay.setVisibility(View.GONE);
                continue;
            }
            
            relay.setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    mRelayPopup.show(v/*, v.getId() - start*/);
                }
            });
            
            if (mRelayPopup == null && relays.length > 0) {
                mRelayPopup = new PopupWindowRelay(getActivity(), relays[0].pole == RelayPole.Legacy 
                                                                  ? R.layout.popup_relay_second : R.layout.popup_relay_pole);
            }
        }
    }
    
    private void InitializeDivide(Channel[] channels) {
        View v = getView();
        VisualActivity context = (VisualActivity)getActivity();

        if (m1x1popup == null) 
            m1x1popup = new PopupWindow1x1Channel(context, channels);
        if (m2x2popup == null) 
            m2x2popup = new PopupWindow2x2Channels(context, channels.length);
        if (m3x3popup == null) 
            m3x3popup = new PopupWindow3x3Channels(context, channels.length);
        if (m4x4popup == null) 
            m4x4popup = new PopupWindow4x4Channels(context, channels.length);
        
        UpdateDivideFunction(channels.length);

        TextView _1x1 = (TextView)v.findViewById(R.id._1x1);
        _1x1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                OnNextVisual(Visuals.Divide_1x1);
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
                OnNextVisual(Visuals.Divide_2x2);
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
                OnNextVisual(Visuals.Divide_3x3);
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
                OnNextVisual(Visuals.Divide_4x4);
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

    private void UpdateDivideFunction(int channelCount) {
        View v = getView();
        v.findViewById(R.id._1x1).setEnabled(true);
        v.findViewById(R.id._2x2).setEnabled(channelCount >= 4);
        v.findViewById(R.id._3x3).setEnabled(channelCount >= 8);
        v.findViewById(R.id._4x4).setEnabled(channelCount >= 10);
    }
    
    private void OnNextVisual(int divide) {
        ((VisualActivity)getActivity()).NextVisual(divide);
    }
}
