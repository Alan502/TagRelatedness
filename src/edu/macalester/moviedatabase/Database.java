package edu.macalester.moviedatabase;

import java.util.HashSet;

public interface Database {
	
	public void initializeMovieLensTags(String dir);
//	public void initializeBibSonomyTags(String dir);
	public HashSet<String> getTagsSet();

}
