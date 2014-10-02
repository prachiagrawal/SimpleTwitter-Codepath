package com.codepath.apps.basictwitter.activities;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.basictwitter.R;
import com.codepath.apps.basictwitter.TwitterApplication;
import com.codepath.apps.basictwitter.fragments.ComposeTweetFragment;
import com.codepath.apps.basictwitter.fragments.ComposeTweetFragment.ComposeTweetDialogListener;
import com.codepath.apps.basictwitter.helpers.NetworkUtils;
import com.codepath.apps.basictwitter.helpers.ParseRelativeDate;
import com.codepath.apps.basictwitter.helpers.TwitterClient;
import com.codepath.apps.basictwitter.models.SavedTweet;
import com.codepath.apps.basictwitter.models.SavedUser;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

public class TweetDetailActivity extends FragmentActivity implements ComposeTweetDialogListener {
	private TwitterClient client;
	ImageView ivProfileImage;
	TextView tvUserProfileName;
	TextView tvUserScreenName;
	TextView tvBody;
	TextView tvCreatedAt;
	ImageView ivMediaImage;
	ImageView ivReplyIcon;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tweet_detail);
		
		client = TwitterApplication.getRestClient();
		ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
		tvUserProfileName = (TextView) findViewById(R.id.tvUserProfileName);
		tvUserScreenName = (TextView) findViewById(R.id.tvUserScreenName);
		tvBody = (TextView) findViewById(R.id.tvBody);
		tvCreatedAt = (TextView) findViewById(R.id.tvCreatedAt);
		ivMediaImage = (ImageView) findViewById(R.id.ivMediaImage);
		ivReplyIcon = (ImageView) findViewById(R.id.ivReplyIcon);
		
		Intent i = getIntent();
		final SavedTweet tweet = (SavedTweet) i.getSerializableExtra("tweet");
		final SavedUser user = (SavedUser) i.getSerializableExtra("user");
		
		ivProfileImage.setImageResource(0);
		ImageLoader imageLoader = ImageLoader.getInstance();
		
		imageLoader.displayImage(tweet.getUser().getProfileImageUrl(), ivProfileImage);
		tvUserProfileName.setText(tweet.getUser().getName());
		tvUserScreenName.setText("@" + tweet.getUser().getScreenName());
		tvBody.setText(tweet.getBody());
		tvBody.setMovementMethod(LinkMovementMethod.getInstance());
		
		String relativeTimespan = ParseRelativeDate.getRelativeTimeAgo(tweet.getCreatedAt());
		String[] timespanParts = relativeTimespan.split(" ");
		String formattedTimespan = timespanParts[0] + timespanParts[1].charAt(0);
		tvCreatedAt.setText(formattedTimespan);
				
		if (tweet.getMediaImageUrl() != null && !tweet.getMediaImageUrl().isEmpty()) {
			ivMediaImage.setVisibility(View.VISIBLE);
			imageLoader.displayImage(tweet.getMediaImageUrl(), ivMediaImage);
		} else {
			ivMediaImage.setVisibility(View.GONE);
		}
		
		
		ivReplyIcon.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				String originalTweet = tvBody.getText().toString();
				String[] handlesBegin = originalTweet.split("@");
				int i = 0;
				for (i = 1; i < handlesBegin.length; i++) {
					// TODO: Use regex to get only alphanumeric characters
					handlesBegin[i-1] = handlesBegin[i].split("\\s*[^a-zA-Z_]+\\s*")[0]; 
				}
				handlesBegin[i-1] = tweet.getUser().getScreenName();
				composeTweet(user, handlesBegin);
			}
		});
	}
	
	private void composeTweet(SavedUser user, String[] handles) {
		FragmentManager fm = getSupportFragmentManager();
		ComposeTweetFragment composeTweetDialog = ComposeTweetFragment.newInstance(
				user.getProfileImageUrl(), user.getName(), user.getScreenName(), handles);
		composeTweetDialog.show(fm, "fragment_compose_tweet");
	}

	@Override
	public void onFinishComposeDialog(String inputText) {
	  	client.postStatus(new JsonHttpResponseHandler() {
	  		@Override
	  		public void onSuccess(JSONObject json) {
	  			if (!NetworkUtils.isNetworkAvailable((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))) {
	  				NetworkUtils.showInternetMissingError(getApplication());
	  				return;
	  			}
	  			finish();
	  		}
	  		
			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("DEBUG", e.toString());
				Log.d("DEBUG", s);
			}
	  	}, inputText);
	}
}
