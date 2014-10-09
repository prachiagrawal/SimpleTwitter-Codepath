package com.codepath.apps.basictwitter.fragments;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;

import com.codepath.apps.basictwitter.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

public class SearchTweetsFragment extends TweetsListFragment {	
	private String searchQuery;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().getActionBar().setTitle("Search");
	}
	
	public SearchTweetsFragment(String searchQuery) {
		this.searchQuery = searchQuery;
	}
	
	public static SearchTweetsFragment newInstance(String searchQuery) {
		SearchTweetsFragment frag = new SearchTweetsFragment(searchQuery);
		Bundle args = new Bundle();
		args.putString("searchQuery", searchQuery);
		frag.setArguments(args);
		return frag;
	}
	
	@Override
	public void populateTimeline(Long sinceId, Long maxId) {
		populateTimeline(sinceId, maxId, this.searchQuery);
	}

	public void populateTimeline(Long sinceId, Long maxId, String searchQuery) {
		if (!searchQuery.equals(this.searchQuery)) {
			clearAll();
			this.searchQuery = searchQuery;
		}

		getTwitterClient().getSearchResults(new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject json) {
				ArrayList<Tweet> tweets;
				try {
					tweets = Tweet.fromJson(json.getJSONArray("statuses"));
					addAll(tweets);
					setRefreshComplete();
					getActivity().setProgressBarIndeterminateVisibility(false); 
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("DEBUG", e.toString());
				Log.d("DEBUG", s);
			}
		}, sinceId, maxId, searchQuery);		
	}

}
