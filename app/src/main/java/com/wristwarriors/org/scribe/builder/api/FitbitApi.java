package com.wristwarriors.org.scribe.builder.api;

import android.util.Log;

import org.scribe.builder.api.DefaultApi20;
import org.scribe.extractors.AccessTokenExtractor;
import org.scribe.extractors.JsonTokenExtractor;
import org.scribe.model.OAuthConfig;
import org.scribe.model.Verb;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by dpinciotti on 9/14/15.
 */
public class FitbitApi extends DefaultApi20 {
    public static final String CALLBACK_URL = "https://www.fitbit.com/oauth2/success";

    private static final String AUTHORIZATION_URL = "https://www.fitbit.com/oauth2/authorize?response_type=code&client_id=%s&redirect_uri=%s&scope=%s";
    private static final String REQUEST_TOKEN_URL = "https://api.fitbit.com/oauth2/token";

    @Override
    public String getAuthorizationUrl(OAuthConfig config) {
        return String.format(AUTHORIZATION_URL, config.getApiKey(), config.getCallback(), config.getScope());
    }

    @Override
    public String getAccessTokenEndpoint() {
        return REQUEST_TOKEN_URL;
    }

    @Override
    public Verb getAccessTokenVerb() {
        return Verb.POST;
    }

    @Override
    public AccessTokenExtractor getAccessTokenExtractor() {
        return new JsonTokenExtractor();
    }
}
