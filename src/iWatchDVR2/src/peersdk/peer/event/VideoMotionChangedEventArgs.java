package peersdk.peer.event;

public class VideoMotionChangedEventArgs {
    public int[]       active;
    public int[]       deactive;

    VideoMotionChangedEventArgs(int[] active, int[] deactive) {
        this.active       = active;
        this.deactive     = deactive;
    }
}
