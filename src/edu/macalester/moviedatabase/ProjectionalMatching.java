package edu.macalester.moviedatabase;

import java.util.HashSet;

public class ProjectionalMatching implements TagSimilarityMeasure{
	
	ProjectionalDatabase db;
	
	public ProjectionalMatching(ProjectionalDatabase database){
		db = database;
	}
	
	@SuppressWarnings("unchecked")
	public double calculateSimilarity(String tag1, String tag2) {
		HashSet<String> movieSet1 = (HashSet<String>) db.getTagsMap().get(tag1).clone();
		HashSet<String> movieSet2 = (HashSet<String>) db.getTagsMap().get(tag2).clone();
		
		movieSet1.retainAll(movieSet2);
		return movieSet1.size();
	}
}
