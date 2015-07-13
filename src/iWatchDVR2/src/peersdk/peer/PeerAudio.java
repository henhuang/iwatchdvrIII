package peersdk.peer;

import peersdk.NativeObject;


public class PeerAudio extends NativeObject {

    public PeerAudio(int nativeInstance) {
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
    native public boolean IsPresent();
}
