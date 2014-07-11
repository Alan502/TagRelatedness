package edu.macalester.tagrelatedness;

import com.google.code.externalsorting.ExternalSort;
import org.apache.commons.cli.*;

import java.io.*;
import java.util.LinkedList;


public class Main {	
	public static void main(String[] args) {
        CommandLineParser parser = new PosixParser();
        String availableAlgorithms = "dist-matching" +
                " dist-mi" +
                " collab-matching" +
                " collab-mi" +
                " wikibrain-ensemble";
        String supportedDatabases = "bibsonomy " +
                "movielens";

        Options options = new Options();
        options.addOption(OptionBuilder.withLongOpt("output-file")
                                        .withDescription("File to output the analyzing on")
                                        .hasArg()
                                        .withArgName("FILE")
                                        .withType(File.class)
                                        .create());
        options.addOption(OptionBuilder.withLongOpt("input-file")
                                        .withDescription("Input file to take the tags from")
                                        .hasArg()
                                        .withType(File.class)
                                        .withArgName("FILE")
                                        .create());
        options.addOption(OptionBuilder.withLongOpt("algorithm")
                                        .withDescription("Algorithm to use to calculate the similarity. Algorithms available: "+availableAlgorithms)
                                        .hasArg()
                                        .withType(String.class)
                                        .withArgName("ALGORITHM")
                                        .create());

        HelpFormatter formatter = new HelpFormatter();

        File inputFile = null;
        String algorithmType = null;
        TagSimilarityMeasure algorithm = null;
        String outputFileDir = null;

        try {
            CommandLine line = parser.parse(options, args);

            if(!line.hasOption("input-file")){
                System.out.println("An input file needs to be specified");
                formatter.printHelp( "tag-relatedness", options );
                System.exit(1);
            }else{
                inputFile = new File(line.getOptionValue("input-file"));
            }

            algorithmType = line.getOptionValue("algorithm", "wikibrain-ensemble");
            outputFileDir = line.getOptionValue("output-file", algorithm+"-"+inputFile.getName());

        }catch (ParseException exp){
            System.out.println("Exception: "+exp.toString());
            System.exit(1);
        }

        BufferedReader inputReader = null;
        String firstLine = "";
        try {
            inputReader = new BufferedReader(new FileReader(inputFile));
            firstLine = inputReader.readLine();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        String databaseType = "";
        if(firstLine.split("::").length == 4 ){
            System.out.println("Detected database: movielens "+inputFile.getName());
            databaseType = "movielens";
        }else if(firstLine.split("\t").length == 5){
            System.out.println("Detected database: bibsonomy "+inputFile.getName());
            databaseType = "bibsonomy";
        }else{
            System.out.println("Database from inputFile "+inputFile.getName()+" does not seem to be one of available supported Databases. "+supportedDatabases);
            System.exit(1);
        }

        Database db = null;

        switch (algorithmType){
            default:
            System.out.println("No algorithm specified. Default: wikibrain-ensemble.");
            case "wikibrain-ensemble":
                algorithm = new WikAPIdiaEnsemble(System.getProperty("user.home")+"/.wikibrain/");
                break;
            case "collab-matching":
                db = new CollaborativeDatabase();
                switch (databaseType){
                    case "bibsonomy":
                        db.initializeBibsonomyTags(inputFile.getAbsolutePath());
                        break;
                    case "movielens":
                        db.initializeMovieLensTags(inputFile.getAbsolutePath());
                        break;
                }
                algorithm = new CollaborativeMatching((CollaborativeDatabase) db);
                break;
            case "collab-mi":
                db = new CollaborativeDatabase();
                switch (databaseType){
                    case "bibsonomy":
                        db.initializeBibsonomyTags(inputFile.getAbsolutePath());
                        break;
                    case "movielens":
                        db.initializeMovieLensTags(inputFile.getAbsolutePath());
                        break;
                }
                algorithm = new CollaborativeMutualInformation((CollaborativeDatabase) db);
                break;
            case "dist-matching":
                db = new ProjectionalDatabase();
                switch (databaseType){
                    case "bibsonomy":
                        db.initializeBibsonomyTags(inputFile.getAbsolutePath());
                        break;
                    case "movielens":
                        db.initializeMovieLensTags(inputFile.getAbsolutePath());
                        break;
                }
                algorithm = new DistributionalMatching((ProjectionalDatabase) db);
                break;
            case "dist-mi":
                db = new DistributionalDatabase();
                switch (databaseType){
                    case "bibsonomy":
                        db.initializeBibsonomyTags(inputFile.getAbsolutePath());
                        break;
                    case "movielens":
                        db.initializeMovieLensTags(inputFile.getAbsolutePath());
                        break;
                }
                algorithm = new DistributionalMutualInformation((DistributionalDatabase) db);
                break;
        }

        System.out.println(outputFileDir);
        File temp = null;
        try {
            temp = File.createTempFile("tmp"+outputFileDir,".tmp");

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Going to generate csv now.");
        CSVUtils.generateTagSimilarityCSV(new LinkedList<String>(db.getTagsSet()), algorithm, temp);

        ExternalSort.defaultcomparator = new CSVComparator();
        try {
            ExternalSort.sort(temp,  new File(outputFileDir));
        } catch (IOException e) {
            e.printStackTrace();
        }

        temp.deleteOnExit();
	}
	  
}



