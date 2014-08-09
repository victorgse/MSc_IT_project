package algorithms;

import weka.experiment.InstanceQuery;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.InterquartileRange;

public class OutlierDetector extends Algorithm {
	
	/**
	 * instance variables
	 */
	private InterquartileRange outlierDetector;
	private OutlierEvaluation eval;

	/**
	 * Constructor
	 */
	public OutlierDetector() {
		try {
			instanceQuery = new InstanceQuery();
			outlierDetector = new InterquartileRange();
			eval = new OutlierEvaluation();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see algorithms.Algorithm#setOptions(java.lang.String)
	 * Sets the options for the Outlier Detection algorithm.
	 */
	public void setOptions(String algorithmParameters) {
		String[] tokens = algorithmParameters.split(" ");
		double outlierFactor = 0;
		for (int i = 0; i < tokens.length; i++) {
			if (tokens[i].equals("-O")) {
				outlierFactor = Double.parseDouble(tokens[i+1]);
			}
		}
		try {
			outlierDetector.setAttributeIndices("first-last");
			outlierDetector.setOutlierFactor(outlierFactor);
			outlierDetector.setExtremeValuesFactor(6);
			outlierDetector.setExtremeValuesAsOutliers(true);
			outlierDetector.setInputFormat(trainingSet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void train() {
		try {
			trainingSet = Filter.useFilter(trainingSet, outlierDetector);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public OutlierEvaluation evaluate() {
		double[] classAssignments = new double[trainingSet.numInstances()]; //1 --> outlier, 0 otherwise
		for (int i = 0; i < trainingSet.numInstances(); i++) {
			if (trainingSet.get(i).value(trainingSet.attribute("Outlier")) == 1) {
				classAssignments[i] = 1;
			} else {
				classAssignments[i] = 0;
			}
		}
		eval.setClassAssignments(classAssignments);
		return eval;
	}

}
