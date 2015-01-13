package edu.umich.engin.cm.onecoolthing.StandaloneFragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.umich.engin.cm.onecoolthing.Core.AnalyticsHelper;
import edu.umich.engin.cm.onecoolthing.R;

/**
 * Created by jawad on 02/12/14.
 */
public class AboutFragment extends android.support.v4.app.Fragment {
    // The TextViews which act as "buttons" of sorts
    TextView textViewTutorial;
    TextView textViewPrivacyPolicy;

    // Full screen dialog which will show the privacy policy
    Dialog dialogPrivacyPolicy;

    // View for the dialog
    View viewDialog;

    // Instance of TutorialEnforcer to tell to show the tutorial
    TutorialEnforcer mTutorialEnforcer;

    // States whether or not the Activity has been created yet
    boolean activityYetCreated = false;

    // Interface the Activity should implement to show the tutorial
    public interface TutorialEnforcer {
        // Tells the interface to show the tutorial
        public void showTutorialAgain();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        // Inflate the view for the Privacy Policy dialog
        viewDialog = inflater.inflate(R.layout.dialog_privacypolicy, null);

        // Cache the textViews to set listeners on them later
        textViewTutorial = (TextView) view.findViewById(R.id.text_reenable_tutorial);
        textViewPrivacyPolicy = (TextView) view.findViewById(R.id.text_privacypolicy);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activityYetCreated = true;

        // Send data that the About page has now been opened
        ((AnalyticsHelper) getActivity().getApplication()).sendScreenView(AnalyticsHelper.TrackerScreen.ABOUT);

        // Create the dialog to show the Privacy Policy now
        dialogPrivacyPolicy = new Dialog(getActivity(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialogPrivacyPolicy.setContentView(viewDialog);

        // Set the close/dismiss button up
        viewDialog.findViewById(R.id.button_dismiss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Try to be a bit more accurate and send data that the About Screen is now open again
                ((AnalyticsHelper) getActivity().getApplication()).sendScreenView(AnalyticsHelper.TrackerScreen.ABOUT);

                dialogPrivacyPolicy.dismiss();
            }
        });

        // Add the listeners now to the textViews
        textViewTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tell the enforcer to show the tutorial
                mTutorialEnforcer.showTutorialAgain();

               // Note: Chosen not to check if the tutorial enforcer is null or not
            }
        });

        textViewPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send some tracker data that the Privacy Policy is opening
                ((AnalyticsHelper) getActivity().getApplication()).sendScreenView(AnalyticsHelper.TrackerScreen.PRIVACYPOL);

                // Show the privacy policy dialog
                dialogPrivacyPolicy.show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        // If the Activity has been linked, then send data that the About page has been returned to
        if(activityYetCreated)
            ((AnalyticsHelper) getActivity().getApplication()).sendScreenView(AnalyticsHelper.TrackerScreen.ABOUT);
    }

    // Set the TutorialEnforcer
    public void setTutorialEnforcer(TutorialEnforcer enforcer) {
        this.mTutorialEnforcer = enforcer;
    }
}
