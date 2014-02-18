package edu.macalester.moviedatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class DistributionalMutualInformation implements TagSimilarityMeasure{

	private DistributionalDatabase db;
	public DistributionalMutualInformation(DistributionalDatabase database){
		db = database;
	}
	
	public double calculateSimilarity(String tag1, String tag2) {
		HashMap<String, ArrayList<String>> tagsMap = db.getTagsMap();
		HashMap<String, ArrayList<String>> moviesMap = db.getMoviesMap();
		
		ArrayList<String> movieList1 = tagsMap.get(tag1);	
		ArrayList<String> movieList2 = tagsMap.get(tag2);
		
		HashSet<String> movieSet1 = new HashSet<String>(movieList1);
		HashSet<String> movieSet2 = new HashSet<String>(movieList2);	

		double similarity = 0.0;
		final double totalEntries = db.getTotalEntries();		
		for(String comparingMovie : movieSet1){
			ArrayList<String> tagsList1 = moviesMap.get(comparingMovie);
			double marginalProbability1 = tagsList1.size()/totalEntries;
			for(String comparedMovie : movieSet2){			
				ArrayList<String> tagsList2 = moviesMap.get(comparedMovie);
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
					
					minSum += freq1 - freq2 < 0 ? freq1/((double) tagsList1.size()): (freq2/ (double) tagsList2.size());
				}
								
				double jointProbability = minSum/totalEntries;
				
				similarity += jointProbability != 0 ? jointProbability * Math.log(jointProbability / (marginalProbability1* marginalProbability2)) : 0;
			}
		}	
		return similarity;
	}
	

}
