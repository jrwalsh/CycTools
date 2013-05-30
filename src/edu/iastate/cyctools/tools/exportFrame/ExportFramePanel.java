package edu.iastate.cyctools.tools.exportFrame;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;

import edu.iastate.cyctools.DefaultController;
import edu.iastate.cyctools.DefaultStateModel.State;
import edu.iastate.cyctools.externalSourceCode.AbstractViewPanel;
import edu.iastate.cyctools.tools.load.util.CustomInterpreter;
import edu.iastate.javacyco.Frame;
import edu.iastate.javacyco.JavacycConnection;
import edu.iastate.javacyco.PtoolsErrorException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.JTable
;
import java.awt.Dimension;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

@SuppressWarnings({"serial", "unchecked"})
public class ExportFramePanel extends AbstractViewPanel {
	private ProgressMonitor progressMonitor;
	private GetFramesOfTypeTask task;
	DefaultController controller;
	private JTable tblPreviewExport;
	private JTree treeFrameHierarchy;
	private ArrayList<String> expandedNodes;
	private final Action actionPreview = new ActionPreview();
	private final Action actionExport = new ActionExport();

	/**
	 * Create the frame.
	 */
	public ExportFramePanel(DefaultController controller) {
		this.controller = controller;
		initComponents();
        localInitialization();
    }

	public void localInitialization() {
		//Add self as property change event listener of the controller
    	controller.addView(this);
	}
    
    private void initComponents() {
    	setPreferredSize(new Dimension(800, 400));
    	
    	JSplitPane splitPane = new JSplitPane();
    	GroupLayout groupLayout = new GroupLayout(this);
    	groupLayout.setHorizontalGroup(
    		groupLayout.createParallelGroup(Alignment.LEADING)
    			.addGroup(groupLayout.createSequentialGroup()
    				.addContainerGap()
    				.addComponent(splitPane, GroupLayout.DEFAULT_SIZE, 780, Short.MAX_VALUE)
    				.addContainerGap())
    	);
    	groupLayout.setVerticalGroup(
    		groupLayout.createParallelGroup(Alignment.TRAILING)
    			.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
    				.addContainerGap()
    				.addComponent(splitPane, GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)
    				.addContainerGap())
    	);
    	
    	JScrollPane scrollPaneViewer = new JScrollPane();
    	splitPane.setRightComponent(scrollPaneViewer);
    	
    	tblPreviewExport = new JTable();
    	tblPreviewExport.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    	scrollPaneViewer.setViewportView(tblPreviewExport);
    	
    	JPanel panel = new JPanel();
    	splitPane.setLeftComponent(panel);
    	
