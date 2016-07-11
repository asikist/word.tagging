package gr.aueb.cs.nlp.wordtagger.data.structure.features;

import java.io.Serializable;
/**
 * @description A pair of double valued vectors for the features and labels of a word
 * @author Thomas Asikis
 * @license Copyright (c) 2016 Thomas Asikis
 *			Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 			The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 			THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
public class FeatureVector implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double[] values;
	private double[] labels;

	public FeatureVector(double[] values) {		
		this.values = values;
	}

	public FeatureVector(double[] values, double[] labels) {		
		this.values = values;
		this.labels = labels;
	}
	
	public double[] getValues() {
		return values;
	}

	public void setValues(double[] values) {
		this.values = values;
	}
	
	public double[] getLabels() {
		return labels;
	}

	public void setLabels(double[] labels) {
		this.labels = labels;
	}

	public String toString(){
		StringBuilder result = new StringBuilder();
		for(int i = 0 ; i < values.length ; i++){
			if( i == 0){
				result.append(values[i]);
			} else {
				result.append("  ").append(values[i]);
			} 
		}
	return result.toString();
	}
	
}
