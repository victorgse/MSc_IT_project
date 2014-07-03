import java.awt.*;

import javax.swing.*;
import javax.swing.border.*;

public class View extends JFrame {
	
	// instance variables
	
	// constructor
	public View() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1140, 585);
		setResizable(false); // disables resizing of the JFrame window
		setLocation(40, 40);
		setTitle("Machine Learning the Premier League");
		
		layoutTop();
		layoutMiddle();
		layoutBottom();
	}
	
	private void layoutTop() {
		JPanel topPanel = new JPanel();
		topPanel.setBorder(new TitledBorder(new EtchedBorder(), "Menu"));
		//topPanel.setPreferredSize(new Dimension(1140, 150));
		//top.setBackground(Color.lightGray);
		
		this.add(topPanel, BorderLayout.NORTH);
	}
	
	private void layoutMiddle() {
		JLabel infoLabel = new JLabel();
		infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
		infoLabel.setFont(new Font("Serif", Font.BOLD + Font.ITALIC, 48));
		infoLabel.setText("Which dataset do you wish to use?");
		this.add(infoLabel, BorderLayout.CENTER);
	}
	
	private void layoutBottom() {
		JPanel bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.setBorder(new TitledBorder(new EtchedBorder(), "User Selection Area"));
		bottomPanel.setPreferredSize(new Dimension(1140, 100));
		//bottom.setBackground(Color.lightGray);
		
		JPanel bottomCenterPanel = new JPanel();
		
		JRadioButton mcfcAnalyticsFullButton = new JRadioButton("MCFC Analytics Full Dataset");
		mcfcAnalyticsFullButton.setSelected(true);
		JRadioButton otherButton = new JRadioButton("Other");
		
		ButtonGroup group = new ButtonGroup();
		group.add(mcfcAnalyticsFullButton);
		group.add(otherButton);
		
		bottomCenterPanel.add(mcfcAnalyticsFullButton);
		bottomCenterPanel.add(otherButton);
		
		bottomPanel.add(bottomCenterPanel, BorderLayout.CENTER);
		
		JPanel bottomSouthPanel = new JPanel();
		bottomSouthPanel.setLayout(new BoxLayout(bottomSouthPanel, BoxLayout.LINE_AXIS));
		bottomSouthPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		
		JButton startOverButton = new JButton("Start Over"); startOverButton.setEnabled(false);
		JButton backButton = new JButton("Back"); backButton.setEnabled(false);
		JButton nextButton = new JButton("Next");
		
		bottomSouthPanel.add(startOverButton);
		bottomSouthPanel.add(Box.createHorizontalGlue());
		bottomSouthPanel.add(backButton);
		bottomSouthPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		bottomSouthPanel.add(nextButton);
		
		bottomPanel.add(bottomSouthPanel, BorderLayout.SOUTH);
		
		this.add(bottomPanel, BorderLayout.SOUTH);
	}
}
