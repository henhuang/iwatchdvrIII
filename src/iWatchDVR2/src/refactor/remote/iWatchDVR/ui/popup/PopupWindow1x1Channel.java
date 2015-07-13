package refactor.remote.iWatchDVR.ui.popup;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import refactor.remote.iWatchDVR.DVR;
import refactor.remote.iWatchDVR.R;
import refactor.remote.iWatchDVR.VisualActivity;
import refactor.remote.iWatchDVR.dvr.args.Channel;
import refactor.remote.iWatchDVR.widget.QuickAction.QuickAction;

public class PopupWindow1x1Channel extends QuickAction {
    
    public final static String TAG = "__PopupWindow1x1Channel__";


    public PopupWindow1x1Channel(final VisualActivity context, Channel[] arg) {
        super(context, R.layout.popup_1x1);

        int id = R.id.button__single_ch00;
        
        for (int i = 0; i < DVR.MAX_CHANNEL; i++) {
            TextView ch = (TextView) mRootView.findViewById(id + i);
            
            boolean isShow = i < arg.length;
            int visible = isShow ? View.VISIBLE : View.GONE;
            ch.setVisibility(visible);
            if (!isShow)
                continue;

            if (arg[i].ptzEnabled) {
                ch.setCompoundDrawablesWithIntrinsicBounds(null, 
                                        context.getResources().getDrawable(R.drawable.selector_button_1x1_ptz), 
                                        null, 
                                        null);
            }
            
            ActionItemExtraHolder holder = new ActionItemExtraHolder(i);
            ch.setTag(holder);
            
            ch.setText(arg[i].name);
            ch.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    int index = ((ActionItemExtraHolder) v.getTag()).getIndex();
                    context.NextVisual1x1(index);
                    dismiss();
                }
            });
        } 
    }

}

