package refactor.remote.iWatchDVR.dvr;

import peersdk.peer.event.ErrorOccurredEventArgs;

public interface PeerStreamListener {
    void OnStreamStartConnect(int channel);
    void OnVideoFirstFrameArrived(int channel);
    void OnStreamErrorOccurred(ErrorOccurredEventArgs arg);
}
