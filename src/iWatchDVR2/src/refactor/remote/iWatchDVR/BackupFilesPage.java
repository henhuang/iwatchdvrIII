package refactor.remote.iWatchDVR;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore.Images;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class BackupFilesPage extends Fragment{
    
    public final static String TAG            = "__BackupFilesPage__";
    public final static String FILE_ID        = "file_id";
    public final static String FILE_SRC       = "file_src";
    public final static String FILE_THUMBNAIL = "file_thumbnail";

    protected final static String FILE_PATH_PREFIX = "file://";

    ArrayList<HashMap<String, Object>> mList = new ArrayList<HashMap<String, Object>>();
    MySimpleAdapter                    mAdapter;
    ListView                           mListView;
    int                                mIndex;
    Bitmap[]                           mThumbnail;
    
    ImageLoader                        mImageLoader;
    
    Handler mHandler;
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        
        final String rootBackupFolderPath =InitializeBackupFilesDir();
        if (rootBackupFolderPath == null) {
            
            Toast.makeText(getActivity(), R.string.noExternalStorage, Toast.LENGTH_LONG).show();
            return;
        }

        // initialize file loading and list view
        new Thread() {
            @Override
            public void run() {
                
                if (LoadFiles(rootBackupFolderPath) <= 0)
                    return;
                
                mImageLoader = new ImageLoader();
                mImageLoader.genThumbnail(rootBackupFolderPath + "/cache");
                
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {                     
                        mAdapter = new MySimpleAdapter(getActivity(), mList);

                        mListView = (ListView) getActivity().findViewById(R.id.list); 
                        mListView.setAdapter(mAdapter);
                    } 
                });
            }
        }.start();
    }

    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,   
                    Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.page_backup_files, container, false);

        Button back = (Button) v.findViewById(R.id.button_back);
        back.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {

                getActivity().finish();
            }
        });
        
        return v;
    }
    
    private int LoadFiles(String rootBackupFolderPath) {
        
        File files = new File(rootBackupFolderPath);
        File[] fileList = files.listFiles();

        if (fileList == null)
            return 0;

        for (int i = 0; i < fileList.length; i++) {
                
            String extension;
            int dotPos = fileList[i].getName().lastIndexOf(".");
            extension = fileList[i].getName().substring(dotPos + 1);
            if (extension.compareToIgnoreCase("mp4") != 0)
                continue;
                
            HashMap<String, Object> item = new HashMap<String, Object>();
            
            String name = fileList[i].getName().substring(0 , dotPos);
            FileHolder file = new FileHolder(rootBackupFolderPath, name, "mp4");
            file.setLength(fileList[i].length());
            
            item.put(FILE_SRC, file);
            item.put(FILE_ID,  i);
            mList.add(item);
        }

        return fileList.length;
    }

    private String InitializeBackupFilesDir() {
        
        String root = null;
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            
            File storage =Environment.getExternalStorageDirectory();
            root = storage.getPath() + "/" + getResources().getString(R.string.app_name);
        }    

        return root;
    }
    
    /////////////////////////////////////////////////////////////////////////////

    
    public class FileHolder {
        
        private String mPath;
        private String mName;
        private String mExtension;
        private long   mLength;
        
        FileHolder () {
            
        }
        
        FileHolder (String path, String name, String extension) {
            
            mPath = path;
            mName = name;
            mExtension = extension;
        }

        public String getPath() {
            
            return mPath;
        }
        
        public String getName() {
            
            return mName;
        }
        
        public String getFileName() {
            
            return mName + "." + mExtension;
        }
        
        public String getAbsoluteName() {
            
            return mPath + "/" + mName + "." + mExtension;
        }
        
        public void setLength(long value) {
            
            mLength = value;
        }
        
        public long getLength() {
            
            return mLength;
        }
        
        public String getExtension() {
            
            return mExtension;
        }
    }
    
    protected class ImageLoader {

        private FileHolder    mCacheDst;  // folder which stores thumbnail

        public FileHolder getCachePath() {
            
            return mCacheDst;
        }
        
        public void genThumbnail(final String dstPath) {

            File file = new File(dstPath);
            if (!file.exists()) {

                if (!file.mkdirs())
                    return;
            }
            
            mCacheDst = new FileHolder(dstPath, "", "png");
        }
        
        public void load(ImageView imageView, FileHolder src) {
 
            BitmapDownloaderTask task = new BitmapDownloaderTask(imageView);
            task.execute(src);
        }

        class BitmapHolder {
            
            public Bitmap  bmp;
            public String  tag;
        }
        
        
        class BitmapDownloaderTask extends AsyncTask<FileHolder, Void, BitmapHolder> {

            private final WeakReference<ImageView> imageViewReference;
            
            ReentrantLock mLock = new ReentrantLock();
            public BitmapDownloaderTask(ImageView imageView) {

                imageViewReference = new WeakReference<ImageView>(imageView);
            }
            
            @Override
            protected BitmapHolder doInBackground(FileHolder... params) {

                BitmapHolder holder = new BitmapHolder();
                FileHolder src = params[0];

                try {
                    
                    // lock here since BitmapFactory.createVideoThumbnail only to be call ONCE at one time
                    mLock.lock();
                    
                    FileHolder thumb = new FileHolder(mCacheDst.getPath(), src.getName(), "png");
    
                    if (new File(thumb.getAbsoluteName()).exists()) {
                        holder.bmp = BitmapFactory.decodeFile(thumb.getAbsoluteName());
                        return holder;
                    }

                    holder.bmp = ThumbnailUtils.createVideoThumbnail(src.getAbsoluteName(), Images.Thumbnails.MICRO_KIND);
                    if (holder.bmp == null) {

                        Log.w(TAG, "create thumbnail error=" + src.getAbsoluteName());
                        return holder;
                    }
                    holder.tag = thumb.getAbsoluteName();

                    FileOutputStream out = new FileOutputStream(thumb.getAbsoluteName());
                    if (!holder.bmp.compress(Bitmap.CompressFormat.PNG, 90, out))
                        Log.w(TAG, "bmp compress warining");
                    
                    out.flush();
                    out.close();


                } catch (Exception e) {

                    e.printStackTrace();
                } finally {
                    
                    mLock.unlock();
                }
                return holder;
            }

            @Override
            protected void onPostExecute(BitmapHolder holder) {
                
                if (isCancelled())
                    return;


                if (imageViewReference != null) {
                    
                    ImageView imageView = imageViewReference.get();
                    if (imageView != null) {
                        
                        if (holder.bmp ==null)
                            imageView.setImageResource(R.drawable.button_div1);
                        else
                            imageView.setImageBitmap(holder.bmp);
                    }
                }

            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    private class MySimpleAdapter extends BaseAdapter  {

        private List<? extends Map<String, ?>> mData;
        private LayoutInflater mInflater;
        
        public MySimpleAdapter(Context context, List<? extends Map<String, ?>> data) {
            
            super();
            mData = data;
            mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);        
        }

        @Override
        public View getView(final int position, View row, ViewGroup parent) {
 
            // TODO: keep getView's order to use a existed row
            //
            // IMPORT: new a row every time we getViewotherwise it will cause the thumb nail loading error 
            // that implemented in AsyncTask
            //if (row == null)
                row = mInflater.inflate(R.layout.listview_row_backup_files, null);

           HashMap<String, Object> item = (HashMap<String, Object>) getItem(position);

            final FileHolder srcfile = (FileHolder) item.get(FILE_SRC);

            row.setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri uri = Uri.parse(((FileHolder) mList.get(position).get(FILE_SRC)).getAbsoluteName()); 
                    intent.setDataAndType(uri, "video/mp4"); 
                    getActivity().startActivity(intent);
                }
            });
            
            row.setOnLongClickListener(new View.OnLongClickListener() {
                
                @Override
                public boolean onLongClick(View v) {
                    
                    final Context contex = v.getContext();
                    
                    new AlertDialog.Builder(contex)
                    .setMessage(R.string.confirmDeleteFile)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                            
                            File file = new File(srcfile.getAbsoluteName());
                            if (!file.delete()) {
                                Toast.makeText(contex, R.string.deleteFailed, Toast.LENGTH_LONG).show();
                                return;
                            }

                            FileHolder cache = mImageLoader.getCachePath();
                            FileHolder thumb = new FileHolder(cache.getPath(), srcfile.getName(), cache.getExtension());
                            new File(srcfile.getAbsoluteName()).delete(); // delete .mp4
                            new File(thumb.getAbsoluteName()).delete();    // delete thumbnail
                            
                            mData.remove(position);
                            notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton(R.string.cancel,new DialogInterface.OnClickListener() {
                        
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            
                            dialog.dismiss();
                        }
                    })
                    .show(); 
                    return true;
                }
            });

            TextView file = (TextView) row.findViewById(R.id.text_file_name);
            file.setText(srcfile.getFileName());

            TextView size = (TextView) row.findViewById(R.id.text_file_size);
            size.setText(Long.toString(srcfile.getLength()));
            
            ImageView thumbView = (ImageView) row.findViewById(R.id.thumbnail);
            //thumbView.setTag(srcfile.getFileName());
            mImageLoader.load(thumbView, srcfile);

            return row;
        }

        @Override
        public int getCount() {

            return mData.size();
        }

        @Override
        public Object getItem(int arg0) {

            return mData.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {

            return arg0;
        }

    }
}


