package com.codepath.apps.basictwitter.fragments;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

import com.codepath.apps.basictwitter.R;
import com.codepath.apps.basictwitter.TwitterApplication;
import com.codepath.apps.basictwitter.adapters.TweetArrayAdapter;
import com.codepath.apps.basictwitter.helpers.EndlessScrollListener;
import com.codepath.apps.basictwitter.helpers.NetworkUtils;
import com.codepath.apps.basictwitter.helpers.TwitterClient;
import com.codepath.apps.basictwitter.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

public abstract class TweetsListFragment extends Fragment {
	private ArrayList<Tweet> _tweets;
	private ArrayAdapter<Tweet> aTweets;
	private PullToRefreshListView lvTweets;
	
	private TwitterClient client;
		
	private long _latestTweetId = -1;
	private long _oldestTweetId = -1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_tweets = new ArrayList<Tweet>();
		aTweets = new TweetArrayAdapter(getActivity(), _tweets);
		client = TwitterApplication.getRestClient();
	}
    
    public interface ShowTweetDetailListener {
        void showDetailView(Tweet tweet, int position);
    }
    
    public abstract void populateTimeline(Long sinceId, Long maxId);
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_tweets_list, container, false);
		lvTweets = (PullToRefreshListView) v.findViewById(R.id.lvTweets);
		lvTweets.setAdapter(aTweets);	
		
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
            	if (!NetworkUtils.isNetworkAvailable(
            			(ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE))) {
        			NetworkUtils.showInternetMissingError(getActivity().getApplication());
            		lvTweets.onRefreshComplete();
            		return;
            	}
            	populateTimeline(_latestTweetId, null);
            }
        });
        
        lvTweets.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ShowTweetDetailListener listener = (ShowTweetDetailListener) getActivity();
				listener.showDetailView(_tweets.get(position), position);
			}
		});
                
		return v;
	}
	
	private void customLoadMoreDataFromApi(int page, int totalItemsCount) {
		if (_tweets != null && _tweets.size() != 0) {
			_latestTweetId = _tweets.get(0).getTweetId();
			_oldestTweetId = _tweets.get(_tweets.size() - 1).getTweetId();
		}
		if (!NetworkUtils.isNetworkAvailable(
				(ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE))) {
			populateTimeline(_oldestTweetId);
			return;
    	}
		populateTimeline(null, _oldestTweetId);
	}
	
	private void populateTimeline(Long maxId) {
		ArrayList<Tweet> tweets = (ArrayList<Tweet>) Tweet.getPaginatedList(maxId);
		if (tweets != null && !tweets.isEmpty()) {
			addAll(tweets);
		} else {
			NetworkUtils.showInternetMissingError(getActivity().getApplication());
		}
	}
	
	public void clearAll() {
		_tweets.clear();
		aTweets.clear();
	}
	
	public void addAll(ArrayList<Tweet> tweets) {
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
	}
		
	public void setRefreshComplete() {
		lvTweets.onRefreshComplete();
	}
	
	public void refreshTimeline(long maxId) {
		populateTimeline(_latestTweetId, maxId);
	}

	public TwitterClient getTwitterClient() {
		return client;
	}
		
	public void refreshTweetAtIndex(long tweetId, final int position) {
	  	client.getStatus(new JsonHttpResponseHandler() {
	  		@Override
	  		public void onSuccess(JSONObject json) {
	  			Tweet tweet = Tweet.fromJson(json);
				_tweets.set(position, tweet);
				aTweets.notifyDataSetChanged();
	  		}
	  		
			@Override
			public void onFailure(Throwable e, String s) {
	  			if (!NetworkUtils.isNetworkAvailable((ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE))) {
	  				NetworkUtils.showInternetMissingError(getActivity().getApplication());
	  				return;
	  			}
				Log.d("DEBUG", e.toString());
				Log.d("DEBUG", s);
			}
	  	}, tweetId);	
	}
	
	public void composeTweet(String inputText) {
	  	client.postStatus(new JsonHttpResponseHandler() {
	  		@Override
	  		public void onSuccess(JSONObject json) {
	  			Tweet tweet = Tweet.fromJson(json);
	  			refreshTimeline(tweet.getTweetId()+1);
	  		}
	  		
			@Override
			public void onFailure(Throwable e, String s) {
	  			if (!NetworkUtils.isNetworkAvailable((ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE))) {
	  				NetworkUtils.showInternetMissingError(getActivity().getApplication());
	  				return;
	  			}
				Log.d("DEBUG", e.toString());
				Log.d("DEBUG", s);
			}
	  	}, inputText);
	}
}
