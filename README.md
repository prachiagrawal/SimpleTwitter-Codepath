Simple Twitter Client
==============

A simple twitter client app for Android.

Time spent: In total, ~12 hours.

Completed user stories:

 * [x] User can sign in to Twitter using OAuth login
 * [x] User can view the tweets from their home timeline
 * [x] User should be displayed the username, name, and body for each tweet
 * [x] User should be displayed the relative timestamp for each tweet "8m", "7h"
 * [x] User can view more tweets as they scroll with infinite pagination
 * [x] Links in tweets are clickable and will launch the web browser (see autolink)
 * [x] User can click a “Compose” icon in the Action Bar on the top right
 * [x] User can then enter a new tweet and post this to twitter
 * [x] User is taken back to home timeline with new tweet visible in timeline
 * [x] User can refresh tweets timeline by pulling down to refresh
 * [x] User can open the twitter app offline and see last loaded tweets
 * [x] Tweets are persisted into sqlite and can be displayed from the local DB
 * [x] User can tap a tweet to display a "detailed" view of that tweet
 * [x] User can select "reply" from detail view to respond to a tweet
 * [x] Improve the user interface and theme the app to feel "twitter branded"
 * [x] User can see embedded image media within the tweet detail view
 * [x] Compose activity is replaced with a modal overlay

Walkthrough of all user stories:

![Video Walkthrough](SimpleTwitterClient.gif)

GIF created with [LiceCap](http://www.cockos.com/licecap/).


Used Twitters's APIs to get the data, UniversaleImageLoader (https://github.com/nostra13/Android-Universal-Image-Loader) to download and display the images and Android Async Http Client for all Async calls to the Instagram APIs (http://loopj.com/android-async-http/). Also used Scribe (https://github.com/fernandezpablo85/scribe-java) for oauth and Codepath's Rest Client template (https://github.com/thecodepath/android-rest-client-template).
