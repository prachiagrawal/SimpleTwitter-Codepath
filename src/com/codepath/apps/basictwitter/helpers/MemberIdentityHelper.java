package com.codepath.apps.basictwitter.helpers;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.codepath.apps.basictwitter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

public class MemberIdentityHelper {
	private TwitterClient client;
	
	public static final String USER_ID = "user_id";
	public static final String USER_NAME = "user_name";
	public static final String USER_SCREEN_NAME = "user_screen_name";
	public static final String USER_PROFILE_IMAGE_URL = "user_profile_img_url";


	public MemberIdentityHelper(TwitterClient client) {
		this.client = client;
	}
	
	public void verifyCredentials(final Context context) {
		client.verifyCredentials(new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject json) {
				User user = User.fromJson(json);
				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
				Editor edit = pref.edit();
				edit.putLong(USER_ID, user.userId);
				edit.putString(USER_NAME, user.name);
				edit.putString(USER_SCREEN_NAME, user.screenName);
				edit.putString(USER_PROFILE_IMAGE_URL, user.profileImageUrl);
				edit.commit(); 			
			}
			
			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("DEBUG", e.toString());
				Log.d("DEBUG", s);
			}
		});
	}
	
	/*
	private void fetchUserInformation(String screenName) {
		client.getUser(new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject json) {
				User user = User.fromJson(json);
			}
			
			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("DEBUG", e.toString());
				Log.d("DEBUG", s);
			}
		}, screenName);
	}
	*/

}
