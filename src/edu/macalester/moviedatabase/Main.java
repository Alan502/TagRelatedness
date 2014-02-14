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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.TreeMap;

import edu.cmu.lti.jawjaw.pobj.POS;
import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.WS4J;
import edu.cmu.lti.ws4j.impl.JiangConrath;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;


public class Main {
	static int threads  = Runtime.getRuntime().availableProcessors();

	public static void main(String[] args) {		
		ParallelForEach.LOG.info("Running program with "+threads+" threads.");
		CollaborativeDatabase db = new CollaborativeDatabase();
		//db.initializeMovieLensTags("ml-10M100K/tags.dat");
		db.intializeBibsonomyTags("bibsonomy/2008-01-01/tas-2000-most-common");
		try {
			generateTagSimilarityCSV(db, new CollaborativeMatching(db), "collab_matching.csv");
			generateTagSimilarityCSV(db, new CollaborativeMutualInformation(db), "collab_MI.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		DistributionalDatabase ddb = new DistributionalDatabase();
		//ddb.initializeMovieLensTags("ml-10M100K/tags.dat");
		ddb.intializeBibsonomyTags("bibsonomy/2008-01-01/tas-2000-most-common");
		try {
			generateTagSimilarityCSV(ddb, new DistributionalMutualInformation(ddb), "dist_MI.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ProjectionalDatabase pdb = new ProjectionalDatabase();
		//pdb.initializeMovieLensTags("ml-10M100K/tags.dat");
		pdb.intializeBibsonomyTags("bibsonomy/2008-01-01/tas-2000-most-common");
		try {
			generateTagSimilarityCSV(pdb, new DistributionalMatching(pdb), "dist_matching.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Calculation for collaborative matching:");
		tauBetweenCSVandWordnet("collab_matching.csv");
		System.out.println("Calculation for collaborative MI:");
		tauBetweenCSVandWordnet("collab_MI.csv");
		System.out.println("Calculation for distributional matching:");
		tauBetweenCSVandWordnet("dist_matching.csv");
		System.out.println("Calculation for distributional MI:");
		tauBetweenCSVandWordnet("dist_MI.csv");
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
		
		LinkedList<String> resourcesWithOverlappingTags = new LinkedList<String>();
		
		try {
			fileStream = new FileInputStream(bibsonomyDSdir);
			bufferedStream = new BufferedInputStream(fileStream);
			readerStream = new BufferedReader(new InputStreamReader(bufferedStream));
			
			while(readerStream.ready()){
				String line = readerStream.readLine();
				String tagInfo[] = line.split("\t");
				if( tagInfo.length == 5 && // the line was split correctly
						//check that the word exists in the wordnet dictionary:
						(!WS4J.findDefinitions(tagInfo[1], POS.n).isEmpty()
					||  !WS4J.findDefinitions(tagInfo[1], POS.v).isEmpty()
					||  !WS4J.findDefinitions(tagInfo[1], POS.a).isEmpty()
					||  !WS4J.findDefinitions(tagInfo[1], POS.r).isEmpty())
					){
					resourcesWithOverlappingTags.add(tagInfo[2]);
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
		
		Collections.sort(resourcesWithOverlappingTags);
		LinkedList<BibsonomyEntry> bibs = new LinkedList<BibsonomyEntry>();
        
        int count = 0;
        String lastKey = resourcesWithOverlappingTags.get(0);
        System.out.println(resourcesWithOverlappingTags);
		for(String res : resourcesWithOverlappingTags.subList(1, resourcesWithOverlappingTags.size())){
			if(res.equals(lastKey)){
				count++;
			}else{
				System.out.println(count+" "+lastKey);
				bibs.add(new BibsonomyEntry(lastKey, count));
				lastKey = res;
				count = 0;
			}
		}
		
		Collections.sort(bibs);
		Collections.reverse(bibs);
		
        try {
			FileWriter writer = new FileWriter(outputDir);
				
			int i = 0;
			for(BibsonomyEntry bib : bibs){
				if(2000 <= i)
					break;
				
				writer.append(bib.contentID+"\n");	
				
				i++;
			}
			
			writer.flush();
			writer.close();
			
		} catch (IOException e) {
			System.out.println("IOException: "+e.getMessage());
			e.printStackTrace();
		}
        
	}
	
	public static void filterBibsonomy(String mostCommonResourcesCSV, String bibsonomyDir){
		java.util.List<String> lines = null;
		try {
			lines = Files.readAllLines(Paths.get(mostCommonResourcesCSV), Charset.defaultCharset());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		HashSet<String> mostCommonResources = new HashSet<String>(lines);
		
		FileInputStream fileStream = null;
		BufferedInputStream bufferedStream = null;
		BufferedReader readerStream = null;
		FileWriter writer = null;
		
		try {
			fileStream = new FileInputStream(bibsonomyDir);
			bufferedStream = new BufferedInputStream(fileStream);
			readerStream = new BufferedReader(new InputStreamReader(bufferedStream));
			writer = new FileWriter("tas-2000-most-common");
			
			while(readerStream.ready()){
				String line = readerStream.readLine();
				String tagInfo[] = line.split("\t");
				if( tagInfo.length == 5 && mostCommonResources.contains(tagInfo[2]) ){
					writer.append(line+"\n");
				}		
			}
			
			readerStream.close();
			fileStream.close();
			bufferedStream.close();
			writer.flush();
			writer.close();
			
		} catch (FileNotFoundException e) {
			System.out.println("File not found exception: "+e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IOException: "+e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	

}

