package refactor.remote.iWatchDVR;

import java.util.List;

import android.util.Pair;

public interface MultiSelectionCallback {
    void onSelectionUpdated(String tag, List<Pair<Integer, Boolean>> state);
}
