package peersdk;

/**
 * @brief Specifies type of audio sample.
 */

public class AudioType {

    /**
     * Indicates empty audio sample.
     */
    public static final int AudioType_None = 0;
    
    /**
     * Indicates <b>IMA ADPCM</b>, mono, 16-bit depth, 8000Hz.
     */
    public static final int AudioType_IMA_ADPCM_16Bits_8000Hz = 1;
    
    /**
     * Indicates <b>Linear PCM</b>, mono, 16-bit depth, 8000Hz.
     */
    public static final int AudioType_LPCM_16Bits_8000Hz = 2;
    
    /**
     * Indicates <b>Linear PCM</b>, mono, 8-bit depth, 8000Hz.
     */
    public static final int AudioType_LPCM_8Bits_8000Hz = 3;

}