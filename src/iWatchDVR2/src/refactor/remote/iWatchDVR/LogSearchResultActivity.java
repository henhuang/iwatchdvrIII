package refactor.remote.iWatchDVR;

import java.util.List;

import refactor.remote.iWatchDVR.dvr.args.Log;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

public class LogSearchResultActivity extends BaseDVRActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_log_search_result);
		
		List<Log> logs = (List<Log>) getIntent().getSerializableExtra("Logs");
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragementContainer, new LogSearchResultFragment((Log[])logs.toArray()));
        ft.commit();
	}
}
