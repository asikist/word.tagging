package gr.aueb.cs.nlp.wordtagger.classifier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.ArrayUtils;

import gr.aueb.cs.nlp.wordtagger.data.structure.Model;
import gr.aueb.cs.nlp.wordtagger.data.structure.Word;
import gr.aueb.cs.nlp.wordtagger.data.structure.features.FeatureVector;
/**
 * 
 * @author Thomas Asikis
 * @license Copyright (c) 2016 Thomas Asikis
 *			Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 			The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 			THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
public class MetaClassifier implements Classifier, Serializable{
	
	/**
	 * A meta classifier, which uses the output of other classifiers as input
	 */
	private static final long serialVersionUID = 1L;
	private List<Classifier> classifiers;
	private Classifier metaClassifier;
	private transient Logger log = Logger.getAnonymousLogger();
	private transient Level lvl = Level.INFO;
	private boolean trainClassifiers;
	final private Model model;
	final int totalCategories;
	
	/**
	 * 
	 * @param classifiers, a list of classifiers used as input
	 * @param metaClassifier, 
	 * @param trainClassifiers
	 * @param model
	 */
	public MetaClassifier(List<Classifier> classifiers, Classifier metaClassifier, boolean trainClassifiers, Model model) {
		super();
		this.classifiers = classifiers;
		this.metaClassifier = metaClassifier;
		this.trainClassifiers = trainClassifiers;
		this.model = model;
		this.totalCategories =model.getCategories().size();	
		}
	
	
	/**
	 * creates a feature vector for each output of the input classfiers
	 * @param wordSet
	 * @return
	 */
	private List<Word> tagsToFeats(List<Word> wordSet){
		List<Word> words = new ArrayList<>();
		wordSet.forEach(w->{
			Word a = new Word(w.getValue(), w.getCategory());
			
			a.setFeatureVec(new FeatureVector(new double[0], model.getCategoryAsOneOfAKDouble(w)));
			words.add(a);			
		});
		classifiers.forEach(v->{
			List<Word> result = v.classifySet(wordSet);
			for (int i = 0 ; i < result.size() ; i++) {
				words.get(i).getFeatureVec().setValues(ArrayUtils.addAll(words.get(i).getFeatureVec().getValues(), model.getCategoryAsOneOfAKDouble(result.get(i))));				
			}
		});
		return words;
		
	}

	/**
	 * returns the Word with the proper feature vector created fromt he output of input classifiers
	 * @param inputWord
	 * @return
	 */
	private Word tagsToFeats(Word inputWord){
		double[][] feats = new double[1][0];
		AtomicInteger i = new AtomicInteger(0);
		classifiers.forEach(v->{
			feats[0] = ArrayUtils.addAll(feats[0], model.getCategoryAsOneOfAKDouble(v.classify(inputWord)));
			i.getAndAdd(totalCategories);
		});
		return new Word(inputWord.getValue(),inputWord.getCategory(),
				new FeatureVector(feats[0], 
						model.getCategoryAsOneOfAKDouble(inputWord)));
	} 
	
	@Override
	public void train(List<Word> trainSet) {
		log.log(lvl, "Starting Logging of multiple classifiers at once!");
		if(trainClassifiers)
			classifiers.forEach(c->{c.train(trainSet);});
		metaClassifier.train(tagsToFeats(trainSet));		
	}
	
	

	@Override
	public String classify(Word input) {
		return metaClassifier.classify(tagsToFeats(input));
	}

	@Override
	public List<Word> classifySet(List<Word> input) {
		return metaClassifier.classifySet(tagsToFeats(input));
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	
	
}
