package peersdk;

import java.io.Serializable;
import java.util.Date;

public class TimeRange extends NativeObject implements Serializable {

    private static final long serialVersionUID = 147258369002L;
    
    protected TimeRange(int nativeInstance) {
        native_setup(nativeInstance);
    }
    
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        native_finalize();
    }
    
    native private void native_setup(int nativeInstance);
    native private void native_finalize();
    
    native public Date Begin();
    native public Date End();
}
