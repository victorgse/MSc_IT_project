package algorithms;

import weka.clusterers.ClusterEvaluation;
import weka.clusterers.SimpleKMeans;
import weka.experiment.InstanceQuery;

/**
 * This class provides an implementation of the K-Means Clustering
 * algorithm through the use of the Weka Machine Learning library.
 */
public class KMeansClusterer extends ClusteringAlgorithm {
	
	/**
	 * Constructor
	 */
	public KMeansClusterer() {
		try {
			instanceQuery = new InstanceQuery();
			clusterer = new SimpleKMeans();
			eval = new ClusterEvaluation();
		} catch (Exception e) {
			e.printStackTrace();
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
			e.printStackTrace();
			System.out.println("Something went wrong with setting K-Means' options.");
		}
	}
	
}
