package com.example.twitter;

import java.util.List;

import com.example.twitter.TwitterWebViewClient.OnAccessTokenListener;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

public class MainActivity extends Activity implements OnAccessTokenListener {

	WebView webView = null;

	Twitter twitter = null;
	RequestToken requestToken = null;

	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		webView = (WebView) findViewById(R.id.webView);
		webView.getSettings().setJavaScriptEnabled(true);

		(new Thread(new Runnable() {

			@Override
			public void run() {
				// The factory instance is re-useable and thread safe.Twitter
				twitter = TwitterFactory.getSingleton();
				twitter.setOAuthConsumer(Constants.KEY, Constants.SECRET);
				try {
					requestToken = twitter.getOAuthRequestToken();
					final String authtorizationURL = requestToken.getAuthorizationURL();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							TwitterWebViewClient client = new TwitterWebViewClient(MainActivity.this, requestToken);
							webView.setWebViewClient(client);
							webView.addJavascriptInterface(client, "GETHTML");
							MainActivity.this.webView.loadUrl(authtorizationURL);
						}
					});
				} catch (TwitterException e) {
					e.printStackTrace();
				}

			}
		})).start();
	}

	@Override
	public void onAccessToken(AccessToken accessToken) {
		try {
			twitter.setOAuthAccessToken(accessToken);
			List<User> users = twitter.getFollowersList(accessToken.getUserId(), -1);
			Log.i("", "users count = " + users.size());
			for (User user : users) {
				Log.i("", "" + user.getId());
				Log.i("", user.getName());
			}
		} catch (TwitterException e) {
			e.printStackTrace();
			Log.i("", "error");
		}

	}

}
