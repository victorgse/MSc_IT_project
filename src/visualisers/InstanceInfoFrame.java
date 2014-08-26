package visualisers;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * This class defines a JFrame window that is used to display information
 * about the instances that were clicked on the interactive 3-D scatter plot.
 */
@SuppressWarnings("serial")
public class InstanceInfoFrame extends JFrame {
	
	/**
	 * instance variables
	 */
	private JScrollPane instanceInfoPane; //scrollable area, which makes the instanceInfoTextArea scrollable if neeeded
	private JTextArea instanceInfoTextArea; //text area for displaying information about clicked instances
	
	/**
	 * Constructor
	 */
	public InstanceInfoFrame() {
		setTitle("Selected Instances Info");
		setLocation(120, 120);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(false);
		setAlwaysOnTop(true);
		
		instanceInfoPane = new JScrollPane();
		instanceInfoPane.setPreferredSize(new Dimension(250, 400));
		instanceInfoTextArea = new JTextArea();
		instanceInfoTextArea.setEditable(false);
		instanceInfoPane.getViewport().add(instanceInfoTextArea);
		getContentPane().add(instanceInfoPane, BorderLayout.CENTER);
		
		pack();
		setVisible(true);
	}
	
	/**
	 * Setter for the text of the instanceInfoTextArea.
	 * @param text
	 */
	public void setTextOfInstanceInfoTextArea(String text) {
		instanceInfoTextArea.setText(text);
	}
	
}
