import java.awt.event.*;

public class Controller implements ActionListener {
	
	// instance variables
	private View viewObject;
	// private Algorithm algorithmObject;
	
	public void setView(View view) {
		viewObject = view;
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == viewObject.startOverButton) {
			System.out.println("Start Over was clicked");
		} else if (ae.getSource() == viewObject.backButton) {
			System.out.println("Back was clicked");
		} else if (ae.getSource() == viewObject.nextButton) {
			System.out.println("Next was clicked");
		}
		
	}

}
