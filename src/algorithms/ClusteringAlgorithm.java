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
			clusterer.buildClusterer(trainingSet);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Something went wrong with training clusterer.");
		}
	}
	
	/* (non-Javadoc)
	 * @see algorithms.Algorithm#evaluate()
	 * Evaluates a clusterer.
	 */
	public ClusterEvaluation evaluate() {
		eval.setClusterer(clusterer);
		try {
			eval.evaluateClusterer(trainingSet);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Something went wrong with evaluating clusterer.");
		}
		return eval;
	}

}
