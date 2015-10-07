package com.fit4039.chatura.moviegram;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
// handles uploading of tweets to sentiment140
public class PostAsyncTask extends AsyncTask<String, Void, String> {
	String outputStr;
	ProgressDialog dialog;
	Context context;
	AsyncTaskListener taskListener;
	TaskType taskType;
	
	public PostAsyncTask(Context context, AsyncTaskListener taskListener, TaskType taskType){
		this.context = context;
		this.taskListener = taskListener;
		this.taskType = taskType;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		dialog = ProgressDialog.show(context, "Loading", "Analysing Tweets...", true);
	}

	@Override
	protected String doInBackground(String... params) {
		String urlString = params[0];
		String jsonString = params[1];
		String respStr = "";
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost post = new HttpPost(urlString);
		post.setHeader("content-type", "application/json; charset=UTF-8");

		try {

			StringEntity entity = new StringEntity(jsonString);
			post.setEntity(entity);

			HttpResponse resp = httpClient.execute(post);
		    respStr = EntityUtils.toString(resp.getEntity());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return respStr;

	}
	
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		taskListener.onTaskCompleted(result, taskType);
		dialog.dismiss();
	}

}
