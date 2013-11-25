package edu.macalester.moviedatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class DistributionalMatching implements TagSimilarityMeasure{
	
	ProjectionalDatabase db;

	public DistributionalMatching(ProjectionalDatabase database){
		db = database;
	}

	public double calculateSimilarity(String tag1, String tag2) {
		
		HashMap<String, HashSet<String>> tagsMap = db.getTagsMap();
		
		HashSet<String> movies1 = (HashSet<String>) tagsMap.get(tag1).clone();
		HashSet<String> movies2 = (HashSet<String>) tagsMap.get(tag2).clone();
		
		movies1.retainAll(movies2);
		
		double similarity = 0.0;
		final double totalMovies = db.getMoviesSet().size();
		
		HashMap<String, HashSet<String>> moviesMap = db.getMoviesMap();
		
		for(String movie : movies1 ){
			double associated = moviesMap.get(movie).size();
			similarity += Math.log(associated / totalMovies );
		}
		
		return similarity*-1;
	}
	
	

}
