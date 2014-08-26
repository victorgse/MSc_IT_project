package algorithms.clustering;

import javax.swing.JOptionPane;

import algorithms.Algorithm;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.Clusterer;

/**
 * An abstract superclass which contains members that are common to all clustering algorithms.
 */
public abstract class ClusteringAlgorithm extends Algorithm {
	
	/**
	 * instance variables
	 */
	protected Clusterer clusterer; //the clusterer object
	protected ClusterEvaluation eval; //the clusterer-evaluation object
	
	/* (non-Javadoc)
	 * @see algorithms.Algorithm#train()
	 * Trains a clusterer.
	 */
	public void train() {
		try {
			clusterer.buildClusterer(trainingSet);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, 
	    			"Something went wrong while attempting to train clusterer.", 
	    			"Error: Clusterer Not Trained", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/* (non-Javadoc)
	 * @see algorithms.Algorithm#evaluate()
	 * Evaluates a clusterer.
	 */
	public ClusterEvaluation evaluate() {
		eval = new ClusterEvaluation();
		eval.setClusterer(clusterer);
		try {
			eval.evaluateClusterer(trainingSet);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, 
	    			"Something went wrong while attempting to evaluate clusterer.", 
	    			"Error: Clusterer Not Evaluated", JOptionPane.ERROR_MESSAGE);
		}
		return eval;
	}

}
