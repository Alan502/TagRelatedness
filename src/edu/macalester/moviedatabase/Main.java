package edu.macalester.moviedatabase;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import edu.cmu.lti.jawjaw.pobj.POS;
import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.WS4J;
import edu.cmu.lti.ws4j.impl.JiangConrath;
import edu.cmu.lti.ws4j.impl.Path;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;


public class Main {
	static int threads  = Runtime.getRuntime().availableProcessors();

	public static void main(String[] args) {		
		ParallelForEach.LOG.info("Running program with "+threads+" threads.");
		
//		generateMostFrequentResources("bibsonomy/2007-10-31/tas", "bibsonomy/2007-10-31/tas-2000-most-common");
				
		CollaborativeDatabase db = new CollaborativeDatabase();
		//db.initializeMovieLensTags("ml-10M100K/tags.dat");
		db.intializeBibsonomyTags("bibsonomy/2007-10-31/tas-2000-most-common");
		try {
			generateTagSimilarityCSV(db, new CollaborativeMatching(db), "collab_matching.csv");
//			generateTagSimilarityCSV(db, new CollaborativeMutualInformation(db), "collab_MI.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
//		DistributionalDatabase ddb = new DistributionalDatabase();
		//ddb.initializeMovieLensTags("ml-10M100K/tags.dat");
//		ddb.intializeBibsonomyTags("bibsonomy/2007-10-31/tas-2000-most-common");
//		try {
//			generateTagSimilarityCSV(ddb, new DistributionalMutualInformation(ddb), "dist_MI.csv");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		ProjectionalDatabase pdb = new ProjectionalDatabase();
//		pdb.initializeMovieLensTags("ml-10M100K/tags.dat");
//		pdb.intializeBibsonomyTags("bibsonomy/2007-10-31/tas-2000-most-common");
//		try {
//			generateTagSimilarityCSV(pdb, new DistributionalMatching(pdb), "dist_matching.csv");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		System.out.println("Calculation for collaborative matching:");
		tauBetweenCSVandWordnet("collab_matching.csv");
//		System.out.println("Calculation for collaborative MI:");
//		tauBetweenCSVandWordnet("collab_MI.csv");
//		System.out.println("Calculation for distributional matching:");
//		tauBetweenCSVandWordnet("dist_matching.csv");
//		System.out.println("Calculation for distributional MI:");
//		tauBetweenCSVandWordnet("dist_MI.csv");
	}
	
	public static void tauBetweenCSVandWordnet(String file){
		
		WS4JConfiguration.getInstance().setMFS(true);
        ILexicalDatabase db = new NictWordNet();
		final JiangConrath rc = new JiangConrath(db);
		
		
		final ArrayList<Double> distMatchingSimilarities  = new ArrayList<Double>();
		final ArrayList<Double> wordnetSimilarities = new ArrayList<Double>();
		
		java.util.List<String> lines = null;
		try {
			lines = Files.readAllLines(Paths.get(file), Charset.defaultCharset());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		ParallelForEach.loop(lines, threads, new Procedure<String>() {
			public void call(String line){
				String[] column = line.split(",");
				 double jc = rc.calcRelatednessOfWords(column[0].replace("\"", "").replace(" ", "") , column[1].replace("\"", "").replace(" ", ""));
				 if(jc != 0.0){
					 synchronized (distMatchingSimilarities) {
						 distMatchingSimilarities.add(Double.parseDouble(column[2]));
						 wordnetSimilarities.add(jc);
					 }
				 }
			}
		});
		
	    System.out.println("Tau: "+KendallsCorrelation.correlation(distMatchingSimilarities, wordnetSimilarities));
	}
	
	public static void generateTagSimilarityCSV(Database database, final TagSimilarityMeasure similarityMeasure, String outputFile) throws IOException{
		
		FileWriter fWriter = null;
		try {
			fWriter = new FileWriter(outputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		fWriter.append('"' + " Tag 1 " + '"'+ ',' + '"' + " Tag 2 " + '"' + " , " + "Similarity");
		fWriter.append('\n');
		
		final LinkedList<String> tags = new LinkedList<String>(database.getTagsSet());
								
		final FileWriter writer = fWriter;
		ParallelForEach.loop(tags,
				threads,
				new Procedure<String>() {
                    @Override
                    public void call(String comparingTag) throws Exception {
                    	int start = tags.indexOf(comparingTag);
                    	for(String comparedTag : tags.subList(start+1, tags.size() )){
							
            				double cc = similarityMeasure.calculateSimilarity(comparingTag, comparedTag);
            				
            				if(cc != 0){
            					// Remove newlines, commas and apostrophes that may distort the CSV file when being written.
            					synchronized(writer){
            					writer.append("\"" + comparingTag.replace("\"", "").replace("\n", "").replace(",", "") + '"'+ ',' + '"' + comparedTag.replace("\"", "").replace("\n", "").replace(",", "") + '"' + "," + cc+"\n");
            					}           						
            						
            						
            				}
            				
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
		
		WS4JConfiguration.getInstance().setMFS(true);
		
		List<BibsonomyRecord> overlappingTagEntries = new LinkedList<BibsonomyRecord>();
		
		try {
			fileStream = new FileInputStream(bibsonomyDSdir);
			bufferedStream = new BufferedInputStream(fileStream);
			readerStream = new BufferedReader(new InputStreamReader(bufferedStream));
			
			while(readerStream.ready()){
				String line = readerStream.readLine();
				String tagInfo[] = line.split("\t");
				if( tagInfo.length == 5 && 
						!(WS4J.findDefinitions(tagInfo[1], POS.a).isEmpty() &&
								WS4J.findDefinitions(tagInfo[1], POS.n).isEmpty() &&
								WS4J.findDefinitions(tagInfo[1], POS.r).isEmpty() &&
								WS4J.findDefinitions(tagInfo[1], POS.v).isEmpty()
								)
						){
					overlappingTagEntries.add(new BibsonomyRecord(tagInfo[2], line));
				}
			}
			
			System.out.println(overlappingTagEntries.size());
			
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

