package refactor.remote.iWatchDVR.dvr.args;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;
import peersdk.VideoType;


public class Channel implements Serializable {
    private static final long serialVersionUID = 147258369000L;
    public int id = -1;
    public String name;
    public boolean ptzEnabled = false;
    public boolean audioPresented = false;
    public boolean videoIsLoss = false;
    public boolean videoIsMotion = false;
    public int videoFormat = VideoType.VideoType_None;
}

