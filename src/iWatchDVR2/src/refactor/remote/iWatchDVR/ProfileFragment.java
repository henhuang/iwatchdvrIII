package refactor.remote.iWatchDVR;

import refactor.remote.iWatchDVR.application.RemoteDVRApplication;
import refactor.remote.iWatchDVR.database.DVRHosts;
import refactor.remote.iWatchDVR.dvr.ConnectInfo;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;

public class ProfileFragment extends ListFragment {

    final static String TAG = "__ProfileFragment__";
    
    ProfileSimpleCursorAdapter mAdapter;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {   
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); 
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        return v;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        InitializeView();
        InitializeListener();
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) { 
        inflater.inflate(R.menu.fragment_profile, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            break;
            
        case R.id.action_new:
            FunctionActivity activity = (FunctionActivity)getActivity();
            if (((RemoteDVRApplication)activity.getApplication()).IsDualPane()) {
                //TODO:
            }
            else {
                activity.AttachProfileNewLayout();
            }
            
            break;
        }
        return true;
    }
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        ((FunctionActivity)getActivity()).Connect((ConnectInfo)mAdapter.getItem(position));
    }
    
    private void InitializeListener() {
        getListView().setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View view,
                    int position, long id) {

                Cursor cursor = mAdapter.getCursor();
                cursor.moveToPosition(position);

                int index = cursor.getInt(cursor.getColumnIndexOrThrow(DVRHosts.DVR._ID));
                String name = cursor.getString(cursor.getColumnIndex(DVRHosts.DVR.NAME));
                String host = cursor.getString(cursor.getColumnIndex(DVRHosts.DVR.HOST));
                String port = cursor.getString(cursor.getColumnIndex(DVRHosts.DVR.PORT));
                String user = cursor.getString(cursor.getColumnIndex(DVRHosts.DVR.USER));
                String password = cursor.getString(cursor.getColumnIndex(DVRHosts.DVR.PASSWORD));
                ((FunctionActivity)getActivity()).AttachProfileEditLayout(index, name, host, port, user, password);
                return true;
            }


        });
    }
    
    private void InitializeView() {
    	Activity context = getActivity();
    	context.invalidateOptionsMenu();
        String[] from = new String[] { DVRHosts.DVR.NAME,
                                       DVRHosts.DVR.USER,
                                       DVRHosts.DVR.HOST, 
                                       DVRHosts.DVR.PORT,
                                       DVRHosts.DVR.VERSION,
                                       DVRHosts.DVR.CHANNEL };
                                        
        int[] to = new int[] { R.id.text_dvrname,
                               R.id.text_user,
                               R.id.text_host, 
                               R.id.text_port,
                               R.id.text_version,
                               R.id.icon_channel };
                                        

        Cursor cursor= context.managedQuery(
                                         ((RemoteDVRApplication)getActivity().getApplication()).getURI(), 
                                         null, null, null, null);

        mAdapter = new ProfileSimpleCursorAdapter(context, R.layout.listview_row_login_dvr, cursor, from, to, 0);
        setListAdapter(mAdapter);
    }
    
    
    public class ProfileSimpleCursorAdapter extends SimpleCursorAdapter {

        public ProfileSimpleCursorAdapter(Context context, int layout, Cursor cur,
                 String[] from, int[] to, int flag) {
            super(context, layout, cur, from, to, flag);
        }

        @Override
        public Object getItem(int position) {
            ConnectInfo info = new ConnectInfo();
            Cursor c = getCursor();
            info.host = c.getString(c.getColumnIndex(DVRHosts.DVR.HOST));
            info.port = Integer.parseInt(c.getString(c.getColumnIndex(DVRHosts.DVR.PORT)));
            info.user = c.getString(c.getColumnIndex(DVRHosts.DVR.USER));
            info.pwd = c.getString(c.getColumnIndex(DVRHosts.DVR.PASSWORD));
            return info;
        }
        
        @Override
        public void setViewImage(ImageView view, String value) {
        
            int channel = 0;
            try {
                channel = Integer.parseInt(value);
            } catch (NumberFormatException e) {
            }
             
            switch (channel) {
            
            case 4:
                view.setImageResource(R.drawable.dvricon_04);
                break;
            case 8:
                view.setImageResource(R.drawable.dvricon_08);
                break;
            case 9:
                view.setImageResource(R.drawable.dvricon_09);
                break;
            case 10:
                view.setImageResource(R.drawable.dvricon_10);
                break;
            case 20:
                view.setImageResource(R.drawable.dvricon_20);
                break;
            case 16:
                view.setImageResource(R.drawable.dvricon_16);
                break;
            case 32:
                view.setImageResource(R.drawable.dvricon_32);
                break;
            default:
                view.setImageResource(R.drawable.dvricon_unknow);    
                break;
            }
        }
    }
        
}
