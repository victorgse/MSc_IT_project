
import java.awt.*;

import javax.swing.*;
import javax.swing.border.*;

import visualisers.PickablePointsScatter3D;
import weka.core.Instances;

@SuppressWarnings("serial")
public class VisualisationView extends JFrame{
	
	/**
	 * instance variables
	 */
	private Controller controllerObject; //a reference to the controller object
	private PickablePointsScatter3D scatterPlot; //a reference to the scatter plot object
	private Instances instances; //the dataset instances
	String[] classLabels;
	private JPanel leftPanel, rightPanel;
	private JPanel axeSelectionPanel, controlsPanel, visualisationPanel, legendPanel;
	JLabel xAxisLabel, yAxisLabel, zAxisLabel;
	JComboBox<String> xAxisCombo, yAxisCombo, zAxisCombo;
	JButton actualisePlotButton;
	
	/**
	 * Constructor
	 * @param controllerObject
	 * @param instances 
	 */
	public VisualisationView(Controller controllerObject, PickablePointsScatter3D scatterPlot, Instances instances, String[] classLabels) {
		this.controllerObject = controllerObject;
		this.scatterPlot = scatterPlot;
		this.instances = instances;
		this.classLabels = classLabels;
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(960, 540); //the size of the JFrame window
		//setResizable(false); //disables resizing of the JFrame window
		setLocation(80, 80); //the initial location of the JFrame window on the screen
		setTitle("Visualisation of Algortihm Results"); //sets the title of the JFrame window
		
		layoutLeft();
		layoutRight();
	}
	
	private void layoutLeft() {
		leftPanel = new JPanel();
		leftPanel.setLayout(new BorderLayout());
		leftPanel.setPreferredSize(new Dimension(250, 450));
		
		axeSelectionPanel = new JPanel();
		axeSelectionPanel.setBorder(new TitledBorder(new EtchedBorder(), "Select Visualisation Attributes"));
		axeSelectionPanel.setLayout(new BoxLayout(axeSelectionPanel, BoxLayout.PAGE_AXIS));
		
		axeSelectionPanel.add(Box.createVerticalGlue());
		xAxisLabel = new JLabel("X-axis:");
		xAxisLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		axeSelectionPanel.add(xAxisLabel);
		xAxisCombo = new JComboBox<String>();
		xAxisCombo.setPreferredSize(new Dimension(250, 40));
		xAxisCombo.setMaximumSize(new Dimension(250, 40));
		for (int i = 0; i < instances.numAttributes(); i++) {
			xAxisCombo.addItem(instances.attribute(i).name());
		}
		xAxisCombo.setSelectedIndex(0);
		axeSelectionPanel.add(xAxisCombo);
		axeSelectionPanel.add(Box.createVerticalGlue());
		
		yAxisLabel = new JLabel("Y-axis:");
		yAxisLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		axeSelectionPanel.add(yAxisLabel);
		yAxisCombo = new JComboBox<String>();
		yAxisCombo.setPreferredSize(new Dimension(250, 40));
		yAxisCombo.setMaximumSize(new Dimension(250, 40));
		if (instances.numAttributes() > 1) {
			for (int i = 0; i < instances.numAttributes(); i++) {
				yAxisCombo.addItem(instances.attribute(i).name());
			}
			yAxisCombo.setSelectedIndex(1);
		} else {
			yAxisLabel.setEnabled(false);
			yAxisCombo.setEnabled(false);
		}
		axeSelectionPanel.add(yAxisCombo);
		axeSelectionPanel.add(Box.createVerticalGlue());
		
		zAxisLabel = new JLabel("Z-axis:");
		zAxisLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		axeSelectionPanel.add(zAxisLabel);
		zAxisCombo = new JComboBox<String>();
		zAxisCombo.setPreferredSize(new Dimension(250, 40));
		zAxisCombo.setMaximumSize(new Dimension(250, 40));
		if (instances.numAttributes() > 2) {
			for (int i = 0; i < instances.numAttributes(); i++) {
				zAxisCombo.addItem(instances.attribute(i).name());
			}
			zAxisCombo.setSelectedIndex(2);
		} else {
			zAxisLabel.setEnabled(false);
			zAxisCombo.setEnabled(false);
		}
		axeSelectionPanel.add(zAxisCombo);
		axeSelectionPanel.add(Box.createVerticalGlue());
		
		actualisePlotButton = new JButton("Actualise Plot");
		actualisePlotButton.setPreferredSize(new Dimension(40, 40));
		actualisePlotButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		actualisePlotButton.addActionListener(controllerObject);
		axeSelectionPanel.add(actualisePlotButton);
		
		leftPanel.add(axeSelectionPanel, BorderLayout.CENTER);
		
		controlsPanel = new JPanel();
		controlsPanel.setBorder(new TitledBorder(new EtchedBorder(), "Controls"));
		controlsPanel.add(new JLabel("<html><table>"
				+ "<tr><td>Rotate</td><td>: &nbsp;Left click and drag mouse</td></tr>"
				+ "<tr><td>Scale</td><td>: &nbsp;Roll mouse wheel</td></tr>"
				+ "<tr><td>Z Shift</td><td>: &nbsp;Right click and drag mouse</td></tr>"
				+ "<tr><td>Animate</td><td>: &nbsp;Double left click</td></tr>"
				+ "<tr><td>Screenshot</td><td>: &nbsp;Press 's'</td></tr>"
				+ "</table></html>"));
		leftPanel.add(controlsPanel, BorderLayout.SOUTH);
		
		this.add(leftPanel, BorderLayout.WEST);
	}
	
	private void layoutRight() {
		rightPanel = new JPanel();
		rightPanel.setLayout(new BorderLayout());
		
		visualisationPanel = new JPanel();
		visualisationPanel.setLayout(new java.awt.BorderLayout());
		visualisationPanel.add((Component)scatterPlot.getChart().getCanvas(), BorderLayout.CENTER);
		rightPanel.add(visualisationPanel, BorderLayout.CENTER);
		
		legendPanel = new JPanel();
		legendPanel.setBorder(new TitledBorder(new EtchedBorder(), "Class Legend"));
		legendPanel.setLayout(new BorderLayout());
		if (controllerObject.getState().equals("classification_step5")) {
			JPanel legendNorthPanel = new JPanel();
			JLabel smallLabel = new JLabel("Small ==> correctly classified     ");
			legendNorthPanel.add(smallLabel);
			JLabel bigLabel = new JLabel("Big ==> incorrectly classified");
			legendNorthPanel.add(bigLabel);
			legendPanel.add(legendNorthPanel, BorderLayout.NORTH);
		}
		JPanel legendCenterPanel = new JPanel();
		for (int i = 0; i < classLabels.length; i++) {
			JLabel nextClassLabel = new JLabel(classLabels[i]);
			Color color = new Color(PickablePointsScatter3D.COLOURS[i].r, 
					PickablePointsScatter3D.COLOURS[i].g, 
					PickablePointsScatter3D.COLOURS[i].b);
			nextClassLabel.setForeground(color);
			legendCenterPanel.add(nextClassLabel);
		}
		legendPanel.add(legendCenterPanel, BorderLayout.CENTER);
		rightPanel.add(legendPanel, BorderLayout.SOUTH);
		
		this.add(rightPanel, BorderLayout.CENTER);
	}
}
