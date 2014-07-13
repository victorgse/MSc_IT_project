import java.awt.event.*;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import weka.core.Instances;
import algorithms.KMeansClusterer;

public class Controller implements ActionListener {
	
	// instance variables
	private View viewObject;
	private String state;
	private String selectedDataset;
	private KMeansClusterer clusterer;
	private String query;
	
	public Controller() {
		state = "startScreen_1";
	}
	
	public void setView(View view) {
		viewObject = view;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}
	
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

	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == viewObject.startOverButton) {
			state = "startScreen_1";
			viewObject.updateView(state);
		} else if (ae.getSource() == viewObject.backButton) {
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
				case "classification_step6":
					state = "classification_step5";
					break;
				case "classification_step7":
					state = "classification_step6";
					break;
				case "classification_step8":
					state = "classification_step7";
					break;
			}
			viewObject.updateView(state);
		} else if (ae.getSource() == viewObject.nextButton) {
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
						state = "classification_step1";
					} else if (viewObject.anomalyDetectionButton.isSelected()) {
						state = "anomalyDetection_step1";
					}
					break;
				case "clustering_step1":
					state = "clustering_step2";
					break;
				case "clustering_step2":
					query = "select sum(goals), sum(assists)"
							+ " from " + selectedDataset
							+ " group by team";
					clusterer.setInstanceQuery(query);
					clusterer.fetchInstances();
					state = "clustering_step3";
					break;
				case "clustering_step3":
					String algorithmParameters = "-N " 
							+ (int) viewObject.numberOfClustersSpinner.getValue()
							+ " -I " 
							+ (int) viewObject.numberOfIterationsSpinner.getValue();
					clusterer.setOptions(algorithmParameters);
					clusterer.train();
					clusterer.evaluate();
					state = "clustering_step4";
					break;
				case "classification_step1":
					state = "classification_step2";
					break;
				case "classification_step2":
					state = "classification_step3";
					break;
				case "classification_step3":
					state = "classification_step4";
					break;
				case "classification_step4":
					if (viewObject.autoModelSelectionButton.isSelected()) {
						state = "classification_step7";
					} else if (viewObject.manualModelSelectionButton.isSelected()) {
						state = "classification_step5";
					}
					break;
				case "classification_step5":
					state = "classification_step6";
					break;
				case "classification_step6":
					state = "classification_step7";
					break;
				case "classification_step7":
					state = "classification_step8";
					break;
			}
			viewObject.updateView(state);
		}
	}

}
