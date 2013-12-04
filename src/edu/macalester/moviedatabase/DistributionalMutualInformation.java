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
		
		HashSet<String> movieSet1 = new HashSet<String>();
		for(String movie : movieList1)
			movieSet1.add(movie);
		
		HashSet<String> movieSet2 = new HashSet<String>();
		for(String movie : movieList2)
			movieSet2.add(movie);		

		double similarity = 0.0;
		final double totalEntries = db.getTotalEntries();		
		for(String comparingMovie : movieSet1){
			double marginalProbability1 = moviesMap.get(comparingMovie).size()/totalEntries;
			for(String comparedMovie : movieSet2){
				double marginalProbability2 = moviesMap.get(comparedMovie).size()/totalEntries;
				
				ArrayList<String> tagsList1 = moviesMap.get(comparedMovie);
				ArrayList<String> tagsList2 = moviesMap.get(comparingMovie);
				
				HashSet<String> tagsSet1 = new HashSet<String>();
				for(String tag : tagsList1 )
					tagsSet1.add(tag);
				
				HashSet<String> tagsSet2 = new HashSet<String>();
				for(String tag : tagsList2)
					tagsSet2.add(tag);
				
				tagsSet1.retainAll(tagsSet2); // tagSet1 now holds the intersection between both sets
				
				double minSum = 0.0;
				
				/* We use the intersection between tags because
				 * all the other fields will be equal to 0, therefore
				 * the do not attribute to the sum*/
				
				for(String tag : tagsSet1){
				double freq1 = 0;
				double freq2 = 0;
					for(String tagFreq : tagsSet1){
						if(tag.equals(tagFreq))
							freq1++;
					}
					
					for(String tagFreq : tagsSet2){
						if(tag.equals(tagFreq))
							freq2++;
					}					
					minSum += freq1 - freq2 > 0 ? freq2 : freq1;;
				}
								
				double jointProbability = minSum/totalEntries;
				
				similarity += jointProbability * Math.log(jointProbability / (marginalProbability1* marginalProbability2));
			}
		}	
		return similarity;
	}
	

}
