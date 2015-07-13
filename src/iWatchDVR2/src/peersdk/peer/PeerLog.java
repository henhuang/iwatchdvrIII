package peersdk.peer;

import java.util.Date;

import peersdk.NativeObject;


public class PeerLog extends NativeObject {

    private PeerLog(int nativeInstance) {
        native_setup(nativeInstance);
    }
    
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        native_finalize();
    }


    native private void native_setup(int nativeInstance);
    native private void native_finalize();
    
    
    //
    //
    native public Date Time();
    native public int Type();
    native public boolean Playable();
    native public boolean HasChannel();
    native public int Channel();
}