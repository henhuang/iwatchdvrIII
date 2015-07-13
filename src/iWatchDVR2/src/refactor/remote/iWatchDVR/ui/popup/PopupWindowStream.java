package refactor.remote.iWatchDVR.ui.popup;

import peersdk.peer.PeerStream;
import android.app.Activity;
import refactor.remote.iWatchDVR.R;
import refactor.remote.iWatchDVR.R.id;
import refactor.remote.iWatchDVR.R.layout;
import refactor.remote.iWatchDVR.application.RemoteDVRApplication;
import refactor.remote.iWatchDVR.widget.QuickAction.QuickAction;
import android.util.Log;
import  android.view.View;

public class PopupWindowStream extends QuickAction {
    
    public final static String TAG = "__PopupWindowStream__";
    
    public PopupWindowStream(final Activity context) {
        super(context, R.layout.popup_stream);

        View mainStream = mRootView.findViewById(R.id.button_stream_mainStream);
        View subStream  = mRootView.findViewById(R.id.button_stream_subStream);
        final View muteOn = mRootView.findViewById(R.id.button_stream_muteon);
        final View muteOff  = mRootView.findViewById(R.id.button_stream_muteoff);
        
        mainStream.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                //((RemoteDVRApplication)context.getApplication()).getDVR().StreamSwitch(PeerStream.HQ);
                dismiss();
            }
        });
        
        subStream.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                //((RemoteDVRApplication)context.getApplication()).getDVR().StreamSwitch(PeerStream.LQ);
                dismiss();
            }
        });
        
        muteOn.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                //TODO:
                dismiss();
                
                v.setVisibility(View.INVISIBLE);
                muteOff.setVisibility(View.VISIBLE);
            }
        });
        
        muteOff.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                //TODO:
                dismiss();
                
                v.setVisibility(View.INVISIBLE);
                muteOn.setVisibility(View.VISIBLE);
            }
        });
    }
    
    public void setMutileChannelEnale(boolean enabled) {
        mRootView.findViewById(R.id.button_stream_mainStream).setEnabled(enabled);
    }
}
