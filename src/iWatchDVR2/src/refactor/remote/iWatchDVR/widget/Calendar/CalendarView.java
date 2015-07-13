package refactor.remote.iWatchDVR.widget.Calendar;

import java.util.Calendar;

import refactor.remote.iWatchDVR.R;
import refactor.remote.iWatchDVR.widget.Calendar.CalendarWrapper.OnDateChangedListener;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;



public class CalendarView extends LinearLayout
{
	private final static String TAG = "__CalendarView__";
	
	private final int CENTURY_VIEW = 5;
	private final int DECADE_VIEW = 4;
	private final int YEAR_VIEW = 3;
	private final int MONTH_VIEW = 2;
	private final int DAY_VIEW = 1;
	private final int ITEM_VIEW = 0;

	private CalendarWrapper   m_calendar;
	private CalendarDayMarker m_marker;
	private TableLayout       m_days;
	private TextView          m_now;
	private Button            m_prev;
	private Button            m_next;
	private OnMonthChangedListener       m_onMonthChangedListener;
	private OnSelectedDayChangedListener m_onSelectedDayChangedListener;
	
	private int m_currentView;
	private int m_currentYear;
	private int m_currentMonth;
	private int m_currentDay;
	private int m_markTableRow;
	private int m_markTableCol;
	private Drawable m_markPrevDrawable;
	
	
	
	public CalendarView(Context context)
	{
		super(context);
		init(context);
	}

	public CalendarView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	public interface OnMonthChangedListener
	{
		public void onMonthChanged(CalendarView view);
	}

	public void setOnMonthChangedListener(OnMonthChangedListener l)
	{
		m_onMonthChangedListener = l;
	}

	public interface OnSelectedDayChangedListener
	{
		public void onSelectedDayChanged(CalendarView view);
	}

	public void setOnSelectedDayChangedListener(OnSelectedDayChangedListener l)
	{
		m_onSelectedDayChangedListener = l;
	}

	public Calendar getVisibleStartDate()
	{
		return m_calendar.getVisibleStartDate();
	}

	public Calendar getVisibleEndDate()
	{
		return m_calendar.getVisibleEndDate();
	}

	public Calendar getSelectedDay()
	{
		return m_calendar.getSelectedDay();
	}
	
	public CalendarDayMarker getDayMarker()
	{
		return m_marker;
	}

	public void cleanDayMark()
	{
		if (m_markTableRow == 0)
			return;
		
		TableRow tr = (TableRow) m_days.getChildAt(m_markTableRow);
		TextView tv = (TextView) tr.getChildAt(m_markTableCol);
		tv.setBackgroundDrawable(m_markPrevDrawable);
		tv.setTextColor(Color.BLACK);
	}
	
	public void setDaysEventDrawable(int[] days)
	{
		int dayItemsInGrid = 42;
		int row = 1; // Skip weekday header row
		int col = 0;
		
		boolean gotStart = false;
		int index        = 0;
		for(int i = 0; i < dayItemsInGrid; i++) 
		{
			TableRow tr = (TableRow) m_days.getChildAt(row);
			TextView tv = (TextView) tr.getChildAt(col);
			int[] tag = (int[]) tv.getTag();
			int day = tag[1];
			
			// start from the first day of this month
			if (!gotStart)
			{
				if (day != 1)
				{
					col++;
					if (col == 7) {
					
						col = 0;
						row++;
					}
					continue;
				}
				gotStart = true;
			}
			
			if (index < days.length && day == days[index])
			{
				tv.setClickable(true);
				
				Drawable d = getResources().getDrawable(R.drawable.selector_calendar_event);
				tv.setBackgroundDrawable(d);
				
				index++;
				if (index == days.length)
					break;
			}

			col++;
			if (col == 7) 
			{
				col = 0;
				row++;
			}			
		}
	}
	
