package refactor.remote.iWatchDVR;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import peersdk.peer.PeerLog;
import refactor.remote.iWatchDVR.dvr.args.Log;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Pair;

public class LogSearchActivity extends BaseDVRActivity implements DateTimeCallback, MultiSelectionCallback {

	public final static String StartTime = "st";
	public final static String EndTime = "et";
	public final static String Channel = "ch";
	public final static String LogType ="lt";
	
	final static int FilterLogs = 0x0;
	
	boolean mStartTimeEnabled;
	boolean mEndTimeEnabled;
    long mStartTime;
    long mEndTime;
    int[] mChannel;
    int[] mLogType;
    
    boolean mQuit = false;
    boolean mProcessing = false;
    Thread mThread;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_log_search);

        InitUIHandler();
        
        AttachContent();
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	mApplication.AddPeerListener(this, LogSearchActivity.class.getCanonicalName());
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	mApplication.RemovePeerListener(LogSearchActivity.class.getCanonicalName());
    }

    protected void HandleMessage(Message msg) {
        switch (msg.what) {
        case FilterLogs:
        	FilterLogs((PeerLog[])msg.obj);
        	break;
        }
    }

    
    private void AttachContent() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragementContainer, Fragment.instantiate(this, LogSearchFragment.class.getName()));
        ft.commit();
    }
    
    public void SetStartTime(long value) {
        mStartTime = value;
    }
    
    public void SetEndTime(long value) {
        mEndTime = value;
    }
    
    public void SetChannel(int[] value) {
        mChannel = value;
    }
    
    public void SetLogType(int[] value) {
        mLogType = value;
    }
    
    private int[] ListToIntArray(List<Integer> value) {
    	int[] result = new int[value.size()];
    	for (int i = 0; i < result.length; i++) {
    		result[i] = value.get(i).intValue();
    	}
    	return result;
    }
    
    private int[] Parse(List<Pair<Integer, Boolean>> arg) {
    	List<Integer> value = new LinkedList<Integer>();
		for (int i = 1; i < arg.size(); i++) {
			Pair<Integer, Boolean> obj = arg.get(i);
			if (obj.second)
				value.add(obj.first);
		}
		return ListToIntArray(value);
    }
    
    private void FilterLogs(final PeerLog[] arg) {

    	//final WaitingDialog dialog = new WaitingDialog(this, this.toString());
    	//dialog.Show();
    	PopupWaitingDialog();
    	
    	new Thread() {
    		@Override
	    	public void run() {
    			setProcessing(true);
    			while (true) {
    				if (IsQuit())
	    				break;
    				DoFilter(arg);
	    		}
    			resetFilter();
    			//dialog.Dismiss();
    			DismissWaitingDialog();
	    	}
    	}.start();

    }

    private void resetFilter() {
    	synchronized (this) {
    		mQuit = false;
    		mProcessing = false;
    	}
    }
    
    private void setQuit(boolean value) {
    	synchronized (this) {
    		mQuit = value;
    	}
    }
    
    private void setProcessing(boolean value) {
    	synchronized (this) {
    		mProcessing = value;
    	}
    }
    
    private boolean IsQuit() {
    	synchronized (this) {
    		if (mQuit) {
    			mQuit = false;
    			return true;
    		}
    		return false;
    	}
    }
    
    private void DoFilter(PeerLog[] arg) {
    	
    	ArrayList<Log> logs = new ArrayList<Log>();
        for (int i = arg.length-1; i > -1; i--) {
        	PeerLog log = arg[i]; 
 
            // type filter;    
            boolean isTypeMatched = false;
            for (int x = 0; x < mLogType.length; x++) {
                                
            	if (log.Type() == mLogType[x]) {
            		isTypeMatched = true;
                    break;
                }
            }
            if (!isTypeMatched)
            	continue;

            // channel filter
            boolean isChannelMatched = false;
            if (log.HasChannel()) {
            	for (int x = 0; x < mChannel.length; x++) {
            		if (log.Channel() == mChannel[x]) {
            			isChannelMatched = true;
                        break;
                    }
                }
             }
             else
            	 isChannelMatched = true;

             if (!isChannelMatched)
            	 continue;

                
             // time filter
             Calendar c = Calendar.getInstance();
             c.setTime(log.Time());
             long logTime = c.getTimeInMillis();
             boolean ret;
             if (mStartTimeEnabled && mEndTimeEnabled)
            	 ret = (logTime <= mEndTime) && (logTime >= mStartTime);
             else if (mEndTimeEnabled)
            	 ret = (logTime <= mEndTime);
             else if (mStartTimeEnabled)
            	 ret = (logTime >= mStartTime);
             else
                ret = true;
                
             if (!ret)
            	 continue;
             
             
             Log loooog = new Log();
             loooog.channel = log.Channel();
             loooog.hasChannel = log.HasChannel();
             loooog.playable = log.Playable();
             loooog.time = log.Time();
                    
             logs.add(loooog);
        }

        Intent intent = new Intent();
        intent.putExtra("Logs", logs);
        intent.setClass(this, LogSearchResultActivity.class);
        startActivity(intent);
    }

    //================ MultiSelectionCallback =============================
    @Override
	public void onSelectionUpdated(String tag, List<Pair<Integer, Boolean>> state) {
		if (tag.equalsIgnoreCase(Channel)) {
			mChannel = Parse(state);
		}
		else if (tag.equalsIgnoreCase(LogType)) {
			mLogType = Parse(state);
		}
	}

    //================ DateTimeCallback =============================
	@Override
	public void onDateTimeUpdated(String tag, long datetime) {
		if (tag.equalsIgnoreCase(StartTime)) {
			
		}
		else if (tag.equalsIgnoreCase(EndTime)) {
			
		}
	}

	//================================================================
	@Override
	public void GetLogs(PeerLog[] arg) {
		if (mProcessing) { // new logs coming
    		setQuit(true);
    		setProcessing(false);
    	}
		SendMessage2UI(FilterLogs, arg);
	}

}
