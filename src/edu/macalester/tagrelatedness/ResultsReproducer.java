package edu.macalester.tagrelatedness;

import java.io.IOException;
import java.util.LinkedList;


/**
 * This class reproduces the results of the paper found at www2009.org/proceedings/pdf/p641.pdf.
 * In order for this class to run correctly, a bibsonomy file 
 * @author alan
 *
 */
public class ResultsReproducer {
	
	public static void main(String[] args) {
		
		CSVUtils.generateMostFrequentResources("bibsonomy/2007-10-31/tas", "bibsonomy/2007-10-31/tas-2000-most-common");
		CollaborativeDatabase db = new CollaborativeDatabase();
		db.initializeBibsonomyTags("bibsonomy/2007-10-31/tas-2000-most-common");
		DistributionalDatabase ddb = new DistributionalDatabase();
		ddb.initializeBibsonomyTags("bibsonomy/2007-10-31/tas-2000-most-common");
		ProjectionalDatabase pdb = new ProjectionalDatabase();
		pdb.initializeBibsonomyTags("bibsonomy/2007-10-31/tas-2000-most-common");
		
		try {
			CSVUtils.generateTagSimilarityCSV(new LinkedList<>(db.getTagsSet()), new CollaborativeMatching(db), "collab_matching-tas-most-common.csv");
			CSVUtils.generateTagSimilarityCSV(new LinkedList<>(db.getTagsSet()), new CollaborativeMutualInformation(db), "collab_MI-tas-most-common.csv");
			CSVUtils.generateTagSimilarityCSV(new LinkedList<>(ddb.getTagsSet()), new DistributionalMutualInformation(ddb), "dist_MI-tas-most-common.csv");
			CSVUtils.generateTagSimilarityCSV(new LinkedList<>(pdb.getTagsSet()), new DistributionalMatching(pdb), "dist_matching-tas-most-common.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		KendallsCorrelation.tauBetweenCSVandWordnet("collab_matching-tas-most-common.csv", true, 2);
		KendallsCorrelation.tauBetweenCSVandWordnet("collab_MI-tas-most-common.csv", true, 15 );
		KendallsCorrelation.tauBetweenCSVandWordnet("dist_MI-tas-most-common.csv", false, 15);
		KendallsCorrelation.tauBetweenCSVandWordnet("dist_matching-tas-most-common.csv", false, 15);
	}

}