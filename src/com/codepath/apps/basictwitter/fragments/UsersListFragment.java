package com.codepath.apps.basictwitter.fragments;

import java.util.ArrayList;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.codepath.apps.basictwitter.R;
import com.codepath.apps.basictwitter.TwitterApplication;
import com.codepath.apps.basictwitter.adapters.UserArrayAdapter;
import com.codepath.apps.basictwitter.helpers.EndlessScrollListener;
import com.codepath.apps.basictwitter.helpers.NetworkUtils;
import com.codepath.apps.basictwitter.helpers.TwitterClient;
import com.codepath.apps.basictwitter.models.User;

import eu.erikw.PullToRefreshListView;

public abstract class UsersListFragment extends Fragment {
	private ArrayList<User> _users;
	private ArrayAdapter<User> aUsers;
	private PullToRefreshListView lvUsers;
	
	private TwitterClient client;
	
	private long _nextCursor = -1;
	
    public abstract void populateTimeline(Long cursor);
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_users = new ArrayList<User>();
		aUsers = new UserArrayAdapter(getActivity(), _users);
		client = TwitterApplication.getRestClient();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_users_list, container, false);
		lvUsers = (PullToRefreshListView) v.findViewById(R.id.lvUsers);
		lvUsers.setAdapter(aUsers);	
		
		lvUsers.setOnScrollListener(new EndlessScrollListener() {
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
                customLoadMoreDataFromApi(page, totalItemsCount);
			}
		});
		
		return v;
	}
	
	private void customLoadMoreDataFromApi(int page, int totalItemsCount) {
		if (!NetworkUtils.isNetworkAvailable((ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE))) {
			NetworkUtils.showInternetMissingError(getActivity().getApplication());
			return;
    	}
		populateTimeline(_nextCursor);
	}
	
	public void addAll(ArrayList<User> users) {
		aUsers.addAll(users);
	}
	
	public void setRefreshComplete() {
		lvUsers.onRefreshComplete();
	}

	public TwitterClient getTwitterClient() {
		return client;
	}
	
	public Long getCursor() {
		return _nextCursor;
	}
	
	public void setCursor(long cursor) {
		_nextCursor = cursor;
	}
}
