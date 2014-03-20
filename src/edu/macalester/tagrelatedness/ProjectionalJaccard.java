package edu.macalester.tagrelatedness;

import java.util.HashSet;

public class ProjectionalJaccard implements TagSimilarityMeasure{
	
	ProjectionalDatabase db;

	public ProjectionalJaccard(ProjectionalDatabase database){
		db = database;
	}
	
	@SuppressWarnings("unchecked")
	public double calculateSimilarity(String tag1, String tag2) {
		HashSet<String> resourceSet1 = (HashSet<String>) db.getTagsMap().get(tag1).clone();
		HashSet<String> resourceSet2 = (HashSet<String>) db.getTagsMap().get(tag2).clone();
		
		double sum = resourceSet1.size() + resourceSet2.size();
		
		resourceSet1.retainAll(resourceSet2);
		double intersection =  resourceSet1.size();
		
		double union = sum - intersection;
		
		return intersection/union;
	}


}
