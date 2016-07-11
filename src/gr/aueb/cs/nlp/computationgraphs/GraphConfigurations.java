package gr.aueb.cs.nlp.computationgraphs;

import java.util.List;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.GravesLSTM;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;

import gr.aueb.cs.nlp.wordtagger.data.structure.Word;

/**
 * 
 * @author Thomas Asikis
 * @license Copyright (c) 2016 Thomas Asikis
 *			Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 			The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 			THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

/**
 * this class is badly witten (at least I don't like what I wrote here), and 
 * its main purpose is to provide some models to test the 
 * deeplearning computation graphs. You use it for examples in how to 
 * handle computation graphs and also you can train some of these models
 * to check how well they do in your classificaiton. Maybe a grid 
 * search or bayesian optimizaiton will provide you with better performing models than these.
 * @author Thomas Asikis
 *
 */
public class GraphConfigurations {
	
	/**
	 * A 5 layer MLP that tries to guess the category of each example based on its features
	 * @param totalCategories
	 * @param trainSet
	 * @param testSet
	 * @return The configuration that provides this model
	 */
	public static ComputationGraphConfiguration DeepMLPGraph(List<Word> trainSet){
		int inputFeatures = trainSet.get(0).getFeatureVec().getValues().length;
		int outputLabels = trainSet.get(0).getFeatureVec().getLabels().length;

		ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder()
		        .learningRate(0.01)
		        .regularization(true) //else it won't use regularizers
		        .graphBuilder()
		        .addInputs("input") //can use any label for this, it is just an identifier for the graph.
		        .addLayer("L1", new DenseLayer.Builder()
		        		.nIn(inputFeatures) // always current nIn = Sum(nOut) of layer inputs
		        		.nOut(300)	//you have as many neurons as your outputs...
		        		.biasLearningRate(0.2) //faster changing bias int he first layer then slower
		        		.activation(ActivationFunction.relu.name()) // the enumerations are made by me, 
		        													//to help me know which activations are available,
		        													//for me relu work so often but they need more neurons per layer than others
		        		.l1(0.3) //l1 regularization
		        		.l2(0.02) //l2 regularization
		        		.dropOut(0.3) //regularization via freezing whole neurons on a feedforward phase, 
		        					  //this works usual/y better than l1,l2 for me
		        		.updater(Updater.NESTEROVS) //how the weights are updated. nesterovs uses the momentum
		        		.momentum(0.3) //the higher the easier to escape a saddle point or miss an optimum
		        		.build(), "input")//input here, means which layer is the input to this layes. 
		        						  //so the layer with identifier "input" is the input for "L1" 
		        .addLayer("L2", new DenseLayer.Builder()
		        		.nIn(300) // an autoencoder for some feature extraction
		        		.nOut(250)
		        		.biasLearningRate(0.02)
		        		.l1(0.3)
		        		.l2(0.02)
		        		.activation(ActivationFunction.relu.name()) 
		        		.dropOut(0.3)
		        		.updater(Updater.ADAM)//how to use ADAM, read the ADAM paper 
		        							  //to understand better what they do https://arxiv.org/pdf/1412.6980.pdf
		        							  //usually cheaper training and better bias correction...
		        		.adamMeanDecay(0.2)
		        		.adamMeanDecay(0.2)
		        		.build(), "L1")
		        .addLayer("L3", new DenseLayer.Builder()
		        		.nIn(250) // an autoencoder for some feature extraction
		        		.nOut(300)
		        		.biasLearningRate(0.02)
		        		.activation(ActivationFunction.relu.name()) 
		        		.l1(0.1)
		        		.l2(0.1)
		        		.dropOut(0.3)
		        		.build(), "L2")
		        .addLayer("L4", new DenseLayer.Builder()
		        		.nIn(300) // an autoencoder for some feature extraction
		        		.nOut(250)
		        		.biasLearningRate(0.03)
		        		.l1(0.1)
		        		.l2(0.1)
		        		.activation(ActivationFunction.relu.name()) 
		        		.dropOut(0.3)
		        		.build(), "L3")
		        .addLayer("L5",new OutputLayer.Builder()
		        		.nIn(250)
		        		.nOut(outputLabels)
		        		.lossFunction(LossFunction.MCXENT)	//categorical cross entropy, when building the output layer be very careful
		        											//about pairing the right loss function with the appropriate activation
		        											//e.g. why can't sigmoid work with hinge loss? cause [0,1] != [-1,1]
		        		.activation(ActivationFunction.softmax.name()) //softmax goes with categorical corss entropy
		        		.build(),  "L4")
		        .setOutputs("L5")	//We need to specify the network outputs and their order
		        .build();
		return conf;		
	}
	
	/**
	 * an example LSTM Graph
	 * @param totalCategories
	 * @param trainSet
	 * @param testSet
	 * @return
	 */
	public static ComputationGraphConfiguration LSTMGraph(List<Word> trainSet){
		int inputFeatures = trainSet.get(0).getFeatureVec().getValues().length;
		int outputLabels = trainSet.get(0).getFeatureVec().getLabels().length;
		
		ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder()
		        .learningRate(0.01)
		        .regularization(true)
		        .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
		        .graphBuilder()
	
		        .addInputs("input") //can use any label for this, it is just an identifier for the graph.
		        .addLayer("L1", new GravesLSTM.Builder()
		        		.nIn(inputFeatures) // always current nIn = Sum(nOut) of layer inputs
		        		.nOut(150)	//you have as many neurons as your outputs...
		        		.biasLearningRate(0.2) //faster changing bias int he first layer then slower
		        		.activation(ActivationFunction.tanh.name()) // the enumerations are made by me, 
		        													//to help me know which activations are available,
		        													//for me relu work so often but they need more neurons per layer than others
		        		.l1(0.3) //l1 regularization
		        		.l2(0.02) //l2 regularization
		        		.dropOut(0.3) //regularization via freezing whole neurons on a feedforward phase, 
		        					  //this works usual/y better than l1,l2 for me
		        		.updater(Updater.ADADELTA) //how the weights are updated. nesterovs uses the momentum
		        		.momentum(0.3) //the higher the easier to escape a saddle point or miss an optimum
		        		.build(), "input")//input here, means which layer is the input to this layes. 
		        						  //so the layer with identifier "input" is the input for "L1" 
		        .addLayer("L2", new GravesLSTM.Builder()
		        		.nIn(150) // an autoencoder for some feature extraction
		        		.nOut(200)
		        		.biasLearningRate(0.02)
		        		.l1(0.3)
		        		.l2(0.02)
		        		.activation(ActivationFunction.relu.name()) 
		        		.dropOut(0.3)
		        		.updater(Updater.ADAM)//how to use ADAM, read the ADAM paper 
		        							  //to understand better what they do https://arxiv.org/pdf/1412.6980.pdf
		        							  //usually cheaper training and better bias correction...
		        		.adamMeanDecay(0.2)
		        		.adamMeanDecay(0.2)
		        		.build(), "L1")
		        .addLayer("L3",new RnnOutputLayer.Builder()
		        		.nIn(200)
		        		.nOut(outputLabels)
		        		.build(),  "L2")
		        .setOutputs("L3")	//We need to specify the network outputs and their order
		        .build();
		return conf;	
	}
	
}
