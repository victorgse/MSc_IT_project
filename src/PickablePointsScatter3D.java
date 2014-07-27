import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

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

public class PickablePointsScatter3D extends AbstractAnalysis{
	
	private List<PickablePoint> points;
	private String[] axeLabels;
	private double[][] coordinates;
	private double[] classAssignments;
	private static final Color[] COLOURS = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.CYAN}; 
	
	public PickablePointsScatter3D(String[] axeLabels, double[][] coordinates, double[] classAssignments) {
		try {
			this.axeLabels = axeLabels;
			this.coordinates = coordinates;
			this.classAssignments = classAssignments;
			init();
			
			// Enable mouse control
			AWTCameraMouseController mouse = new AWTCameraMouseController();
	        chart.addController(mouse);
	        CameraThreadController thread = new CameraThreadController();
	        mouse.addSlaveThreadController(thread);
	        chart.addController(thread);
			
			// Embed into Swing.
			JFrame frame = new JFrame();
			//frame.setSize(800, 600);
			frame.setLayout(new BorderLayout());
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			frame.setTitle("Visualisation");
			
			JPanel panel3d = new JPanel();
			panel3d.setPreferredSize(new Dimension(800, 600));
			panel3d.setLayout(new java.awt.BorderLayout());
			panel3d.add((Component)chart.getCanvas(), BorderLayout.CENTER);
			frame.add(panel3d, BorderLayout.CENTER);
			
			frame.pack();
			frame.setVisible(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void init() {
        int size = coordinates.length;
        Color colour;
        double x;
        double y;
        double z;
        points = new ArrayList<>();
        
        Random r = new Random();
        r.setSeed(0);
        
        for(int i=0; i<size; i++){
        	colour = COLOURS[(int) classAssignments[i]];
            x = coordinates[i][0];
            y = coordinates[i][1];
            z = coordinates[i][2];
            Coord3d p = new Coord3d(x, y, z);
            points.add(new PickablePoint(p, colour, 5));
        }
        
        chart = AWTChartComponentFactory.chart(Quality.Advanced, "awt");
        chart.getView().setBackgroundColor(Color.BLACK);
        chart.getAxeLayout().setMainColor(Color.WHITE);
        chart.getAxeLayout().setXAxeLabel(axeLabels[0]);
        chart.getAxeLayout().setYAxeLabel(axeLabels[1]);
        chart.getAxeLayout().setZAxeLabel(axeLabels[2]);
        
        for (PickablePoint p : points) {
        	chart.getScene().add(p);
		}
        
        enablePicking(points);
    }
	
	private void enablePicking(List<PickablePoint> points) {
		AWTMousePickingController<?,?> mousePicker = new AWTMousePickingController<>(chart, 10);
		PickingSupport picking = mousePicker.getPickingSupport();

		for (PickablePoint p : points) {
			picking.registerPickableObject(p, p);
		}
		
		picking.addObjectPickedListener(new IObjectPickedListener() {
			@Override
			public void objectPicked(List<?> picked, PickingSupport ps) {
				processPicked(picked);
			}
		});
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