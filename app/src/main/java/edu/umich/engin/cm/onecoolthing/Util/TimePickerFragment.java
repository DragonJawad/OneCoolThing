package edu.umich.engin.cm.onecoolthing.Util;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by jawad on 20/09/15.
 */
public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    private TimePickerDialog.OnTimeSetListener mUserTimeListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void setOnTimeSetListener(TimePickerDialog.OnTimeSetListener onTimeSetListener) {
        mUserTimeListener = onTimeSetListener;
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if(mUserTimeListener != null)
            mUserTimeListener.onTimeSet(view, hourOfDay, minute);
    }
}