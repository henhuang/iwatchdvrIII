package refactor.remote.iWatchDVR;

import peersdk.TimeSpan;
import refactor.remote.iWatchDVR.dvr.PeerDVR;
import refactor.remote.iWatchDVR.widget.Calendar.CalendarView;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class TimeSearchActivity extends BaseDVRActivity {

    final static String TAG = "__TimeSearchActivity__";

    final static int Calendar_MonthChanged = 0x1;
    final static int Calendar_SelectedDayChanged = 0x2;
    final static int Calendar_SetTodayMark = 0x3;
    final static int Prompt_NoRecord = 0x4;

    TimeSpan[] mRecordedMinutesOfDay;
    int[] mRecordedDaysOfMonth;
    
    private CalendarView mCalendar;
    private Clock        mClock;
    private boolean      mPlayable;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_time_search);
        
        InitUIHandler();

        mApplication.AddPeerListener(this, TimeSearchActivity.class.getName());

        PopupWaitingDialog();

        /*
        if (savedInstanceState == null) {
            mApplication.dvr().GetRecordList();
        }
        */
        
        InitCalendarClock();
    }
    
    @Override
    public void onDestroy(){
        super.onDestroy();
        mApplication.RemovePeerListener(TimeSearchActivity.class.getName());
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {                                                     
        getMenuInflater().inflate(R.menu.activity_time_search, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            break;
            
        case R.id.action_play:
            Intent intent = new Intent(this, PlayActivity.class);
            startActivityForResult(intent, 0x123);
            //startActivity(intent);
            break;
        }
        
        
        return (super.onOptionsItemSelected(item));
    }

    private void InitCalendarClock() {
        mCalendar = (CalendarView) findViewById(R.id.calendar);
        mClock    = (Clock) findViewById(R.id.clock);

        mCalendar.setOnMonthChangedListener(new CalendarView.OnMonthChangedListener() {

            @Override
            public void onMonthChanged(CalendarView view) {
                PopupWaitingDialog();
                mApplication.dvr().GetRecordedDaysOfMonth(view.Year(), view.Month()+1);
            }
        });

        mCalendar.setOnSelectedDayChangedListener(new CalendarView.OnSelectedDayChangedListener(){

            @Override
            public void onSelectedDayChanged(CalendarView view) {
                int day = view.Day();
                for (int i = 0; i < mRecordedDaysOfMonth.length; i++) {
                    if (day == mRecordedDaysOfMonth[i]) {
                        PopupWaitingDialog();
                        mApplication.dvr().GetRecordedMinutesOfDay(view.Year(), view.Month()+1, day);
                        return;
                    }
                }
                mClock.setRecordTime(null);
                mClock.setNowTime();
            }
        });
        
        mCalendar.setDayMark(mCalendar.Year(), mCalendar.Month(), mCalendar.Day());
        
        PeerDVR dvr = mApplication.dvr();
        int year = mCalendar.Year();
        int month = mCalendar.Month() + 1;
        int day = mCalendar.Day();

        dvr.GetRecordedDaysOfMonth(year, month);
        dvr.GetRecordedMinutesOfDay(year, month, day);
    }

    //////////////// BaseDVRAcitivity ////////////////////////////
    
    
    @Override
    protected void HandleMessage(Message msg) {
        switch (msg.what) {
        case Calendar_MonthChanged:
            Log.i(TAG, "Calendar_MonthChanged");
            mCalendar.setDaysEventDrawable(mRecordedDaysOfMonth);
            break;
            
        case Calendar_SelectedDayChanged:
            Log.i(TAG, "Calendar_SelectedDayChanged");
            mClock.setRecordTime(mRecordedMinutesOfDay);
            mClock.setNowTime();
            System.gc(); // avoid to much timespan's global reference not release
            break;
        }
    }


    @Override
    public void OnGetRecordedMinutesOfDay(TimeSpan[] arg) {
        DismissWaitingDialog();
        
        synchronized (this) {
            mRecordedMinutesOfDay = arg;
        }
        if (arg == null || arg.length == 0)
            return;
        SendMessage2UI(Calendar_SelectedDayChanged);
    }

    @Override
    public void OnGetRecordedDaysOfMonth(int[] arg) {
        DismissWaitingDialog();
        
        synchronized (this) {
            mRecordedDaysOfMonth = arg;
        }
        if (arg == null || arg.length == 0)
            return;
        SendMessage2UI(Calendar_MonthChanged);
    }
}
