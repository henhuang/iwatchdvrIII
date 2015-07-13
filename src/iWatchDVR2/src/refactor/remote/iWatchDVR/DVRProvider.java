package refactor.remote.iWatchDVR;

import java.util.HashMap;

import refactor.remote.iWatchDVR.R;
import refactor.remote.iWatchDVR.R.string;
import refactor.remote.iWatchDVR.database.DVRHosts;
import refactor.remote.iWatchDVR.database.DVRHosts.DVR;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class DVRProvider extends ContentProvider {
    
    private static final String TAG = "__DVRProvider__";  
    
    private static final int    DVR_ID   = 0x10;
    private static final int    DVR_UUID = 0x11;
    private static final int    DVR_NONE = 0x12;
    
    // database name
    private static final String            DATABASE_NAME    = "dvr.db";  
    private static final int               DATABASE_VERSION = 5;  
    
    // table name
    private static final String            TABLE_NAME       = "DVR";  
    private static HashMap<String, String> sDVRHostMap;  

    private UriMatcher                     sUriMatcher;  
    private DatabaseHelper                 mOpenHelper;  

    private static final String            CREATE_TABLE ="CREATE TABLE IF NOT EXISTS "  
                                                        + TABLE_NAME
                                                        + " (" + DVR._ID
                                                        + " INTEGER PRIMARY KEY,"  
                                                        + DVR.NAME  
                                                        + " TEXT,"  
                                                        + DVR.HOST  
                                                        + " TEXT,"  
                                                        + DVR.PORT  
                                                        + " TEXT,"
                                                        + DVR.USER  
                                                        + " TEXT," 
                                                        + DVR.PASSWORD  
                                                        + " TEXT,"
                                                        + DVR.CHANNEL
                                                        + " INTEGER," 
                                                        + DVR.VERSION
                                                        + " TEXT,"
                                                        + DVR.UUID
                                                        + " TEXT"
                                                        + "); ";

    //Uri.parse  
    public static final String DVR_PATH               = "dvr";
    public static final String DVR_PATH_UUID          = "dvr/uuid";
    public static final String CONTENT_TYPE           = "vnd.android.cursor.dir/dvr";  
    public static final String CONTENT_TYPE_ITEM      = "vnd.android.cursor.item/dvr";
    public static final String CONTENT_TTPE_ITEM_UUID = "vnd.android.cursor.item/dvr/uuid";
    protected           Uri    CONTENT_URI;
    protected           String AUTHORITY;

    @Override
    public boolean onCreate() {

        mOpenHelper = new DatabaseHelper(getContext());
        
        
        AUTHORITY = getContext().getResources().getString(R.string.providerAuthority);
        CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + DVR_PATH);
        
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, DVR_PATH + "/#"     , DVR_ID);        // match: content://remote.iWatchDVR.DVRProvider/dvr/1
                                                                            // # -> number
        sUriMatcher.addURI(AUTHORITY, DVR_PATH_UUID + "/*", DVR_UUID);      // match: content://remote.iWatchDVR.DVRProvider/dvr/uuid/0000-0000-0000
                                                                            // * -> any token
        sUriMatcher.addURI(AUTHORITY, DVR_PATH, DVR_NONE);                  // match: content://remote.iWatchDVR.DVRProvider/dvr
        
        sDVRHostMap = new HashMap<String, String>();  
        sDVRHostMap.put(DVR._ID,      DVR._ID);  
        sDVRHostMap.put(DVR.NAME,     DVR.NAME);
        sDVRHostMap.put(DVR.HOST,     DVR.HOST);
        sDVRHostMap.put(DVR.PORT,     DVR.PORT);
        sDVRHostMap.put(DVR.USER,     DVR.USER);  
        sDVRHostMap.put(DVR.PASSWORD, DVR.PASSWORD);  
        sDVRHostMap.put(DVR.CHANNEL,  DVR.CHANNEL);
        sDVRHostMap.put(DVR.UUID,     DVR.UUID);
        sDVRHostMap.put(DVR.VERSION,  DVR.VERSION);
        
        return true;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();  
        int count = 0;  

        switch (sUriMatcher.match(uri)) {  
    
        case DVR_ID:  
            String id = uri.getPathSegments().get(1);  
                count = db.delete(TABLE_NAME, DVRHosts.DVR._ID + "=" + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);  
                break;  
                  
        default:  
            throw new IllegalArgumentException(TAG + "Unnown URI" + uri);  
        }
    
        getContext().getContentResolver().notifyChange(uri, null);  
        return count;
    }

    @Override
    public String getType(Uri uri) {
        
        switch (sUriMatcher.match(uri)) {
        
        case DVR_ID:
            return CONTENT_TYPE_ITEM;
            
        case DVR_UUID:
            return CONTENT_TTPE_ITEM_UUID;
            
        case DVR_NONE:
            return CONTENT_TYPE;
            
        default:
            throw new IllegalArgumentException(TAG + "Uri IllegalArgument:" + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();  
        long rowId = db.insert(TABLE_NAME, DVRHosts.DVR.NAME, values);

        if (rowId > 0) {
                
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowId);  
            getContext().getContentResolver().notifyChange(_uri, null);  
            return _uri;  
        }
        throw new SQLException("Failed to insert row into" + uri);
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();  

        switch (sUriMatcher.match(uri)) {  
        case DVR_NONE:
            //Log.i(TAG, "query: DVR_NONE");
            qb.setTables(TABLE_NAME);  
            qb.setProjectionMap(sDVRHostMap);  
            break;  
              
        case DVR_ID:
            //Log.i(TAG, "query: DVR_ID, " + uri.getPathSegments().get(1));
            qb.setTables(TABLE_NAME);  
            qb.setProjectionMap(sDVRHostMap);
            qb.appendWhere(DVRHosts.DVR._ID + "=" + uri.getPathSegments().get(1));  
            break;
            
        case DVR_UUID:
            //Log.i(TAG, "query: DVR_UUID, " + uri.getLastPathSegment());
            qb.setTables(TABLE_NAME);
            qb.setProjectionMap(sDVRHostMap);
            qb.appendWhere(DVRHosts.DVR.UUID + "='" + uri.getLastPathSegment() + "'");
            break;
            
        default:  
            throw new IllegalArgumentException(TAG + "Unknown URI " + uri);  
        }
        
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();  
        Cursor c = qb.query(db, null, selection, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c; 
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();  
        int count = 0;
        
        switch (sUriMatcher.match(uri)) {  

        case DVR_ID:   
            String id = uri.getPathSegments().get(1);  
            count = db.update(TABLE_NAME, values, DVRHosts.DVR._ID + "=" + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);  
            break;  
              
        default:  
            throw new IllegalArgumentException("Unknow URI " + uri);  
        }  

        return count;  
    }
    
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context){  
            super(context, DATABASE_NAME, null, DATABASE_VERSION);  
        }  

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            
            //Log.i(TAG, "db table onUpgrade: newVersion=" + newVersion + ", oldVersion=" + oldVersion);

            if (newVersion > oldVersion && newVersion == 5) {


                db.execSQL("ALTER TABLE " + TABLE_NAME 
                        + " ADD COLUMN " + DVR.UUID + " TEXT NOT NULL" + " DEFAULT ''");
                
                db.execSQL("ALTER TABLE " + TABLE_NAME 
                        + " ADD COLUMN " + DVR.VERSION + " TEXT NOT NULL" + " DEFAULT ''");
            }
            
            else {
                
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
                onCreate(db);
            }
        }
    }
}
