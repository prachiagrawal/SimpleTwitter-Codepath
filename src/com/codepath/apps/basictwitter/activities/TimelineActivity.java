package com.codepath.apps.basictwitter.activities;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

import com.codepath.apps.basictwitter.R;
import com.codepath.apps.basictwitter.TwitterApplication;
import com.codepath.apps.basictwitter.adapters.TweetArrayAdapter;
import com.codepath.apps.basictwitter.fragments.ComposeTweetFragment;
import com.codepath.apps.basictwitter.fragments.ComposeTweetFragment.ComposeTweetDialogListener;
import com.codepath.apps.basictwitter.helpers.EndlessScrollListener;
import com.codepath.apps.basictwitter.helpers.NetworkUtils;
import com.codepath.apps.basictwitter.helpers.TwitterClient;
import com.codepath.apps.basictwitter.models.SavedTweet;
import com.codepath.apps.basictwitter.models.SavedUser;
import com.loopj.android.http.JsonHttpResponseHandler;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

public class TimelineActivity extends FragmentActivity implements ComposeTweetDialogListener {
	private TwitterClient client;
	private ArrayList<SavedTweet> _tweets;
	private ArrayAdapter<SavedTweet> aTweets;
	private PullToRefreshListView lvTweets;
	private SavedUser user;
		
	private long _latestTweetId = -1;
	private long _oldestTweetId = -1;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
		client = TwitterApplication.getRestClient();
		lvTweets = (PullToRefreshListView) findViewById(R.id.lvTweets);
		_tweets = new ArrayList<SavedTweet>();
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
            	if (!NetworkUtils.isNetworkAvailable((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE))) {
        			NetworkUtils.showInternetMissingError(getApplication());
            		lvTweets.onRefreshComplete();
            		return;
            	}
                // Make sure you call listView.onRefreshComplete() when
                // once the network request has completed successfully.
        		if (_tweets != null && _tweets.size() != 0) {
        			_latestTweetId = _tweets.get(0).getTweetId();
        		}
            	populateTimeline(_latestTweetId, null);
            }
        });
        
        lvTweets.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				showDetailView(position);
			}
		});
	}
	
	protected void showDetailView(int position) {
		Intent i = new Intent(this, TweetDetailActivity.class);
		i.putExtra("tweet", _tweets.get(position));
		i.putExtra("user", user);
		startActivity(i);
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
			_oldestTweetId = _tweets.get(_tweets.size() - 1).getTweetId();
		}
		if (!NetworkUtils.isNetworkAvailable((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE))) {
			populateTimeline(_oldestTweetId);
			return;
    	}
		populateTimeline(null, _oldestTweetId);
	}
	
	private void populateTimeline(Long maxId) {
		ArrayList<SavedTweet> tweets = (ArrayList<SavedTweet>) SavedTweet.getPaginatedList(maxId);
		if (tweets != null && !tweets.isEmpty()) {
			_tweets.addAll(tweets);
			aTweets.notifyDataSetChanged();	
		} else {
			NetworkUtils.showInternetMissingError(getApplication());
		}
	}
	
	private void populateTimeline(Long sinceId, Long maxId) {
		client.getHomeTimeline(new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONArray jsonArray) {
				ArrayList<SavedTweet> tweets = SavedTweet.fromJson(jsonArray);	
				//SavedUser.saveAll(tweets); // Persist for using when there is no internet connection
				//SavedTweet.saveAll(tweets); // Persist for using when there is no internet connection

				if (tweets != null && !tweets.isEmpty()) {
					if (_tweets == null || _tweets.size() == 0 ||
					    (_tweets.get(_tweets.size() - 1).getTweetId() > tweets.get(0).getTweetId())) {
						// If this is the first load, or
						// If the oldest (bottom most) tweet in the listview happened after the
						// first tweet in the new list, append the new list at the end
						aTweets.addAll(tweets);
					} else if (_tweets.get(0).getTweetId() < tweets.get(tweets.size() - 1).getTweetId()) {
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
				user = SavedUser.fromJson(json);
			}
			
			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("DEBUG", e.toString());
				Log.d("DEBUG", s);
			}
		}, screenName);
	}
	
	private void composeTweet(SavedUser user) {
		FragmentManager fm = getSupportFragmentManager();
		ComposeTweetFragment composeTweetDialog = ComposeTweetFragment.newInstance(
				user.getProfileImageUrl(), user.getName(), user.getScreenName(), null);
		composeTweetDialog.show(fm, "fragment_compose_tweet");
	}

	@Override
	public void onFinishComposeDialog(String inputText) {
	  	client.postStatus(new JsonHttpResponseHandler() {
	  		@Override
	  		public void onSuccess(JSONObject json) {
	  			if (!NetworkUtils.isNetworkAvailable((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE))) {
	  				NetworkUtils.showInternetMissingError(getApplication());
	  				return;
	  			}
	  			SavedTweet tweet = SavedTweet.fromJson(json);
	  			if (_tweets != null && _tweets.size() != 0) {
        			_latestTweetId = _tweets.get(0).getTweetId();
        		}
	  			populateTimeline(_latestTweetId, tweet.getTweetId()+1);
	  		}
	  		
			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("DEBUG", e.toString());
				Log.d("DEBUG", s);
			}
	  	}, inputText);
	}
}
