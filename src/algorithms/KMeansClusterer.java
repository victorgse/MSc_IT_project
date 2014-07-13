package algorithms;

import weka.clusterers.ClusterEvaluation;
import weka.clusterers.SimpleKMeans;
import weka.experiment.InstanceQuery;

public class KMeansClusterer extends ClusteringAlgorithm {
	
	// constructor
	public KMeansClusterer() {
		try {
			instanceQuery = new InstanceQuery();
			clusterer = new SimpleKMeans();
			eval = new ClusterEvaluation();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
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
