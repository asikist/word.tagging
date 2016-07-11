package gr.aueb.cs.nlp.wordtagger.classifier.evaluation;

import java.util.HashMap;
import java.util.List;

import gr.aueb.cs.nlp.wordtagger.classifier.MetaTagger;
import gr.aueb.cs.nlp.wordtagger.data.structure.Word;
/**
 * 
 * @author Thomas Asikis
 * @license Copyright (c) 2016 Thomas Asikis
 *			Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 			The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 			THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
public class Evaluation {
	private final MetaTagger metaTagger;
	private boolean multicat;
	private boolean ignoreOther;
	private double total = 0;
	private double accurate = 0;

	/**
	 * 
	 * @param metaTagger, the object that contains the rules for mettagging
	 * @param multicat, if the words are tagged based on the bigtagset or the small one.
	 * @param ignoreOther
	 */
	public Evaluation(MetaTagger metaTagger, boolean multicat, boolean ignoreOther) {
		this.metaTagger = metaTagger;
		this.multicat = multicat;
		this.ignoreOther = ignoreOther;
	}

	/**
	 * Evaluates the accuracy of a classifier by comparing the differences between 2 tagsets.s
	 * @param refWords, the reference words (test set)
	 * @param predWords, the predicted words (predictions on test set)
	 * @return the accuracy,raging in [0,1]
	 * @throws IllegalStateException, if the word values in each set are different
	 */
	public double evaluateAcc(List<Word> refWords, List<Word> predWords) throws Exception {
		accurate = 0;
		total = 0;
		HashMap<String, Double> evaluation = new HashMap<>();
		int i = 0;
		//System.out.println(predWords.size());
		/* Meta Tagging */
		for (Word w : predWords) {
			if (!w.getValue().equals(refWords.get(i).getValue()) && !w.getCategory().equals("null")) {
				throw new IllegalStateException(w.getValue() + " != " + refWords.get(i).getValue());
			}
			evalLabels(
					metaTag(refWords.get(i), w.getCategory(),
							i + 1 < predWords.size() ? predWords.get(i + 1).getCategory() : null),
					refWords.get(i).getCategory());
			i++;

		}
		/* Meta Tagging End */
		double accuracy = ((double) accurate / (double) total);
		evaluation.put("accuracy", accuracy);
		return (double) accurate / total;
	}

	/**
	 * Applies metatagging rules when evaluating. For now they are hardcoded.
	 * @param word, the current words
	 * @param predLabel, the previous word in seuence
	 * @param nextLabel, the next word in sequence
	 * @return
	 */
	public String metaTag(Word word, String predLabel, String nextLabel) {
		if (metaTagger != null) {
			String wordValue = word.getValue();
			if (metaTagger.getWordDict() != null && metaTagger.containsWord(word)) {
				metaTagger.wordDictionary(word);
			} else if (metaTagger.singleCategory(wordValue)) {
				predLabel = word.getCategory();
			} else if ((metaTagger.isNumeral(wordValue)) != null) {
				predLabel = "numeral/--/--/--/--";
			} else if (predLabel.matches("^article.*") || predLabel.matches("^pronoun.*")) {
				predLabel = metaTagger.pronounAdj(predLabel, nextLabel, multicat);
			}
		}
		return predLabel;
	}

	/**
	 * the method which increments the nominators and denominators of the accurace fraction
	 * based ont he metagging rules.
	 * @param predLabel, the predicted labels of a word
	 * @param realLabel, the actual labels of a word
	 */
	private void evalLabels(String predLabel, String realLabel) {
		if (ignoreOther && realLabel.contains("other") || realLabel.equals("null")) 
			return;
		if (predLabel.equals(realLabel)) 
			accurate++;		
		total++;
	}


}
