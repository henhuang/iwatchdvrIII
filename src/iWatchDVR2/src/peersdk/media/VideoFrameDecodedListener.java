package peersdk.media;

public interface VideoFrameDecodedListener {
    void OnVideoOneFrameDecoded(int channel, int width, int height,
        int[] outputSize, int format, int croppedLeft, int croppedTop);
}