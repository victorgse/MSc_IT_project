package algorithms;

import weka.clusterers.ClusterEvaluation;
import weka.clusterers.Clusterer;

public abstract class ClusteringAlgorithm extends Algorithm {
	
	// instance variables
	Clusterer clusterer;
	ClusterEvaluation eval;
	
	public void train() {
		try {
			clusterer.buildClusterer(data);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Something went wrong with training clusterer.");
		}
	}
	
	public void evaluate() {
		eval.setClusterer(clusterer);
		try {
			eval.evaluateClusterer(data);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Something went wrong with evaluating clusterer.");
		}
		System.out.println(eval.clusterResultsToString());
		/*
		for (double d : eval.getClusterAssignments()) {
			System.out.println("" + d);
		}
		*/
	}

}
