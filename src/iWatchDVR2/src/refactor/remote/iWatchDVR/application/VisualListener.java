package refactor.remote.iWatchDVR.application;

public interface VisualListener {

    void onVisualCreated();
    void onVisualChange(int divide);
    void onVisualChange(int divide, int defaultID);
}
