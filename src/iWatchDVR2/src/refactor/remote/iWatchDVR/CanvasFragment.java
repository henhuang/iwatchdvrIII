package refactor.remote.iWatchDVR;

import java.util.ArrayList;

import refactor.remote.iWatchDVR.application.CanvasRender;
import refactor.remote.iWatchDVR.application.CanvasView;
import refactor.remote.iWatchDVR.application.RemoteDVRApplication;
import refactor.remote.iWatchDVR.application.ViewA;
import refactor.remote.iWatchDVR.application.Visuals;
import refactor.remote.iWatchDVR.listener.OnVisualChangedListener;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.AbsoluteLayout.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class CanvasFragment extends Fragment implements OnVisualChangedListener {

    final String TAG = "__CanvasFragment__";
    
    VisualAdapter mAdapter;
    CanvasView mSurface;
    RemoteDVRApplication mApplication;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.fragment_canvas, null);
        mSurface = (CanvasView)v.findViewById(R.id.canvas);
        mAdapter = null;
        return v;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
        mApplication = (RemoteDVRApplication)getActivity().getApplication();
        
        Log.i(TAG, "SetCanvas to MediaDispatcher");
        mApplication.dvr().SetCanvas(mSurface);
    }
    
    @Override
    public void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
        mSurface.onResume();
    }
    
    @Override
    public void onPause() {
        Log.i(TAG, "onPause");
        super.onPause();
        mSurface.onPause();
    }

    
    private void NextVisual(int divide) {
        switch (divide) {
        case Visuals.Divide_1x1:
            LayoutGrid(1);
            break;
        case Visuals.Divide_2x2:
            LayoutGrid(2);
            break;
        case Visuals.Divide_3x3:
            LayoutGrid(3);
            break;
        case Visuals.Divide_4x4:
            LayoutGrid(4);
            break;
        }
    }
    
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
    }
    
    private void LayoutGrid(int strip) {
        //if (mSurface.getVisuals() == null) {
        //    Log.w(TAG, "mSurface.getVisuals() == null");
         //   return;
       // }
        
        int dx = mSurface.getVisuals().dX();
        int dy = mSurface.getVisuals().dY();
        int w = mSurface.getVisuals().Width();
        int h = mSurface.getVisuals().Height();
        
        //Log.i(TAG, "dx=" + dx + " dy=" + dy + " w=" + w + " h=" + h);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(w, h);
        params.topMargin = dy;
        params.leftMargin = dx;

        GridView visual = (GridView)getView().findViewById(R.id.visual);
        visual.setLayoutParams(params); 
        visual.setNumColumns(strip);
        if (mAdapter == null) {
            mAdapter = new VisualAdapter(getActivity(), strip, 0);
            visual.setAdapter(mAdapter);
        }
        else {
            mAdapter.setStrip(strip);
            mAdapter.notifyDataSetChanged();
        }
        
    }


    public class VisualAdapter extends BaseAdapter {
        
        private Context mContext;
        int mStrip;

        public VisualAdapter(Context c, int strip, int count) {
            mContext = c;
            mStrip = strip;
        }

        public void setStrip(int strip) {
            mStrip = strip;
        }
        
        @Override
        public int getCount() {
            return mStrip * mStrip;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }
        
        @Override
        public long getItemId(int id) {
            return id;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.gridview_grid, null);
            v.setLayoutParams(new GridView.LayoutParams(LayoutParams.MATCH_PARENT, parent.getHeight() / mStrip));

            Visuals visuals = mSurface.getVisuals();
            if (visuals == null) {
                //Log.i(TAG, "mSurface.getVisuals is null");
                return v;
            }
            ArrayList<ViewA> views = visuals.getView();
            if (views == null) {
                //Log.i(TAG, "mSurface.getVisuals.getView is null");
                return v;
            }

            if (position >= views.size()) {
                //Log.i(TAG, "views size=" + views.size() + ", -_-");
                return v;
            }

            return v;
        }
       
    }

    
    public CanvasView Canvas() {
        return mSurface;
    }
    
    public void Refresh() {
        mSurface.Refresh();
    }

    @Override
    public void OnVisualChanged(int divide) {
        Log.i(TAG, "OnVisualChanged: " + divide);
        NextVisual(divide);
    }

}