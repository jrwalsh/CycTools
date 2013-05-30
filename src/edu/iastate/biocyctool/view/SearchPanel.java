package edu.iastate.biocyctool.view;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.table.DefaultTableModel;
import java.awt.FlowLayout;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import edu.iastate.biocyctool.controller.BrowserController;
import edu.iastate.biocyctool.util.util.Util;
import edu.iastate.biocyctool.util.view.AbstractViewPanel;
import edu.iastate.javacyco.Frame;
import edu.iastate.javacyco.Gene;
import edu.iastate.javacyco.JavacycConnection;
import edu.iastate.javacyco.Protein;
import edu.iastate.javacyco.PtoolsErrorException;
import javax.swing.JEditorPane;
import java.awt.event.ActionListener;

@SuppressWarnings("serial")
public class SearchPanel extends AbstractViewPanel {
	BrowserController controller;
	private ProgressMonitor progressMonitor;
	private GetSearchResultsTableTask task;

	private JPanel queryPanel;
	private JPanel resultPanel;
	private final Action actionSubmit = new ActionSubmit();
	private JScrollPane scrollPaneResults;
	private JButton btnSubmit;
	private JTable tblResults;
	private JEditorPane txtSearch;
	private JScrollPane scrollPaneQuery;
	
	/**
	 * Create the frame.
	 */
	public SearchPanel(BrowserController controller) {
		this.controller = controller;
		initComponents();
        localInitialization();
    }

    public void localInitialization() {
    	Util.installContextMenu(txtSearch);
    }
    
    private void initComponents() {
		setMinimumSize(new Dimension(800, 525));
		setName("SimpleBrowser");
		
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(new BorderLayout(0, 0));
		
		queryPanel = new JPanel();
		queryPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		add(queryPanel, BorderLayout.NORTH);
		
		scrollPaneQuery = new JScrollPane();
		queryPanel.add(scrollPaneQuery);
		
		txtSearch = new JEditorPane();
		txtSearch.setPreferredSize(new Dimension(400, 80));
		scrollPaneQuery.setViewportView(txtSearch);
		
		btnSubmit = new JButton("Search");
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		queryPanel.add(btnSubmit);
		btnSubmit.setAction(actionSubmit);
		
		resultPanel = new JPanel();
		resultPanel.setMinimumSize(new Dimension(450, 400));
		add(resultPanel, BorderLayout.CENTER);
		GridBagLayout gbl_resultPanel = new GridBagLayout();
		gbl_resultPanel.columnWidths = new int[]{503, 0};
		gbl_resultPanel.rowHeights = new int[]{132, 0};
		gbl_resultPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_resultPanel.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		resultPanel.setLayout(gbl_resultPanel);
		
		scrollPaneResults = new JScrollPane();
		scrollPaneResults.setMinimumSize(new Dimension(10, 10));
		GridBagConstraints gbc_scrollPaneResults = new GridBagConstraints();
		gbc_scrollPaneResults.fill = GridBagConstraints.BOTH;
		gbc_scrollPaneResults.gridx = 0;
		gbc_scrollPaneResults.gridy = 0;
		resultPanel.add(scrollPaneResults, gbc_scrollPaneResults);
		
		tblResults = new JTable();
		scrollPaneResults.setViewportView(tblResults);
	}
    
	private class ActionSubmit extends AbstractAction {
		public ActionSubmit() {
			putValue(NAME, "Submit");
			putValue(SHORT_DESCRIPTION, "Submits query from text field and returns results in the primary tab.");
		}
		
		public void actionPerformed(ActionEvent e) {
			getSearchResultsTable(txtSearch.getText(), Gene.GFPtype); //TODO selectable type
		}
	}
	
	public void getSearchResultsTable(String text, String type) {
		progressMonitor = new ProgressMonitor(BrowserController.mainJFrame, "Searching terms...", "", 0, 100);
		progressMonitor.setMinimum(0);
		progressMonitor.setProgress(0);
		task = new GetSearchResultsTableTask(text, type);
		
		task.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                if ("progress".equals(e.getPropertyName())) {
                	progressMonitor.setProgress((Integer) e.getNewValue());
                }
            }
        });

		task.execute();
	}
	
	private class GetSearchResultsTableTask extends SwingWorker<DefaultTableModel, Void> {
		private String text;
		private String type;
		
		public GetSearchResultsTableTask(String text, String type) {
			this.text = text;
			this.type = type;
		}
		
		@Override
		public DefaultTableModel doInBackground() {
			JavacycConnection conn = controller.getConnection();
			
			int progress = 0;
			setProgress(progress);
			try {
				// Parse text for search terms
				// Expect 1 term per line from the user
				String[] terms = text.split("\n"); //TODO more checking for bad input

				// Create dataTable of results
				Object[] header = new String[]{"Search Term", "Results"};
				Object[][] data = new Object[terms.length][2];
				for (int i=0; i<terms.length; i++) {
					String term = terms[i].trim();
					
					ArrayList<Frame> results = conn.search(term, type);
					String resultString = "";
					for (Frame result : results) resultString += result.getLocalID() + ",";
					if (resultString.length() > 0) resultString = resultString.substring(0, resultString.length()-1);
					
					data[i] = new String[]{term, resultString};
					
					progressMonitor.setNote("Completed " + i + " of " + terms.length);
					progress = (int) ((i*100)/terms.length);
					setProgress(progress);
				}
				
				DefaultTableModel dtm = new DefaultTableModel(data, header);
				return dtm;
			} catch (PtoolsErrorException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		@Override
		public void done() {
			try {
				tblResults.setModel(task.get());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			revalidate();
			repaint();
			
			progressMonitor.setProgress(0);
			progressMonitor.close();
		}
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		
	}
	
}
