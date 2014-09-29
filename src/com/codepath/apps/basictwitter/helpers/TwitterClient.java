package com.codepath.apps.basictwitter.helpers;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "3rIVcXgTtPYkl3O3hAcHO6Eh7";       // Change this
	public static final String REST_CONSUMER_SECRET = "ERbdfNa42RX42myQFdVagK1pqoQ7STLwEnbakYQ1434QeGYr1M"; // Change this
	public static final String REST_CALLBACK_URL = "oauth://cpbasictweets"; // Change this (here and in manifest)

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}
	
	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */
	
	public void getHomeTimeline(AsyncHttpResponseHandler handler, Long sinceId, Long maxId) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		RequestParams params = null;
		if ((sinceId != null && sinceId != -1) || (maxId != null && maxId != -1)) {
			params = new RequestParams();
			if ((maxId != null && maxId != -1)) {
				params.put("max_id", Long.toString(maxId - 1)); // -1 to exclude the tweet with this ID
			}
			if ((sinceId != null && sinceId != -1)) {
				params.put("since_id", Long.toString(sinceId)); // -1 to exclude the tweet with this ID
			}
		}
		client.get(apiUrl, params, handler);
	}
	
	public void getPersonalSettings(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("account/settings.json");
		client.get(apiUrl, null, handler);
	}
	
	public void getUser(AsyncHttpResponseHandler handler, String screenName) {
		String apiUrl = getApiUrl("users/show.json");
		RequestParams params = null;
		if (!screenName.isEmpty()) {
			params = new RequestParams();
			params.put("screen_name", screenName);
		}
		client.get(apiUrl, params, handler);
	}
	
	public void postStatus(AsyncHttpResponseHandler handler, String status) {
		String apiUrl = getApiUrl("statuses/update.json");
		RequestParams params = null;
		if (!status.isEmpty()) {
			params = new RequestParams();
			params.put("status", status);
		}
		client.post(apiUrl, params, handler);
	}

}