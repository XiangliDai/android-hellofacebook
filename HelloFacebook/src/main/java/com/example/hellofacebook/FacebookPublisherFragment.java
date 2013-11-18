package com.example.hellofacebook;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.EditText;

/**
 * Created by xdai on 11/15/13.
 */
public class FacebookPublisherFragment extends DialogFragment {
    public static String EXTRA_NAME = "name";
    public static String EXTRA_CAPTION = "caption";
    public static String EXTRA_DESC = "desc";
    public static String EXTRA_TITLE = "title";
    private EditText nameText ;
    private EditText captionText;
    private EditText descText;


    private static final String TAG = "FacebookPublisherFragment";
    public static FacebookPublisherFragment newInstance(String title) {
        FacebookPublisherFragment frag = new FacebookPublisherFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_TITLE, title);
        frag.setArguments(args);
        return frag;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        View rootView = getActivity().getLayoutInflater().inflate(R.layout.facebook_publisher_fragment, null) ;

        nameText = (EditText)rootView.findViewById(R.id.publisher_name);
        captionText = (EditText)rootView.findViewById(R.id.publisher_caption);
        descText = (EditText)rootView.findViewById(R.id.publisher_description);
        nameText.setText("Facebook SDK for Android");
        captionText.setText("Build great social apps and get more installs.");
        descText.setText("The Facebook SDK for Android makes it easier and faster to develop Facebook integrated Android apps.");

        return new AlertDialog.Builder(getActivity())
                .setView(rootView)
                .setTitle((CharSequence) getArguments().getSerializable(EXTRA_TITLE))
                .setPositiveButton(
                        android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                sendResult(Activity.RESULT_OK);
                            }
                        }
                )
                .setNegativeButton(
                        android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                sendResult(Activity.RESULT_CANCELED);
                            }
                        }
                )
                .create();
    }

    private void sendResult(int resultCode){
        if(getTargetFragment() == null) return;

        Intent i = new Intent();
        i.putExtra(EXTRA_NAME, nameText.getText().toString());
        i.putExtra(EXTRA_CAPTION, captionText.getText().toString());
        i.putExtra(EXTRA_DESC, descText.getText().toString());
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);

    }


}
