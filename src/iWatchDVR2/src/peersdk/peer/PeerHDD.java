package peersdk.peer;

import peersdk.NativeObject;


public class PeerHDD extends NativeObject {

    public PeerHDD(int nativeInstance) {
        native_setup(nativeInstance);
    }
    
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        native_finalize();
    }


    native private void native_setup(int nativeInstace);
    native private void native_finalize();
    
    
    //
    //
    native public long Capacity();
    native public long Available();
}
