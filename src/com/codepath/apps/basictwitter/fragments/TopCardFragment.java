package com.codepath.apps.basictwitter.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.basictwitter.R;
import com.codepath.apps.basictwitter.models.User;
import com.nostra13.universalimageloader.core.ImageLoader;

public class TopCardFragment extends Fragment {
	private ImageView ivProfileBackgroundImage;
	private ImageView ivProfileTopCardImage;
	private TextView tvProfileScreenName;
	private TextView tvProfileName;
	private TextView tvTagline;
	private TextView tvFollowersCount;
	private TextView tvFriendsCount;
	private TextView tvTweetsCount;
	private ImageView ivVerified;


	public TopCardFragment() {
		// Empty constructor
	}
	
	public static TopCardFragment newInstance(long userId) {
		TopCardFragment frag = new TopCardFragment();
		Bundle args = new Bundle();
		args.putLong("userId", userId);
		frag.setArguments(args);
		return frag;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_top_card, container, false);
		ivProfileBackgroundImage = (ImageView) v.findViewById(R.id.ivProfileBackgroundImage);
		ivProfileTopCardImage = (ImageView) v.findViewById(R.id.ivProfileTopCardImage);
		tvProfileScreenName = (TextView) v.findViewById(R.id.tvProfileScreenName);
		tvProfileName = (TextView) v.findViewById(R.id.tvProfileName);
		tvTagline = (TextView) v.findViewById(R.id.tvTagline);
		tvFollowersCount = (TextView) v.findViewById(R.id.tvFollowersCount);
		tvFriendsCount = (TextView) v.findViewById(R.id.tvFriendsCount);
		tvTweetsCount = (TextView) v.findViewById(R.id.tvTweetsCount);
		ivVerified = (ImageView) v.findViewById(R.id.ivVerified);


		User user = User.getById(getArguments().getLong("userId"));		
		if (user != null) {
			ivProfileBackgroundImage.setImageResource(0);
			ivProfileTopCardImage.setImageResource(0);
		
			ImageLoader imageLoader = ImageLoader.getInstance();			
			if (user.getUseBackgroundImage()) {
				imageLoader.displayImage(user.getProfileBackgroundImageUrl(), ivProfileBackgroundImage);
			}
			imageLoader.displayImage(user.getProfileImageUrl(), ivProfileTopCardImage);
			
			tvProfileName.setText(user.getName());
			tvProfileScreenName.setText("@" + user.getScreenName());
			
			tvTagline.setText(user.getDescription());
			tvFollowersCount.setText(Integer.toString(user.getFollowersCount()));
			tvFriendsCount.setText(Integer.toString(user.getFriendsCount()));
			tvTweetsCount.setText(Integer.toString(user.getStatusesCount()));		
			
			if (!user.getIsVerified()) {
				ivVerified.setVisibility(View.INVISIBLE);
			}
		}
		
		return v;		
	}
}
