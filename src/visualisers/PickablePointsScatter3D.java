package visualisers;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import org.jzy3d.analysis.AbstractAnalysis;
import org.jzy3d.chart.controllers.keyboard.screenshot.AWTScreenshotKeyController;
import org.jzy3d.chart.controllers.mouse.camera.AWTCameraMouseController;
import org.jzy3d.chart.controllers.mouse.picking.AWTMousePickingController;
import org.jzy3d.chart.controllers.thread.camera.CameraThreadController;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.picking.IObjectPickedListener;
import org.jzy3d.picking.PickingSupport;
import org.jzy3d.plot3d.primitives.pickable.PickablePoint;
import org.jzy3d.plot3d.rendering.canvas.Quality;

import weka.core.Instances;

public class PickablePointsScatter3D extends AbstractAnalysis {
	
	/**
	 * instance variables
	 */
	public static final Color[] COLOURS = {Color.GREEN, Color.RED, Color.BLUE, Color.YELLOW, Color.CYAN};
	private List<PickablePoint> points;
	private String selectedDataset;
	private String[] axeLabels;
	private double[][] coordinates;
	private double[] actualClassAssignments;
	private double[] predictedClassAssignments;
	private Instances instances;
	private String[] namesOfInstances;
	private String[] classLabels;
	private boolean plotInitiated;
	private InstanceInfoFrame instanceInfoFrame;
	
	/**
	 * Constructor
	 * @param selectedDataset
	 * @param axeLabels
	 * @param coordinates
	 * @param actualClassAssignments
	 * @param predictedClassAssignments
	 * @param instances
	 * @param namesOfInstances
	 * @param classLabels
	 */
	public PickablePointsScatter3D(String selectedDataset, String[] axeLabels, double[][] coordinates, 
			double[] actualClassAssignments, double[] predictedClassAssignments, 
			Instances instances, String[] namesOfInstances, String[] classLabels) {
		points = new ArrayList<>();
		this.selectedDataset = selectedDataset;
		this.axeLabels = axeLabels;
		this.coordinates = coordinates;
		this.actualClassAssignments = actualClassAssignments;
		this.predictedClassAssignments = predictedClassAssignments;
		this.instances = instances;
		this.namesOfInstances = namesOfInstances;
		this.classLabels = classLabels;
		plotInitiated = false;
		instanceInfoFrame = null;
		init();
	}

	/* (non-Javadoc)
	 * @see org.jzy3d.analysis.IAnalysis#init()
	 * Initiates plot.
	 */
	public void init() {
		// Create chart
		chart = AWTChartComponentFactory.chart(Quality.Advanced, "awt");
		
		// Set chart colours
        chart.getView().setBackgroundColor(Color.BLACK);
        chart.getAxeLayout().setMainColor(Color.WHITE);
        
        // Rename axes (so that they have the names of the data attributes they represent)
        renameAxes(axeLabels);
        
        // Enable mouse control
        AWTCameraMouseController mouse = new AWTCameraMouseController();
     	chart.addController(mouse);
   		CameraThreadController thread = new CameraThreadController();
     	mouse.addSlaveThreadController(thread);
     	chart.addController(thread);
     	
     	// Enable screenshots
     	chart.getCanvas().addKeyController(new AWTScreenshotKeyController(chart, "./screenshots/screenshot.png"));
        
     	// Insert or replace scatter points
        updatePoints(coordinates);
        
        plotInitiated = true;
    }
	
	/**
	 * Renames the axes.
	 * @param axeLabels
	 */
	public void renameAxes(String[] axeLabels) {
		switch (axeLabels.length) {
			case 3:
				chart.getAxeLayout().setZAxeLabel(axeLabels[2]);
			case 2:
				chart.getAxeLayout().setYAxeLabel(axeLabels[1]);
			case 1:
				chart.getAxeLayout().setXAxeLabel(axeLabels[0]);
				break;
		}
	}
	
