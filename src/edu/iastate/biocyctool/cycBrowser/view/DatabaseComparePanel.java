package edu.iastate.biocyctool.cycBrowser.view;

import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTextArea;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JScrollPane;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.text.DefaultEditorKit;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import javax.swing.GroupLayout.Alignment;

import edu.iastate.biocyctool.cycBrowser.controller.BrowserController;
import edu.iastate.biocyctool.util.view.AbstractViewPanel;
import edu.iastate.javacyco.*;
import java.awt.event.ActionListener;
import javax.swing.LayoutStyle.ComponentPlacement;

public class DatabaseComparePanel extends AbstractViewPanel {
	BrowserController controller;
	
	/**
	 * Create the frame.
	 */
	public DatabaseComparePanel(BrowserController controller) {
		this.controller = controller;
		
		initComponents();
        localInitialization();
    }

    public void localInitialization() {
    }
    
    private void initComponents() {
    	setPreferredSize(new Dimension(800, 400));
		setMinimumSize(new Dimension(800, 400));
		setName("SimpleBrowser");
		
		setBorder(new EmptyBorder(5, 5, 5, 5));
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGap(0, 790, Short.MAX_VALUE)
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGap(0, 390, Short.MAX_VALUE)
		);
		setLayout(groupLayout);
	}
	
	
	
	//http://www.coderanch.com/t/346220/GUI/java/Copy-paste-popup-menu
    private void installContextMenu(final JTextField component) {
    }
    
    private void installContextMenu(final JTextArea component) {
    }

	private class ActionSubmit extends AbstractAction {
		public ActionSubmit() {
			putValue(NAME, "Submit");
			putValue(SHORT_DESCRIPTION, "Submits query from text field and returns results in the primary tab.");
		}
		public void actionPerformed(ActionEvent e) {
		}
	}
	
	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		
	}
	
}
