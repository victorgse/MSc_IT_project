import gui.Controller;
import gui.MainView;

public class Main {

	public static void main(String[] args) {
		
		Controller controller = new Controller();
		MainView view = new MainView(controller);
		controller.setView(view);
		
	}
	
}
