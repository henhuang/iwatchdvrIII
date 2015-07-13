package refactor.remote.iWatchDVR.database;

import android.net.Uri;
import android.provider.BaseColumns;

public class DVRHosts {
    
    public static final String AUTHORITY = "remote.iWatchDVR.DVRProvider";
    public static final String PATH      = "dvr";
    
    public static final int NONE         = 0xff;
    public static final int ADD          = 0x01;
    public static final int DELETE       = 0x02;
    public static final int UPDATE       = 0x03;
    
    public static final String MODE       = "mode";
    
    private DVRHosts(){}  
    

    public static final class DVR implements BaseColumns{  
        
        private DVR(){}  
             

        //public static final int DVR_ID       = 0xaa;
        //public static final int DVR_UUID     = 0xab;
        //public static final int DVR          = 0xbb;


        
        public static final String HAS_UPDATE = "hasUpdate";
        public static final String DO_CONNECT = "doConnect";
        
        public static final String NAME       = "name";  
        public static final String HOST       = "host";  
        public static final String PORT       = "port";  
        public static final String USER       = "user";  
        public static final String PASSWORD   = "password";
        public static final String CHANNEL    = "channel";
        public static final String UUID       = "uuid";
        public static final String VERSION    = "version";

        public static final String IS_NOTIFY  = "isNotify";

        
        public static final int DVRDelete = 0x01;
        public static final int DVRAdd    = 0x02;
        public static final int DVRUpdate = 0x03;
        public static final int DVRQuery  = 0x04;
        
    }  
}
