package refactor.remote.iWatchDVR.application;

import java.io.File;

import refactor.remote.iWatchDVR.R;

import android.content.Context;
import android.os.Environment;

public class Utility {

    public static int GetScreenRrientation(Context context) {
        return context.getResources().getConfiguration().orientation;
    }
    
    public static int GetAvailableProcessor() {
        return Runtime.getRuntime().availableProcessors();
    }
    
    public static boolean IsExternalStorageMounted() {
        return Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }
    
    public static String GetExternalStorageRoot() {
        if (IsExternalStorageMounted()) {
            File storage = Environment.getExternalStorageDirectory();
            return storage.getPath();
        }    
        return null;
    }
    
    public static String GetDefaultRecordFolder(Context context) {    
        String root = GetExternalStorageRoot();
        return root == null ? root : root + "/" + context.getResources().getString(R.string.app_name);
    }
    
}