	public void setDayMark(int year, int month, int day) 
	{
		cleanDayMark();

		m_marker.setYear(year);
		m_marker.setMonth(month);
		m_marker.setDay(day);
		
		int dayItemsInGrid = 42;
		int row = 1; // Skip weekday header row
		int col = 0;
		Calendar tempCal = m_calendar.getVisibleStartDate();

		for(int i = 0; i < dayItemsInGrid; i++) 
		{
			TableRow tr = (TableRow) m_days.getChildAt(row);
			TextView tv = (TextView) tr.getChildAt(col);
			int[] tag = (int[]) tv.getTag();
			int _day = tag[1];

			if (tempCal.get(Calendar.YEAR) == m_marker.getYear() 
					&& tempCal.get(Calendar.MONTH) == m_marker.getMonth() 
					&& _day == m_marker.getDay()) 
			{
				m_markPrevDrawable = tv.getBackground();
				m_markTableRow     = row;
				m_markTableCol     = col;
				
				tv.setTextColor(Color.RED);
				break;
			}
			
			tempCal.add(Calendar.DAY_OF_MONTH, 1);
			
			col++;

			if (col == 7) 
			{
				col = 0;
				row++;
			}			
		}
	}


	private void init(Context context)
	{
		View v = LayoutInflater.from(context).inflate(R.layout.calendar_view, this, true);

		Calendar c = Calendar.getInstance();
		m_calendar = new CalendarWrapper();
		refreshCurrentDate();
		
		m_days = (TableLayout) v.findViewById(R.id.calendar_days);
		m_now = (TextView) v.findViewById(R.id.calendar_now);
		m_prev = (Button) v.findViewById(R.id.calendar_previous);
		m_next = (Button) v.findViewById(R.id.calendar_next);

		m_marker = new CalendarDayMarker(c, Color.CYAN);
		
		// Days Table
		String[] shortWeekDayNames = m_calendar.getShortDayNames();

		for (int i = 0; i < 7; i++) { // Rows
			TableRow tr = (TableRow) m_days.getChildAt(i);

			for (int j = 0; j < 7; j++) // Columns
			{
				Boolean header = i == 0; // First row is weekday headers
				TextView tv = (TextView) tr.getChildAt(j);

				if (header)
					tv.setText(shortWeekDayNames[j]);
				else
					tv.setOnClickListener(_dayClicked);
			}
		}

		refreshDayCells();

		// Listeners
		m_calendar.setOnDateChangedListener(m_dateChanged);
		m_prev.setOnClickListener(m_incrementClicked);
		m_next.setOnClickListener(m_incrementClicked);

		setView(MONTH_VIEW);
	}

	private OnDateChangedListener m_dateChanged = new OnDateChangedListener()
	{
		public void onDateChanged(CalendarWrapper sc)
		{
			Boolean monthChanged = m_currentYear != sc.getYear() || m_currentMonth != sc.getMonth();

			if (monthChanged) {
				//Log.i(TAG, "m_markerYear=" + m_marker.getYear() + ", m_markertMonth=" + m_marker.getMonth() +
				//			", m_calendar.Year=" + m_calendar.getYear() + ", m_calendar.Month=" + m_calendar.getMonth() + ", m_calendar.Day=" + m_calendar.getDay());

				refreshDayCells();
				
				if (m_marker.getYear() == m_calendar.getYear() && m_marker.getMonth() == m_calendar.getMonth())
					setDayMark(m_marker.getYear(), m_marker.getMonth(), m_marker.getDay());
				
				invokeMonthChangedListener();
			}

			refreshCurrentDate();
			refreshUpText();
		}
	};

	private OnClickListener m_incrementClicked = new OnClickListener()
	{
		public void onClick(View v)
		{
			int inc = (v == m_next ? 1 : -1);

			String target = (inc == 1) ? "next" : "prev";
			//Log.i(TAG,  target + " onClick");
			
			if (m_currentView == MONTH_VIEW)
			{
				m_calendar.addMonth(inc);
			}
			else if (m_currentView == DAY_VIEW)
			{
				m_calendar.addDay(inc);
				invokeSelectedDayChangedListener();
			}
			else if (m_currentView == YEAR_VIEW)
			{
				m_currentYear += inc;
				refreshUpText();
			}

		}
	};

