package refactor.remote.iWatchDVR;

import refactor.remote.iWatchDVR.application.RemoteDVRApplication;
import refactor.remote.iWatchDVR.database.DVRHosts;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class ProfileNewFragment extends Fragment {

    EditText mName;
    EditText mHost;
    EditText mPort;
    EditText mUser;
    EditText mPassword;
    @Override
    public void onCreate (Bundle savedInstanceState) {   
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        ((ActionBarActivity)getActivity()).getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        ((ActionBarActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile_edit, container, false);
        mName = (EditText)v.findViewById(R.id.name);
        mHost = (EditText)v.findViewById(R.id.host);
        mPort = (EditText)v.findViewById(R.id.port);
        mUser = (EditText)v.findViewById(R.id.user);
        mPassword = (EditText)v.findViewById(R.id.password);
        return v;
    }
    
    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_profile_new, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            Exit();
            break;
            
        case R.id.action_clear:
            Clear();
            break;
            
        case R.id.action_save:
            Save();
            break;
        }
        return true;
    }
    
    private void Exit() {
        getActivity().getSupportFragmentManager().popBackStack();
    }
    
    private void Clear() {
        mName.setText("");
        mHost.setText("");
        mPort.setText("");
        mUser.setText("");
        mPassword.setText("");
    }
    
    private void Save() {
        Uri uri = ((RemoteDVRApplication) getActivity().getApplication()).getURI();
        getActivity().getContentResolver().insert(uri, 
                GenDateBaseContentItem(mName.getText().toString(), mHost.getText().toString(), 
                        mPort.getText().toString(), mUser.getText().toString(), mPassword.getText().toString()));
        
        Exit();
    }

    private ContentValues GenDateBaseContentItem(String name, String host, 
            String port, String user, String password) {
        ContentValues values = new ContentValues();
        values.put(DVRHosts.DVR.NAME, name);
        values.put(DVRHosts.DVR.HOST, host);
        values.put(DVRHosts.DVR.PORT, port);
        values.put(DVRHosts.DVR.USER, user);
        values.put(DVRHosts.DVR.PASSWORD, password);
        return values;
    }
}
