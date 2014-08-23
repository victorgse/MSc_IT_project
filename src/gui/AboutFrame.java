package gui;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class AboutFrame extends JFrame {
	
	Controller controllerObject;
	
	public AboutFrame(final Controller controllerObject) {
		this.controllerObject = controllerObject;
		new Thread(new Runnable() {
			public void run() {
				try {
					SwingUtilities.invokeAndWait(new Runnable() {
						public void run() {
							setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
							setResizable(false);
							addWindowListener(new WindowAdapter() {
								public void windowClosed(WindowEvent we) {
									controllerObject.processAboutFrameClosed();
								}
							});
							setSize(400, 300); //the size of the JFrame window
							setLocation(70, 70); //the initial location of the JFrame window on the screen
							setTitle("About 'Machine Learning the Premier League'"); //sets the title of the JFrame window
							
							layoutComponents();
							
							setVisible(true);
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	private void layoutComponents() {
		String about = "<html>This application has been developed by Victor Semerdjiev<br>"
				+ "as a part of his final project for an MSc in Information Technology<br>"
				+ "at the University of Glasgow.<br><br><br><br>"
				+ "The project is entitled 'Machine Learning the Premier League'<br>"
				+ "and its goal was to develop an application that would enable<br>"
				+ "its users to analyse the MCFC Analytics Full Dataset.<br><br><br><br>"
				+ "If you have any comments, questions, or suggestions about the<br>"
				+ "application, please do not hesitate to voice them by sending<br>"
				+ "an e-mail to victorgse@gmail.com";
		JLabel aboutLabel = new JLabel(about);
		this.add(aboutLabel, BorderLayout.CENTER);
	}
}
