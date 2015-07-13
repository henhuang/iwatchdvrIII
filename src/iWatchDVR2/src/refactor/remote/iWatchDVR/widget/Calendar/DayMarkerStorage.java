package refactor.remote.iWatchDVR.widget.Calendar;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import refactor.remote.iWatchDVR.R;

import android.content.Context;
import android.content.SharedPreferences;

public class DayMarkerStorage
{
	private static final String LOGTAG = "__DayMarkerStorage__";
	private static final String PREFS_NAME = "DAYMAKER";
	private static final String YEAR  = "YEAR";
	private static final String MONTH = "MONTH";
	private static final String DATE  = "DATE";
	private static final String HOUR  = "HOUR";
	private static final String MIN   = "MIN";
	private static final String SEC   = "SEC";
	private static final String TIME  = "TIME";
	private SharedPreferences  m_sp;
	private Context m_context;
	
	public DayMarkerStorage(Context context)
	{
		m_context = context;
		m_sp = m_context.getSharedPreferences("PREF_SESSION", Context.MODE_PRIVATE);

	}
	
	public int readYear()
	{
		return m_sp.getInt(YEAR, Calendar.getInstance().get(Calendar.YEAR));
	}
	
	public int readMonth()
	{
		return m_sp.getInt(MONTH, Calendar.getInstance().get(Calendar.MONTH));
	}
	
	public int readDate()
	{
		return m_sp.getInt(DATE, Calendar.getInstance().get(Calendar.DATE));
	}
	
	public int readHour()
	{
		return m_sp.getInt(HOUR, Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
	}
	
	public int readMin()
	{
		return m_sp.getInt(MIN, Calendar.getInstance().get(Calendar.MINUTE));
	}
	
	public int readSec()
	{
		return m_sp.getInt(SEC, Calendar.getInstance().get(Calendar.SECOND));
	}
	
	public String readTime()
	{
		return m_sp.getString(TIME, DateFormat.getInstance().format(m_context.getResources().getString(R.string.timeFormat)));
	}
	
	public void writeYear(int value)
	{
		SharedPreferences.Editor editor = m_sp.edit();
		editor.putInt(YEAR, value);
		editor.commit();
	}
	
	public void writeMonth(int value)
	{
		SharedPreferences.Editor editor = m_sp.edit();
		editor.putInt(MONTH, value);
		editor.commit();
	}
	
	public void writeDate(int value)
	{
		SharedPreferences.Editor editor = m_sp.edit();
		editor.putInt(DATE, value);
		editor.commit();
	}
	
	public void writeHour(int value)
	{
		SharedPreferences.Editor editor = m_sp.edit();
		editor.putInt(HOUR, value);
		editor.commit();
	}
	
	public void writeMin(int value)
	{
		SharedPreferences.Editor editor = m_sp.edit();
		editor.putInt(MIN, value);
		editor.commit();
	}
	
	public void writeSec(int value)
	{
		SharedPreferences.Editor editor = m_sp.edit();
		editor.putInt(SEC, value);
		editor.commit();
	}
	
	public void writeTime(String value)
	{
		SharedPreferences.Editor editor = m_sp.edit();
		editor.putString(TIME, value);
		editor.commit();
		
		try {
			DateFormat df = new SimpleDateFormat(m_context.getResources().getString(R.string.timeFormat));
			Calendar c = Calendar.getInstance();
			c.setTime(df.parse(value));
			
			writeYear(c.get(Calendar.YEAR));
			writeMonth(c.get(Calendar.MONTH));
			writeDate(c.get(Calendar.DATE));
			writeHour(c.get(Calendar.HOUR_OF_DAY));
			writeMin(c.get(Calendar.MINUTE));
			writeSec(c.get(Calendar.SECOND));
			
		} catch (ParseException e) {

			e.printStackTrace();
		}	
	}
	
	
	public void clear()
	{
		SharedPreferences.Editor editor = m_sp.edit();
		editor.clear();
		editor.commit();
	}
	
}
