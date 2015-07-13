package refactor.remote.iWatchDVR;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class FakeRootActivity extends Activity {
    
    @Override  
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);  
        Intent intent = (Intent)getIntent().clone();
        intent.setClass(FakeRootActivity.this, FunctionActivity.class);  
        startActivity(intent);  
        finish();  
    }  
}
