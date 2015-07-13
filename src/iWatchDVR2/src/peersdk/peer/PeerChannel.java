package peersdk.peer;

import peersdk.NativeObject;


public class PeerChannel extends NativeObject {

    private PeerChannel(int nativeInstance) {
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
    native public int Index();
    native public String Name();
    native public PeerVideo Video();
    native public PeerAudio Audio();
}