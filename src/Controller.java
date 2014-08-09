import java.awt.event.*;
import java.io.File;
import java.util.TreeSet;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jzy3d.plot3d.rendering.view.modes.ViewPositionMode;

import visualisers.PickablePointsScatter3D;
import weka.classifiers.Evaluation;
import weka.clusterers.ClusterEvaluation;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import algorithms.KMeansClusterer;
import algorithms.OutlierDetector;
import algorithms.OutlierEvaluation;
import algorithms.SVMClassifier;

public class Controller implements ActionListener {
	
	/**
	 * instance variables
	 */
	private View viewObject; //the main view object
	private VisualisationView visualisationViewObject; //the visualisation view object
	private PickablePointsScatter3D plot;
	private String state; //keeps track of what state the program is in
	private String selectedDataset; //the name of the dataset that has been selected by user
	private KMeansClusterer clusterer; //the clustering object
	private ClusterEvaluation clustererEvaluation; //the clusterer evaluation object
	private SVMClassifier classifier; //the classifier object
	private Evaluation classifierEvaluation; //the classifier evaluation object
	private String classifierEvaluationMethod; //the desired classifier evaluation method
	private OutlierDetector outlierDetector; //the outlier detector object
	private int desiredLevelOfAnalysis; //tracks the desired level of analysis for the MCFC Analytics Full Dataset
	private TreeSet<String> selectedFeatures; //a list of the features selected by user
	private String query; //builds and stores the query with which instances will be requested
	private OutlierEvaluation outlierEvaluation;
	
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
		TreeSet<String> featuresSelectedByUser = new TreeSet<String>();
		for (int i = 0; i < viewObject.features.length; i++) {
			if (viewObject.features[i].isSelected()) {
				featuresSelectedByUser.add(viewObject.features[i].getText());
			}
		}
		return featuresSelectedByUser;
	}

	/**
	 * A helper method for fetching an .xls file selected by user.
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
	 * A helper method for processing clicks of the "Start Over" button.
	 */
	private void processStartOverButtonClick() {
		if (visualisationViewObject != null) {
			visualisationViewObject.dispose();
		}
		visualisationViewObject = null;
		state = "startScreen_1";
		viewObject.updateView(state);
	}
	
	/**
	 * A helper method for processing clicks of the "Back" button.
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
					visualisationViewObject.dispose();
				}
				visualisationViewObject = null;
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
					visualisationViewObject.dispose();
				}
				visualisationViewObject = null;
				state = "classification_step4";
				break;
			case "outlierDetection_step2":
				state = "outlierDetection_step1";
				break;
			case "outlierDetection_step3":
				if (visualisationViewObject != null) {
					visualisationViewObject.dispose();
				}
				visualisationViewObject = null;
				state = "outlierDetection_step2";
				break;
		}
		viewObject.updateView(state);
	}
	
	/**
	 * A helper method for processing clicks of the "Next" button.
	 */
	private void processNextButtonClick() {
		switch (state) {
			case "startScreen_1":
				if (viewObject.mcfcAnalyticsFullDatasetButton.isSelected()) {
					selectedDataset = "MCFC_ANALYTICS_FULL_DATASET";
					state = "startScreen_2";
				} else if (viewObject.otherDatasetButton.isSelected()) {
					File selectedFile = getFile();
					try {
						new DatasetLoader(selectedFile);
						state = "startScreen_3";
					} catch (Exception e) {
						System.out.println("Error: Dataset could not be loaded");
					}
				}
				/*
				for (int i = 0; i < viewObject.availableDatasetsButtons.length; i++) {
					if (viewObject.availableDatasetsButtons[i].isSelected()) {
						selectedDataset = viewObject.availableDatasetsButtons[i].getText();
					}
				}
				if (viewObject.otherDatasetButton.isSelected()) {
					File selectedFile = getFile();
					try {
						DatasetLoader datasetLoader = new DatasetLoader(selectedFile);
						selectedDataset = datasetLoader.getDatasetName();
						state = "startScreen_2";
					} catch (Exception e) {
						System.out.println("Error: Dataset could not be loaded");
					}
				}
				state = "startScreen_2";
				*/
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
				break;
			case "clustering_step1":
			case "classification_step1":
			case "outlierDetection_step1":
				selectedFeatures = getSelectedFeatures();
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
					if (state.equals("clustering_step1")) {
						clusterer.setInstanceQuery(query);
						clusterer.fetchInstances();
						clusterer.renameAttributesOfInstances(selectedFeatures);
						state = "clustering_step2";
					} else if (state.equals("outlierDetection_step1")) {
						outlierDetector.setInstanceQuery(query);
						outlierDetector.fetchInstances();
						outlierDetector.renameAttributesOfInstances(selectedFeatures);
						state = "outlierDetection_step2";
					}
				} else if (state.equals("classification_step1")) {
					state = "classification_step2";
				}
				break;
			case "clustering_step2":
				int desiredNumberOfClusters = (int) viewObject.numberOfClustersSpinner.getValue();
				int desiredNumberOfKMeansRuns = (int) viewObject.numberOfKMeansRunsSpinner.getValue();
				String KMeansClustererParameters = "-N " + desiredNumberOfClusters;
				clusterer.setOptions(KMeansClustererParameters);
				clusterer.train(desiredNumberOfKMeansRuns);
				clustererEvaluation = clusterer.evaluate();
				viewObject.algorithmOutputTextArea.setText(clustererEvaluation.clusterResultsToString());
				state = "clustering_step3";
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
				classifier.setInstanceQuery(query);
				classifier.fetchInstances();
				classifier.renameAttributesOfInstances(selectedFeatures);
				classifier.setTargetLabel(selectedFeatures.size() - 1);
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
				classifier.setEvaluationOption(classifierEvaluationMethod);
				classifier.train();
				classifierEvaluation = classifier.evaluate();
				viewObject.algorithmOutputTextArea.setText(classifierEvaluation.toSummaryString());
				state = "classification_step5";
				if (!classifierEvaluationMethod.equals("CV")) {
					processActualisePlotButtonClick();
				}
				break;
			case "outlierDetection_step2":
				double outlierFactor = (double) viewObject.outlierFactorSpinner.getValue();
				String OutlierDetectorParameters = "-O " + outlierFactor;
				outlierDetector.setOptions(OutlierDetectorParameters);
				outlierDetector.train();
				outlierEvaluation = outlierDetector.evaluate();
				//viewObject.algorithmOutputTextArea.setText(clustererEvaluation.clusterResultsToString());
				state = "outlierDetection_step3";
				processActualisePlotButtonClick();
				break;
		}
		viewObject.updateView(state);
	}
	
	private void processActualisePlotButtonClick() {
		Instances instances = null;
		String[] axeLabels;
		double[][] coordinates;
		double[] actualClassAssignments = null;
		double[] predictedClassAssignments = null;
		String[] classLabels = null;
		switch (state) {
			case "clustering_step3":
				instances = clusterer.getTrainingSet();
				actualClassAssignments = clustererEvaluation.getClusterAssignments();
				for (int i = 0; i < instances.numInstances(); i++) {
					actualClassAssignments[i] += 1;
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
				    remove.setAttributeIndices("last");
				    remove.setInvertSelection(false);
					remove.setInputFormat(instances);
					instances = Filter.useFilter(instances, remove);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case "outlierDetection_step3":
				instances = outlierDetector.getTrainingSet();
				actualClassAssignments = outlierEvaluation.getClassAssignments();
				for (int i = 0; i < instances.numInstances(); i++) {
					actualClassAssignments[i] += 1;
				}
				classLabels = new String[2];
				classLabels[0] = "Normal";
				classLabels[1] = "Outlier";
				break;
		}
		if (visualisationViewObject == null) {
			axeLabels = new String[3];
			for (int m = 0; m < 3; m++) {
				axeLabels[m] = instances.attribute(m).name();
			}
			coordinates = new double[instances.numInstances()][3];
			for (int i = 0; i < instances.numInstances(); i++) {
				for (int j = 0; j < 3; j++) {
					coordinates[i][j] = instances.get(i).value(j);
				}
				//coordinates[i][1] = 0;
				//coordinates[i][2] = 0;
			}
			plot = new PickablePointsScatter3D(axeLabels, coordinates, 
					actualClassAssignments, predictedClassAssignments);
			//plot.getChart().getView().setViewPositionMode(ViewPositionMode.TOP);
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
				coordinates[i][0] = instances.get(i).value(visualisationViewObject.xAxisCombo.getSelectedIndex());
				coordinates[i][1] = instances.get(i).value(visualisationViewObject.yAxisCombo.getSelectedIndex());
				coordinates[i][2] = instances.get(i).value(visualisationViewObject.zAxisCombo.getSelectedIndex());
			}
			plot.updatePoints(coordinates);
		}
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
		} else if (ae.getSource() == visualisationViewObject.actualisePlotButton) {
			processActualisePlotButtonClick();
		}
	}

}
