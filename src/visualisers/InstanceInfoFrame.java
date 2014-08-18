package visualisers;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class InstanceInfoFrame extends JFrame {
	
	private JScrollPane instanceInfoPane;
	private JTextArea instanceInfoTextArea;
	
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
	
	public void setTextOfInstanceInfoTextArea (String text) {
		instanceInfoTextArea.setText(text);
	}
	
}
