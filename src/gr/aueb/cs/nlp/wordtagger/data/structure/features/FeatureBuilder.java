package gr.aueb.cs.nlp.wordtagger.data.structure.features;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import gr.aueb.cs.nlp.wordtagger.data.structure.Model;
import gr.aueb.cs.nlp.wordtagger.data.structure.Word;
import gr.aueb.cs.nlp.wordtagger.data.structure.embeddings.Embeddings;
import gr.aueb.cs.nlp.wordtagger.util.MathHelper;
import gr.aueb.cs.nlp.wordtagger.util.StringHelpers;
/**
 * 
 *	@desription A class that is responsible for building features either 
 *  from embeddings or statistical and morphological characteristics of words 
 * @author Thomas Asikis
 * @license Copyright (c) 2016 Thomas Asikis
 *			Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 			The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 			THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
public class FeatureBuilder {
	//TODO badly written revisit code with some overaloads and some buffers
	private final Model generator;
	private int suffixLength;
	private String[] featHeader;

	public FeatureBuilder(Model generator) {
		super();
		this.generator = generator;
		suffixLength = generator.getSuffixLength();
	}

	/**
	 * A custom constructor if we want to use a suffix length which is smaller
	 * than the model's. If the suffix length that we provide is bigger, then we
	 * simply set the model's suffix length as the default
	 * 
	 * @param generator
	 * @param suffixLength
	 */

	public FeatureBuilder(Model generator, int suffixLength) {
		super();
		int modelSuffixLength = generator.getSuffixLength();
		this.generator = generator;
		this.suffixLength = suffixLength <= modelSuffixLength ? suffixLength : modelSuffixLength;
	}

	public void generateFeats(List<Word> set) {
		generateFeats(set, 0, 1);
	}

	/**
	 * Based on a model generate features for each word. This method generates only ambitag features.
	 * @param set, the input wordset, the operation is inplace meaning that it will erase any features 
	 * the words of the set already have
	 * @param lookBehind, the previous words that should be taken into account for word generation,
	 * any woord with value %newarticle% is ignored and used in order to stop feature generation 
	 * and force a zero padding
	 * @param lookAhead, words ahead of the word to be taken into account, works same as lookbehind
	 */
	public void generateFeats(List<Word> set, int lookBehind, int lookAhead) {
		if (featHeader == null) {

		}
		for (int i = 0; i < set.size(); i++) {
			Word w = set.get(i);
			double[] feats = ambiFeats(w, suffixLength);
			double[] featsBack = new double[0];
			boolean backwardDummy = true;
			for (int j = 1; j <= lookBehind; j++) {
				backwardDummy &= i - j >= 0 && !set.get(i - j).equals("%newarticle%");
				if (backwardDummy) {
					Word prev = set.get(i - j);
					featsBack = ArrayUtils.addAll(featsBack, ambiFeats(prev, suffixLength));
				} else {
					double[] dummy = new double[generator.getCategories().size() * (suffixLength + 1)];
					Arrays.fill(dummy, 0.0);
					featsBack = ArrayUtils.addAll(featsBack, dummy);
				}
			}

			double[] featsForw = new double[0];
			boolean forwardDummy = true;
			for (int j = 1; j <= lookAhead; j++) {
				forwardDummy &= i + j < set.size() && !set.get(i + j).equals("%newarticle%");
				if (forwardDummy) {
					Word next = set.get(i + j);
					featsForw = ArrayUtils.addAll(featsForw, ambiFeats(next, suffixLength));
				} else {
					double[] dummy = new double[generator.getCategories().size() * (suffixLength + 1)];
					Arrays.fill(dummy, 0.0);
					if (featsForw.length == 0) {
						featsForw = dummy;
					} else {
						featsForw = ArrayUtils.addAll(featsForw, dummy);
					}
				}
			}
		
			feats = ArrayUtils.addAll(feats, featsBack);
			feats = ArrayUtils.addAll(feats, featsForw);
			feats = ArrayUtils.addAll(feats, simpleFeats(w));
			w.setFeatureVec(new FeatureVector(feats));
		}
	}

	/**
	 * calculates the number of feats a word would have if they were generated from a given model,
	 *  and given lookback and lookbehind.
	 * @param lookBehind, the previous words that should be taken into account for word generation,
	 * any woord with value %newarticle% is ignored and used in order to stop feature generation 
	 * and force a zero padding
	 * @param lookAhead, words ahead of the word to be taken into account, works same as lookbehind
	 * @return the numebr of features
	 */
	public int noFeats(int lookBehind, int lookAhead) {
		List<Word> set = new ArrayList<>();
		set.add(new Word("dummy", "null"));
		for (int i = 0; i < set.size(); i++) {
			Word w = set.get(i);
			double[] feats = ambiFeats(w, suffixLength);
			double[] featsBack = new double[0];
			boolean backwardDummy = true;
			for (int j = 1; j <= lookBehind; j++) {
				backwardDummy &= i - j >= 0 && !set.get(i - j).equals("%newarticle%");
				if (backwardDummy) {
					Word prev = set.get(i - j);
					featsBack = ArrayUtils.addAll(featsBack, ambiFeats(prev, suffixLength));
				} else {
					double[] dummy = new double[generator.getCategories().size() * (suffixLength + 1)];
					Arrays.fill(dummy, 0.0);
					featsBack = ArrayUtils.addAll(featsBack, dummy);
				}
			}

			double[] featsForw = new double[0];
			boolean forwardDummy = true;
			for (int j = 1; j <= lookAhead; j++) {
				forwardDummy &= i + j < set.size() && !set.get(i + j).equals("%newarticle%");
				if (forwardDummy) {
					Word next = set.get(i + j);
					featsForw = ArrayUtils.addAll(featsForw, ambiFeats(next, suffixLength));
				} else {
					double[] dummy = new double[generator.getCategories().size() * (suffixLength + 1)];
					Arrays.fill(dummy, 0.0);
					if (featsForw.length == 0) {
						featsForw = dummy;
					} else {
						featsForw = ArrayUtils.addAll(featsForw, dummy);
					}
				}
			}
			
			feats = ArrayUtils.addAll(feats, featsBack);
			feats = ArrayUtils.addAll(feats, featsForw);
			feats = ArrayUtils.addAll(feats, simpleFeats(w));
			w.setFeatureVec(new FeatureVector(feats));
		}
		return set.get(0).getFeatureVec().getValues().length;
	}

	/**
	 * use embeddings in order to generate feats
	 * @param set
	* @param lookBehind, the previous words that should be taken into account for word generation,
	 * any woord with value %newarticle% is ignored and used in order to stop feature generation 
	 * and force a zero padding
	 * @param lookAhead, words ahead of the word to be taken into account, works same as lookbehind
	 * @param embeddings, the embeddings to be used for word generator
	 */
	public void generateFeats(List<Word> set, int lookBehind, int lookAhead, Embeddings embeddings) {
		for (int i = 0; i < set.size(); i++) {
			Word w = set.get(i);
			double[] feats = embeddings.getVec(w.getValue());
			double[] featsBack = new double[0];
			boolean backwardDummy = true;
			for (int j = 1; j <= lookBehind; j++) {
				backwardDummy &= i - j >= 0 && !set.get(i - j).equals("%newarticle%");
				if (backwardDummy) {
					Word prev = set.get(i - j);
					featsBack = ArrayUtils.addAll(featsBack, embeddings.getVec(prev.getValue()));
				} else {
					double[] dummy = embeddings.getUnknownVec();
					Arrays.fill(dummy, 0.0);
					featsBack = ArrayUtils.addAll(featsBack, dummy);
				}
			}

			double[] featsForw = new double[0];
			boolean forwardDummy = true;
			for (int j = 1; j <= lookAhead; j++) {
				forwardDummy &= i + j < set.size() && !set.get(i + j).equals("%newarticle%");
				if (forwardDummy) {
					Word next = set.get(i + j);
					featsForw = ArrayUtils.addAll(featsForw, embeddings.getVec(next.getValue()));
				} else {
					double[] dummy = embeddings.getUnknownVec();
					Arrays.fill(dummy, 0.0);
					if (featsForw.length == 0) {
						featsForw = dummy;
					} else {
						featsForw = ArrayUtils.addAll(featsForw, dummy);
					}
				}
			}
			feats = ArrayUtils.addAll(feats, featsBack);
			feats = ArrayUtils.addAll(feats, featsForw);
			feats = ArrayUtils.addAll(feats, simpleFeats(w));
			w.setFeatureVec(new FeatureVector(feats));
		}
	}
	
	
	/**
	 * generate features for embedding for a single word without lookarounds.
	 * @param set
	 * @param gwv
	 */
	public void generateFeats(LinkedList<Word> set, Embeddings gwv) {
		for (int i = 0; i < set.size(); i++) {
			Word w = set.get(i);
			double[] word2VecFeats = new double[0];
			int lookBehind = 0;
			// get the feaure vecs of previous words
			for (int j = 1; j <= lookBehind; j++) {
				if (i - j < 0) {
					// if the previous words are out of index, we ll generate
					// some random word2Vecs vecs
					word2VecFeats = ArrayUtils.addAll(word2VecFeats,
							embeddingFeats(new Word("beginStringRandomGen" + Integer.toString(j)), gwv));
				} else {
					word2VecFeats = ArrayUtils.addAll(word2VecFeats, embeddingFeats(set.get((i - j)), gwv));
				}
			}
			// get the feature vecs of next words
			int lookAhead = 0;
			for (int j = 1; j <= lookAhead; j++) {
				if (i + j >= set.size()) {
					// if the next words are out of index, we ll generate some
					// random word2Vecs vecs
					word2VecFeats = ArrayUtils.addAll(word2VecFeats,
							embeddingFeats(new Word("finishStringRAndomGen" + Integer.toString(j)), gwv));
				} else {
					word2VecFeats = ArrayUtils.addAll(word2VecFeats, embeddingFeats(set.get((i + j)), gwv));
				}
			}
			word2VecFeats = ArrayUtils.addAll(word2VecFeats, embeddingFeats(w, gwv));
			// System.out.println("size " + word2VecFeats.length);
			if (w.getFeatureVec() == null) {
				w.setFeatureVec(new FeatureVector(word2VecFeats));
			} else {
				w.setFeatureVec(new FeatureVector(ArrayUtils.addAll(w.getFeatureVec().getValues(), word2VecFeats)));
			}
		}
		System.out.println("current vec size " + set.get(3).getFeatureVec().getValues().length);
	}
	
	/**
	 * generating ambitag for single word, 
	 * @param w
	 * @param suffixLength
	 * @return
	 */
	private double[] ambiFeats(Word w, int suffixLength) {
		double[] ambiTagFeaturesWord = generator.ambiTagFeaturesWord(w.getValue());
		int featSize = ambiTagFeaturesWord.length;
		int loops = w.getValue().length() > suffixLength ? w.getValue().length() : suffixLength;
		double[] ambiTagFeaturesSuffix = new double[0];
		double[] lastSuffixArray = new double[0];
		for (int i = 1; i <= suffixLength; i++) {
			if (i < loops) {
				lastSuffixArray = generator.ambiTagFeaturesSuffix(StringHelpers.getSuffix(w.getValue(), i));
				ambiTagFeaturesSuffix = ArrayUtils.addAll(ambiTagFeaturesSuffix, lastSuffixArray);
			} else {
				double[] dummy = new double[featSize];
				Arrays.fill(dummy, 0.0);
				ambiTagFeaturesSuffix = ArrayUtils.addAll(ambiTagFeaturesSuffix, dummy);
			}
		}
		double[] feats = ArrayUtils.addAll(ambiTagFeaturesWord, ambiTagFeaturesSuffix);
		return feats;

	}

	/**
	 * generate word features based on:
	 * hasApostrophe, hasComma, hasPunct, hasNumber, hasLatin, character length
	 * @param w
	 * @return
	 */
	public static double[] simpleFeats(Word w) {
		String v = w.getValue();
		double[] simpleFeats = new double[] { (double) v.length(),
				MathHelper.booleanToDouble(StringHelpers.hasApostrophe(v)),
				MathHelper.booleanToDouble(StringHelpers.hasComma(v)),
				MathHelper.booleanToDouble(StringHelpers.hasPunct(v)),
				MathHelper.booleanToDouble(StringHelpers.hasNumber(v)),
				MathHelper.booleanToDouble(StringHelpers.hasLatin(v)) };
		return simpleFeats;
	}

	/**
	 * retwurn the embedding vector of a word
	 * @param word
	 * @param embeddings
	 * @return
	 */
	public static double[] embeddingFeats(Word word, Embeddings embeddings) {
		return embeddings.getVec(word.getValue().toLowerCase());
	}
	
	/**
	 * generate one hot encoding for words and save them inside Word Object >  Feature Vector
	 * @param words
	 * @param model
	 */
	public static void generateBinDoubleLabels(List<Word> words, Model model) {
		for (Word w : words) {
			w.getFeatureVec().setLabels(model.getCategoryAsOneOfAKDouble(w));
		}
	}

	/**
	 * normalize a subset of the wordset
	 * @param words
	 * @param samples
	 */
	public static void normalize(List<Word> words, int samples) {
		List<Integer> indeces = new ArrayList<>();
		for (int i = 0; i < samples; i++) {
			double[] featVector = words.get(i).getFeatureVec().getValues();
			for (int j = 0; j < featVector.length; j++) {
				if (Math.abs(featVector[j]) > 1)
					indeces.add(j);
			}
		}
		normalize(words, indeces);
	}

	/**
	 * normalize feature vectors of words using mean and stadard devaition
	 *  provided the vectora values are
	 * higher than 1.
	 * @param words
	 * @param indeces
	 */
	private static void normalize(List<Word> words, List<Integer> indeces) {
		Map<Integer, Double> means = new WeakHashMap<>();
		Map<Integer, Double> stds = new WeakHashMap<>();
		for (Integer i : indeces) {
			SummaryStatistics smt = new SummaryStatistics();
			for (Word w : words) {
				smt.addValue(w.getFeatureVec().getValues()[i]);
			}
			means.put(i, smt.getMean());
			stds.put(i, smt.getStandardDeviation());
		}
		for (Integer i : indeces) {
			for (Word w : words) {
				double value = w.getFeatureVec().getValues()[i];
				w.getFeatureVec().getValues()[i] = (value - means.get(i)) / stds.get(i);
			}
		}
	}

}
