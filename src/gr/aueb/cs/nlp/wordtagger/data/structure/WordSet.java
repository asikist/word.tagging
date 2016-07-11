package gr.aueb.cs.nlp.wordtagger.data.structure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import edu.stanford.nlp.ling.RVFDatum;
import edu.stanford.nlp.stats.ClassicCounter;
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
public class WordSet {
	private LinkedList<Word> words;
	
	public WordSet(LinkedList<Word> words) {
		this.words = words;
	}

	public void addWord(Word word) {
		words.add(word);
	}
	
	/**
	 * The wordset transformed to a list compatible with stanfrod classifier of standford core
	 * @return alist of real valued datums
	 */
	public List<RVFDatum<String, String>> toStanfordSet() {
		List<RVFDatum<String, String>> trainignData = new ArrayList<>();
		for (Word w : words) {			
			trainignData.add(word2Datum(w));
		}
		System.out.println("Converted List to classifier trainset");
		return trainignData;
	}
	
	
	/**
	 * Converts any List with words to a Stanford set;
	 * @param words
	 * @return, a list of real valued datums
	 */
	public static List<RVFDatum<String, String>> toStanfordSet(List<Word> words) {
		List<RVFDatum<String, String>> trainignData = new ArrayList<>();
		for (Word w : words) {
			List<Double> feats = Arrays.asList(ArrayUtils.toObject(w.getFeatureVec().getValues()));
			ClassicCounter<String> cc = new ClassicCounter<>();
			for(int i = 0 ; i < feats.size() ; i++){
				cc.incrementCount("feature" + i, feats.get(i));
			}
			if(w.getCategory()!=null){
				RVFDatum<String, String> dtm = new RVFDatum<>(cc, w.getCategory());
				trainignData.add(dtm);
			}
		}
		System.out.println("Converted List to classifier trainset");
		return trainignData;
	}
	
	/**
	 * convers a word to a stanforf real valued atum
	 * @param w
	 * @return
	 */
	public static RVFDatum<String, String> word2Datum(Word w){
		List<Double> feats = Arrays.asList(ArrayUtils.toObject(w.getFeatureVec().getValues()));
		ClassicCounter<String> cc = new ClassicCounter<>();
		for(int i = 0 ; i < feats.size() ; i++){
			cc.incrementCount("feature" + i, feats.get(i));
		}
		return new RVFDatum<>(cc, w.getCategory());
	}
	
	/**
	 * loads a wordset from a file
	 * @param path
	 * @param delimeter
	 * @return
	 */
	public static List<Word> fileToList(String path, String delimeter){
		final LinkedList<Word> testSet = new LinkedList<>();
		FileHelper.readFile(path, new CallBack<String>() {
			@Override
			public void call(String t) {
				String[] a = t.split(delimeter);
				testSet.add(new Word(a[0], a[1]));
			}
		});
		return testSet;
	}

	/**
	 * saves a wordset to a file using the CONLL format (word category)
	 * @param path
	 * @param wordSet
	 */
	public static void toFile(String path, List<Word> wordSet){
		StringBuilder strb = new StringBuilder(); 
		int i = 0;
		for(Word w : wordSet){
			strb.append(w.getValue()).append(" ").append(w.getCategory()).append("\n");
			i++;
			//batching to 10000 words
			if(i%10000 == 0 || i == wordSet.size() - 1){
				FileHelper.toFile(path, strb.toString());
				strb = new StringBuilder();
			}			
		}
	}
	
	public LinkedList<Word> getWords() {
		return words;
	}
	
	

}
