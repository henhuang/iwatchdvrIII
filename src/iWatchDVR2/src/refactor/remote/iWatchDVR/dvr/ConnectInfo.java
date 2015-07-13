package refactor.remote.iWatchDVR.dvr;

import android.os.Parcel;
import android.os.Parcelable;

public class ConnectInfo implements Parcelable {
    
    public String  host;
    public int     port;
    public String  user;
    public String  pwd;
    public boolean isP2P = false;
    public String  uid;
    
    public static final Parcelable.Creator<ConnectInfo> CREATOR = new Creator() {
        
        @Override
        public ConnectInfo createFromParcel(Parcel source) {
            ConnectInfo info = new ConnectInfo();
            info.host = source.readString();
            info.port = source.readInt();
            info.user = source.readString();
            info.pwd = source.readString();
            info.isP2P = source.readByte() == 1;
            info.uid = source.readString();
            return info;
        }
        
        @Override
        public ConnectInfo[] newArray(int size) {
            return new ConnectInfo[size];
        }
    };
    
    public ConnectInfo() {
        
    }
    
    ConnectInfo(String host, int port, String user, String pwd) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.pwd = pwd;
        this.isP2P = false;
    }
    
    ConnectInfo(String uid, String user, String pwd) {
        this.uid = uid;
        this.user = user;
        this.pwd = pwd;
        this.isP2P = true;
    }
    
    public void setHost(String host) {
        this.host = host;
    }
    
    public void setPort(int port) {
        this.port = port;
    }
    
    public void setUser(String user) {
        this.user = user;
    }
    
    public void setPassword(String pwd) {
        this.pwd = pwd;
    }
    
    public void setIsP2P(boolean p2p) {
        this.isP2P = p2p;
    }
    
    public void setUID(String uid) {
        this.uid = uid;
    }
    
    public String getHost() {
        return this.uid;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public String getUser() {
        return this.user;
    }
    
    public String getPassword() {
        return this.pwd;
    }
    
    public boolean getIsP2P() {
        return this.isP2P;
    }
    
    public String getUID() {
        return this.uid;
    }
    
    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.host);
        dest.writeInt(this.port);
        dest.writeString(this.user);
        dest.writeString(this.pwd);
        dest.writeByte((byte)(this.isP2P ? 1 : 0));
        dest.writeString(this.uid);
    }
}

