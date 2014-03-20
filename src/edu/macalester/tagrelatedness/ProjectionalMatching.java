package edu.macalester.tagrelatedness;

import java.util.HashSet;

public class ProjectionalMatching implements TagSimilarityMeasure{
	
	ProjectionalDatabase db;
	
	public ProjectionalMatching(ProjectionalDatabase database){
		db = database;
	}
	
	@SuppressWarnings("unchecked")
	public double calculateSimilarity(String tag1, String tag2) {
		HashSet<String> resourceSet1 = (HashSet<String>) db.getTagsMap().get(tag1).clone();
		HashSet<String> resourceSet2 = (HashSet<String>) db.getTagsMap().get(tag2).clone();
		
		resourceSet1.retainAll(resourceSet2);
		return resourceSet1.size();
	}
}
