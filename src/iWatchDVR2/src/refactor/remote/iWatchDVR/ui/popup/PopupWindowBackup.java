package refactor.remote.iWatchDVR.ui.popup;

import java.util.Calendar;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import refactor.remote.iWatchDVR.R;
import refactor.remote.iWatchDVR.R.id;
import refactor.remote.iWatchDVR.widget.QuickAction.QuickAction;

public class PopupWindowBackup extends QuickAction {
    
    public final static String TAG = "__PopupWindowBackup__";
    
    private boolean mMark = false;
    
    public PopupWindowBackup(Context context, int layoutID) {
        
        super(context, layoutID);        
        Initialize();
    }

    private void Initialize() {
        
        TextView goBackup = (TextView) mRootView.findViewById(R.id.button_backup_goBackup);
        goBackup.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                
                dismiss();
                /*
                Intent intent = new Intent();
                DisplayActivity context = (DisplayActivity) v.getContext();
                Preference preference = context.getPreference();
                long[] backupTimeDuration = new long[2];
                if (!mMark) {
                    Calendar c = Calendar.getInstance();
                    backupTimeDuration[0] = c.getTimeInMillis() - BackupActivity.DEAULT_BACKUP_DURRATION;
                    backupTimeDuration[1] = c.getTimeInMillis() ;
                }
                else {
                    backupTimeDuration[0] = preference.getMarkStartTime();
                    backupTimeDuration[1] = preference.getMarkEndTime();
                }

                intent.putExtra(BackupActivity.TIMESET, backupTimeDuration);
                intent.setClass(v.getContext(), BackupActivity.class);
                v.getContext().startActivity(intent);
                */
            }
        });
        
        TextView  markIn = (TextView) mRootView.findViewById(R.id.button_backup_markIn);
        markIn.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                /** TODO:
                if (!mMark)
                    mMark = true;
                DisplayActivity context = (DisplayActivity) v.getContext();
                int chbits = context.getPreference().getVideoChbits();
                int index = chbitsToChannel(chbits);
                DateTime time = context.getMeidaDispatcher().getVideoTimeStamp(index);
                
                Log.i(TAG, "markIn=" + time.getYear() + "-" + time.getMonth() + "-" + time.getDay()
                        + " " + time.getHour() + ":" + time.getMinute() + ":" + time.getSecond());
                
                Calendar c = Calendar.getInstance();
                c.set(time.getYear(), time.getMonth()-1, time.getDay(), time.getHour(), time.getMinute(), time.getSecond());
                context.getPreference().setMarkStartTime(c.getTimeInMillis());
                */
            }
        });
        
        TextView  markOut = (TextView) mRootView.findViewById(R.id.button_backup_markOut);
        markOut.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                /**
                DisplayActivity context = (DisplayActivity) v.getContext();
                int chbits = context.getPreference().getVideoChbits();
                int index = chbitsToChannel(chbits);
                DateTime time = context.getMeidaDispatcher().getVideoTimeStamp(index);
                
                Log.i(TAG, "markOut=" + time.getYear() + "-" + time.getMonth() + "-" + time.getDay()
                        + " " + time.getHour() + ":" + time.getMinute() + ":" + time.getSecond());
                
                Calendar c = Calendar.getInstance();
                c.set(time.getYear(), time.getMonth()-1, time.getDay(), time.getHour(), time.getMinute(), time.getSecond());
                context.getPreference().setMarkEndTime(c.getTimeInMillis());
                */
            }
        });
    }
    
    private int chbitsToChannel(int chbits) {
        
        for (int i = 0; i < 32; i++) {

            if (((chbits & (1 << i)) >> i) == 1) {
                return i;
            }
        }
        return -1;
    }
}
