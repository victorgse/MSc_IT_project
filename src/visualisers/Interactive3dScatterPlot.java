package visualisers;

import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jzy3d.analysis.AbstractAnalysis;
import org.jzy3d.chart.controllers.keyboard.camera.AWTCameraKeyController;
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

import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

import weka.core.Instances;

/**
 * This class utilises the Jzy3d library to define an interactive 3-D scatter plot.
 */
public class Interactive3dScatterPlot extends AbstractAnalysis {
	
	/**
	 * instance variables
	 */
	public static final Color[] COLOURS = {Color.GREEN, Color.RED, Color.BLUE, Color.YELLOW, Color.CYAN}; //the colours for the different classes
	private List<PickablePoint> points; //the pickable points on the plot
	private String selectedDataset; //the name of the dataset to visualise
	private String[] axeLabels; //the names of the attributes that are measured on the Cartesian axes of the plot
	private double[][] coordinates; //the coordinates of the scatter points
	private double[] actualClassAssignments; //the actual class assignments of the data instances
	private double[] predictedClassAssignments; //the classifier-predicted class assignments of the data instances
	private Instances instances; //the data instances to be visualised
	private String[] namesOfInstances; //the names of the data instances to be visualised
	private String[] classLabels; //the names of the classes
	private ArrayList<Integer> pickingIDs; //the picking ids of the pickable points on the scatter plot
	private boolean plotInitiated; //signifies whether the plot has been initiated (or not)
	private InstanceInfoFrame instanceInfoFrame; //the instanceInfoFrame object
	
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
	public Interactive3dScatterPlot(String selectedDataset, String[] axeLabels, double[][] coordinates, 
			double[] actualClassAssignments, double[] predictedClassAssignments, 
			Instances instances, String[] namesOfInstances, String[] classLabels) {
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

	/**
	 * Getter for the instanceInfoFrame object.
	 * @return the instanceInfoFrame
	 */
	public InstanceInfoFrame getInstanceInfoFrame() {
		return instanceInfoFrame;
	}

	/* (non-Javadoc)
	 * @see org.jzy3d.analysis.IAnalysis#init()
	 * Initiates plot.
	 */
	public void init() {
		// Create chart
		chart = AWTChartComponentFactory.chart(Quality.Nicest, "awt");
		
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
     	AWTCameraKeyController keyboard = new AWTCameraKeyController() {
     		public void keyTyped(KeyEvent ke) {
     			switch (ke.getKeyChar()) {
     				case 's':
	     		    	try {
	     		    		TextureData screenshot = chart.screenshot();
	         		    	screenshot.setMustFlipVertically(false);
	         		    	JFileChooser fileChooser = new JFileChooser();
	         			    FileNameExtensionFilter filter = new FileNameExtensionFilter("Choose a .png file to overwrite, or name a new one.", "png");
	         			    fileChooser.setFileFilter(filter);
	         			    fileChooser.setAcceptAllFileFilterUsed(false);
	         			    int returnVal = fileChooser.showSaveDialog(null);
	         			    if (returnVal == JFileChooser.APPROVE_OPTION) {
	         			    	String filePath = fileChooser.getSelectedFile().getAbsolutePath();
	         			    	if (filePath.endsWith(".png")) {
	         			    		TextureIO.write(screenshot, new File(filePath));
	         			    	} else {
	         			    		TextureIO.write(screenshot, new File(filePath + ".png"));
	         			    	}
	         			    	JOptionPane.showMessageDialog(null, 
	        			    			"The screenshot has been saved to the location of your choosing.", 
	        			    			"Screenshot Saved", JOptionPane.INFORMATION_MESSAGE);
	         			    }
						} catch (Exception e) {
							e.printStackTrace();
						}
     		    }
     		}
     	};
     	chart.addController(keyboard);
        
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
		points = new ArrayList<>();
		for (int i = 0; i < coordinates.length; i++) {
            double x = coordinates[i][0];
            double y = coordinates[i][1];
            double z = coordinates[i][2];
            Coord3d position = new Coord3d(x, y, z);
            Color colour = COLOURS[(int) actualClassAssignments[i]];
            float width;
            if ((predictedClassAssignments != null) 
            		&& (actualClassAssignments[i] != predictedClassAssignments[i])) {
            	width = 5; //incorrectly classified instance
            } else {
            	width = 7.5f;
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
	 * Adds event listeners to the pickable points.
	 * @param points
	 */
	private void enablePicking(List<PickablePoint> points) {
		mousePicker = new AWTMousePickingController<>(chart, 10);
		PickingSupport picking = mousePicker.getPickingSupport();
		
		pickingIDs = new ArrayList<Integer>();
		for (PickablePoint point : points) {
			picking.registerPickableObject(point, point);
			pickingIDs.add(point.getPickingId());
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
				int indexOfClickedInstance = pickingIDs.indexOf(Integer.parseInt(tokens[1]));
				if (selectedDataset.equals("MCFC_ANALYTICS_FULL_DATASET")) {
					clickedInstancesInfo += "Instance: " + namesOfInstances[indexOfClickedInstance] + "\n";
				} else {
					clickedInstancesInfo += "Instance: " + indexOfClickedInstance + "\n";
				}
				for (int i = 0; i < instances.numAttributes(); i++) {
					clickedInstancesInfo += instances.attribute(i).name() + ": " + instances.get(indexOfClickedInstance).value(i) + "\n";
				}
				if (predictedClassAssignments != null) {
					clickedInstancesInfo += "Actual Class: " + classLabels[(int) actualClassAssignments[indexOfClickedInstance]] + "\n";
					clickedInstancesInfo += "Predicted Class: " + classLabels[(int) predictedClassAssignments[indexOfClickedInstance]] + "\n\n";
				} else {
					clickedInstancesInfo += "Class: " + classLabels[(int) actualClassAssignments[indexOfClickedInstance]] + "\n\n";
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