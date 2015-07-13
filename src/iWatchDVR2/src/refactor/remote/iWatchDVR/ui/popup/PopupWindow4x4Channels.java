package refactor.remote.iWatchDVR.ui.popup;

import refactor.remote.iWatchDVR.DVR;
import refactor.remote.iWatchDVR.R;
import refactor.remote.iWatchDVR.R.id;
import refactor.remote.iWatchDVR.R.layout;
import refactor.remote.iWatchDVR.widget.QuickAction.QuickAction;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class PopupWindow4x4Channels extends QuickAction {
    
    public final static String TAG = "__PopupWindow16Channels__";

    public PopupWindow4x4Channels(final Activity context, int channelCount) {     
        super(context, R.layout.popup_4x4);

        int max = (int) Math.ceil((double) channelCount / 16);
        
        int id = R.id._4x4_ch00;
        for (int i = 0; i < max; i++) {
            TextView ch = (TextView) mRootView.findViewById(id + i);
            ch.setVisibility(View.VISIBLE);

            ActionItemExtraHolder holder = new ActionItemExtraHolder(i);
            ch.setTag(holder);
            ch.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    //TODO
                    //int index = ((ActionItemExtraHolder) v.getTag()).getIndex();
                    //context.gotoNextVisaul(16, index);
                    dismiss();
                }
            });
        }
    }
}
