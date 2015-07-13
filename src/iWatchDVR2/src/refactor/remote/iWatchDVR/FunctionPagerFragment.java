package refactor.remote.iWatchDVR;

import java.util.ArrayList;

import refactor.remote.iWatchDVR.R;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TabHost.TabSpec;

public class FunctionPagerFragment extends Fragment {

    final static String TAG = "__FunctionPagerFragment__";
    
    TabHost     mTabHost;
    ViewPager   mViewPager;
    TabsAdapter mTabsAdapter;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(false);
        Log.i(TAG, "onCreate");
        if (savedInstanceState != null)
            mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        
        View v = inflater.inflate(R.layout.fragment_tabs_pager, container, false);
        mTabHost = (TabHost)v.findViewById(android.R.id.tabhost);
        mTabHost.setup();

        mViewPager = (ViewPager)v.findViewById(R.id.pager);

        // NOTICE: nested fragment
        mTabsAdapter = new TabsAdapter(getActivity(), getChildFragmentManager(), mTabHost, mViewPager);
        
        TabSpec tabSpecDVR        = mTabHost.newTabSpec(ProfileFragment.class.getSimpleName()).setIndicator("DVR");
        TabSpec tabSpecBackupList = mTabHost.newTabSpec(VersionFragment.class.getSimpleName()).setIndicator("Backup");

        mTabsAdapter.addTab(tabSpecDVR, ProfileFragment.class, null);
        mTabsAdapter.addTab(tabSpecBackupList, VersionFragment.class, null);
        return v;
    }
    
    @Override
    public 
    void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    
    
    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) { 
        Log.i(TAG, "onCreateOptionsMenu");
        
        inflater.inflate(R.menu.fragment_profile, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "onOptionsItemSelected");
        /*
        switch (item.getItemId()) {
        case android.R.id.home:
            break;
            
        case R.id.action_new:
            FunctionActivity activity = (FunctionActivity)getActivity();
            if (activity.IsDualPane()) {
                //TODO:
            }
            else {
                activity.AttachProfileNewLayout();
            }
            
            break;
        }
        */
        return true;
    }

    protected void Test() {
        getActivity().invalidateOptionsMenu();
    }
    /**
     * This is a helper class that implements the management of tabs and all
     * details of connecting a ViewPager with associated TabHost.  It relies on a
     * trick.  Normally a tab host has a simple API for supplying a View or
     * Intent that each tab will show.  This is not sufficient for switching
     * between pages.  So instead we make the content part of the tab host
     * 0dp high (it is not shown) and the TabsAdapter supplies its own dummy
     * view to show as the tab content.  It listens to changes in tabs, and takes
     * care of switch to the correct paged in the ViewPager whenever the selected
     * tab changes.
     */
    public static class TabsAdapter extends FragmentPagerAdapter
            implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {

        private Context mContext;
        private final TabHost mTabHost;
        private final ViewPager mViewPager;
        private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
        private final ArrayList<TabSpec> mTabSpecs = new ArrayList<TabSpec>();

        static final class TabInfo {
            private final String tag;
            private final Class<?> clss;
            private final Bundle args;

            TabInfo(String _tag, Class<?> _class, Bundle _args) {
                
                tag = _tag;
                clss = _class;
                args = _args;
            }
        }

        static class DummyTabFactory implements TabHost.TabContentFactory {
            
            private final Context mContext;

            public DummyTabFactory(Context context) {
                mContext = context;
            }

            @Override
            public View createTabContent(String tag) {
                View v = new View(mContext);
                v.setMinimumWidth(0);
                v.setMinimumHeight(0);
                return v;
            }
        }

        public TabsAdapter(FragmentActivity activity, FragmentManager  fragmentManager, TabHost tabHost, ViewPager pager) {
            super(fragmentManager);
            
            mContext = activity;
            mTabHost = tabHost;
            mViewPager = pager;
            mTabHost.setOnTabChangedListener(this);
            mViewPager.setAdapter(this);
            mViewPager.setOnPageChangeListener(this);
        }

        public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
            
            tabSpec.setContent(new DummyTabFactory(mContext));
            String tag = tabSpec.getTag();
            

            TabInfo info = new TabInfo(tag, clss, args);
            mTabs.add(info);

            if (mTabSpecs.size() == 0) {
            
                mTabSpecs.add(tabSpec);
                mTabHost.addTab(tabSpec);
            }
            else {
            
                Boolean dulicated = false;
                for (int i = 0; i < mTabSpecs.size(); i++) {
                
                    if (tag == mTabSpecs.get(i).getTag()) {
                    
                        dulicated = true;
                        break;
                    }
                }
                
                if (!dulicated) {
                
                    mTabHost.addTab(tabSpec);
                    mTabSpecs.add(tabSpec);
                }
            }
            
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            
            return mTabs.size();
        }

        @Override
        public Fragment getItem(int position) {
            Log.i(TAG, "getItem:" + position);
            TabInfo info = mTabs.get(position);
            return Fragment.instantiate(mContext, info.clss.getName(), info.args);
        }

        @Override
        public void onTabChanged(String tabId) {
            
            int position = mTabHost.getCurrentTab();
            mViewPager.setCurrentItem(position);      
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            // Unfortunately when TabHost changes the current tab, it kindly
            // also takes care of putting focus on it when not in touch mode.
            // The jerk.
            // This hack tries to prevent this from pulling focus out of our
            // ViewPager.
            TabWidget widget = mTabHost.getTabWidget();
            int oldFocusability = widget.getDescendantFocusability();
            widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            mTabHost.setCurrentTab(position);
            widget.setDescendantFocusability(oldFocusability);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
 
    }
}
