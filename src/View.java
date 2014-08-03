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
	private JPanel statusPanel; //the panel that holds the status label
	private JLabel infoLabel, statusLabel; //the info and status labels
	private boolean topInitiated, middleInitiated, bottomInitiated; //have the JPanels been initialised?
	JButton startOverButton, backButton, nextButton; //the buttons of bottomPanel
	JRadioButton mcfcAnalyticsFullDatasetButton, otherDatasetButton; //the radio buttons for selecting a dataset
	//JRadioButton[] availableDatasetsButtons; //the radio buttons for selecting a dataset
	//JRadioButton otherDatasetButton; //the radio button for opting to insert a new dataset
	JRadioButton clusteringButton, classificationButton, anomalyDetectionButton; //the radio buttons for selecting a task
	JRadioButton trainingSetButton, crossValidationButton, percentageSplitButton; //the radio buttons for selecting a testing option for the clusterer
	JComboBox<String> levelOfAnalysisCombo; //combo box for specifying the desired level of analysis for the MCFC Analytics Full Dataset
	TreeSet<String> tableSchema, selectedFeatures; //sets storing the fields of the dataset and the selected features
	JCheckBox[] features; //check boxes allowing the user to select features
	JComboBox<String> targetLabelCombo; //combo box for selecting a target label for the SVM classifier
	JSpinner numberOfClustersSpinner, numberOfKMeansRunsSpinner; //spinners for K-Means' options
	JComboBox<String> kernelTypeCombo; //combo box for choosing a kernel for the SVM classifier
	JSpinner regularisationSpinner, gammaSpinner; //spinners for some of the SVM's options
	JLabel regularisationLabel, gammaLabel; //labels describing the regularisation and gamma spinners
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
		
		JMenuItem userManualItem = new JMenuItem("User Manual");
		helpMenu.add(userManualItem);
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
									//Toolkit.getDefaultToolkit().getImage("/MSc_IT_project/cool-background.jpg");
							protected void paintComponent(Graphics g) {
								super.paintComponent(g);
								g.drawImage(img, 0, 0, null);
							}
						};
					} catch (IOException e) {
						// TODO Auto-generated catch block
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
				infoLabel.setText("What task do you wish to perform?");
				break;
			case "clustering_step1":
				infoLabel.setText("What data items do you wish to cluster?");
				break;
			case "clustering_step2":
				infoLabel.setText("What features do you wish to cluster by?");
				break;
			case "clustering_step3":
				infoLabel.setText("Please specify parameters for the K-Means algorithm.");
				break;
			case "clustering_step4":
				infoLabel.setText("Results of the K-Means clustering algorithm");
				break;
			case "classification_step1":
				infoLabel.setText("At what level of analysis do you wish to make predictions?");
				break;
			case "classification_step2":
				infoLabel.setText("What features do you wish to use to make predictions?");
				break;
			case "classification_step3":
				infoLabel.setText("What feature would you like to predict?");
				break;
			case "classification_step4":
				infoLabel.setText("Please specify parameters for the SVM algorithm.");
				break;
			case "classification_step5":
				infoLabel.setText("How should the classifier's performance be evaluated?");
				break;
			case "classification_step6":
				infoLabel.setText("Results of the SVM classification algorithm");
				break;
		}
	}
	
	/**
	 * Adds components to the middle of the GUI.
	 * @param state
	 */
	private void layoutMiddle(String state) {
		GridBagConstraints c = new GridBagConstraints();
		//c.fill = GridBagConstraints.HORIZONTAL;
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
				mcfcAnalyticsFullDatasetButton.setSelected(true);
				otherDatasetButton = new JRadioButton("Other");
				
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
				clusteringButton = new JRadioButton("Clustering");
				clusteringButton.setSelected(true);
				classificationButton = new JRadioButton("Classification");
				anomalyDetectionButton = new JRadioButton("Anomaly Detection");
				
				ButtonGroup group2 = new ButtonGroup();
				group2.add(clusteringButton);
				group2.add(classificationButton);
				group2.add(anomalyDetectionButton);
				
				c.fill = GridBagConstraints.HORIZONTAL;
				c.gridx = 0;
				c.gridy = 0;
				middlePanel.add(clusteringButton, c);
				c.gridx = 0;
				c.gridy = 1;
				middlePanel.add(classificationButton, c);
				c.gridx = 0;
				c.gridy = 2;
				middlePanel.add(anomalyDetectionButton, c);
				break;
			case "clustering_step1":
			case "classification_step1":
				if (controllerObject.getSelectedDataset().equals("MCFC_ANALYTICS_FULL_DATASET")) {
					levelOfAnalysisCombo = new JComboBox<String>();
					levelOfAnalysisCombo.addItem("Player performances in individual matches");
					levelOfAnalysisCombo.addItem("Player performances summed up over the season");
					levelOfAnalysisCombo.addItem("Team performances summed up over the season");
					middlePanel.add(levelOfAnalysisCombo);
				}
				break;
			case "clustering_step2":
			case "classification_step2":
				JScrollPane featuresPane = new JScrollPane();
				featuresPane.setPreferredSize(new Dimension(400, 200));
				tableSchema = getFieldsOfDataset(false);
				TreeSet<String> numericFields = getFieldsOfDataset(true);
				features = new JCheckBox[numericFields.size()];
				JPanel featuresPanel = new JPanel();
				featuresPanel.setLayout(new BoxLayout(featuresPanel, BoxLayout.PAGE_AXIS));
				int i = 0;
				for (String field : numericFields) {
					features[i] = new JCheckBox(field);
					featuresPanel.add(features[i]);
					i++;
				}
				featuresPane.getViewport().add(featuresPanel);
				middlePanel.add(featuresPane);
				break;
			case "clustering_step3":
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
				((DefaultEditor) numberOfKMeansRunsSpinner.getEditor()).getTextField().setEditable(false);
				middlePanel.add(numberOfKMeansRunsSpinner, c);
				algorithmOutputTextArea = new JTextArea();
				break;
			case "classification_step3":
				targetLabelCombo = new JComboBox<String>();
				TreeSet<String> targetLabelOptions = tableSchema;
				targetLabelOptions.removeAll(controllerObject.getSelectedFeatures());
				for (String option : targetLabelOptions) {
					targetLabelCombo.addItem(option);
				}
				middlePanel.add(targetLabelCombo);
				break;
			case "classification_step4":
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
				            100.0, //max
				            0.1); //step
				regularisationSpinner = new JSpinner(regularisationSpinnerModel);
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
				middlePanel.add(gammaSpinner, c);
				break;
			case "classification_step5":
				trainingSetButton = new JRadioButton("Test on training set");
				trainingSetButton.setSelected(true);
				crossValidationButton = new JRadioButton("Cross-validation");
				percentageSplitButton = new JRadioButton("Percentage split: 70% training / 30% test");
				
				ButtonGroup testOptionButtonGroup = new ButtonGroup();
				testOptionButtonGroup.add(trainingSetButton);
				testOptionButtonGroup.add(crossValidationButton);
				testOptionButtonGroup.add(percentageSplitButton);
				
				c.fill = GridBagConstraints.HORIZONTAL;
				c.gridx = 0;
				c.gridy = 0;
				middlePanel.add(trainingSetButton, c);
				c.gridx = 0;
				c.gridy = 1;
				middlePanel.add(crossValidationButton, c);
				c.gridx = 0;
				c.gridy = 2;
				middlePanel.add(percentageSplitButton, c);
				algorithmOutputTextArea = new JTextArea();
				break;
			case "clustering_step4":
			case "classification_step6":
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
					

					statusPanel = new JPanel();
					statusPanel.setBorder(new TitledBorder(new EtchedBorder(), "Status"));
					statusLabel = new JLabel();
					statusPanel.add(statusLabel);
					
					bottomPanel.add(Box.createRigidArea(new Dimension(10, 0)));
					bottomPanel.add(statusPanel);
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
				startOverButton.setEnabled(false);
				backButton.setEnabled(false);
				nextButton.setEnabled(true);
				break;
			case "startScreen_2":
				startOverButton.setEnabled(true);
				backButton.setEnabled(true);
				break;
			case "clustering_step3":
			case "classification_step5":
				nextButton.setEnabled(true);
				break;
			case "clustering_step4":
			case "classification_step6":
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
					+ "and tablename = 'MCFC_ANALYTICS_FULL_DATASET'";
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
	
}
