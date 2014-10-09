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
public class Tweet extends Model implements Serializable {
	private static final long serialVersionUID = -3589856967691936974L;
	// This is how you avoid duplicates
    @Column(name = "tweet_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public long tweetId;
    @Column(name = "body")
    public String body;
    @Column(name = "user", onUpdate = ForeignKeyAction.CASCADE, onDelete = ForeignKeyAction.CASCADE)
    public User user;
    @Column(name = "created_at")
    public String createdAt;
    @Column(name = "media_image")
    public String mediaImageUrl;
    @Column(name = "retweet_count")
    public int retweetCount;
    @Column(name = "favorite_count")
    public int favoriteCount;
    @Column(name = "is_retweeted")
    public Boolean isRetweeted;
    @Column(name = "is_favorited")
    public Boolean isFavorited;

	// Make sure to have a default constructor for every ActiveAndroid model
    public Tweet(){
       super();
    }
    
    public Tweet(long remoteId, String body, User user, String createdAt, String mediaImageUrl){
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

	public User getUser() {
		return user;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public String getMediaImageUrl() {
		return mediaImageUrl;
	} 
    
    public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public int getRetweetCount() {
		return retweetCount;
	}

	public int getFavoriteCount() {
		return favoriteCount;
	}	

	public Boolean getIsRetweeted() {
		return isRetweeted;
	}

	public Boolean getIsFavorited() {
		return isFavorited;
	}
		
	public static Tweet fromJson(JSONObject jsonObject) {
		Tweet tweet = new Tweet();
		
		try {
			tweet.body = jsonObject.getString("text");
			tweet.tweetId = jsonObject.getLong("id");
			tweet.createdAt = jsonObject.getString("created_at");
			tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
			tweet.mediaImageUrl = null;
			
			JSONObject json = jsonObject.optJSONObject("entities");
			if (json != null) {
				JSONArray jsonArray = json.optJSONArray("media");
				if (jsonArray != null && !jsonArray.isNull(0)) {
					tweet.mediaImageUrl = jsonArray.getJSONObject(0).optString("media_url");
				}
			}
			
			tweet.favoriteCount = jsonObject.optJSONObject("retweeted_status") != null ?
					jsonObject.optJSONObject("retweeted_status").getInt("favorite_count") : 0;
			tweet.retweetCount = jsonObject.getInt("retweet_count");
			tweet.isFavorited = jsonObject.getBoolean("favorited");
			tweet.isRetweeted = jsonObject.getBoolean("retweeted");;
			
			tweet.save();
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		
		return tweet;
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
	
	public static List<Tweet> getPaginatedList(Long maxId) {
		if (maxId != null && maxId > 0) {
	        return new Select()
	          .from(Tweet.class)
	          .orderBy("tweet_id DESC")
	          .where("tweet_id < ?", maxId)
	          .limit(20)
	          .execute();
		}
        return new Select()
          .from(Tweet.class)
          .orderBy("tweet_id DESC")
          .limit(20)
          .execute();
    }

	public static void saveAll(List<Tweet> tweets) {
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