package refactor.remote.iWatchDVR.ui.popup;

import refactor.remote.iWatchDVR.DVR;
import refactor.remote.iWatchDVR.R;
import refactor.remote.iWatchDVR.R.id;
import refactor.remote.iWatchDVR.widget.QuickAction.QuickAction;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PopupWindow16Channels extends QuickAction {
	
	public final static String TAG = "__PopupWindow16Channels__";
	
	protected int mChannels = 0;

	public PopupWindow16Channels(Context context, int layoutID, int channels) {
		
		super(context, layoutID);
		
		mChannels = channels;
		
		int max = DVR.MAX_CHANNEL / 16;
		int id = R.id._4x4_ch00;
		for (int i = 0; i < max; i++) {

			TextView ch = (TextView) mRootView.findViewById(id + i);
			int visible = i < mChannels ? View.VISIBLE : View.INVISIBLE;
			ch.setVisibility(visible);
		}
		

		/*
		setOnActionItemClickListener(new OnActionItemClickListener() {
			
			@Override
			public void onItemClick(int pos) {
				// TODO Auto-generated method stub
			}
		});
*/
	}
	
	public void setChannels(int value) {
		
		if (mChannels == value)
			return;
		mChannels = value;
		
		relayout();
	}
	
	protected void relayout() {

		int max = DVR.MAX_CHANNEL / 16;
		int id = R.id._4x4_ch00;
		for (int i = 0; i < max; i++) {
			
			TextView ch = (TextView) mRootView.findViewById(id + i);
			int visible = i < mChannels ? View.VISIBLE : View.INVISIBLE;
			ch.setVisibility(visible);
		}
	}
}
