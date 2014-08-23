package visualisers;

import gui.Controller;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.ThresholdCurve;
import weka.core.Instances;
import weka.core.Utils;
import weka.gui.visualize.PlotData2D;
import weka.gui.visualize.ThresholdVisualizePanel;

/**
 * Generates an ROC curve of a classifier's performance in a new window.
 * ======================================================================
 * The code in the plotROCcurve method of this class was copied in its
 * entirety from the following tutorial on generating ROC curves in Weka:
 * http://weka.wikispaces.com/Generating+ROC+curve
 */
public class ROCcurvePlotter {
	
	/**
	 * instance variables
	 */
	private Controller controllerObject;
	private JFrame jf;
	
	/**
	 * Constructor
	 * @param controllerObject
	 */
	public ROCcurvePlotter(Controller controllerObject) {
		this.controllerObject = controllerObject;
	}

	/**
	 * @return the jf
	 */
	public JFrame getJf() {
		return jf;
	}

	/**
	 * Plots an ROC curve of a classifier's evaluation in a new window.
	 * ====================================================================
	 * The code inside this method was copied in its entirety from the
	 * following tutorial on generating ROC curves in Weka:
	 * http://weka.wikispaces.com/Generating+ROC+curve
	 * @param eval
	 */
	@SuppressWarnings("static-access")
	public void plotROCcurve(Evaluation eval) {
		// generate curve
	    ThresholdCurve tc = new ThresholdCurve();
	    int classIndex = 0;
	    Instances result = tc.getCurve(eval.predictions(), classIndex);
	 
	    // plot curve
	    ThresholdVisualizePanel vmc = new ThresholdVisualizePanel();
	    vmc.setROCString("(Area under ROC = " + 
	    Utils.doubleToString(tc.getROCArea(result), 4) + ")");
	    vmc.setName(result.relationName());
	    PlotData2D tempd = new PlotData2D(result);
	    tempd.setPlotName(result.relationName());
	    tempd.addInstanceNumberAttribute();
	    // specify which points are connected
	    boolean[] cp = new boolean[result.numInstances()];
	    for (int n = 1; n < cp.length; n++)
	    cp[n] = true;
	    try {
			tempd.setConnectPoints(cp);
		} catch (Exception e2) {}
	    // add plot
	    try {
			vmc.addPlot(tempd);
		} catch (Exception e1) {}
	 
	    // display curve
	    String plotName = vmc.getName(); 
	    jf = new javax.swing.JFrame("Weka Classifier Visualize: "+plotName);
	    jf.setSize(500,400);
	    jf.getContentPane().setLayout(new BorderLayout());
	    jf.getContentPane().add(vmc, BorderLayout.CENTER);
	    jf.addWindowListener(new java.awt.event.WindowAdapter() {
		    public void windowClosing(java.awt.event.WindowEvent e) {
		    	jf.dispose();
		    	controllerObject.processROCcurveClosed();
		    }
	    });
	    jf.setVisible(true);
	}
}
