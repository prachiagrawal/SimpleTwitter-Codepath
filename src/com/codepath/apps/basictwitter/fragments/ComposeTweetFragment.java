package com.codepath.apps.basictwitter.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.basictwitter.R;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ComposeTweetFragment extends DialogFragment implements TextWatcher {
	private EditText etComposeTweet;
	private ImageView ivFragmentUserProfileImage;
	private TextView tvFragmentUserProfileName;
	private TextView tvFragmentUserScreenName;
	private Button btnTweet;

	public ComposeTweetFragment() {
		// Empty constructor required for DialogFragment
	}
	
	public static ComposeTweetFragment newInstance(String profileUrl, String userProfileName, String userScreenName) {
		ComposeTweetFragment frag = new ComposeTweetFragment();
		Bundle args = new Bundle();
		args.putString("url", profileUrl);
		args.putString("profileName", userProfileName);
		args.putString("screenName", userScreenName);
		frag.setArguments(args);
		return frag;
	}
	
    public interface ComposeTweetDialogListener {
        void onFinishComposeDialog(String inputText);
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_compose_tweet, container);
		etComposeTweet = (EditText) view.findViewById(R.id.etComposeTweet);
		ivFragmentUserProfileImage = (ImageView) view.findViewById(R.id.ivFragmentUserProfileImage);
		tvFragmentUserProfileName = (TextView) view.findViewById(R.id.tvFragmentUserProfileName);
		tvFragmentUserScreenName = (TextView) view.findViewById(R.id.tvFragmentUserScreenName);
		btnTweet = (Button) view.findViewById(R.id.btnTweet);
		
		String url = getArguments().getString("url", "");
		String profileName = getArguments().getString("profileName", "");
		String screenName = getArguments().getString("screenName", "");

		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.displayImage(url, ivFragmentUserProfileImage);
		tvFragmentUserProfileName.setText(profileName);
		tvFragmentUserScreenName.setText("@" + screenName);
		
		btnTweet.setEnabled(false);
		btnTweet.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				// Return input text to activity
				ComposeTweetDialogListener listener = (ComposeTweetDialogListener) getActivity();
				listener.onFinishComposeDialog(etComposeTweet.getText().toString());
				dismiss();				
			}
		});
		
		// Show soft keyboard automatically
		etComposeTweet.requestFocus();
		etComposeTweet.addTextChangedListener(this);
		
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		
		//getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		return view;
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		int textLength = etComposeTweet.getText().length();
		if (textLength > 0 && textLength <= 140) {
			btnTweet.setEnabled(true);
		} else {
			btnTweet.setEnabled(false);
		}		
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		return; // Do nothing
	}

	@Override
	public void afterTextChanged(Editable s) {
		return; // Do nothing
	}
}
