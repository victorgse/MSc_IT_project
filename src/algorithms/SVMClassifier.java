package algorithms;

import weka.classifiers.functions.LibSVM;
import weka.experiment.InstanceQuery;

/**
 * This class provides an implementation of the SVM Classification
 * algorithm through the use of the Weka Machine Learning library
 * and the LibSVM Support Vector Machine library.
 */
public class SVMClassifier extends ClassificationAlgorithm {
	
	/**
	 * Constructor
	 */
	public SVMClassifier() {
		try {
			instanceQuery = new InstanceQuery();
			classifier = new LibSVM();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see algorithms.Algorithm#setOptions(java.lang.String)
	 * Sets the options of the SVM classifier.
	 */
	public void setOptions(String algorithmParameters) {
		try {
			options = weka.core.Utils.splitOptions(algorithmParameters);
			((LibSVM) classifier).setOptions(options);
			((LibSVM) classifier).setProbabilityEstimates(true);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Something went wrong with setting the SVM's options.");
		}
	}
	
}
