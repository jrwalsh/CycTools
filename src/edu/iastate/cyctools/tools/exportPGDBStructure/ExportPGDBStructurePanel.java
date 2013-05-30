package edu.iastate.cyctools.tools.exportPGDBStructure;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.table.DefaultTableModel;
import java.awt.FlowLayout;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import edu.iastate.cyctools.DefaultController;
import edu.iastate.cyctools.externalSourceCode.AbstractViewPanel;

@SuppressWarnings("serial")
public class ExportPGDBStructurePanel extends AbstractViewPanel {
	DefaultController controller;

	private JPanel panel;
	private JPanel textPanel;
	private final Action actionSubmit = new ActionSubmit();
	private JScrollPane scrollPane;
	private JButton btnSubmit;
	private JTable tblResults;
	private JButton btnExport;
	
	/**
	 * Create the frame.
	 */
	public ExportPGDBStructurePanel(DefaultController controller) {
		this.controller = controller;
		initComponents();
        localInitialization();
    }

    public void localInitialization() {
    	//Add self as property change event listener of the controller
    	controller.addView(this);
    }
    
    private void initComponents() {
		setMinimumSize(new Dimension(800, 525));
		setName("SimpleBrowser");
		
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(new BorderLayout(0, 0));
		
		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		add(panel, BorderLayout.NORTH);
		
		btnSubmit = new JButton("Search");
		panel.add(btnSubmit);
		btnSubmit.setAction(actionSubmit);
		
		btnExport = new JButton("Export");
		panel.add(btnExport);
		
		textPanel = new JPanel();
		add(textPanel, BorderLayout.CENTER);
		GridBagLayout gbl_textPanel = new GridBagLayout();
		gbl_textPanel.columnWidths = new int[]{503, 0};
		gbl_textPanel.rowHeights = new int[]{132, 0};
		gbl_textPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_textPanel.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		textPanel.setLayout(gbl_textPanel);
		
		scrollPane = new JScrollPane();
		scrollPane.setMinimumSize(new Dimension(10, 10));
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		textPanel.add(scrollPane, gbc_scrollPane);
		
		tblResults = new JTable();
		scrollPane.setViewportView(tblResults);
	}
	
	private class ActionSubmit extends AbstractAction {
		public ActionSubmit() {
			putValue(NAME, "Submit");
			putValue(SHORT_DESCRIPTION, "Submits query from text field and returns results to the primary table.");
		}
		public void actionPerformed(ActionEvent e) {
			DefaultTableModel dtm = controller.getPGDBStructure("BC-1", false, false); //TODO search type selection
			tblResults.setModel(dtm);
			revalidate();
			repaint();
		}
	}
	
	private class ActionExport extends AbstractAction {
		public ActionExport() {
			putValue(NAME, "Export");
			putValue(SHORT_DESCRIPTION, "Export table to file.");
		}
		public void actionPerformed(ActionEvent e) {
			//TODO
		}
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		
	}
	
}
