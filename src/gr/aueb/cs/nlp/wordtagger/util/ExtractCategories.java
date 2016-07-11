package gr.aueb.cs.nlp.wordtagger.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtractCategories {
	//path to a file containing the appropriate categories, even in COnll woth words
	static String categoriesFile = "data\\redCats.txt";
	//the patter for a category
	static String pattern = "(((\\w+|--)/)+(\\w+|--))";
	//where to write
	static String whereTo = "data/categories-clean-red.txt" ;
	public static void main(String[] args) throws IOException {
		File f = new File(categoriesFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f),"UTF-8"));
		String line = "";
		Pattern p = Pattern.compile(pattern);
		ArrayList<String> cats = new ArrayList<>();
		System.out.println("  "+p.toString());
		while((line = br.readLine())!=null){
			Matcher m = p.matcher(line);
			m.find();
			System.out.println(line);
			String group = m.group();
			
			System.out.println(group);
				cats.add(group);				
		}
		br.close();
	 	BufferedWriter wr =
	 			new BufferedWriter(new OutputStreamWriter(new FileOutputStream(whereTo),"UTF8"));
	 	for(String cat1 : cats){
	 		wr.write(cat1+"\n");
	 	}
	 	wr.close();
	}
	
	

}
