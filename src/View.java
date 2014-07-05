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
	
	// constructor
	public View(Controller controller) {
		controllerObject = controller;
		topInitiated = middleInitiated = bottomInitiated = false;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1140, 585);
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
					topPanel.setPreferredSize(new Dimension(1140, 270));
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
