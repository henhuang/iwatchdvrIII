package refactor.remote.iWatchDVR;

import java.util.Calendar;
import refactor.remote.iWatchDVR.widget.Calendar.CalendarView;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

public class DateTimeFragment extends CallbackDialogFragment {

    View mView;
    DateTimeCallback mCallback;
    
    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);	
		mCallback = (DateTimeCallback)getActivity();
	}
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Context context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        mView = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).
                            inflate(R.layout.fragment_datetime, null);

        builder.setView(mView)
        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                dialog.dismiss();
                
                CalendarView calender = (CalendarView)mView.findViewById(R.id.calendar);
                Clock clock = (Clock)mView.findViewById(R.id.clock);
                Calendar c = Calendar.getInstance();
                c.set(calender.Year(), calender.Month(), calender.Day(), clock.getHour(), clock.getMin());
                mCallback.onDateTimeUpdated(mTag, c.getTimeInMillis() / 1000000);
            }

        })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                dialog.dismiss();
            }
        });
        return builder.create();
    }
}
