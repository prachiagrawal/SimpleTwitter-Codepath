package com.codepath.apps.basictwitter.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

@Table(name = "Users")
public class User extends Model implements Serializable {
	private static final long serialVersionUID = -3337864828154135416L;
	// This is how you avoid duplicates based on a unique ID
    @Column(name = "user_id", unique = true, onUniqueConflict = Column.ConflictAction.FAIL)
    public long userId;
    @Column(name = "name")
    public String name;
    @Column(name = "screen_name")
    public String screenName;
    @Column(name = "profile_image_url")
    public String profileImageUrl;
    @Column(name = "profile_use_background_image")
    public Boolean useBackgroundImage;
    @Column(name = "profile_background_image_url")
    public String profileBackgroundImageUrl;
    @Column(name = "profile_background_color")
    public String profileBackgroundColor;
    @Column(name = "followers_count")
    public int followersCount;
    @Column(name = "statuses_count")
    public int statusesCount;
    @Column(name = "friends_count")
    public int friendsCount;
    @Column(name = "is_verified")
    public Boolean isVerified;
    @Column(name = "is_following")
    public Boolean isFollowing;
    @Column(name = "description")
    public String description;
    

    public static long getSerialversionuid() {
		return serialVersionUID;
	}
       
	public long getUserId() {
		return userId;
	}

	public String getName() {
		return name;
	}

	public String getScreenName() {
		return screenName;
	}

	public String getProfileImageUrl() {
		return profileImageUrl;
	}

	public Boolean getUseBackgroundImage() {
		return useBackgroundImage;
	}

	public int getFollowersCount() {
		return followersCount;
	}

	public int getStatusesCount() {
		return statusesCount;
	}

	public int getFriendsCount() {
		return friendsCount;
	}

	public String getProfileBackgroundColor() {
		return profileBackgroundColor;
	}

	public Boolean getIsVerified() {
		return isVerified;
	}

	public String getProfileBackgroundImageUrl() {
		return profileBackgroundImageUrl;
	}

	public Boolean getIsFollowing() {
		return isFollowing;
	}
	
	public String getDescription() {
		return description;
	}
	

	// Make sure to have a default constructor for every ActiveAndroid model
    public User(){
       super();
    }

	public static User fromJson(JSONObject jsonObject) {
		User user = new User();		
		try {
			user.userId = jsonObject.getLong("id");
			
			User savedUser = getById(user.userId);
			if (savedUser != null) {
				return savedUser;
			}		
			
			user.name = jsonObject.getString("name");			
			user.screenName = jsonObject.getString("screen_name");
			user.profileImageUrl = jsonObject.getString("profile_image_url");
			user.profileBackgroundColor = jsonObject.getString("profile_background_color");
			user.useBackgroundImage = jsonObject.getBoolean("profile_use_background_image");
			user.profileBackgroundImageUrl = jsonObject.getString("profile_background_image_url");
			user.followersCount = jsonObject.getInt("followers_count");
			user.statusesCount = jsonObject.getInt("statuses_count");
			user.friendsCount = jsonObject.getInt("friends_count");
			user.isVerified = jsonObject.getBoolean("verified");
			user.isFollowing = jsonObject.getBoolean("following");
			user.description = jsonObject.getString("description");
			
			user.save();
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		
		return user;
	}
	

	public static ArrayList<User> fromJson(JSONArray jsonArray) {
		ArrayList<User> users = new ArrayList<User>();
		
		if (jsonArray == null) {
			return users;
		}
		
		for (int i = 0; i < jsonArray.length(); i++) {			
			try {
				users.add(fromJson(jsonArray.getJSONObject(i)));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return users;
	}

	public static User getById(long id) {
		return new Select().from(User.class).where("user_id = ?", id).executeSingle();
	}
	
	// The SavedUser class can indicate itÕs relationship to many SavedTweets. 
	// We do this with a helper method.
    public List<Tweet> items() {
        return getMany(Tweet.class, "user");
    }
}
