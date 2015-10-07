package com.fit4039.chatura.moviegram;
// list of task types
public enum TaskType {
	DOWNLOAD_MOVIES(1),
    MOVIE_IMAGES(2),
    DOWNLOAD_MOVIE(3),
    DOWNLOAD_SIMILAR_MOVIES(4),
    SIMILAR_MOVIE_IMAGES(5),
    GET_YOUTUBE_ID(6),
	DOWNLOAD_TWEETS(7),
    DOWNLOAD_SENTIMENT_RESULTS(8),
    DOWNLOAD_REVIEWS(9),
    DOWNLOAD_CAST(10);

	int value;

	private TaskType(int value) {
		this.value = value;
	}
}
