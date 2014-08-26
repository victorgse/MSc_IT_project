package algorithms.classification;

import javax.swing.JOptionPane;

import weka.classifiers.functions.LibSVM;
import weka.experiment.InstanceQuery;

/**
 * This class provides an implementation of the SVM Classification
 * algorithm through the use of the Weka API and the LibSVM library.
 */
public class SVMClassifier extends ClassificationAlgorithm {
	
	/**
	 * Constructor
	 */
	public SVMClassifier() {
		try {
			instanceQuery = new InstanceQuery();
			classifier = new LibSVM();
			featuresScaledAndMeanNormalised = false;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, 
	    			"Something went wrong while attempting to instantiate classifier.", 
	    			"Error: Classifier Not Instantiated", JOptionPane.ERROR_MESSAGE);
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
			//((LibSVM) classifier).setProbabilityEstimates(true);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, 
	    			"Something went wrong while attempting to set the classifier's options.", 
	    			"Error: Classifier's Options Not Set", JOptionPane.ERROR_MESSAGE);
		}
	}
	
}
