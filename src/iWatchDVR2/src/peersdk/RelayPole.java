package peersdk;

/**
 * @brief Specifies pole type of relay.
 */

public class RelayPole {

    /**
     * Indicates relay switch its pole in seconds.
     */
    public static final int Legacy = 0;
    
    /**
     * Indicates pole of relay is controlled by DVR.
     */
    public static final int Auto = 1;
    
    /**
     * Indicates pole of relay is normally-open.
     */
    public static final int NO = 2;
    
    /**
     * Indicates pole of relay is normally-close.
     */
    public static final int NC = 3;
}
