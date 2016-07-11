package gr.aueb.cs.nlp.wordtagger.data.structure;

import java.util.HashMap;

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
public class Dictionary {
	private HashMap<String, String> dict;
	/**
	 * Default constructor, which uses a single space as a delimeter
	 * @param path, the location of the file
	 */
	public Dictionary(String path){
		this(path, " ");
	}
	
	/**
	 * Constructor for custom delimeter.
	 * @param path, the location of the delimeter
	 * @param delimeter, the custom delimeter
	 */
	public Dictionary(String path, String delimeter){
		dict = new HashMap<>();
			fromFile(path, delimeter);
	}
	
	/**
	 * Reads the file and splits it in columns. the first column is the 
	 * word and the second column is the corresponding category.
	 * @param path, the location of the file
	 * @param delimeter, the delimeter string
	 */
	private void fromFile(String path, String delimeter){
		FileHelper.readFile(path, new CallBack<String>() {
			@Override
			public void call(String t) {
				String[] split = t.split(delimeter);
				dict.put(split[0], split[1]);
			}
		});
	}
	
	/**
	 * Returns the category of a given word if it exists in the dictionary.
	 * Else it returns null;
	 * @param word
	 * @return
	 */
	public String getCategory(String word){
		return dict.get(word);		
	}

	public HashMap<String, String> getDict() {
		return dict;
	}
	
	
}
