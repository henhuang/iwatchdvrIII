package refactor.remote.iWatchDVR.ui.popup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import refactor.remote.iWatchDVR.DVR;
import refactor.remote.iWatchDVR.R;
import refactor.remote.iWatchDVR.R.id;
import refactor.remote.iWatchDVR.widget.QuickAction.QuickAction;

public class PopupWindowSingleChannel extends QuickAction {
	
	public final static String TAG = "__PopupWindowSingleChannel__";

	public final static String CHANNEL = "channel";
	
	protected int mChannels = 0;
	
	public PopupWindowSingleChannel(Context context, int layoutID, int channels) {
		
		super(context, layoutID);
		
		mChannels = channels;
		

		int id = R.id.button__single_ch00;
		for (int i = 0; i < DVR.MAX_CHANNEL; i++) {
			
			TextView ch = (TextView) mRootView.findViewById(id + i);
			int visible = i < mChannels ? View.VISIBLE : View.INVISIBLE;
			ch.setVisibility(visible);
			ActionItemExtraHolder holder = new ActionItemExtraHolder(i);
			ch.setTag(holder);
			

			ch.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					
					int index = ((ActionItemExtraHolder) v.getTag()).getIndex();
					//if (mListener != null) 
					//	mListener.onItemClick(index);

					/**
					if (index == 0) {
						
						Log.i(TAG, "item on Click=" + index);
						Intent intent = new Intent();
						Bundle bundle = new Bundle();
						bundle.putInt(CHANNEL, index);
						intent.putExtras(bundle);
						intent.setClass(v.getContext(), PTZActivity.class);
						v.getContext().startActivity(intent);
					}
					*/
					
					dismiss();
				}
			});
		}

	
		setOnActionItemClickListener(new OnActionItemClickListener() {
			@Override
			public void onItemClick(int pos) {
				
				//Log.i(TAG, "item on Click=" + pos);
			}
		});
			
	}
	
	public void setChannels(int value) {
		
		if (mChannels == value)
			return;
		mChannels = value;
		
		relayout();
	}
	
	protected void relayout() {
		int id = R.id._8ch_ch00;
		for (int i = 0; i < DVR.MAX_CHANNEL; i++) {
			TextView ch = (TextView) mRootView.findViewById(id + i);
			int visible = i < mChannels ? View.VISIBLE : View.INVISIBLE;
			ch.setVisibility(visible);
		}
	}
}

