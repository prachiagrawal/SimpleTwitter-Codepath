package com.codepath.apps.basictwitter.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.codepath.apps.basictwitter.R;
import com.codepath.apps.basictwitter.fragments.FollowersListFragment;
import com.codepath.apps.basictwitter.fragments.FriendsListFragment;

public class UsersActivity extends FragmentActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_users);
		
		Long userId = getIntent().getLongExtra("userId", -1);
		String listType = getIntent().getStringExtra("listType");

		if (userId != null && userId > -1) {
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

			if ("followers".equals(listType)) {
				ft.replace(R.id.flUserListContainer, createFollowersList(userId), "followersList");
			} else if ("friends".equals(listType)) {
				ft.replace(R.id.flUserListContainer, createFriendsList(userId), "friendsList");
			}

			ft.commit();
		}
	}
	
	private FollowersListFragment createFollowersList(long userId) {
		return FollowersListFragment.newInstance(userId);
	}
	
	private FriendsListFragment createFriendsList(long userId) {
		return FriendsListFragment.newInstance(userId);
	}
}
