package edu.macalester.tagrelatedness;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
/**
 * The DistributionalDatabase holds a matrix of tags and resources, capturing the frequency in which a tag has been associated to each resource.
 * @author Alan Morales
 */
public class DistributionalDatabase implements Database{

	private HashMap<String, ArrayList<String>> tagsMap;
	private HashMap<String, ArrayList<String>> resourcesMap;
	private int totalEntries = 0;
	
	public DistributionalDatabase(){
		tagsMap = new HashMap<String, ArrayList<String>>();
		resourcesMap = new HashMap<String, ArrayList<String>>();
	}
	/**
	 * Adds a tag to the database.
	 * @param resourceName A string identifying the resource to be tagged.
	 * @param tagName A string identifying the tag.
	 */
	public void addTag(String resourceName, String tagName){
		ArrayList<String> tagsList = resourcesMap.get(resourceName);
		
		if(null == tagsList)
			tagsList = new ArrayList<String>();
		
		tagsList.add(tagName);
		
		resourcesMap.put(resourceName, tagsList);
		
		ArrayList<String> resourcesList = tagsMap.get(tagName);
		
		if(null == resourcesList)
			resourcesList = new ArrayList<String>();
		
		resourcesList.add(resourceName);		
		
		tagsMap.put(tagName, resourcesList);
		
		
		totalEntries++;
		}
	/**
	 * Adds all the tags from a specified movie lens tags.dat file.
	 * @param tagsDataFileDir The directory of the tags.dat file to be added to the database.
	 */
	public void initializeMovieLensTags(String tagsDataFileDir){
		
		FileInputStream fileStream;
		BufferedInputStream bufferedStream;
		BufferedReader readerStream;
		
		try {
			
			fileStream = new FileInputStream(tagsDataFileDir);
			bufferedStream = new BufferedInputStream(fileStream);
			readerStream = new BufferedReader(new InputStreamReader(bufferedStream));
			
			while(readerStream.ready()){
				
				String line = readerStream.readLine();
				String tagInfo[] = line.split("::");
				
				String resource = tagInfo[1];
				String tag = tagInfo[2];
				
				if(tagInfo.length == 4)
					addTag(resource, tag);
			}
			
		} catch (FileNotFoundException e) {
			System.out.println("File not found exception: "+e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Input output exception: "+e.toString());
			e.printStackTrace();
		}
	}
	/**
	 * Adds all the tags from a specified Bibsonomy tas file.
	 * @param dir The directory of the Bibsonomy tas file to be added.
	 */
	public void initializeBibsonomyTags(String dir){
		FileInputStream fileStream;
		BufferedInputStream bufferedStream;
		BufferedReader readerStream;
		
		try {
			fileStream = new FileInputStream(dir);
			bufferedStream = new BufferedInputStream(fileStream);
			readerStream = new BufferedReader(new InputStreamReader(bufferedStream));
			
			while(readerStream.ready()){
				
				String line = readerStream.readLine();	
				
				String tagInfo[] = line.split("\t");
				
				String tag = tagInfo[1];
				String resource = tagInfo[2];
				
				if(tagInfo.length == 5)
					addTag(resource, tag);
			}
			
		} catch (FileNotFoundException e) {
			System.out.println("File not found exception: "+e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Input output exception: "+e.toString());
			e.printStackTrace();
		}
	}
	/**
	 * Returns a set of all the tags that have been added to the database.
	 * @return A Set of all the tags in the database.
	 */
	public Set<String> getTagsSet() {
		return tagsMap.keySet();
	}
	/**
	 * Returns a set of all the resources that have been added to the database.
	 * @return A set of all the resources in the database.
	 */
	public Set<String> getResourcesSet(){
		return resourcesMap.keySet();
	}
	/**
	 * Returns a HashMap with tags as keys and an ArrayList of the resources associated with such tag as values.
	 * @return A HashMap with tags and keys and an ArrayList of the resources of such tag as values.
	 */
	public HashMap<String, ArrayList<String>> getTagsMap(){
		return tagsMap;
	}
	/**
	 * Returns a HashMap with resources as keys and an ArrayList of the tags associated with such resource as values.
	 * @return A HashMap with resources as keys and an ArrayList of the tags of such resource as values.
	 */
	public HashMap<String, ArrayList<String>> getResourcesMap(){
		return resourcesMap;
	}
	/**
	 * Returns the total number of entries in the database.
	 * @return An int with the size of entries in the database.
	 */
	public int getTotalEntries(){
		return totalEntries;
	}

}
