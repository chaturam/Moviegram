// twitter related code was taekn from following link
// http://java.dzone.com/articles/how-build-android-twitter

package com.fit4039.chatura.moviegram;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;

import com.fit4039.chatura.moviegram.model.AuthToken;
import com.fit4039.chatura.moviegram.model.Util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by hp on 6/3/2015.
 */

// handles tweet download
public class TweetDownloader extends AsyncTask<String, Void, String> {
    final static String TwitterTokenURL = "https://api.twitter.com/oauth2/token";
    final static String TwitterSearchURL = "https://api.twitter.com/1.1/search/tweets.json?q=";

    ProgressDialog dialog;
    Context context;
    AsyncTaskListener taskListener;
    TaskType taskType;

    public TweetDownloader(Context context,  AsyncTaskListener taskListener, TaskType taskType){
        this.context = context;
        this.taskListener = taskListener;
        this.taskType = taskType;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = ProgressDialog.show(context, "Loading", "Downloading Tweets...", true);
    }

    @Override
    protected String doInBackground(String... params) {
        String results = null;
        AuthToken token = getAuthToken(); // getting authorisation token  (method call)
        if (token != null && token.getTokenType().equals("bearer")) {
            String searchTerm = params[0];
            try {
                // downloading tweets using received authentication token
                String encodedSearchTerm = URLEncoder.encode(searchTerm, "UTF-8");
                String searchURL = TwitterSearchURL + encodedSearchTerm + "&count=100";

                HttpGet httpGet = new HttpGet(searchURL);
                httpGet.setHeader("Authorization", "Bearer " + token.getAccessToken());
                httpGet.setHeader("Content-Type", "application/json");
                results = getResponseBody(httpGet);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return results;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        taskListener.onTaskCompleted(result, taskType);
        dialog.dismiss();
    }

    // getting authorisation token
    private AuthToken getAuthToken(){
        AuthToken token = null;
        try {
            String urlApiKey = URLEncoder.encode(Util.TwitterKey, "UTF-8");
            String urlApiSecret = URLEncoder.encode(Util.TwitterSecret, "UTF-8");
            String combined = urlApiKey + ":" + urlApiSecret;
            String base64Encoded = Base64.encodeToString(combined.getBytes(), Base64.NO_WRAP);

            HttpPost httpPost = new HttpPost(TwitterTokenURL);
            httpPost.setHeader("Authorization", "Basic " + base64Encoded);
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            httpPost.setEntity(new StringEntity("grant_type=client_credentials"));
            String rawAuthorization = getResponseBody(httpPost);

            // decoding authentication token
            JSONObject jObj = new JSONObject(rawAuthorization);
            token = new AuthToken();
            token.setTokenType(jObj.getString("token_type"));
            token.setAccessToken(jObj.getString("access_token"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  token;
    }

    private String getResponseBody(HttpRequestBase request) {
        StringBuilder sb = new StringBuilder();
        try {

            DefaultHttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());
            HttpResponse response = httpClient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            String reason = response.getStatusLine().getReasonPhrase();

            if (statusCode == 200) {

                HttpEntity entity = response.getEntity();
                InputStream inputStream = entity.getContent();

                BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                String line = null;
                while ((line = bReader.readLine()) != null) {
                    sb.append(line);
                }
            } else {
                sb.append(reason);
            }
        } catch (UnsupportedEncodingException ex) {
        } catch (ClientProtocolException ex1) {
        } catch (IOException ex2) {
        }
        return sb.toString();
    }
}
