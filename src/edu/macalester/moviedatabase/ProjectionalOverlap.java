package edu.macalester.moviedatabase;

import java.util.HashSet;

public class ProjectionalOverlap implements TagSimilarityMeasure{
	
	ProjectionalDatabase db;

	public ProjectionalOverlap(ProjectionalDatabase database){
		db = database;
	}
	
	@SuppressWarnings("unchecked")
	public double calculateSimilarity(String tag1, String tag2) {
		HashSet<String> movieSet1 = (HashSet<String>) db.getTagsMap().get(tag1).clone();
		HashSet<String> movieSet2 = (HashSet<String>) db.getTagsMap().get(tag2).clone();
		
		double minimum = movieSet1.size() - movieSet2.size() > 0 ? movieSet2.size() : movieSet1.size();
		
		movieSet1.retainAll(movieSet2);
		double intersection =  movieSet1.size();
		
		return intersection/minimum;
	}


}
