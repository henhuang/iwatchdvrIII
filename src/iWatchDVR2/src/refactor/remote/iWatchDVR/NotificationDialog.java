package refactor.remote.iWatchDVR;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.PopupWindow;


public class NotificationDialog extends AlertDialog {

    protected NotificationDialog(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    static final int State_Show    = 0x00;
    static final int State_Dismiss = 0x01;
    
    protected ProgressDialog mDialog;
    protected int            mState = State_Dismiss;

    public void setState(int state) {
        
        mState = state;
    }
    
    public int getState() {
        
        return mState;
    }
    
    public void onDismiss() {
        
        mState = State_Dismiss;
    }
}
