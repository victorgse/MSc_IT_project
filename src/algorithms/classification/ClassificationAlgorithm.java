package algorithms.classification;

import java.util.Random;

import javax.swing.JOptionPane;

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
 * An abstract superclass which contains members that are common to all classification algorithms.
 */
public abstract class ClassificationAlgorithm extends Algorithm {
	
	/**
	 * instance variables
	 */
	protected Classifier classifier; //the classifier object
	protected Evaluation eval; //the classifier-evaluation object
	protected String evaluationOption; //the desired method for evaluating a classifier
	protected Instances reducedTrainingSet; //the reduced training set (if the "70% training/30% test" classifier evaluation option has been selected)
	protected Instances originalTestSet; //the original test set, whose features have not been scaled and mean-normalised
	protected Instances testSet; //the test set
	protected AbstractOutput objectForPredictionsPrinting; //this object is required for storing the predictions made by a classifier
	protected StringBuffer buffer; //this object is required by the objectForPredictionsPrinting
	protected double[] actualClassAssignments; //the actual class assignments of the data instances
	protected double[] predictedClassAssignments; //the predicted class assignments of the data instances
	
	/**
	 * Method for setting the target label.
	 * @param classIndex
	 */
	public void setTargetLabel(int classIndex) {
		trainingSet.setClassIndex(classIndex);
		if (!trainingSet.classAttribute().isNominal()) {
			nominaliseOrDiscretiseInstances(String.valueOf(classIndex + 1));
		}
	}
	
	/**
	 * Method that converts the class attribute from numeric to nominal (if necessary),
	 * and that discretises the values of the class attribute if there are more
	 * than five classes initially.
	 * @param attributeIndices
	 */
	public void nominaliseOrDiscretiseInstances(String attributeIndices) {
		Instances newTrainingSet = null;
		try {
			NumericToNominal numericToNominalConverter = new NumericToNominal();
			numericToNominalConverter.setAttributeIndices(attributeIndices);
			numericToNominalConverter.setInvertSelection(false);
			numericToNominalConverter.setInputFormat(trainingSet);
			newTrainingSet = Filter.useFilter(trainingSet, numericToNominalConverter);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, 
	    			"Something went wrong while attempting to nominalise attributes.", 
	    			"Error: Attributes Not Nominalised", JOptionPane.ERROR_MESSAGE);
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
				JOptionPane.showMessageDialog(null, 
		    			"Something went wrong while attempting to discretise attributes.", 
		    			"Error: Attributes Not Discretised", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	/**
	 * Method for setting the desired method for evaluating a classifier.
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
		reducedTrainingSet = new Instances(trainingSet, 0, trainingSetSize);
		testSet = new Instances(trainingSet, trainingSetSize, testSetSize);
		originalTestSet = new Instances(originalTrainingSet, trainingSetSize, testSetSize);
	}
	
	/**
	 * @return the testSet
	 */
	public Instances getTestSet() {
		if (!featuresScaledAndMeanNormalised) {
			return testSet;
		} else {
			return originalTestSet;
		}
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
			if (reducedTrainingSet != null) {
				classifier.buildClassifier(reducedTrainingSet);
				reducedTrainingSet = null;
			} else {
				classifier.buildClassifier(trainingSet);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, 
	    			"Something went wrong while attempting to train classifier.", 
	    			"Error: Classifier Not Trained", JOptionPane.ERROR_MESSAGE);
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
					try {
						predictedClassAssignments[i] = Double.parseDouble(tokens[3].substring(0, 1));
					} catch (Exception e) {
						predictedClassAssignments[i] = Double.parseDouble(tokens[4].substring(0, 1));
					}
				}
			}
			//System.out.println(objectForPredictionsPrinting.getBuffer());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, 
	    			"Something went wrong while attempting to evaluate classifier.", 
	    			"Error: Classifier Not Evaluated", JOptionPane.ERROR_MESSAGE);
		}
		return eval;
	}

}
