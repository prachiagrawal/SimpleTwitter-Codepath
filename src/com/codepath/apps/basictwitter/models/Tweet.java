package com.codepath.apps.basictwitter.models;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Tweet {
	private String body;
	private long uid;
	private String createdAt;
	private User user;

	public String getBody() {
		return body;
	}

	public long getUid() {
		return uid;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public User getUser() {
		return user;
	}

	public static ArrayList<Tweet> fromJson(JSONArray jsonArray) {
		ArrayList<Tweet> tweets = new ArrayList<Tweet>();
		
		if (jsonArray == null) {
			return tweets;
		}
		
		for (int i = 0; i < jsonArray.length(); i++) {			
			try {
				tweets.add(fromJson(jsonArray.getJSONObject(i)));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return tweets;
	}
		
	public static Tweet fromJson(JSONObject jsonObject) {
		if (jsonObject == null) {
			return null;
		}

		Tweet tweet = new Tweet();
		try {
			tweet.body = jsonObject.getString("text");
			tweet.uid = jsonObject.getLong("id");
			tweet.createdAt = jsonObject.getString("created_at");
			tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		
		return tweet;
	}

	@Override
	public String toString() {
		return getBody() + " - " + getUser().getScreenName();
	}
}
