package refactor.remote.iWatchDVR.ui.popup;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import refactor.remote.iWatchDVR.R;
import refactor.remote.iWatchDVR.R.id;
import refactor.remote.iWatchDVR.RemoteDVRActivity;
import refactor.remote.iWatchDVR.TimeSearchActivity;
import refactor.remote.iWatchDVR.application.RemoteDVRApplication;
import refactor.remote.iWatchDVR.widget.QuickAction.QuickAction;

public class PopupWindowSearch extends QuickAction {
    
    public final static String TAG = "__PopoupWindowSearch__";

    public PopupWindowSearch(final Context context, int layoutID) {
        super(context, layoutID);

        mRootView.findViewById(R.id.button_search_time_search).setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                dismiss();
                /*
                RemoteDVRActivity context = (RemoteDVRActivity) v.getContext();
                Intent intent = new Intent();
                intent.setClass(context, TimeSearchActivity.class);
                context.startActivity(intent);
                */
                ((RemoteDVRActivity) v.getContext()).GetRecordList();
            }
        });
        
        mRootView.findViewById(R.id.button_search_log_search).setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                dismiss();
                
                ((RemoteDVRActivity) v.getContext()).GotoLogSearch();
            }
        });
    }

}
