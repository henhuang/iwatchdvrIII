package refactor.remote.iWatchDVR;

import java.util.Calendar;

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

public class ClockTime extends FrameLayout
{
    public static final String TAG = "__ClockTime__";

    private WheelView m_hour;
    private WheelView m_min;
    private WheelView m_sec;

    public ClockTime(Context context)
    {
        this(context, null);
    }
    
    public ClockTime(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public ClockTime(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.clock_time,
                         this, // we are the parent
                         true);
        
        Calendar calendar = Calendar.getInstance();

        m_hour = (WheelView) findViewById(R.id.clock_hour);
        m_min  = (WheelView) findViewById(R.id.clock_min);
        m_sec  = (WheelView) findViewById(R.id.clock_sec); 
        
        OnWheelChangedListener listener = new OnWheelChangedListener()
        {
            public void onChanged(WheelView wheel, int oldValue, int newValue)
            {
            }
        };

        // hour
        int curHour = calendar.get(Calendar.HOUR_OF_DAY);
        m_hour.setViewAdapter(new ClockNumericAdapter(context, 0, 23, curHour, this));
        m_hour.setCurrentItem(curHour);
        m_hour.addChangingListener(listener);
    
        // min
        int curMin = calendar.get(Calendar.MINUTE);
        m_min.setViewAdapter(new ClockNumericAdapter(context, 0, 59, curMin, this));
        m_min.setCurrentItem(curMin);
        m_min.addChangingListener(listener);
        
        // sec
        int curSec = calendar.get(Calendar.MINUTE);
        m_sec.setViewAdapter(new ClockNumericAdapter(context, 0, 59, curSec, this));
        m_sec.setCurrentItem(curMin);
        m_sec.addChangingListener(listener);
    }
    
    public void setNowTime()
    {
        Log.i(TAG, "setNowTime");
        Calendar calendar = Calendar.getInstance();
        int curHour = calendar.get(Calendar.HOUR_OF_DAY);
        int curMin = calendar.get(Calendar.MINUTE);
        int curSec = calendar.get(Calendar.SECOND);
        
        m_hour.setCurrentItem(curHour);
        m_hour.invalidateWheel(false);
        
        m_min.setCurrentItem(curMin);
        m_min.invalidateWheel(false);
        
        m_sec.setCurrentItem(curSec);
        m_sec.invalidateWheel(false);
    }
    
    public void setTime(int hour, int minute, int second)
    {
        Log.i(TAG, "setTime");
        m_hour.setCurrentItem(hour);
        m_hour.invalidate();
        
        m_min.setCurrentItem(minute);
        m_min.invalidate();
        
        m_sec.setCurrentItem(second);
        m_sec.invalidate();
    }

    public int getHour()
    {
        return m_hour.getCurrentItem();
    }
    
    public int getMin()
    {
        return m_min.getCurrentItem();
    }
    
    public int getSec()
    {
        return m_sec.getCurrentItem();
    }
    
    public void refreshByCalendar(Calendar c)
    {
        m_hour.setCurrentItem(c.get(Calendar.HOUR_OF_DAY));
        m_min.setCurrentItem(c.get(Calendar.MINUTE));
    }


    /**
     * Adapter for numeric wheels. Highlights the current value.
     */
    private class ClockNumericAdapter extends NumericWheelAdapter
    {
        // Index of current item
        int currentItem;
        // Index of item to be highlighted
        int currentValue;
        // Parent of the adapter of the view 
        ClockTime mParent;

        
        /**
         * Constructor
         */
        public ClockNumericAdapter(Context context, int minValue, int maxValue, int current, ClockTime parent)
        {
            super(context, minValue, maxValue);
            this.currentValue = current;
            setTextSize(context.getResources().getInteger(R.integer.textSize));

        }
        
        @Override
        protected void configureTextView(TextView view)
        {
            super.configureTextView(view);
            
            // highlight current value
            /*
            if (currentItem == currentValue)
            {
                view.setTextColor(0xFF0000F0);
            }
            */
            view.setTypeface(Typeface.SANS_SERIF);
        }
        
        @Override
        public View getItem(int index, View cachedView, ViewGroup parent)
        {
            currentItem = index;
            View v = super.getItem(index, cachedView, parent);
            ((TextView) v).setTextColor(Color.BLACK);

            return v;
        }
    }

}
