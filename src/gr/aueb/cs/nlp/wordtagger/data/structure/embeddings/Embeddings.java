package gr.aueb.cs.nlp.wordtagger.data.structure.embeddings;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import gr.aueb.cs.nlp.wordtagger.util.CallBack;
import gr.aueb.cs.nlp.wordtagger.util.FileHelper;
import gr.aueb.cs.nlp.wordtagger.util.MathHelper;

/**
 * 
 * @description at the moment of development there were not much simple
 *              Embedding classes, for future upgrade of this class, it can be a
 *              wrapper or replaced with deeplearning4j WordRepresentation.
 *              Still this one can be used in order to import any kind of
 *              externally generated double valued features per word inh the
 *              system.
 * @author Thomas Asikis
 * @license Copyright (c) 2016 Thomas Asikis Permission is hereby granted, free
 *          of charge, to any person obtaining a copy of this software and
 *          associated documentation files (the "Software"), to deal in the
 *          Software without restriction, including without limitation the
 *          rights to use, copy, modify, merge, publish, distribute, sublicense,
 *          and/or sell copies of the Software, and to permit persons to whom
 *          the Software is furnished to do so, subject to the following
 *          conditions: The above copyright notice and this permission notice
 *          shall be included in all copies or substantial portions of the
 *          Software. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 *          KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 *          WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *          NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 *          BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 *          ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 *          CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *          SOFTWARE.
 */
public class Embeddings {
	private final String source;
	private HashMap<String, double[]> word2vecVoc;
	public int vecSize = -1;
	private double[] totalMax;
	private double[] totalMin;

	/**
	 * This constructor uses the default delimeter for embeddings, the space char \s
	 * @param source
	 */
	public Embeddings(String source) {
		this(source, " ");
	}

	/**
	 * A general constructor for loading embeddings. Automatically detects zip file.
	 * 
	 * @param source
	 * @param delimeter
	 */
	public Embeddings(String source, String delimeter) {
		this.source = source;

		try {
			InputStream is = FileHelper.isZipFile(new File(source)) ? FileHelper.unzip(source, 2048)
					: FileHelper.getInputStream(source);

			this.word2vecVoc = new HashMap<>();
			final int[] j = { 0 };
			FileHelper.readFile(is, new CallBack<String>() {
				@Override
				public void call(String s) {
					String[] st = s.split(delimeter);
					if (vecSize == -1 || vecSize < st.length - 1) {
						vecSize = st.length - 1;
						totalMax = new double[vecSize];
						totalMin = new double[vecSize];
						Arrays.fill(totalMax, Double.NaN);
						Arrays.fill(totalMin, Double.NaN);
						System.out.println("vec size " + vecSize);
					}
					double[] vec = new double[vecSize - 1];
					// some word2Vec results contain a unparsable element
					if (st.length >= vecSize) { // in case of corruption, or
												// wrong lines
						for (int i = 1; i < vecSize - 2; i++) {

							vec[i - 1] = Double.parseDouble(st[i]);
							totalMax[i - 1] = MathHelper.findMax(vec[i - 1], totalMax[i - 1]);
							totalMax[i - 1] = MathHelper.findMax(vec[i - 1], totalMax[i - 1]);
						}
					}
					word2vecVoc.put(st[0], vec);
					j[0]++;
					word2vecVoc.put(st[0], vec);
					if (j[0] % 10000 == 0) {
						System.out.println("loaded :" + j[0] + " words");
					}
				}
			});

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * get a random vector for words that we don't have embeddings for, seems to
	 * work good (increases accuracy)
	 * 
	 * @return
	 */
	public double[] getUnknownVec() {
		double[] randVec = new double[vecSize - 1];
		Random random = new Random();
		for (int i = 0; i < randVec.length; i++) {
			if (Double.isNaN(totalMax[i]) || Double.isNaN(totalMin[i])) {
				randVec[i] = 0.0;
				return randVec;
			} else {
				// for now. i ll check zipf later
				randVec[i] = totalMin[i] + (1 + random.nextGaussian()) * (totalMax[i] - totalMin[i]) / 2;
			}
		}
		return randVec;
	}

	/**
	 * get the double valued vector for a word
	 * 
	 * @param w
	 * @return
	 */
	public double[] getVec(String w) {
		if (word2vecVoc.containsKey(w)) {
			// System.out.println("yolooo" + word2vecVoc.get(w).length);
			return word2vecVoc.get(w);
		} else {
			double[] randVec = getUnknownVec();
			word2vecVoc.put(w, randVec);
			return randVec;
		}
	}

	// for iknown words generate random vectors on gaussian and
	// the set maxmins
	public HashMap<String, double[]> getWord2vecVoc() {
		return word2vecVoc;
	}

	public void setWord2vecVoc(HashMap<String, double[]> word2vecVoc) {
		this.word2vecVoc = word2vecVoc;
	}

	public String getSource() {
		return source;
	}

}
