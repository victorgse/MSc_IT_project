import java.awt.event.*;
import java.io.File;
import java.util.TreeSet;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import weka.classifiers.Evaluation;
import weka.clusterers.ClusterEvaluation;
import weka.core.Instances;
import algorithms.KMeansClusterer;
import algorithms.SVMClassifier;

public class Controller implements ActionListener {
	
	/**
	 * instance variables
	 */
	private View viewObject; //a reference to the GUI object
	private String state; //keeps track of what state the program is in
	private String selectedDataset; //the name of the dataset that has been selected by user
	private KMeansClusterer clusterer; //the clustering object
	private int selectedMcfcClusteringOption; //tracks the types of MCFC data items to be clustered
	private SVMClassifier classifier; //the classification object
	private TreeSet<String> selectedFeatures; //a list of the features selected by user
	private String query; //builds and stores the query with which instances will be requested
	
	/**
	 * Constructor
	 */
	public Controller() {
		state = "startScreen_1";
	}
	
	/**
	 * viewObject Setter
	 * @param view
	 */
	public void setView(View view) {
		viewObject = view;
	}

	/**
	 * state Getter
	 * @return the state
	 */
	public String getState() {
		return state;
	}
	
	/**
	 * selectedDataset Getter
	 * @return the selectedDataset
	 */
	public String getSelectedDataset() {
		return selectedDataset;
	}

	/**
	 * @return the selectedFeatures
	 */
	public TreeSet<String> getSelectedFeatures() {
		return selectedFeatures;
	}

	/**
	 * A helper method for fetching an .xls file selected by user.
	 * @return
	 */
	public File getFile() {
		JFileChooser chooser = new JFileChooser();
	    FileNameExtensionFilter filter = new FileNameExtensionFilter(
	        "Excel .xls files", "xls");
	    chooser.setFileFilter(filter);
	    int returnVal = chooser.showOpenDialog(viewObject);
	    if (returnVal == JFileChooser.APPROVE_OPTION) {
	    	return chooser.getSelectedFile();
	    } else {
	    	return null;
	    }
	}
	
	/**
	 * A helper method that adds the features selected by user to the selectedFeatures list.
	 */
	public void collectSelectedFeatures() {
		for (int i = 0; i < viewObject.features.length; i++) {
			if (viewObject.features[i].isSelected()) {
				selectedFeatures.add(viewObject.features[i].getText());
			}
		}
	}

	/**
	 * A helper method for processing clicks of the "Start Over" button.
	 */
	public void processStartOverButtonClick() {
		state = "startScreen_1";
		viewObject.updateView(state);
	}
	
	/**
	 * A helper method for processing clicks of the "Back" button.
	 */
	public void processBackButtonClick() {
		switch (state) {
			case "startScreen_2":
				state = "startScreen_1";
				break;
			case "clustering_step1":
			case "classification_step1":
			case "anomalyDetection_step1":
				state = "startScreen_2";
				break;
			case "clustering_step2":
				state = "clustering_step1";
				break;
			case "clustering_step3":
				state = "clustering_step2";
				break;
			case "clustering_step4":
				state = "clustering_step3";
				break;
			case "classification_step2":
				state = "classification_step1";
				break;
			case "classification_step3":
				state = "classification_step2";
				break;
			case "classification_step4":
				state = "classification_step3";
				break;
			case "classification_step5":
				state = "classification_step4";
				break;
		}
		viewObject.updateView(state);
	}
	
