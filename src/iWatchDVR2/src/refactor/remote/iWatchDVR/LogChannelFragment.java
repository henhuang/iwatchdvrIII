package refactor.remote.iWatchDVR;

import java.util.ArrayList;
import java.util.List;
import refactor.remote.iWatchDVR.application.RemoteDVRApplication;
import refactor.remote.iWatchDVR.dvr.args.Channel;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Pair;
import android.view.View;

public class LogChannelFragment  extends CallbackDialogFragment {

    MultiSelectionAdapter mAdapter;
    MultiSelectionCallback mMultiSelectionCallback;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mMultiSelectionCallback = (MultiSelectionCallback)getActivity();
	}
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        Channel[] channels = ((RemoteDVRApplication)context.getApplicationContext()).dvr().ChannelInfo();
        List<Pair<String, Integer>> items = new ArrayList<Pair<String, Integer>>();
        for (Channel obj : channels)
            items.add(new Pair<String, Integer>(obj.name, obj.id));

        if (mAdapter == null)
            mAdapter = new MultiSelectionAdapter(getActivity(), 0, 0, items);
        
        builder.setAdapter(mAdapter, null)
        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                dialog.dismiss();
                
                mMultiSelectionCallback.onSelectionUpdated(LogSearchActivity.Channel, mAdapter.getState());
            }

        })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                dialog.dismiss();
            }
        });
        return builder.create();
    }
}
