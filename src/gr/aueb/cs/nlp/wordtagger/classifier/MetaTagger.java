package gr.aueb.cs.nlp.wordtagger.classifier;

import gr.aueb.cs.nlp.wordtagger.data.structure.Dictionary;
import gr.aueb.cs.nlp.wordtagger.data.structure.Model;
import gr.aueb.cs.nlp.wordtagger.data.structure.Word;
/**
 * 
 * @author Thomas Asikis
 * @license Copyright (c) 2016 Thomas Asikis
 *			Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 			The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 			THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
//TODO use with interface, use builder
public class MetaTagger {
	private final Model model;
	private Dictionary wordDict; 
	private boolean useWordDictionary = true;
	private boolean useSingleCat = true;
	private boolean useNumeric = true;
	private boolean useAdvDer = true;
	
	
	
	public boolean containsWord(Word word){
		return wordDict.getDict().containsKey(word.getValue());
	}
	
	/**
	 * @param model, The model that contains the tagset
	 * @param catDictionary, A dictionary 
	 * @param wordDictionary
	 */
	public MetaTagger(Model model, String wordDictionary) {
		this.model = model;
		wordDict =  wordDictionary != null ? new Dictionary(wordDictionary) : null;
	}
	
	public String wordDictionary(Word word){
		String cat = wordDict.getCategory(word.getValue());
		return  cat != null && useWordDictionary ? cat : word.getCategory();
	}
	
	public boolean singleCategory(String word){
		String singleCategory = model.singleCat(word);
		return singleCategory != null && useSingleCat;
	}
	
	public String isNumeral(String word){
		if (word.length() > 0  && word.substring(0, 1).matches("[0-9]")) {
			// numeral check
			return "numeral/--/--/--/--";
		} 
		return null;
	}
	
	public String pronounAdj(String predLabel, String nextLabel, boolean multicat){
		if(nextLabel == null | !useAdvDer)
			return predLabel;
		if (predLabel.matches("^article.*") || predLabel.matches("^pronoun.*")) {
			// noun-article check, for now worse results
				if (nextLabel.matches("^noun.*") || nextLabel.matches("^adjective.*")) {
					if (multicat) {
						predLabel = predLabel.replace("pronoun/--", "article/definitive");
					} else {
						predLabel = predLabel.replace("pronoun/--", "article/--");
					}
				} else if (nextLabel.matches("^verb/.*")) {
					if (multicat) {
						predLabel = predLabel.replace("article/definitive", "pronoun/--");
					} else {
						predLabel = predLabel.replace("article/--", "pronoun/--");
					}
				}
		}
		return predLabel;
	}

	public Model getModel() {
		return model;
	}

	public boolean isUseSingleCat() {
		return useSingleCat;
	}

	public MetaTagger setUseSingleCat(boolean useSingleCat) {
		this.useSingleCat = useSingleCat;
		return this;
	}

	public boolean isUseNumeric() {
		return useNumeric;
	}

	public MetaTagger setUseNumeric(boolean useNumeric) {
		this.useNumeric = useNumeric;
		return this;
	}

	public boolean isUseAdvDer() {
		return useAdvDer;
	}

	public MetaTagger setUseAdvDer(boolean useAdvDer) {
		this.useAdvDer = useAdvDer;
		return this;
	}

	public boolean isUseWordDictionary() {
		return useWordDictionary;
	}

	public MetaTagger setUseWordDictionary(boolean useWordDictionary) {
		this.useWordDictionary = useWordDictionary;
		return this;
	}

	public Dictionary getWordDict() {
		return wordDict;
	}
	
	
	
	
}
