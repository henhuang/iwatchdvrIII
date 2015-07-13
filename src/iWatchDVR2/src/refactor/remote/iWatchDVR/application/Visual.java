package refactor.remote.iWatchDVR.application;

import java.util.ArrayList;


public class Visual {

    public static final String TAG = "__Visual__";

    protected ArrayList<ViewA> mViews = new ArrayList<ViewA>();

    
    protected int mWidth;
    protected int mHeight;
    protected int mX;
    protected int mY;
    
    
    public Visual(int x, int y, int width, int height) {    
        mWidth  = width;
        mHeight = height;
        mX      = x;
        mY      = y;
    }
    
    ///////////////////
    
    public void Add(ViewA view) {
        mViews.add(view);
    }
    
    public ArrayList<ViewA> Views() {
        return mViews;
    }

    
    public int Width() {
        return mWidth;
    }
    
    public int Height() {
        return mHeight;
    }
    
    public int X() {
        return mX;
    }
    
    public int Y() {
        return mY;
    }
}
