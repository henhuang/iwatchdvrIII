package refactor.remote.iWatchDVR;

import java.text.SimpleDateFormat;
import peersdk.LogType;
import refactor.remote.iWatchDVR.application.RemoteDVRApplication;
import refactor.remote.iWatchDVR.dvr.args.Log;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class LogSearchResultFragment extends ListFragment {

	LogAdapter mAdapter;
	
	RemoteDVRApplication mApp;
	Log[] mLogs;
	
	public LogSearchResultFragment(Log[] logs) {
		mLogs = logs;
		mAdapter = new LogAdapter();
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_log_search_result, container, false);
        return v;
    }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setRetainInstance(true);
		
		mApp = (RemoteDVRApplication)getActivity().getApplication();
		setListAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
	}
	
	@Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        // TODO: if playable
        // play video
    }
	
	//////////////////////////////////////////////////////
	//////////////// class LogAdapter ////////////////////
	
	class LogAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mLogs.length;
		}

		@Override
		public Object getItem(int position) {
			return mLogs[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater inflator = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflator.inflate(R.layout.listview_row_log, null);
			}
			
			Log log = mLogs[position];
			
			TextView playCH = (TextView)convertView.findViewById(R.id.play);			
			playCH.setClickable(log.playable);
			if (log.playable) {
				playCH.setVisibility(View.VISIBLE);
				playCH.setText(log.hasChannel ? String.format("CH%2d", log.channel) : "");
			}
			else {
				playCH.setVisibility(View.INVISIBLE);
			}
			
			((TextView)convertView.findViewById(R.id.logType)).setText(LogType.toString(getActivity(), mLogs[position].type));
			
			SimpleDateFormat sdf = new SimpleDateFormat("%4d-%02d-%02d %02d:%02d:%02d");
            String time = sdf.format(log.time);
			((TextView)convertView.findViewById(R.id.logTime)).setText(time);
			
			return convertView;
		}

	}
}
