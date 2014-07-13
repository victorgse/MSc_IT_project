package algorithms;

import weka.core.Instances;
import weka.experiment.InstanceQuery;

public abstract class Algorithm {
	
	// instance variables
	protected InstanceQuery instanceQuery;
	protected Instances data;
	protected String[] options;
	
	public void setInstanceQuery(String query) {
		instanceQuery.setQuery(query);
	}
	
	public void fetchInstances() {
		try {
			data = instanceQuery.retrieveInstances();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Something went wrong with retrieving instances.");
		}
	}
	
	public abstract void setOptions(String algorithmParameters);
	
	public abstract void train();
	
	public abstract void evaluate();
}
