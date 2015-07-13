package peersdk.peer;

import peersdk.NativeObject;
import peersdk.peer.event.ErrorOccurredEventArgs;
import peersdk.peer.event.VideoLossChangedEventArgs;
import peersdk.peer.event.VideoMotionChangedEventArgs;

public class Peer extends NativeObject {

    public Peer() {
        native_setup();
    }
    
    public void Dispose() {
        native_finalize();
    }


    native private void native_setup();
    native private void native_finalize();
    
    native private void native_setOnVideoLossChangedListener();
    native private void native_setOnVideoMotionChangedListener();
    native private void native_setOnErrorOccurredListener();
    
    //
    //
    
    public interface ErrorOccurredListener {
        void OnErrorOccurred(Peer peer, ErrorOccurredEventArgs arg);
    }
    
    public interface VideoLossChangedListener {
        void OnVideoLossChanged(Peer peer, VideoLossChangedEventArgs arg);
    }

    public interface VideoMotionChangedListener {
        void OnVideoMotionChanged(Peer peer, VideoMotionChangedEventArgs arg);
    }


    private VideoLossChangedListener mVideoLossChangedListener;
    private VideoMotionChangedListener mVideoMotionChangedListener;
    private ErrorOccurredListener mErrorOccurredListener;
    
    public void SetOnVideoLossChangedListener(VideoLossChangedListener listener) {
        mVideoLossChangedListener = listener; 
        native_setOnVideoLossChangedListener(); // OnVideoLossChanged is invoked within native
    }
    
    public void SetOnVideoMotionChangedListener(VideoMotionChangedListener listener) {
        mVideoMotionChangedListener = listener; 
        native_setOnVideoMotionChangedListener(); // OnVideoMotionChanged is invoked within native
    }
    
    public void SetOnErrorOccurredListener(ErrorOccurredListener listener) {
        mErrorOccurredListener = listener; 
        native_setOnErrorOccurredListener(); // OnVideoMotionChanged is invoked within native
    }
    
    
    native public boolean Connect(String host, int port, String user, String password, String type, boolean tryOthers);
    native public boolean CreateLiveStream(PeerStream stream);
    native public boolean CreateRecordedStream(int startYear, int startMonth, int startDay, int startHour, int startMinute, int startSecond, PeerStream stream);
    native public boolean CreateRecordedStream(int startYear, int startMonth, int startDay, int startHour, int startMinute, int startSecond, 
                                               int endYear, int endMonth, int endDay, int endHour, int endMinute, int endSecond, PeerStream stream);
    native public boolean CreateRecordList(PeerRecordList recordList);
    native public PeerLog[] GetLogList();
    native public PeerHDD[] GetHDDList();
    native public void Ack();
    native public boolean IsAvailableVideoFormatDetection(int index);
    native public void SetVideoFormatDetectMethod(int index);
    native public boolean IsSupportedDeviceUUID();
    native public boolean IsSupportedPushNotification();
    native public boolean RegisterPushNotification(String token);
    native public boolean UnregisterPushNotification(String token);
    native public boolean IsPushNotificationRegistered(String token);
    native public void Startup();
    native public void Cleanup();
    native public String Version();
    native public int VideoFormatDetectMethod();
    native public int OutputVideoFormat();
    native public String DeviceUUID();
    native public PeerChannel[] Channels();
    native public PeerRelay[] Relays();
    native public PeerRecorder Recorder();
}
