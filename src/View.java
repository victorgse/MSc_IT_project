import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.TreeSet;

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
	JRadioButton clusteringButton, classificationButton, anomalyDetectionButton; //the radio buttons for selecting a task
	JRadioButton crossValidationButton, percentageSplitButton; //the radio buttons for selecting a testing option for clusterer
	JComboBox<String> itemsToClusterCombo; //combo box for selecting the types of data items to analyse
	TreeSet<String> tableSchema, selectedFeatures; //sets storing the fields of the dataset and the selected features
	JCheckBox[] features; //check boxes allowing the user to select features
	JSpinner numberOfClustersSpinner, maxNumberOfIterationsSpinner; //spinners for K-Means' options
	JTextArea algorithmOutputTextArea; //text area for displaying textual algorithm output
	JComboBox<String> targetLabelCombo;
	
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
					topPanel = new JPanel();
					topPanel.setLayout(new GridBagLayout());
					topPanel.setPreferredSize(new Dimension(1140, 290));
					topPanel.setBackground(Color.YELLOW);
					
					infoLabel = new JLabel();
					infoLabel.setFont(new Font("Serif", Font.BOLD + Font.ITALIC, 48));

					topPanel.add(infoLabel);
					
					this.add(topPanel, BorderLayout.NORTH);
					topInitiated = true;
				}
				infoLabel.setText("Which dataset do you wish to use?");
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
				if (controllerObject.getSelectedDataset().equals("MCFC_Analytics_Full_Dataset")) {
					itemsToClusterCombo = new JComboBox<String>();
					itemsToClusterCombo.addItem("Player performances in individual matches");
					itemsToClusterCombo.addItem("Player performances summed up over the season");
					itemsToClusterCombo.addItem("Team performances summed up over the season");
					middlePanel.add(itemsToClusterCombo);
				}
				break;
			case "clustering_step2":
			case "classification_step1":
				JScrollPane featuresPane = new JScrollPane();
				featuresPane.setPreferredSize(new Dimension(400, 200));
				tableSchema = getFieldsOfDataset();
				features = new JCheckBox[tableSchema.size()];
				JPanel featuresPanel = new JPanel();
				featuresPanel.setLayout(new BoxLayout(featuresPanel, BoxLayout.PAGE_AXIS));
				int i = 0;
				for (String field : tableSchema) {
					features[i] = new JCheckBox(field);
					featuresPanel.add(features[i]);
					i++;
				}
				/*
				for (int i = 0; i < tableSchema.size(); i++) {
					features[i] = new JCheckBox(tableSchema.get(i));
					featuresPanel.add(features[i]);
				}
				*/
				featuresPane.getViewport().add(featuresPanel);
				middlePanel.add(featuresPane);
				break;
			case "clustering_step3":
				c.fill = GridBagConstraints.HORIZONTAL; //align
				c.gridx = 0; //first column
				c.gridy = 0; //first row
				JLabel numberOfClustersLabel = new JLabel("Number of Clusters (K):");
				middlePanel.add(numberOfClustersLabel, c);
				c.insets = new Insets(0,10,0,0);  //left padding
				c.gridx = 1; //second column
				c.gridy = 0; //first row
				SpinnerModel numberOfClustersSpinnerModel =
				         new SpinnerNumberModel(3, //initial value
				            2, //min
				            10, //max
				            1);//step
				numberOfClustersSpinner = new JSpinner(numberOfClustersSpinnerModel);
				((DefaultEditor) numberOfClustersSpinner.getEditor()).getTextField().setEditable(false);
				middlePanel.add(numberOfClustersSpinner, c);
				c.insets = new Insets(10,0,0,0);  //top padding
				c.gridx = 0; //first column
				c.gridy = 1; //second row
				JLabel maxNumberOfIterationsLabel = new JLabel("Maximum Number of K-Means Iterations:");
				middlePanel.add(maxNumberOfIterationsLabel, c);
				c.insets = new Insets(10,10,0,0);  //top and left padding
				c.gridx = 1; //second column
				c.gridy = 1; //second row
				SpinnerModel maxNumberOfIterationsSpinnerModel =
				         new SpinnerNumberModel(50, //initial value
				            10, //min
				            50, //max
				            10);//step
				maxNumberOfIterationsSpinner = new JSpinner(maxNumberOfIterationsSpinnerModel);
				((DefaultEditor) maxNumberOfIterationsSpinner.getEditor()).getTextField().setEditable(false);
				middlePanel.add(maxNumberOfIterationsSpinner, c);
				algorithmOutputTextArea = new JTextArea();
				break;
			case "classification_step2":
				targetLabelCombo = new JComboBox<String>();
				tableSchema = getFieldsOfDataset();
				TreeSet<String> targetLabelOptions = tableSchema;
				targetLabelOptions.removeAll(controllerObject.getSelectedFeatures());
				for (String option : targetLabelOptions) {
					targetLabelCombo.addItem(option);
				}
				/*
				for (int i = 0; i < tableSchema.size(); i++) {
					targetLabelCombo.addItem(tableSchema.get(i));
				}
				*/
				middlePanel.add(targetLabelCombo);
				break;
			case "classification_step3":
				c.gridx = 0;
				c.gridy = 0;
				JLabel kernelTypeLabel = new JLabel("Kernel Type:");
				middlePanel.add(kernelTypeLabel, c);
				c.gridx = 1;
				c.gridy = 0;
				JComboBox<String> kernelTypeCombo = new JComboBox<String>();
				kernelTypeCombo.addItem("Gaussian Kernel");
				kernelTypeCombo.addItem("Linear Kernel");
				middlePanel.add(kernelTypeCombo, c);
				c.gridx = 0;
				c.gridy = 1;
				JLabel regularisationLabel = new JLabel("Regularisation Parameter (C):");
				middlePanel.add(regularisationLabel, c);
				c.gridx = 1;
				c.gridy = 1;
				SpinnerModel regularisationSpinnerModel =
				         new SpinnerNumberModel(5, //initial value
				            1, //min
				            100, //max
				            1);//step
				JSpinner regularisationSpinner = new JSpinner(regularisationSpinnerModel);
				middlePanel.add(regularisationSpinner, c);
				c.gridx = 0;
				c.gridy = 2;
				JLabel gammaLabel = new JLabel("Parameter of the Gaussian Kernel (gamma):");
				middlePanel.add(gammaLabel, c);
				c.gridx = 1;
				c.gridy = 2;
				SpinnerModel gammaSpinnerModel =
				         new SpinnerNumberModel(1, //initial value
				            0.5, //min
				            10, //max
				            0.05);//step
				JSpinner gammaSpinner = new JSpinner(gammaSpinnerModel);
				middlePanel.add(gammaSpinner, c);
				break;
			case "classification_step4":
				crossValidationButton = new JRadioButton("Cross-validation");
				crossValidationButton.setSelected(true);
				percentageSplitButton = new JRadioButton("Percentage split: 60% training / "
						+ "20% validation / 20% test");
				
				ButtonGroup group3 = new ButtonGroup();
				group3.add(crossValidationButton);
				group3.add(percentageSplitButton);
				
				c.fill = GridBagConstraints.HORIZONTAL;
				c.gridx = 0;
				c.gridy = 0;
				middlePanel.add(crossValidationButton, c);
				c.fill = GridBagConstraints.HORIZONTAL;
				c.gridx = 0;
				c.gridy = 1;
				middlePanel.add(percentageSplitButton, c);
				algorithmOutputTextArea = new JTextArea();
				break;
			case "clustering_step4":
			case "classification_step5":
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
			case "clustering_step4":
			case "classification_step5":
				nextButton.setEnabled(false);
		}
	}
	
	/**
	 * A helper method for fetching a table's schema.
	 * @return
	 */
	private TreeSet<String> getFieldsOfDataset() {
		TreeSet<String> tableFields = new TreeSet<String>();
		try {
			Connection con = DriverManager.getConnection("jdbc:derby:datasetsDB");
			Statement stmt = con.createStatement();
			ResultSet RS = stmt.executeQuery("select columnname "
					+ "from sys.systables t, sys.syscolumns "
					+ "where TABLEID = REFERENCEID "
					+ "and tablename = 'MCFC_ANALYTICS_FULL_DATASET' ");
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
