package algorithms;

import weka.clusterers.ClusterEvaluation;
import weka.clusterers.Clusterer;

/**
 * An abstract superclass which contains members that are common to all clustering algorithms.
 */
public abstract class ClusteringAlgorithm extends Algorithm {
	
	/**
	 * instance variables
	 */
	Clusterer clusterer;
	ClusterEvaluation eval;
	
	/* (non-Javadoc)
	 * @see algorithms.Algorithm#train()
	 * Trains a clusterer.
	 */
	public void train() {
		try {
			clusterer.buildClusterer(data);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Something went wrong with training clusterer.");
		}
	}
	
	/* (non-Javadoc)
	 * @see algorithms.Algorithm#evaluate()
	 * Evaluates a clusterer.
	 */
	public String evaluate() {
		eval.setClusterer(clusterer);
		try {
			eval.evaluateClusterer(data);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Something went wrong with evaluating clusterer.");
		}
		return eval.clusterResultsToString();
		/*
		for (double d : eval.getClusterAssignments()) {
			System.out.println("" + d);
		}
		*/
	}

}
