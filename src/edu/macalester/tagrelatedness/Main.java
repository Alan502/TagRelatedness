package edu.macalester.tagrelatedness;

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
                                        .withDescription("Output CSV with tag similarities.")
                                        .hasArg()
                                        .withArgName("FILE")
                                        .withType(File.class)
                                        .create("o"));
        options.addOption(OptionBuilder.withLongOpt("input-file")
                                        .withDescription("Input database with tags.")
                                        .hasArg()
                                        .withType(File.class)
                                        .withArgName("FILE")
                                        .create("i"));

        HelpFormatter formatter = new HelpFormatter();
        
        File inputFile = null;
        TagSimilarityMeasure algorithm = null;
        String outputFileDir = null;
        String algorithmType = "";
        
        if(args.length < 1){
        	printHelp(formatter, options);
        }else{
        	algorithmType = args[0];
        }
        
        // Detect input and output file
        	
        try {
            CommandLine line = parser.parse(options, args);

            if(line.hasOption("i")){
                inputFile = new File(line.getOptionValue("i"));
            }else if(line.hasOption("input-file")){
                inputFile = new File(line.getOptionValue("input-file"));
            }else{
                System.out.println("ERROR: An input file needs to be specified.");
                printHelp(formatter, options);
            }
            
            if(line.hasOption("o")){
            	outputFileDir = line.getOptionValue("o");
            }else if(line.hasOption("output-file")){
            	outputFileDir = line.getOptionValue("output-file");
            }else{
            	System.out.println("ERROR: An output file needs to be specified.");
            	printHelp(formatter, options);
            }

        }catch (ParseException exp){
            System.out.println("Exception: "+exp.toString());
            System.exit(1);
        }
        
        // Detect the database

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
            System.out.println("INFO: Detected database: movielens "+inputFile.getName());
            databaseType = "movielens";
        }else if(firstLine.split("\t").length == 5){
            System.out.println("INFO: Detected database: bibsonomy "+inputFile.getName());
            databaseType = "bibsonomy";
        }else{
            System.out.println("ERROR: Database from inputFile "+inputFile.getName()+" does not seem to be one of available supported Databases. "+supportedDatabases);
            printHelp(formatter, options);
        }
        
        Database db = null;
        
        // Detect the algorithm
        switch (algorithmType){
            default:
            	System.out.println("ERROR: No algorithm specified.");
            	printHelp(formatter, options);
            	break;
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

        System.out.println("INFO: Generating CSV.");
        CSVUtils.generateTagSimilarityCSV(new LinkedList<String>(db.getTagsSet()), algorithm, temp);

        try {
			ExternalSort.mergeSortedFiles(ExternalSort.sortInBatch(temp, new CSVComparator()),
					new File(outputFileDir));
		} catch (IOException e) {
            e.printStackTrace();
		}

        temp.deleteOnExit();
	}
	
	public static void printHelp(HelpFormatter formatter, Options options){
		System.out.println("Tagrelatedness Help: ");
		System.out.println("java edu.macalester.tagrelatedness.Main <ALGORITHM> <OPTIONS>");
		System.out.println("ALGORITHM		one of collab-matching, collab-mi, dist-matching or dist-mi");
		System.out.println("OPTIONS			see below");
		formatter.printHelp( "tag-relatedness", options );
		System.exit(1);
	}
	  
}



