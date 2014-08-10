import javax.swing.SwingUtilities;


public class Main {

	public static void main(String[] args) {
		
		final Controller controller = new Controller();
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				View view = new View(controller);
				view.setVisible(true);
				controller.setView(view);
			}
		});
		
	}
	
}
