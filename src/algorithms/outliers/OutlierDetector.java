package algorithms.outliers;

import javax.swing.JOptionPane;

import weka.core.Instances;
import weka.experiment.InstanceQuery;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.InterquartileRange;

/**
 * This class provides access to the Interquartile Range Outlier Detection
 * filter through the use of the Weka API.
 */
public class OutlierDetector extends OutlierDetectionAlgorithm {
	
	/**
	 * instance variables
	 */
	private InterquartileRange outlierDetector; //the outlierDetector object
	private OutlierEvaluation eval; //the outlierDetector-evaluation object
	private Instances filteredTrainingSet; //the training set that has already been scanned for outliers

	/**
	 * Constructor
	 */
	public OutlierDetector() {
		try {
			instanceQuery = new InstanceQuery();
			outlierDetector = new InterquartileRange();
			eval = new OutlierEvaluation();
			featuresScaledAndMeanNormalised = false;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, 
	    			"Something went wrong while attempting to instantiate outlier-detector.", 
	    			"Error: Outlier-detector Not Instantiated", JOptionPane.ERROR_MESSAGE);
		}
	}

	/* (non-Javadoc)
	 * @see algorithms.Algorithm#setOptions(java.lang.String)
	 * Sets the options for the Outlier Detection filter.
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
			outlierDetector.setExtremeValuesFactor(6.01);
			outlierDetector.setExtremeValuesAsOutliers(true);
			outlierDetector.setInputFormat(trainingSet);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, 
	    			"Something went wrong while attempting to set the outlier-detector's options.", 
	    			"Error: Outlier-detector's Options Not Set", JOptionPane.ERROR_MESSAGE);
		}
	}

	/* (non-Javadoc)
	 * @see algorithms.Algorithm#train()
	 * Runs the Interquartile Range Outlier Detection filter on the training set.
	 */
	public void train() {
		try {
			filteredTrainingSet = Filter.useFilter(trainingSet, outlierDetector);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, 
	    			"Something went wrong while attempting to train outlier-detector.", 
	    			"Error: Outlier-Detector Not Trained", JOptionPane.ERROR_MESSAGE);
		}
	}

	/* (non-Javadoc)
	 * @see algorithms.Algorithm#evaluate()
	 * Evaluates the results of the Interquartile Range Outlier Detection filter.
	 */
	public OutlierEvaluation evaluate() {
		double[] classAssignments = new double[filteredTrainingSet.numInstances()]; //1 --> outlier, 0 otherwise
		int numberOfOutliers = 0;
		int numberOfNormals = 0;
		for (int i = 0; i < filteredTrainingSet.numInstances(); i++) {
			if (filteredTrainingSet.get(i).value(filteredTrainingSet.attribute("Outlier")) == 1) {
				classAssignments[i] = 1;
				numberOfOutliers++;
			} else {
				classAssignments[i] = 0;
				numberOfNormals++;
			}
		}
		eval.setClassAssignments(classAssignments);
		eval.setNumOutliers(numberOfOutliers);
		eval.setNumNormals(numberOfNormals);
		return eval;
	}

}
