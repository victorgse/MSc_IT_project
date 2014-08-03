package algorithms;

import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Discretize;
import weka.filters.unsupervised.attribute.NumericToNominal;

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
	Instances testSet;
	
	/**
	 * Method for setting the target label
	 * @param classIndex
	 */
	public void setTargetLabel(int classIndex) {
		trainingSet.setClassIndex(classIndex);
		if (!trainingSet.classAttribute().isNominal()) {
			Instances newTrainingSet = null;
			try {
				NumericToNominal numericToNominalConverter = new NumericToNominal();
				numericToNominalConverter.setAttributeIndices("last");
				numericToNominalConverter.setInvertSelection(false);
				numericToNominalConverter.setInputFormat(trainingSet);
				newTrainingSet = Filter.useFilter(trainingSet, numericToNominalConverter);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (newTrainingSet.classAttribute().numValues() <= 5) {
				trainingSet = newTrainingSet;
			} else {
				try {
					Discretize numericToDiscreteNominalConverter = new Discretize();
					numericToDiscreteNominalConverter.setAttributeIndices("last");
					numericToDiscreteNominalConverter.setBins(5);
					numericToDiscreteNominalConverter.setIgnoreClass(true);
					numericToDiscreteNominalConverter.setInvertSelection(false);
					numericToDiscreteNominalConverter.setUseEqualFrequency(true);
					numericToDiscreteNominalConverter.setInputFormat(trainingSet);
					trainingSet = Filter.useFilter(trainingSet, numericToDiscreteNominalConverter);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * @param evaluationOption the evaluationOption to set
	 */
	public void setEvaluationOption(String evaluationOption) {
		this.evaluationOption = evaluationOption;
	}
	
	/**
	 * @return the testSet
	 */
	public Instances getTestSet() {
		return testSet;
	}
	
	/**
	 * Method for splitting a dataset into training, validation, and test sets (in specified proportions).
	 * @param trainingPercent
	 * @param ValidationPercent
	 * @param TestPercent
	 */ /*
	public void splitDataset(double trainingPercent, double validationPercent, double testPercent) {
		int trainingSetSize = (int) Math.round(trainingSet.numInstances() * trainingPercent);
		int validationSetSize = (int) Math.round(trainingSet.numInstances() * validationPercent);
		int testSetSize = trainingSet.numInstances() - (trainingSetSize + validationSetSize);
		Instances trainSet = new Instances(trainingSet, 0, trainingSetSize);
		validationSet = new Instances(trainingSet, trainingSetSize, validationSetSize);
		testSet = new Instances(trainingSet, trainingSetSize + validationSetSize, testSetSize);
		trainingSet = trainSet;
	} */

	/**
	 * Method for splitting a dataset into training and test sets (in specified proportions).
	 * @param trainingPercent
	 */
	public void splitDataset(double trainingPercent) {
		int trainingSetSize = (int) Math.round(trainingSet.numInstances() * trainingPercent);
		int testSetSize = trainingSet.numInstances() - trainingSetSize;
		Instances trainSet = new Instances(trainingSet, 0, trainingSetSize);
		testSet = new Instances(trainingSet, trainingSetSize, testSetSize);
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
			if (evaluationOption.equals("trainingSet")) {
				eval.evaluateModel(classifier, trainingSet);
			} else if (evaluationOption.equals("CV")) {
				eval.crossValidateModel(classifier, trainingSet, 10, new Random(1));
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
