package edu.macalester.tagrelatedness;

import java.util.Comparator;

public class CSVComparator implements Comparator<String>{

	public int compare(String r1, String r2) {
		String[] data1 = r1.replace("\"", "").split(",");
		String[] data2 = r2.replace("\"", "").split(",");
		
		double result = Double.parseDouble(data1[1]) - Double.parseDouble(data2[1]);
		
		if(result < 0)
			return -1;
		else if(result > 0)
			return 1;
		else
			return 0;
	}


}
