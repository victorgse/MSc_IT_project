package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.border.*;

import dbtools.DatabaseAccess;

/**
 * Defines a GUI.
 */
@SuppressWarnings("serial")
public class MainView extends JFrame {
	
	/**
	 * instance variables
	 */
	private Controller controllerObject; //a reference to the controller object
	private DatabaseAccess dbAccess; //object used for querying the database
	private boolean topInitiated, middleInitiated, bottomInitiated; //have the main JPanels been initialised?
	JPanel topPanel, middlePanel, bottomPanel; //the panels for the three areas of the GUI
	JPanel programStatePanel; //the panel that holds the programState label
	JLabel infoLabel, programStateLabel; //the info and programState labels
	JButton startOverButton, backButton, nextButton; //the buttons of bottomPanel
	JRadioButton mcfcAnalyticsFullDatasetButton, otherDatasetButton; //radio buttons for selecting a dataset
	JRadioButton[] otherAvailableDatasetsButtons; //radio buttons for selecting a dataset
	JComboBox<String> datasetToDeleteCombo;
	JButton deleteDatasetButton;
	JRadioButton clusteringButton, classificationButton, outlierDetectionButton; //the radio buttons for selecting a task
	JRadioButton trainingSetButton, percentageSplitButton, crossValidationButton; //the radio buttons for selecting a testing option for the clusterer
	JComboBox<String> levelOfAnalysisCombo; //combo box for specifying the desired level of analysis for the MCFC Analytics Full Dataset
	ArrayList<String> availableFeatures; //stores the dataset's numeric fields.
	ArrayList<JCheckBox> featureCheckBoxes; //check boxes allowing the user to select features
	JCheckBox scaleAndMeanNormaliseFeatures; //check box allowing the user to request feature scaling and mean normalisation
	JComboBox<String> targetLabelCombo; //combo box for selecting a target label for the SVM classifier
	JSpinner numberOfClustersSpinner, numberOfKMeansRunsSpinner; //spinners for K-Means' options
	JComboBox<String> kernelTypeCombo; //combo box for choosing a kernel for the SVM classifier
	JSpinner regularisationSpinner, gammaSpinner; //spinners for some of the SVM's options
	JLabel regularisationLabel, gammaLabel; //labels describing the regularisation and gamma spinners
	JSpinner outlierFactorSpinner; //spinner for setting the outlierFactor parameter of the outlier detector
	JTextArea algorithmOutputTextArea; //text area for displaying textual algorithm output
	
