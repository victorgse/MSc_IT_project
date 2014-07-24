import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

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
	private ArrayList<String> selectedFeatures; //a list of the features selected by user
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
				selectedFeatures.add(viewObject.tableSchema.get(i));
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
				selectedFeatures = new ArrayList<String>();
				collectSelectedFeatures();
				query = "select ";
				if (selectedDataset.equals("MCFC_Analytics_Full_Dataset")) {
					for (int i = 0; i < selectedFeatures.size(); i++) {
						if (selectedMcfcClusteringOption == 0) {
							query += selectedFeatures.get(i) + ",";
						} else {
							query += "sum(" + selectedFeatures.get(i) + "),";
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
				//clusterer.renameAttributesOfInstances(selectedFeatures);
				state = "clustering_step3";
				break;
			case "clustering_step3":
				String algorithmParameters = "-N " 
						+ (int) viewObject.numberOfClustersSpinner.getValue()
						+ " -I " 
						+ (int) viewObject.maxNumberOfIterationsSpinner.getValue();
				clusterer.setOptions(algorithmParameters);
				clusterer.train();
				viewObject.algorithmOutputTextArea.setText(clusterer.evaluate());
				state = "clustering_step4";
				break;
			case "classification_step1":
				selectedFeatures = new ArrayList<String>();
				collectSelectedFeatures();
				query = "select ";
				for (int i = 0; i < selectedFeatures.size(); i++) {
					query += selectedFeatures.get(i) + ",";
				}
				state = "classification_step2";
				break;
			case "classification_step2":
				selectedFeatures.add(viewObject.targetLabelCombo.getSelectedItem().toString());
				query += selectedFeatures.get(selectedFeatures.size() - 1);
				query += " from " + selectedDataset;
				classifier.setInstanceQuery(query);
				classifier.fetchInstances();
				classifier.setTargetLabel(selectedFeatures.size() - 1);
				state = "classification_step3";
				break;
			case "classification_step3":
				classifier.train();
				state = "classification_step4";
				break;
			case "classification_step4":
				classifier.setEvaluationOption("CV");
				viewObject.algorithmOutputTextArea.setText(classifier.evaluate());
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
