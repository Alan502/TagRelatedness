package edu.macalester.tagrelatedness;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


import edu.cmu.lti.jawjaw.pobj.POS;
import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.WS4J;
import edu.cmu.lti.ws4j.impl.JiangConrath;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;

public class Main {
	static int threads  = Runtime.getRuntime().availableProcessors();
	
	public static void main(String[] args) {
		ParallelForEach.LOG.info("Running program with "+threads+" threads.");
		
		CSVSorter.sortCSV("collab_matching-tas-most-common.csv", "collab_matching-tas-most-common-sorted.csv",true);
		CSVSorter.sortCSV("collab_MI-tas-most-common.csv", "collab_MI-tas-most-common-sorted.csv",true);
		CSVSorter.sortCSV("collab_matching-tas.csv", "collab_matching-tas-sorted.csv",true);
		CSVSorter.sortCSV("collab_MI-tas.csv", "collab_MI-tas-sorted.csv",true);
		CSVSorter.sortCSV("collab_matching-movielens.csv", "collab_matching-movielens-sorted.csv",true);
		CSVSorter.sortCSV("collab_MI-movielens.csv", "collab_MI-movielens-sorted.csv",true);
		CSVSorter.sortCSV("dist_MI-tas-most-common.csv","dist_MI-tas-most-common-sorted.csv",true);
		CSVSorter.sortCSV("dist_MI-tas.csv","dist_MI-tas-sorted.csv",true);
		CSVSorter.sortCSV("dist_MI-movielens.csv","dist_MI-movielens-sorted.csv",true);
		CSVSorter.sortCSV("dist_matching-tas-most-common.csv","dist_matching-tas-most-common-sorted.csv",true);
		CSVSorter.sortCSV("dist_matching-tas.csv","dist_matching-tas-sorted.csv",true);
		CSVSorter.sortCSV("dist_matching-movielens.csv","dist_matching-movielens-sorted.csv",true);
		CSVSorter.sortCSV("wikAPIdia_ensemble-tas-most-common.csv","wikAPIdia_ensemble-tas-most-common-sorted.csv",true);
		CSVSorter.sortCSV("wikAPIdia_ensemble-tas.csv","wikAPIdia_ensemble-tas-sorted.csv",true);
		CSVSorter.sortCSV("wikAPIdia_ensemble-movielens.csv","wikAPIdia_ensemble-movielens-sorter.csv",true);
			
//		generateMostFrequentResources("bibsonomy/2007-10-31/tas", "bibsonomy/2007-10-31/tas-2000-most-common");		
//		CollaborativeDatabase db = new CollaborativeDatabase();
		//db.initializeMovieLensTags("ml-10M100K/tags.dat");
//		db.initializeBibsonomyTags("bibsonomy/2007-10-31/tas-2000-most-common");
//		CollaborativeDatabase db2 = new CollaborativeDatabase();
//		db2.initializeBibsonomyTags("bibsonomy/2007-10-31/tas");
//		CollaborativeDatabase db3 = new CollaborativeDatabase();
//		db3.initializeMovieLensTags("ml-10M100K/tags.dat");
//		try {
//			generateTagSimilarityCSV(new LinkedList<>(db.getTagsSet()), new CollaborativeMatching(db), "collab_matching-tas-most-common.csv");
//			generateTagSimilarityCSV(new LinkedList<>(db.getTagsSet()), new CollaborativeMutualInformation(db), "collab_MI-tas-most-common.csv");
//			generateTagSimilarityCSV(new LinkedList<>(db2.getTagsSet()), new CollaborativeMatching(db2), "collab_matching-tas.csv");
//			generateTagSimilarityCSV(new LinkedList<>(db2.getTagsSet()), new CollaborativeMutualInformation(db2), "collab_MI-tas.csv");
//			generateTagSimilarityCSV(new LinkedList<>(db3.getTagsSet()), new CollaborativeMatching(db3), "collab_matching-movielens.csv");
//			generateTagSimilarityCSV(new LinkedList<>(db3.getTagsSet()), new CollaborativeMutualInformation(db3), "collab_MI-movielens.csv");
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
//			generateTagSimilarityCSV(new LinkedList<>(ddb.getTagsSet()), new DistributionalMutualInformation(ddb), "dist_MI-tas-most-common.csv");
//			generateTagSimilarityCSV(new LinkedList<>(ddb2.getTagsSet()), new DistributionalMutualInformation(ddb2), "dist_MI-tas.csv");
//			generateTagSimilarityCSV(new LinkedList<>(ddb3.getTagsSet()), new DistributionalMutualInformation(ddb3), "dist_MI-movielens.csv");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		ProjectionalDatabase pdb = new ProjectionalDatabase();
//		pdb.initializeBibsonomyTags("bibsonomy/2007-10-31/tas-2000-most-common");
//		
//		ProjectionalDatabase pdb2 = new ProjectionalDatabase();
//		pdb2.initializeBibsonomyTags("bibsonomy/2007-10-31/tas");
//		
//		ProjectionalDatabase pdb3 = new ProjectionalDatabase();
//		pdb3.initializeMovieLensTags("ml-10M100K/tags.dat");
//		try {
//			generateTagSimilarityCSV(new LinkedList<>(pdb.getTagsSet()), new DistributionalMatching(pdb), "dist_matching-tas-most-common.csv");
//			generateTagSimilarityCSV(new LinkedList<>(pdb2.getTagsSet()), new DistributionalMatching(pdb2), "dist_matching-tas.csv");
//			generateTagSimilarityCSV(new LinkedList<>(pdb3.getTagsSet()), new DistributionalMatching(pdb3), "dist_matching-movielens.csv");
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		WikAPIdiaEnsemble wikApidia = new WikAPIdiaEnsemble(System.getProperty("user.home")+"/.wikAPIdia/");
//		try {
//			generateTagSimilarityCSV(new LinkedList<>(pdb.getTagsSet()), wikApidia, "wikAPIdia_ensemble-tas-most-common.csv.csv");
//			generateTagSimilarityCSV(new LinkedList<>(pdb2.getTagsSet()), wikApidia, "wikAPIdia_ensemble-tas.csv");
//			generateTagSimilarityCSV(new LinkedList<>(pdb3.getTagsSet()), wikApidia, "wikAPIdia_ensemble-movielens.csv");
//		} catch (IOException e) {
//			
//			e.printStackTrace();
//		}		
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
//			generateTagSimilarityCSV(new LinkedList<>(pdb.getTagsSet()), wikApidia, "movielens_wikapidia_ensemble.csv");
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
//			generateTagSimilarityCSV(new LinkedList<>(pdb2.getTagsSet()), wikApidia, "bibsonomy_wikapidia_ensemble.csv");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		System.out.println("WikAPIdiaEnsemble for all of bibsonomy:");
//		
//		tauBetweenCSVandWordnet("bibsonomy_wikapidia_ensemble.csv");
	}
	public static void tauBetweenCSVandWordnet(String file){
		ILexicalDatabase db = new NictWordNet();
		final RelatednessCalculator rc = new JiangConrath(db);
		WS4JConfiguration.getInstance().setMFS(true);

		final ArrayList<Double> measurementSimilarities  = new ArrayList<Double>();
		final ArrayList<Double> wordnetSimilarities = new ArrayList<Double>();
		
		java.util.List<String> lines = null;
		try {
			lines = Files.readAllLines(Paths.get(file), Charset.defaultCharset());
		} catch (IOException e1) {
			e1.printStackTrace();
		}		
		ParallelForEach.loop(lines.subList(1, lines.size()), threads, new Procedure<String>() {
			public void call(String line){
				String[] column = line.split(",");
				 String word1 = column[0].replace("\"", "").replace(" ", "");
				 String word2 = column[1].replace("\"", "").replace(" ", "");
				 double jc = rc.calcRelatednessOfWords(word1, word2);
				 
				 if(!(jc < 0.00000000000000001 && jc > -.00000000000000001)){
					 synchronized (measurementSimilarities) {
						 try{
							 measurementSimilarities.add(Double.parseDouble(column[2]));
							 wordnetSimilarities.add(jc);
						 }catch(NumberFormatException e){
							 System.out.println("NumberFormatException Ex: "+column[2]);
						 }catch(ArrayIndexOutOfBoundsException e){
							 System.out.println("ArrayIndexOutOfBounds Ex: "+Arrays.toString(column));
						 }
					 }
				 }
			}
		});
	    System.out.println("Tau: "+KendallsCorrelation.correlation(measurementSimilarities, wordnetSimilarities));
	}
	
