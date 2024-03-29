package algorithms;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import weka.core.Instances;
import weka.experiment.InstanceQuery;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;

/**
 * An abstract superclass which contains members that
 * are common to all algorithms that inherit from it.
 */
public abstract class Algorithm {
	
	/**
	 * instance variables
	 */
	protected InstanceQuery instanceQuery; //SQL query for fetching instances
	protected boolean featuresScaledAndMeanNormalised; //indicated whether the features have been scaled and mean-normalised
	protected Instances originalTrainingSet; //the original training set, whose features have not been scaled and mean-normalised
	protected Instances trainingSet; //the dataset instances that are used for training a learning algorithm
	protected String[] options; //algorithm options
	
	/**
	 * Method that sets the SQL query for fetching instances.
	 * @param query
	 */
	public void setInstanceQuery(String query) {
		instanceQuery.setQuery(query);
	}
	
	/**
	 * Method that executes the SQL query for fetching instances
	 * and stores the returned instances in data.
	 */
	public void fetchInstances() {
		try {
			trainingSet = instanceQuery.retrieveInstances();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, 
	    			"Something went wrong while attempting to retrieve instances.", 
	    			"Error: Instances Not Retrieved", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Method for renaming the attributes of instances.
	 * @param attributesOfInstances
	 */
	public void renameAttributesOfInstances(ArrayList<String> namesOfAttributesOfInstances) {
		int i = 0;
		for (String name : namesOfAttributesOfInstances) {
			trainingSet.renameAttribute(i, name);
			i++;
		}
	}
	
	/**
	 * @return the trainingSet
	 */
	public Instances getTrainingSet() {
		if (!featuresScaledAndMeanNormalised) {
			return trainingSet;
		} else {
			return originalTrainingSet;
		}
	}
	
	/**
	 * Method that scales and mean-normalised the features of the training set.
	 */
	public void scaleAndMeanNormaliseFeatures() {
		originalTrainingSet = new Instances(trainingSet, 0, trainingSet.size());
		Normalize normalizer = new Normalize();
		try {
			normalizer.setInputFormat(trainingSet);
			trainingSet = Filter.useFilter(trainingSet, normalizer);
			featuresScaledAndMeanNormalised = true;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, 
	    			"Something went wrong while attempting to scale features.", 
	    			"Error: Features Not Scaled", JOptionPane.ERROR_MESSAGE);
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
	 * Abstract method for evaluating an algorithm.
	 * @return Object containing essential information of an evaluation of algorithm
	 * (The actual type of evaluation Object returned will vary depending on the type of algorithm)
	 */
	public abstract Object evaluate();
	
}