	/**
	 * Constructor
	 * @param controller
	 */
	public MainView(Controller controller) {
		controllerObject = controller;
		dbAccess = new DatabaseAccess();
		topInitiated = middleInitiated = bottomInitiated = false;
		
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					setSize(1120, 630); //the size of the JFrame window
					setResizable(false); //disables resizing of the JFrame window
					setLocation(40, 40); //the initial location of the JFrame window on the screen
					setTitle("Machine Learning the Premier League"); //sets the title of the JFrame window
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		layoutMenu();
		updateView(controllerObject.getState());
		
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					setVisible(true);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Refreshes the view.
	 * @param state
	 */
	public void updateView(final String state) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					layoutTop(state);
					layoutMiddle(state);
					layoutBottom(state);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Adds components to the menu.
	 */
	private void layoutMenu() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					JMenuBar menuBar = new JMenuBar();
					setJMenuBar(menuBar);
					
					JMenu helpMenu = new JMenu("Help");
					menuBar.add(helpMenu);
					
					JMenuItem aboutItem = new JMenuItem("About");
					helpMenu.add(aboutItem);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Adds components to the top of the GUI.
	 * @param state
	 */
	private void layoutTop(String state) {
		switch (state) {
			case "startScreen_1":
				if (!topInitiated) {
					try {
						topPanel = new JPanel() {
							Image img = ImageIO.read(new File("MWC.png"));
							protected void paintComponent(Graphics g) {
								super.paintComponent(g);
								g.drawImage(img, 0, 0, null);
							}
						};
					} catch (IOException e) {
						JOptionPane.showMessageDialog(null, 
				    			"Something went wrong while attempting to set background image.", 
				    			"Error: IO Exception", JOptionPane.ERROR_MESSAGE);
					}
					topPanel.setLayout(new GridBagLayout());
					topPanel.setPreferredSize(new Dimension(1140, 300));
					topPanel.setBackground(Color.YELLOW);
					
					infoLabel = new JLabel();
					infoLabel.setFont(new Font("Serif", Font.BOLD + Font.ITALIC, 48));
					infoLabel.setForeground(Color.WHITE);

					topPanel.add(infoLabel);
					
					this.add(topPanel, BorderLayout.NORTH);
					topInitiated = true;
				}
				infoLabel.setText("Which dataset do you wish to analyse?");
				break;
			case "startScreen_2":
				infoLabel.setText("What kind of data items do you wish to analyse?");
				break;
			case "startScreen_3":
				infoLabel.setText("What task do you wish to perform?");
				break;
			case "clustering_step1":
				infoLabel.setText("What features do you wish to cluster by?");
				break;
			case "clustering_step2":
				infoLabel.setText("Please specify parameters for the K-Means algorithm.");
				break;
			case "clustering_step3":
				infoLabel.setText("Results of the K-Means clustering algorithm");
				break;
			case "classification_step1":
				infoLabel.setText("What features do you wish to use to make predictions?");
				break;
			case "classification_step2":
				infoLabel.setText("What feature would you like to predict?");
				break;
			case "classification_step3":
				infoLabel.setText("Please specify parameters for the SVM algorithm.");
				break;
			case "classification_step4":
				infoLabel.setText("How should the classifier's performance be evaluated?");
				break;
			case "classification_step5":
				infoLabel.setText("Results of the SVM classification algorithm");
				break;
			case "outlierDetection_step1":
				infoLabel.setText("What features do you wish to use to identify outliers?");
				break;
			case "outlierDetection_step2":
				infoLabel.setText("Please specify parameters for the Outlier Detector.");
				break;
			case "outlierDetection_step3":
				infoLabel.setText("Results of the Outlier Detection algorithm");
				break;
		}
	}
	
	/**
	 * Adds components to the middle of the GUI.
	 * @param state
	 */
	private void layoutMiddle(String state) {
		GridBagConstraints c = new GridBagConstraints();
		if (middleInitiated) {
			middlePanel.removeAll();
			middlePanel.repaint();
		}
		switch (state) {
			case "startScreen_1":
				if (!middleInitiated) {
					middlePanel = new JPanel();
					middlePanel.setLayout(new GridBagLayout());
					this.add(middlePanel, BorderLayout.CENTER);
					middleInitiated = true;
				}
				
				ArrayList<String> otherAvailableDatasets = dbAccess.getNamesOfAvailableDatasets();
				otherAvailableDatasets.remove("MCFC_ANALYTICS_FULL_DATASET");
				
				if (otherAvailableDatasets.size() > 0) {
					JPanel chooseDatasetToDeletePanel = new JPanel();
					chooseDatasetToDeletePanel.setLayout(new BoxLayout(chooseDatasetToDeletePanel, BoxLayout.PAGE_AXIS));
					chooseDatasetToDeletePanel.setPreferredSize(new Dimension(400, 200));
					chooseDatasetToDeletePanel.setBorder(new TitledBorder(new EtchedBorder(), "Delete a Dataset from the Database (optional)"));
					
					chooseDatasetToDeletePanel.add(Box.createVerticalGlue());
					
					datasetToDeleteCombo = new JComboBox<String>();
					datasetToDeleteCombo.setMaximumSize(new Dimension(320, 40));
					datasetToDeleteCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
					for (String dataset : otherAvailableDatasets) {
						datasetToDeleteCombo.addItem(dataset);
					}
					chooseDatasetToDeletePanel.add(datasetToDeleteCombo);
					
					chooseDatasetToDeletePanel.add(Box.createVerticalGlue());
					
					deleteDatasetButton = new JButton("Delete");
					deleteDatasetButton.addActionListener(controllerObject);
					deleteDatasetButton.setAlignmentX(Component.CENTER_ALIGNMENT);
					chooseDatasetToDeletePanel.add(deleteDatasetButton);
					
					chooseDatasetToDeletePanel.add(Box.createVerticalGlue());
					
					c.insets = new Insets(0,0,0,20); //right padding
					c.gridx = 0; //first column
					c.gridy = 0; //first row
					middlePanel.add(chooseDatasetToDeletePanel, c);
				}
				
				JScrollPane chooseDatasetToAnalysePane = new JScrollPane();
				chooseDatasetToAnalysePane.setPreferredSize(new Dimension(400, 200));
				chooseDatasetToAnalysePane.setBorder(new TitledBorder(new EtchedBorder(), "Choose a Dataset to Analyse"));
				
				JPanel chooseDatasetToAnalysePanel = new JPanel();
				chooseDatasetToAnalysePanel.setLayout(new BoxLayout(chooseDatasetToAnalysePanel, BoxLayout.PAGE_AXIS));
				
				ButtonGroup group = new ButtonGroup();
				
				mcfcAnalyticsFullDatasetButton = new JRadioButton("MCFC Analytics Full Dataset");
				mcfcAnalyticsFullDatasetButton.setToolTipText("Analyse the MCFC Analytics Full Dataset.");
				mcfcAnalyticsFullDatasetButton.setSelected(true);
				group.add(mcfcAnalyticsFullDatasetButton);
				chooseDatasetToAnalysePanel.add(mcfcAnalyticsFullDatasetButton);
				
				otherAvailableDatasetsButtons = new JRadioButton[otherAvailableDatasets.size()];
				for (int i = 0; i < otherAvailableDatasets.size(); i++) {
					otherAvailableDatasetsButtons[i] = new JRadioButton(otherAvailableDatasets.get(i));
					otherAvailableDatasetsButtons[i].setToolTipText("Analyse \"" + otherAvailableDatasets.get(i) + "\".");
					group.add(otherAvailableDatasetsButtons[i]);
					chooseDatasetToAnalysePanel.add(otherAvailableDatasetsButtons[i]);
				}
				
				otherDatasetButton = new JRadioButton("Other");
				otherDatasetButton.setToolTipText("Load a new .xls dataset into the database, and analyse it after that.");
				group.add(otherDatasetButton);
				chooseDatasetToAnalysePanel.add(otherDatasetButton);
				
				
				chooseDatasetToAnalysePane.getViewport().add(chooseDatasetToAnalysePanel);
				c.gridx = 1; //second column
				c.gridy = 0; //first row
				middlePanel.add(chooseDatasetToAnalysePane, c);
				
				middlePanel.setVisible(false);
				middlePanel.setVisible(true);

				break;
			case "startScreen_2":
				if (controllerObject.getSelectedDataset().equals("MCFC_ANALYTICS_FULL_DATASET")) {
					levelOfAnalysisCombo = new JComboBox<String>();
					levelOfAnalysisCombo.addItem("Player performances in individual matches");
					levelOfAnalysisCombo.addItem("Player performances summed up over the season");
					levelOfAnalysisCombo.addItem("Team performances summed up over the season");
					middlePanel.add(levelOfAnalysisCombo);
				}
				break;
			case "startScreen_3":
				clusteringButton = new JRadioButton("Clustering");
				clusteringButton.setToolTipText("Clustering aims to discover groups of similar items within the data.");
				clusteringButton.setSelected(true);
				classificationButton = new JRadioButton("Classification");
				classificationButton.setToolTipText("Classification aims to make discrete predictions.");
				outlierDetectionButton = new JRadioButton("Outlier Detection");
				outlierDetectionButton.setToolTipText("Outlier Detection aims to identify outlier items within the data.");
				
				ButtonGroup group2 = new ButtonGroup();
				group2.add(clusteringButton);
				group2.add(classificationButton);
				group2.add(outlierDetectionButton);
				
				c.fill = GridBagConstraints.HORIZONTAL;
				c.gridx = 0;
				c.gridy = 0;
				middlePanel.add(clusteringButton, c);
				c.gridx = 0;
				c.gridy = 1;
				middlePanel.add(classificationButton, c);
				c.gridx = 0;
				c.gridy = 2;
				middlePanel.add(outlierDetectionButton, c);
				break;
			case "clustering_step1":
			case "classification_step1":
			case "outlierDetection_step1":
				String datasetName = controllerObject.getSelectedDataset();
				boolean numericOnly = true;
				availableFeatures = dbAccess.getNamesOfFieldsOfTable(datasetName, numericOnly);
				featureCheckBoxes = new ArrayList<JCheckBox>();
				
				JScrollPane featuresPane = new JScrollPane();
				featuresPane.setPreferredSize(new Dimension(400, 200));
				JPanel featuresPanel = new JPanel();
				featuresPanel.setToolTipText("You must select at lease 1 feature (preferably 3 or more).");
				featuresPanel.setLayout(new BoxLayout(featuresPanel, BoxLayout.PAGE_AXIS));
				int i = 0;
				for (String field : availableFeatures) {
					featureCheckBoxes.add(new JCheckBox(field));
					featuresPanel.add(featureCheckBoxes.get(i));
					i++;
				}
				featuresPane.getViewport().add(featuresPanel);
				c.gridx = 1; //second column
				c.gridy = 0; //first row
				middlePanel.add(featuresPane, c);
				
				if (datasetName.equals("MCFC_ANALYTICS_FULL_DATASET")) {
					JPanel examplesPanel = new JPanel();
					examplesPanel.setLayout(new BoxLayout(examplesPanel, BoxLayout.PAGE_AXIS));
					examplesPanel.add(new JLabel("Build your own feature vector on the right,"));
					examplesPanel.add(new JLabel("or select from one of the examples below:"));
					examplesPanel.add(Box.createRigidArea(new Dimension(0,10)));
					
					final JRadioButton buildOwnFeatureVectorButton = new JRadioButton("Build own feature vector");
					buildOwnFeatureVectorButton.setSelected(true);
					buildOwnFeatureVectorButton.addActionListener(new ActionListener () {
						public void actionPerformed(ActionEvent ae) {
							if (buildOwnFeatureVectorButton.isSelected()) {
								for (JCheckBox feature : featureCheckBoxes) {
									feature.setSelected(false);
									feature.setEnabled(true);
								}
							}
						}
					});
					examplesPanel.add(buildOwnFeatureVectorButton);
					
					final String copyOfState = state;
					
					final JRadioButton example1Button = new JRadioButton();
					if (state.equals("clustering_step1")) {
						example1Button.setText("(assists, goals, touches)");
					} else if (state.equals("classification_step1")) {
						example1Button.setText("classification");
					} else if (state.equals("outlierDetection_step1")) {
						example1Button.setText("(assists, goals, touches)");
					}
					example1Button.addActionListener(new ActionListener () {
						public void actionPerformed(ActionEvent ae) {
							if (example1Button.isSelected()) {
								for (JCheckBox feature : featureCheckBoxes) {
									feature.setSelected(false);
									feature.setEnabled(false);
								}
								if (copyOfState.equals("clustering_step1")) {
									featureCheckBoxes.get(availableFeatures.indexOf("ASSISTS")).setSelected(true);
									featureCheckBoxes.get(availableFeatures.indexOf("GOALS")).setSelected(true);
									featureCheckBoxes.get(availableFeatures.indexOf("TOUCHES")).setSelected(true);
								} else if (copyOfState.equals("classification_step1")) {
									featureCheckBoxes.get(availableFeatures.indexOf("GOALS")).setSelected(true);
									featureCheckBoxes.get(availableFeatures.indexOf("ASSISTS")).setSelected(true);
									featureCheckBoxes.get(availableFeatures.indexOf("TOUCHES")).setSelected(true);
								} else if (copyOfState.equals("outlierDetection_step1")) {
									featureCheckBoxes.get(availableFeatures.indexOf("ASSISTS")).setSelected(true);
									featureCheckBoxes.get(availableFeatures.indexOf("GOALS")).setSelected(true);
									featureCheckBoxes.get(availableFeatures.indexOf("TOUCHES")).setSelected(true);
								}
							}
						}
					});
					examplesPanel.add(example1Button);
					
					final JRadioButton example2Button = new JRadioButton();
					if (state.equals("clustering_step1")) {
						example2Button.setText("(blocks, interceptions, recoveries)");
					} else if (state.equals("classification_step1")) {
						example2Button.setText("classification");
					} else if (state.equals("outlierDetection_step1")) {
						example2Button.setText("(blocks, interceptions, recoveries)");
					}
					example2Button.addActionListener(new ActionListener () {
						public void actionPerformed(ActionEvent ae) {
							if (example2Button.isSelected()) {
								for (JCheckBox feature : featureCheckBoxes) {
									feature.setSelected(false);
									feature.setEnabled(false);
								}
								if (copyOfState.equals("clustering_step1")) {
									featureCheckBoxes.get(availableFeatures.indexOf("BLOCKS")).setSelected(true);
									featureCheckBoxes.get(availableFeatures.indexOf("INTERCEPTIONS")).setSelected(true);
									featureCheckBoxes.get(availableFeatures.indexOf("RECOVERIES")).setSelected(true);
								} else if (copyOfState.equals("classification_step1")) {
									featureCheckBoxes.get(availableFeatures.indexOf("GOALS")).setSelected(true);
									featureCheckBoxes.get(availableFeatures.indexOf("ASSISTS")).setSelected(true);
									featureCheckBoxes.get(availableFeatures.indexOf("TOUCHES")).setSelected(true);
								} else if (copyOfState.equals("outlierDetection_step1")) {
									featureCheckBoxes.get(availableFeatures.indexOf("BLOCKS")).setSelected(true);
									featureCheckBoxes.get(availableFeatures.indexOf("INTERCEPTIONS")).setSelected(true);
									featureCheckBoxes.get(availableFeatures.indexOf("RECOVERIES")).setSelected(true);
								}
							}
						}
					});
					examplesPanel.add(example2Button);
					
					final JRadioButton example3Button = new JRadioButton();
					if (state.equals("clustering_step1")) {
						example3Button.setText("(offsides, pass forward, successful passes final third)");
					} else if (state.equals("classification_step1")) {
						example3Button.setText("classification");
					} else if (state.equals("outlierDetection_step1")) {
						example3Button.setText("(red cards, total fouls conceded, yellow cards)");
					}
					example3Button.addActionListener(new ActionListener () {
						public void actionPerformed(ActionEvent ae) {
							if (example3Button.isSelected()) {
								for (JCheckBox feature : featureCheckBoxes) {
									feature.setSelected(false);
									feature.setEnabled(false);
								}
								if (copyOfState.equals("clustering_step1")) {
									featureCheckBoxes.get(availableFeatures.indexOf("OFFSIDES")).setSelected(true);
									featureCheckBoxes.get(availableFeatures.indexOf("PASS_FORWARD")).setSelected(true);
									featureCheckBoxes.get(availableFeatures.indexOf("SUCCESSFUL_PASSES_FINAL_THIRD")).setSelected(true);
								} else if (copyOfState.equals("classification_step1")) {
									featureCheckBoxes.get(availableFeatures.indexOf("GOALS")).setSelected(true);
									featureCheckBoxes.get(availableFeatures.indexOf("ASSISTS")).setSelected(true);
									featureCheckBoxes.get(availableFeatures.indexOf("TOUCHES")).setSelected(true);
								} else if (copyOfState.equals("outlierDetection_step1")) {
									featureCheckBoxes.get(availableFeatures.indexOf("RED_CARDS")).setSelected(true);
									featureCheckBoxes.get(availableFeatures.indexOf("TOTAL_FOULS_CONCEDED")).setSelected(true);
									featureCheckBoxes.get(availableFeatures.indexOf("YELLOW_CARDS")).setSelected(true);
								}
							}
						}
					});
					examplesPanel.add(example3Button);
					
					ButtonGroup examplesButtonGroup = new ButtonGroup();
					examplesButtonGroup.add(buildOwnFeatureVectorButton);
					examplesButtonGroup.add(example1Button);
					examplesButtonGroup.add(example2Button);
					examplesButtonGroup.add(example3Button);
					
					c.insets = new Insets(0,0,0,40); //right padding
					c.gridx = 0; //first column
					c.gridy = 0; //first row
					middlePanel.add(examplesPanel, c);
				}
				
				if (!state.equals("outlierDetection_step1")) {
					scaleAndMeanNormaliseFeatures = new JCheckBox("Scale and Mean-normalise Features");
					scaleAndMeanNormaliseFeatures.setToolTipText("This option would bring all selected features on a [0, 1] scale.");
					c.insets = new Insets(0,30,0,0); //left padding
					c.gridx = 2; //third column
					c.gridy = 0; //first row
					middlePanel.add(scaleAndMeanNormaliseFeatures, c);
				}
				break;
			case "clustering_step2":
				c.fill = GridBagConstraints.HORIZONTAL;
				c.gridx = 0; //first column
				c.gridy = 0; //first row
				JLabel numberOfClustersLabel = new JLabel("How many clusters should the K-Means algorithm group the data into?");
				middlePanel.add(numberOfClustersLabel, c);
				c.insets = new Insets(0,10,0,0); //left padding
				c.gridx = 1; //second column
				c.gridy = 0; //first row
				SpinnerModel numberOfClustersSpinnerModel =
				         new SpinnerNumberModel(3, //initial value
				            2, //min
				            5, //max
				            1); //step
				numberOfClustersSpinner = new JSpinner(numberOfClustersSpinnerModel);
				((DefaultEditor) numberOfClustersSpinner.getEditor()).getTextField().setEditable(false);
				middlePanel.add(numberOfClustersSpinner, c);
				c.insets = new Insets(10,0,0,0); //top padding
				c.gridx = 0; //first column
				c.gridy = 1; //second row
				JLabel numberOfKMeansRunsLabel = new JLabel("How many times should the K-Means algorithm be ran?");
				middlePanel.add(numberOfKMeansRunsLabel, c);
				c.insets = new Insets(10,10,0,0); //top and left padding
				c.gridx = 1; //second column
				c.gridy = 1; //second row
				SpinnerModel numberOfKMeansRunsSpinnerModel =
				         new SpinnerNumberModel(50, //initial value
				            1, //min
				            100, //max
				            1); //step
				numberOfKMeansRunsSpinner = new JSpinner(numberOfKMeansRunsSpinnerModel);
				numberOfKMeansRunsSpinner.setToolTipText("<html>The results of K-Means depend on the random initialisations of its cluster centroids.<br>"
						+ "Running K-Means multiple times makes it more likely that a good hypothesis model<br>"
						+ "would be picked up.");
				((DefaultEditor) numberOfKMeansRunsSpinner.getEditor()).getTextField().setEditable(false);
				middlePanel.add(numberOfKMeansRunsSpinner, c);
				algorithmOutputTextArea = new JTextArea();
				break;
			case "classification_step2":
				targetLabelCombo = new JComboBox<String>();
				ArrayList<String> targetLabelOptions;
				if (controllerObject.getSelectedDataset().equals("MCFC_ANALYTICS_FULL_DATASET")) {
					targetLabelOptions = availableFeatures;
				} else {
					boolean justNumeric = false;
					String nameOfDataset = controllerObject.getSelectedDataset();
					ArrayList<String> tableSchema = dbAccess.getNamesOfFieldsOfTable(nameOfDataset, justNumeric);
					targetLabelOptions = tableSchema;
				}
				targetLabelOptions.removeAll(controllerObject.getSelectedFeatures());
				for (String option : targetLabelOptions) {
					targetLabelCombo.addItem(option);
				}
				middlePanel.add(targetLabelCombo);
				break;
			case "classification_step3":
				c.fill = GridBagConstraints.HORIZONTAL;
				c.gridx = 0;
				c.gridy = 0;
				JLabel kernelTypeLabel = new JLabel("Kernel Type:");
				middlePanel.add(kernelTypeLabel, c);
				c.insets = new Insets(0,10,0,0); //left padding
				c.gridx = 1;
				c.gridy = 0;
				kernelTypeCombo = new JComboBox<String>();
				kernelTypeCombo.setToolTipText("<html>Using a Linear Kernel, the SVM classifier would be able to plot simple linear hypotheses.<br>"
						+ "Using a Gaussian Kernel, the SVM classifier would be able to plot complex non-linear hypotheses.</html>");
				kernelTypeCombo.addItem("Gaussian Kernel");
				kernelTypeCombo.addItem("Linear Kernel");
				kernelTypeCombo.addActionListener(new ActionListener () {
					public void actionPerformed(ActionEvent ae) {
						if (kernelTypeCombo.getSelectedItem().toString().equals("Gaussian Kernel")) {
							regularisationLabel.setEnabled(true);
							regularisationSpinner.setEnabled(true);
							regularisationSpinner.setToolTipText("<html>If C is set to a big value, then the SVM classifier would better fit the training set data, but<br>"
									+ "if C is too big, then the prediction hypothesis would not generalise well to unseen examples.</html>");
							gammaLabel.setEnabled(true);
							gammaSpinner.setEnabled(true);
							gammaSpinner.setToolTipText("<html>If gamma is set to a small value, then the SVM classifier would better fit the training set data, but<br>"
									+ "if gamma is too small, then the prediction hypothesis would not generalise well to unseen examples.<br>"
									+ "If set to 0, then gamma will be calculated as 1/number_of_features.</html>");
						} else {
							regularisationLabel.setEnabled(false);
							regularisationSpinner.setEnabled(false);
							regularisationSpinner.setToolTipText(null);
							gammaLabel.setEnabled(false);
							gammaSpinner.setEnabled(false);
							gammaSpinner.setToolTipText(null);
						}
					}
				});
				middlePanel.add(kernelTypeCombo, c);
				c.insets = new Insets(10,0,0,0); //top padding
				c.gridx = 0;
				c.gridy = 1;
				regularisationLabel = new JLabel("Regularisation Parameter (C):");
				middlePanel.add(regularisationLabel, c);
				c.fill = GridBagConstraints.NONE;
				c.insets = new Insets(10,10,0,0); //top and left padding
				c.gridx = 1;
				c.gridy = 1;
				SpinnerModel regularisationSpinnerModel =
				         new SpinnerNumberModel(1.0, //initial value
				            0.1, //min
				            10.0, //max
				            0.1); //step
				regularisationSpinner = new JSpinner(regularisationSpinnerModel);
				regularisationSpinner.setToolTipText("<html>If C is set to a big value, then the SVM classifier would better fit the training set data, but<br>"
						+ "if C is too big, then the prediction hypothesis would not generalise well to unseen examples.</html>");
				regularisationSpinner.setPreferredSize(new Dimension(55, 20));
				((DefaultEditor) regularisationSpinner.getEditor()).getTextField().setEditable(false);
				middlePanel.add(regularisationSpinner, c);
				c.insets = new Insets(10,0,0,0); //top padding
				c.gridx = 0;
				c.gridy = 2;
				gammaLabel = new JLabel("Parameter of the Gaussian Kernel (gamma):");
				middlePanel.add(gammaLabel, c);
				c.insets = new Insets(10,10,0,0); //top and left padding
				c.gridx = 1;
				c.gridy = 2;
				SpinnerModel gammaSpinnerModel =
				         new SpinnerNumberModel(0.0, //initial value
				            0.0, //min
				            10.0, //max
				            0.1); //step
				gammaSpinner = new JSpinner(gammaSpinnerModel);
				gammaSpinner.setToolTipText("<html>If gamma is set to a small value, then the SVM classifier would better fit the training set data, but<br>"
						+ "if gamma is too small, then the prediction hypothesis would not generalise well to unseen examples.</html>");
				gammaSpinner.setPreferredSize(new Dimension(55, 20));
				((DefaultEditor) gammaSpinner.getEditor()).getTextField().setEditable(false);
				middlePanel.add(gammaSpinner, c);
				break;
			case "classification_step4":
				trainingSetButton = new JRadioButton("Test on training set");
				trainingSetButton.setSelected(true);
				percentageSplitButton = new JRadioButton("Percentage split: 70% training / 30% test");
				crossValidationButton = new JRadioButton("Cross-validation");
				
				ButtonGroup testOptionButtonGroup = new ButtonGroup();
				testOptionButtonGroup.add(trainingSetButton);
				testOptionButtonGroup.add(percentageSplitButton);
				testOptionButtonGroup.add(crossValidationButton);
				
				c.fill = GridBagConstraints.HORIZONTAL;
				c.gridx = 0;
				c.gridy = 0;
				middlePanel.add(trainingSetButton, c);
				c.gridx = 0;
				c.gridy = 1;
				middlePanel.add(percentageSplitButton, c);
				c.gridx = 0;
				c.gridy = 2;
				middlePanel.add(crossValidationButton, c);
				algorithmOutputTextArea = new JTextArea();
				break;
			case "outlierDetection_step2":
				c.fill = GridBagConstraints.HORIZONTAL;
				c.gridx = 0; //first column
				c.gridy = 0; //first row
				JLabel outlierFactorLabel = new JLabel("Outlier Threshold Setting:");
				middlePanel.add(outlierFactorLabel, c);
				c.insets = new Insets(0,10,0,0); //left padding
				c.gridx = 1; //second column
				c.gridy = 0; //first row
				SpinnerModel outlierFactorSpinnerModel =
				         new SpinnerNumberModel(1.5, //initial value
				            0.0, //min
				            6.0, //max
				            0.1); //step
				outlierFactorSpinner = new JSpinner(outlierFactorSpinnerModel);
				outlierFactorSpinner.setToolTipText("<html>If set to 0, instances that rank in the bottom and top quartiles will be classified as outliers.<br>"
						+ "If greater than 0, fewer instances would be classified as outliers.</html>");
				outlierFactorSpinner.setPreferredSize(new Dimension(55, 20));
				((DefaultEditor) outlierFactorSpinner.getEditor()).getTextField().setEditable(false);
				middlePanel.add(outlierFactorSpinner, c);
				algorithmOutputTextArea = new JTextArea();
				break;
			case "clustering_step3":
			case "classification_step5":
			case "outlierDetection_step3":
				algorithmOutputTextArea.setEditable(false);
				JScrollPane resultsPane = new JScrollPane(algorithmOutputTextArea);
				resultsPane.setPreferredSize(new Dimension(600, 200));
				middlePanel.add(resultsPane);
				break;
		}
	}
	
	/**
	 * Adds components to the bottom of the GUI.
	 * @param state
	 */
	private void layoutBottom(String state) {
		switch (state) {
			case "startScreen_1":
				if (!bottomInitiated) {
					bottomPanel = new JPanel();
					bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.LINE_AXIS));
					bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
					
					startOverButton = new JButton("Start Over");
					startOverButton.addActionListener(controllerObject);
					backButton = new JButton("Back");
					backButton.addActionListener(controllerObject);
					nextButton = new JButton("Next");
					nextButton.addActionListener(controllerObject);

					programStatePanel = new JPanel();
					programStatePanel.setBorder(new TitledBorder(new EtchedBorder(), "Program State"));
					programStateLabel = new JLabel();
					programStatePanel.add(programStateLabel);
					
					bottomPanel.add(Box.createRigidArea(new Dimension(10, 0)));
					bottomPanel.add(programStatePanel);
					bottomPanel.add(Box.createRigidArea(new Dimension(50, 0)));
					bottomPanel.add(startOverButton);
					bottomPanel.add(Box.createRigidArea(new Dimension(50, 0)));
					bottomPanel.add(backButton);
					bottomPanel.add(Box.createRigidArea(new Dimension(10, 0)));
					bottomPanel.add(nextButton);
					bottomPanel.add(Box.createRigidArea(new Dimension(10, 0)));
					
					this.add(bottomPanel, BorderLayout.SOUTH);
					bottomInitiated = true;
				}
				programStateLabel.setText("Initial Setup Screen - Select a Dataset");
				startOverButton.setEnabled(false);
				backButton.setEnabled(false);
				nextButton.setEnabled(true);
				break;
			case "startScreen_2":
				programStateLabel.setText("Initial Setup Screen - Specify Level of Analysis");
				startOverButton.setEnabled(true);
				backButton.setEnabled(true);
				break;
			case "startScreen_3":
				programStateLabel.setText("Initial Setup Screen - Choose a Task to Perform");
				startOverButton.setEnabled(true);
				backButton.setEnabled(true);
				nextButton.setEnabled(true);
				break;
			case "clustering_step1":
				programStateLabel.setText("Clustering (Step 1 of 3) - Select Features");
				startOverButton.setEnabled(true);
				backButton.setEnabled(true);
				nextButton.setEnabled(true);
				break;
			case "clustering_step2":
				programStateLabel.setText("Clustering (Step 2 of 3) - Set Algorithm Parameters");
				startOverButton.setEnabled(true);
				backButton.setEnabled(true);
				nextButton.setEnabled(true);
				break;
			case "clustering_step3":
				programStateLabel.setText("Clustering (Step 3 of 3) - Displaying Results");
				startOverButton.setEnabled(true);
				backButton.setEnabled(true);
				break;
			case "classification_step1":
				programStateLabel.setText("Classification (Step 1 of 5) - Select Features");
				startOverButton.setEnabled(true);
				backButton.setEnabled(true);
				nextButton.setEnabled(true);
				break;
			case "classification_step2":
				programStateLabel.setText("Classification (Step 2 of 5) - Specify Target Label");
				startOverButton.setEnabled(true);
				backButton.setEnabled(true);
				nextButton.setEnabled(true);
				break;
			case "classification_step3":
				programStateLabel.setText("Classification (Step 3 of 5) - Set Algorithm Parameters");
				startOverButton.setEnabled(true);
				backButton.setEnabled(true);
				nextButton.setEnabled(true);
				break;
			case "classification_step4":
				programStateLabel.setText("Classification (Step 4 of 5) - Specify Algorithm Testing Option");
				nextButton.setEnabled(true);
				break;
			case "classification_step5":
				programStateLabel.setText("Classification (Step 5 of 5) - Displaying Results");
				startOverButton.setEnabled(true);
				backButton.setEnabled(true);
				break;
			case "outlierDetection_step1":
				programStateLabel.setText("Outlier Detection (Step 1 of 3) - Select Features");
				startOverButton.setEnabled(true);
				backButton.setEnabled(true);
				nextButton.setEnabled(true);
				break;
			case "outlierDetection_step2":
				programStateLabel.setText("Outlier Detection (Step 2 of 3) - Set Algorithm Parameters");
				startOverButton.setEnabled(true);
				backButton.setEnabled(true);
				nextButton.setEnabled(true);
				break;
			case "outlierDetection_step3":
				programStateLabel.setText("Outlier Detection (Step 3 of 3) - Displaying Results");
				startOverButton.setEnabled(true);
				backButton.setEnabled(true);
				break;
		}
	}
	
	public void setTextOfProgramStateLabel(final String text) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					programStateLabel.setText(text);
				}
			});
		} catch (Exception e) {}
	}
	
	public void setTextOfAlgorithmOutputTextArea(final String text) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					algorithmOutputTextArea.setText(text);
				}
			});
		} catch (Exception e) {}
	}
	
	public void toggleNavigationButtons(final boolean startOverButtonEnabled, final boolean backButtonEnabled, final boolean nextButtonEnabled) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					startOverButton.setEnabled(startOverButtonEnabled);
					backButton.setEnabled(backButtonEnabled);
					nextButton.setEnabled(nextButtonEnabled);
				}
			});
		} catch (Exception e) {}
	}
	
	public void disableAllComponentsOfContainer(Container container) {
		Component[] components = container.getComponents();
        for (Component component : components) {
        	final Component referenceToComponent = component;
        	SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					referenceToComponent.setEnabled(false);
				}
			});
        	if (component instanceof Container) {
        		disableAllComponentsOfContainer((Container)component);
            }
        }
	}
	
}
