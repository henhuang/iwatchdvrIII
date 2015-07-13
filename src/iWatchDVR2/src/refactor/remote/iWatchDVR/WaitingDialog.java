package refactor.remote.iWatchDVR;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.widget.ProgressBar;


public class WaitingDialog extends DialogFragment {

	FragmentActivity mContext;
	String mTag;
	
	public WaitingDialog() {
		super();
		mContext = getActivity();
	}
	/**
	public WaitingDialog(FragmentActivity context, String tag) {
		mContext = context;
		mTag = tag;
	}
	
	public WaitingDialog(FragmentActivity context) {
		mContext = context;
	}
	*/
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Dialog dialog = new AlertDialog.Builder(mContext)
        .setView(new ProgressBar(mContext))
        .create();

        return dialog;
    }
    
    public void Show() {
    	show(mContext.getSupportFragmentManager(), mTag);
    }
    
    public void Dismiss() {
    	dismiss();
    }

}
