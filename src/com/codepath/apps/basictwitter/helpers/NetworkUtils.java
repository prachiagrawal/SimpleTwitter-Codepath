package com.codepath.apps.basictwitter.helpers;

import android.app.Application;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetworkUtils {
	
	public static Boolean isNetworkAvailable(ConnectivityManager connectivityManager) {
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
	}
		
	public static void showInternetMissingError(Application application) {
		Toast.makeText(application, "No internet connection available", Toast.LENGTH_LONG).show();
	}

	
}
