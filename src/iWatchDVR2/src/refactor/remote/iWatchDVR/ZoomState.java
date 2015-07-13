package refactor.remote.iWatchDVR;

import java.util.Observable;

import refactor.remote.iWatchDVR.Type.Size;

import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

public class ZoomState extends Observable {

    static final String TAG = "__ZoomState__";
    private float mScaleAdjustShiftX = 0.0f;
    private float mScaleAdjustShiftY = 0.0f;
 
    private RectF  mZoomView = new RectF();
    private Size   mZoomDimension = new Size();
    
    public ZoomState(Rect zoomView, Size zoomDimension) {
        
        mZoomView.set(zoomView);
        mZoomDimension.width = zoomDimension.width;
        mZoomDimension.height = zoomDimension.height;
    }

    public Size getZoomDemision() {
        
        return mZoomDimension;
    }
    
    public void setZoomDimension(int width, int height) {
        
        mZoomDimension.width = width;
        mZoomDimension.height = height;
    }
    
    public RectF getZoomView() {
        
        return mZoomView;
    }
    
    public void setZoomView(float x, float y, float width, float height)
    {
        if (width <= 10.0f || height <= 10.0f)
            return;
        
        if (width == mZoomDimension.width && height == mZoomDimension.height)
        {
            x = 0.0f;
            y = 0.0f;
        }
        
        mZoomView.set(x, y, x + width, y + height);

        setChanged();
        notifyObservers(); // invoke MUST AFTER setChanged() for valid

    }
    
    public void setZoomViewPanTile(float panX, float panY) {

        float width = mZoomView.width();
        float height = mZoomView.height();
        float l = (int) (mZoomView.left + panX);
        float t = (int) (mZoomView.top + panY);
        
        if (l < 0f)
            l = 0f;
        if (t < 0f)
            t = 0f;
        if (l + width > mZoomDimension.width)
            l = mZoomDimension.width - width;
        if (t + height > mZoomDimension.height)
            t = mZoomDimension.height - height;
        
        mZoomView.set(l, t, l + width, t + height);
        //Log.i(TAG, "zoomView l=" + l + ", t=" + t + ", r=" + r + ", b=" + b + ", w" + mZoomView.width() + ", h=" + mZoomView.height());
        setChanged();
        notifyObservers();
    }

    public float getScaleAdjustShiftX() {
        
        return mScaleAdjustShiftX;
    }
    
    public float getScaleAdjustShiftY() {
        
        return mScaleAdjustShiftY;
    }
}
