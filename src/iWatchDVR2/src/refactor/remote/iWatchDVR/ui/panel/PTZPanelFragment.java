package refactor.remote.iWatchDVR.ui.panel;

import refactor.remote.iWatchDVR.PresetFragment;
import refactor.remote.iWatchDVR.R;
import refactor.remote.iWatchDVR.RemoteDVRActivity;
import refactor.remote.iWatchDVR.WaitingDialog;
import refactor.remote.iWatchDVR.application.RemoteDVRApplication;
import refactor.remote.iWatchDVR.dvr.PeerDVR;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;

public class PTZPanelFragment extends Fragment implements OnTouchListener {

    final static protected String TAG = "__PTZPanelFragment__";

    int mIndex;
    int[] mPreset;
    int mMode = 0xf;
            
    public PTZPanelFragment(int index) {
        Log.i(TAG, "PTZPanelFragment: " + index);
        mIndex = index;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");

        final PeerDVR dvr = ((RemoteDVRApplication)getActivity().getApplication()).dvr();
        View v = inflater.inflate(R.layout.fragment_ptz_panel, null);
        v.findViewById(R.id.zoomIn).setOnTouchListener(this);
        v.findViewById(R.id.zoomOut).setOnTouchListener(this);
        v.findViewById(R.id.irisOpen).setOnTouchListener(this);
        v.findViewById(R.id.irisClose).setOnTouchListener(this);
        v.findViewById(R.id.focusIn).setOnTouchListener(this);
        v.findViewById(R.id.focusOut).setOnTouchListener(this);
        v.findViewById(R.id.goPreset).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PreparePreset(PresetFragment.Mode_Go);
            }
            
        });
        v.findViewById(R.id.setPreset).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PreparePreset(PresetFragment.Mode_Set);
            }
        });
        return v;
    }
    
    private void PreparePreset(int mode) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        synchronized (this) {
            mMode = mode;
            if (mPreset == null) {  
                new WaitingDialog().show(fm, WaitingDialog.class.getName());
                ((RemoteDVRApplication)getActivity().getApplication()).dvr().PTZGetAvailablePreset(mIndex);
                return;
            }
        }
        new PresetFragment(mIndex, mode, mPreset).show(fm, PresetFragment.class.getName());
    }
    
    public void OnGetAvialbePreset(int index, int[] arg) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        
        DialogFragment dialog = (DialogFragment)fm.findFragmentByTag(WaitingDialog.class.getName());
        if (dialog != null)
            dialog.dismiss();


        synchronized (this) {
            if (mIndex != index)
                return;
            mPreset = arg;
        }
        new PresetFragment(mIndex, mMode, mPreset).show(fm, PresetFragment.class.getName());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int motion = event.getAction();
        PeerDVR dvr = ((RemoteDVRApplication)getActivity().getApplication()).dvr();
        View view = getView();
        if (v == view.findViewById(R.id.irisOpen)) {
            if (motion == MotionEvent.ACTION_DOWN)
                dvr.PTZIrisOpen(mIndex);
            else if (motion == MotionEvent.ACTION_UP)
                dvr.PTZStop(mIndex);
            return true;
        }
        else if (v == view.findViewById(R.id.irisClose)) {
            if (motion == MotionEvent.ACTION_DOWN)
                dvr.PTZIrisClose(mIndex);
            else if (motion == MotionEvent.ACTION_UP)
                dvr.PTZStop(mIndex);
            return true;
        }
        else if (v == view.findViewById(R.id.zoomIn)) {
            if (motion == MotionEvent.ACTION_DOWN)
                dvr.PTZZoomIn(mIndex);
            else if (motion == MotionEvent.ACTION_UP)
                dvr.PTZStop(mIndex);
            return true;
        }
        else if (v == view.findViewById(R.id.zoomOut)) {
            if (motion == MotionEvent.ACTION_DOWN)
                dvr.PTZZoomOut(mIndex);
            else if (motion == MotionEvent.ACTION_UP)
                dvr.PTZStop(mIndex);
            return true;
        }
        else if (v == view.findViewById(R.id.focusIn)) {
            if (motion == MotionEvent.ACTION_DOWN)
                dvr.PTZFocusIn(mIndex);
            else if (motion == MotionEvent.ACTION_UP)
                dvr.PTZStop(mIndex);
            return true;
        }
        else if (v == view.findViewById(R.id.focusOut)) {
            if (motion == MotionEvent.ACTION_DOWN)
                dvr.PTZFocusOut(mIndex);
            else if (motion == MotionEvent.ACTION_UP)
                dvr.PTZStop(mIndex);
            return true;
        }
        return false;
    }

}
