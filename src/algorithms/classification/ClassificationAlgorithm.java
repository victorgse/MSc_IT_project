package algorithms.classification;

import java.util.Random;

import algorithms.Algorithm;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.output.prediction.AbstractOutput;
import weka.classifiers.evaluation.output.prediction.PlainText;
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
	protected Classifier classifier;
	protected Evaluation eval;
	protected String evaluationOption;
	protected Instances testSet;
	protected AbstractOutput objectForPredictionsPrinting;
	protected StringBuffer buffer;
	protected double[] actualClassAssignments;
	protected double[] predictedClassAssignments;
	
	/**
	 * Method for setting the target label.
	 * It converts the class attribute from numeric to nominal if necessary,
	 * and it discretises the values of the class attribute if there are more
	 * than five classes initially.
	 * @param classIndex
	 */
	public void setTargetLabel(int classIndex) {
		trainingSet.setClassIndex(classIndex);
		nominaliseOrDiscretiseInstances(String.valueOf(classIndex + 1));
	}
	
	public void nominaliseOrDiscretiseInstances(String attributeIndices) {
		if (!trainingSet.classAttribute().isNominal()) {
			Instances newTrainingSet = null;
			try {
				NumericToNominal numericToNominalConverter = new NumericToNominal();
				numericToNominalConverter.setAttributeIndices(attributeIndices);
				numericToNominalConverter.setInvertSelection(false);
				numericToNominalConverter.setInputFormat(trainingSet);
				newTrainingSet = Filter.useFilter(trainingSet, numericToNominalConverter);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (newTrainingSet.classAttribute().numValues() <= 5) {
				trainingSet = newTrainingSet;
			} else {
				try {
					Discretize numericToDiscreteNominalConverter = new Discretize();
					numericToDiscreteNominalConverter.setAttributeIndices(attributeIndices);
					numericToDiscreteNominalConverter.setBins(5);
					numericToDiscreteNominalConverter.setIgnoreClass(true);
					numericToDiscreteNominalConverter.setInvertSelection(false);
					numericToDiscreteNominalConverter.setUseEqualFrequency(true);
					numericToDiscreteNominalConverter.setInputFormat(trainingSet);
					trainingSet = Filter.useFilter(trainingSet, numericToDiscreteNominalConverter);
				} catch (Exception e) {
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
	
	/**
	 * @return the testSet
	 */
	public Instances getTestSet() {
		return testSet;
	}
	
	/**
	 * @return the actualClassAssignments
	 */
	public double[] getActualClassAssignments() {
		return actualClassAssignments;
	}

	/**
	 * @return the predictedClassAssignments
	 */
	public double[] getPredictedClassAssignments() {
		return predictedClassAssignments;
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
			objectForPredictionsPrinting = new PlainText();
			buffer = new StringBuffer();
			objectForPredictionsPrinting.setBuffer(buffer);
			//objectForPredictionsPrinting.setOutputDistribution(true);
			if (evaluationOption.equals("trainingSet")) {
				objectForPredictionsPrinting.setHeader(trainingSet);
				eval.evaluateModel(classifier, trainingSet, objectForPredictionsPrinting);
			} else if (evaluationOption.equals("testSet")) {
				objectForPredictionsPrinting.setHeader(testSet);
				eval.evaluateModel(classifier, testSet, objectForPredictionsPrinting);
			} else if (evaluationOption.equals("CV")) {
				objectForPredictionsPrinting.setHeader(trainingSet);
				eval.crossValidateModel(classifier, trainingSet, 10, new Random(1), objectForPredictionsPrinting);
			}
			if (evaluationOption.equals("trainingSet") || evaluationOption.equals("testSet")) {
				String[] lines = objectForPredictionsPrinting.getBuffer().toString().split("\n");
				actualClassAssignments = new double[lines.length];
				predictedClassAssignments = new double[lines.length];
				for (int i = 0; i < lines.length; i++) {
					String[] tokens = lines[i].split("[ ]+");
					actualClassAssignments[i] = Double.parseDouble(tokens[2].substring(0, 1));
					predictedClassAssignments[i] = Double.parseDouble(tokens[3].substring(0, 1));
				}
			}
			//System.out.println(objectForPredictionsPrinting.getBuffer());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Something went wrong with evaluating classifier.");
		}
		return eval;
	}

}
