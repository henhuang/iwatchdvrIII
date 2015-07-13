package refactor.remote.iWatchDVR;

import refactor.remote.iWatchDVR.application.RemoteDVRApplication;
import refactor.remote.iWatchDVR.dvr.PeerDVR;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;


public class PresetFragment extends DialogFragment {

    final static String TAG = "__PresetFragment__";
    public final static int Mode_Go = 0x0;
    public final static int Mode_Set = 0x1;
    
    int mIndex;
    int mMode;
    int[] mPreset;
    
    public PresetFragment(int index, int mode, int[] preset) {
        mIndex = index;
        mMode = mode;
        mPreset = preset;
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.fragment_preset, null);
        
        final Dialog dialog =  new AlertDialog.Builder(getActivity())
        .setView(view)
        .create();
        
        //////////////////////////////////////////////////////////////
        
        String[] presetName = new String[mPreset.length];
        for (int i = 0; i < presetName.length; i++)
            presetName[i] = Integer.toString(mPreset[i]);

        int res = mMode == Mode_Go ? R.layout.gridview_gopreset : R.layout.gridview_setpreset;
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), res, presetName);

        final PeerDVR dvr = ((RemoteDVRApplication)getActivity().getApplication()).dvr();
        
        GridView gridView = (GridView) view.findViewById(R.id.ptz_preset_grid);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                    long id) {

                switch (mMode) {
                case Mode_Go:
                    dvr.PTZGoPreset(mIndex, position);
                    break;
                    
                case Mode_Set:
                    dvr.PTZSetPreset(mIndex, position);
                    break;
                }
                dialog.dismiss();
            }        
        });

        return dialog;
    }
    
    public void onPause() {
        super.onPause();
        
        DialogFragment dialog = (DialogFragment)getActivity().getSupportFragmentManager().findFragmentByTag(WaitingDialog.class.getName());
        
        if (dialog != null) { // dialog could be not attached before PresetFragment showing
            dialog.dismiss();
            Log.i(TAG, "dialog dismiss");
        }
        
    }
}
