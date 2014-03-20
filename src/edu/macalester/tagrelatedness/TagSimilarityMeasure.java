package edu.macalester.tagrelatedness;

public interface TagSimilarityMeasure {
	double calculateSimilarity(String tag1, String tag2);
}