    	JButton btnExport = new JButton("Export");
    	btnExport.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent arg0) {
    		}
    	});
    	btnExport.setAction(actionExport);
    	
    	JButton btnRefreshPreview = new JButton("Refresh Preview");
    	btnRefreshPreview.setAction(actionPreview);
    	
    	JScrollPane scrollPaneController = new JScrollPane();
    	
    	GroupLayout gl_panel = new GroupLayout(panel);
    	gl_panel.setHorizontalGroup(
    		gl_panel.createParallelGroup(Alignment.LEADING)
    			.addGroup(gl_panel.createSequentialGroup()
    				.addContainerGap()
    				.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
    					.addComponent(scrollPaneController, GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE)
    					.addGroup(gl_panel.createSequentialGroup()
    						.addComponent(btnRefreshPreview)
    						.addPreferredGap(ComponentPlacement.RELATED)
    						.addComponent(btnExport)
    						.addGap(0, 0, Short.MAX_VALUE)))
    				.addContainerGap())
    	);
    	gl_panel.setVerticalGroup(
    		gl_panel.createParallelGroup(Alignment.LEADING)
    			.addGroup(gl_panel.createSequentialGroup()
    				.addContainerGap()
    				.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
    					.addComponent(btnRefreshPreview)
    					.addComponent(btnExport))
    				.addPreferredGap(ComponentPlacement.RELATED)
    				.addComponent(scrollPaneController, GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)
    				.addContainerGap())
    	);
    	
    	treeFrameHierarchy = new JTree();
    	treeFrameHierarchy.addTreeWillExpandListener(new MyTreeWillExpandListener());
    	
    	scrollPaneController.setViewportView(treeFrameHierarchy);
    	panel.setLayout(gl_panel);
    	setLayout(groupLayout);
	}
    
	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		if (evt.getNewValue() == State.EXPORT) {
			expandedNodes = new ArrayList<String>();
			try {
				DefaultMutableTreeNode top = new DefaultMutableTreeNode("FRAMES");
				for (String childID : controller.getPGDBChildrenOfFrame("FRAMES")) {
					DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(childID);
					expandedNodes.add(childID);
					for (String grandChildID : controller.getPGDBChildrenOfFrame(childID)) {
						childNode.add(new DefaultMutableTreeNode(grandChildID));
						expandedNodes.add(grandChildID);
					}
					top.add(childNode);
				}
				DefaultTreeModel model = new DefaultTreeModel(top);
				treeFrameHierarchy.setModel(model);
			} catch (Exception e) {
				DefaultTreeModel model = new DefaultTreeModel(new DefaultMutableTreeNode("Error: Could not download hierarchy."));
				treeFrameHierarchy.setModel(model);
			}
		}
	}
	
	private class MyTreeWillExpandListener implements TreeWillExpandListener {
		public MyTreeWillExpandListener() {
		}
		
		@Override
		public void treeWillCollapse(TreeExpansionEvent arg0) throws ExpandVetoException {
		}

		@Override
		public void treeWillExpand(TreeExpansionEvent arg0) throws ExpandVetoException {
			DefaultMutableTreeNode expandedNode = (DefaultMutableTreeNode) arg0.getPath().getLastPathComponent();
			for (int i = 0; i<expandedNode.getChildCount(); i++) {
				DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) expandedNode.getChildAt(i);
				
				if (!expandedNodes.contains(childNode.toString())) System.err.println("Tree not expanding correctly"); // Should not reach here
				
				for (String grandChildID : controller.getPGDBChildrenOfFrame(childNode.toString())) {
					childNode.add(new DefaultMutableTreeNode(grandChildID));
					expandedNodes.add(grandChildID);
				}
			}
			
		}
		
	}
	
