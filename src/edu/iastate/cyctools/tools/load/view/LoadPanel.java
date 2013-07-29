package edu.iastate.cyctools.tools.load.view;

import javax.imageio.ImageIO;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import edu.iastate.cyctools.DefaultController;
import edu.iastate.cyctools.DefaultStateModel.State;
import edu.iastate.cyctools.externalSourceCode.AbstractViewPanel;
import edu.iastate.cyctools.tools.load.fileAdaptors.FileAdaptor;
import edu.iastate.cyctools.tools.load.fileAdaptors.MaizeAdaptor;
import edu.iastate.cyctools.tools.load.fileAdaptors.SimpleInterpreter;
import edu.iastate.cyctools.tools.load.model.AbstractFrameEdit;
import edu.iastate.cyctools.tools.load.model.BatchEditModel.Event;
import edu.iastate.cyctools.tools.load.model.BatchEditModel.Status;
import edu.iastate.cyctools.tools.load.model.DocumentModel;
import edu.iastate.cyctools.tools.load.model.BatchEditModel;
import edu.iastate.cyctools.tools.load.util.KeyValue;
import edu.iastate.javacyco.Frame;
import edu.iastate.javacyco.PtoolsErrorException;

import java.awt.CardLayout;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JSplitPane;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.JTextPane;
import javax.swing.JTabbedPane;
import javax.swing.ImageIcon;

@SuppressWarnings("serial")
public class LoadPanel extends AbstractViewPanel {
	private FileAdaptor selectedAdaptor;
	DefaultController controller;
	private JTable tableSpreadSheet;
	private CardLayout cardLayout;
	private JPanel contentPane;
	private JList<String> listFrames;
	private JTextArea textAreaOld;
	private JTextPane textAreaNew;
	private JTextArea textLog;
	private JTextArea textArea;
	private JTextArea textArea_1;
	private BatchEditModel batchEdits;
	private JCheckBox chckbxAppend;
	private JCheckBox chckbxIgnoreDuplicate;
	private JTabbedPane tabbedPane;
	
	private final Action actionBrowse = new ActionBrowse();
	private final Action actionUpload = new ActionUpload();
	private final Action actionSave = new ActionSave();
	private final Action actionRevert = new ActionRevert();
	private final Action actionOpen = new ActionOpen();
	private final Action actionBack = new ActionBack();
	private final Action actionPreview = new ActionPreview();
	private JTextField textFilePath;
	private JComboBox<KeyValue> cmbFormat;
	private JComboBox<AdaptorKeyValue> cmbAdaptor;
	private JTextField textMultipleValueDelimiter;
	private final Action actionBack2 = new ActionBack2();
	private final Action actionSaveLog = new ActionSaveLog();
	
	/**
	 * Create the frame.
	 */
	public LoadPanel(DefaultController controller) {
		this.controller = controller;
		initComponents();
        localInitialization();
    }

    public void localInitialization() {
    	//Add self as property change event listener of the controller
    	controller.addView(this);
    	
    	DocumentModel document = new DocumentModel();
		document.initDefault();
		controller.setDocumentModel(document);
		document.addPropertyChangeListener(controller);
    }
    
