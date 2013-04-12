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

public class FrameInspectPanel extends AbstractViewPanel {
	BrowserController controller;
	private final Action actionSubmit = new ActionSubmit();
	private JScrollPane scrollPane;
	private JTextArea textArea;
	private JTextField txtEnterFrameid;
	private JButton button;
	
	/**
	 * Create the frame.
	 */
	public FrameInspectPanel(BrowserController controller) {
		this.controller = controller;
		
		initComponents();
        localInitialization();
    }

    public void localInitialization() {
    	installContextMenu(textArea);
    }
    
    private void initComponents() {
    	setPreferredSize(new Dimension(800, 400));
		setMinimumSize(new Dimension(800, 400));
		setName("SimpleBrowser");
		
		setBorder(new EmptyBorder(5, 5, 5, 5));
		
		txtEnterFrameid = new JTextField();
		txtEnterFrameid.setColumns(10);
		
		button = new JButton("Search");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		button.setAction(actionSubmit);
		
		textArea = new JTextArea();
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		textArea.setEditable(false);
		
		scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new Dimension(800, 300));
		scrollPane.setViewportView(textArea);
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(txtEnterFrameid, GroupLayout.PREFERRED_SIZE, 165, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(button))
						.addGroup(groupLayout.createSequentialGroup()))
					.addGap(389))
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 770, Short.MAX_VALUE)
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(5)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(txtEnterFrameid, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(button))
					.addGap(5)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 351, GroupLayout.PREFERRED_SIZE))
		);
		setLayout(groupLayout);
	}
	
	
	
	//http://www.coderanch.com/t/346220/GUI/java/Copy-paste-popup-menu
    private void installContextMenu(final JTextField component) {
    }
    
    private void installContextMenu(final JTextArea component) {
    	component.addMouseListener(new MouseAdapter() {
    		public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void showMenu(final MouseEvent e) {
				if (e.isPopupTrigger()) {
					final JPopupMenu menu = new JPopupMenu();
					JMenuItem item;
					item = new JMenuItem(new DefaultEditorKit.CopyAction());
					item.setText("Copy");
					item.setEnabled(component.getSelectionStart() != component.getSelectionEnd());
					menu.add(item);
					item = new JMenuItem(new DefaultEditorKit.CutAction());
					item.setText("Cut");
					item.setEnabled(component.isEditable() && component.getSelectionStart() != component.getSelectionEnd());
					menu.add(item);
					item = new JMenuItem(new DefaultEditorKit.PasteAction());
					item.setText("Paste");
					item.setEnabled(component.isEditable());
					menu.add(item);
					menu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
    	});
    }

	private class ActionSubmit extends AbstractAction {
		public ActionSubmit() {
			putValue(NAME, "Submit");
			putValue(SHORT_DESCRIPTION, "Submits query from text field and returns results in the primary tab.");
		}
		public void actionPerformed(ActionEvent e) {
			String result = controller.frameToString(txtEnterFrameid.getText());
			if (result == null || result.isEmpty()) {
				Object[] possibilities = controller.substringSearch(txtEnterFrameid.getText(), Pathway.GFPtype).toArray(); //TODO search type selection
				if (possibilities.length > 0) {
					String s = (String)JOptionPane.showInputDialog(
										controller.mainJFrame,
					                    "Select from the below possible search results",
					                    "Search Results",
					                    JOptionPane.PLAIN_MESSAGE,
					                    null,
					                    possibilities,
					                    "ham");
					if ((s != null) && (s.length() > 0)) {
						result = controller.frameToString(s);
					} else {
						// Canceled
					}
				} else {
					JOptionPane.showMessageDialog(controller.mainJFrame, "No search results found");
				}
			}
			textArea.setText(result);
		}
	}
	
	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		
	}
	
}
