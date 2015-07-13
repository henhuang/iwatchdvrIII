package peersdk.peer;

import peersdk.NativeObject;

public class PeerRecorder extends NativeObject {

    protected PeerRecorder(int nativeInstace) {
        native_setup(nativeInstace);
    }
    
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        native_finalize();
    }


    native protected void native_setup(int nativeInstace);
    native protected void native_finalize();
    
    //
    //
    native public int[] RecordStatus();

}
