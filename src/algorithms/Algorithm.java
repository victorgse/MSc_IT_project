package algorithms;

import java.util.ArrayList;

import weka.core.Instances;
import weka.experiment.InstanceQuery;

/**
 * An abstract superclass which contains members that are common to all inheriting algorithms.
 */
public abstract class Algorithm {
	
	/**
	 * instance variables
	 */
	protected InstanceQuery instanceQuery; //SQL query for fetching instances
	protected Instances data; //dataset instances
	protected String[] options; //algorithm options
	
	/**
	 * Method which sets the SQL query for fetching instances.
	 * @param query
	 */
	public void setInstanceQuery(String query) {
		instanceQuery.setQuery(query);
	}
	
	/**
	 * Method which executes the SQL query for fetching instances
	 * and stores the returned instances in data.
	 */
	public void fetchInstances() {
		try {
			data = instanceQuery.retrieveInstances();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Something went wrong with retrieving instances.");
		}
	}
	
	/**
	 * Method for renaming the attributes of instances.
	 * @param attributesOfInstances
	 */
	public void renameAttributesOfInstances(ArrayList<String> namesOfAttributesOfInstances) {
		for (int i = 0; i < namesOfAttributesOfInstances.size(); i++) {
			data.renameAttribute(i, namesOfAttributesOfInstances.get(i));
		}
	}
	
	/**
	 * Abstract method for setting the options of an algorithm.
	 * @param algorithmParameters
	 */
	public abstract void setOptions(String algorithmParameters);
	
	/**
	 * Abstract method for training an algorithm.
	 */
	public abstract void train();
	
	/**
	 * Abstract method for evaluating an algorithm
	 * @return String containing essential information of an evaluation of algorithm
	 */
	public abstract String evaluate();
	
}
