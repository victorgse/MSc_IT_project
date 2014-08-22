package gui;

import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;

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
import visualisers.Interactive3dScatterPlot;
import visualisers.ROCcurvePlotter;
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
	private Interactive3dScatterPlot plot; //the plot object
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
	private ArrayList<String> selectedFeatures; //a list of the features selected by user
	private String query; //builds and stores the query with which instances will be requested
	private String algorithmResults;
	
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
	public ArrayList<String> getSelectedFeatures() {
		ArrayList<String> featuresSelectedByUser = new ArrayList<String>();
		for (int i = 0; i < viewObject.featureCheckBoxes.size(); i++) {
			if (viewObject.featureCheckBoxes.get(i).isSelected()) {
				featuresSelectedByUser.add(viewObject.featureCheckBoxes.get(i).getText());
			}
		}
		return featuresSelectedByUser;
	}
	
	/**
	 * @return the classifier
	 */
	public SVMClassifier getClassifier() {
		return classifier;
	}
	
	public void processVisualisationViewClosed() {
		new Thread(new Runnable() {
			public void run() {
				disposeVisualisationView();
				viewObject.toggleEndButtons(true, true, true);
			}
		}).start();
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
					plot.getInstanceInfoFrame().dispose();
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
						viewObject.disableAllComponentsOfContainer(viewObject.middlePanel);
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
				viewObject.disableAllComponentsOfContainer(viewObject.middlePanel);
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
						viewObject.disableAllComponentsOfContainer(viewObject.middlePanel);
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
				viewObject.disableAllComponentsOfContainer(viewObject.middlePanel);
				viewObject.setTextOfProgramStateLabel("Clustering (Step 2 of 3) - Training Clusterer...");
				int desiredNumberOfKMeansRuns = (int) viewObject.numberOfKMeansRunsSpinner.getValue();
				clusterer.train(desiredNumberOfKMeansRuns);
				viewObject.setTextOfProgramStateLabel("Clustering (Step 2 of 3) - Evaluating Clusterer...");
				clustererEvaluation = clusterer.evaluate();
				algorithmResults = clustererEvaluation.clusterResultsToString();
				viewObject.setTextOfAlgorithmOutputTextArea(algorithmResults);
				state = "clustering_step3";
				break;
			case "classification_step2":
				String targetLabel = viewObject.targetLabelCombo.getSelectedItem().toString();
				selectedFeatures.add(targetLabel);
				if (desiredLevelOfAnalysis == 0) {
					query += targetLabel;
					query += " from " + selectedDataset;
				} else if (desiredLevelOfAnalysis == 1) {
					query += "sum(" + targetLabel + ")";
					query += " from " + selectedDataset;
					query += " group by Player_ID";
				} else if (desiredLevelOfAnalysis == 2) {
					query += "sum(" + targetLabel + ")";
					query += " from " + selectedDataset;
					query += " group by Team";
				}
				classifier.setInstanceQuery(query);
				viewObject.toggleNavigationButtons(false, false, false);
				viewObject.disableAllComponentsOfContainer(viewObject.middlePanel);
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
				viewObject.disableAllComponentsOfContainer(viewObject.middlePanel);
				viewObject.setTextOfProgramStateLabel("Classification (Step 4 of 5) - Training Classifier...");
				classifier.train();
				classifier.setEvaluationOption(classifierEvaluationMethod);
				viewObject.setTextOfProgramStateLabel("Classification (Step 4 of 5) - Evaluating Classifier...");
				classifierEvaluation = classifier.evaluate();
				algorithmResults = classifierEvaluation.toSummaryString("===SVM===\n", false);
				try {
					algorithmResults += "\n" + classifierEvaluation.toClassDetailsString();
					algorithmResults += "\n" + classifierEvaluation.toMatrixString();
				} catch (Exception e) {}
				viewObject.setTextOfAlgorithmOutputTextArea(algorithmResults);
				state = "classification_step5";
				if (classifierEvaluationMethod.equals("CV")) {
					viewObject.toggleEndButtons(false, true, true);
				}
				break;
			case "outlierDetection_step2":
				double outlierFactor = (double) viewObject.outlierFactorSpinner.getValue();
				String OutlierDetectorParameters = "-O " + outlierFactor;
				outlierDetector.setOptions(OutlierDetectorParameters);
				viewObject.toggleNavigationButtons(false, false, false);
				viewObject.disableAllComponentsOfContainer(viewObject.middlePanel);
				viewObject.setTextOfProgramStateLabel("Outlier Detection (Step 2 of 3) - Training Outlier-detector...");
				outlierDetector.train();
				viewObject.setTextOfProgramStateLabel("Outlier Detection (Step 2 of 3) - Evaluating Outlier-detector...");
				outlierDetectorEvaluation = outlierDetector.evaluate();
				viewObject.setTextOfAlgorithmOutputTextArea(outlierDetectorEvaluation.resultsToString());
				state = "outlierDetection_step3";
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
	 * Processes clicks on the "Visualise Results" button.
	 */
	private void processVisualiseResultsButtonClick() {
		viewObject.toggleEndButtons(false, false, false);
		viewObject.toggleNavigationButtons(false, false, false);
		switch (state) {
			case "clustering_step3":
				viewObject.setTextOfProgramStateLabel("Clustering (Step 3 of 3) - Generating Visualisation...");
				break;
			case "classification_step5":
				viewObject.setTextOfProgramStateLabel("Classification (Step 5 of 5) - Generating Visualisation...");
				break;
			case "outlierDetection_step3":
				viewObject.setTextOfProgramStateLabel("Outlier Detection (Step 3 of 3) - Generating Visualisation...");
				break;
		}
		processActualisePlotButtonClick();
		viewObject.toggleEndButtons(false, false, true);
		viewObject.toggleNavigationButtons(true, true, false);
		switch (state) {
			case "clustering_step3":
				viewObject.setTextOfProgramStateLabel("Clustering (Step 3 of 3) - Displaying Results");
				break;
			case "classification_step5":
				viewObject.setTextOfProgramStateLabel("Classification (Step 5 of 5) - Displaying Results");
				break;
			case "outlierDetection_step3":
				viewObject.setTextOfProgramStateLabel("Outlier Detection (Step 3 of 3) - Displaying Results");
				break;
		}
	}
	
	/**
	 * Processes clicks on the "Plot ROC curve" button.
	 */
	private void processPlotROCcurveButtonClick() {
		ROCcurvePlotter plotter = new ROCcurvePlotter();
		plotter.plotROCcurve(classifierEvaluation);
	}
	
	/**
	 * Processes clicks on the "Plot ROC curve" button.
	 */
	private void processSaveResultsButtonClick() {
		
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
				classLabels = new String[clustererEvaluation.getNumClusters()];
				for (int i = 0; i < clustererEvaluation.getNumClusters(); i++) {
					int clusterNumber = i;
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
				if (visualisationViewObject == null) {
					for (int i = 0; i < instances.numInstances(); i++) { //so that the clustering, classification, and outlier detection classes are all on the same scale
						actualClassAssignments[i] -= 1;
						predictedClassAssignments[i] -= 1;
					}
				}
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
			plot = new Interactive3dScatterPlot(selectedDataset,axeLabels, coordinates, 
					actualClassAssignments, predictedClassAssignments, 
					instances, namesOfInstances, classLabels);
			if (instances.numAttributes() < 3) {
				plot.getChart().getView().setViewPositionMode(ViewPositionMode.TOP);
			}
			visualisationViewObject = new VisualisationView(this, plot, instances, classLabels);
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
				} else if (AE.getSource() == viewObject.visualiseResultsButton) {
					processVisualiseResultsButtonClick();
				} else if (AE.getSource() == viewObject.plotROCcurveButton) {
					processPlotROCcurveButtonClick();
				} else if (AE.getSource() == viewObject.saveResultsButton) {
					processSaveResultsButtonClick();
				} else if (AE.getSource() == visualisationViewObject.actualisePlotButton) {
					processActualisePlotButtonClick();
				}
			}
		}).start();
	}

}
