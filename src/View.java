import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.border.*;

/**
 * Defines a GUI.
 */
@SuppressWarnings("serial")
public class View extends JFrame {
	
	/**
	 * instance variables
	 */
	private Controller controllerObject; //a reference to the controller object
	private JPanel topPanel, middlePanel, bottomPanel; //the panels for the three areas of the GUI
	JPanel programStatePanel; //the panel that holds the programState label
	private JLabel infoLabel, programStateLabel; //the info and programState labels
	private boolean topInitiated, middleInitiated, bottomInitiated; //have the JPanels been initialised?
	JButton startOverButton, backButton, nextButton; //the buttons of bottomPanel
	JRadioButton mcfcAnalyticsFullDatasetButton, otherDatasetButton; //the radio buttons for selecting a dataset
	//JRadioButton[] availableDatasetsButtons; //the radio buttons for selecting a dataset
	//JRadioButton otherDatasetButton; //the radio button for opting to insert a new dataset
	JRadioButton clusteringButton, classificationButton, outlierDetectionButton; //the radio buttons for selecting a task
	JRadioButton trainingSetButton, percentageSplitButton, crossValidationButton; //the radio buttons for selecting a testing option for the clusterer
	JComboBox<String> levelOfAnalysisCombo; //combo box for specifying the desired level of analysis for the MCFC Analytics Full Dataset
	TreeSet<String> numericFieldsOfTableSchema; //stores the dataset's numeric fields.
	JCheckBox[] features; //check boxes allowing the user to select features
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
	public View(Controller controller) {
		controllerObject = controller;
		topInitiated = middleInitiated = bottomInitiated = false;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1120, 630); //the size of the JFrame window
		setResizable(false); //disables resizing of the JFrame window
		setLocation(40, 40); //the initial location of the JFrame window on the screen
		setTitle("Machine Learning the Premier League"); //sets the title of the JFrame window
		
