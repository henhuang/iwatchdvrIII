package refactor.remote.iWatchDVR;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LogSearchFragment extends Fragment implements DateTimeCallback {
    
    final static String StartTime = "st";
    final static String EndTime = "et";
    final static String LogType = "lt";
    final static String LogChannel = "lc";
    
    long mStartTime;
    long mEndTime;
    int[] mLogChannel;
    int[] mLogType;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_log_search, container, false);
        final FragmentManager fm = getActivity().getSupportFragmentManager();
        v.findViewById(R.id.startTime).setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                new DateTimeFragment().show((FragmentActivity)v.getContext(), LogSearchFragment.StartTime);
            }
        });
        v.findViewById(R.id.endTime).setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                new DateTimeFragment().show((FragmentActivity)v.getContext(), LogSearchFragment.EndTime);
            }
        });
        v.findViewById(R.id.logtypes).setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                new LogTypeFragment().show((FragmentActivity)v.getContext(), LogSearchFragment.LogType);
            }
        });
        v.findViewById(R.id.channels).setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                new LogChannelFragment().show((FragmentActivity)v.getContext(), LogSearchFragment.LogChannel);
            }
        });
        
        v.findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                
            }
        });
        return v;
    }



    @Override
    public void onDateTimeUpdated(String tag, long datetime) {
        // TODO Auto-generated method stub
        
    }
}
