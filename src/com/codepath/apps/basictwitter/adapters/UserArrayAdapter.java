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
import com.codepath.apps.basictwitter.models.User;
import com.nostra13.universalimageloader.core.ImageLoader;

public class UserArrayAdapter extends ArrayAdapter<User> {
	public class Viewholder {
		ImageView ivUserImage;
		TextView tvUserName;
		TextView tvUserScnName;
		TextView tvUserTagline;
		ImageView ivUserVerified;
		ImageView ivUserConnected;

	}

	public UserArrayAdapter(Context context, List<User> users) {
		super(context, 0, users);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		User user = getItem(position);

		Viewholder viewholder;
		if (convertView == null) {
			viewholder = new Viewholder();
			LayoutInflater inflater = LayoutInflater.from(getContext());
			convertView = inflater.inflate(R.layout.item_user, parent, false);
			viewholder.ivUserImage = (ImageView) convertView.findViewById(R.id.ivUserImage);
			viewholder.tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
			viewholder.tvUserScnName = (TextView) convertView.findViewById(R.id.tvUserScnName);
			viewholder.tvUserTagline = (TextView) convertView.findViewById(R.id.tvUserTagline);
			viewholder.ivUserVerified = (ImageView) convertView.findViewById(R.id.ivUserVerified);
			viewholder.ivUserConnected = (ImageView) convertView.findViewById(R.id.ivUserConnected);

			convertView.setTag(viewholder);
		}
		else {
			viewholder = (Viewholder) convertView.getTag();
		}
		
		ImageLoader imageLoader = ImageLoader.getInstance();
		
		viewholder.ivUserImage.setImageResource(0);		
		imageLoader.displayImage(user.getProfileImageUrl(), viewholder.ivUserImage);
		viewholder.ivUserImage.setTag(user.getUserId());
		viewholder.tvUserName.setText(user.getName());
		viewholder.tvUserScnName.setText("@" + user.getScreenName());
		viewholder.tvUserTagline.setText(user.getDescription());
		
		if (user.getIsVerified()) {
			viewholder.ivUserVerified.setVisibility(View.VISIBLE);
		} else {
			viewholder.ivUserVerified.setVisibility(View.INVISIBLE);
		}
		
		if (user.getIsFollowing()) {
			viewholder.ivUserConnected.setVisibility(View.INVISIBLE);
		} else {
			viewholder.ivUserConnected.setVisibility(View.VISIBLE);
		}
		
		return convertView;
	}

}
