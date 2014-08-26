import gui.Controller;
import gui.MainView;

/**
 * Machine Learning the Premier League (MLPL)
 * @author Victor Semerdjiev, GUID: 2106504s
 * ===============================================================================================
 * This application allows its users to run algorithms for clustering, classification, and
 * outlier detection tasks in order to analyse the MCFC Analytics Full Dataset (or other datasets).
 * It uses the Weka API and LibSVM for implementations of machine learning algorithms, Jzy3d for
 * visualising data, and Java DB for its database.
 */
public class Main {

	public static void main(String[] args) {
		
		Controller controller = new Controller();
		MainView view = new MainView(controller);
		controller.setView(view);
		
	}
	
}
