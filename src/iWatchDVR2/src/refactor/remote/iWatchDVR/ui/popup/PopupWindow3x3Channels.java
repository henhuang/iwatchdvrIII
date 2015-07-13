package refactor.remote.iWatchDVR.ui.popup;

import refactor.remote.iWatchDVR.R;
import refactor.remote.iWatchDVR.widget.QuickAction.QuickAction;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class PopupWindow3x3Channels extends QuickAction {
    
    public final static String TAG = "__PopupWindow3x3Channels__";

    public PopupWindow3x3Channels(final Activity context, int channelCount) {
        super(context, R.layout.popup_8ch);

        int max = (int) Math.ceil((double) channelCount / 9);
        int id = R.id._8ch_ch00;

            for (int i = 0; i < max; i++) {
                TextView ch = (TextView) mRootView.findViewById(id + i);
                if (i == 0 && channelCount == 9)
                    ch.setText(R.string._3x3_9ch_ch0);
                ch.setVisibility(View.VISIBLE);

                ActionItemExtraHolder holder = new ActionItemExtraHolder(i);
                ch.setTag(holder);
                ch.setOnClickListener(new OnClickListener() {
    
                    @Override
                    public void onClick(View v) {
                        //TODO
                        //int index = ((ActionItemExtraHolder) v.getTag()).getIndex();
                        //context.gotoNextVisaul(9, index);
                        dismiss();
                    }
                });
            }

        }
        
}
    
