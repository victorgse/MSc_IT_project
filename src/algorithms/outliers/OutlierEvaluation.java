package algorithms.outliers;

/**
 * This class stores and provides access to statistical
 * information about the results of an outlierDetector.
 *
 */
public class OutlierEvaluation {
	
	/**
	 * instance variables
	 */
	private double[] classAssignments; //1 --> outlier, 0 otherwise
	private int numOutliers; //the number of identified outliers
	private int numNormals; //the number of non-outliers

	/**
	 * @return the classAssignments
	 */
	public double[] getClassAssignments() {
		return classAssignments.clone();
	}

	/**
	 * @param classAssignments the classAssignments to set
	 */
	public void setClassAssignments(double[] classAssignments) {
		this.classAssignments = classAssignments;
	}

	/**
	 * @param numOutliers the numOutliers to set
	 */
	public void setNumOutliers(int numOutliers) {
		this.numOutliers = numOutliers;
	}

	/**
	 * @param numNormals the numNormals to set
	 */
	public void setNumNormals(int numNormals) {
		this.numNormals = numNormals;
	}

	/**
	 * Method that returns a String of information about the results of an outlierDetector.
	 * @return a String of information about the results of an outlierDetector.
	 */
	public String resultsToString() {
		String results = null;
		int totalNumberOfInstances = numOutliers + numNormals;
		double percentOutliers = ((double) numOutliers / (double) totalNumberOfInstances);
		double percentNormals = 1.00 - percentOutliers;
		results = "Interquartile Range Outlier Detector\n"
				+ "====================================\n\n";
		results += String.format("Number of Outliers: %d/%d (%4.2f%%)\n", numOutliers, totalNumberOfInstances, percentOutliers * 100);
		results += String.format("Number of Normals: %d/%d (%4.2f%%)", numNormals, totalNumberOfInstances, percentNormals * 100);
		return results;
	}
	
}
