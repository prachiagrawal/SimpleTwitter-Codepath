package com.codepath.apps.basictwitter.fragments;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;

import com.codepath.apps.basictwitter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

public class FriendsListFragment extends UsersListFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().getActionBar().setTitle("Friends");
	}
	
	public FriendsListFragment() {
		// Override the public constructor
	}
	
	public static FriendsListFragment newInstance(Long userId) {
		FriendsListFragment frag = new FriendsListFragment();
		Bundle args = new Bundle();
		args.putLong("userId", userId);
		frag.setArguments(args);
		return frag;
	}
	
	@Override
	public void populateTimeline(Long cursor) {
		getTwitterClient().getFriendsList(new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject json) {
				long cursor;
				try {
					cursor = json.getLong("next_cursor");
					setCursor(cursor);
					ArrayList<User> users = User.fromJson(json.getJSONArray("users"));
					addAll(users);
					setRefreshComplete();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("DEBUG", e.toString());
				Log.d("DEBUG", s);
			}
		}, cursor, getArguments().getLong("userId"));		
	}

}
