package refactor.remote.iWatchDVR;

import java.util.Vector;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;

abstract interface INotification {
    
    public static final String NotificationFromCloud     = "NlCoud";
    public static final String NotificationSender        = "NSender";
    public static final String NotificationId            = "NId";
    public static final String NotificationWatchType     = "NType";
    public static final String NotificationWatchDateTime = "NDateTime";
    public static final String NotificationWatchChannel  = "NChannel";
    public static final String NotificationAlert         = "NAlert";

    Vector<AlertDialog> mNotifications = new Vector<AlertDialog>();
    
    abstract void onNotificationConfirm(final Intent intent, Context context);
    abstract void doNotification(Intent intent);
    
    int[] mState = null;
}
