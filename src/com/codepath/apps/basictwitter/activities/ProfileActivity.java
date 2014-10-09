package com.codepath.apps.basictwitter.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.codepath.apps.basictwitter.R;
import com.codepath.apps.basictwitter.fragments.TopCardFragment;
import com.codepath.apps.basictwitter.fragments.TweetsListFragment.ShowTweetDetailListener;
import com.codepath.apps.basictwitter.fragments.UserTimelineFragment;
import com.codepath.apps.basictwitter.models.Tweet;

public class ProfileActivity extends FragmentActivity 
	implements ShowTweetDetailListener {
	
	private final int DETAIL_REQUEST_CODE = 150;
	
	private long userId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS); 
		setContentView(R.layout.activity_profile);
		
		Long userId = getIntent().getLongExtra("userId", -1);
		if (userId != null && userId > -1) {
			this.userId = userId;
			// Create a transaction
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

			// Hide, show, add, remove fragments "topCard" is the tag which you can use
			// to get hold of a dynamic fragment later
			ft.replace(R.id.flTopCardContainer, createTopCard(userId), "topCard");
			ft.replace(R.id.flUserTweetListContainer, createUserTimeline(userId), "userTimeline");

			ft.commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profile, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
		
	private TopCardFragment createTopCard(long userId) {
		return TopCardFragment.newInstance(userId);
	}
	
	private UserTimelineFragment createUserTimeline(long userId) {
		return UserTimelineFragment.newInstance(userId);
	}

	public void showDetailView(Tweet tweet, int position) {
		Intent i = new Intent(this, TweetDetailActivity.class);
		i.putExtra("tweet", tweet);
		i.putExtra("position", position);
		startActivityForResult(i, DETAIL_REQUEST_CODE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == DETAIL_REQUEST_CODE) {
			long tweetId = data.getExtras().getLong("tweetId");
			int position = data.getExtras().getInt("position");
			UserTimelineFragment frag = (UserTimelineFragment) getSupportFragmentManager().findFragmentByTag("userTimeline");
  			frag.refreshTweetAtIndex(tweetId, position);
		} else if (requestCode == DETAIL_REQUEST_CODE) {
			SharedPreferences pref =   
				    PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			long tweetId = pref.getLong("tweetId", 0);
			int position = pref.getInt("position", 0);
			UserTimelineFragment frag = (UserTimelineFragment) getSupportFragmentManager().findFragmentByTag("userTimeline");
  			frag.refreshTweetAtIndex(tweetId, position);			
		}
	}
	
	public void onClickProfileView(View view) {
		// Do nothing if already on profile view
	}
	
	
	public void onClickFollowersView(View view) {
		Intent i = new Intent(this, UsersActivity.class);
		i.putExtra("userId", userId);
		i.putExtra("listType", "followers");
		startActivity(i);		
	}
	
	
	public void onClickFriendsView(View view) {
		Intent i = new Intent(this, UsersActivity.class);
		i.putExtra("userId", userId);
		i.putExtra("listType", "friends");
		startActivity(i);		
	}
}
