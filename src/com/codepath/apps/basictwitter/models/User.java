package com.codepath.apps.basictwitter.models;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
	private String name;
	private Long uid;
	private String screenName;
	private String profileImageUrl;
	
	public static User fromJson(JSONObject jsonObject) {
		if (jsonObject == null) {
			return null;
		}

		User user = new User();
		try {
			user.name = jsonObject.getString("name");
			user.uid = jsonObject.getLong("id");
			user.screenName = jsonObject.getString("screen_name");
			user.profileImageUrl = jsonObject.getString("profile_image_url");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return user;
	}

	public String getName() {
		return name;
	}

	public Long getUid() {
		return uid;
	}

	public String getScreenName() {
		return screenName;
	}

	public String getProfileImageUrl() {
		return profileImageUrl;
	}

}
