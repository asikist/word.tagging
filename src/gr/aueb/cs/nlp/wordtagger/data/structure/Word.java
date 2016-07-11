package gr.aueb.cs.nlp.wordtagger.data.structure;

import java.io.Serializable;

import org.apache.commons.lang3.ArrayUtils;

import gr.aueb.cs.nlp.wordtagger.data.structure.features.FeatureVector;
/**
 * 
 * @author Thomas Asikis
 * @license Copyright (c) 2016 Thomas Asikis
 *			Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 			The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 			THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
public class Word implements Serializable {
	private static final long serialVersionUID = 1L;
	final String value;
	private FeatureVector featureVec;
	private String category;
	
	/**
	 * Create a new word based on its string value
	 * @param value
	 */
	public Word(String value) {
		this.value = value;
	}

	/**
	 * Create 
	 * @param value
	 * @param category
	 */
	public Word(String value, String category) {
		this.value = value;
		this.category =category;
	}
	
	/**
	 * Create a word with a given Feature Vec Object
	 * @param value
	 * @param category
	 * @param featureVec
	 */
	public Word(String value, String category, FeatureVector featureVec) {
		this.value = value;
		this.category =category;
		this.featureVec = featureVec;
	}
	
	
	/* Getters and Setters */
	public String getValue() {
		return value;
	}

	public FeatureVector getFeatureVec() {
		return featureVec;
	}

	public void setFeatureVec(FeatureVector featureVec) {
		if(this.featureVec == null || featureVec.getValues().length == 0)
			this.featureVec = featureVec;
		else 
			this.featureVec.setValues(ArrayUtils.addAll(this.featureVec.getValues(), featureVec.getValues()));
	}

	public String getCategory() {
		return category;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
	
	public String toString(){
		return new StringBuilder(value).append(" ").append(featureVec.toString()).toString();
	}
	
	

}
