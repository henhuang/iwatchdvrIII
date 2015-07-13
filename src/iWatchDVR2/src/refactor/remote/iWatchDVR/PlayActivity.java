package refactor.remote.iWatchDVR;

import refactor.remote.iWatchDVR.ui.panel.PlayPanelFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

public class PlayActivity extends VisualActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_play);
        
        InitUIHandler();

        InitializeCanvasView();

        if (savedInstanceState == null) {
            AttachPanel();
        }
        else {
            Log.i(TAG, "savedInstanceState is not null......");
        }
        
        //
        if (savedInstanceState != null) {
            
        }
    }
    
    private void InitializeCanvasView() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        CanvasFragment cf = new CanvasFragment();
        ft.replace(R.id.mainFragementContainer, cf);
        ft.commit();
    }

    private CanvasFragment GetCanvasFragment() {
        CanvasFragment c = (CanvasFragment)getSupportFragmentManager().findFragmentById(R.id.mainFragementContainer);
        return c;
    }
    
    private void AttachPanel() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.panelFragementContainer, Fragment.instantiate(this, PlayPanelFragment.class.getName()));
        ft.commit();
    }
    
    @Override
    public void Raise_VisualChanged(int divide) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void Raise_VisualChanged(int divide, boolean arg) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void Raise_VisualChanged() {
        // TODO Auto-generated method stub
        
    }

}
