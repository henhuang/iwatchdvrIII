package peersdk.peer.event;

import java.util.Date;

public class VideoArrivedEventArgs {
    
    public int       type;
    public int       channel;
    public int       bufferLength;
    public int       nativeBufferPointer;
    public long      pts;
    public Date      time;
    public int       width;
    public int       height;
    public float     speed;
    
    VideoArrivedEventArgs(int type, int channel, int bufferLength, int nativeBufferPointer,
            long pts, int width, int height, float speed, Date time) {
        this.type         = type;
        this.channel      = channel;
        this.bufferLength = bufferLength;
        this.nativeBufferPointer = nativeBufferPointer;
        this.pts          = pts;
        this.width        = width;
        this.height       = height;
        this.speed        = speed;
        this.time         = time;
    }
}