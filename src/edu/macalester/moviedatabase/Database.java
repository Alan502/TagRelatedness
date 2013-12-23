package edu.macalester.moviedatabase;

import java.util.LinkedList;

public interface Database {
	
	public void initializeMovieLensTags(String dir);
	public void initializeBibSonomyTags(String dir);
	public LinkedList<String> getTagsSet();

}
