package com.example.hellofacebook;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockDialogFragment;

/**
 * Created by xdai on 11/15/13.
 */
public class FacebookPublisherFragment extends SherlockDialogFragment {
    public static String EXTRA_MESSAGE = "test message";
    public static String EXTRA_TITLE = "title";
    private EditText messageText ;


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

        messageText = (EditText)rootView.findViewById(R.id.publisher_message);
        messageText.setText(EXTRA_MESSAGE);
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
        i.putExtra(EXTRA_MESSAGE, messageText.getText().toString());
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);

    }


}
