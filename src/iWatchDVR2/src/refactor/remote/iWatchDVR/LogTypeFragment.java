package refactor.remote.iWatchDVR;

import java.util.ArrayList;
import java.util.List;

import peersdk.LogType;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.util.Pair;

public class LogTypeFragment extends CallbackDialogFragment {

	MultiSelectionAdapter mAdapter;
	MultiSelectionCallback mMultiSelectionCallback;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mMultiSelectionCallback = (MultiSelectionCallback)getActivity();
	}
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Context context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        List<Pair<String, Integer>> items = new ArrayList<Pair<String, Integer>>();
        items.add(new Pair<String, Integer>(LogType.toString(context, LogType.PowerOn), LogType.PowerOn));
        items.add(new Pair<String, Integer>(LogType.toString(context, LogType.RecordCH), LogType.RecordCH));
        items.add(new Pair<String, Integer>(LogType.toString(context, LogType.VLoss), LogType.VLoss));
        items.add(new Pair<String, Integer>(LogType.toString(context, LogType.Sensor), LogType.Sensor));
        items.add(new Pair<String, Integer>(LogType.toString(context, LogType.Motion), LogType.Motion));
        items.add(new Pair<String, Integer>(LogType.toString(context, LogType.Login), LogType.Login));
        items.add(new Pair<String, Integer>(LogType.toString(context, LogType.Logout), LogType.Logout));
        items.add(new Pair<String, Integer>(LogType.toString(context, LogType.ConfigExport), LogType.ConfigExport));
        items.add(new Pair<String, Integer>(LogType.toString(context, LogType.ConfigDefault), LogType.ConfigDefault));
        items.add(new Pair<String, Integer>(LogType.toString(context, LogType.ConfigImport), LogType.ConfigImport));
        items.add(new Pair<String, Integer>(LogType.toString(context, LogType.LogExport), LogType.LogExport));
        items.add(new Pair<String, Integer>(LogType.toString(context, LogType.LogClear), LogType.LogClear));
        items.add(new Pair<String, Integer>(LogType.toString(context, LogType.ChangeDateTime), LogType.ChangeDateTime));
        items.add(new Pair<String, Integer>(LogType.toString(context, LogType.ChangeRecordSetting), LogType.ChangeRecordSetting));
        items.add(new Pair<String, Integer>(LogType.toString(context, LogType.HDDFormat), LogType.HDDFormat));
        items.add(new Pair<String, Integer>(LogType.toString(context, LogType.HDDSet), LogType.HDDSet));
        items.add(new Pair<String, Integer>(LogType.toString(context, LogType.Upgrade), LogType.Upgrade));
        items.add(new Pair<String, Integer>(LogType.toString(context, LogType.Backup), LogType.Backup));
        items.add(new Pair<String, Integer>(LogType.toString(context, LogType.ChangeAdminPass), LogType.ChangeAdminPass));
        items.add(new Pair<String, Integer>(LogType.toString(context, LogType.NoHDD), LogType.NoHDD));
        items.add(new Pair<String, Integer>(LogType.toString(context, LogType.HDDFull), LogType.HDDFull));
        items.add(new Pair<String, Integer>(LogType.toString(context, LogType.HDDError), LogType.HDDError));
        items.add(new Pair<String, Integer>(LogType.toString(context, LogType.MCUError), LogType.MCUError));
        items.add(new Pair<String, Integer>(LogType.toString(context, LogType.SystemError), LogType.SystemError));
        items.add(new Pair<String, Integer>(LogType.toString(context, LogType.WriteError), LogType.WriteError));
        items.add(new Pair<String, Integer>(LogType.toString(context, LogType.Reboot), LogType.Reboot));

        if (mAdapter == null)
        	mAdapter = new MultiSelectionAdapter(getActivity(), 0, 0, items);
        
        builder.setAdapter(mAdapter, null)
        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            	mMultiSelectionCallback.onSelectionUpdated(LogSearchActivity.LogType, mAdapter.getState());
            }
        })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return builder.create();
    }
}

