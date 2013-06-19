package com.example.twitter;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.annotation.SuppressLint;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

interface MyJavaScriptInterface {
	@JavascriptInterface
	public void processHTML(String html);
}

public class TwitterWebViewClient extends WebViewClient implements MyJavaScriptInterface {

	protected static String LOG_TAG = "TwitterWebViewClient";
	protected OnAccessTokenListener _listener = null;
	protected RequestToken _requestToken = null;

	protected final static String twitterAuthtorizeUrl = "https://api.twitter.com/oauth/authorize";

	public TwitterWebViewClient(OnAccessTokenListener listener, RequestToken requestToken) {
		setListener(listener);
		setRequestToken(requestToken);
	}

	@SuppressLint("JavascriptInterface")
	@Override
	public void onPageFinished(WebView view, final String url) {
		super.onLoadResource(view, url);
		Log.i("", url);
		if (url.equals(twitterAuthtorizeUrl)) {
			view.loadUrl("javascript:window.GETHTML.processHTML(document.getElementsByTagName('code')[0].innerHTML);");
		}

	}

	public interface OnAccessTokenListener {
		public void onAccessToken(AccessToken accessToken);
	}

	public OnAccessTokenListener getListener() {
		return _listener;
	}

	public void setListener(OnAccessTokenListener listener) {
		this._listener = listener;
	}

	public RequestToken getRequestToken() {
		return _requestToken;
	}

	public void setRequestToken(RequestToken requestToken) {
		this._requestToken = requestToken;
	}

	@JavascriptInterface
	public void processHTML(final String pinCode) {
		// TODO Auto-generated method stub

		try {
			Twitter twitter = TwitterFactory.getSingleton();
			AccessToken accessToken = twitter.getOAuthAccessToken(getRequestToken(), pinCode);
			Log.d(LOG_TAG, "success = " + accessToken.toString());
			Log.d(LOG_TAG, "token = " + accessToken.getToken());
			Log.d(LOG_TAG, "tokenSecret = " + accessToken.getTokenSecret());
			getListener().onAccessToken(accessToken);
		} catch (TwitterException e) {
			e.printStackTrace();
		}

	}
}
