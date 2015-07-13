package peersdk.peer;

import peersdk.NativeObject;


public class PeerVideo extends NativeObject {

    protected PeerVideo(int nativeInstance) {
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
    native public PeerPTZ PTZ();
    native public boolean IsLoss();
    native public boolean IsMotion();
    native public int Format();
}
