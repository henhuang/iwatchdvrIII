package peersdk.peer.event;

public class CompletedEventArgs {
    public boolean terminated;

    public CompletedEventArgs(boolean terminated) {
        this.terminated = terminated;
    }
}
