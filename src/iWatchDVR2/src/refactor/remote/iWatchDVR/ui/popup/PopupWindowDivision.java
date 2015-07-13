package refactor.remote.iWatchDVR.ui.popup;

import peersdk.peer.PeerChannel;
import refactor.remote.iWatchDVR.R;
import refactor.remote.iWatchDVR.R.id;
import refactor.remote.iWatchDVR.R.layout;
import refactor.remote.iWatchDVR.application.RemoteDVRApplication;
import refactor.remote.iWatchDVR.widget.QuickAction.QuickAction;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.app.AlertDialog.Builder;

public class PopupWindowDivision extends QuickAction {
    
    public final static String TAG = "__PopupWindowDivision__";
    
    AlertDialog           m1x1Dialog;
    AlertDialog           m2x2Dialog;
    AlertDialog           m3x3Dialog;
    AlertDialog           m4x4Dialog;

    
    public PopupWindowDivision(Context context, int layoutID) {
        super(context, layoutID);
        
        InitDivide();
        /*
        RemoteDVRApplication app = (RemoteDVRApplication) mContext.getApplication();
        int channels = app.Peer().Channels().length;
        
        switch (channels) {
        
        case 4:
            Init4Channels();
            break;        
        case 8:
        case 9:
            Init8Channels();
            break;
        case 10:
            Init10Channels();
            break;
        case 16:
            Init16Channels();
            break;
        case 20:
            Init20Channels();
            break;
        case 32:
            Init32Channels();
            break;
        }
        */
    }

    private void Init4Channels() {

        mRootView.findViewById(R.id.division_3x3).setEnabled(false);
        mRootView.findViewById(R.id.division_4x4).setEnabled(false);
        InitializePopup(1);
        InitializePopup(4);
    }
    
    private void Init8Channels() {

        mRootView.findViewById(R.id.division_4x4).setEnabled(false);
        InitializePopup(1);
        InitializePopup(4);
        InitializePopup(9);
    }
    
    private void Init10Channels() {

        InitializePopup(1);
        InitializePopup(4);
        InitializePopup(9);
        InitializePopup(16);
    }
    
    private void Init16Channels() {
        
        InitializePopup(1);
        InitializePopup(4);
        InitializePopup(9);
        InitializePopup(16);
    }
    
    private void Init20Channels() {
        
        InitializePopup(1);
        InitializePopup(4);
        InitializePopup(9);
        InitializePopup(16);
    }
    
    private void Init32Channels() {
    
        InitializePopup(1);
        InitializePopup(4);
        InitializePopup(9);
        InitializePopup(16); 
    }
    
    private void InitDivide() {
        
        View _1x1 = mRootView.findViewById(R.id.division_1x1);
        View _2x2 = mRootView.findViewById(R.id.division_2x2);
        View _3x3 = mRootView.findViewById(R.id.division_3x3);
        View _4x4 = mRootView.findViewById(R.id.division_4x4);
    
        _1x1.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                dismiss();

            }
        });
        
        _1x1.setOnLongClickListener(new View.OnLongClickListener() {
            
            @Override
            public boolean onLongClick(View arg0) {
                m1x1Dialog.show();
                return true;
            }
        });
        
        _2x2.setOnClickListener(new View.OnClickListener() {
                    
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        
        _2x2.setOnLongClickListener(new View.OnLongClickListener() {
            
            @Override
            public boolean onLongClick(View v) {
                m2x2Dialog.show();
                return true;
            }
        });
        
        _3x3.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        
        _3x3.setOnLongClickListener(new View.OnLongClickListener() {
            
            @Override
            public boolean onLongClick(View v) {
                m3x3Dialog.show();
                return true;
            }
        });

        _4x4.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        
        _4x4.setOnLongClickListener(new View.OnLongClickListener() {
            
            @Override
            public boolean onLongClick(View v) {
                m4x4Dialog.show();
                return true;
            }
        });
    }
    
    private void InitializePopup(final int divide) {

         int dialogResid  = R.layout.dialog_divide;
         int contentResid = R.layout.dialog_divide_gridcontent;

         LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         View view = inflater.inflate(R.layout.dialog_divide, null);
         GridView content = (GridView) view.findViewById(R.id.divide_grid);
         /*
         RemoteDVRApplication app = (RemoteDVRApplication) mContext.getApplication();
         PeerChannel[] channels = app.Peer().Channels();
         
         AlertDialog dialog = null;
         String[] channelsName = new String[(int) Math.ceil(((double) channels.length / divide))];
         int titleResid = 0;
         
         switch (divide) {
         
         case 1: 
             {
                 titleResid = R.string._1x1;
                 
                 for (int i = 0; i < channels.length; i++)
                      channelsName[i] = channels[i].Name();
                 
                 m1x1Dialog = new AlertDialog.Builder(mContext)
                    .setTitle(titleResid)
                    .setView(view)
                    .create();
                 dialog = m1x1Dialog;
                 
             }
             break;
             
         case 4:
              {
                   titleResid = R.string._2x2;
                   
                   int start = R.string._2x2_ch0;
                   for (int i = 0; i < channelsName.length; i++) 
                      channelsName[i] = mContext.getResources().getString(start+i);
                   
                  m2x2Dialog = new AlertDialog.Builder(mContext)
                    .setTitle(titleResid)
                    .setView(view)
                    .create();
                  dialog = m2x2Dialog;
              }
             break;
             
         case 9:
             {
                  titleResid = R.string._3x3;
                  
                  int start = R.string._3x3_ch0;
                   for (int i = 0; i < channelsName.length; i++) 
                      channelsName[i] = mContext.getResources().getString(start+i);
                   
                  m3x3Dialog = new AlertDialog.Builder(mContext)
                    .setTitle(titleResid)
                    .setView(view)
                    .create();
                  dialog = m3x3Dialog;
             }
            break;
             
         case 16:
              {
                  dialog = m4x4Dialog;
                  titleResid = R.string._4x4;
                  int start = R.string._4x4_ch0;
                   for (int i = 0; i < channelsName.length; i++) 
                      channelsName[i] = mContext.getResources().getString(start+i);
                   
                  m4x4Dialog = new AlertDialog.Builder(mContext)
                    .setTitle(titleResid)
                    .setView(view)
                    .create();
                  dialog = m4x4Dialog;
              }
             break;
         }
         

         DivideAdapter adapter = new DivideAdapter(mContext, dialogResid, channelsName, contentResid);
         content.setAdapter(adapter);
         content.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                        long id) {

                    mContext.gotoNextVisaul(divide, (int) id);
                    ((DivideAdapter) parent.getAdapter()).getDialog().dismiss();
                    dismiss();
                }        
            });
        adapter.setDialog(dialog);
        */
    }
    
    ///////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////
    class DivideAdapter extends ArrayAdapter<String>  {
        
        protected Context   mContext;
        protected int       mContentResid;
        protected Dialog    mDialog;
        protected String[]  mContent;
        
        DivideAdapter(Context context, int resource, String[] items, int contentResid){
            super(context,resource,items);
            
            mContext = context;
            mContentResid = contentResid;
            mContent = items;
        }

        public Dialog getDialog() {
            
            return mDialog;
        }
        
        public void setDialog(Dialog dialog) {
            
            mDialog = dialog;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {

            LayoutInflater inflater = 
                    (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            
            if (view == null)  // if it's not recycled,     
                view = inflater.inflate(mContentResid, null);
                
            TextView text = (TextView)view.findViewById(R.id.categoryText);
            text.setText(mContent[position]);
            return view;
        }
    }
}