	AWTMousePickingController<?,?> mousePicker;
	/**
	 * Inserts new or replaces old scatter points.
	 * @param coordinates
	 */
	public void updatePoints(double[][] coordinates) {
		if (plotInitiated) {
			// remove old points and their event listeners
			for (PickablePoint point : points) {
	        	chart.getScene().remove(point);
			}
			mousePicker.dispose();
		}
		// create points
		for (int i = 0; i < coordinates.length; i++) {
            double x = coordinates[i][0];
            double y = coordinates[i][1];
            double z = coordinates[i][2];
            Coord3d position = new Coord3d(x, y, z);
            Color colour = COLOURS[(int) actualClassAssignments[i] - 1];
            float width;
            if (predictedClassAssignments != null) { //is it a classification algorithm?
            	if (actualClassAssignments[i] == predictedClassAssignments[i]) { //classified correctly
                	width = 3;
                } else { //classified incorrectly
                	width = 5;
                }
            } else { //non-classification algorithm
            	width = 5;
            }
            PickablePoint point = new PickablePoint(position, colour, width);
            points.add(point);
        }
		// add created points to the plot
		for (PickablePoint point : points) {
        	chart.getScene().add(point);
		}
		enablePicking(points); //add event listeners
	}
	
	/**
	 * Adds event listeners to points.
	 * @param points
	 */
	private void enablePicking(List<PickablePoint> points) {
		mousePicker = new AWTMousePickingController<>(chart, 10);
		PickingSupport picking = mousePicker.getPickingSupport();

		for (PickablePoint point : points) {
			picking.registerPickableObject(point, point);
		}
		
		IObjectPickedListener listener = new IObjectPickedListener() {
			@Override
			public void objectPicked(List<?> picked, PickingSupport pickingSupport) {
				processPicked(picked);
			}
		};
		picking.addObjectPickedListener(listener);
	}
	
	/**
	 * Event handler for picking points.
	 * Displays information about the last picked instances in an instanceInfoFrame.
	 * @param picked
	 */
	private void processPicked(List<?> picked) {
		String clickedInstancesInfo = "";
		if (!picked.isEmpty()) {
			for (Object p: picked) {//for each picked instance
				// add information about the picked instance to an info String about the last picked instances
				final String[] tokens = p.toString().split("[ :]+");
				int indexOfClickedInstance = Integer.parseInt(tokens[1]) % instances.numInstances();
				if (selectedDataset.equals("MCFC_ANALYTICS_FULL_DATASET")) {
					clickedInstancesInfo += "Instance: " + namesOfInstances[indexOfClickedInstance] + "\n";
				} else {
					clickedInstancesInfo += "Instance: " + indexOfClickedInstance + "\n";
				}
				for (int i = 0; i < instances.numAttributes(); i++) {
					clickedInstancesInfo += instances.attribute(i).name() + ": " + instances.get(indexOfClickedInstance).value(i) + "\n";
				}
				if (predictedClassAssignments != null) {
					clickedInstancesInfo += "Actual Class: " + classLabels[(int) actualClassAssignments[indexOfClickedInstance] - 1] + "\n";
					clickedInstancesInfo += "Predicted Class: " + classLabels[(int) predictedClassAssignments[indexOfClickedInstance] - 1] + "\n";
				} else {
					clickedInstancesInfo += "Class: " + classLabels[(int) actualClassAssignments[indexOfClickedInstance] - 1] + "\n\n";
				}
			}
			final String copyOfclickedInstancesInfo = clickedInstancesInfo;
			// display the info String about the last picked instances in an instanceInfoFrame
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					if (instanceInfoFrame == null) {
						instanceInfoFrame = new InstanceInfoFrame();
					} else if (!instanceInfoFrame.isVisible()) {
						instanceInfoFrame = null;
						instanceInfoFrame = new InstanceInfoFrame();
					}
					instanceInfoFrame.setTextOfInstanceInfoTextArea(copyOfclickedInstancesInfo);
				}
			});
		}
	}
	
}