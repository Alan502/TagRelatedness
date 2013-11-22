package edu.macalester.moviedatabase;

public interface TagSimilarityMeasure {
	int calculateSimilarity(String tag1, String tag2);
}
