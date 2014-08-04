import java.util.ArrayList;
import java.util.List;

import org.jzy3d.analysis.AbstractAnalysis;
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

public class PickablePointsScatter3D extends AbstractAnalysis {
	
	static final Color[] COLOURS = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.CYAN};
	private List<PickablePoint> points;
	private String[] axeLabels;
	private double[][] coordinates;
	private double[] actualClassAssignments;
	private double[] predictedClassAssignments;
	private boolean plotInitiated;
	
	public PickablePointsScatter3D(String[] axeLabels, double[][] coordinates, 
			double[] actualClassAssignments, double[] predictedClassAssignments) {
		try {
			this.axeLabels = axeLabels;
			this.coordinates = coordinates;
			this.actualClassAssignments = actualClassAssignments;
			this.predictedClassAssignments = predictedClassAssignments;
			plotInitiated = false;
			init();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void init() {
		chart = AWTChartComponentFactory.chart(Quality.Advanced, "awt");
        chart.getView().setBackgroundColor(Color.BLACK);
        chart.getAxeLayout().setMainColor(Color.WHITE);
        renameAxes(axeLabels);
        
        // Enable mouse control
        AWTCameraMouseController mouse = new AWTCameraMouseController();
     	chart.addController(mouse);
   		CameraThreadController thread = new CameraThreadController();
     	mouse.addSlaveThreadController(thread);
     	chart.addController(thread);
        
        updatePoints(coordinates);
        
        plotInitiated = true;
    }
	
	public void renameAxes(String[] axeLabels) {
		chart.getAxeLayout().setXAxeLabel(axeLabels[0]);
        chart.getAxeLayout().setYAxeLabel(axeLabels[1]);
        chart.getAxeLayout().setZAxeLabel(axeLabels[2]);
	}
	
	AWTMousePickingController<?,?> mousePicker;
	public void updatePoints(double[][] coordinates) {
		if (plotInitiated) {
			for (PickablePoint point : points) {
	        	chart.getScene().remove(point);
			}
			mousePicker.dispose();
		}
		points = new ArrayList<>();
		for (int i = 0; i < coordinates.length; i++) {
            double x = coordinates[i][0];
            double y = coordinates[i][1];
            double z = coordinates[i][2];
            Coord3d position = new Coord3d(x, y, z);
            Color colour = COLOURS[(int) actualClassAssignments[i] - 1];
            float width;
            if (predictedClassAssignments != null) { //classification algorithm
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
		for (PickablePoint point : points) {
        	chart.getScene().add(point);
		}
		enablePicking(points);
	}
	
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
	
	private void processPicked(List<?> picked) {
		if (picked.isEmpty()) {
			System.out.println("Nothing was picked.");
		} else {
			System.out.println("These were picked:");
			for (Object p: picked) {
				System.out.println(p);
			}
		}
	}
	
}