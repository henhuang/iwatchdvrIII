package refactor.remote.iWatchDVR.application;

public class ViewA {

    public static final int Divide1X1 = 0x0010;
    public static final int Divide2x2 = 0x0011;
    public static final int Divide3x3 = 0x0012;
    public static final int Divide4x4 = 0x0013;

    protected int mChannel = -1;
    protected int mId = -1;
    protected int mDivide;
    protected int mWidth;
    protected int mHeight;
    protected int mX;
    protected int mY;
    protected String mTag;
    
    ViewA(int id, int channel, int x, int y, int width, int height) {
        
        mId      = id;
        mChannel = channel;
        mWidth   = width;
        mHeight  = height;
        mX       = x;
        mY       = y;
    }
    
    ViewA(int id, String tag, int x, int y, int width, int height) {
        
        mId      = id;
        mTag     = tag;
        mWidth   = width;
        mHeight  = height;
        mX       = x;
        mY       = y;
    }

    ViewA(int id) {
        mId = id;
    }

    public int Channel() {
        return mChannel;
    }
    
    public int Id() {
        return mId;
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
    
    public String Tag() {
        return mTag;
    }
    
    public boolean Empty() {
        return mChannel < 0;
    }
}

