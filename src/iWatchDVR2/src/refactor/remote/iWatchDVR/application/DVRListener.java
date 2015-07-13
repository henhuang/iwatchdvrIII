package refactor.remote.iWatchDVR.application;

import peersdk.TimeRange;
import peersdk.TimeSpan;
import peersdk.peer.PeerLog;
import peersdk.peer.event.ErrorOccurredEventArgs;
import peersdk.peer.event.VideoLossChangedEventArgs;
import peersdk.peer.event.VideoMotionChangedEventArgs;
import refactor.remote.iWatchDVR.dvr.args.Channel;
import refactor.remote.iWatchDVR.dvr.args.Relay;

public interface DVRListener {
    void OnConnected(String host);
    void OnDisconnected(String host);
    void OnVideoLossChanged(VideoLossChangedEventArgs arg);
    void OnVideoMotionChanged(VideoMotionChangedEventArgs arg);
    void OnErrorOccurred(ErrorOccurredEventArgs arg);
    
    void OnGetInitialConfig(Channel[] channels, boolean hasRelayPermission, Relay[] relays, boolean notifySupported, boolean notigyEnabled);

    void OnGetChannel(Channel arg);
    void OnGetChannels(Channel[] arg);
    
    void GetLogs(PeerLog[] arg);

    void OnGetPTZAvailablePreset(int index, int[] arg);
    
    void OnGetRecordList(TimeRange[] arg);
    void OnGetRecordedMinutesOfDay(TimeSpan[] arg);
    void OnGetRecordedDaysOfMonth(int[] arg);
    
    ////////////////////////////////
    
    void OnStreamStartConnect(int channel);
    void OnVideoFirstFrameArrived(int channel);
    void OnStreamErrorOccurred(ErrorOccurredEventArgs arg);
}
