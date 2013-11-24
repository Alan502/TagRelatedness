package edu.macalester.moviedatabase;

public interface TagSimilarityMeasure {
	double calculateSimilarity(String tag1, String tag2);
}
