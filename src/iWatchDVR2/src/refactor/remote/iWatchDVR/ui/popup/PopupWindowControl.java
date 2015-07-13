package refactor.remote.iWatchDVR.ui.popup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import refactor.remote.iWatchDVR.R;
import refactor.remote.iWatchDVR.RemoteDVRActivity;
import refactor.remote.iWatchDVR.R.id;
import refactor.remote.iWatchDVR.R.layout;
import refactor.remote.iWatchDVR.widget.QuickAction.QuickAction;

public class PopupWindowControl extends QuickAction {
    
    public final static String TAG = "__PopupWindowControl__";
    
    public final static String CHANNEL = "channel";

    public PopupWindowControl(Context context, boolean hasRelayPermission, boolean notifySupported, boolean notifyEnabled) {
        super(context, R.layout.popup_control);
        
        TextView ptz = (TextView) mRootView.findViewById(R.id.button_control_ptz);
        ptz.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                dismiss();
                
                RemoteDVRActivity context = (RemoteDVRActivity) v.getContext();
                context.AttachPTZPanel();
            }
        });
        
        TextView relay = (TextView) mRootView.findViewById(R.id.button_control_relay);
        if (hasRelayPermission) {
            
            relay.setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    dismiss();
                    
                    RemoteDVRActivity context = (RemoteDVRActivity) v.getContext();
                    context.AttachRelayPanel();
                }
            });
        }
        else {
            relay.setEnabled(false);
        }
        
        TextView zoom = (TextView) mRootView.findViewById(R.id.button_control_zoom);
        zoom.setOnClickListener(new View.OnClickListener() {
           
            @Override
            public void onClick(View v) {
                
                dismiss();
                /**
                RemoteDVRActivity context = (RemoteDVRActivity) v.getContext();
                context.getPreference().setContinueStream(true);
                
                Intent intent = new Intent();
                intent.setClass(v.getContext(), ZoomActivity.class);
                v.getContext().startActivity(intent);
                */
            }
        });
        
        final TextView notify = (TextView) mRootView.findViewById(R.id.button_control_notify);
        final TextView unnotify = (TextView) mRootView.findViewById(R.id.button_control_unnotify);
        notify.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                /**           
                dismiss();
                
                RemoteDVRActivity context = (RemoteDVRActivity) v.getContext();
                context.EnablePushNotification(false);
                */
            }
        });
                    
        unnotify.setOnClickListener(new View.OnClickListener() {
                        
            @Override
            public void onClick(View v) {
                /*
                dismiss();
                      
                RemoteDVRActivity context = (RemoteDVRActivity) v.getContext();
                context.EnablePushNotification(true);
                */
            }
        });
    }
    
    public void InitializeNotificationButton(boolean support, boolean registered) {
        //Log.i(TAG, "InitializeNotificationButton, support=" + Boolean.toString(support) +
        //        ", rigistered=" + Boolean.toString(registered));
        
        final TextView notify = (TextView) mRootView.findViewById(R.id.button_control_notify);
        final TextView unnotify = (TextView) mRootView.findViewById(R.id.button_control_unnotify);
        
        if (!support) {
            notify.setEnabled(false);
            unnotify.setEnabled(false);
            return;
        }

        notify.setEnabled(registered);
        unnotify.setEnabled(registered);
    }
    
    public void SetPTZEnable(boolean enable) {  
        TextView ptz = (TextView) mRootView.findViewById(R.id.button_control_ptz);
        ptz.setEnabled(enable);
    }
}
