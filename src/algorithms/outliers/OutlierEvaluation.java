package algorithms.outliers;

public class OutlierEvaluation {
	
	/**
	 * instance variables
	 */
	private double[] classAssignments; //1 --> outlier, 0 otherwise

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
	
	public String resultsToString() {
		String results = null;
		
		return results;
	}
	
}
