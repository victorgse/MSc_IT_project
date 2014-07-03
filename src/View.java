import java.awt.*;
import javax.swing.*;

public class View extends JFrame {
	
	// instance variables
	
	// constructor
	public View() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1140, 585);
		//setLocation(100, 100);
		//setResizable(false); // disables resizing of the JFrame window
		setTitle("Machine Learning the Premier League");
		
		layoutTop();
		layoutMiddle();
		layoutBottom();
	}
	
	private void layoutTop() {
		JPanel top = new JPanel();
		top.setPreferredSize(new Dimension(1140, 150));
		top.setBackground(Color.lightGray);
		
		this.add(top, BorderLayout.NORTH);
	}
	
	private void layoutMiddle() {
		JLabel instructions_and_info = new JLabel();
		instructions_and_info.setHorizontalAlignment(SwingConstants.CENTER);
		//instructions_and_info.setVerticalAlignment(SwingConstants.TOP);
		instructions_and_info.setFont(new Font("Serif", Font.BOLD + Font.ITALIC, 48));
		instructions_and_info.setText("Which dataset do you wish to analyse?");
		this.add(instructions_and_info, BorderLayout.CENTER);
	}
	
	private void layoutBottom() {
		JPanel bottom = new JPanel();
		bottom.setPreferredSize(new Dimension(1140, 150));
		bottom.setBackground(Color.lightGray);
		
		JComboBox datasets = new JComboBox();
		datasets.setPreferredSize(new Dimension(300, 40));
		datasets.addItem("");
		datasets.addItem("MCFC Analytics Full Dataset");
		datasets.addItem("Option 2");
		bottom.add(datasets);
		
		this.add(bottom, BorderLayout.SOUTH);
	}
}
