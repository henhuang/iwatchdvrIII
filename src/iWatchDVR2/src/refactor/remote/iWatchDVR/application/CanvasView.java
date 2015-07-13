package refactor.remote.iWatchDVR.application;


import peersdk.media.MediaDispatcher;
import refactor.remote.iWatchDVR.VisualActivity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;


public class CanvasView extends GLSurfaceView {

    final static String TAG = "__CanvasView__";
    VisualActivity mContext;
    VisualListener mListener;
    RemoteDVRApplication mApp;
    CanvasRender mRender;
    Visuals mVisuals;

    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.i(TAG, "new CanvasView");
        mContext = (VisualActivity)context;
        mApp = (RemoteDVRApplication)context.getApplicationContext();
        mRender = new CanvasRender(this);
        mListener = (VisualListener)context;
        // TODO: touch gesture
        setEGLContextClientVersion(2);
        setRenderer(mRender);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        setDebugFlags(GLSurfaceView.DEBUG_LOG_GL_CALLS | GLSurfaceView.DEBUG_CHECK_GL_ERROR);
    }
    
    public CanvasView(Context context) {
        super(context);
        Log.i(TAG, "new CanvasView(Context context)");
        /*
        mContext = context;
        mApp = (RemoteDVRApplication)context.getApplicationContext();
        mRender = new CanvasRender(this);
        
        // TODO: touch gesture
       
        setEGLContextClientVersion(2);
        setRenderer(mRender);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        setDebugFlags(GLSurfaceView.DEBUG_LOG_GL_CALLS | GLSurfaceView.DEBUG_CHECK_GL_ERROR);
        */
    }
    
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        super.surfaceCreated(holder);
        Log.i(TAG, "surfaceCreated");
        /*
        if (mApp.getVisuals() == null) {
            int channelCount = mApp.getDVR().mCache.channels.length;
            mApp.CreateVisuals(mContext, channelCount, (int)getX(), (int)getY(), getWidth(), getHeight(), 1, 0);
            mVisuals = mApp.getVisuals();
            
            mListener.onVisualCreated();

            Log.i(TAG, "x=" + getX() + ", y=" + getY() + ", w=" + getWidth() + ", h" + getHeight());
        }
        */
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder,int format,int width,int height) {
        super.surfaceChanged(holder, format, width, height);
        Log.i(TAG, "surfaceChanged");
        
        if (mApp.dvr() == null) {
            Log.i("CanvasView", "dvr is null");
            return;
        }
        

        MediaDispatcher dispacher = mApp.dvr().MediaDispatcher();
        if (dispacher == null) { // tablet
            Log.i("CanvasView", "MediaDispatcher is null");
            return;
        }

        mContext.SetVisual(this);
        mContext.NextVisual();
        dispacher.SetSurface(this);
    }
    
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {   
        super.surfaceDestroyed(holder);
        Log.i(TAG, "surfaceChanged");
    }
    
    ////////////////////////////////////////////////////////////////////////
    
    public void SetVisuals(Visuals visual) {
        synchronized (this) {
            mVisuals = visual;
        }
    }
    
    public void doRender() {
        requestRender();
    }
    
    public void Refresh() {
        mRender.flush();
    }
    
    public CanvasRender getRender() {
        return mRender;
    }
    
    public Visuals getVisuals() {
        return mVisuals;
    }    
}
