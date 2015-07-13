package peersdk.peer;

import java.util.Calendar;
import java.util.Date;

import android.util.Log;

import peersdk.NativeObject;
import peersdk.media.MediaDispatcher;
import peersdk.peer.event.AudioArrivedEventArgs;
import peersdk.peer.event.CompletedEventArgs;
import peersdk.peer.event.ErrorOccurredEventArgs;
import peersdk.peer.event.ProgressChangedEventArgs;
import peersdk.peer.event.VideoArrivedEventArgs;

public class PeerStream extends NativeObject {

    final public static int HQ = 1;
    final public static int LQ = 0;
    
    public boolean firstFrameGot = false;
    
    public int connectStatus = StreamConntectStatus.None;
    
    public class StreamConntectStatus {
        final static public int None      = 0x0;
        final static public int Prepare   = 0x1;
        final static public int Streaming = 0x2;
        final static public int Error     = 0x3;
    }
    
    static public PeerStream CreateLive(Peer peer) {
        PeerStream stream = new PeerStream();
        if (peer.CreateLiveStream(stream))
            return stream;
        return null;
    }
    
    static public PeerStream CreateRecord(Peer peer, Date start) {
        PeerStream stream = new PeerStream();
        
        /*
        * @param [in] year    The year (1970 through 2038).
        * @param [in] month   The month (1 through 12).
        * @param [in] day     The day (1 through the number of days in month).
        * @param [in] hour    The hours (0 through 23).
        * @param [in] minute  The minutes (0 through 59).
        * @param [in] second  The seconds (0 through 59).
        */
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        int startYear = calendar.get(Calendar.YEAR) + 1900;
        int startMonth = calendar.get(Calendar.MONTH) + 1;
        int startDay = calendar.get(Calendar.DATE);
        int startHour = calendar.get(Calendar.HOUR_OF_DAY);
        int startMinute = calendar.get(Calendar.MINUTE);
        int startSecond = calendar.get(Calendar.SECOND);
        Log.i("PeerStream", startYear + "-" + startMonth + "-" + startDay+ "-" + startHour + ":" + startMinute + ":" + startSecond);
        if (peer.CreateRecordedStream(startYear, startMonth, startDay, startHour, startMinute, startSecond,  stream))
            return stream;
        return null;
    }
    
    static public PeerStream CreateRecord(Peer peer, Date start, Date end) {
        PeerStream stream = new PeerStream();
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        int startYear = calendar.get(Calendar.YEAR) + 1900;
        int startMonth = calendar.get(Calendar.MONTH) + 1;
        int startDay = calendar.get(Calendar.DATE);
        int startHour = calendar.get(Calendar.HOUR_OF_DAY);
        int startMinute = calendar.get(Calendar.MINUTE);
        int startSecond = calendar.get(Calendar.SECOND);
        Log.i("PeerStream", startYear + "-" + startMonth + "-" + startDay+ "-" + startHour + ":" + startMinute + ":" + startSecond);

        calendar.setTime(end);
        int endYear = calendar.get(Calendar.YEAR) + 1900;
        int endMonth = calendar.get(Calendar.MONTH) + 1;
        int endDay = calendar.get(Calendar.DATE);
        int endHour = calendar.get(Calendar.HOUR_OF_DAY);
        int endMinute = calendar.get(Calendar.MINUTE);
        int endSecond = calendar.get(Calendar.SECOND);
        
        if (peer.CreateRecordedStream(startYear, startMonth, startDay, startHour, startMinute, startSecond, 
                                      endYear, endMonth, endDay, endHour, endMinute, endSecond, 
                                      stream))
            return stream;
        return null;
    }
    
    private PeerStream() {
        native_setup();
    }
    
    public void Dispose() {
        native_finalize();
    }
    
    native private void native_setup();
    native private void native_finalize();
    
    ////
    
    public interface AudioArrivedListener {
        void OnAudioArrived(PeerStream stream, AudioArrivedEventArgs arg);
    }

    public interface VideoArrivedListener {
        void OnVideoArrived(PeerStream stream, VideoArrivedEventArgs arg);
    }

    public interface ErrorOccurredListener {
        void OnErrorOccurred(PeerStream stream, ErrorOccurredEventArgs arg);
    }
    
    public interface CompletedListener {
        void OnCompleted(PeerStream stream, CompletedEventArgs arg);
    }
    
    public interface ProgressChangedListener {
        void OnProgressChanged(PeerStream stream, ProgressChangedEventArgs arg);
    }
    
    native private void native_setOnAudioArrivedListener();
    native private void native_setOnVideoArrivedListener();
    native private void native_setOnErrorOccurredListener();
    native private void native_setOnCompletedListener();
    native private void native_setOnProgressChangedListener();
    
    //
    //
    private AudioArrivedListener mAudioArrivedListener;
    private VideoArrivedListener mVideoArrivedListener;
    private ErrorOccurredListener mErrorOccurredListener;
    private CompletedListener mCompletedListener;
    private ProgressChangedListener mProgressChangedListener;
    
    public void SetAudioArrivedListener(AudioArrivedListener listener) {
        mAudioArrivedListener = listener;
        native_setOnAudioArrivedListener();
    }
    
    public void SetVideoArrivedListener(VideoArrivedListener listener) {
        mVideoArrivedListener = listener;
        native_setOnVideoArrivedListener();
    }
    
    public void SetErrorOccurredListener(ErrorOccurredListener listener) {
        mErrorOccurredListener = listener;
        native_setOnErrorOccurredListener();
    }
    
    public void SetCompletedListener(CompletedListener listener) {
        mCompletedListener = listener;
        native_setOnCompletedListener();
    }
    
    public void SetProgressChangedListener(ProgressChangedListener listener) {
        mProgressChangedListener = listener;
        native_setOnProgressChangedListener();
    }
    

    native public boolean Start();
    native public boolean SetActive(int active);
    native public boolean SetChannelMask(int videoMask, int audioMask);
    native public boolean Play();
    native public boolean Pause();
    native public boolean Step();
    native public boolean SetSpeed(double speed);
    native public long CalculateRequiredSpace();
    native public boolean SetDownloadLimit(long limit);

    native public int Available();
    native public int Active();
    native public boolean CanSeek();
    native public double Speed();
    native public long DownloadLimit();
    native public long DownloadRate();

    public void setDispatcher(MediaDispatcher mDispatcher) {
        // TODO Auto-generated method stub
        
    }
}