    private void initComponents() {
    	setPreferredSize(new Dimension(800, 400));
		setMinimumSize(new Dimension(800, 400));
		setName("SimpleBrowser");
		contentPane = this;
    	
    	JPanel optionsPanel = new JPanel();
    	JPanel filePanel = new JPanel();
    	JPanel previewPanel = new JPanel();
    	JPanel reviewPanel = new JPanel();
    	JPanel finalPanel = new JPanel();
    	
        JButton btnBrowse = new JButton("Browse");
        btnBrowse.setAction(actionBrowse);
        
        JButton btnPreview = new JButton("Preview");
        btnPreview.setAction(actionPreview);
        
        JButton btnRevert = new JButton("Revert");
        btnRevert.setAction(actionRevert);
        
        JButton btnSave = new JButton("Save");
        btnSave.setAction(actionSave);
        
        JButton btnOpen = new JButton("Open");
		btnOpen.setAction(actionOpen);
		
        JButton btnBack = new JButton("Back");
        btnBack.setAction(actionBack);
        
        JScrollPane SpreadsheetScrollPane = new JScrollPane();
        tableSpreadSheet = new JTable();
        JTableHeader th = tableSpreadSheet.getTableHeader();  
        th.setFont(new Font("Serif", Font.BOLD, 15)); 
        SpreadsheetScrollPane.setViewportView(tableSpreadSheet);
        
        setLayout(new CardLayout(0, 0));
        cardLayout = (CardLayout)(this.getLayout());
		add(optionsPanel, "OptionsPanel");
		add(filePanel, "FilePanel");
		add(previewPanel, "PreviewPanel");
		add(reviewPanel, "ReviewPanel");
		add(finalPanel, "FinalPanel");
		
		JButton button = new JButton("Save Log");
		button.setAction(actionSaveLog);
		finalPanel.add(button);
		
		JButton btnBack2 = new JButton("Back");
		btnBack2.setAction(actionBack2);
		
		JButton btnUpload = new JButton("Upload");
		btnUpload.setAction(actionUpload);
		
		JPanel panel = new JPanel();
		
		JSplitPane splitPane = new JSplitPane();
		
		JLabel lblPreviewChanges = new JLabel("<html><h3>Preview Changes</h3></html>");
		
		JLabel lblFramesToBe = new JLabel("Frames to be Updated");
		
		JLabel lblExistingFrameData = new JLabel("Existing Frame Data");
		
		JLabel lblFrameDataAfter = new JLabel("Frame Data after Update");
		GroupLayout gl_previewPanel = new GroupLayout(previewPanel);
		gl_previewPanel.setHorizontalGroup(
			gl_previewPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_previewPanel.createSequentialGroup()
					.addGroup(gl_previewPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_previewPanel.createSequentialGroup()
							.addContainerGap()
							.addGroup(gl_previewPanel.createParallelGroup(Alignment.TRAILING)
								.addGroup(gl_previewPanel.createSequentialGroup()
									.addComponent(btnBack2)
									.addPreferredGap(ComponentPlacement.RELATED, 608, Short.MAX_VALUE)
									.addComponent(btnUpload))
								.addGroup(Alignment.LEADING, gl_previewPanel.createSequentialGroup()
									.addComponent(panel, GroupLayout.PREFERRED_SIZE, 216, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(splitPane, GroupLayout.DEFAULT_SIZE, 558, Short.MAX_VALUE))))
						.addGroup(gl_previewPanel.createSequentialGroup()
							.addGap(303)
							.addComponent(lblPreviewChanges, GroupLayout.PREFERRED_SIZE, 129, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_previewPanel.createSequentialGroup()
							.addContainerGap()
							.addComponent(lblFramesToBe, GroupLayout.PREFERRED_SIZE, 214, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblExistingFrameData, GroupLayout.PREFERRED_SIZE, 247, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblFrameDataAfter, GroupLayout.PREFERRED_SIZE, 300, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		gl_previewPanel.setVerticalGroup(
			gl_previewPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_previewPanel.createSequentialGroup()
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(lblPreviewChanges)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_previewPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblExistingFrameData)
						.addComponent(lblFrameDataAfter)
						.addComponent(lblFramesToBe))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_previewPanel.createParallelGroup(Alignment.TRAILING, false)
						.addComponent(splitPane)
						.addComponent(panel, GroupLayout.DEFAULT_SIZE, 287, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_previewPanel.createParallelGroup(Alignment.TRAILING)
						.addComponent(btnUpload)
						.addComponent(btnBack2))
					.addContainerGap())
		);
		listFrames = new JList<String>(new DefaultListModel<String>());
		listFrames.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				updateComparison();
			}
		});
		listFrames.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollPane = new JScrollPane();
		panel.add(scrollPane);
		scrollPane.setViewportView(listFrames);
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.TRAILING)
				.addComponent(scrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 344, Short.MAX_VALUE)
		);
		panel.setLayout(gl_panel);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		splitPane.setLeftComponent(scrollPane_1);
		
		textAreaOld = new JTextArea();
		textAreaOld.setEditable(false);
		scrollPane_1.setViewportView(textAreaOld);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		splitPane.setRightComponent(scrollPane_2);
		
		textAreaNew = new JTextPane();
		textAreaNew.setEditable(false);
		scrollPane_2.setViewportView(textAreaNew);
		splitPane.setDividerLocation(250);
		
		previewPanel.setLayout(gl_previewPanel);
		
		textFilePath = new JTextField();
		textFilePath.setEditable(false);
		textFilePath.setColumns(10);
		
		Vector<KeyValue> modelFormat = new Vector<KeyValue>();
        modelFormat.addElement( new KeyValue(1, "Comma-separated values (CSV)"));
        modelFormat.addElement( new KeyValue(2, "Tab-delimited file (tab)"));
        cmbFormat = new JComboBox<KeyValue>(modelFormat);
		
		chckbxAppend = new JCheckBox("Append new data to existing values?");
		chckbxAppend.setSelected(true);
		
		chckbxIgnoreDuplicate = new JCheckBox("Ignore Duplicates?");
		chckbxIgnoreDuplicate.setSelected(true);
		
		JLabel lblNewLabel = new JLabel("Select Input File");
		
		JLabel lblNewLabel_1 = new JLabel("Select file format");
		
		JLabel lblNewLabel_3 = new JLabel("Multiple value delimiter");
		
		textMultipleValueDelimiter = new JTextField();
		textMultipleValueDelimiter.setText("$");
		textMultipleValueDelimiter.setColumns(10);
		
		JLabel lblNewLabel_4 = new JLabel("Append values or overwrite existing");
		
		JLabel lblNewLabel_5 = new JLabel("Check if this value exists before importing");
		
		JLabel lblNewLabel_2 = new JLabel("");
		lblNewLabel_2.setOpaque(true);
		lblNewLabel_2.setIcon(new ImageIcon("C:\\Users\\Jesse\\workspace\\CycTools\\Images\\test.png"));
		
		GroupLayout gl_optionsPanel = new GroupLayout(optionsPanel);
		gl_optionsPanel.setHorizontalGroup(
			gl_optionsPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_optionsPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_optionsPanel.createParallelGroup(Alignment.TRAILING)
						.addComponent(lblNewLabel_5)
						.addComponent(lblNewLabel_4)
						.addComponent(lblNewLabel_3)
						.addComponent(lblNewLabel_1)
						.addComponent(lblNewLabel))
					.addGap(18)
					.addGroup(gl_optionsPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_optionsPanel.createParallelGroup(Alignment.LEADING)
							.addGroup(gl_optionsPanel.createSequentialGroup()
								.addComponent(btnBrowse)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(textFilePath, GroupLayout.PREFERRED_SIZE, 341, GroupLayout.PREFERRED_SIZE))
							.addComponent(cmbFormat, GroupLayout.PREFERRED_SIZE, 259, GroupLayout.PREFERRED_SIZE)
							.addComponent(chckbxIgnoreDuplicate, GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE)
							.addComponent(btnOpen))
						.addGroup(gl_optionsPanel.createSequentialGroup()
							.addGroup(gl_optionsPanel.createParallelGroup(Alignment.TRAILING, false)
								.addComponent(textMultipleValueDelimiter, Alignment.LEADING, 0, 0, Short.MAX_VALUE)
								.addComponent(chckbxAppend, Alignment.LEADING))
							.addPreferredGap(ComponentPlacement.RELATED, 211, GroupLayout.PREFERRED_SIZE)))
					.addGap(158))
				.addGroup(gl_optionsPanel.createSequentialGroup()
					.addGap(66)
					.addComponent(lblNewLabel_2)
					.addContainerGap(81, Short.MAX_VALUE))
		);
		gl_optionsPanel.setVerticalGroup(
			gl_optionsPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_optionsPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblNewLabel_2, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
					.addGroup(gl_optionsPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnBrowse)
						.addComponent(textFilePath, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_optionsPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(cmbFormat, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel_1))
					.addGap(18)
					.addGroup(gl_optionsPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(textMultipleValueDelimiter, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel_3))
					.addGap(13)
					.addGroup(gl_optionsPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel_4)
						.addComponent(chckbxAppend))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_optionsPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(chckbxIgnoreDuplicate)
						.addComponent(lblNewLabel_5))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnOpen)
					.addGap(131))
		);
		optionsPanel.setLayout(gl_optionsPanel);
        
		Vector<AdaptorKeyValue> modelAdaptor = new Vector<AdaptorKeyValue>();
		modelAdaptor.addElement( new AdaptorKeyValue(new SimpleInterpreter(), "Standard CSV: Column header determines slot label"));
		modelAdaptor.addElement( new AdaptorKeyValue(null, "Annotation Mod: FrameID, SlotValue, AnnotationValue.  Column header determines label")); //TODO implement the generic annotation adaptor
		modelAdaptor.addElement( new AdaptorKeyValue(new MaizeAdaptor(), "MaizeGDB Custom: frameID, goTerm, pubMedID, evCode, timeStampString (dd-mm-yyyy hh-mm-ss), curator"));
        cmbAdaptor = new JComboBox<AdaptorKeyValue>(modelAdaptor);
        
        GroupLayout gl_filePanel = new GroupLayout(filePanel);
        gl_filePanel.setHorizontalGroup(
        	gl_filePanel.createParallelGroup(Alignment.TRAILING)
        		.addGroup(gl_filePanel.createSequentialGroup()
        			.addContainerGap()
        			.addGroup(gl_filePanel.createParallelGroup(Alignment.TRAILING)
        				.addComponent(SpreadsheetScrollPane, GroupLayout.DEFAULT_SIZE, 777, Short.MAX_VALUE)
        				.addGroup(gl_filePanel.createSequentialGroup()
        					.addComponent(btnBack)
        					.addPreferredGap(ComponentPlacement.RELATED, 258, Short.MAX_VALUE)
        					.addComponent(cmbAdaptor, GroupLayout.PREFERRED_SIZE, 375, GroupLayout.PREFERRED_SIZE)
        					.addGap(18)
        					.addComponent(btnPreview)))
        			.addGap(13))
        );
        gl_filePanel.setVerticalGroup(
        	gl_filePanel.createParallelGroup(Alignment.TRAILING)
        		.addGroup(gl_filePanel.createSequentialGroup()
        			.addContainerGap()
        			.addComponent(SpreadsheetScrollPane, GroupLayout.DEFAULT_SIZE, 344, Short.MAX_VALUE)
        			.addPreferredGap(ComponentPlacement.UNRELATED)
        			.addGroup(gl_filePanel.createParallelGroup(Alignment.BASELINE)
        				.addComponent(btnPreview)
        				.addComponent(btnBack)
        				.addComponent(cmbAdaptor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        			.addContainerGap())
        );
        filePanel.setLayout(gl_filePanel);
        
        JLabel lblSummaryResults = new JLabel("Summary Results");
        
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        
        GroupLayout gl_reviewPanel = new GroupLayout(reviewPanel);
        gl_reviewPanel.setHorizontalGroup(
        	gl_reviewPanel.createParallelGroup(Alignment.LEADING)
        		.addGroup(gl_reviewPanel.createSequentialGroup()
        			.addContainerGap()
        			.addGroup(gl_reviewPanel.createParallelGroup(Alignment.LEADING)
        				.addComponent(lblSummaryResults)
        				.addGroup(gl_reviewPanel.createParallelGroup(Alignment.TRAILING, false)
        					.addGroup(gl_reviewPanel.createSequentialGroup()
        						.addComponent(btnSave)
        						.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        						.addComponent(btnRevert))
        					.addComponent(tabbedPane, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 773, GroupLayout.PREFERRED_SIZE)))
        			.addGap(17))
        );
        gl_reviewPanel.setVerticalGroup(
        	gl_reviewPanel.createParallelGroup(Alignment.TRAILING)
        		.addGroup(gl_reviewPanel.createSequentialGroup()
        			.addGap(14)
        			.addComponent(lblSummaryResults)
        			.addPreferredGap(ComponentPlacement.UNRELATED)
        			.addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
        			.addGap(7)
        			.addGroup(gl_reviewPanel.createParallelGroup(Alignment.BASELINE)
        				.addComponent(btnRevert)
        				.addComponent(btnSave))
        			.addContainerGap())
        );
        
        JScrollPane scrollPane_3 = new JScrollPane();
        tabbedPane.addTab("All Events", null, scrollPane_3, null);
        
        textLog = new JTextArea();
        scrollPane_3.setViewportView(textLog);
        
        JScrollPane scrollPane_4 = new JScrollPane();
        tabbedPane.addTab("Successful Imports", null, scrollPane_4, null);
        
        textArea = new JTextArea();
        scrollPane_4.setViewportView(textArea);
        
        JScrollPane scrollPane_5 = new JScrollPane();
        tabbedPane.addTab("Failed Imports", null, scrollPane_5, null);
        
        textArea_1 = new JTextArea();
        scrollPane_5.setViewportView(textArea_1);
        reviewPanel.setLayout(gl_reviewPanel);
	}
    

	private void updateComparison() {
		String frameID = "";
		try {
			frameID = listFrames.getSelectedValue().toString();
		} catch (NullPointerException e) {
			return;  // Ignore request to compare frame if no frame is selected (or list is empty)
		}
		
		String originalFrameString = controller.frameToString(frameID);
		
		if (originalFrameString == null || originalFrameString.equalsIgnoreCase("")) {
			String message = "Frame " + frameID + " does not exist in database " + controller.getSelectedOrganism();
			textAreaOld.setText(message);
			textAreaNew.setText(message);
			return;
		}
		
		textAreaOld.setText(originalFrameString);
		
		// select the frame edits which relate to this frame
		ArrayList<AbstractFrameEdit> frameEditArray = batchEdits.getFrameEditsMap().get(frameID);
		
		// apply frame edits to local copy of frame
		Frame resultFrame = controller.updateLocalFrame(frameID, frameEditArray);
		
		// return print of the updated frame
		String updatedFrameString = controller.frameToString(resultFrame);
		textAreaNew.setText(updatedFrameString);
		int currentPosition = 0;
		for (String line : updatedFrameString.split("\n")) {
			if (!originalFrameString.contains(line)) {
				DefaultHighlighter.DefaultHighlightPainter highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
				try {
					textAreaNew.getHighlighter().addHighlight(currentPosition, currentPosition + line.length(), highlightPainter);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
			currentPosition += line.length()+1;
		}
	}
	
	
	private void populateListOfFrames() throws PtoolsErrorException {
		batchEdits = new BatchEditModel(selectedAdaptor.tableToFrameUpdates(tableSpreadSheet.getModel()));
		batchEdits.addPropertyChangeListener(controller);
		
		DefaultListModel<String> listModel = (DefaultListModel<String>) listFrames.getModel();
		for (String frameID : batchEdits.getFrameIDSet()) {
			listModel.addElement(frameID);
		}
	}
    
    
    private class ActionBrowse extends AbstractAction {
		public ActionBrowse() {
			putValue(NAME, "Browse");
			putValue(SHORT_DESCRIPTION, "Browse local files");
		}
		public void actionPerformed(ActionEvent e) {
			JFileChooser fileChooser = new JFileChooser();
			if (fileChooser.showOpenDialog((Component) e.getSource()) == JFileChooser.APPROVE_OPTION) {
				textFilePath.setText(fileChooser.getSelectedFile().getAbsolutePath());
			}
		}
	}
	
	private class ActionUpload extends AbstractAction {
		public ActionUpload() {
			putValue(NAME, "Update Database");
			putValue(SHORT_DESCRIPTION, "Upload data table to database");
		}
		public void actionPerformed(ActionEvent e) {
			controller.lockDatabaseOperation();
			
			cardLayout.show(contentPane, "ReviewPanel");
			batchEdits.commitAll(controller.getConnection());
			
			String allEventLog = "";
			String successEventLog = "";
			String failedEventLog = "";
			int events = 0;
			int successfulEvents = 0;
			int failedEvents = 0;
			for (Event event : batchEdits.getEventLog()) {
				allEventLog += event.toString() + "\n";
				events++;
				if (event.getStatus() == Status.SUCCESS) {
					successEventLog += event.toString() + "\n";
					successfulEvents++;
				} else {
					failedEventLog += event.toString() + "\n";
					failedEvents++;
				}
			}
			textLog.setText(allEventLog);
			textArea.setText(successEventLog);
			textArea_1.setText(failedEventLog);
			tabbedPane.setTitleAt(0, "All Events (" + events + ")");
			tabbedPane.setTitleAt(1, "Successful Imports (" + successfulEvents + ")");
			tabbedPane.setTitleAt(2, "Failed Imports (" + failedEvents + ")");
		}
	}
	
	private class ActionSave extends AbstractAction {
		public ActionSave() {
			putValue(SMALL_ICON, new ImageIcon("C:\\Users\\Jesse\\workspace\\CycTools\\Images\\accept\\1374877061_button_ok.png"));
			putValue(NAME, "Save");
			putValue(SHORT_DESCRIPTION, "Save changes to database");
		}
		public void actionPerformed(ActionEvent e) {
			controller.saveDataBase();
			controller.unlockDatabaseOperation();
			cardLayout.show(contentPane, "FinalPanel");
		}
	}
	
	private class ActionRevert extends AbstractAction {
		public ActionRevert() {
			putValue(SMALL_ICON, new ImageIcon("C:\\Users\\Jesse\\workspace\\CycTools\\Images\\accept\\1374877110_button_cancel.png"));
			putValue(NAME, "Undo Changes");
			putValue(SHORT_DESCRIPTION, "Revert changes to database");
		}
		public void actionPerformed(ActionEvent e) {
			controller.revertDataBase();
			controller.unlockDatabaseOperation();
			cardLayout.show(contentPane, "OptionsPanel");
		}
	}
	
	private class ActionOpen extends AbstractAction {
		public ActionOpen() {
			putValue(NAME, "Open");
			putValue(SHORT_DESCRIPTION, "Open file");
		}
		public void actionPerformed(ActionEvent e) {
			File file = new File(textFilePath.getText());
			
			String fileDelimiter = ",";
			if (((KeyValue)cmbFormat.getSelectedItem()).getKey() == 1) fileDelimiter = ","; 
			else if (((KeyValue)cmbFormat.getSelectedItem()).getKey() == 2) fileDelimiter = "\t";
			
			controller.changeDocumentFile(file, fileDelimiter);
			if (controller.getDocumentModel().getFile() == null) {
				JOptionPane.showMessageDialog(DefaultController.mainJFrame, "Please select an input file", "File error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			cardLayout.show(contentPane, "FilePanel");
		}
	}
	
	private class ActionPreview extends AbstractAction {
		public ActionPreview() {
			putValue(NAME, "Preview");
			putValue(SHORT_DESCRIPTION, "Preview file before import");
		}
		public void actionPerformed(ActionEvent e) {
			cardLayout.show(contentPane, "PreviewPanel");
			try {
				selectedAdaptor = ((AdaptorKeyValue)cmbAdaptor.getSelectedItem()).getKey();
				selectedAdaptor.setMultipleValueDelimiter(textMultipleValueDelimiter.getText());
				selectedAdaptor.setAppend(chckbxAppend.getModel().isSelected());
				selectedAdaptor.setIgnoreDuplicates(chckbxIgnoreDuplicate.getModel().isSelected());
			} catch (Exception exception) {
				System.out.println("Error selecting adaptor, unknown adaptor.");
			}
			
			try {
				populateListOfFrames();
			} catch (PtoolsErrorException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	private class ActionBack extends AbstractAction {
		public ActionBack() {
			putValue(NAME, "Back");
			putValue(SHORT_DESCRIPTION, "Back");
		}
		public void actionPerformed(ActionEvent e) {
			cardLayout.show(contentPane, "OptionsPanel");
		}
	}
	
	private class ActionBack2 extends AbstractAction {
		public ActionBack2() {
			putValue(NAME, "Back");
			putValue(SHORT_DESCRIPTION, "Back");
		}
		public void actionPerformed(ActionEvent e) {
			cardLayout.show(contentPane, "FilePanel");
		}
	}
	
	private void resetForm() {
		cardLayout.show(contentPane, "OptionsPanel");
		selectedAdaptor = null;
		tableSpreadSheet.setModel(new DefaultTableModel());
		listFrames.setModel(new DefaultListModel<String>());
		textAreaOld.setText("");
		textAreaNew.setText("");
		textLog.setText("");
		batchEdits = null;
		chckbxAppend.setSelected(true);
		chckbxIgnoreDuplicate.setSelected(true);
		cmbFormat.setSelectedIndex(0);
		cmbAdaptor.setSelectedIndex(0);
		
		textLog.setText("");
		textArea.setText("");
		textArea_1.setText("");
		tabbedPane.setTitleAt(0, "All Events");
		tabbedPane.setTitleAt(1, "Successful Imports");
		tabbedPane.setTitleAt(2, "Failed Imports");
		
		textFilePath.setText("");
		textMultipleValueDelimiter.setText("$");
	}
	
	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(DefaultController.BROWSER_STATE_PROPERTY) && evt.getNewValue() != null) {
			if (evt.getNewValue() == State.LOAD) {
				resetForm();
			}
		}
		if (evt.getPropertyName().equals(DefaultController.DOCUMENT_TABLEMODEL_PROPERTY) && evt.getNewValue() != null) {
			DefaultTableModel dtm = (DefaultTableModel)evt.getNewValue();
			tableSpreadSheet.setModel(dtm);
			revalidate();
			repaint();
		}
	}

	private class AdaptorKeyValue {
		private FileAdaptor key;
		private String value;

		public AdaptorKeyValue(FileAdaptor key, String value) {
			this.key = key;
			this.value = value;
		}

		public FileAdaptor getKey() {
			return key;
		}

		public String getValue() {
			return value;
		}

		public String toString() {
			return value;
		}
	}
	
	private class ActionSaveLog extends AbstractAction {
		public ActionSaveLog() {
			putValue(NAME, "Save Log");
			putValue(SHORT_DESCRIPTION, "Save import log files");
		}
		public void actionPerformed(ActionEvent e) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new java.io.File("."));
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fileChooser.setAcceptAllFileFilterUsed(false);
		    
			if (fileChooser.showOpenDialog((Component) e.getSource()) == JFileChooser.APPROVE_OPTION) {
				printString(new File(fileChooser.getSelectedFile() + File.separator + "eventLog.txt"), textLog.getText());
				printString(new File(fileChooser.getSelectedFile() + File.separator + "successLog.txt"), textArea.getText());
				printString(new File(fileChooser.getSelectedFile() + File.separator + "failLog.txt"), textArea_1.getText());
				JOptionPane.showMessageDialog(DefaultController.mainJFrame, "Files saved", "Success", JOptionPane.PLAIN_MESSAGE);
			}
		}
	}
	
	// Offers buffered printing using File class for interoperability of file paths between OS.  Uses default encoding.
	private void printString(File fileName, String text) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(fileName));
			writer.write(text);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		    try {
		        if (writer != null)
		        writer.close( );
		    } catch (IOException e) {
		    	e.printStackTrace();
		    }
		}
 	}
}
