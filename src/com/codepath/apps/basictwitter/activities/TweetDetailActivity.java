package com.codepath.apps.basictwitter.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codepath.apps.basictwitter.R;
import com.codepath.apps.basictwitter.TwitterApplication;
import com.codepath.apps.basictwitter.fragments.ComposeTweetFragment;
import com.codepath.apps.basictwitter.fragments.ComposeTweetFragment.ComposeTweetDialogListener;
import com.codepath.apps.basictwitter.fragments.HomeTimelineFragment;
import com.codepath.apps.basictwitter.helpers.EditTweetUtils;
import com.codepath.apps.basictwitter.helpers.NetworkUtils;
import com.codepath.apps.basictwitter.helpers.ParseRelativeDate;
import com.codepath.apps.basictwitter.helpers.TwitterClient;
import com.codepath.apps.basictwitter.models.Tweet;
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
	ImageView ivRetweetIcon;
	ImageView ivFavoriteIcon;
	ImageView ivShareIcon;
	TextView tvRetweetCount;
	TextView tvFavoritesCount;
	TextView tvRetweets;
	TextView tvFavorites;
	LinearLayout llTweetStatisticsContainer;
	View vSecondDivider;
	ImageView ivVerified;
	
	private long tweetId;
	private int position;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS); 
		setContentView(R.layout.activity_tweet_detail);
		
		client = TwitterApplication.getRestClient();
		ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
		tvUserProfileName = (TextView) findViewById(R.id.tvUserProfileName);
		tvUserScreenName = (TextView) findViewById(R.id.tvUserScreenName);
		tvBody = (TextView) findViewById(R.id.tvBody);
		tvCreatedAt = (TextView) findViewById(R.id.tvCreatedAt);
		ivMediaImage = (ImageView) findViewById(R.id.ivMediaImage);
		ivReplyIcon = (ImageView) findViewById(R.id.ivReplyIcon);
		ivRetweetIcon = (ImageView) findViewById(R.id.ivRetweetIcon);
		ivFavoriteIcon = (ImageView) findViewById(R.id.ivFavoriteIcon);
		ivShareIcon = (ImageView) findViewById(R.id.ivShareIcon);
		tvRetweetCount = (TextView) findViewById(R.id.tvRetweetCount);
		tvFavoritesCount = (TextView) findViewById(R.id.tvFavoritesCount);
		tvRetweets = (TextView) findViewById(R.id.tvRetweets);
		tvFavorites = (TextView) findViewById(R.id.tvFavorites);
		llTweetStatisticsContainer = (LinearLayout) findViewById(R.id.llTweetStatisticsContainter);
		vSecondDivider = (View) findViewById(R.id.vSecondDivider);
		ivVerified = (ImageView) findViewById(R.id.ivVerified);
	
		Intent i = getIntent();
		final Tweet tweet = (Tweet) i.getSerializableExtra("tweet");
		
		tweetId = tweet.getTweetId();
		position = i.getIntExtra("position", 0);
		
		ivProfileImage.setImageResource(0);
		ImageLoader imageLoader = ImageLoader.getInstance();
		
		imageLoader.displayImage(tweet.getUser().getProfileImageUrl(), ivProfileImage);
		ivProfileImage.setTag(tweet.getUser().getUserId());
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
				String[] handlesBegin = EditTweetUtils.extractHandles(originalTweet, tweet.getUser().getScreenName());
				composeTweet(handlesBegin);
			}
		});
		
		ivFavoriteIcon.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				editFavorite(tweet);
			}
		});
		
		
		if (tweet.getRetweetCount() == 0 && tweet.getFavoriteCount() == 0 ) {
			llTweetStatisticsContainer.setVisibility(View.GONE);
			vSecondDivider.setVisibility(View.GONE);
		} else {
			if (tweet.getRetweetCount() == 0) {
				tvRetweetCount.setVisibility(View.GONE);
				tvRetweets.setVisibility(View.GONE);
			} else {
				tvRetweetCount.setText(Integer.toString(tweet.getRetweetCount()));
			}

			if (tweet.getFavoriteCount() == 0) {
				tvFavoritesCount.setVisibility(View.GONE);
				tvFavorites.setVisibility(View.GONE);
			} else {
				tvFavoritesCount.setText(Integer.toString(tweet.getFavoriteCount()));
			}
		}
		
		if (!tweet.getIsRetweeted()) {
			ivRetweetIcon.setImageResource(R.drawable.rewteet_symbol);		
		} else {
			ivRetweetIcon.setImageResource(R.drawable.retweet_green);		
		}

		if (!tweet.getIsFavorited()) {
			ivFavoriteIcon.setImageResource(R.drawable.favorite_star);
		} else {
			ivFavoriteIcon.setImageResource(R.drawable.star_orange);
		}
	
		/*
		if (tweet.getUser().getIsFollowing()) {
			ivAddContactIcon.setVisibility(View.INVISIBLE);
		}
		*/
		
		if (!tweet.getUser().getIsVerified()) {
			ivVerified.setVisibility(View.INVISIBLE);
		}
	}
	
	protected void editFavorite(final Tweet tweet) {
		if (!NetworkUtils
				.isNetworkAvailable((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))) {
			NetworkUtils.showInternetMissingError(getApplication());
			return;
		}
		
		EditTweetUtils.editFavorite(client, tweet);
		if (tweet.getIsFavorited()) {
			// If it was a favorite till now, unfavorite it
			ivFavoriteIcon.setImageResource(R.drawable.favorite_star);
			tvFavoritesCount.setText(Integer.toString(tweet.getFavoriteCount() - 1));			
		} else {
			ivFavoriteIcon.setImageResource(R.drawable.star_orange);
			tvFavoritesCount.setText(Integer.toString(tweet.getFavoriteCount() + 1));
		}
	}

	private void composeTweet(String[] handles) {
		if (!NetworkUtils.isNetworkAvailable((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))) {
			NetworkUtils.showInternetMissingError(getApplication());
			return;
		}
		FragmentManager fm = getSupportFragmentManager();
		ComposeTweetFragment composeTweetDialog = ComposeTweetFragment.newInstance(handles);
		composeTweetDialog.show(fm, "fragment_compose_tweet");
	}

	@Override
	public void onFinishComposeDialog(String inputText) {
		HomeTimelineFragment frag = (HomeTimelineFragment) getSupportFragmentManager().findFragmentByTag("home");
		frag.composeTweet(inputText);
	}
	
	public void onClickProfileView(View view) {
		Long userId = (Long) view.getTag();
		showProfileView(userId);
	}
		
	private void showProfileView(Long userId) {
		Intent i = new Intent(this, ProfileActivity.class);
		i.putExtra("userId", userId);
		startActivity(i);		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		// For some reason, the below 4 lines do not work
		Intent i = new Intent();
		i.putExtra("tweetId", tweetId);
		i.putExtra("position", position);
		setResult(RESULT_OK);
		
		// Hack to make things work somewhat
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		Editor edit = pref.edit();
		edit.putLong("tweetId", tweetId);
		edit.putInt("position", position);
		edit.commit(); 
	}
}
