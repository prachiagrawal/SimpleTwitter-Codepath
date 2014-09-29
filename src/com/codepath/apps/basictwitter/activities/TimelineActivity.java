package com.codepath.apps.basictwitter.activities;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.codepath.apps.basictwitter.R;
import com.codepath.apps.basictwitter.TwitterApplication;
import com.codepath.apps.basictwitter.adapters.TweetArrayAdapter;
import com.codepath.apps.basictwitter.fragments.ComposeTweetFragment;
import com.codepath.apps.basictwitter.fragments.ComposeTweetFragment.ComposeTweetDialogListener;
import com.codepath.apps.basictwitter.helpers.EndlessScrollListener;
import com.codepath.apps.basictwitter.helpers.TwitterClient;
import com.codepath.apps.basictwitter.models.Tweet;
import com.codepath.apps.basictwitter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

public class TimelineActivity extends FragmentActivity implements ComposeTweetDialogListener{
	private TwitterClient client;
	private ArrayList<Tweet> _tweets;
	private ArrayAdapter<Tweet> aTweets;
	private PullToRefreshListView lvTweets;
	private User user;
		
	private long _latestTweetId = -1;
	private long _oldestTweetId = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
		client = TwitterApplication.getRestClient();
		lvTweets = (PullToRefreshListView) findViewById(R.id.lvTweets);
		_tweets = new ArrayList<Tweet>();
		aTweets = new TweetArrayAdapter(this, _tweets);
		lvTweets.setAdapter(aTweets);
		
		fetchPersonalInformation();

		lvTweets.setOnScrollListener(new EndlessScrollListener() {
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
                customLoadMoreDataFromApi(page, totalItemsCount);
			}
		});
		
		// Set a listener to be invoked when the list should be refreshed.
        lvTweets.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call listView.onRefreshComplete() when
                // once the network request has completed successfully.
        		if (_tweets != null && _tweets.size() != 0) {
        			_latestTweetId = _tweets.get(0).getUid();
        		}
            	populateTimeline(_latestTweetId, null);
            }
        });
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.timeline, menu);
        return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.miCompose:
	        	composeTweet(user);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private void customLoadMoreDataFromApi(int page, int totalItemsCount) {
		if (_tweets != null && _tweets.size() != 0) {
			_oldestTweetId = _tweets.get(_tweets.size() - 1).getUid();
		}
		populateTimeline(null, _oldestTweetId);
	}
	
	private void populateTimeline(Long sinceId, Long maxId) {
		//if (page == 0) {
		//	_oldestTweetId = -1;
		//	aTweets.clear();
		//}
		
		client.getHomeTimeline(new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONArray jsonArray) {
				ArrayList<Tweet> tweets = Tweet.fromJson(jsonArray);				
				if (tweets != null && !tweets.isEmpty()) {
					if (_tweets == null || _tweets.size() == 0) {
						aTweets.addAll(tweets);
					} else if (_tweets.get(_tweets.size() - 1).getUid() > tweets.get(0).getUid()) {
						// If the oldest (bottom most) tweet in the listview happened after the
						// first tweet in the new list, append the new list at the end
						aTweets.addAll(tweets);
					} else if (_tweets.get(0).getUid() < tweets.get(tweets.size() - 1).getUid()) {
						// If the newest (topmost) tweet already in the listview happened before the
						// last tweet in the new list, insert the new list at the beginning
						_tweets.addAll(0, tweets);
						aTweets.notifyDataSetChanged();
					}
					// else looks like a duplicate fetch. Discard.
				}
				
				lvTweets.onRefreshComplete();
			}
			
			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("DEBUG", e.toString());
				Log.d("DEBUG", s);
			}
		}, sinceId, maxId);
	}
	
	private void fetchPersonalInformation() {
		client.getPersonalSettings(new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject json) {
				try {
					String screenName = json.getString("screen_name");
					fetchPersonalInformation(screenName);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("DEBUG", e.toString());
				Log.d("DEBUG", s);
			}
		});
	}
	
	private void fetchPersonalInformation(String screenName) {
		client.getUser(new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject json) {
				user = User.fromJson(json);
			}
			
			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("DEBUG", e.toString());
				Log.d("DEBUG", s);
			}
		}, screenName);
	}
	
	private void composeTweet(User user) {
		FragmentManager fm = getSupportFragmentManager();
		ComposeTweetFragment composeTweetDialog = ComposeTweetFragment.newInstance(
				user.getProfileImageUrl(), user.getName(), user.getScreenName());
		composeTweetDialog.show(fm, "fragment_compose_tweet");
	}

	@Override
	public void onFinishComposeDialog(String inputText) {
	  	Toast.makeText(this, "Hi, " + inputText, Toast.LENGTH_SHORT).show();
	  	client.postStatus(new JsonHttpResponseHandler() {
	  		@Override
	  		public void onSuccess(JSONObject json) {
	  			Tweet tweet = Tweet.fromJson(json);
	  			if (_tweets != null && _tweets.size() != 0) {
        			_latestTweetId = _tweets.get(0).getUid();
        		}
	  			populateTimeline(_latestTweetId, tweet.getUid()+1);
	  		}
	  		
			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("DEBUG", e.toString());
				Log.d("DEBUG", s);
			}
	  	}, inputText);
	}
}
