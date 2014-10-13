package edu.umich.engin.cm.onecoolthing.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.umich.engin.cm.onecoolthing.R;

/**
 * Created by jawad on 13/10/14.
 *
 * Used as fill in for testing views that contain several different fragments
 */
public class FragmentBase extends Fragment {
    int mNewColor = 0; // The new bg color, set from activity
    String mNewText = ""; // The new text, set from activity
    TextView mMainText;  // The only textview in this fragment

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the fragment's layout
        View view = inflater.inflate(R.layout.fragment_base,container,false);
        // Save the textview for further editing
        mMainText = (TextView) view.findViewById(R.id.textView);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // If there is new text or color assigned, set em
        if(mNewText != ""){
            mMainText.setText(mNewText);
        }
        if(mNewColor != 0){
            mMainText.setBackgroundColor(mNewColor);
        }
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    // Simply indicate to change the text of the fragment
    public void changeText(String newText){
        mNewText=newText;
    }

    // Simply indicate to change the background color of the fragment
    public void changeBG(int color) {
        // If no color was passed, then set background to white
        if(color == 0)
        {
            mNewColor=getResources().getColor(R.color.white);
        }
        // else set the color to what was passed in
        else{
            mNewColor=color;
        }
    }
}
