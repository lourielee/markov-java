package hmmLyricGen;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrintToFile {
	
	
	
	
	public static void printHash(HashMap<String, List<String>> map, String description) throws IOException{
		
		String outfile = description + ".txt";
		PrintWriter writer = new PrintWriter(outfile, "UTF-8");
		
		for(Map.Entry<String, List<String>> entry : map.entrySet()) {
			String key = entry.getKey().toString();
			List<String> val = map.get(key);
			writer.print(key + ": \t");
			for(String strVal:val) {
				writer.print(strVal + "\t");
			}
			writer.println("\n");
		}
		
		writer.close();
		
		return;
	}
}
