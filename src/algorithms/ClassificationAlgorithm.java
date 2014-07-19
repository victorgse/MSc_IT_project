package algorithms;

import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;

/**
 * An abstract superclass which contains members that are common to all classification algorithms
 */
public abstract class ClassificationAlgorithm extends Algorithm {
	
	/**
	 * instance variables
	 */
	Classifier classifier;
	Evaluation eval;

	/* (non-Javadoc)
	 * @see algorithms.Algorithm#train()
	 * Trains a classifier.
	 */
	public void train() {
		try {
			classifier.buildClassifier(data);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Something went wrong with training classifier.");
		}
	}

	/* (non-Javadoc)
	 * @see algorithms.Algorithm#evaluate()
	 * Evaluates a classifier.
	 */
	@Override
	public String evaluate() {
		try {
			eval.crossValidateModel(classifier, data, 10, new Random(1));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Something went wrong with evaluating classifier.");
		}
		return null;
	}

}
