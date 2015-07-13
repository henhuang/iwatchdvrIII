package refactor.remote.iWatchDVR;

import org.json.JSONException;
import org.json.JSONObject;

import refactor.remote.iWatchDVR.application.RemoteDVRApplication;
import refactor.remote.iWatchDVR.database.DVRHosts;

import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {

    public static final String TAG = "__GCMIntentService__";
    
    public static final String SENDER_ID = "286350667029";

    protected String m_registerId;
    
    protected static int m_notifyId = 0x00;
    
    public GCMIntentService() {
        
        super(SENDER_ID);
    }

    
    @Override
    protected void onError(Context arg0, String arg1) {

        Log.i(TAG, "onError:" + arg1);
    }

    
    
    @Override
    protected void onMessage(Context context, Intent intent) {
        // receive message from GCM server
        // context is RemoteDVRApplication
       
        String alert                   = "";
        String uuid                    = "" ;
        PushNotificationWatchType type = new PushNotificationWatchType();
        long   datetime                = 0;
        int    channel                 = 0;

        try {
            JSONObject japs = new JSONObject(intent.getExtras().getString("aps"));
            alert = japs.getString("alert");
            
            JSONObject jiwatch = new JSONObject(intent.getExtras().getString("iwatch"));
            uuid     = jiwatch.getString("uuid");
            datetime = jiwatch.getLong("datetime") * 1000;
            channel  = jiwatch.getInt("channel");
            type.parse(jiwatch.getString("type"));
            
        } catch (JSONException e) {
            
            e.printStackTrace();
        }

        Uri uri = ((RemoteDVRApplication) context).getURIByUuid(uuid);
        Log.i(TAG, "alert="    + alert);
        Log.i(TAG, "uuid="     + uuid);
        Log.i(TAG, "datetime=" + datetime);
        Log.i(TAG, "notify: channel="  + channel);
        Log.i(TAG, "type="     + type.toString());
        Log.i(TAG, "uri(uuid)=" + uri);
        Cursor c = context.getContentResolver().query(uri, null, null, null, null);
        
        if (!c.moveToNext()) {
            // if the device isn't on the DVR list, do nothing 
            Log.i(TAG, "no match device on the list, uuid=" + uuid);
            return;
        }

        ///////////////////////////////////////////////////////
        
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE); 
        
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle(getString(R.string.app_name));
        
        switch (type.getType()) {
        case PushNotificationWatchType.Type_Live:
        case PushNotificationWatchType.Type_Play:
            builder.setContentInfo(type.toString());
            break;
        }

        builder.setContentText(alert);
        builder.setTicker(alert);
        builder.setSmallIcon(R.drawable.ic_launcher_actionbar); // MUST set to enable NotificationManager.notify()
        
        boolean[] isForeground = new boolean[] { false };
        int[] flag = new int[] { Intent.FLAG_ACTIVITY_NEW_TASK };
        ComponentName component = AppUtility.getApplicationStatus(context, isForeground, flag);

        Intent notifyIntent = new Intent();
        notifyIntent.setComponent(component);

        notifyIntent.putExtra(DVRHosts.DVR._ID,               Integer.toString(c.getInt(c.getColumnIndex(DVRHosts.DVR._ID))));
        notifyIntent.putExtra(DVRHosts.DVR.USER,              c.getString(c.getColumnIndex(DVRHosts.DVR.USER)));
        notifyIntent.putExtra(DVRHosts.DVR.PASSWORD,          c.getString(c.getColumnIndex(DVRHosts.DVR.PASSWORD)));
        notifyIntent.putExtra(DVRHosts.DVR.HOST,              c.getString(c.getColumnIndex(DVRHosts.DVR.HOST)));
        notifyIntent.putExtra(DVRHosts.DVR.PORT,              c.getString(c.getColumnIndex(DVRHosts.DVR.PORT)));
        notifyIntent.putExtra(INotification.NotificationFromCloud,       true);
        notifyIntent.putExtra(INotification.NotificationWatchChannel,    channel);
        notifyIntent.putExtra(INotification.NotificationWatchDateTime,   datetime);
        notifyIntent.putExtra(INotification.NotificationId,              m_notifyId);
        notifyIntent.putExtra(INotification.NotificationWatchType,       type.getType());
        notifyIntent.putExtra(INotification.NotificationAlert,           alert);

        notifyIntent.setFlags(flag[0]);

        if (isForeground[0]) {
            // send notification to both the program and status bar
            context.startActivity(notifyIntent);
        }
        
        /**
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(RemoteDVRActivity.class);
        stackBuilder.addNextIntent(notifyIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                    m_notifyId,
                    PendingIntent.FLAG_ONE_SHOT
                );
        */
        
        //
        // the application is on background, send notification to status bar
        //
        switch (type.getType()) {
        
        case PushNotificationWatchType.Type_Live:
        case PushNotificationWatchType.Type_Play:
            {
                PendingIntent resultPendingIntent = PendingIntent.getActivity(
                    this, 
                    m_notifyId,                     // request code MUST be different.
                                                    // if it's the same value, only one intent is to be received
                                                    // for multiple notifications to launch activity.
                                                    // scenario: we got three notifications on notification bar
                                                    // not matter which notification was clicked with different
                                                    // passed notifyId to notify(), only the first of 
                                                    // there notification is able to launch activity
                    notifyIntent, 
                    PendingIntent.FLAG_ONE_SHOT);
            
            
                builder.setContentIntent(resultPendingIntent);
                builder.setAutoCancel(true); // MUST add PendingIntent to enable
            }
            break;
        }
        builder.setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND);

        Notification n = builder.build();
        notificationManager.notify(m_notifyId++, n);
    }

    @Override
    protected void onRegistered(Context arg0, String arg1) {
        // receive resiterID from GCM server
        Log.i(TAG, "onRegistered:" + arg1);
    }

    @Override
    protected void onUnregistered(Context arg0, String arg1) {
        // resiterID was canceled, inform DVR to revoke the resiterID
        
    }

}
