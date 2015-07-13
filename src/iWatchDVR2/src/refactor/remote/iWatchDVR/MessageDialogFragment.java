package refactor.remote.iWatchDVR;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class MessageDialogFragment extends DialogFragment {

    boolean mError;
    boolean mYesno;
    int mTitleResid;
    int mMsgResid;
    
    public MessageDialogFragment(boolean error, boolean yesno, int titleResid, int msgResid) {
        mError = error;
        mYesno = yesno;
        mTitleResid = titleResid;
        mMsgResid = msgResid;
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Builder builder = new AlertDialog.Builder(getActivity());
        
        builder.setTitle(mTitleResid);
        builder.setMessage(mMsgResid);
        if (mError) {
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    

                    dialog.dismiss();
                }
            });
        }
        
        if (mYesno) {
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                }
            });
        }

        
        return builder.create();
    }
}
