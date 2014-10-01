package com.codepath.apps.basictwitter.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Column.ForeignKeyAction;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

@Table(name = "Tweets")
public class SavedTweet extends Model implements Serializable {
	private static final long serialVersionUID = -3589856967691936974L;
	// This is how you avoid duplicates
    @Column(name = "tweet_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public long tweetId;
    @Column(name = "body")
    public String body;
    @Column(name = "user", onUpdate = ForeignKeyAction.CASCADE, onDelete = ForeignKeyAction.CASCADE)
    public SavedUser user;
    @Column(name = "created_at")
    public String createdAt;
    @Column(name = "media_image")
    public String mediaImageUrl;
    
    // Make sure to have a default constructor for every ActiveAndroid model
    public SavedTweet(){
       super();
    }
    
    public SavedTweet(long remoteId, String body, SavedUser user, String createdAt, String mediaImageUrl){
        super();
        this.tweetId = remoteId;
        this.body = body;
        this.user = user;
        this.createdAt = createdAt;
        this.mediaImageUrl = mediaImageUrl;
    }
    	
	public long getTweetId() {
		return tweetId;
	}

	public String getBody() {
		return body;
	}

	public SavedUser getUser() {
		return user;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public String getMediaImageUrl() {
		return mediaImageUrl;
	}
		
	public static SavedTweet fromJson(JSONObject jsonObject) {
		SavedTweet tweet = new SavedTweet();
		
		try {
			tweet.body = jsonObject.getString("text");
			tweet.tweetId = jsonObject.getLong("id");
			tweet.createdAt = jsonObject.getString("created_at");
			tweet.user = SavedUser.fromJson(jsonObject.getJSONObject("user"));
			tweet.mediaImageUrl = null;
			
			JSONObject json = jsonObject.optJSONObject("entities");
			if (json != null) {
				JSONArray jsonArray = json.optJSONArray("media");
				if (jsonArray != null && !jsonArray.isNull(0)) {
					tweet.mediaImageUrl = jsonArray.getJSONObject(0).optString("media_url");
				}
			}
			
			tweet.save();
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		
		return tweet;
	}

	public static ArrayList<SavedTweet> fromJson(JSONArray jsonArray) {
		ArrayList<SavedTweet> tweets = new ArrayList<SavedTweet>();
		
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
	
	public static List<SavedTweet> getPaginatedList(Long maxId) {
		if (maxId != null && maxId > 0) {
	        return new Select()
	          .from(SavedTweet.class)
	          .orderBy("tweet_id DESC")
	          .where("tweet_id < ?", maxId)
	          .limit(20)
	          .execute();
		}
        return new Select()
          .from(SavedTweet.class)
          .orderBy("tweet_id DESC")
          .limit(20)
          .execute();
    }

	public static void saveAll(List<SavedTweet> tweets) {
		if (tweets == null) {
			return;
		}

		ActiveAndroid.beginTransaction();
		try {
			for (int i = 0; i < tweets.size(); i++) {
				tweets.get(i).save();
			}
			ActiveAndroid.setTransactionSuccessful();
		} finally {
			ActiveAndroid.endTransaction();
		}
	}
}