package edu.macalester.tagrelatedness;

import java.util.HashSet;
/**
 * This ProjectionalJaccard similarity determines tag similarity by making each user have a vote on a Projectionaldatabase.
 * For more reference on the construction of this algorithm look at: www2009.org/proceedings/pdf/p641.pdf
 * @author alan
 *
 */
public class ProjectionalJaccard implements TagSimilarityMeasure{
	
	ProjectionalDatabase db;
	
	public ProjectionalJaccard(ProjectionalDatabase database){
		db = database;
	}
	/**
	 * Calculates the similarity between two tags in the ProjectionalDatabase.
	 * @param tag1 first tag
	 * @param tag2 second tag
	 */
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
