package refactor.remote.iWatchDVR.ui.popup;

import peersdk.RelayPole;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import refactor.remote.iWatchDVR.R;
import refactor.remote.iWatchDVR.R.layout;
import refactor.remote.iWatchDVR.application.RemoteDVRApplication;
import refactor.remote.iWatchDVR.widget.QuickAction.QuickAction;

public class PopupWindowRelay extends QuickAction {
    
    public final static String TAG = "__PopupWindowRelay__";
    private int mSelected = 0; // TODO
    
    public PopupWindowRelay(Context context, int layoutID) {
        super(context, layoutID);

        switch (layoutID) {        
        case R.layout.popup_relay_second:
            InitRelaySecond();
            break;
            
        case R.layout.popup_relay_pole:
            InitRelayPole();
            break;
        }
    }
    
    protected void InitRelaySecond() {
        
        TextView _1sec = (TextView) mRootView.findViewById(R.id.relay_1sec);
        _1sec.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                dismiss();
                ((RemoteDVRApplication)v.getContext().getApplicationContext()).dvr().RelayActivate(mSelected, 1);
            }
        });
        
        TextView _3sec = (TextView) mRootView.findViewById(R.id.relay_3sec);
        _3sec.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                dismiss();
                ((RemoteDVRApplication)v.getContext().getApplicationContext()).dvr().RelayActivate(mSelected, 3);
            }
        });
        
        TextView _5sec = (TextView) mRootView.findViewById(R.id.relay_5sec);
        _5sec.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                dismiss();
                ((RemoteDVRApplication)v.getContext().getApplicationContext()).dvr().RelayActivate(mSelected, 5);
            }
        });
        
        TextView _8sec = (TextView) mRootView.findViewById(R.id.relay_8sec);
        _8sec.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                dismiss();
                ((RemoteDVRApplication)v.getContext().getApplicationContext()).dvr().RelayActivate(mSelected, 8);
            }
        });
    }
    
    protected void InitRelayPole() {
        /**
        TextView no = (TextView) mRootView.findViewById(R.id.relay_no);
        no.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                dismiss();
                ((RemoteDVRApplication)v.getContext().getApplicationContext()).dvr().RelaySwitch(mSelected, RelayPole.NO);
            }
        });
        
        TextView nc = (TextView) mRootView.findViewById(R.id.relay_nc);
        nc.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
                ((RemoteDVRApplication)v.getContext().getApplicationContext()).dvr().RelaySwitch(mSelected, RelayPole.NC);
            }
        });
        
        TextView auto = (TextView) mRootView.findViewById(R.id.relay_auto);
        auto.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                dismiss();
                ((RemoteDVRApplication)v.getContext().getApplicationContext()).dvr().RelaySwitch(mSelected, RelayPole.Auto);
            }
        });
        */
    }
    
    public void show(View anchor, int selected) {
        mSelected = selected;
        show(anchor);
    }
}
