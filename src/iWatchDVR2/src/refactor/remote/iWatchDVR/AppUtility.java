package refactor.remote.iWatchDVR;

import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AppUtility {

public static ComponentName getApplicationStatus(Context context, boolean[] isForeground, int[] flag) {
        
        ActivityUtil util = new ActivityUtil(context);
        ComponentName component = null;
  
        RunningAppProcessInfo app = util.isForegroundApp();

        if (app != null) {
            // the program comes into foreground
            
            component = util.getTopActivityForApp(app);
            isForeground[0] = true;
        }
        else {

            app = util.isBackgroundApp();
            if (app != null) {  
                // the program comes into background

                component = util.getTopActivityForApp(app);
                flag[0] |= Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT;
            }
        }
        
        if (component == null) {
            // the program is finished, re-launch main activity
            
            component = new ComponentName(context.getPackageName(), 
                    context.getString(R.string.rootPackName) + ".RemoteDVRActivity");
        }

        return component;
    }


    public static boolean getTopActivity(Context context, ComponentName[] topActivity) {
        
        ActivityUtil util = new ActivityUtil(context);
        RunningAppProcessInfo app = util.isForegroundApp();
        if (app == null)
            return false;
        
        topActivity[0] = util.getTopActivityForApp(app);
        return true;
    }

}
