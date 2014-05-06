package edu.macalester.tagrelatedness;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import com.google.code.externalsorting.ExternalSort;


public class Main {	
	public static void main(String[] args) {
		
		CollaborativeDatabase db = new CollaborativeDatabase();
		db.initializeMovieLensTags("ml-10M100K/tags.dat");
		db.initializeBibsonomyTags("bibsonomy/2007-10-31/tas-2000-most-common");
		CollaborativeDatabase db2 = new CollaborativeDatabase();
		db2.initializeBibsonomyTags("bibsonomy/2007-10-31/tas");
		CollaborativeDatabase db3 = new CollaborativeDatabase();
		db3.initializeMovieLensTags("ml-10M100K/tags.dat");
//		try {
//			CSVUtils.generateTagSimilarityCSV(new LinkedList<>(db.getTagsSet()), new CollaborativeMatching(db), "collab_matching-tas-most-common.csv");
//			CSVUtils.generateTagSimilarityCSV(new LinkedList<>(db.getTagsSet()), new CollaborativeMutualInformation(db), "collab_MI-tas-most-common.csv");
//			CSVUtils.generateTagSimilarityCSV(new LinkedList<>(db2.getTagsSet()), new CollaborativeMatching(db2), "collab_matching-tas.csv");
//			CSVUtils.generateTagSimilarityCSV(new LinkedList<>(db2.getTagsSet()), new CollaborativeMutualInformation(db2), "collab_MI-tas.csv");
//			CSVUtils.generateTagSimilarityCSV(new LinkedList<>(db3.getTagsSet()), new CollaborativeMatching(db3), "collab_matching-movielens.csv");
//			CSVUtils.generateTagSimilarityCSV(new LinkedList<>(db3.getTagsSet()), new CollaborativeMutualInformation(db3), "collab_MI-movielens.csv");
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		DistributionalDatabase ddb = new DistributionalDatabase();
//		//ddb.initializeMovieLensTags("ml-10M100K/tags.dat");
//		ddb.initializeBibsonomyTags("bibsonomy/2007-10-31/tas-2000-most-common");
//		
//		DistributionalDatabase ddb2 = new DistributionalDatabase();
//		ddb2.initializeBibsonomyTags("bibsonomy/2007-10-31/tas");
//		
//		DistributionalDatabase ddb3 = new DistributionalDatabase();
//		db3.initializeMovieLensTags("ml-10M100K/tags.dat");
//
//		try {
//			CSVUtils.generateTagSimilarityCSV(new LinkedList<>(ddb.getTagsSet()), new DistributionalMutualInformation(ddb), "dist_MI-tas-most-common.csv");
//			CSVUtils.generateTagSimilarityCSV(new LinkedList<>(ddb2.getTagsSet()), new DistributionalMutualInformation(ddb2), "dist_MI-tas.csv");
//			CSVUtils.generateTagSimilarityCSV(new LinkedList<>(ddb3.getTagsSet()), new DistributionalMutualInformation(ddb3), "dist_MI-movielens.csv");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
		ProjectionalDatabase pdb = new ProjectionalDatabase();
		pdb.initializeBibsonomyTags("bibsonomy/2007-10-31/tas-2000-most-common");
		
		ProjectionalDatabase pdb2 = new ProjectionalDatabase();
		pdb2.initializeBibsonomyTags("bibsonomy/2007-10-31/tas");
		
		ProjectionalDatabase pdb3 = new ProjectionalDatabase();
		pdb3.initializeMovieLensTags("ml-10M100K/tags.dat");
//		try {
//			CSVUtils.generateTagSimilarityCSV(new LinkedList<>(pdb.getTagsSet()), new DistributionalMatching(pdb), "dist_matching-tas-most-common.csv");
//			CSVUtils.generateTagSimilarityCSV(new LinkedList<>(pdb2.getTagsSet()), new DistributionalMatching(pdb2), "dist_matching-tas.csv");
//			CSVUtils.generateTagSimilarityCSV(new LinkedList<>(pdb3.getTagsSet()), new DistributionalMatching(pdb3), "dist_matching-movielens.csv");
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		WikAPIdiaEnsemble wikApidia = new WikAPIdiaEnsemble(System.getProperty("user.home")+"/.wikAPIdia/");
		try {
			CSVUtils.generateTagSimilarityCSV(new LinkedList<>(pdb.getTagsSet()), wikApidia, "wikAPIdia_ensemble-tas-most-common.csv.csv");
			CSVUtils.generateTagSimilarityCSV(new LinkedList<>(pdb2.getTagsSet()), wikApidia, "wikAPIdia_ensemble-tas.csv");
			CSVUtils.generateTagSimilarityCSV(new LinkedList<>(pdb3.getTagsSet()), wikApidia, "wikAPIdia_ensemble-movielens.csv");
		} catch (IOException e) {
			
			e.printStackTrace();
		}		
		
				
		ExternalSort.defaultcomparator = new CSVComparator();
		
		try{		
		ExternalSort.sort(new File("collab_matching-tas-most-common.csv"), new File("collab_matching-tas-most-common-sorted.csv"));
		ExternalSort.sort(new File("collab_MI-tas-most-common.csv"), new File("collab_MI-tas-most-common-sorted.csv"));
		ExternalSort.sort(new File("collab_matching-tas.csv"), new File("collab_matching-tas-sorted.csv"));
		ExternalSort.sort(new File("collab_MI-tas.csv"), new File("collab_MI-tas-sorted.csv"));
		ExternalSort.sort(new File("collab_matching-movielens.csv"), new File("collab_matching-movielens-sorted.csv"));
		ExternalSort.sort(new File("collab_MI-movielens.csv"), new File("collab_MI-movielens-sorted.csv"));
		ExternalSort.sort(new File("dist_MI-tas-most-common.csv"),new File("dist_MI-tas-most-common-sorted.csv"));
		ExternalSort.sort(new File("dist_MI-tas.csv"),new File("dist_MI-tas-sorted.csv"));
		ExternalSort.sort(new File("dist_MI-movielens.csv"),new File("dist_MI-movielens-sorted.csv"));
		ExternalSort.sort(new File("dist_matching-tas-most-common.csv"),new File("dist_matching-tas-most-common-sorted.csv"));
		ExternalSort.sort(new File("dist_matching-tas.csv"),new File("dist_matching-tas-sorted.csv"));
		ExternalSort.sort(new File("dist_matching-movielens.csv"),new File("dist_matching-movielens-sorted.csv"));
		ExternalSort.sort(new File("wikAPIdia_ensemble-tas-most-common.csv"),new File("wikAPIdia_ensemble-tas-most-common-sorted.csv"));
		ExternalSort.sort(new File("wikAPIdia_ensemble-tas.csv"),new File("wikAPIdia_ensemble-tas-sorted.csv"));
		ExternalSort.sort(new File("wikAPIdia_ensemble-movielens.csv"),new File("wikAPIdia_ensemble-movielens-sorted.csv"));
		}catch(Exception e){
			System.out.println(e.toString());
		}
		
		
			
		
//		
//		System.out.println("Calculation for collaborative matching tas most common:");
//		tauBetweenCSVandWordnet("collab_matching-tas-most-common.csv");
//		System.out.println("Calculation for collaborative matching tas:");
//		tauBetweenCSVandWordnet("collab_matching-tas.csv");
//		System.out.println("Calculation for collaborative matching movielens:");
//		tauBetweenCSVandWordnet("collab_matching-movielens.csv");
//		System.out.println("Calculation for collaborative MI tas most common:");
//		tauBetweenCSVandWordnet("collab_MI-tas-most-common.csv");
//		System.out.println("Calculation for collaborative MI tas:");
//		tauBetweenCSVandWordnet("collab_MI-tas.csv");
//		System.out.println("Calculation for collaborative MI movielens:");
//		tauBetweenCSVandWordnet("collab_MI-movielens.csv");
//		System.out.println("Calculation for distributional matching tas most common:");
//		tauBetweenCSVandWordnet("dist_matching-tas-most-common.csv");
//		System.out.println("Calculation for distributional matching tas:");
//		tauBetweenCSVandWordnet("dist_matching-tas.csv");
//		System.out.println("Calculation for distributional MI movielens:");
//		tauBetweenCSVandWordnet("dist_MI-movielens.csv");
//		ProjectionalDatabase pdb = new ProjectionalDatabase();
//		pdb.initializeMovieLensTags("ml-10M100K/tags.dat");
//						
//		WikAPIdiaEnsemble wikApidia = new WikAPIdiaEnsemble(System.getProperty("user.home")+"/.wikAPIdia/");
//		try {
//			CSVHandler.generateTagSimilarityCSV(new LinkedList<>(pdb.getTagsSet()), wikApidia, "movielens_wikapidia_ensemble.csv");
//		} catch (IOException e1) {
//			System.out.println("IO Exception: "+e1.getMessage());
//			e1.printStackTrace();
//		}
//		
//		System.out.println("WikAPIdiaEnsemble for movielens:");
//		tauBetweenCSVandWordnet("movielens_wikapidia_ensemble.csv");
//		
//		ProjectionalDatabase pdb2 = new ProjectionalDatabase();
//		pdb2.initializeBibsonomyTags("bibsonomy/2007-10-31/tas");
//		try {
//			CSVHandler.generateTagSimilarityCSV(new LinkedList<>(pdb2.getTagsSet()), wikApidia, "bibsonomy_wikapidia_ensemble.csv");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		System.out.println("WikAPIdiaEnsemble for all of bibsonomy:");
//		
//		tauBetweenCSVandWordnet("bibsonomy_wikapidia_ensemble.csv");
	}
	  
}



