package edu.umich.engin.cm.onecoolthing.StandaloneFragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

        // Create the dialog to show the Privacy Policy now
        dialogPrivacyPolicy = new Dialog(getActivity(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialogPrivacyPolicy.setContentView(viewDialog);

        // Set the close/dismiss button up
        viewDialog.findViewById(R.id.button_dismiss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                // Show the privacy policy dialog
                dialogPrivacyPolicy.show();
            }
        });
    }

    // Set the TutorialEnforcer
    public void setTutorialEnforcer(TutorialEnforcer enforcer) {
        this.mTutorialEnforcer = enforcer;
    }
}
