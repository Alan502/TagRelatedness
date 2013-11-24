package edu.macalester.moviedatabase;

import java.util.HashMap;
import java.util.HashSet;

public class ProjectionalJaccard implements TagSimilarityMeasure{
	
	ProjectionalDatabase db;

	public ProjectionalJaccard(ProjectionalDatabase database){
		db = database;
	}
	
	public double calculateSimilarity(String tag1, String tag2) {
		HashSet<String> movieSet1 = (HashSet<String>) db.getTagsMap().get(tag1).clone();
		HashSet<String> movieSet2 = (HashSet<String>) db.getTagsMap().get(tag2).clone();
		
		double sum = movieSet1.size() + movieSet2.size();
		
		movieSet1.retainAll(movieSet2);
		double intersection =  movieSet1.size();
		
		double union = sum - intersection;
		
		return intersection/union;
	}


}
