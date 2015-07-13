package refactor.remote.iWatchDVR;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public class CallbackDialogFragment extends DialogFragment {

    protected FragmentActivity mContext;
    protected String mTag;
    
    public void show(FragmentActivity context, String tag) {
        mContext= context;
        mTag = tag;
        show(context.getSupportFragmentManager(), toString()); 
    }
}
