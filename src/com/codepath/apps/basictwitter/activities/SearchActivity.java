package com.codepath.apps.basictwitter.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import com.codepath.apps.basictwitter.R;
import com.codepath.apps.basictwitter.fragments.SearchTweetsFragment;
import com.codepath.apps.basictwitter.fragments.TweetsListFragment.ShowTweetDetailListener;
import com.codepath.apps.basictwitter.models.Tweet;

public class SearchActivity extends FragmentActivity implements ShowTweetDetailListener {
	private SearchView svQuery;
	
	private final int DETAIL_REQUEST_CODE = 10;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS); 
		setContentView(R.layout.activity_search);
		
		String searchString = getIntent().getStringExtra("searchQuery");
		if (searchString != null && !searchString.isEmpty()) {
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.replace(R.id.flSearchTweetListContainer, createSearchFragment(searchString), "search");
			ft.commit();
		}
	}
	
	SearchTweetsFragment createSearchFragment(String searchQuery) {
		return SearchTweetsFragment.newInstance(searchQuery);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.search, menu);
		MenuItem searchItem = menu.findItem(R.id.action_search);
		svQuery = (SearchView) searchItem.getActionView();

		svQuery.setOnQueryTextListener(new OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
		        setProgressBarIndeterminateVisibility(true); 

				// perform query here
				if (query != null && !query.isEmpty()) {
					SearchTweetsFragment frag = (SearchTweetsFragment) getSupportFragmentManager().findFragmentByTag("search");
					frag.populateTimeline(null, null, query);
				}
				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});
        return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void showDetailView(Tweet tweet, int position) {
		Intent i = new Intent(this, TweetDetailActivity.class);
		i.putExtra("tweet", tweet);
		i.putExtra("position", position);
		startActivityForResult(i, DETAIL_REQUEST_CODE);		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_CANCELED && requestCode == DETAIL_REQUEST_CODE && data != null) {
			// It never gets here because data is always NULL here!?
			long tweetId = data.getExtras().getLong("tweetId");
			int position = data.getExtras().getInt("position");
  			SearchTweetsFragment frag = (SearchTweetsFragment) getSupportFragmentManager().findFragmentByTag("search");
  			frag.refreshTweetAtIndex(tweetId, position);
		} else if (requestCode == DETAIL_REQUEST_CODE) {
			SharedPreferences pref =   
				    PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			long tweetId = pref.getLong("tweetId", 0);
			int position = pref.getInt("position", 0);
  			SearchTweetsFragment frag = (SearchTweetsFragment) getSupportFragmentManager().findFragmentByTag("search");
  			frag.refreshTweetAtIndex(tweetId, position);			
		}
	}
}
