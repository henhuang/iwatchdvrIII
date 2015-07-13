package peersdk.peer.event;

import java.util.Date;

public class AudioArrivedEventArgs {
    
    public int    type;
    public int    channel;
    public int    bufferLength;
    public int    nativeBufferPointer;
    public long   pts;
    public Date   time;
    
    AudioArrivedEventArgs(int type, int channel, int bufferLength, int nativeBufferPointer, long pts, Date time) {
        this.type = type;
        this.channel = channel;
        this.bufferLength = bufferLength;
        this.nativeBufferPointer = nativeBufferPointer;
        this.pts = pts;
        this.time = time;
    }
}
