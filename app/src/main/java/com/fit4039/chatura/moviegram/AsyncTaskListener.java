package com.fit4039.chatura.moviegram;

// this interface is used to support callback functions.
// Caller can identify the reply if it implements this interface
// AsyncTask will pass the result and taskType through this interface to its caller
public interface AsyncTaskListener {
	public void onTaskCompleted(String result, TaskType taskType);
}
