package gui;

import java.awt.event.*;
import java.io.File;
import java.util.TreeSet;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jzy3d.plot3d.rendering.view.modes.ViewPositionMode;

import algorithms.classification.SVMClassifier;
import algorithms.clustering.KMeansClusterer;
import algorithms.outliers.OutlierDetector;
import algorithms.outliers.OutlierEvaluation;
import dbtools.DatabaseAccess;
import dbtools.DatasetDatabaseLoader;
import visualisers.PickablePointsScatter3D;
import weka.classifiers.Evaluation;
import weka.clusterers.ClusterEvaluation;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class Controller implements ActionListener {
	
	/**
	 * instance variables
	 */
	private MainView viewObject; //the main view object
	private VisualisationView visualisationViewObject; //the visualisation view object
	private PickablePointsScatter3D plot; //the plot object
	private String state; //keeps track of what state the program is in
	private String selectedDataset; //the name of the dataset that has been selected by user
	private int desiredLevelOfAnalysis; //tracks the desired level of analysis for the MCFC Analytics Full Dataset
	private KMeansClusterer clusterer; //the clusterer object
	private ClusterEvaluation clustererEvaluation; //the clusterer evaluation object
	private SVMClassifier classifier; //the classifier object
	private Evaluation classifierEvaluation; //the classifier evaluation object
	private String classifierEvaluationMethod; //the desired classifier evaluation method
	private OutlierDetector outlierDetector; //the outlierDetector object
	private OutlierEvaluation outlierDetectorEvaluation; //the outlierDetector evaluation object
	private TreeSet<String> selectedFeatures; //a list of the features selected by user
	private String query; //builds and stores the query with which instances will be requested
	
	/**
	 * Constructor
	 */
	public Controller() {
		state = "startScreen_1";
	}
	
	/**
	 * Setter of viewObject.
	 * @param view
	 */
	public void setView(MainView view) {
		viewObject = view;
	}

	/**
	 * Getter for the state of the application.
	 * @return the state
	 */
	public String getState() {
		return state;
	}
	
	/**
	 * Getter for the name of the selected dataset.
	 * @return the selectedDataset
	 */
	public String getSelectedDataset() {
		return selectedDataset;
	}

	/**
	 * Getter for the features selected by user.
	 * @return the selectedFeatures
	 */
	public TreeSet<String> getSelectedFeatures() {
		TreeSet<String> featuresSelectedByUser = new TreeSet<String>();
		for (int i = 0; i < viewObject.featureCheckBoxes.size(); i++) {
			if (viewObject.featureCheckBoxes.get(i).isSelected()) {
				featuresSelectedByUser.add(viewObject.featureCheckBoxes.get(i).getText());
			}
		}
		return featuresSelectedByUser;
	}

	/**
	 * Fetches an .xls file selected by user.
	 * @return
	 */
	private File getFile() {
		JFileChooser fileChooser = new JFileChooser();
	    FileNameExtensionFilter filter = new FileNameExtensionFilter(
	        "Excel .xls files", "xls");
	    fileChooser.setFileFilter(filter);
	    fileChooser.setAcceptAllFileFilterUsed(false);
	    int returnVal = fileChooser.showOpenDialog(viewObject);
	    if (returnVal == JFileChooser.APPROVE_OPTION) {
	    	return fileChooser.getSelectedFile();
	    } else {
	    	return null;
	    }
	}
	
	/**
	 * Disposes the visualisationView object.
	 */
	private void disposeVisualisationView() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					visualisationViewObject.dispose();
					visualisationViewObject = null;
					plot.instanceInfoFrame.dispose();
					plot.instanceInfoFrame = null;
				}
			});
		} catch (Exception e) {}
	}

	/**
	 * Processes clicks on the "Start Over" button.
	 */
	private void processStartOverButtonClick() {
		if (visualisationViewObject != null) {
			disposeVisualisationView();
		}
		state = "startScreen_1";
		viewObject.updateView(state);
	}
	
	/**
	 * Processes clicks on the "Back" button.
	 */
	private void processBackButtonClick() {
		switch (state) {
			case "startScreen_2":
				state = "startScreen_1";
				break;
			case "startScreen_3":
				if (selectedDataset.equals("MCFC_ANALYTICS_FULL_DATASET")) {
					state = "startScreen_2";
				} else {
					state = "startScreen_1";
				}
				break;
			case "clustering_step1":
			case "classification_step1":
			case "outlierDetection_step1":
				state = "startScreen_3";
				break;
			case "clustering_step2":
				state = "clustering_step1";
				break;
			case "clustering_step3":
				if (visualisationViewObject != null) {
					disposeVisualisationView();
				}
				state = "clustering_step2";
				break;
			case "classification_step2":
				state = "classification_step1";
				break;
			case "classification_step3":
				state = "classification_step1";
				break;
			case "classification_step4":
				state = "classification_step3";
				break;
			case "classification_step5":
				if (visualisationViewObject != null) {
					disposeVisualisationView();
				}
				state = "classification_step4";
				break;
			case "outlierDetection_step2":
				state = "outlierDetection_step1";
				break;
			case "outlierDetection_step3":
				if (visualisationViewObject != null) {
					disposeVisualisationView();
				}
				state = "outlierDetection_step2";
				break;
		}
		viewObject.updateView(state);
	}
	
	/**
	 * Processes clicks on the "Next" button.
	 */
	private void processNextButtonClick() {
		switch (state) {
			case "startScreen_1":
				if (viewObject.mcfcAnalyticsFullDatasetButton.isSelected()) {
					selectedDataset = "MCFC_ANALYTICS_FULL_DATASET";
					state = "startScreen_2";
				} else if (viewObject.otherDatasetButton.isSelected()) {
					File selectedFile = getFile();
					if (selectedFile != null) {
						viewObject.toggleNavigationButtons(false, false, false);
						viewObject.setTextOfProgramStateLabel("Initial Setup Screen - Inserting dataset into the database...");
						DatasetDatabaseLoader datasetDatabaseLoader = new DatasetDatabaseLoader();
						boolean datasetSuccessfullyInsertedIntoDatabase = datasetDatabaseLoader.insertDatasetIntoDatabase(selectedFile);
						if (datasetSuccessfullyInsertedIntoDatabase) {
							selectedDataset = datasetDatabaseLoader.getNameOfDataset();
							state = "startScreen_3";
						}
						selectedFile = null;
					}
				} else {
					for (int i = 0; i < viewObject.otherAvailableDatasetsButtons.length; i++) {
						if (viewObject.otherAvailableDatasetsButtons[i].isSelected()) {
							selectedDataset = viewObject.otherAvailableDatasetsButtons[i].getText();
							state = "startScreen_3";
						}
					}
				}
				break;
			case "startScreen_2":
				desiredLevelOfAnalysis = viewObject.levelOfAnalysisCombo.getSelectedIndex();
				state = "startScreen_3";
				break;
			case "startScreen_3":
				if (viewObject.clusteringButton.isSelected()) {
					clusterer = new KMeansClusterer();
					state = "clustering_step1";
				} else if (viewObject.classificationButton.isSelected()) {
					classifier = new SVMClassifier();
					state = "classification_step1";
				} else if (viewObject.outlierDetectionButton.isSelected()) {
					outlierDetector = new OutlierDetector();
					state = "outlierDetection_step1";
				}
				viewObject.toggleNavigationButtons(false, false, false);
				viewObject.setTextOfProgramStateLabel("Initial Setup Screen - Fetching a List of Available Features...");
				break;
			case "clustering_step1":
			case "classification_step1":
			case "outlierDetection_step1":
				selectedFeatures = getSelectedFeatures();
				if (selectedFeatures.size() < 1) {
					JOptionPane.showMessageDialog(null, 
			    			"You must select at least 1 feature.", 
			    			"Error: No Features Selected", JOptionPane.ERROR_MESSAGE);
				} else {
					query = "select ";
					for (String feature : selectedFeatures) {
						if ((!selectedDataset.equals("MCFC_ANALYTICS_FULL_DATASET")) 
								|| (selectedDataset.equals("MCFC_ANALYTICS_FULL_DATASET") 
								&& desiredLevelOfAnalysis == 0)) {
							query += feature + ",";
						} else if (selectedDataset.equals("MCFC_ANALYTICS_FULL_DATASET") 
								&& desiredLevelOfAnalysis != 0) {
							query += "sum(" + feature + "),";
						}
					}
					if (state.equals("clustering_step1") || state.equals("outlierDetection_step1")) {
						query = query.substring(0, query.length()-1);
						query += " from " + selectedDataset;
						if (desiredLevelOfAnalysis == 1) {
							query += " group by Player_ID";
						} else if (desiredLevelOfAnalysis == 2) {
							query += " group by Team";
						}
						viewObject.toggleNavigationButtons(false, false, false);
						if (state.equals("clustering_step1")) {
							clusterer.setInstanceQuery(query);
							viewObject.setTextOfProgramStateLabel("Clustering (Step 1 of 3) - Fetching Instances...");
							clusterer.fetchInstances();
							clusterer.renameAttributesOfInstances(selectedFeatures);
							if (viewObject.scaleAndMeanNormaliseFeatures.isSelected()) {
								clusterer.scaleAndMeanNormaliseFeatures();
							}
							state = "clustering_step2";
						} else if (state.equals("outlierDetection_step1")) {
							outlierDetector.setInstanceQuery(query);
							viewObject.setTextOfProgramStateLabel("Outlier Detection (Step 1 of 3) - Fetching Instances...");
							outlierDetector.fetchInstances();
							outlierDetector.renameAttributesOfInstances(selectedFeatures);
							state = "outlierDetection_step2";
						}
					} else if (state.equals("classification_step1")) {
						state = "classification_step2";
					}
				}
				break;
			case "clustering_step2":
				int desiredNumberOfClusters = (int) viewObject.numberOfClustersSpinner.getValue();
				String KMeansClustererParameters = "-N " + desiredNumberOfClusters;
				clusterer.setOptions(KMeansClustererParameters);
				viewObject.toggleNavigationButtons(false, false, false);
				viewObject.setTextOfProgramStateLabel("Clustering (Step 2 of 3) - Training Clusterer...");
				int desiredNumberOfKMeansRuns = (int) viewObject.numberOfKMeansRunsSpinner.getValue();
				clusterer.train(desiredNumberOfKMeansRuns);
				viewObject.setTextOfProgramStateLabel("Clustering (Step 2 of 3) - Evaluating Clusterer...");
				clustererEvaluation = clusterer.evaluate();
				state = "clustering_step3";
				viewObject.setTextOfAlgorithmOutputTextArea(clustererEvaluation.clusterResultsToString());
				viewObject.setTextOfProgramStateLabel("Clustering (Step 2 of 3) - Generating Visualisation...");
				processActualisePlotButtonClick();
				break;
			case "classification_step2":
				selectedFeatures.add(viewObject.targetLabelCombo.getSelectedItem().toString());
				if (desiredLevelOfAnalysis == 0) {
					query += viewObject.targetLabelCombo.getSelectedItem().toString();
					query += " from " + selectedDataset;
				} else if (desiredLevelOfAnalysis == 1) {
					query += "sum(" + viewObject.targetLabelCombo.getSelectedItem().toString() + ")";
					query += " from " + selectedDataset;
					query += " group by Player_ID";
				} else if (desiredLevelOfAnalysis == 2) {
					query += "sum(" + viewObject.targetLabelCombo.getSelectedItem().toString() + ")";
					query += " from " + selectedDataset;
					query += " group by Team";
				}
				viewObject.toggleNavigationButtons(false, false, false);
				classifier.setInstanceQuery(query);
				viewObject.setTextOfProgramStateLabel("Classification (Step 2 of 5) - Fetching Instances...");
				classifier.fetchInstances();
				classifier.renameAttributesOfInstances(selectedFeatures);
				classifier.setTargetLabel(selectedFeatures.size() - 1);
				if (viewObject.scaleAndMeanNormaliseFeatures.isSelected()) {
					classifier.scaleAndMeanNormaliseFeatures();
				}
				state = "classification_step3";
				break;
			case "classification_step3":
				String selectedKernel = (String) viewObject.kernelTypeCombo.getSelectedItem();
				double regularisation = (double) viewObject.regularisationSpinner.getValue();
				double gamma = (double) viewObject.gammaSpinner.getValue();
				int kernel;
				if (selectedKernel.equals("Linear Kernel")) {
					kernel = 0; //0 represents linear kernel in LibSVM
				} else { //if not "Linear Kernel", then it must be "Gaussian Kernel"
					kernel = 2; //2 represents gaussian kernel in LibSVM
				}
				String SVMClassifierParameters = "-K " + kernel;
				if (selectedKernel.equals("Gaussian Kernel")) {
					SVMClassifierParameters += " -C " + regularisation + " -G " + gamma;
				}
				classifier.setOptions(SVMClassifierParameters);
				state = "classification_step4";
				break;
			case "classification_step4":
				if (viewObject.trainingSetButton.isSelected()) {
					classifierEvaluationMethod = "trainingSet";
				} else if (viewObject.crossValidationButton.isSelected()) {
					classifierEvaluationMethod = "CV";
				}  else if (viewObject.percentageSplitButton.isSelected()) {
					classifierEvaluationMethod = "testSet";
					classifier.splitDataset(0.70);
				}
				viewObject.toggleNavigationButtons(false, false, false);
				viewObject.setTextOfProgramStateLabel("Classification (Step 4 of 5) - Training Classifier...");
				classifier.train();
				classifier.setEvaluationOption(classifierEvaluationMethod);
				viewObject.setTextOfProgramStateLabel("Classification (Step 4 of 5) - Evaluating Classifier...");
				classifierEvaluation = classifier.evaluate();
				viewObject.setTextOfAlgorithmOutputTextArea(classifierEvaluation.toSummaryString());
				state = "classification_step5";
				if (!classifierEvaluationMethod.equals("CV")) {
					viewObject.setTextOfProgramStateLabel("Classification (Step 4 of 5) - Generating Visualisation...");
					processActualisePlotButtonClick();
				}
				break;
			case "outlierDetection_step2":
				double outlierFactor = (double) viewObject.outlierFactorSpinner.getValue();
				String OutlierDetectorParameters = "-O " + outlierFactor;
				outlierDetector.setOptions(OutlierDetectorParameters);
				viewObject.toggleNavigationButtons(false, false, false);
				viewObject.setTextOfProgramStateLabel("Outlier Detection (Step 2 of 3) - Training Outlier-detector...");
				outlierDetector.train();
				viewObject.setTextOfProgramStateLabel("Outlier Detection (Step 2 of 3) - Evaluating Outlier-detector...");
				outlierDetectorEvaluation = outlierDetector.evaluate();
				//viewObject.setTextOfAlgorithmOutputTextArea(outlierDetectorEvaluation.resultsToString());
				state = "outlierDetection_step3";
				viewObject.setTextOfProgramStateLabel("Outlier Detection (Step 2 of 3) - Generating Visualisation...");
				processActualisePlotButtonClick();
				break;
		}
		viewObject.updateView(state);
	}
	
	/**
	 * Processes clicks on the "Delete Dataset" button.
	 */
	private void processDeleteDatasetButtonClick() {
		DatabaseAccess dbAccess = new DatabaseAccess();
		String nameOfDatasetToDelete = viewObject.datasetToDeleteCombo.getSelectedItem().toString();
		dbAccess.deleteDatasetFromDatabase(nameOfDatasetToDelete);
		viewObject.updateView(state);
	}
	
	/**
	 * Processes clicks on the "Actualise Plot" button.
	 */
	private void processActualisePlotButtonClick() {
		Instances instances = null;
		String[] axeLabels;
		double[][] coordinates;
		double[] actualClassAssignments = null;
		double[] predictedClassAssignments = null;
		String[] namesOfInstances = null;
		String[] classLabels = null;
		switch (state) {
			case "clustering_step3":
				instances = clusterer.getTrainingSet();
				actualClassAssignments = clustererEvaluation.getClusterAssignments();
				if (visualisationViewObject == null) {
					for (int i = 0; i < instances.numInstances(); i++) {
						actualClassAssignments[i] += 1;
					}
				}
				classLabels = new String[clustererEvaluation.getNumClusters()];
				for (int i = 0; i < clustererEvaluation.getNumClusters(); i++) {
					int clusterNumber = i + 1;
					classLabels[i] = "Cluster " + clusterNumber;
				}
				break;
			case "classification_step5":
				if (classifierEvaluationMethod.equals("trainingSet")) {
					instances = classifier.getTrainingSet();
				} else if (classifierEvaluationMethod.equals("testSet")) {
					instances = classifier.getTestSet();
				}
				actualClassAssignments = classifier.getActualClassAssignments();
				predictedClassAssignments = classifier.getPredictedClassAssignments();
				classLabels = new String[instances.numClasses()];
				for (int i = 0; i < instances.numClasses(); i++) {
					classLabels[i] = instances.classAttribute().value(i) + " " + instances.classAttribute().name();
				}
				// remove class attribute
				try {
					Remove remove = new Remove();
				    remove.setAttributeIndices(String.valueOf(instances.classIndex() + 1));
				    remove.setInvertSelection(false);
					remove.setInputFormat(instances);
					instances = Filter.useFilter(instances, remove);
				} catch (Exception e) {}
				break;
			case "outlierDetection_step3":
				instances = outlierDetector.getTrainingSet();
				actualClassAssignments = outlierDetectorEvaluation.getClassAssignments();
				for (int i = 0; i < instances.numInstances(); i++) {
					actualClassAssignments[i] += 1;
				}
				classLabels = new String[2];
				classLabels[0] = "Normal";
				classLabels[1] = "Outlier";
				break;
		}
		if (visualisationViewObject == null) {
			if (instances.numAttributes() >= 3) {
				axeLabels = new String[3];
				for (int m = 0; m < 3; m++) {
					axeLabels[m] = instances.attribute(m).name();
				}
			} else {
				axeLabels = new String[instances.numAttributes()];
				for (int m = 0; m < instances.numAttributes(); m++) {
					axeLabels[m] = instances.attribute(m).name();
				}
			}
			coordinates = new double[instances.numInstances()][3];
			for (int i = 0; i < instances.numInstances(); i++) {
				if (instances.numAttributes() >= 3) {
					for (int j = 0; j < 3; j++) {
						coordinates[i][j] = instances.get(i).value(j);
					}
				} else if (instances.numAttributes() == 2) {
					coordinates[i][0] = instances.get(i).value(0);
					coordinates[i][1] = instances.get(i).value(1);
					coordinates[i][2] = 0;
				} else if (instances.numAttributes() == 1) {
					coordinates[i][0] = instances.get(i).value(0);
					coordinates[i][1] = 0;
					coordinates[i][2] = 0;
				}
			}
			DatabaseAccess dbAccess = new DatabaseAccess();
			if (selectedDataset.equals("MCFC_ANALYTICS_FULL_DATASET")) {
				namesOfInstances = dbAccess.getNamesOfFullDatasetInstances(desiredLevelOfAnalysis, instances.numInstances());
			}
			plot = new PickablePointsScatter3D(selectedDataset,axeLabels, coordinates, 
					actualClassAssignments, predictedClassAssignments, 
					instances, namesOfInstances, classLabels);
			if (instances.numAttributes() < 3) {
				plot.getChart().getView().setViewPositionMode(ViewPositionMode.TOP);
			}
			visualisationViewObject = new VisualisationView(this, plot, instances, classLabels);
			visualisationViewObject.setVisible(true);
		} else {
			axeLabels = new String[3];
			axeLabels[0] = (String) visualisationViewObject.xAxisCombo.getSelectedItem();
			axeLabels[1] = (String) visualisationViewObject.yAxisCombo.getSelectedItem();
			axeLabels[2] = (String) visualisationViewObject.zAxisCombo.getSelectedItem();
			plot.renameAxes(axeLabels);
			coordinates = new double[instances.numInstances()][3];
			for (int i = 0; i < instances.numInstances(); i++) {
				if (instances.numAttributes() >= 3) {
					coordinates[i][0] = instances.get(i).value(visualisationViewObject.xAxisCombo.getSelectedIndex());
					coordinates[i][1] = instances.get(i).value(visualisationViewObject.yAxisCombo.getSelectedIndex());
					coordinates[i][2] = instances.get(i).value(visualisationViewObject.zAxisCombo.getSelectedIndex());
				} else if (instances.numAttributes() == 2) {
					coordinates[i][0] = instances.get(i).value(visualisationViewObject.xAxisCombo.getSelectedIndex());
					coordinates[i][1] = instances.get(i).value(visualisationViewObject.yAxisCombo.getSelectedIndex());
					coordinates[i][2] = 0;
				} else if (instances.numAttributes() == 1) {
					coordinates[i][0] = instances.get(i).value(visualisationViewObject.xAxisCombo.getSelectedIndex());
					coordinates[i][1] = 0;
					coordinates[i][2] = 0;
				}
			}
			plot.updatePoints(coordinates);
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent ae) {
		final ActionEvent AE = ae;
		new Thread(new Runnable() {
			public void run() {
				if (AE.getSource() == viewObject.startOverButton) {
					processStartOverButtonClick();
				} else if (AE.getSource() == viewObject.backButton) {
					processBackButtonClick();
				} else if (AE.getSource() == viewObject.nextButton) {
					processNextButtonClick();
				} else if (AE.getSource() == viewObject.deleteDatasetButton) {
					processDeleteDatasetButtonClick();
				} else if (AE.getSource() == visualisationViewObject.actualisePlotButton) {
					processActualisePlotButtonClick();
				}
			}
		}).start();
	}

}
