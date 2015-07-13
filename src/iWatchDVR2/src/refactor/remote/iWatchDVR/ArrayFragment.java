package refactor.remote.iWatchDVR;

import android.os.Bundle;
import android.support.v4.app.Fragment;


public class ArrayFragment extends Fragment {
    
    int mNum;
    int mkey;
    
    /**
     * Create a new instance of CountingFragment, providing "num"
     * as an argument.
     */
    static ArrayFragment newInstance(int num) {
        
        ArrayFragment f = new ArrayFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        args.putInt("key", 0);
        f.setArguments(args);

        return f;
    }
    
    static ArrayFragment newInstance(int num, int key) {

        ArrayFragment f = new ArrayFragment();

        Bundle args = new Bundle();
        args.putInt("num", num);
        args.putInt("key", key);
        f.setArguments(args);

        return f;
    }
}
