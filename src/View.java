import java.awt.*;

import javax.swing.*;
//import javax.swing.border.*;

public class View extends JFrame {
	
	// instance variables
	private Controller controllerObject;
	private JLabel infoLabel;
	private JPanel topPanel, middlePanel, bottomPanel;
	private boolean topInitiated, middleInitiated, bottomInitiated;
	JButton startOverButton, backButton, nextButton;
	JRadioButton mcfcAnalyticsFullDatasetButton, otherDatasetButton;
	JRadioButton clusteringButton, classificationButton, anomalyDetectionButton;
	JRadioButton splitDatasetAutomaticallyButton, splitDatasetManuallyButton;
	JRadioButton autoModelSelectionButton, manualModelSelectionButton;
	
	// constructor
	public View(Controller controller) {
		controllerObject = controller;
		topInitiated = middleInitiated = bottomInitiated = false;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1120, 630);
		setResizable(false); // disables resizing of the JFrame window
		setLocation(40, 40);
		setTitle("Machine Learning the Premier League");
		
		layoutMenu();
		
		updateView(controllerObject.getState());
	}
	
	public void updateView(String state) {
		layoutTop(state);
		layoutMiddle(state);
		layoutBottom(state);
	}
	
	private void layoutMenu() {
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu helpMenu = new JMenu("Help");
		menuBar.add(helpMenu);
	}
	
	private void layoutTop(String state) {
		switch (state) {
			case "startScreen_1":
				if (!topInitiated) {
					topPanel = new JPanel();
					topPanel.setLayout(new GridBagLayout());
					topPanel.setPreferredSize(new Dimension(1140, 290));
					topPanel.setBackground(Color.CYAN);
					//topPanel.setBorder(new TitledBorder(new EtchedBorder(), "Top"));
					
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
				infoLabel.setText("<html> Please indicate how the dataset should be split into<br/>"
						+ "training, validation, and test sets.</html>");
				break;
			case "classification_step2":
				infoLabel.setText("What features do you wish to use to make predictions?");
				break;
			case "classification_step3":
				infoLabel.setText("What feature would you like to predict?");
				break;
			case "classification_step4":
				infoLabel.setText("How do you wish to optimise the algorithm parameters?");
				break;
			case "classification_step5":
				infoLabel.setText("Please specify parameters for the SVM algorithm.");
				break;
			case "classification_step6":
				infoLabel.setText("Validation results of the current SVM hypothesis model");
				break;
			case "classification_step7":
				infoLabel.setText("Do you wish to classify test set examples now?");
				break;
			case "classification_step8":
				infoLabel.setText("Results of the SVM classification algorithm");
				break;
		}
	}
	
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
					//middlePanel.setBorder(new TitledBorder(new EtchedBorder(), "Middle"));
					this.add(middlePanel, BorderLayout.CENTER);
					middleInitiated = true;
				}
				
				mcfcAnalyticsFullDatasetButton = new JRadioButton("MCFC Analytics Full Dataset");
				mcfcAnalyticsFullDatasetButton.setSelected(true);
				otherDatasetButton = new JRadioButton("Other");
				
				ButtonGroup group = new ButtonGroup();
				group.add(mcfcAnalyticsFullDatasetButton);
				group.add(otherDatasetButton);
				
				middlePanel.add(mcfcAnalyticsFullDatasetButton, c);
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
				
				middlePanel.add(clusteringButton, c);
				middlePanel.add(classificationButton, c);
				middlePanel.add(anomalyDetectionButton, c);
				break;
			case "clustering_step1":
				JComboBox itemsToClusterCombo = new JComboBox();
				itemsToClusterCombo.addItem("Option 1");
				itemsToClusterCombo.addItem("Option 2");
				middlePanel.add(itemsToClusterCombo);
				break;
			case "clustering_step2":
			case "classification_step2":
				JScrollPane featuresPane = new JScrollPane();
				featuresPane.setPreferredSize(new Dimension(400, 200));
				JCheckBox[] features = new JCheckBox[50];
				JPanel featuresPanel = new JPanel();
				featuresPanel.setLayout(new BoxLayout(featuresPanel, BoxLayout.PAGE_AXIS));
				for (int i = 0; i < 50; i++) {
					features[i] = new JCheckBox("Feature " + i);
					featuresPanel.add(features[i]);
				}
				featuresPane.getViewport().add(featuresPanel);
				middlePanel.add(featuresPane);
				break;
			case "clustering_step3":
				c.gridx = 0;
				c.gridy = 0;
				JLabel numberOfClustersLabel = new JLabel("Number of Clusters (K):");
				middlePanel.add(numberOfClustersLabel, c);
				c.gridx = 1;
				c.gridy = 0;
				SpinnerModel numberOfClustersSpinnerModel =
				         new SpinnerNumberModel(3, //initial value
				            2, //min
				            10, //max
				            1);//step
				JSpinner numberOfClustersSpinner = new JSpinner(numberOfClustersSpinnerModel);
				middlePanel.add(numberOfClustersSpinner, c);
				c.gridx = 0;
				c.gridy = 1;
				JLabel numberOfIterationsLabel = new JLabel("Number of K-Means Iterations:");
				middlePanel.add(numberOfIterationsLabel, c);
				c.gridx = 1;
				c.gridy = 1;
				SpinnerModel numberOfIterationsSpinnerModel =
				         new SpinnerNumberModel(50, //initial value
				            50, //min
				            150, //max
				            50);//step
				JSpinner numberOfIterationsSpinner = new JSpinner(numberOfIterationsSpinnerModel);
				middlePanel.add(numberOfIterationsSpinner, c);
				break;
			case "classification_step1":
				splitDatasetAutomaticallyButton = new JRadioButton("60% training / "
						+ "20% validation / 20% test");
				splitDatasetAutomaticallyButton.setSelected(true);
				splitDatasetManuallyButton = new JRadioButton("Indicate Manually");;
				
				ButtonGroup group3 = new ButtonGroup();
				group3.add(splitDatasetAutomaticallyButton);
				group3.add(splitDatasetManuallyButton);
				
				c.fill = GridBagConstraints.HORIZONTAL;
				c.gridx = 0;
				c.gridy = 0;
				middlePanel.add(splitDatasetAutomaticallyButton, c);
				c.fill = GridBagConstraints.HORIZONTAL;
				c.gridx = 0;
				c.gridy = 1;
				middlePanel.add(splitDatasetManuallyButton, c);
				break;
			case "classification_step3":
				JComboBox targetLabelCombo = new JComboBox();
				targetLabelCombo.addItem("Option 1");
				targetLabelCombo.addItem("Option 2");
				middlePanel.add(targetLabelCombo);
				break;
			case "classification_step4":
				autoModelSelectionButton = new JRadioButton("Automatic Model Selection");
				autoModelSelectionButton.setSelected(true);
				manualModelSelectionButton = new JRadioButton("Set Parameters Manually");
				
				ButtonGroup group4 = new ButtonGroup();
				group4.add(autoModelSelectionButton);
				group4.add(manualModelSelectionButton);
				
				middlePanel.add(autoModelSelectionButton, c);
				middlePanel.add(manualModelSelectionButton, c);
				break;
			case "classification_step5":
				c.gridx = 0;
				c.gridy = 0;
				JLabel kernelTypeLabel = new JLabel("Kernel Type:");
				middlePanel.add(kernelTypeLabel, c);
				c.gridx = 1;
				c.gridy = 0;
				JComboBox kernelTypeCombo = new JComboBox();
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
			case "classification_step7":
				middlePanel.add(new JLabel("The SVM algorithm is fully trained now. "
						+ "Proceed to classify examples from the test set."));
				break;
		}
	}
	
	private void layoutBottom(String state) {
		switch (state) {
			case "startScreen_1":
				if (!bottomInitiated) {
					bottomPanel = new JPanel();
					bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.LINE_AXIS));
					bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
					//bottomPanel.setBorder(new TitledBorder(new EtchedBorder(), "Bottom"));
					
					startOverButton = new JButton("Start Over");
					startOverButton.addActionListener(controllerObject);
					backButton = new JButton("Back");
					backButton.addActionListener(controllerObject);
					nextButton = new JButton("Next");
					nextButton.addActionListener(controllerObject);
					
					bottomPanel.add(startOverButton);
					bottomPanel.add(Box.createHorizontalGlue());
					bottomPanel.add(backButton);
					bottomPanel.add(Box.createRigidArea(new Dimension(10, 0)));
					bottomPanel.add(nextButton);
					
					this.add(bottomPanel, BorderLayout.SOUTH);
					bottomInitiated = true;
				}
				startOverButton.setEnabled(false);
				backButton.setEnabled(false);
				break;
			case "startScreen_2":
				startOverButton.setEnabled(true);
				backButton.setEnabled(true);
				break;
		}
	}
	
}
