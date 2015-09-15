package com.wristwarriors.activity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.wristwarriors.R;
import com.wristwarriors.org.scribe.builder.api.FitbitApi;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

public class OauthActivity extends AppCompatActivity {

    private WebView mWebView;
    private OAuthService mOauthService;
    private Token mRequestToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mOauthService = new ServiceBuilder()
                .provider(FitbitApi.class)
                .apiKey(getResources().getString(R.string.fitbit_client_id))
                .apiSecret(getResources().getString(R.string.fitbit_client_id))
                .callback(FitbitApi.CALLBACK_URL)
                .scope("activity%20heartrate%20location%20nutrition%20profile%20settings%20sleep%20social%20weight")
                .build();

        setContentView(R.layout.activity_oauth);

        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.clearCache(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.setWebViewClient(mWebViewClient);

        startAuthorize();
    }

    private void startAuthorize() {
        (new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String url = mOauthService.getAuthorizationUrl(null);
                return url;
            }

            @Override
            protected void onPostExecute(String url) {
                mWebView.loadUrl(url);
            }
        }).execute();
    }

    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if ((url != null) && (url.startsWith(FitbitApi.CALLBACK_URL))) { // Override webview when user came back to CALLBACK_URL
                mWebView.stopLoading();
                mWebView.setVisibility(View.INVISIBLE); // Hide webview if necessary
                Uri uri = Uri.parse(url);
                final Verifier verifier = new Verifier(uri.getQueryParameter("code"));
                (new AsyncTask<Void, Void, Token>() {
                    @Override
                    protected Token doInBackground(Void... params) {
                        return mOauthService.getAccessToken(mRequestToken, verifier);
                    }

                    @Override
                    protected void onPostExecute(Token accessToken) {
                        // AccessToken is passed here! Do what you want!
                        finish();
                    }
                }).execute();
            } else {
                super.onPageStarted(view, url, favicon);
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_oauth, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
