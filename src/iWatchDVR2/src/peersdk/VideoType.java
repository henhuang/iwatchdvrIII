package peersdk;

/**
 * @brief Specifies type of video frame.
 */

public class VideoType {

    /**
     * Indicates empty video frame.
     */
    public static final int VideoType_None = 0;
    
    /**
     * Indicates \b H264 I-frame.
     */
    public static final int VideoType_H264_IFrame = 1;
    
    /**
     * Indicates \b H264 P-frame.
     */
    public static final int VideoType_H264_PFrame = 2;
}
