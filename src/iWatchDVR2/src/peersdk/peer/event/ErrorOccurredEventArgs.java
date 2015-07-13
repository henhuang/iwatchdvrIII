package peersdk.peer.event;

public class ErrorOccurredEventArgs {

    protected  boolean result;
    protected  String  message;
    
    public ErrorOccurredEventArgs(boolean result, String message) {
        this.result = result;
        this.message = message;
    }
}

