package com.codepath.apps.basictwitter.helpers;

import org.json.JSONObject;

import android.util.Log;

import com.codepath.apps.basictwitter.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

public class EditTweetUtils {
	public static void editFavorite(TwitterClient client, final Tweet tweet) {
		if (tweet.getIsFavorited()) {
			client.destroyFavorite(new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(JSONObject json) {
					tweet.isFavorited = false;
					tweet.favoriteCount -= 1;
					tweet.save();
				}

				@Override
				public void onFailure(Throwable e, String s) {					
					Log.d("DEBUG", e.toString());
					Log.d("DEBUG", s);
				}
			}, tweet.getTweetId());		
		} else {
			client.createFavorite(new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(JSONObject json) {
					tweet.isFavorited = true;
					tweet.favoriteCount += 1;
					tweet.save();
				}

				@Override
				public void onFailure(Throwable e, String s) {					
					Log.d("DEBUG", e.toString());
					Log.d("DEBUG", s);
				}
			}, tweet.getTweetId());
		}
	}
	
	public static String[] extractHandles(String tweet, String userName) {
		String[] handlesBegin = tweet.split("@");
		int i = 0;
		for (i = 1; i < handlesBegin.length; i++) {
			// Find only alphanumeric characters
			handlesBegin[i-1] = handlesBegin[i].split("\\s*[^a-zA-Z_]+\\s*")[0]; 
		}
		handlesBegin[i-1] = userName;
		return handlesBegin;
	}
}
