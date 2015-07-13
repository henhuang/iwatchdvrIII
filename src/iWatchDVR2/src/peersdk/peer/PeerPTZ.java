package peersdk.peer;

import peersdk.NativeObject;


public class PeerPTZ extends NativeObject {

    protected PeerPTZ(int nativeInstace) {
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
    native public int Stop();
    native public void Move(double x, double y, boolean stop);
    native public void DoZoom(double speed, boolean stop);
    native public void DoFocus(double speed, boolean stop);
    native public void DoIris(double speed, boolean stop);
    native public void DoAutoFocus();
    native public void DoAutoIris();
    native public void SetPreset(int index);
    native public void GoPreset(int index);
    native public boolean CanZoom();
    native public boolean CanFocus();
    native public boolean CanIris();
    native public boolean CanAutoFocus();
    native public boolean CanAutoIris();
    native public boolean CanPreset();
    native public int[] GetAvailablePreset();
    native public boolean Enabled();
}