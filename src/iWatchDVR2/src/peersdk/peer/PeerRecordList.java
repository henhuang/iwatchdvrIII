package peersdk.peer;

import peersdk.NativeObject;
import peersdk.TimeRange;
import peersdk.TimeSpan;

public class PeerRecordList extends NativeObject {

    public PeerRecordList() {
        native_setup();
    }
    
    public void Dispose() {
        native_finalize();
    }
    
    native private void native_setup();
    native private void native_finalize();
    
    
    native public int[] GetRecordedDaysOfMonth(int year, int month);
    native public TimeSpan[] GetRecordedMinutesOfDay(int year, int month, int day);
    native public TimeRange[] GetList();
}
