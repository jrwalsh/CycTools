/*
 * DisplayViewPanel.java
 *
 * Created on January 22, 2007, 2:36 PM
 */

package edu.iastate.cycspreadsheetloader.view;

import edu.iastate.cycspreadsheetloader.controller.DefaultController;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import javax.swing.JComponent;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import java.awt.FlowLayout;
import javax.swing.JPanel;
import javax.swing.JButton;


public class ControlViewPanel extends AbstractViewPanel
{
    // The controller used by this view
    private DefaultController controller;
    
    public ControlViewPanel(DefaultController controller) {
        this.controller = controller;
        setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        
        JPanel controlPanel = new JPanel();
        add(controlPanel);
        
        JButton btnSubmit = new JButton("Submit");
        controlPanel.add(btnSubmit);
        
        JButton btnSave = new JButton("Save");
        controlPanel.add(btnSave);
        
        JButton btnRevert = new JButton("Revert");
        controlPanel.add(btnRevert);
        initComponents();
        localInitialization();
    }

    public void localInitialization() {
    	
    }
    
    private void initComponents() {
    	
    }

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub
		
	}
}