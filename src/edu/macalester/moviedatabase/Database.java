package edu.macalester.moviedatabase;

import java.util.Set;

public interface Database {
	
	public void initializeMovieLensTags(String dir);
//	public void initializeBibSonomyTags(String dir);
	public Set<String> getTagsSet();

}
