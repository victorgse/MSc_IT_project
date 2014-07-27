package algorithms;

import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

/**
 * An abstract superclass which contains members that are common to all classification algorithms
 */
public abstract class ClassificationAlgorithm extends Algorithm {
	
	/**
	 * instance variables
	 */
	Classifier classifier;
	Evaluation eval;
	String evaluationOption;
	Instances validationSet;
	Instances testSet;
	
	/**
	 * Method for setting the target label
	 * @param classIndex
	 */
	public void setTargetLabel(int classIndex) {
		trainingSet.setClassIndex(classIndex);
	}
	
	/**
	 * @param evaluationOption the evaluationOption to set
	 */
	public void setEvaluationOption(String evaluationOption) {
		this.evaluationOption = evaluationOption;
	}

	/**
	 * Method for splitting a dataset into training, validation, and test sets (in specified proportions)
	 * @param trainingPercent
	 * @param ValidationPercent
	 * @param TestPercent
	 */
	public void splitDataset(double trainingPercent, double validationPercent, double testPercent) {
		int trainingSetSize = (int) Math.round(trainingSet.numInstances() * trainingPercent);
		int validationSetSize = (int) Math.round(trainingSet.numInstances() * validationPercent);
		int testSetSize = trainingSet.numInstances() - (trainingSetSize + validationSetSize);
		Instances trainSet = new Instances(trainingSet, 0, trainingSetSize);
		validationSet = new Instances(trainingSet, trainingSetSize, validationSetSize);
		testSet = new Instances(trainingSet, trainingSetSize + validationSetSize, testSetSize);
		trainingSet = trainSet;
	}

	/* (non-Javadoc)
	 * @see algorithms.Algorithm#train()
	 * Trains a classifier.
	 */
	public void train() {
		try {
			classifier.buildClassifier(trainingSet);
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
	public Evaluation evaluate() {
		try {
			eval = new Evaluation(trainingSet);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			if (evaluationOption.equals("CV")) {
				eval.crossValidateModel(classifier, trainingSet, 10, new Random(1));
			} else if (evaluationOption.equals("validationSet")) {
				eval.evaluateModel(classifier, validationSet);
			} else if (evaluationOption.equals("testSet")) {
				eval.evaluateModel(classifier, testSet);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Something went wrong with evaluating classifier.");
		}
		return eval;
	}

}
