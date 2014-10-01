package com.codepath.apps.basictwitter.fragments;

import java.util.List;

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
	private ImageView ivBackArrow;
	private EditText etComposeTweet;
	private ImageView ivFragmentUserProfileImage;
	private TextView tvFragmentUserProfileName;
	private TextView tvFragmentUserScreenName;
	private Button btnTweetGrey;
	private TextView tvCharCount;

	public ComposeTweetFragment() {
		// Empty constructor required for DialogFragment
	}
	
	public static ComposeTweetFragment newInstance(String profileUrl, 
			String userProfileName, String userScreenName, String[] rtHandles) {
		ComposeTweetFragment frag = new ComposeTweetFragment();
		Bundle args = new Bundle();
		args.putString("url", profileUrl);
		args.putString("profileName", userProfileName);
		args.putString("screenName", userScreenName);
		args.putStringArray("rtHandles", rtHandles);
		frag.setArguments(args);
		return frag;
	}
	
    public interface ComposeTweetDialogListener {
        void onFinishComposeDialog(String inputText);
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_compose_tweet, container);
		ivBackArrow = (ImageView) view.findViewById(R.id.ivBackArrow);
		etComposeTweet = (EditText) view.findViewById(R.id.etComposeTweet);
		ivFragmentUserProfileImage = (ImageView) view.findViewById(R.id.ivFragmentUserProfileImage);
		tvFragmentUserProfileName = (TextView) view.findViewById(R.id.tvFragmentUserProfileName);
		tvFragmentUserScreenName = (TextView) view.findViewById(R.id.tvFragmentUserScreenName);
		btnTweetGrey = (Button) view.findViewById(R.id.btnTweetGrey);
		btnTweetGrey.setEnabled(false);
		tvCharCount = (TextView) view.findViewById(R.id.tvCharCount);
		tvCharCount.setText("140");
		
		String url = getArguments().getString("url", "");
		String profileName = getArguments().getString("profileName", "");
		String screenName = getArguments().getString("screenName", "");
		String[] rtHandles = getArguments().getStringArray("rtHandles");

		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.displayImage(url, ivFragmentUserProfileImage);
		tvFragmentUserProfileName.setText(profileName);
		tvFragmentUserScreenName.setText("@" + screenName);
		
		btnTweetGrey.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				// Return input text to activity
				ComposeTweetDialogListener listener = (ComposeTweetDialogListener) getActivity();
				listener.onFinishComposeDialog(etComposeTweet.getText().toString());
				dismiss();				
			}
		});
		
		ivBackArrow.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		
		String rtString = "";
		if (rtHandles != null) {
			for (int i = 0; i < rtHandles.length; i++) {
				rtString +=  "@" + rtHandles[i] + " ";
			}
		}
		// Show soft keyboard automatically
		etComposeTweet.requestFocus();
		etComposeTweet.addTextChangedListener(this);
		etComposeTweet.setText(rtString);
		etComposeTweet.setSelection(rtString.length());
		
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		
		//getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		return view;
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		int textLength = etComposeTweet.getText().length();
		tvCharCount.setText(Integer.toString(140-textLength));

		if (textLength > 0 && textLength <= 140) {
			btnTweetGrey.setEnabled(true);
			tvCharCount.setTextColor(getResources().getColor(android.R.color.darker_gray));
		} else {
			btnTweetGrey.setEnabled(false);
			if (textLength > 140) {
				tvCharCount.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
			}
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
