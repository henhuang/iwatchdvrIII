package refactor.remote.iWatchDVR.application;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

public class Visuals {
    final public String TAG = "__Visuals__";
    
    final static public int Divide_1x1 = 1;
    final static public int Divide_2x2 = 4;
    final static public int Divide_3x3 = 9;
    final static public int Divide_4x4 = 16;
    
    private Context mContext;
    private int mChannelCount;
    
    ArrayList<Visual> _1x1;
    ArrayList<Visual> _2x2;
    ArrayList<Visual> _3x3;
    ArrayList<Visual> _4x4;
    int _x;
    int _y;
    int _w;
    int _h;
    
    int _dx;
    int _dy;
    
    int mVisualID;
    int mDivide;

    public Visuals (Context context, int channelCount, int x, int y, int width, int height, int defaultDivide, int defaultVisualID) {
        
        mContext = context;

        _x = x;
        _y = y;
        if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            _w = width;
            _h = width * 9 / 16;
            _dx = x;
            _dy = (height - _h) / 2;
        }
        else {
            _w = width;
            _h = height;
            _dx = x;
            _dy = y;  
        }

        mDivide = defaultDivide;
        mVisualID = defaultVisualID;
        
        mChannelCount = channelCount;
        Initialize();
    }
    
    public int X() {
        return _x;
    }
    
    public int Y() {
        return _y;
    }
    
    public int Width() {
        return _w;
    }
    
    public int Height() {
        return _h;
    }
    
    public int dX() {
        return _dx;
    }
    
    public int dY() {
        return _dy;
    }
    
    protected void Initialize() {
        if (mChannelCount > 0)
            Init_1x1();

        if (mChannelCount > 1)
            Init_2x2();

        if (mChannelCount > 4)
            Init_3x3();

        if (mChannelCount > 9)
            Init_4x4();
    }


    protected Visual CreateVisual(int colStride, int start, int count, int channelCount) {
        int w = _w / colStride;
        int h = _h / colStride;
        Visual visual = new Visual(_x, _y, _w, _h);
        for (int i = 0; i < count; i++) {
            
            
            int ch = start + i;
            if (ch >= channelCount)
                ch = -1;
            int offsetX = i % colStride;
            int offsetY = i / colStride;
            
            ViewA view = new ViewA(ch, ch, _x + w*offsetX, _y + h*offsetY, w, h);
            visual.Add(view);
        }
        return visual;
    }
    
    protected void Init_1x1() {

        int divide = 1;
        int stride = 1;

        _1x1 = new ArrayList<Visual>();

        for (int i = 0; i < mChannelCount; i++)
            _1x1.add(CreateVisual(stride, i*divide, divide, mChannelCount));
    }
       
    protected void Init_2x2() {
        
        int divide = 4;
        int stride = 2;
        int count = (int) Math.ceil((double) mChannelCount / (double)divide);
        
        _2x2 = new ArrayList<Visual>();

        for (int i = 0; i < count; i++)
            _2x2.add(CreateVisual(stride, i*divide, divide, mChannelCount));
    }
    
    protected void Init_3x3() {

        int divide = 9;
        int stride = 3;
        int count = (int) Math.ceil((double) mChannelCount / (double)divide);
        
        _3x3 = new ArrayList<Visual>();

        for (int i = 0; i < count; i++)
            _3x3.add(CreateVisual(stride, i*divide, divide, mChannelCount));
    }
    
    protected void Init_4x4() {
        
        int divide = 16;
        int stride = 4;
        int count = (int) Math.ceil((double) mChannelCount / (double)divide);
        
        _4x4 = new ArrayList<Visual>();

        for (int i = 0; i < count; i++)
            _4x4.add(CreateVisual(stride, i*divide, divide, mChannelCount));
    }

    public Visual getVisual() {
        synchronized(this) {
            switch (mDivide) {
            case 1:
                return _1x1.get(mVisualID);
            case 4:
                return _2x2.get(mVisualID);
            case 8:
            case 9:
                return _3x3.get(mVisualID);
            case 16:
                return _4x4.get(mVisualID);
            }
            
            Log.e(TAG, "getVisual is null, divide=" + mDivide);
            return null;
        }
    }
       
    public ArrayList<ViewA> getView() {
        synchronized(this) {

            switch (mDivide) {
            case 1:
                return _1x1.get(mVisualID).Views();
            case 4:
                return _2x2.get(mVisualID).Views();
            case 9:
                return _3x3.get(mVisualID).Views();
            case 16:
                return _4x4.get(mVisualID).Views();
            }
            Log.e(TAG, "getView is null, divide=" +mDivide);
            return null;
        }
    }

    public void NextVisual(int divide) {
        synchronized (this) {
            if (mDivide == divide) {
                ArrayList<Visual> visuals = null;
                switch (divide) {
                case 1:
                    visuals = _1x1;
                    break;
                case 4:
                    visuals = _2x2;
                    break;
                case 9:
                case 8:    
                    visuals = _3x3;
                    break;
                case 16:
                    visuals = _4x4;
                    break;
                default:
                    Log.e(TAG, "NextVisual is null, divide=" + divide);
                    return;
                }
                mVisualID = (mVisualID + 1) % visuals.size() == 0 ? 0 : mVisualID + 1;
            }
            else {
                mVisualID = mVisualID * mDivide / divide;
                mDivide = divide;
            }
            Log.i(TAG, "divide=" + mDivide + ", visualID=" + mVisualID);
        }

    }
    
    public void NextVisual(int divide, int visualID) {
        synchronized (this) {
            mDivide = divide;
            mVisualID = visualID;
            Log.i(TAG, "divide=" + mDivide + ", visualID=" + mVisualID);
        }
    }
    
    public void NextVisual1x1(int cameraId) {
        synchronized (this) {
            mDivide = Divide_1x1;
            for (int i = 0; i < _1x1.size(); i++) {
                if (_1x1.get(i).Views().get(0).Id() == cameraId) {
                    mVisualID = i;
                    break;
                }
            }
        }
    }
    
    public void NextVisual2x2(int visualId) {
        synchronized (this) {
            mDivide = Divide_2x2;
            for (int i = 0; i < _2x2.size(); i++) {
                if (i == visualId) {
                    mVisualID = i;
                    break;
                }
            }
        }
    }
    
    public void NextVisual3x3(int cameraId) {
        synchronized (this) {
            mDivide = Divide_3x3;
            for (int i = 0; i < _3x3.size(); i++) {
                mVisualID = i;
                break;
            }
        }
    }
    
    public void NextVisual4x4(int cameraId) {
        synchronized (this) {
            mDivide = Divide_4x4;
            for (int i = 0; i < _4x4.size(); i++) {
                mVisualID = i;
                break;
            }
        }
    }

    public int Divide() {
        return mDivide;
    }
    
    public int VisualID() {
        return mVisualID;
    }
    
}

