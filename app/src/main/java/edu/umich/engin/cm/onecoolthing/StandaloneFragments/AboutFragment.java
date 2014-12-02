package edu.umich.engin.cm.onecoolthing.StandaloneFragments;

import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import edu.umich.engin.cm.onecoolthing.R;

/**
 * Created by jawad on 02/12/14.
 */
public class AboutFragment extends Fragment {
    // The TextViews which act as "buttons" of sorts
    TextView textViewTutorial;
    TextView textViewPrivacyPolicy;

    // Full screen dialog which will show the privacy policy
    Dialog dialogPrivacyPolicy;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

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
        dialogPrivacyPolicy.setContentView(R.layout.dialog_privacypolicy);

        // Add the listeners now to the textViews
        textViewTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "You want the tutorial? Too bad! =3", Toast.LENGTH_SHORT).show();
            }
        });

        textViewPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the privacy policy dialog
                dialogPrivacyPolicy.show();

                Toast.makeText(getActivity(), "Sorry, privacy not enabled =3", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
