package edu.umich.engin.cm.onecoolthing.StandaloneFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import edu.umich.engin.cm.onecoolthing.R;
import edu.umich.engin.cm.onecoolthing.Util.IntentStarter;

/**
 * Created by jawad on 20/04/15.
 *
 * For sending us cool things [ie, button to open up email]
 */
public class SendCoolFragment extends android.support.v4.app.Fragment {
  // The specific subject and to strings for the template email
  private static final String emailTo = "MichiganEngineer@umich.edu";
  private static final String emailSubject = "Suggestion for One Cool Thing.";

  // LinearLayout that contains the image+text pair of the "button"
  LinearLayout mButton;

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_sendcool, container, false);

    // Save the button (of sorts) to attach a listener to it later
    mButton = (LinearLayout) view.findViewById(R.id.sendbutton);

    return view;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    // Set up the button now that the Activity is available for sure
    mButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        // Open up an email intent to send along the user's suggestion
        IntentStarter.sendEmail(getActivity(), emailTo, emailSubject);
      }
    });
  }
}
