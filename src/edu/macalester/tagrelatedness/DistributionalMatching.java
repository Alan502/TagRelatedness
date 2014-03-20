package edu.macalester.tagrelatedness;

import java.util.HashMap;
import java.util.HashSet;
/**
 * The DistributionalMatching similarity measure is able to extract signals of similarity based on the frequency in which users have associated
 * a given tag with a resource. For reference of the construction of this algorithm look at: www2009.org/proceedings/pdf/p641.pdf
 * @author Alan Morales
 * @see DistributionalDatabase
 */
public class DistributionalMatching implements TagSimilarityMeasure{
	
	ProjectionalDatabase db;

	public DistributionalMatching(ProjectionalDatabase database){
		db = database;
	}
	/**
	 * Calculates the similarity between two tags in the database.
	 * @param tag1 the first tag
	 * @param tag2 the second tag
	 */
	@SuppressWarnings("unchecked")
	public double calculateSimilarity(String tag1, String tag2) {
		
		HashMap<String, HashSet<String>> tagsMap = db.getTagsMap();
		
		HashSet<String> resources1 = (HashSet<String>) tagsMap.get(tag1).clone();
		HashSet<String> resources2 = (HashSet<String>) tagsMap.get(tag2).clone();
		
		resources1.retainAll(resources2);
		
		double similarity = 0.0;
		final double totalResources = db.getResourcesSet().size();
		
		HashMap<String, HashSet<String>> resourcesMap = db.getResourcesMap();
		
		for(String resource : resources1 ){
			double associated = resourcesMap.get(resource).size();
			similarity += Math.log(associated / totalResources );
		}
		
		return similarity*-1;
	}
	
	

}