		layoutMenu();
		updateView(controllerObject.getState());
	}
	
	/**
	 * Refreshes the view.
	 * @param state
	 */
	public void updateView(String state) {
		layoutTop(state);
		layoutMiddle(state);
		layoutBottom(state);
	}
	
	/**
	 * Adds components to the menu.
	 */
	private void layoutMenu() {
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu helpMenu = new JMenu("Help");
		menuBar.add(helpMenu);
		
		JMenuItem aboutItem = new JMenuItem("About");
		helpMenu.add(aboutItem);
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
						e.printStackTrace();
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
				
				mcfcAnalyticsFullDatasetButton = new JRadioButton("MCFC Analytics Full Dataset");
				mcfcAnalyticsFullDatasetButton.setToolTipText("Analyse the MCFC Analytics Full Dataset.");
				mcfcAnalyticsFullDatasetButton.setSelected(true);
				otherDatasetButton = new JRadioButton("Other");
				otherDatasetButton.setToolTipText("Load a new .xls dataset into the database.");
				
				ButtonGroup group = new ButtonGroup();
				group.add(mcfcAnalyticsFullDatasetButton);
				group.add(otherDatasetButton);

				c.fill = GridBagConstraints.HORIZONTAL;
				c.gridx = 0;
				c.gridy = 0;
				middlePanel.add(mcfcAnalyticsFullDatasetButton, c);
				c.gridx = 0;
				c.gridy = 1;
				middlePanel.add(otherDatasetButton, c);
				
				middlePanel.setVisible(false);
				middlePanel.setVisible(true);
				
				/*
				TreeSet<String> availableDatasets = getNamesOfAvailableDatasets();
				
				ButtonGroup group = new ButtonGroup();
				availableDatasetsButtons = new JRadioButton[availableDatasets.size()];
				
				int k = 0;
				for (String dataset : availableDatasets) {
					availableDatasetsButtons[k] = new JRadioButton(dataset);
					group.add(availableDatasetsButtons[k]);
					c.gridy = k;
					middlePanel.add(availableDatasetsButtons[k], c);
					k++;
				}
				
				availableDatasetsButtons[0].setSelected(true);
				
				otherDatasetButton = new JRadioButton("Other");
				group.add(otherDatasetButton);
				c.gridy = k;
				middlePanel.add(otherDatasetButton, c);
				*/
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
				JScrollPane featuresPane = new JScrollPane();
				featuresPane.setPreferredSize(new Dimension(400, 200));
				numericFieldsOfTableSchema = getFieldsOfDataset(true);
				features = new JCheckBox[numericFieldsOfTableSchema.size()];
				JPanel featuresPanel = new JPanel();
				featuresPanel.setToolTipText("You must select at lease 1 feature (preferably more).");
				featuresPanel.setLayout(new BoxLayout(featuresPanel, BoxLayout.PAGE_AXIS));
				int i = 0;
				for (String field : numericFieldsOfTableSchema) {
					features[i] = new JCheckBox(field);
					featuresPanel.add(features[i]);
					i++;
				}
				featuresPane.getViewport().add(featuresPanel);
				c.gridx = 1; //second column
				c.gridy = 0; //first row
				middlePanel.add(featuresPane, c);
				if (!controllerObject.getState().equals("outlierDetection_step1")) {
					scaleAndMeanNormaliseFeatures = new JCheckBox("Scale and Mean-normalise Features");
					scaleAndMeanNormaliseFeatures.setToolTipText("This option would bring all selected features on a [0, 1] scale.");
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
				numberOfKMeansRunsSpinner.setToolTipText("K-Means depends on random initialisations (of the cluster centroids) - multiple runs make it more likely that K-Means would pick up a good hypothesis model.");
				((DefaultEditor) numberOfKMeansRunsSpinner.getEditor()).getTextField().setEditable(false);
				middlePanel.add(numberOfKMeansRunsSpinner, c);
				algorithmOutputTextArea = new JTextArea();
				break;
			case "classification_step2":
				targetLabelCombo = new JComboBox<String>();
				TreeSet<String> targetLabelOptions = numericFieldsOfTableSchema;
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
				kernelTypeCombo.addItem("Gaussian Kernel");
				kernelTypeCombo.addItem("Linear Kernel");
				kernelTypeCombo.addActionListener(new ActionListener () {
					@Override
					public void actionPerformed(ActionEvent ae) {
						if(kernelTypeCombo.getSelectedItem().toString().equals("Gaussian Kernel")) {
							regularisationLabel.setEnabled(true);
							regularisationSpinner.setEnabled(true);
							gammaLabel.setEnabled(true);
							gammaSpinner.setEnabled(true);
						} else {
							regularisationLabel.setEnabled(false);
							regularisationSpinner.setEnabled(false);
							gammaLabel.setEnabled(false);
							gammaSpinner.setEnabled(false);
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
				         new SpinnerNumberModel(3.0, //initial value
				            0.0, //min
				            6.0, //max
				            0.1); //step
				outlierFactorSpinner = new JSpinner(outlierFactorSpinnerModel);
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
					//bottomPanel.add(Box.createHorizontalGlue());
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
				break;
			case "clustering_step1":
				programStateLabel.setText("Clustering (Step 1 of 3) - Select Features");
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
				break;
			case "classification_step2":
				programStateLabel.setText("Classification (Step 2 of 5) - Specify Target Label");
				break;
			case "classification_step3":
				programStateLabel.setText("Classification (Step 3 of 5) - Set Algorithm Parameters");
				break;
			case "classification_step4":
				programStateLabel.setText("Classification (Step 4 of 5) - Specify Algorithm Testing Option");
				startOverButton.setEnabled(true);
				backButton.setEnabled(true);
				nextButton.setEnabled(true);
				break;
			case "classification_step5":
				programStateLabel.setText("Classification (Step 5 of 5) - Displaying Results");
				startOverButton.setEnabled(true);
				backButton.setEnabled(true);
				break;
			case "outlierDetection_step1":
				programStateLabel.setText("Outlier Detection (Step 1 of 3) - Select Features");
				break;
			case "outlierDetection_step2":
				programStateLabel.setText("Outlier Detection (Step 2 of 3) - Set Algorithm Parameters");
				nextButton.setEnabled(true);
				break;
			case "outlierDetection_step3":
				programStateLabel.setText("Outlier Detection (Step 3 of 3) - Displaying Results");
				nextButton.setEnabled(false);
				break;
		}
	}
	
	/**
	 * A helper method for getting the names of datasets stored in the database.
	 * @return
	 */ /*
	private TreeSet<String> getNamesOfAvailableDatasets() {
		TreeSet<String> datasetsInDatabase = new TreeSet<String>();
		try {
			Connection con = DriverManager.getConnection("jdbc:derby:datasetsDB");
			Statement stmt = con.createStatement();
			String query =  "select tablename "
					+ "from sys.systables "
					+ "where tabletype = 'T'";
			ResultSet RS = stmt.executeQuery(query);
			while (RS.next()) {
				datasetsInDatabase.add(RS.getString("tablename"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Something went wrong when fetching the names of available datasets.");
		}
		return datasetsInDatabase;
	} */
	
	/**
	 * A helper method for fetching a table's schema.
	 * @return
	 */
	private TreeSet<String> getFieldsOfDataset(boolean numericOnly) {
		TreeSet<String> tableFields = new TreeSet<String>();
		try {
			Connection con = DriverManager.getConnection("jdbc:derby:datasetsDB");
			Statement stmt = con.createStatement();
			String query = "select columnname "
					+ "from sys.systables t, sys.syscolumns "
					+ "where TABLEID = REFERENCEID "
					+ "and tablename = '" + controllerObject.getSelectedDataset() + "'";
			if (numericOnly) {
				query += " and CAST(COLUMNDATATYPE AS VARCHAR(128)) = 'NUMERIC(10,2)'";
			}
			ResultSet RS = stmt.executeQuery(query);
			while (RS.next()) {
				tableFields.add(RS.getString("columnname"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Something went wrong when reading table schema.");
		}
		return tableFields;
	}
	
	public void setProgramStateLabel(String text) {
		programStateLabel.setText(text);
	}

	/**
	 * @return the programStateLabel
	 */
	public JLabel getProgramStateLabel() {
		return programStateLabel;
	}
	
}
