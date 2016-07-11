package gr.aueb.cs.nlp.wordtagger.classifier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import cc.mallet.fst.CRF;
import cc.mallet.fst.CRFTrainerByThreadedLabelLikelihood;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.Target2LabelSequence;
import cc.mallet.pipe.TokenSequence2FeatureVectorSequence;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Sequence;
import cc.mallet.types.Token;
import cc.mallet.types.TokenSequence;
import gr.aueb.cs.nlp.wordtagger.data.structure.Word;

/**
 * 
 * @author Thomas Asikis
 * @license Copyright (c) 2016 Thomas Asikis
 *			Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 			The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 			THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
public class CRFClassifier implements Classifier, Serializable {
	private static final long serialVersionUID = 1L;
	private Pipe pipe; // effectively final
	private int lookBehind;
	private int lookAhead;
	private CRF classifier;
	private int threads;
	
	/**
	 * A CRF classifer constructor.
	 * @param lookAhead, the number of tokens after the current token, to included
	 * in each instance. 
	 * @param lookeBehind, the number of tokens before the current token, to be 
	 * included in each instance.
	 * @param threads, is used in training with batch likelihood.
	 */
	public CRFClassifier(int lookAhead, int lookeBehind, int threads) {
		
		this.lookBehind = lookeBehind;
		this.lookAhead = lookAhead;
		this.threads  = threads;
		generatePipe();		
	}
	
	
	/**
	 * This method generates a mallet pipe for preprocessing of the
	 * words in order to be used as input for the classifier.
	 */
	private void generatePipe() {
		ArrayList<Pipe> pipeList = new ArrayList<>();
		pipeList.add(new TokenSequence2FeatureVectorSequence());
		pipeList.add(new Target2LabelSequence());
		// pipeList.add(new Target2Label());

		this.pipe = new SerialPipes(pipeList);

	}

	/**
	 * This method converts a Word object to token array 
	 * (a token for features and a token for labels) for mallet.
	 * so it can be used from the Mallet API.
	 * @param w , the word that will be converted to token array.
	 * @return, the token[] containg the features in pos 
	 * 0 and the labels in pos 1
	 */
	private Token[] generateTokens(Word w) {
		Token[] featAndLabel = new Token[2];
		Token curr = new Token(w.getValue());
		Token currLabel = new Token(w.getCategory());
		double[] feats = w.getFeatureVec().getValues();
		for (int j = 0; j < feats.length; j++) {
			curr.setFeatureValue("featValue" + j, feats[j]);
		}
		featAndLabel[0] = curr;
		featAndLabel[1] = currLabel;
		return featAndLabel;
	}
	
	
	/**
	 * Generates tokens which split the word sequences
	 * @param w, a dummy word used for sequence split
	 * @return a dummy token to split sequences
	 */
	private Token[] generateDummyTokens(Word w) {
		Token[] featAndLabel = new Token[2];
		Token curr = new Token("null");
		Token currLabel = new Token(w.getCategory());
		double[] feats = w.getFeatureVec().getValues();
		for (int j = 0; j < feats.length; j++) {
			curr.setFeatureValue("featValue" + j, 0.0);
		}
		featAndLabel[0] = curr;
		featAndLabel[1] = currLabel;
		return featAndLabel;
	}
	
	
	/**
	 * Converts a list of Word objects to an instance list for mallet.
	 * @param set, the input list of words
	 * @return the instalist ready to be processed from mallet
	 */
	private InstanceList generateInstance(List<Word> set) {
		InstanceList il =  new InstanceList(pipe);
		for (int i = 0; i < set.size(); i++) {
			TokenSequence words = new TokenSequence();
			TokenSequence labels = new TokenSequence();
			Word w = set.get(i);

			// look behind
			boolean backwardDummy = true;
			for (int j = 1; j <= lookBehind; j++) {
				backwardDummy &= i - j >= 0 && !set.get(i - j).equals("%newarticle%");
				Token[] prevW;
				if (backwardDummy) {
					Word prev = set.get(i - j);
					prevW = generateTokens(prev);

				} else {
					prevW = generateDummyTokens(w);
				}
				words.add(prevW[0]); // add to wordSequence
				labels.add(prevW[1]); // add To Label Sequence
			}

			// look ahead
			Token[] curr = generateTokens(w);
			words.add(curr[0]); // add to wordSequence
			labels.add(curr[1]); // add To Label Sequence

			boolean forwardDummy = true;
			for (int j = 1; j <= lookAhead; j++) {
				forwardDummy &= i + j < set.size() && !set.get(i + j).equals("%newarticle%");
				Token[] nextW;
				if (forwardDummy) {
					Word next = set.get(i + j);
					nextW = generateTokens(next);
				} else {
					nextW = generateDummyTokens(w);
				}
				words.add(nextW[0]); // add to wordSequence
				labels.add(nextW[1]); // add To Label Sequence
			}
			il.addThruPipe(new Instance(words, labels, w.getValue(), null));
		}
		return il;
	}
	
	

	@Override
	public void train(List<Word> trainSet) {
		InstanceList trainS =  generateInstance(trainSet);
		classifier = new CRF(trainS.getDataAlphabet(), trainS.getTargetAlphabet());
		classifier.addFullyConnectedStatesForThreeQuarterLabels(trainS);
		classifier.addStartState();		
		CRFTrainerByThreadedLabelLikelihood trainer = 
				new CRFTrainerByThreadedLabelLikelihood(classifier, threads);
		trainer.setGaussianPriorVariance(10.0);
		trainer.train(trainS);
	    trainer.shutdown();
	}

	@Override
	public String classify(Word input) {
		Logger.getAnonymousLogger().log(Level.SEVERE, "CRF Classifier is not intended to "
				+ "be used for single Words Predictions but rather for sequences."
				+ " Consider using a MaxEnt Instead");
		throw new IllegalStateException("CRF Classifier is not intended to "
				+ "be used for single Words Predictions but rather for sequences."
				+ " Consider using a MaxEnt Instead");
	}

	/*
	 * wil use deprecated for now since I have little time find how to produce
	 * labels from mallet
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public List<Word> classifySet(List<Word> input) {
		List<Word> result = new ArrayList<>();
	
		Sequence<String>[] seqs = 
				classifier.predict(generateInstance(input)); 
		for(int i = 0 ; i < input.size() ; i++ ){
			Word w = input.get(i);
			result.add(new Word(w.getValue(),  seqs[i].get(lookBehind).toString()));
		}
		return result;
	}

	@Override
	public void reset() {
		classifier = null;

	}

}
