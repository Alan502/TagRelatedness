package edu.macalester.moviedatabase;

import java.util.Set;

public interface Database {
	void addTag(String movieName, String tagName);
	void intializeMovieTags(String tagsDataFileDir);
	Set<String> getTagsSet();
	Set<String> getMoviesSet();
}
