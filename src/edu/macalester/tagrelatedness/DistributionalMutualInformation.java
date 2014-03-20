package edu.macalester.tagrelatedness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
/**
 * The DistributionalMutualInformation similarity measure is able to extract signals of similarity based on the frequency in which users have associated
 * a given tag with a resource. For reference of the construction of this algorithm look at: www2009.org/proceedings/pdf/p641.pdf
 * @author Alan Morales
 * @see DistributionalDatabase
 */
public class DistributionalMutualInformation implements TagSimilarityMeasure{

	private DistributionalDatabase db;
	public DistributionalMutualInformation(DistributionalDatabase database){
		db = database;
	}
	/**
	 * Calculates the similarity between two tags in the database.
	 * @param tag1 the first tag
	 * @param tag2 the second tag
	 */
	public double calculateSimilarity(String tag1, String tag2) {
		HashMap<String, ArrayList<String>> tagsMap = db.getTagsMap();
		HashMap<String, ArrayList<String>> resourcesMap = db.getResourcesMap();
		
		ArrayList<String> resourceList1 = tagsMap.get(tag1);	
		ArrayList<String> resourceList2 = tagsMap.get(tag2);
		
		HashSet<String> resourceSet1 = new HashSet<String>(resourceList1);
		HashSet<String> resourceSet2 = new HashSet<String>(resourceList2);	

		double similarity = 0.0;
		final double totalEntries = db.getTotalEntries();		
		for(String comparingResource : resourceSet1){
			ArrayList<String> tagsList1 = resourcesMap.get(comparingResource);
			double marginalProbability1 = tagsList1.size()/totalEntries;
			for(String comparedResource : resourceSet2){			
				ArrayList<String> tagsList2 = resourcesMap.get(comparedResource);
				double marginalProbability2 = tagsList2.size()/totalEntries;
				
				HashSet<String> tagsSet1 = new HashSet<String>(tagsList1);				
				HashSet<String> tagsSet2 = new HashSet<String>(tagsList2);
								
				tagsSet1.retainAll(tagsSet2); // tagSet1 now holds the intersection between both sets
				
				double minSum = 0.0;
				
				/* We use the intersection between tags because
				 * all the other fields will be equal to 0, therefore
				 * the do not attribute to the sum*/
								
				for(String comparingTag : tagsSet1){
				double freq1 = 0.0;
				double freq2 = 0.0;
					for(String comparedTag : tagsList1){
						if(comparingTag.equals(comparedTag))
							freq1++;
					}
					
					for(String comparedTag : tagsList2){
						if(comparingTag.equals(comparedTag))
							freq2++;
					}					
					
					minSum += freq1 - freq2 < 0 ? (freq1/ (double) tagsList1.size() ) : (freq2/  (double) tagsList2.size());
				}
								
				double jointProbability = minSum/totalEntries;
				
				similarity += jointProbability != 0 ? jointProbability * Math.log(jointProbability / (marginalProbability1* marginalProbability2)) : 0;
			}
		}	
		return similarity;
	}
	

}
