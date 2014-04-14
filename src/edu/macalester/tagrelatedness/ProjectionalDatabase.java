package edu.macalester.tagrelatedness;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * The projectional database stores whether each tag has been associated with a resource, without keeping any information of how many users may have put that tag on a certain resource.
 * For more reference on the construction of this algorithm look at: www2009.org/proceedings/pdf/p641.pdf
 * @author alan
 *
 */
public class ProjectionalDatabase implements Database{

	protected HashMap<String, HashSet<String>> tagsMap;
	protected HashMap<String, HashSet<String>> resourcesMap;
	private int totalEntries;
	
	public ProjectionalDatabase(){
		tagsMap = new HashMap<String, HashSet<String>>();
		resourcesMap = new HashMap<String, HashSet<String>>();
		totalEntries = 0;
	}
	/**
	 * Adds a tag to the projectional database.
	 * @param resourceName The identifier of the resource to be added.
	 * @param tagName The name of the tag to be added.
	 */
	public void addTag(String resourceName, String tagName){
		
		HashSet<String> tagsSet = resourcesMap.get(resourceName);
		
		if(null == tagsSet)
			tagsSet = new HashSet<String>();
		
		if(!tagsSet.contains(tagName))
			totalEntries++; // if this is a new tag added, increment the number of total entries
		
		tagsSet.add(tagName);
		resourcesMap.put(resourceName, tagsSet);
		
		HashSet<String> resourcesSet = tagsMap.get(tagName);
		
		if(null == resourcesSet)
			resourcesSet = new HashSet<String>();
		
		resourcesSet.add(resourceName);	
		tagsMap.put(tagName, resourcesSet);
		
	}
	/**
	 * Get a set containing all the tags added to the projectional database.
	 * @returns a set with all the tags in the database
	 */
	public Set<String> getTagsSet(){
		return tagsMap.keySet();
	}
	/**
	 * Gets a set containing all the resources added to the projectional database.
	 * @return a set with all the resources that have been added to the database
	 */
	public Set<String> getResourcesSet(){
		return resourcesMap.keySet();
	}
	/**
	 * Returns a HashMap with tags as keys and a HashSet of the resources associated with such tag as values.
	 * @return A HashMap with tags and keys and a HashSet of the resources of such tag as values.
	 */
	public HashMap<String, HashSet<String>> getTagsMap(){
		return tagsMap;
	}
	/**
	 * Returns a HashMap with resources as keys and a HashSet of the tags associated with such resource as values.
	 * @return A HashMap with resources as keys and an HashSet of the tags of such resource as values.
	 */
	public HashMap<String, HashSet<String>> getResourcesMap(){
		return resourcesMap;
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
	 * Returns the total number of entries in the database.
	 * @return An int with the size of entries in the database.
	 */
	public int getTotalEntries() {
		return totalEntries;
	}


}
