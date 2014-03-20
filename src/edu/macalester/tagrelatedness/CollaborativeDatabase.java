package edu.macalester.tagrelatedness;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
/**
 * CollaborativeDatabase class is a database where each user has its own resource/tag matrix independent from the other users.
 * The CollaborativeMatching and CollaborativeMutualInformation tag relatedness measures use this database.
 * @author alan
 */
public class CollaborativeDatabase implements Database{
	private HashMap<String, ArrayList<HashMap<String, HashSet<String>>> > userMap;
	private HashSet<String> allTags;
	// Each key in the map above is a user tag, and each user is asociated with two hashmaps:
	// at index 0 one that maps every resource to a set of tags that the resource is associated with
	// at index 1 that maps every tag to a set of resources that the tag is associated with
		
	public CollaborativeDatabase(){
		userMap = new HashMap<String, ArrayList<HashMap<String,HashSet<String>>>>();
		allTags = new HashSet<String>();
	}
	/**
	 * Adds a tag to the database.
	 * @param user A string that identifies the user that added the tag
	 * @param resourceName A string that identifies the resource tagged
	 * @param tagName The tag
	 */
	public void addTag(String user, String resourceName, String tagName) {
		
		ArrayList<HashMap<String, HashSet<String>>> mapList = userMap.get(user);
		
		if(null == mapList){
			mapList = new ArrayList<HashMap<String, HashSet<String>>>();
			mapList.add(0, new HashMap<String, HashSet<String>>()); // resourcesMap
			mapList.add(1, new HashMap<String, HashSet<String>>()); // tagsMap
		}
		
		HashSet<String> tagsSet = mapList.get(0).get(resourceName);
		
		if(null == tagsSet)
			tagsSet = new HashSet<String>();
		
		tagsSet.add(tagName);
		mapList.get(0).put(resourceName, tagsSet);
		
		HashSet<String> resourcesSet = mapList.get(1).get(tagName);
		
		if(null == resourcesSet)
			resourcesSet = new HashSet<String>();
		
		resourcesSet.add(resourceName);
		mapList.get(1).put(tagName, resourcesSet);
		
		userMap.put(user, mapList);
		allTags.add(tagName);
				
	}
	
	/**
	 * Adds all the tags in the specified movieLens tags.dat file.
	 * @param dir The directory of the movieLens tags.dat file
	 */
	public void initializeMovieLensTags(String dir) {
		FileInputStream fileStream;
		BufferedInputStream bufferedStream;
		BufferedReader readerStream;
		
		try {
			
			fileStream = new FileInputStream(dir);
			bufferedStream = new BufferedInputStream(fileStream);
			readerStream = new BufferedReader(new InputStreamReader(bufferedStream));
			
			while(readerStream.ready()){
				
				String line = readerStream.readLine();	
				
				String tagInfo[] = line.split("::");
				
				String user = tagInfo[0];
				String resource = tagInfo[1];
				String tag = tagInfo[2];
				
				if(tagInfo.length == 4)
					addTag(user, resource, tag);
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
	 * Adds all the tags in the specified Bibsonomy tas file.
	 * @param dir The directory of the Bibsonomy tas file.
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
				
				String user = tagInfo[0];
				String tag = tagInfo[1];
				String resource = tagInfo[2];
				
				if(tagInfo.length == 5)
					addTag(user, resource, tag);
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
	 * Returns a Hashmap that contains the userIDs as keys and an arrayList that holds:
	 * <ol>
	 * <li> A Hashmap that maps every resource to a set of tags that the resource is associated with</li>
	 * <li> A Hashmap that maps every tag to a set of resources that the tag is associated with</li>
	 * </ol>
	 * @return A hashmap (see above)
	 */
	public HashMap<String, ArrayList<HashMap<String, HashSet<String>>>> getUserMap(){
		return userMap;
	}
	/**
	 * Returns a Hashset that contains all the tags that have been added to the database
	 * @return A Hashset containing all the tags added to the database
	 */
	public HashSet<String> getTagsSet() {
		return allTags;
	}




}
