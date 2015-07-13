package peersdk;

public class TimeSpan extends NativeObject {

    protected TimeSpan(int nativeInstance) {
        native_setup(nativeInstance);
    }
    
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        native_finalize();
    }
    
    native private void native_setup(int nativeInstance);
    native private void native_finalize();
    
    native public int Hours();
    native public int Minutes();
}
