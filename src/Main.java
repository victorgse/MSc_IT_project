import javax.swing.SwingUtilities;


public class Main {

	public static void main(String[] args) {
		
		final Controller controller = new Controller();
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				MainView view = new MainView(controller);
				view.setVisible(true);
				controller.setView(view);
			}
		});
		
	}
	
}
