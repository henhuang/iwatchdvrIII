package refactor.remote.iWatchDVR.dvr;

import peersdk.TimeRange;
import peersdk.TimeSpan;
import peersdk.peer.PeerLog;
import peersdk.peer.event.ErrorOccurredEventArgs;
import peersdk.peer.event.VideoLossChangedEventArgs;
import peersdk.peer.event.VideoMotionChangedEventArgs;
import refactor.remote.iWatchDVR.dvr.args.Channel;
import refactor.remote.iWatchDVR.dvr.args.Relay;

public interface PeerDVRListener {
    void OnConnected(String host, boolean connected);
    void OnErrorOccurred(ErrorOccurredEventArgs arg);
    void OnVideoLossChanged(VideoLossChangedEventArgs arg);
    void OnVideoMotionChanged(VideoMotionChangedEventArgs arg);

    void GetInitialConfig(Channel[] channels, boolean hasRelayPermission, Relay[] relays, boolean notifySupported, boolean notifyEnabled);
    
    void GetChannel(Channel arg);
    void GetChannels(Channel[] arg);
    void GetLogs(PeerLog[] arg);
    
    void GetPTZAvailablePreset(int index, int[] arg);
    
    void GetRecordList(TimeRange[] arg);
    void GetRecordedMinutesOfDay(TimeSpan[] arg);
    void GetRecordedDaysOfMonth(int[] arg);
}