//	private class ActionPreview extends AbstractAction {
//		public ActionPreview() {
//			putValue(NAME, "Preview");
//			putValue(SHORT_DESCRIPTION, "Load a preview of the selected frame instances in the preview table");
//		}
//		public void actionPerformed(ActionEvent e) {
//			ArrayList<Frame> frames = new ArrayList<Frame>();
//			if (treeFrameHierarchy.getSelectionPath() != null) {
//				frames = controller.getFramesOfType(treeFrameHierarchy.getSelectionPath().getLastPathComponent().toString());
//			}
//			
//			// create table from frames
//			Object[] header = new String[]{"FrameID", "CommonName"};
//			Object[][] data = new Object[frames.size()][header.length];
//			int i = 0;
//			for (Frame frame : frames) {
//				try {
//					data[i] = new String[]{frame.getLocalID(), frame.getCommonName()};
//				} catch (PtoolsErrorException e1) {
//					e1.printStackTrace();
//				}
//				i++;
//			}
//			
//			DefaultTableModel dtm = new DefaultTableModel(data, header);
//			tblPreviewExport.setModel(dtm);
//		}
//	}
	
	private class ActionPreview extends AbstractAction {
		public ActionPreview() {
			putValue(NAME, "Preview");
			putValue(SHORT_DESCRIPTION, "Load a preview of the selected frame instances in the preview table");
		}
		public void actionPerformed(ActionEvent e) {
			if (treeFrameHierarchy.getSelectionPath() != null) {
				getFramesOfType(treeFrameHierarchy.getSelectionPath().getLastPathComponent().toString());
			}
		}
	}
	
	private void getFramesOfType(String type) {
		progressMonitor = new ProgressMonitor(DefaultController.mainJFrame, "Importing frame data...", "", 0, 100);
		progressMonitor.setMinimum(0);
		progressMonitor.setProgress(0);
		task = new GetFramesOfTypeTask(type);
		
		task.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                if ("progress".equals(e.getPropertyName())) {
                	progressMonitor.setProgress((Integer) e.getNewValue());
                	
                	if (progressMonitor.isCanceled()) {
                		task.cancel(true);
                	}
                }
            }
        });

		task.execute();
	}
	
	private class GetFramesOfTypeTask extends SwingWorker<DefaultTableModel, Void> {
		private String type;
		
		public GetFramesOfTypeTask(String type) {
			this.type = type;
		}
		
		@Override
		public DefaultTableModel doInBackground() {
			JavacycConnection conn = controller.getConnection();
			
			try {
				// Import frame data
				int progress = 0;
				setProgress(progress);
				ArrayList<Frame> frames = new ArrayList<Frame>();
				ArrayList<String> frameLabels = (ArrayList<String>)conn.getClassAllInstances(type);
				progressMonitor.setNote("Importing " + frameLabels.size() + " frames...");
				int processCount = 0;
				for (String frameLabel : frameLabels) {
					frames.add(Frame.load(conn, frameLabel));
					processCount++;
					progress = (int) ((processCount*100)/frameLabels.size());
					setProgress(progress);
					
					if (isCancelled()) return new DefaultTableModel();
				}
				
//				// Create table from frames
//				progress = 0;
//				setProgress(progress);
//				progressMonitor.setNote("Processing frames old way...");
//				
//				Object[] header = new String[]{"FrameID", "CommonName"};
//				Object[][] data = new Object[frames.size()][header.length];
//				int i = 0;
//				for (Frame frame : frames) {
//					try {
//						data[i] = new String[]{frame.getLocalID(), frame.getCommonName()};
//					} catch (PtoolsErrorException e1) {
//						e1.printStackTrace();
//					}
//					i++;
//					
//					progress = (int) ((i*100)/frames.size());
//					setProgress(progress);
//					if (isCancelled()) return new DefaultTableModel();
//				}
				
				
				
				
				progress = 0;
				setProgress(progress);
				progressMonitor.setNote("Processing frames...");
				
				TreeSet<String> slots = new TreeSet<String>(); //TODO potential slow down here. Prefer modal select slot screen.  minimum add this to the progress monitor
				for (Frame frame : frames) {
					for (String slotLabel : frame.getSlotLabels()) {
						slots.add(slotLabel);
					}
					if (isCancelled()) return new DefaultTableModel();
				}
				
				//TODO Dialog which slots user wants
				Object[] possibilities = {"All Slots", "GO-Term Annotations"};
				String s = (String)JOptionPane.showInputDialog(DefaultController.mainJFrame, "Select output format", "Select", JOptionPane.PLAIN_MESSAGE, null, possibilities, "All Slots");
				
				if (s.equalsIgnoreCase("GO-Term Annotations")) { //TODO potential slow down here. Prefer modal select slot screen.  minimum add this to the progress monitor
					CustomInterpreter interpreter = new CustomInterpreter();
					return interpreter.framesToTable(frames, conn);
				}
				
				Object[] header = new Object[slots.size() + 1];
				Object[][] data = new Object[frames.size()][header.length];
				
				int i = 0;
				String slotDelimiter = "\t";
				String valueDelimiter = "$";
				
				ArrayList<ArrayList<String>> rows = new ArrayList<ArrayList<String>>();
				for (Frame frame : frames) {
					ArrayList<String> row = new ArrayList<String>();
					
					// Format to CSV
					row.add(frame.getLocalID());
					
					for (String slotLabel : slots) {
						ArrayList<Object> slotValueObjects = frame.getSlotValues(slotLabel);
						String slotValue = "";
						for (Object slotValueObject : slotValueObjects) {
							if (slotValueObject instanceof ArrayList) {
								ArrayList<String> valueArray = (ArrayList<String>)slotValueObject;
								String valueString = "(";
								for (String value : valueArray) {
									valueString += value + " ";
								}
								if (valueArray.size() > 0) valueString = valueString.substring(0, valueString.length()-1);
								valueString += ")";
								slotValue += valueString + valueDelimiter;
							} else {
								String value = (String) slotValueObject;
								slotValue += value + valueDelimiter;
							}	
						}
						if (slotValue.endsWith(valueDelimiter)) slotValue = slotValue.substring(0, slotValue.length()-1);
						row.add(slotValue);
					}
					rows.add(row);
					
					i++;
					progress = (int) ((i*100)/frames.size());
					setProgress(progress);
					if (isCancelled()) return new DefaultTableModel();
				}
				
				header = new String[slots.size()+1];
				header[0] = "FrameID";
				int n = 1;
				for (String slotLabel : slots) {
					header[n] = slotLabel;
					n++;
				}
				
				data = new Object[frames.size()][header.length];
				for (int k = 0; k < rows.size(); k++) {
					ArrayList<String> row = rows.get(k);
					for (int l = 0; l < row.size(); l++) {
						data[k][l] = row.get(l);
						System.out.println(row.get(l));
					}
				}
				
				
				
				
				
				DefaultTableModel dtm = new DefaultTableModel(data, header);
				return dtm;
			} catch (PtoolsErrorException e) {
				e.printStackTrace();
				return new DefaultTableModel();
			}
		}
		
		@Override
		public void done() {
			try {
				if (!task.isCancelled()) tblPreviewExport.setModel(task.get());
				else tblPreviewExport.setModel(new DefaultTableModel());
			} catch (InterruptedException e) {
				tblPreviewExport.setModel(new DefaultTableModel());
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			revalidate();
			repaint();
			
			progressMonitor.setProgress(0);
			progressMonitor.close();
		}
	}
	
	private class ActionExport extends AbstractAction {
		public ActionExport() {
			putValue(NAME, "Export to File");
			putValue(SHORT_DESCRIPTION, "Export the preview table to a file");
		}
		public void actionPerformed(ActionEvent e) {
			if (tblPreviewExport.getModel().getRowCount() == 0) {
				JOptionPane.showMessageDialog(DefaultController.mainJFrame, "Nothing to export", "Warning", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			JFileChooser fc = new JFileChooser();
			fc.addChoosableFileFilter(new XMLFileFilter());
			fc.addChoosableFileFilter(new CSVFileFilter());
			int returnVal = fc.showSaveDialog(DefaultController.mainJFrame);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
	            File file = fc.getSelectedFile();
	            String path = file.getAbsolutePath();
	            
	            if (fc.getFileFilter().getDescription().equalsIgnoreCase(XMLFileFilter.xml)) {
	            	controller.printFramesToXML(path, treeFrameHierarchy.getSelectionPath().getLastPathComponent().toString());
	            } else {
	            	controller.printFramesToCSV(path, treeFrameHierarchy.getSelectionPath().getLastPathComponent().toString());
	            }
				
	        } else {
	            //ignore
	        }
		}
	}
	
	private class XMLFileFilter extends FileFilter {
		public final static String xml = "xml";

		@Override
		public boolean accept(File file) {
			if (file.isDirectory()) {
				return true;
			}
			
			String extension = getExtension(file);
			if (extension != null) {
				if (extension.equals(xml)) return true;
				else return false;
			}
			return false;
		}
		
		@Override
		public String getDescription() {
			return xml;
		}
		
		public String getExtension(File f) {
			String ext = null;
			String s = f.getName();
			int i = s.lastIndexOf('.');

			if (i > 0 && i < s.length() - 1) {
				ext = s.substring(i + 1).toLowerCase();
			}
			return ext;
		}
	}
	
	private class CSVFileFilter extends FileFilter {
		public final static String csv = "csv";

		@Override
		public boolean accept(File file) {
			if (file.isDirectory()) {
				return true;
			}
			
			String extension = getExtension(file);
			if (extension != null) {
				if (extension.equals(csv)) return true;
				else return false;
			}
			return false;
		}
		
		@Override
		public String getDescription() {
			return csv;
		}
		
		public String getExtension(File f) {
			String ext = null;
			String s = f.getName();
			int i = s.lastIndexOf('.');

			if (i > 0 && i < s.length() - 1) {
				ext = s.substring(i + 1).toLowerCase();
			}
			return ext;
		}
	}
}