	/**
	 * A helper method for processing clicks of the "Next" button.
	 */
	public void processNextButtonClick() {
		switch (state) {
			case "startScreen_1":
				if (viewObject.mcfcAnalyticsFullDatasetButton.isSelected()) {
					selectedDataset = "MCFC_Analytics_Full_Dataset";
				} else if (viewObject.otherDatasetButton.isSelected()) {
					File selectedFile = getFile();
				    try {
						new DatasetLoader(selectedFile);
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("Error: Dataset could not be loaded");
					}
				}
				state = "startScreen_2";
				break;
			case "startScreen_2":
				if (viewObject.clusteringButton.isSelected()) {
					clusterer = new KMeansClusterer();
					state = "clustering_step1";
				} else if (viewObject.classificationButton.isSelected()) {
					classifier = new SVMClassifier();
					state = "classification_step1";
				} else if (viewObject.anomalyDetectionButton.isSelected()) {
					state = "anomalyDetection_step1";
				}
				break;
			case "clustering_step1":
				state = "clustering_step2";
				selectedMcfcClusteringOption = viewObject.itemsToClusterCombo.getSelectedIndex();
				break;
			case "clustering_step2":
				selectedFeatures = new TreeSet<String>();
				collectSelectedFeatures();
				query = "select ";
				if (selectedDataset.equals("MCFC_Analytics_Full_Dataset")) {
					for (String feature : selectedFeatures) {
						if (selectedMcfcClusteringOption == 0) {
							query += feature + ",";
						} else {
							query += "sum(" + feature + "),";
						}
					}
				} else {
					//other dataset selected
				}
				query = query.substring(0, query.length()-1);
				query += " from " + selectedDataset;
				if (selectedMcfcClusteringOption == 1) {
					query += " group by Player_ID";
				} else if (selectedMcfcClusteringOption == 2) {
					query += " group by Team";
				}
				clusterer.setInstanceQuery(query);
				clusterer.fetchInstances();
				clusterer.renameAttributesOfInstances(selectedFeatures);
				state = "clustering_step3";
				break;
			case "clustering_step3":
				String algorithmParameters = "-N " 
						+ (int) viewObject.numberOfClustersSpinner.getValue()
						+ " -I " 
						+ (int) viewObject.maxNumberOfIterationsSpinner.getValue();
				clusterer.setOptions(algorithmParameters);
				clusterer.train();
				
				ClusterEvaluation clustererEvaluation = clusterer.evaluate();
				viewObject.algorithmOutputTextArea.setText(clustererEvaluation.clusterResultsToString());
				
				Instances instances = clusterer.getTrainingSet();
				String[] axesLabels = new String[3];
				for (int m = 0; m < 3; m++) {
					axesLabels[m] = instances.attribute(m).name();
				}
				double[][] coordinates = new double[instances.numInstances()][instances.numAttributes()];
				for (int i = 0; i < instances.numInstances(); i++) {
					for (int j = 0; j < instances.numAttributes(); j++) {
						coordinates[i][j] = instances.get(i).value(j);
					}
				}
				double[] clusterAssignments = clustererEvaluation.getClusterAssignments();
				new PickablePointsScatter3D(axesLabels, coordinates, clusterAssignments);
				
				state = "clustering_step4";
				break;
			case "classification_step1":
				selectedFeatures = new TreeSet<String>();
				collectSelectedFeatures();
				query = "select ";
				for (String feature : selectedFeatures) {
					query += feature + ",";
				}
				state = "classification_step2";
				break;
			case "classification_step2":
				//selectedFeatures.add(viewObject.targetLabelCombo.getSelectedItem().toString());
				query += viewObject.targetLabelCombo.getSelectedItem().toString();
				query += " from " + selectedDataset;
				classifier.setInstanceQuery(query);
				classifier.fetchInstances();
				classifier.setTargetLabel(selectedFeatures.size());
				state = "classification_step3";
				break;
			case "classification_step3":
				classifier.train();
				state = "classification_step4";
				break;
			case "classification_step4":
				classifier.setEvaluationOption("CV");
				Evaluation classifierEvaluation = classifier.evaluate();
				viewObject.algorithmOutputTextArea.setText(classifierEvaluation.toSummaryString());
				state = "classification_step5";
				break;
		}
		viewObject.updateView(state);
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == viewObject.startOverButton) {
			processStartOverButtonClick();
		} else if (ae.getSource() == viewObject.backButton) {
			processBackButtonClick();
		} else if (ae.getSource() == viewObject.nextButton) {
			processNextButtonClick();
		}
	}

}
