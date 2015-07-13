package refactor.remote.iWatchDVR;

import refactor.remote.iWatchDVR.application.CanvasView;
import refactor.remote.iWatchDVR.application.Visuals;
import android.util.Log;

public abstract class VisualActivity extends BaseDVRActivity {
final static String TAG = "__VisualActivity__";
    
    int mDivide = 1;    // to restore visuals's state
    int mVisualID = 0;  // to restore visuals's state
    Visuals mVisual;

    public abstract void Raise_VisualChanged(int divide);
    public abstract void Raise_VisualChanged(int divide, boolean arg);
    public abstract void Raise_VisualChanged();

    
    private void storeVisual(int divide, int visualID) {
        mDivide = divide;
        mVisualID = visualID;
    }

    public void NextVisual() {
        Log.i(TAG, "NextVisual(update visual)*: " + mDivide);
        mVisual.NextVisual(mDivide, mVisualID); // update visual
        Raise_VisualChanged();
    }

    public void NextVisual(int divide) {
        Log.i(TAG, "NextVisual(update visual): " + divide);
        mVisual.NextVisual(divide); // update visual
        storeVisual(mVisual.Divide(), mVisual.VisualID());
        Raise_VisualChanged(divide);
    }
    
    public void NextVisual(int divide, int visualID) {
        Log.i(TAG, "NextVisual(update visual): " + divide + ", " + visualID);
        mVisual.NextVisual(divide, visualID); // update visual
        storeVisual(mVisual.Divide(), mVisual.VisualID());
        Raise_VisualChanged(divide);
    }
    
    public void NextVisual1x1(int channelId) {
        Log.i(TAG, "NextVisual1x1: channelId=" + channelId);
        mVisual.NextVisual1x1(channelId); // update visual
        storeVisual(mVisual.Divide(), mVisual.VisualID());
        Raise_VisualChanged(Visuals.Divide_1x1);
    }
    
    public void NextVisual2x2(int visualId) {
        Log.i(TAG, "NextVisual1x1: visualId=" + visualId);
        mVisual.NextVisual2x2(visualId); // update visual
        storeVisual(mVisual.Divide(), mVisual.VisualID());
        Raise_VisualChanged(Visuals.Divide_2x2);
    }
    
    public void NextVisual3x3(int visualId) {
        Log.i(TAG, "NextVisual3x3: visualId=" + visualId);
        mVisual.NextVisual3x3(visualId); // update visual
        storeVisual(mVisual.Divide(), mVisual.VisualID());
        Raise_VisualChanged(Visuals.Divide_3x3);
    }

    public void NextVisual4x4(int visualId) {
        Log.i(TAG, "NextVisual4x4: visualId=" + visualId);
        mVisual.NextVisual4x4(visualId); // update visual
        storeVisual(mVisual.Divide(), mVisual.VisualID());
        Raise_VisualChanged(Visuals.Divide_4x4);
    }
    
    ///////////////////////////////////////////////////////////
    
    protected void SetVisual(CanvasView canvas, int channelCount){};
    public void SetVisual(CanvasView canvas){}
    
    public Visuals getVisual() {
        return mVisual;
    }

}
