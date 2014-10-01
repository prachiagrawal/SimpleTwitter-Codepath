package com.codepath.apps.basictwitter.models;

import java.io.Serializable;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

@Table(name = "Users")
public class SavedUser extends Model implements Serializable {
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
    

    // Make sure to have a default constructor for every ActiveAndroid model
    public SavedUser(){
       super();
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

	public static SavedUser fromJson(JSONObject jsonObject) {
		SavedUser user = new SavedUser();		
		try {
			user.userId = jsonObject.getLong("id");
			List<SavedUser> savedUser = new Select().from(SavedUser.class)
					.where("user_id = ?", user.userId).execute();
			if (savedUser != null && !savedUser.isEmpty()) {
				return savedUser.get(0);
			}		
			user.name = jsonObject.getString("name");			
			user.screenName = jsonObject.getString("screen_name");
			user.profileImageUrl = jsonObject.getString("profile_image_url");
			
			user.save();
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		
		return user;
	}

	// The SavedUser class can indicate it’s relationship to many SavedTweets. 
	// We do this with a helper method.
    public List<SavedTweet> items() {
        return getMany(SavedTweet.class, "user");
    }
}
