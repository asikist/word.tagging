package gr.aueb.cs.nlp.main;

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
/**
 * 
 * @author Thomas Asikis
 * @license Copyright (c) 2016 Thomas Asikis
 *			Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 			The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 			THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
public class ExtractCategories {
	//self explanatory, extracts categories from conll file of tagged words
	public static void main(String[] args) throws IOException {
		File f = new File("data\\redCats.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f),"UTF-8"));
		String line = "";
		Pattern p = Pattern.compile("(((\\w+|--)/)+(\\w+|--))");
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
	 			new BufferedWriter(new OutputStreamWriter(new FileOutputStream("data/categories-clean-all.txt"),"UTF8"));
	 	for(String cat1 : cats){
	 		wr.write(cat1+"\n");
	 	}
	 	wr.close();
	}
	
	

}
