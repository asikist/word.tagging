package gr.aueb.cs.nlp.main;

import java.util.HashMap;
import java.util.Map.Entry;

import gr.aueb.cs.nlp.wordtagger.util.CallBack;
import gr.aueb.cs.nlp.wordtagger.util.FileHelper;
/**
 * 
 * @author Thomas Asikis
 * @license Copyright (c) 2016 Thomas Asikis
 *			Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 			The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 			THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
public class SelectDistinctCategories {
	//Selects the distincts categories of a file and then writes them and their stats to corresponding files
	public static void main(String[] args) {
		HashMap<String, Integer> occurencesDict = new HashMap<>();
		FileHelper.readFile("config/categories-reduced.txt", new CallBack<String>() {
			
			@Override
			public void call(String t) {
				System.out.println("  "+t);
				if (occurencesDict.containsKey(t)){
					occurencesDict.put(t, occurencesDict.get(t)+1);
				} else {
					occurencesDict.put(t,1);
				}
				
			}
		});
		
		String toWrite = "";
		String toWrite2 = "";
		for(Entry<String, Integer> e : occurencesDict.entrySet()){
			toWrite += e.getKey() +"\n";
			toWrite2 += e.getKey() +" "+e.getValue()+"\n";
		}
		FileHelper.toFile("config/ReducedCats.txt", toWrite);
		FileHelper.toFile("config/ReducedCatsStats.txt", toWrite2);
	}
}
