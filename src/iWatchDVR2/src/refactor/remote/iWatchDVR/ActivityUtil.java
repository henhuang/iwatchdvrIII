package refactor.remote.iWatchDVR;

import java.util.Iterator;
import java.util.List;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;


public class ActivityUtil{

    protected Context mContext;
    protected ActivityManager mActivityManager ;
    public static final String TAG = "__ActivityUtil__";
    
    ActivityUtil(Context context) {
        
        mContext = context;
    }
    
    protected RunningAppProcessInfo isBackgroundApp() {
        
        RunningAppProcessInfo info   = null;

        if (mActivityManager == null)
            mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);

        List <RunningAppProcessInfo> l = mActivityManager.getRunningAppProcesses();
        Iterator <RunningAppProcessInfo> i = l.iterator();
        
        while (i.hasNext()) {
            
            info = i.next();
            if (info.processName.compareTo(mContext.getPackageName()) == 0)
                return info;
        }
        return null;
    }
    
    protected RunningAppProcessInfo isForegroundApp() {
        
        RunningAppProcessInfo info   = null;

        if (mActivityManager == null)
            mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        
        List <RunningAppProcessInfo> l = mActivityManager.getRunningAppProcesses();
        Iterator <RunningAppProcessInfo> i = l.iterator();
        
        while (i.hasNext()) {
            
            info = i.next();
            
            //Log.i(TAG, "processName=" + info.processName);
            if (info.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                    && info.processName.compareTo(mContext.getPackageName()) == 0) 
                return info;
        }
        return null;
    }

    protected RunningAppProcessInfo getForegroundApp() {

        RunningAppProcessInfo info   = null;

        if (mActivityManager == null)
            mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        
        List <RunningAppProcessInfo> l = mActivityManager.getRunningAppProcesses();
        Iterator <RunningAppProcessInfo> i = l.iterator();
        
        while (i.hasNext()) {
            
            info = i.next();
            
            //Log.i(TAG, "processName=" + info.processName);
            if (info.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) 
                return info;
        }
        return null;
    }
    
    
    private boolean isStillActive(RunningAppProcessInfo process, ComponentName activity) {
    
        // activity can be null in cases, where one app starts another. for example, aster
        // starting rock player when a move file was clicked. we don't have an activity then,
        // but the package exits as soon as back is hit. so we can ignore the activity
        // in this case
        if(process == null)
            return false;

        RunningAppProcessInfo currentFg = getForegroundApp();
        ComponentName currentActivity = getTopActivityForApp(currentFg);

        if (currentFg != null && currentFg.processName.equals(process.processName) &&
                (activity == null || currentActivity.compareTo(activity) == 0))
            return true;

        //Log.i(TAG, "isStillActive returns false - CallerProcess: " + process.processName + " CurrentProcess: "
        //        + (currentFg==null ? "null" : currentFg.processName) + " CallerActivity:" + (activity==null ? "null" : activity.toString())
        //        + " CurrentActivity: " + (currentActivity==null ? "null" : currentActivity.toString()));
        return false;
    }
    

    
    public ComponentName getTopActivityForApp(RunningAppProcessInfo target) {
        
        ComponentName result = null;
        ActivityManager.RunningTaskInfo info;

        if (target == null)
            return null;

        if (mActivityManager == null)
            mActivityManager = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);
        
        List <ActivityManager.RunningTaskInfo> l = mActivityManager.getRunningTasks(9999);
        Iterator <ActivityManager.RunningTaskInfo> i = l.iterator();

        while (i.hasNext()){
            
            info = i.next();
            if (info.baseActivity.getPackageName().equals(target.processName)) {
                
                result = info.topActivity;
                break;
            }
        }

        return result;
    }
    
    private boolean isRunningService(String processname){
        
        if (processname == null || processname.length() == 0)
            return false;

        RunningServiceInfo service;

        if(mActivityManager==null)
            mActivityManager = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List <RunningServiceInfo> l = mActivityManager.getRunningServices(9999);
        Iterator <RunningServiceInfo> i = l.iterator();
        while(i.hasNext()){
            service = i.next();
            if(service.process.equals(processname))
                return true;
        }

        return false;
    }

}
