package peersdk.peer;

import peersdk.NativeObject;


public class PeerRelay extends NativeObject {

    protected PeerRelay(int nativeInstance) {
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
    native public boolean Activate(int seconds);
    native public boolean Switch(int pole);
    native public int Pole();
}