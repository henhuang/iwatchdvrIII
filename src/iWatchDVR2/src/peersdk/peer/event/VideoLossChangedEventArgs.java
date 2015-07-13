package peersdk.peer.event;

public class VideoLossChangedEventArgs {
    public int[]       active;
    public int[]       deactive;

    VideoLossChangedEventArgs(int[] active, int[] deactive) {
        this.active       = active;
        this.deactive     = deactive;
    }
}
