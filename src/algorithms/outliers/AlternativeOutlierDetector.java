package algorithms.outliers;

import algorithms.Algorithm;
import weka.core.Instance;
import weka.experiment.InstanceQuery;

public class AlternativeOutlierDetector extends Algorithm {
	
	/**
	 * instance variables
	 */
	private OutlierEvaluation eval;
	private double epsilon;
	private double[] probabilities;

	/**
	 * Constructor
	 */
	public AlternativeOutlierDetector() {
		try {
			instanceQuery = new InstanceQuery();
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
		for (int i = 0; i < tokens.length; i++) {
			if (tokens[i].equals("-E")) {
				epsilon = Double.parseDouble(tokens[i+1]);
			}
		}

	}

	@Override
	public void train() {
		probabilities = new double[trainingSet.numInstances()];
		for (int i = 0; i < trainingSet.numInstances(); i++) {
			probabilities[i] = computeProbability(trainingSet.get(i));
		}	
	}

	@Override
	public OutlierEvaluation evaluate() {
		double[] classAssignments = new double[probabilities.length]; //1 --> outlier, 0 otherwise
		for (int i = 0; i < probabilities.length; i++) {
			if (probabilities[i] < epsilon) {
				classAssignments[i] = 1;
			} else {
				classAssignments[i] = 0;
			}
		}
		eval.setClassAssignments(classAssignments);
		return eval;
	}
		
	private double computeProbability(Instance x) {
		double attrMean = trainingSet.attributeStats(0).numericStats.mean;
		double attrStdDev = trainingSet.attributeStats(0).numericStats.stdDev;
		double probabilityOfx = (1 / (Math.sqrt(2 * Math.PI) * attrStdDev)) 
				* Math.pow(Math.E, -((Math.pow((x.value(0) - attrMean), 2)
						/ (2 * Math.pow(attrStdDev, 2)))));
		for (int i = 1; i < x.numAttributes(); i++) {
			attrMean = trainingSet.attributeStats(i).numericStats.mean;
			attrStdDev = trainingSet.attributeStats(i).numericStats.stdDev;
			probabilityOfx *= (1 / (Math.sqrt(2 * Math.PI) * attrStdDev)) 
					* Math.pow(Math.E, -((Math.pow((x.value(i) - attrMean), 2)
									/ (2 * Math.pow(attrStdDev, 2)))));
		}
		return probabilityOfx;
	}

}
