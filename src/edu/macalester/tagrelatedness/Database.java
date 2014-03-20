package edu.macalester.tagrelatedness;

import java.util.Set;
/**
 * This interfaces guarantees that each database has a method to initialize a movie lens dataset, a bibsonomy dataset and that each database is able to return
 * all the tags through a single method getTagsSet();
 * @author Alan Morales
 *
 */
public interface Database {
	
	public void initializeMovieLensTags(String dir);
	public void initializeBibsonomyTags(String dir);
	public Set<String> getTagsSet();

}
