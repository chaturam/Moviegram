package com.fit4039.chatura.moviegram;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
// this class is used to download data from internet.
// Since it is an AsyncTask, it accepts the URL as input and outputs a String. Updates are not published (void)
public class GetAsyncTask extends AsyncTask<String, Void, String> {
	String outputStr;
	ProgressDialog dialog;// to indicate the user about progress.
	Context context;
	AsyncTaskListener taskListener; // to inform the caller once the task is completed.
	TaskType taskType; // each job has a task type.
	
	public GetAsyncTask(Context context, AsyncTaskListener taskListener, TaskType taskType){
		this.context = context;
		this.taskListener = taskListener;
		this.taskType = taskType;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		dialog = ProgressDialog.show(context, "Loading", "Please wait...", true);
	}
	@Override
	protected String doInBackground(String... params) {
		String urlString = params[0];

        try {
			// downloading data
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            if (conn.getResponseCode() != 200) {
                throw new IOException(conn.getResponseMessage());
            }

            // Buffer the result into a string
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            conn.disconnect();
            String jsonStr = sb.toString();
            outputStr = jsonStr;
        } catch (SocketTimeoutException e) {
            outputStr = "timeout";
        }catch(Exception e){
        	e.printStackTrace();
        	outputStr = "error";
        }
        
		return outputStr;
	}
	
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		// returning the result to caller with the task type
		taskListener.onTaskCompleted(result, taskType);
		dialog.dismiss();
	}

}