	public static void generateTagSimilarityCSV(LinkedList<String> tagsList, final TagSimilarityMeasure similarityMeasure, String outputFile) throws IOException{
		final LinkedList<String> tags = tagsList;
		FileWriter fWriter = null;
		try {
			fWriter = new FileWriter(outputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		fWriter.append('"' + " Tag 1 " + '"'+ ',' + '"' + " Tag 2 " + '"' + " , " + "Similarity");
		fWriter.append('\n');
		final DecimalFormat formatter = new DecimalFormat("0.00000000000000000");
		formatter.setRoundingMode(RoundingMode.HALF_UP);
								
		final FileWriter writer = fWriter;
		ParallelForEach.loop(tags,
				threads,
				new Procedure<String>() {
                    @Override
                    public void call(String comparingTag) throws Exception {
                    	int start = tags.indexOf(comparingTag);
                    	for(String comparedTag : tags.subList(start+1, tags.size() )){
            				double cc = similarityMeasure.calculateSimilarity(comparingTag, comparedTag);
//            				if(!(cc < 0.001 && cc > -.001)){
            					// Remove newlines, commas and apostrophes that may distort the CSV file when being written.
            					synchronized(writer){
            					writer.append("\"" + comparingTag.replace("\"", "").replace("\n", "").replace(",", "") + '"'+ ',' + '"' + comparedTag.replace("\"", "").replace("\n", "").replace(",", "") + '"' + "," + formatter.format(cc) +"\n");
            					}
//            				}
            				
            			}
                    }
                }
				);
		
		

		fWriter.flush();
	    fWriter.close();
		
	}
	
	public static void generateMostFrequentResources(String bibsonomyDSdir, String outputDir){
		FileInputStream fileStream;
		BufferedInputStream bufferedStream;
		BufferedReader readerStream;
		
		List<BibsonomyRecord> overlappingTagEntries = new LinkedList<BibsonomyRecord>();
		
		try {
			fileStream = new FileInputStream(bibsonomyDSdir);
			bufferedStream = new BufferedInputStream(fileStream);
			readerStream = new BufferedReader(new InputStreamReader(bufferedStream));
			
			while(readerStream.ready()){
				String line = readerStream.readLine();
				String tagInfo[] = line.split("\t");
				if( tagInfo.length == 5 && (
						!WS4J.findDefinitions(tagInfo[1], POS.a).isEmpty() ||
						!WS4J.findDefinitions(tagInfo[1], POS.n).isEmpty() ||
						!WS4J.findDefinitions(tagInfo[1], POS.r).isEmpty() ||
						!WS4J.findDefinitions(tagInfo[1], POS.v).isEmpty()
						)){
					overlappingTagEntries.add(new BibsonomyRecord(tagInfo[2], line));
				}
			}
						
			readerStream.close();
			fileStream.close();
			bufferedStream.close();
			
		} catch (FileNotFoundException e) {
			System.out.println("File not found exception: "+e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IOException: "+e.getMessage());
			e.printStackTrace();
		}
		
		Collections.sort(overlappingTagEntries);
		
		List<EntryFrequency> bibsonomyEntries  = new LinkedList<EntryFrequency>();
		
		String lastContentID = overlappingTagEntries.get(0).contentID;
		int frequency = 1;
		
		for(BibsonomyRecord rec : overlappingTagEntries.subList(1, overlappingTagEntries.size())){
			if(lastContentID.equals(rec.contentID)){
				if(rec.equals(overlappingTagEntries.get(overlappingTagEntries.size()-1)))
					bibsonomyEntries.add(new EntryFrequency(lastContentID, frequency));
				else
					frequency++;
			}else{
				bibsonomyEntries.add(new EntryFrequency(lastContentID, frequency));
				lastContentID = rec.contentID;
				frequency = 1;
			}
		}
		
		Collections.sort(bibsonomyEntries);
		Collections.reverse(bibsonomyEntries);
						
		ArrayList<String> mostCommonResources= new ArrayList<String>();
		for(EntryFrequency bib : bibsonomyEntries.subList(0, 2000)){
			mostCommonResources.add(bib.contentID);
		}
		
		FileWriter writer = null;
		
		try {
			writer = new FileWriter(outputDir);
			for(BibsonomyRecord rec : overlappingTagEntries){
				if(mostCommonResources.contains(rec.contentID)){
					writer.write(rec.line+"\n");
				}	
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			System.out.println("IOException: "+e.getMessage());
		}
        
	}
	  
}



