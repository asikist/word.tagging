package gr.aueb.cs.nlp.wordtagger.classifier;

import java.util.List;

import gr.aueb.cs.nlp.wordtagger.data.structure.Word;
/**
 * 
 * @author Thomas Asikis
 * @license Copyright (c) 2016 Thomas Asikis
 *			Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 			The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 			THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
public interface Classifier {
	/**
	 * A method which trains a classifier on tagged set of words.
	 * If the Classifier is a sequence classifier, it breaks the sequences on
	 * null categories.
	 * @param trainSet
	 */
	public void train(List<Word> trainSet);
	
	/**
	 * 
	 * @param input, classifies a single word using a trained classifier. 
	 * Throws illegal state exception when used on sequence classifiers
	 * @return
	 */
	public String classify(Word input);
	
	/**
	 * Classifies a set of words. If the classifier is a sequence classifiers it automatically
	 * breaks sequences on categories with null value. 
	 * @param input
	 * @return
	 */
	public List<Word> classifySet(List<Word> input);
	
	/**
	 * Resets the classifier, erasing its hyperparameters, usefull if we want to retrain the 
	 * same classifiier on different portions of the dataset or different datasets.
	 */
	public void reset();
}
