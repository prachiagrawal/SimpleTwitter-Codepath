package com.codepath.apps.basictwitter.adapters;

import java.util.List;

import android.content.Context;
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
			convertView.setTag(viewholder);
		}
		else {
			viewholder = (Viewholder) convertView.getTag();
		}
		
		viewholder.ivProfileImage.setImageResource(0);
		ImageLoader imageLoader = ImageLoader.getInstance();
		
		imageLoader.displayImage(tweet.getUser().getProfileImageUrl(), viewholder.ivProfileImage);
		viewholder.tvUserProfileName.setText(tweet.getUser().getName());
		viewholder.tvUserScreenName.setText("@" + tweet.getUser().getScreenName());
		viewholder.tvBody.setText(tweet.getBody());
		
		String relativeTimespan = ParseRelativeDate.getRelativeTimeAgo(tweet.getCreatedAt());
		String[] timespanParts = relativeTimespan.split(" ");
		String formattedTimespan = timespanParts[0] + timespanParts[1].charAt(0);
		viewholder.tvCreatedAt.setText(formattedTimespan);
				
		return convertView;
	}
}
