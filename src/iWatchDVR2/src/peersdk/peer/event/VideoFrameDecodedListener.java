package peersdk.peer.event;

public interface VideoFrameDecodedListener {
    void OnVideoOneFrameDecoded(int id, int channel, int width, int height, int[] outputSize, int format, int croppedLeft, int croppedTop);
}
