package edu.iastate.biocyctool.view;

import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTextArea;

import javax.swing.JScrollPane;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import javax.swing.GroupLayout.Alignment;

import edu.iastate.biocyctool.controller.BrowserController;
import edu.iastate.biocyctool.util.util.Util;
import edu.iastate.biocyctool.util.view.AbstractViewPanel;
import edu.iastate.javacyco.*;
import java.awt.event.ActionListener;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JComboBox;

@SuppressWarnings("serial")
public class FrameInspectPanel extends AbstractViewPanel {
	BrowserController controller;
	private final Action actionSubmit = new ActionSubmit();
	private JScrollPane scrollPane;
	private JTextArea textArea;
	private JTextField txtEnterFrameid;
	private JButton button;
	private JComboBox<String> cmbType;
	
	/**
	 * Create the frame.
	 */
	public FrameInspectPanel(BrowserController controller) {
		this.controller = controller;
		
		initComponents();
        localInitialization();
    }

    public void localInitialization() {
    	Util.installContextMenu(txtEnterFrameid);
    	Util.installContextMenu(textArea);
    	
    	cmbType.addItem(Compound.GFPtype);
    	cmbType.addItem(Gene.GFPtype);
    	cmbType.addItem(Pathway.GFPtype);
    	cmbType.addItem(Protein.GFPtype);
    	cmbType.addItem(Regulation.GFPtype);
    	cmbType.addItem(Reaction.GFPtype);
    }
    
    private void initComponents() {
    	setPreferredSize(new Dimension(800, 400));
		setMinimumSize(new Dimension(800, 400));
		setName("SimpleBrowser");
		
		setBorder(new EmptyBorder(5, 5, 5, 5));
		
		txtEnterFrameid = new JTextField();
		txtEnterFrameid.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				actionSubmit.actionPerformed(event);
			}
		});
		txtEnterFrameid.setColumns(10);
		
		button = new JButton("Search");
		button.setAction(actionSubmit);
		
		textArea = new JTextArea();
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		textArea.setEditable(false);
		
		scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new Dimension(800, 300));
		scrollPane.setViewportView(textArea);
		
		cmbType = new JComboBox<String>();
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(txtEnterFrameid, GroupLayout.PREFERRED_SIZE, 165, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(cmbType, GroupLayout.PREFERRED_SIZE, 131, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(button)
							.addGap(389))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 770, Short.MAX_VALUE)
							.addContainerGap())))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(5)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(txtEnterFrameid, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(button)
						.addComponent(cmbType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(5)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 351, GroupLayout.PREFERRED_SIZE))
		);
		setLayout(groupLayout);
	}

	private class ActionSubmit extends AbstractAction {
		public ActionSubmit() {
			putValue(NAME, "Submit");
			putValue(SHORT_DESCRIPTION, "Submits query from text field and returns results in the primary tab.");
		}
		
		public void actionPerformed(ActionEvent e) {
			String result = "";//controller.frameToString(txtEnterFrameid.getText());
//			if (result == null || result.isEmpty()) {
				Object[] possibilities = controller.substringSearch(txtEnterFrameid.getText(), (String)cmbType.getSelectedItem()).toArray();
				if (possibilities.length > 0) {
					String s = (String)JOptionPane.showInputDialog(
										BrowserController.mainJFrame,
					                    "Select from the below possible search results",
					                    "Search Results",
					                    JOptionPane.PLAIN_MESSAGE,
					                    null,
					                    possibilities,
					                    "ham");
					if ((s != null) && (s.length() > 0)) {
						result = controller.frameToString(s);
					} else {
						// Cancelled
					}
				} else {
					JOptionPane.showMessageDialog(BrowserController.mainJFrame, "No search results found");
				}
//			}
			textArea.setText(result);
		}
	}
	
	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		
	}
}
