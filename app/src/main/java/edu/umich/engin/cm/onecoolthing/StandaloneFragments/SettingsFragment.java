package edu.umich.engin.cm.onecoolthing.StandaloneFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.umich.engin.cm.onecoolthing.R;

/**
 * Created by jawad on 20/09/15.
 */
public class SettingsFragment extends Fragment {
    TextView mTutorialTextView;

    // Instance of TutorialEnforcer to tell to show the tutorial
    TutorialEnforcer mTutorialEnforcer;

    // Interface the Activity should implement to show the tutorial
    public interface TutorialEnforcer {
        // Tells the interface to show the tutorial
        public void showTutorialAgain();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Cache the textViews to set listeners on them later
        mTutorialTextView = (TextView) view.findViewById(R.id.text_reenable_tutorial);

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
    }

    // Set the TutorialEnforcer
    public void setTutorialEnforcer(TutorialEnforcer enforcer) {
        this.mTutorialEnforcer = enforcer;
    }
}
