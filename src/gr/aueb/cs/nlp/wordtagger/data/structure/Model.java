package gr.aueb.cs.nlp.wordtagger.data.structure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import gr.aueb.cs.nlp.wordtagger.util.CallBack;
import gr.aueb.cs.nlp.wordtagger.util.FileHelper;
import gr.aueb.cs.nlp.wordtagger.util.MathHelper;
import gr.aueb.cs.nlp.wordtagger.util.StringHelpers;

/**
 * 
 * @author Thomas Asikis
 * @license Copyright (c) 2016 Thomas Asikis
 *			Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 			The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 			THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
public class Model implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * A map containing category objects and their names as identifiers for
	 * fast retrieval.
	 */
	private LinkedHashMap<String, Category> categories = new LinkedHashMap<>();
	private List<String> catsAsList = new ArrayList<>();
	/**
	 * The maximum suffix length of the model.
	 */
	private final int suffixLength;
	/**
	 * How many suffices and words were found in the trainset (not distinct)
	 */
	private int totalWords;
	private int totalSuffices;
	
	/**
	 * The training set of words, which we use to build the model.
	 */
	private LinkedList<Word> trainSet;

	
	/**
	 * model contructor
	 * @param the path of the text file containing the categories of the model
	 * @param suffixLength the maximum suffix length that the model will use
	 */
	public Model(String path, int suffixLength) {
		this.suffixLength = suffixLength;
		this.totalSuffices = 0;
		this.totalWords = 0;
		this.trainSet = new LinkedList<>();
		FileHelper.readFile(path, new CallBack<String>() {
			@Override
			public void call(String s) {
				if (!categories.containsKey(s))
					categories.put(s, new Category(s));
					catsAsList.add(s);
			}
		});
	}

	/**
	 * Reads a file containing a pair of word and categories in each line,
	 * split with a space " ". The word is placed on the left side of the split[0]
	 * and the category is placed on the right. The categories must be the same as the ones
	 * provided in the configuration file.
	 * @param path the path where the file containing the words is located in the disk
	 */
	public void generateStats(String path) {
		generateStats(path, Long.MAX_VALUE);
	}
	
	/**
	 * for multiple files
	 * @param path
	 */
	public void generateStats(String ... paths) {
		for(String path : paths){
			generateStats(path, Long.MAX_VALUE);			
		}
	}
	
	/**
	 * for multiple files
	 * @param path
	 */
	public void generateStats(Map<String, Integer> paths) {
		paths.forEach((path,limit)->{
			generateStats(path, limit);			
		});
	}


	/**
	 * Reads a file containing a pair of word and categories in each line,
	 * split with a space " ". The word is placed on the left side of the split[0]
	 * and the category is placed on the right. The categories must be the same as the ones
	 * provided in the configuration file.
	 * @param path the path where the file containing the words is located in the disk
	 * @param limit in case we want to read a portion of the words in the file
	 */
	public void generateStats(String path, long limit) {
		final long[] j = { 0 };
		FileHelper.readFile(path, new CallBack<String>() {
			@Override
			public void call(String s) {
				if (j[0] < limit) {
					String[] st = s.split(" ");
					try{
					categories.get(st[1]).addWord(st[0]);
					} catch(NullPointerException e){
						System.out.println("       ");
						System.out.println(st[1]);
					}
					trainSet.add(new Word(st[0], st[1]));
					totalWords++;
					int loops = suffixLength < st[0].length() ? suffixLength : st[0].length();
					for (int i = 0; i < loops; i++) {
						String suffix = StringHelpers.getSuffix(st[0], i + 1);
						try{
						categories.get(st[1]).addSuffix(suffix);
						} catch (NullPointerException e){
							System.out.println(st[1]);
						}
						totalSuffices++;
					}
				} else if(j[0]%1000 == 1) {
					//TODO Logger
				}
				j[0]++;
			}
		});

	}

	/**
	 * Checks if the given word belongs only to a single category.
	 * @param word, the String representation of a word
	 * @return the single category that a word belongs, otherwise null.
	 */
	public String singleCat(String word) {
		int cats = 0;
		String current = null;
		for (Entry<String, Category> e : categories.entrySet()) {
			if (e.getValue().getWordFrequency(word) > 0) {
				cats++;
				current = e.getValue().getName();
			}
		}
		return cats == 1 ? current : null;
	}

	/**
	 * in order to create statistical ambitag features for a word.
	 * @param word, the input word
	 * @return, a double array which every element is the ambitag feature
	 */
	public double[] ambiTagFeaturesWord(String word) {
		double[] feats = new double[categories.size()];
		int j = 0;
		double total = 0;
		for (Entry<String, Category> e : categories.entrySet()) {
			feats[j] = e.getValue().getWordFrequency(word);
			total += (double) e.getValue().getWordFrequency(word);
			j++;
		}
		total = total == 0 ? 1 : total;
		feats = MathHelper.arrayDivision(feats, total);
		return feats;
	}

	/**
	 * in order to create statistical ambitag features for a word's posible suffices up to a the model's suffix length.
	 * @param word, the input word
	 * @return, a double array which every element is the ambitag feature
	 */
	public double[] ambiTagFeaturesSuffix(String suffix) {
		double[] feats = new double[categories.size()];
		double total = 0;
		int j = 0;
		for (Entry<String, Category> e : categories.entrySet()) {
			feats[j] = (double) e.getValue().getSuffixFrequency(suffix);
			total += (double) e.getValue().getSuffixFrequency(suffix);
			j++;
		}
		// to avoid NaN in the feat vector, it will simply be replaced with 0s
		total = total == 0 ? 1 : total;
		feats = MathHelper.arrayDivision(feats, total);
		return feats;
	}

	/**
	 * a simple print of the model in the console
	 */
	public void print() {
		for (Entry<String, Category> e : categories.entrySet()) {
			System.out.println(e.getKey() + " " + e.getValue());
		}
	}
	
	/**
	 * get category index of a word, usefull when wanting to check against classification vectors that 
	 * @param w
	 * @return
	 */
	public int getCategoryAsNumber(Word w){
		return catsAsList.indexOf(w.getCategory());
	}
	
	/**
	 * Given the category's name get it's correspoding index
	 * @param categoryName
	 * @return
	 */
	public int getCategoryAsNumber(String categoryName){
		return catsAsList.indexOf(categoryName);
	}
	
	/**
	 * get the oneHot vector of a word's category
	 * @param word
	 * @return the integer oneHot vector
	 */
	public int[] getCategoryAsOneOfAK(Word word){
		return MathHelper.oneOfAK(getCategoryAsNumber(word), catsAsList.size());
	}
	
	/**
	 * get the oneHot vector of a word's category
	 * @param word
	 * @return the double oneHot vector
	 */
	public double[] getCategoryAsOneOfAKDouble(Word w){
		return MathHelper.oneOfAKDouble(getCategoryAsNumber(w), catsAsList.size());
	}
	
	/**
	 * get the oneHot vector of a category
	 * @param word
	 * @return the double oneHot vector
	 */
	public double[] getCategoryAsOneOfAKDouble(String categoryName){
		return MathHelper.oneOfAKDouble(getCategoryAsNumber(categoryName), catsAsList.size());
	}
	
	/**
	 * get the oneHot vector of a category
	 * @param word
	 * @return the double oneHot vector
	 */
	public String getCategogoryFromOneOfAK(int[] input){
		for(int i = 0 ; i < input.length ; i++){
			if(input[i] == 1)
				return catsAsList.get(i);
		}
		return catsAsList.get(catsAsList.size() - 1);
	}
	/**
	 * get the category name of the oneHot Vector
	 * @param word
	 * @return the category name
	 */
	public String getCategogoryFromOneOfAK(double[] input){
		return catsAsList.get(Arrays.asList(input).indexOf(1.0));
	}
	
	/**
	 * put a new category in the model
	 * @param categoryName
	 */
	public void put(String categoryName) {
		categories.put(categoryName, new Category(categoryName));
	}

	public Map<String, Category> getCategories() {
		return categories;
	}

	public LinkedList<Word> getTrainSet() {
		return trainSet;
	}

	public int getSuffixLength() {
		return suffixLength;
	}

	public int getTotalWords() {
		return totalWords;
	}

	public int getTotalSuffices() {
		return totalSuffices;
	}


	
	

}
