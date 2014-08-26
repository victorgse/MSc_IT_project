package algorithms.clustering;

import javax.swing.JOptionPane;

import weka.clusterers.Clusterer;
import weka.clusterers.SimpleKMeans;
import weka.experiment.InstanceQuery;

/**
 * This class provides an implementation of the K-Means
 * clustering algorithm through the use of the Weka API.
 */
public class KMeansClusterer extends ClusteringAlgorithm {
	
	/**
	 * Constructor
	 */
	public KMeansClusterer() {
		try {
			instanceQuery = new InstanceQuery();
			clusterer = new SimpleKMeans();
			featuresScaledAndMeanNormalised = false;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, 
	    			"Something went wrong while attempting to instantiate clusterer.", 
	    			"Error: Clusterer Not Instantiated", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Alternative method for training the K-Means clusterer - trains multiple K-Means clusterers 
	 * with different random initialisations of the cluster centroids, and then sets the original 
	 * clusterer to be the one that gave the least within cluster sum of squared errors.
	 * @param numberOfRuns
	 */
	public void train(int numberOfRuns) {
		double leastSquaredErrorSoFar = Double.POSITIVE_INFINITY;
		for (int i = 0; i < numberOfRuns; i++) {
			SimpleKMeans testRunClusterer = new SimpleKMeans();
			try {
				testRunClusterer.setOptions(((SimpleKMeans) clusterer).getOptions());
				testRunClusterer.setSeed(i + 1);
				testRunClusterer.buildClusterer(trainingSet);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, 
		    			"Something went wrong while attempting to train clusterer.", 
		    			"Error: Clusterer Not Trained", JOptionPane.ERROR_MESSAGE);
			}
			double squaredError = testRunClusterer.getSquaredError();
			if (squaredError < leastSquaredErrorSoFar) {
				leastSquaredErrorSoFar = squaredError;
				clusterer = (Clusterer) testRunClusterer;
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see algorithms.Algorithm#setOptions(java.lang.String)
	 * Sets the options of the K-Means clusterer.
	 */
	public void setOptions(String algorithmParameters) {
		try {
			options = weka.core.Utils.splitOptions(algorithmParameters);
			((SimpleKMeans) clusterer).setOptions(options);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, 
	    			"Something went wrong while attempting to set the clusterer's options.", 
	    			"Error: Clusterer's Options Not Set", JOptionPane.ERROR_MESSAGE);
		}
	}
	
}
