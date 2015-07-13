package refactor.remote.iWatchDVR;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class MultiSelectionAdapter extends ArrayAdapter<Pair<String, Integer>> {
    private Context mContext;
    List<Pair<String, Integer>> mItems;
    boolean[] mStates;

    public MultiSelectionAdapter(Context context, int resource,
      int textViewResourceId, List<Pair<String, Integer>> items) {
        super(context, resource, textViewResourceId, items);

        mContext = context;
        mItems = items;
        mItems.add(0, new Pair<String, Integer>(context.getResources().getString(R.string.all), -1));
        mStates = new boolean[mItems.size()];
    }

    public List<Pair<Integer, Boolean>> getState() {
    	List<Pair<Integer, Boolean>> state = new ArrayList<Pair<Integer, Boolean>>();
    	
    	synchronized (mStates) {
    		for (int i = 0; i < mStates.length; i++)
    			state.add(new Pair<Integer, Boolean>(mItems.get(i).second, Boolean.valueOf(mStates[i])));
    	}
    	return state;
    };
    
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    
        if (convertView == null)
        	convertView = inflater.inflate(R.layout.listview_multi_selection, null);

        TextView tag = (TextView) convertView.findViewById(R.id.tag);
        tag.setText(mItems.get(position).first);
    
        CheckBox check = (CheckBox) convertView.findViewById(R.id.state);
        check.setClickable(false);
        check.setChecked(mStates[position]);

        convertView.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                synchronized (mStates) {
                	boolean state = mStates[position];
	                if (position == 0) {
	                    for (int i = 0; i < mStates.length; i++)
	                        mStates[i] = !state;
	                }
	                else {
	                    mStates[position] = !state;
	                }
                }
                notifyDataSetChanged();
            }
        });

        return convertView;
    }
}

