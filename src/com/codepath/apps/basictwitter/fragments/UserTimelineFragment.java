package com.codepath.apps.basictwitter.fragments;

import java.util.ArrayList;

import org.json.JSONArray;

import android.os.Bundle;
import android.util.Log;

import com.codepath.apps.basictwitter.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

public class UserTimelineFragment extends TweetsListFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	public UserTimelineFragment() {
		// Override the public constructor
	}
	
	public static UserTimelineFragment newInstance(Long userId) {
		UserTimelineFragment frag = new UserTimelineFragment();
		Bundle args = new Bundle();
		args.putLong("userId", userId);
		frag.setArguments(args);
		return frag;
	}
		
	public void populateTimeline(Long sinceId, Long maxId) {
		getTwitterClient().getUserTimeline(new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONArray jsonArray) {
				ArrayList<Tweet> tweets = Tweet.fromJson(jsonArray);
				addAll(tweets);
				setRefreshComplete();
			}
			
			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("DEBUG", e.toString());
				Log.d("DEBUG", s);
			}
		}, sinceId, maxId, getArguments().getLong("userId"));
	}
}
