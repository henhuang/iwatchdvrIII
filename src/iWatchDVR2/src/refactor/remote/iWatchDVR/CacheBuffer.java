package refactor.remote.iWatchDVR;

import peersdk.peer.event.VideoLossChangedEventArgs;
import peersdk.peer.event.VideoMotionChangedEventArgs;
import refactor.remote.iWatchDVR.dvr.args.Channel;

public class CacheBuffer {

    public Channel[] channels;
    public VideoLossChangedEventArgs videoLossChangedEventArgs;
    public VideoMotionChangedEventArgs videoMotionChangedEventArgs;
}