	private OnClickListener _dayClicked = new OnClickListener()
	{
		public void onClick(View v)
		{
			int[] tag = (int[]) v.getTag();
			m_calendar.addMonthSetDay(tag[0], tag[1]);
			invokeSelectedDayChangedListener();
			
			//Log.i(TAG, "day click=" + tag[0] + ", " + tag[1]);
			setDayMark(m_calendar.getYear(), m_calendar.getMonth(), m_calendar.getDay());
		}
	};
	
	public void refreshDayCells()
	{
		int[] dayGrid = m_calendar.get7x6DayArray();
		int monthAdd = -1;
		int row = 1; // Skip weekday header row
		int col = 0;

		for (int i = 0; i < dayGrid.length; i++)
		{
			int day = dayGrid[i];

			if (day == 1)
				monthAdd++;

			TableRow tr = (TableRow) m_days.getChildAt(row);
			TextView tv = (TextView) tr.getChildAt(col);

			//Clear current markers, if any.
			//tv.setBackgroundDrawable(null);
			
			tv.setText(dayGrid[i] + "");

			if (monthAdd == 0)
			{
				tv.setTextColor(Color.DKGRAY);
				tv.setClickable(true);
			}
			else
			{
				tv.setTextColor(Color.LTGRAY);
				tv.setClickable(false);
			}

			Drawable drawable = getResources().getDrawable(R.drawable.selector_calendar);
			tv.setBackgroundDrawable(drawable);
			
			tv.setTag(new int[] { monthAdd, dayGrid[i] });
			col++;

			if (col == 7)
			{
				col = 0;
				row++;
			}
		}

	}

	private void setView(int view) 
	{
		if (m_currentView != view) 
		{
			m_currentView = view;
			m_days.setVisibility(m_currentView == MONTH_VIEW ? View.VISIBLE : View.GONE);

			refreshUpText();
		}
	}

	private void refreshUpText()
	{
		switch (m_currentView)
		{
			case MONTH_VIEW:
				m_now.setText(m_calendar.toString("MMMM yyyy"));
				break;
			case YEAR_VIEW:
				m_now.setText(m_currentYear + "");
				break;
			case CENTURY_VIEW:
				m_now.setText("CENTURY_VIEW");
				break;
			case DECADE_VIEW:
				m_now.setText("DECADE_VIEW");
				break;
			case DAY_VIEW:
				m_now.setText(m_calendar.toString("EEEE, MMMM dd, yyyy"));
				break;
			case ITEM_VIEW:
				m_now.setText("ITEM_VIEW");
				break;
			default:
				break;
		}
	}

	private void refreshCurrentDate()
	{
		m_currentYear  = m_calendar.getYear();
		m_currentMonth = m_calendar.getMonth();
		m_currentDay   = m_calendar.getDay();
	}

	public void invokeMonthChangedListener()
	{
		Log.i(TAG, "invokeMonthChangedListener");
		if (m_onMonthChangedListener != null)
			m_onMonthChangedListener.onMonthChanged(this);
	}

	public void invokeSelectedDayChangedListener()
	{
		if (m_onSelectedDayChangedListener != null)
			m_onSelectedDayChangedListener.onSelectedDayChanged(this);
	}

	public void SetDate(Calendar c)
	{
		m_calendar.setYear(c.get(Calendar.YEAR));
		m_calendar.setMonth(c.get(Calendar.MONTH));
		m_calendar.setDay(c.get(Calendar.DATE));
		
		m_dateChanged.onDateChanged(m_calendar);
		refreshUpText();
	}

	public int Year() {
		
		return m_calendar.getYear();
	}
	
	public int Month() {
		
		return m_calendar.getMonth();
	}
	
	public int Day() {
		
		return m_calendar.getDay();
	}
	
}
