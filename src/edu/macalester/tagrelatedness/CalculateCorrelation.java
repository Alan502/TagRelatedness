package edu.macalester.tagrelatedness;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import edu.cmu.lti.ws4j.WS4J;

/**
 * Created by alan on 6/9/14.
 */
public class CalculateCorrelation {


    public static void main(String[] args){
        CommandLineParser parser = new PosixParser();
        Options options = new Options();
        options.addOption(OptionBuilder.withLongOpt("input-file")
                                        .withDescription("Input file to calculate the correlation of")
                                        .hasArg()
                                        .withArgName("FILE")
                                        .withType(File.class)
                                        .create("i"));

        HelpFormatter formatter = new HelpFormatter();

        try {
            File input = null;
            CommandLine line = parser.parse(options, args);
            if(line.hasOption("i")){
            	input = new File(line.getOptionValue("i"));
            }else if(line.hasOption("input-file")){
                input = new File(line.getOptionValue("input-file"));
            }else{
                System.out.println("ERROR: An input file needs to be specified.");
                formatter.printHelp("Correlation calculator", options);
                System.exit(1);
            }
            assert null != input;
            tauBetweenCSVandWordnet(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public static void tauBetweenCSVandWordnet(File file, int threads){	
    	
    	long start = System.nanoTime();

        final ArrayList<Double> measurementSimilarities  = new ArrayList<Double>();
        final ArrayList<Double> wordnetSimilarities = new ArrayList<Double>();

        java.util.List<String> lines = null;
        try {
            lines = Files.readAllLines(Paths.get(file.getAbsolutePath()), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Similarities to add: "+lines.size());
        
        
	   ParallelForEach.loop(lines.subList(0, lines.size()), threads, new Procedure<String>() {
	   public void call(String line){
	          String[] column = line.split(",");
	          String word1 = column[0].replace("\"", "").replace(" ", "");
	          String word2 = column[1].replace("\"", "").replace(" ", "");
	          double jc = WS4J.runJCN(word1, word2);
	          double cc = Double.parseDouble(column[2]);
	
	          if(jc != 0){ // check that wordnet does have a result for this word pair
	              synchronized (measurementSimilarities) {
	                          measurementSimilarities.add(cc);
	                          wordnetSimilarities.add(jc);
	              }
	          }
	      }
	  });
        
        System.out.println("Tau: "+KendallsCorrelation.correlation(measurementSimilarities, wordnetSimilarities));
        
    }
    
    public static void tauBetweenCSVandWordnet(File file){
        tauBetweenCSVandWordnet(file, Runtime.getRuntime().availableProcessors());
    }

}
