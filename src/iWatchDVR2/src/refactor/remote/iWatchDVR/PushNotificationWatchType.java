package refactor.remote.iWatchDVR;

public class PushNotificationWatchType {

    static final int Type_Null = 0x0;
    static final int Type_None = 0x1;
    static final int Type_Live = 0x2;
    static final int Type_Play = 0x3;
    
    protected int mType = Type_Null;
    
    public PushNotificationWatchType() {
        
    }
    
    public PushNotificationWatchType(String type) {
    
        parse(type);
    }
    
    protected void parse(String type) {
        
        if (type.equalsIgnoreCase("none"))
            mType = Type_None;
        else if (type.equalsIgnoreCase("live"))
            mType = Type_Live;
        else if (type.equalsIgnoreCase("play"))
            mType = Type_Play;
        else
            mType = Type_Null;
    }
    
    public String toString() {
        
        switch (mType) {
        case Type_None:
            return "none";
        case Type_Live:
            return "live";
        case Type_Play:
            return "play";
        default:
            return "null";
        }
    }
    
    public int getType () {
        
        return mType;
    }
}
