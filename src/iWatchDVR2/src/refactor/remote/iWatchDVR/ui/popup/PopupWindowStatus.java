package refactor.remote.iWatchDVR.ui.popup;

import peersdk.peer.event.VideoLossChangedEventArgs;
import peersdk.peer.event.VideoMotionChangedEventArgs;
import android.content.Context;
import android.view.View;
import android.widget.TextView;
import refactor.remote.iWatchDVR.DVR;
import refactor.remote.iWatchDVR.R;
import refactor.remote.iWatchDVR.R.id;
import refactor.remote.iWatchDVR.R.layout;
import refactor.remote.iWatchDVR.dvr.args.Channel;
import refactor.remote.iWatchDVR.widget.QuickAction.QuickAction;


public class PopupWindowStatus extends QuickAction {

    public final static String TAG = "__PopupWindowStatusChannel__";

    public    final static String CHANNEL = "channel";
    protected final static int UPDATE_CHANNEL_STAUTS = 0xa0;

    
    public PopupWindowStatus(final Context context, Channel[] arg) {
        super(context, R.layout.popup_status);

        int start = R.id.button__status_ch00;
        int startNormal = R.id.button__status__normal_ch00;
        int startVloss = R.id.button__status__vloss_ch00;
        int startMotion = R.id.button__status__motion_ch00;
        
        for (int i = 0; i < DVR.MAX_CHANNEL; i++) {
            
            int resid = start + i*4;
            View ch = mRootView.findViewById(resid);
            ch.setVisibility(i < arg.length ? View.VISIBLE : View.GONE);
            
            if (ch.getVisibility() != View.VISIBLE)
                continue;
            
            ActionItemExtraHolder holder = new ActionItemExtraHolder(i);
            ch.setTag(holder);
            
            String name = arg[i].name;
            ((TextView) ch.findViewById(startNormal + i*4)).setText(name);
            ((TextView) ch.findViewById(startVloss + i*4)).setText(name);
            ((TextView) ch.findViewById(startMotion + i*4)).setText(name);
            
            ch.setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    //TODO
                    //int index = ((ActionItemExtraHolder) v.getTag()).getIndex();
                    //context.gotoNextVisaul(1, index);
                    dismiss();
                }
            });
        }
  
    }

    protected void updateChannelStatusVisible(View parent, int i,  boolean normal, boolean vloss, boolean motion) {
        
        int normalId = R.id.button__status__normal_ch00 + i;
        int vlossId  = R.id.button__status__vloss_ch00  + i;
        int motionId = R.id.button__status__motion_ch00 + i;
        
        parent.findViewById(normalId).setVisibility(normal ? View.VISIBLE : View.INVISIBLE);
        parent.findViewById(vlossId).setVisibility(vloss ? View.VISIBLE : View.INVISIBLE);
        parent.findViewById(motionId).setVisibility(motion ? View.VISIBLE : View.INVISIBLE);
    }
    
    
    public void UpdateVideoLossChanged(VideoLossChangedEventArgs arg) {
        
        int active[] = arg.active;
        int deactive[] = arg.deactive;
        
        int id = R.id.button__status_ch00;
        for (int i = 0; i < active.length; i++) {
            
            int index = active[i] * 4;
            View ch = mRootView.findViewById(id + index);
            updateChannelStatusVisible(ch, index, false, true, false);
        }
        
        for (int i = 0; i < deactive.length; i++) {
            
            int index = deactive[i] * 4;
            View ch = mRootView.findViewById(id + index);
            updateChannelStatusVisible(ch, index, true, false, false);
        }
    }
    
    public void UpdateVideoMotionChanged(VideoMotionChangedEventArgs arg) {
        
        int active[] = arg.active;
        int deactive[] = arg.deactive;
                
        int id = R.id.button__status_ch00;
        for (int i = 0; i < active.length; i++) {
            int index = active[i] * 4; // resource id generated in R.java is 4-alignment
            View ch = mRootView.findViewById(id + index);
            updateChannelStatusVisible(ch, index, false, false, true);
        }
        
        for (int i = 0; i < deactive.length; i++) {
            int index = deactive[i] * 4;
            View ch = mRootView.findViewById(id + index);
            updateChannelStatusVisible(ch, index, true, false, false);
        }
    }
}
