package com.codepath.apps.basictwitter.adapters;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.basictwitter.R;
import com.codepath.apps.basictwitter.helpers.ParseRelativeDate;
import com.codepath.apps.basictwitter.models.Tweet;
import com.nostra13.universalimageloader.core.ImageLoader;

public class TweetArrayAdapter extends ArrayAdapter<Tweet> {
	public class Viewholder {
		ImageView ivProfileImage;
		TextView tvUserProfileName;
		TextView tvUserScreenName;
		TextView tvBody;
		TextView tvCreatedAt;
		ImageView ivMediaImage;
		ImageView ivReplyIcon;
		ImageView ivRetweetIcon;
		ImageView ivFavoriteIcon;
		ImageView ivAddContactIcon;
		TextView tvRetweetCount;
		TextView tvFavoriteCount;	
	}
	
	public TweetArrayAdapter(Context context, List<Tweet> tweets) {
		super(context, 0, tweets);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Tweet tweet = getItem(position);
		
		Viewholder viewholder;
		if (convertView == null) {
			viewholder = new Viewholder();
			LayoutInflater inflater = LayoutInflater.from(getContext());
			convertView = inflater.inflate(R.layout.item_tweet, parent, false);
			viewholder.ivProfileImage = (ImageView) convertView.findViewById(R.id.ivProfileImage);
			viewholder.tvUserProfileName = (TextView) convertView.findViewById(R.id.tvUserProfileName);
			viewholder.tvUserScreenName = (TextView) convertView.findViewById(R.id.tvUserScreenName);
			viewholder.tvBody = (TextView) convertView.findViewById(R.id.tvBody);
			viewholder.tvCreatedAt = (TextView) convertView.findViewById(R.id.tvCreatedAt);
			viewholder.ivMediaImage = (ImageView) convertView.findViewById(R.id.ivMediaImage);
			viewholder.ivReplyIcon = (ImageView) convertView.findViewById(R.id.ivReplyIcon);
			viewholder.ivRetweetIcon = (ImageView) convertView.findViewById(R.id.ivRetweetIcon);
			viewholder.ivFavoriteIcon = (ImageView) convertView.findViewById(R.id.ivFavoriteIcon);
			viewholder.ivAddContactIcon = (ImageView) convertView.findViewById(R.id.ivAddContactIcon);
			viewholder.tvRetweetCount = (TextView) convertView.findViewById(R.id.tvRetweetCount);
			viewholder.tvFavoriteCount = (TextView) convertView.findViewById(R.id.tvFavoriteCount);

			convertView.setTag(viewholder);
		}
		else {
			viewholder = (Viewholder) convertView.getTag();
		}
		
		ImageLoader imageLoader = ImageLoader.getInstance();
		
		viewholder.ivProfileImage.setImageResource(0);		
		imageLoader.displayImage(tweet.getUser().getProfileImageUrl(), viewholder.ivProfileImage);
		viewholder.ivProfileImage.setTag(tweet.getUser().getUserId());
		viewholder.tvUserProfileName.setText(tweet.getUser().getName());
		viewholder.tvUserScreenName.setText("@" + tweet.getUser().getScreenName());
		viewholder.tvBody.setText(tweet.getBody());
		
		if (tweet.getMediaImageUrl() != null && !tweet.getMediaImageUrl().isEmpty()) {
			viewholder.ivMediaImage.setVisibility(View.VISIBLE);
			imageLoader.displayImage(tweet.getMediaImageUrl(), viewholder.ivMediaImage);
		} else {
			viewholder.ivMediaImage.setVisibility(View.GONE);
		}
		
		viewholder.tvRetweetCount.setVisibility(View.VISIBLE);
		viewholder.tvRetweetCount.setText(Integer.toString(tweet.getRetweetCount()));

		viewholder.tvFavoriteCount.setVisibility(View.VISIBLE);
		viewholder.tvFavoriteCount.setText(Integer.toString(tweet.getFavoriteCount()));

		
		if (!tweet.getIsRetweeted()) {
			if (tweet.getRetweetCount() == 0) {
				viewholder.tvRetweetCount.setVisibility(View.INVISIBLE);
			} else {
				viewholder.tvRetweetCount.setTextColor(Color.parseColor("#A4A4A4"));
			}
			viewholder.ivRetweetIcon.setImageResource(R.drawable.retweet_gray);		
		} else {
			viewholder.tvRetweetCount.setTextColor(Color.parseColor("#4AA44A"));
			viewholder.ivRetweetIcon.setImageResource(R.drawable.retweet_green);		
		}

		if (!tweet.getIsFavorited()) {
			if (tweet.getFavoriteCount() == 0) {
				viewholder.tvFavoriteCount.setVisibility(View.INVISIBLE);
			} else {
				viewholder.tvFavoriteCount.setTextColor(Color.parseColor("#A4A4A4"));
			}
			viewholder.ivFavoriteIcon.setImageResource(R.drawable.star_gray);		
		} else {
			viewholder.tvFavoriteCount.setTextColor(Color.parseColor("#FAA628"));
			viewholder.ivFavoriteIcon.setImageResource(R.drawable.star_orange);		
		}	
		
		if (tweet.getUser().getIsFollowing()) {
			viewholder.ivAddContactIcon.setVisibility(View.INVISIBLE);
		}
		
		String relativeTimespan = ParseRelativeDate.getRelativeTimeAgo(tweet.getCreatedAt());
		String[] timespanParts = relativeTimespan.split(" ");
		String formattedTimespan = timespanParts[0] + timespanParts[1].charAt(0);
		viewholder.tvCreatedAt.setText(formattedTimespan);
				
		return convertView;
	}
}
