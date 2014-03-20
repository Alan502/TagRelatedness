package edu.macalester.tagrelatedness;

import java.util.HashSet;

public class ProjectionalOverlap implements TagSimilarityMeasure{
	
	ProjectionalDatabase db;

	public ProjectionalOverlap(ProjectionalDatabase database){
		db = database;
	}
	
	@SuppressWarnings("unchecked")
	public double calculateSimilarity(String tag1, String tag2) {
		HashSet<String> resourceSet1 = (HashSet<String>) db.getTagsMap().get(tag1).clone();
		HashSet<String> resourceSet2 = (HashSet<String>) db.getTagsMap().get(tag2).clone();
		
		double minimum = resourceSet1.size() - resourceSet2.size() > 0 ? resourceSet2.size() : resourceSet1.size();
		
		resourceSet1.retainAll(resourceSet2);
		double intersection =  resourceSet1.size();
		
		return intersection/minimum;
	}


}
