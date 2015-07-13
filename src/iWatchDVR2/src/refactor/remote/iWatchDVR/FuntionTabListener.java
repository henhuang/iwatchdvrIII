package refactor.remote.iWatchDVR;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar;
import android.util.Log;

public class FuntionTabListener<T extends Fragment> implements ActionBar.TabListener {

    private Fragment mFragment;
    private final Activity mActivity;
    private final Class<T> mClass;
    private String mTag;

    /** Constructor used each time a new tab is created.
     * @param activity  The host Activity, used to instantiate the fragment
     * @param tag  The identifier tag for the fragment
     * @param clz  The fragment's Class, used to instantiate the fragment
     */
   public FuntionTabListener(Activity activity, String tag, Class<T> clz) {
       mActivity = activity;
       mTag = tag;
       mClass = clz;
   }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
        // TODO Auto-generated method stub
    }

    
    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        Log.i("TaListener", "onTabSelected");
        /*
        // Check if the fragment is already initialized
        if (mFragment == null) {
            // If not, instantiate and add it to the activity
            Log.i("TaListener", " new fragment");
            mFragment = Fragment.instantiate(mActivity, mClass.getName());
            ft.add(R.id.sideFragementContainer, mFragment, mTag);
        } else {
            // If it exists, simply attach it in order to show it
            ft.attach(mFragment);
        }
        */
        //mFragment = Fragment.instantiate(mActivity, mClass.getName());
        //ft.replace(R.id.fragementContainer, mFragment, mClass.getName());
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        Log.i("TaListener", "onTabUnselected");
        if (mFragment != null) {

            // not work for ics
            // Detach the fragment, because another one is being attached
            //ft.detach(mFragment); 
            
            // not work either -_-#
            // https://code.google.com/p/android/issues/detail?id=32405
            //ft.hide(mFragment);
            ft.remove(mFragment);
        }
    }

}

