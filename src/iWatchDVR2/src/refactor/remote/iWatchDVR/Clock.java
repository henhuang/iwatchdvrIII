package refactor.remote.iWatchDVR;

import java.util.Calendar;

import peersdk.TimeSpan;
import refactor.remote.iWatchDVR.widget.Wheel.OnWheelChangedListener;
import refactor.remote.iWatchDVR.widget.Wheel.WheelView;
import refactor.remote.iWatchDVR.widget.Wheel.adapters.NumericWheelAdapter;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

public class Clock extends FrameLayout
{
    public static final String TAG = "__Clock__";

    private WheelView m_hour;
    private WheelView m_min;
    private ClockHourNumericAdapter   m_hourAdapater;
    private ClockMinuteNumericAdapter m_minuteAdapter;
    private boolean                   m_forceRefresh = false;

    protected TimeSpan[] m_recordTime;

    public Clock(Context context) {
        this(context, null);
    }
    
    public Clock(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Clock(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.clock,
                         this, // we are the parent
                         true);
        
        Calendar calendar = Calendar.getInstance();

        m_hour = (WheelView) findViewById(R.id.clock_hour);
        m_min  = (WheelView) findViewById(R.id.clock_min); 
        
        
        OnWheelChangedListener hourlistener = new OnWheelChangedListener() {
        
            public void onChanged(WheelView wheel, int oldValue, int newValue) {

                refeshMinuteViewByHour(newValue);
                
                //Log.i("TAG", "hourlistener onChanged isplay=" + Boolean.toString(m_minuteAdapter.getPlayable())
                //        + ", " + newValue);
            }
        };
        
        OnWheelChangedListener minutelistener = new OnWheelChangedListener() {
            
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                
                //Log.i("TAG", "minutelistener onChanged isplay=" + Boolean.toString(m_minuteAdapter.getPlayable())
                //        + ", " + m_min.getCurrentItem());
            }
        };

        // hour
        int curHour = calendar.get(Calendar.HOUR_OF_DAY);
        m_hourAdapater = new ClockHourNumericAdapter(context, 0, 23, curHour, this);
        
        m_hour.setViewAdapter(m_hourAdapater);
        m_hour.setCurrentItem(curHour);
        m_hour.addChangingListener(hourlistener);

    
        // min
        int curMin = calendar.get(Calendar.MINUTE);
        m_minuteAdapter = new ClockMinuteNumericAdapter(context, 0, 59, curMin, this);
        m_min.setViewAdapter(m_minuteAdapter);
        m_min.setCurrentItem(curMin);
        m_min.addChangingListener(minutelistener);
    }
    
    protected void refeshMinuteViewByHour(int hour) {
        
        m_minuteAdapter.setCurrentHour(hour);
        m_min.invalidateWheel(false);
    }
    
    public void setNowTime()
    {
        Calendar calendar = Calendar.getInstance();
        int curHour = calendar.get(Calendar.HOUR_OF_DAY);
        int curMin = calendar.get(Calendar.MINUTE);
        
        m_min.setCurrentItem(curMin);
        m_hour.setCurrentItem(curHour);
        
        m_hour.invalidateWheel(false);
        refeshMinuteViewByHour(curHour);
    }
    
    public void setTime(int hour, int minute)
    {
        m_min.setCurrentItem(minute);
        m_hour.setCurrentItem(hour);
        
        m_hour.invalidateWheel(false);
        refeshMinuteViewByHour(hour);
    }

    public int getHour()
    {
        return m_hour.getCurrentItem();
    }
    
    public int getMin()
    {
        return m_min.getCurrentItem();
    }

    public void refreshByCalendar(Calendar c)
    {
        m_hour.setCurrentItem(c.get(Calendar.HOUR_OF_DAY));
        m_min.setCurrentItem(c.get(Calendar.MINUTE));
    }
    
    public void setRecordTime(TimeSpan[] span) {
        
        m_recordTime = span;
        m_forceRefresh = true;
    }
    
    public TimeSpan[] getRecordTime() {
        return m_recordTime;
    }

    
    /**
     * Adapter for numeric wheels. Highlights the current value.
     */
    private class ClockHourNumericAdapter extends NumericWheelAdapter
    {
        protected static final String TAG = "__ClockHourNumericAdapter__";
        
        // Index of current item
        int currentItem;
        // Index of item to be highlighted
        int currentValue;
        // Parent of the adapter of the view 
        Clock mParent;

                  
        /**
         * Constructor
         */
        public ClockHourNumericAdapter(Context context, int minValue, int maxValue, int current, Clock parent)
        {
            super(context, minValue, maxValue);
            this.currentValue = current;
            setTextSize(context.getResources().getInteger(R.integer.textSize));
            
            mParent = parent;
        }
        

        @Override
        protected void configureTextView(TextView view)
        {
            super.configureTextView(view);
            
            // highlight current value
            if (currentItem == currentValue)
            {
                //Log.i(TAG, "configureTextView=" + currentItem);
                //view.setTextColor(0xFF0000F0);
            }
            view.setTypeface(Typeface.SANS_SERIF);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {

            currentItem = index;
            View v = super.getItem(index, cachedView, parent);
            ((TextView) v).setTextColor(Color.BLACK);
            
            if (mParent.getRecordTime() == null)
                return v;
            
            TimeSpan[] time = mParent.getRecordTime();
            if (time == null || time.length == 0)
                return v;

            for (int i = 0; i < time.length; i++) {
                
                if (index == time[i].Hours()) {

                    ((TextView) v).setTextColor(Color.RED);
                    return v;
                }
            }

            return v;
        }
    }
    
    /**
     * Adapter for numeric wheels. Highlights the current value.
     */
    private class ClockMinuteNumericAdapter extends NumericWheelAdapter
    {
        protected static final String TAG = "__ClockMinuteNumericAdapter__";
        
        // Index of current item
        int currentItem;
        // Index of item to be highlighted
        int currentValue;
        // Parent of the adapter of the view 
        Clock mParent;

        boolean mPlayable;
        int mHour = -1;

        public void setCurrentHour(int hour) {
            
            if (mHour == hour)
                return;
            mHour = hour;
        }
        
        /**
         * Constructor
         */
        public ClockMinuteNumericAdapter(Context context, int minValue, int maxValue, int current, Clock parent)
        {
            super(context, minValue, maxValue);
            this.currentValue = current;
            setTextSize(context.getResources().getInteger(R.integer.textSize));
            
            mParent = parent;
        }
        
        public boolean getPlayable() {
            
            return mPlayable;
        }
        
        @Override
        protected void configureTextView(TextView view)
        {
            super.configureTextView(view);
            
            // highlight current value
            if (currentItem == currentValue)
            {
                //Log.i(TAG, "configureTextView=" + currentItem);
                //view.setTextColor(0xFF0000F0);
            }
            view.setTypeface(Typeface.SANS_SERIF);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {

            currentItem = index;
            
            View v = super.getItem(index, cachedView, parent);
            ((TextView) v).setTextColor(Color.BLACK);
            
            if (mParent.getRecordTime() == null)
                return v;
            
            TimeSpan[] time = mParent.getRecordTime();
            if (time == null || time.length == 0)
                return v;
            
            if (mHour < 0)
                return v;
            
            for (int i = 0; i < time.length; i++) {

                if (mHour != time[i].Hours())
                    continue;
                
                if (index == time[i].Minutes()) {
                    
                     
                    ((TextView) v).setTextColor(Color.RED);
                    //v.setTag("play", new Boolean(true));
                    //mPlayable = true;
                    return v;
                }
            }

            return v;
        }
    }
}

