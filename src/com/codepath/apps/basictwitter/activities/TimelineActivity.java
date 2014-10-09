package com.codepath.apps.basictwitter.activities;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import com.codepath.apps.basictwitter.R;
import com.codepath.apps.basictwitter.TwitterApplication;
import com.codepath.apps.basictwitter.activities.listeners.FragmentTabListener;
import com.codepath.apps.basictwitter.fragments.ComposeTweetFragment;
import com.codepath.apps.basictwitter.fragments.ComposeTweetFragment.ComposeTweetDialogListener;
import com.codepath.apps.basictwitter.fragments.HomeTimelineFragment;
import com.codepath.apps.basictwitter.fragments.MentionsTimelineFragment;
import com.codepath.apps.basictwitter.fragments.TweetsListFragment.ShowTweetDetailListener;
import com.codepath.apps.basictwitter.helpers.MemberIdentityHelper;
import com.codepath.apps.basictwitter.helpers.NetworkUtils;
import com.codepath.apps.basictwitter.helpers.TwitterClient;
import com.codepath.apps.basictwitter.models.Tweet;

public class TimelineActivity extends FragmentActivity 
implements ComposeTweetDialogListener, ShowTweetDetailListener {
	private TwitterClient client;
	private MemberIdentityHelper memberIdentityHelper;
	private SearchView svQuery;
	
	private final int DETAIL_REQUEST_CODE = 100;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS); 
		setContentView(R.layout.activity_timeline);
		client = TwitterApplication.getRestClient();
		memberIdentityHelper = new MemberIdentityHelper(client);
		memberIdentityHelper.verifyCredentials(getApplicationContext());
		setupTabs();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.timeline, menu);
		MenuItem searchItem = menu.findItem(R.id.action_search);
		svQuery = (SearchView) searchItem.getActionView();

		svQuery.setOnQueryTextListener(new OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				// perform query here
				if (query != null && !query.isEmpty()) {
					Intent i = new Intent(TimelineActivity.this, SearchActivity.class);
					i.putExtra("searchQuery", query);
					startActivity(i);
				}
				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});
        return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.miCompose:
	        	composeTweet();
	            return true;
	        case R.id.miProfile:
	        	viewProfile();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private void composeTweet() {
		if (!NetworkUtils.isNetworkAvailable((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))) {
			NetworkUtils.showInternetMissingError(getApplication());
			return;
		}
		FragmentManager fm = getSupportFragmentManager();
		ComposeTweetFragment composeTweetDialog = ComposeTweetFragment.newInstance(null);
		composeTweetDialog.show(fm, "fragment_compose_tweet");
	}

	@Override
	public void onFinishComposeDialog(String inputText) {
		HomeTimelineFragment frag = (HomeTimelineFragment) getSupportFragmentManager().findFragmentByTag("home");
		frag.composeTweet(inputText);
	}

	public void showDetailView(Tweet tweet, int position) {
		Intent i = new Intent(this, TweetDetailActivity.class);
		i.putExtra("tweet", tweet);
		i.putExtra("position", position);
		startActivityForResult(i, DETAIL_REQUEST_CODE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == DETAIL_REQUEST_CODE && data != null) {
			long tweetId = data.getExtras().getLong("tweetId");
			int position = data.getExtras().getInt("position");
  			HomeTimelineFragment frag = (HomeTimelineFragment) getSupportFragmentManager().findFragmentByTag("home");
  			frag.refreshTweetAtIndex(tweetId, position);
		} else if (requestCode == DETAIL_REQUEST_CODE) {
			SharedPreferences pref =   
				    PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			long tweetId = pref.getLong("tweetId", 0);
			int position = pref.getInt("position", 0);
  			HomeTimelineFragment frag = (HomeTimelineFragment) getSupportFragmentManager().findFragmentByTag("home");
  			frag.refreshTweetAtIndex(tweetId, position);			
		}
	}
	
	private void viewProfile() {
		if (!NetworkUtils.isNetworkAvailable((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))) {
			NetworkUtils.showInternetMissingError(getApplication());
			return;
		}
		
		SharedPreferences pref =   
			    PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		long userId = pref.getLong(MemberIdentityHelper.USER_ID, 0);
		showProfileView(userId);
	}
	
	private void setupTabs() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(true);

		Tab tab1 = actionBar
			.newTab()
			.setText("Home")
			//.setIcon(R.drawable.ic_home)
			.setTag("HomeTimelineFragment")
			.setTabListener(
				new FragmentTabListener<HomeTimelineFragment>(R.id.flTweetListContainer, this, "home", HomeTimelineFragment.class));

		actionBar.addTab(tab1);
		actionBar.selectTab(tab1);

		Tab tab2 = actionBar
			.newTab()
			.setText("Mentions")
			//.setIcon(R.drawable.ic_mentions)
			.setTag("MentionsTimelineFragment")
			.setTabListener(
			    new FragmentTabListener<MentionsTimelineFragment>(R.id.flTweetListContainer, this, "mentions", MentionsTimelineFragment.class));

		actionBar.addTab(tab2);
	}

	private void showProfileView(Long userId) {
		Intent i = new Intent(this, ProfileActivity.class);
		i.putExtra("userId", userId);
		startActivity(i);		
	}
	
	public void onClickProfileView(View view) {
		Long userId = (Long) view.getTag();
		showProfileView(userId);
	}
		
	/*
	public void onClickReplyIcon(View view) {
		TextView tweet = (TextView) ((View) view.getParent()).findViewById(R.id.tvBody);
		TextView user = (TextView) ((View) view.getParent()).findViewById(R.id.tvUserScreenName);

		String text = tweet.getText().toString();
		String userName = user.getText().toString();
		String[] handles = EditTweetUtils.extractHandles(text, userName);
		FragmentManager fm = getSupportFragmentManager();
		ComposeTweetFragment composeTweetDialog = ComposeTweetFragment.newInstance(handles);
		composeTweetDialog.show(fm, "fragment_compose_tweet");	
		}
		*/
}
