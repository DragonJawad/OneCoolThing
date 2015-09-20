package edu.umich.engin.cm.onecoolthing.StandaloneFragments;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import edu.umich.engin.cm.onecoolthing.R;
import edu.umich.engin.cm.onecoolthing.Util.Constants;
import edu.umich.engin.cm.onecoolthing.Util.TimePickerFragment;

/**
 * Created by jawad on 20/09/15.
 */
public class SettingsFragment extends Fragment implements TimePickerDialog.OnTimeSetListener {
    TextView mTutorialTextView;

    // Instance of TutorialEnforcer to tell to show the tutorial
    TutorialEnforcer mTutorialEnforcer;

    ToggleButton mToggleBtnShake;
    ToggleButton mToggleBtnDailyDose;
    Button mBtnChangeNotifTime;

    // Interface the Activity should implement to show the tutorial
    public interface TutorialEnforcer {
        // Tells the interface to show the tutorial
        public void showTutorialAgain();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Cache the basic views that need to be changed later on
        mTutorialTextView = (TextView) view.findViewById(R.id.text_reenable_tutorial);
        mToggleBtnShake = (ToggleButton) view.findViewById(R.id.toggleShake);
        mToggleBtnDailyDose = (ToggleButton) view.findViewById(R.id.toggleDailyDose);
        mBtnChangeNotifTime = (Button) view.findViewById(R.id.buttonChangeTime);


        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mTutorialTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tell the enforcer to show the tutorial
                mTutorialEnforcer.showTutorialAgain();

                // Note: Chosen not to check if the tutorial enforcer is null or not
            }
        });
        // Get the shared preferences to load in the previous values for the different settings
        SharedPreferences sharedPreferences = getActivity().getPreferences(getActivity().MODE_PRIVATE);

        mToggleBtnShake.setChecked(sharedPreferences.getBoolean(Constants.KEY_ENABLESHAKE, true));
        mToggleBtnDailyDose.setChecked(sharedPreferences.getBoolean(Constants.KEY_ENABLEDAILYDOSE, false));

        setTimeButtonText(
                sharedPreferences.getInt(Constants.KEY_DAILYNOTIFTIME_HOUR, 8),
                sharedPreferences.getInt(Constants.KEY_DAILYNOTIFTIME_MINUTE, 0)
        );

        mToggleBtnShake.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                // Save the new value of the Shake for Random setting
                SharedPreferences sharedPreferences = getActivity().getPreferences(getActivity().MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                // Set the saved bool to whatever isTutorialSeen is
                editor.putBoolean(Constants.KEY_ENABLESHAKE, b);

                // Commit the changes
                editor.apply();
            }
        });

        mToggleBtnDailyDose.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                // Save the new value of the Shake for Random setting
                SharedPreferences sharedPreferences = getActivity().getPreferences(getActivity().MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                // Set the saved bool to whatever isTutorialSeen is
                editor.putBoolean(Constants.KEY_ENABLEDAILYDOSE, b);

                // Commit the changes
                editor.apply();
            }
        });

        mBtnChangeNotifTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerFragment timePickerDialog = new TimePickerFragment();
                timePickerDialog.setOnTimeSetListener(SettingsFragment.this);
                timePickerDialog.show(getFragmentManager(), "timePicker");
            }
        });
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        // Store the new values
        SharedPreferences sharedPreferences = getActivity().getPreferences(getActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Constants.KEY_DAILYNOTIFTIME_HOUR, hourOfDay);
        editor.putInt(Constants.KEY_DAILYNOTIFTIME_MINUTE, minute);
        editor.apply();

        // Change the time button text display
        setTimeButtonText(hourOfDay, minute);
    }

    private void setTimeButtonText(int hourOfDay, int minute) {
        // The suffix of the time stamp text of the button depends on if the hour is >= 12
        String suffix = (hourOfDay < 12) ? "AM" : "PM";

        // If the hour of the day is above 12, chop it down by 12 for the 12 hour clock display
        if(hourOfDay > 12) {
            hourOfDay -= 12;
        }
        // Otherwise, if the hour of the day is 0, show 12 (so it would be 12:mm AM)
        else if(hourOfDay == 0) {
            hourOfDay = 12;
        }

        // Create the final display text
        String displayMinute = (minute < 10) ? "0" + minute : "" + minute;
        String displayText = hourOfDay + ":" + displayMinute + " " + suffix;

        // Finally, change the text of the button
        mBtnChangeNotifTime.setText(displayText);
    }

    // Set the TutorialEnforcer
    public void setTutorialEnforcer(TutorialEnforcer enforcer) {
        this.mTutorialEnforcer = enforcer;
    }
}
