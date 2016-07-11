package gr.aueb.cs.nlp.wordtagger.data.structure;

import java.io.Serializable;
import java.util.HashMap;
/**
 * 
 * @author Thomas Asikis
 * @license Copyright (c) 2016 Thomas Asikis
 *			Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 			The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 			THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
public class Category implements Serializable {
	private static final long serialVersionUID = 1L;
	private final String name;
	private HashMap<String, Double> wordFrequencies;
	private HashMap<String, Double> suffixFrequencies;

	public Category(String name) {
		this.name = name;
		this.wordFrequencies = new HashMap<>();
		this.suffixFrequencies = new HashMap<>();
	}
	
	/**
	 * Check whether the category contains the given word
	 * @param word
	 * @return true or false
	 */
	public boolean containsWord(String word){
		return wordFrequencies.containsKey(word);
	}
	
	/**
	 * Check whether the category contains the given suffix
	 * @param word
	 * @return true or false
	 */
	public boolean containsSuffix(String suffix){
		return suffixFrequencies.containsKey(suffix);
	}
	
	/**
	 * Adds a word to the category's dictionary, if it already contains the words, 
	 * it increments its frequency
	 * @param word
	 */
	public void addWord(String word){
		if(containsWord(word)){
			wordFrequencies.put(word, wordFrequencies.get(word)+1.0);
		} else {
			wordFrequencies.put(word, 1.0);
		}
	}
	
	/**
	 * Adds a suffix to the category's dictionary, if it already contains the words, 
	 * it increments its frequency
	 * @param word
	 */
	public void addSuffix(String suffix){
		if(containsSuffix(suffix)){
			suffixFrequencies.put(suffix, suffixFrequencies.get(suffix)+1.0);
		} else {
			suffixFrequencies.put(suffix, 1.0);
		}
	}
	
	/**
	 * to get the ferequency of a given word
	 * @param word
	 * @return the double frequency unnormalized -> result > 1
	 */
	public double getWordFrequency(String word){
			return containsWord(word) ? wordFrequencies.get(word) : 0.0;		
	}
	
	/**
	 * to get the ferequency of a given suffix
	 * @param word
	 * @return the double frequency unnormalized -> result > 1
	 */
	public double getSuffixFrequency(String suffix){
		return containsSuffix(suffix) ? suffixFrequencies.get(suffix) : 0.0;		
	}

	public String getName() {
		return name;
	}

	public HashMap<String, Double> getWordFrequencies() {
		return wordFrequencies;
	}

	public HashMap<String, Double> getSuffixFrequencies() {
		return suffixFrequencies;
	}
	
	
}
