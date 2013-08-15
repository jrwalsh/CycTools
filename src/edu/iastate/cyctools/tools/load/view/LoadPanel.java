package edu.iastate.cyctools.tools.load.view;

import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter.Highlight;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import edu.iastate.cyctools.CycToolsError;
import edu.iastate.cyctools.DefaultController;
import edu.iastate.cyctools.InternalStateModel.State;
import edu.iastate.cyctools.externalSourceCode.AbstractViewPanel;
import edu.iastate.cyctools.tools.load.fileAdaptors.FileAdaptor;
import edu.iastate.cyctools.tools.load.fileAdaptors.MaizeAdaptor;
import edu.iastate.cyctools.tools.load.fileAdaptors.SimpleAnnotationValueImport;
import edu.iastate.cyctools.tools.load.fileAdaptors.SimpleSlotValueImport;
import edu.iastate.cyctools.tools.load.model.BatchUpdate.Event;
import edu.iastate.cyctools.tools.load.model.BatchUpdate.Status;
import edu.iastate.cyctools.tools.load.model.DocumentModel;
import edu.iastate.cyctools.tools.load.model.BatchUpdate;
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
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.event.ActionListener;

@SuppressWarnings("serial")
public class LoadPanel extends AbstractViewPanel {
	private FileAdaptor selectedAdaptor;
	DefaultController controller;
	private JTable tableSpreadSheet;
	private CardLayout cardLayout;
	private JPanel contentPane;
	private JList<String> listFrames;
	private JTextPane textAreaOld;
	private JTextPane textAreaNew;
	private JTextArea textLog;
	private JTextArea textArea;
	private JTextArea textArea_1;
	private BatchUpdate batchEdits;
	private JCheckBox chckbxAppend;
	private JCheckBox chckbxIgnoreDuplicate;
	private JTabbedPane tabbedPane;
	private JTextField textFilePath;
	private JComboBox<String> cmbFormat;
	private JComboBox<String> cmbAdaptor;
	private JTextField textMultipleValueDelimiter;
	private JCheckBox chckbxFilter;
	DefaultListModel<String> allFramesWithImports;
	DefaultListModel<String> framesWhichModifyKB;
	private Patch patch;
	private int currentDelta;
	private List<String> original;
	private List<String> revised;
	
	private final Action actionBrowse = new ActionBrowse();
	private final Action actionUpload = new ActionUpload();
	private final Action actionSave = new ActionSave();
	private final Action actionRevert = new ActionRevert();
	private final Action actionOpen = new ActionOpen();
	private final Action actionBack = new ActionBack();
	private final Action actionPreview = new ActionPreview();
	private final Action actionBack2 = new ActionBack2();
	private final Action actionSaveLog = new ActionSaveLog();
	private final Action actionNextDiff = new ActionNextDiff();
	
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
		
		JLabel lblPreviewChanges = new JLabel("<html><h3>Preview Changes</h3></html>");
		
		chckbxFilter = new JCheckBox("Show only frames which are altered");
		chckbxFilter.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if (chckbxFilter.isSelected()) {
					framesWhichModifyKB = batchEdits.framesWhichModifyKB(controller.getConnection());
					if (framesWhichModifyKB.isEmpty()) framesWhichModifyKB.addElement("No Frames will modify KB");
					listFrames.setModel(framesWhichModifyKB);
				} else {
					listFrames.setModel(allFramesWithImports);
				}
			}
		});
		
		JSplitPane splitPane_1 = new JSplitPane();
		
		JButton btnNextDiff = new JButton("New button");
		btnNextDiff.setAction(actionNextDiff);
		
		GroupLayout gl_previewPanel = new GroupLayout(previewPanel);
		gl_previewPanel.setHorizontalGroup(
			gl_previewPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_previewPanel.createSequentialGroup()
					.addGroup(gl_previewPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_previewPanel.createSequentialGroup()
							.addGap(303)
							.addComponent(lblPreviewChanges, GroupLayout.PREFERRED_SIZE, 129, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_previewPanel.createSequentialGroup()
							.addGap(10)
							.addComponent(btnBack2)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(chckbxFilter)
							.addPreferredGap(ComponentPlacement.RELATED, 298, Short.MAX_VALUE)
							.addComponent(btnNextDiff)
							.addGap(18)
							.addComponent(btnUpload))
						.addGroup(gl_previewPanel.createSequentialGroup()
							.addContainerGap()
							.addComponent(splitPane_1, GroupLayout.DEFAULT_SIZE, 780, Short.MAX_VALUE)))
					.addContainerGap())
		);
		gl_previewPanel.setVerticalGroup(
			gl_previewPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_previewPanel.createSequentialGroup()
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(lblPreviewChanges)
					.addGap(26)
					.addComponent(splitPane_1, GroupLayout.PREFERRED_SIZE, 286, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_previewPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnBack2)
						.addComponent(chckbxFilter)
						.addComponent(btnUpload)
						.addComponent(btnNextDiff))
					.addGap(31))
		);
		
		JPanel panel = new JPanel();
		splitPane_1.setLeftComponent(panel);
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
		
		JLabel lblFramesToBe = new JLabel("Frames to be Updated");
		lblFramesToBe.setFont(new Font("Arial", Font.BOLD, 16));
		lblFramesToBe.setAlignmentX(Component.CENTER_ALIGNMENT);
		scrollPane.setColumnHeaderView(lblFramesToBe);
		panel.setLayout(gl_panel);
		
		JSplitPane splitPane = new JSplitPane();
		splitPane_1.setRightComponent(splitPane);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		splitPane.setLeftComponent(scrollPane_1);
		
		textAreaOld = new JTextPane();
		textAreaOld.setEditable(false);
		scrollPane_1.setViewportView(textAreaOld);
		
		JLabel lblExistingFrameData = new JLabel("Existing Frame Data");
		lblExistingFrameData.setFont(new Font("Arial", Font.BOLD, 16));
		scrollPane_1.setColumnHeaderView(lblExistingFrameData);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		splitPane.setRightComponent(scrollPane_2);
		
		textAreaNew = new JTextPane();
		textAreaNew.setEditable(false);
		scrollPane_2.setViewportView(textAreaNew);
		
		JLabel lblFrameDataAfter = new JLabel("Frame Data after Update");
		lblFrameDataAfter.setFont(new Font("Arial", Font.BOLD, 16));
		scrollPane_2.setColumnHeaderView(lblFrameDataAfter);
		splitPane.setDividerLocation(250);
		
		previewPanel.setLayout(gl_previewPanel);
		
		textFilePath = new JTextField();
		textFilePath.setEditable(false);
		textFilePath.setColumns(10);
		
		DefaultComboBoxModel<String> modelFormat = new DefaultComboBoxModel<String>();
        modelFormat.addElement("Comma-separated values (CSV)");
        modelFormat.addElement("Tab-delimited file (tab)");
        cmbFormat = new JComboBox<String>(modelFormat);
		
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
		lblNewLabel_2.setIcon(new ImageIcon("C:\\Users\\Jesse\\workspace\\CycTools\\Images\\test2.png"));
		
		GroupLayout gl_optionsPanel = new GroupLayout(optionsPanel);
		gl_optionsPanel.setHorizontalGroup(
			gl_optionsPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_optionsPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_optionsPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_optionsPanel.createSequentialGroup()
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
									.addComponent(chckbxIgnoreDuplicate, GroupLayout.DEFAULT_SIZE, 425, Short.MAX_VALUE)
									.addComponent(btnOpen))
								.addGroup(gl_optionsPanel.createSequentialGroup()
									.addGroup(gl_optionsPanel.createParallelGroup(Alignment.TRAILING, false)
										.addComponent(textMultipleValueDelimiter, Alignment.LEADING, 0, 0, Short.MAX_VALUE)
										.addComponent(chckbxAppend, Alignment.LEADING))
									.addPreferredGap(ComponentPlacement.RELATED, 222, GroupLayout.PREFERRED_SIZE)))
							.addGap(158))
						.addGroup(Alignment.TRAILING, gl_optionsPanel.createSequentialGroup()
							.addComponent(lblNewLabel_2, GroupLayout.PREFERRED_SIZE, 743, GroupLayout.PREFERRED_SIZE)
							.addContainerGap())))
		);
		gl_optionsPanel.setVerticalGroup(
			gl_optionsPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_optionsPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblNewLabel_2, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
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
        
		DefaultComboBoxModel<String> modelAdaptor = new DefaultComboBoxModel<String>();
		modelAdaptor.addElement("Standard CSV: Column header determines slot label");
        modelAdaptor.addElement("Annotation Mod: FrameID, SlotValue, AnnotationValue.  Column header determines label");
        modelAdaptor.addElement("MaizeGDB Custom: frameID, goTerm, pubMedID, evCode, timeStampString (dd-mm-yyyy hh-mm-ss), curator");
        cmbAdaptor = new JComboBox<String>(modelAdaptor);
        
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
    
    private static List<String> textToLines(String text) {
		List<String> lines = new LinkedList<String>();
		String line = "";
		BufferedReader in = null;
		try {
		        in = new BufferedReader(new StringReader(text));
		        while ((line = in.readLine()) != null) {
		                lines.add(line);
		        }
		} catch (IOException e) {
		        e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return lines;
        
//        List<String> lines = new LinkedList<String>();
//        
//        if (text != null) {
//        	for (String line : text.split("\n")) lines.add(line + "\n");
//        }
//        return lines;
    }

	private void updateComparison() {
		String frameID = "";
		original = null;
		revised = null;
		currentDelta = 0;
		patch = null;
		actionNextDiff.setEnabled(false);
		try {
			frameID = listFrames.getSelectedValue().toString();
			if (frameID.equalsIgnoreCase("No Frames will modify KB")) {
				textAreaOld.setText("");
				textAreaNew.setText("");
				return; // This is the place holder for an empty list
			}
		} catch (NullPointerException e) {
			return;  // Ignore request to compare frame if no frame is selected (or list is empty)
		}
		
		Frame originalFrame = batchEdits.getFrameByID(frameID);
		String originalFrameString = controller.frameToString(originalFrame);//controller.frameToString(frameID);
		
		if (originalFrame == null || originalFrameString == null || originalFrameString.equalsIgnoreCase("")) {
			String message = "Frame " + frameID + " does not exist in database " + controller.getSelectedOrganism();
			textAreaOld.setText(message);
			originalFrame = new Frame(controller.getConnection(), frameID);
			originalFrameString = controller.frameToString(originalFrame);
		} else textAreaOld.setText(originalFrameString);
		original = textToLines(originalFrameString);
		
		// apply frame edits to local copy of frame
		Frame updatedFrame = batchEdits.updateLocalFrame(originalFrame);//controller.updateLocalFrame(frameID, frameEditArray);
		
		// return print of the updated frame
		String updatedFrameString = controller.frameToString(updatedFrame);
		textAreaNew.setText(updatedFrameString);
		revised  = textToLines(updatedFrameString);
		
		currentDelta = 0;
		patch = DiffUtils.diff(original, revised);
		actionNextDiff.setEnabled(true);
		
		highlightDiffs();
	}
	
	private void highlightDiffs() {
		// Highlight diffs
//		int currentPosition = 0;
//		for (String line : updatedFrameString.split("\n")) {
//			if (!originalFrameString.contains(line)) {
//				DefaultHighlighter.DefaultHighlightPainter highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
//				try {
//					textAreaNew.getHighlighter().addHighlight(currentPosition, currentPosition + line.length(), highlightPainter);
//				} catch (BadLocationException e) {
//					e.printStackTrace();
//				}
//			}
//			currentPosition += line.length()+1;
//		}
		textAreaOld.getHighlighter().removeAllHighlights();
		textAreaNew.getHighlighter().removeAllHighlights();
		
		for (Delta delta : patch.getDeltas()) {
			// Highlight the original text
			int originalPosition = 0;
			for (String line : original) {
				if (delta.getOriginal().getLines() != null && delta.getOriginal().getLines().size() > 0 && line.equals(delta.getOriginal().getLines().get(0))) {
					originalPosition += delta.getOriginal().getPosition();
					
					DefaultHighlighter.DefaultHighlightPainter highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
					try {
						int length = 0;
						for (Object s : delta.getOriginal().getLines()) length += s.toString().length();
						textAreaOld.getHighlighter().addHighlight(originalPosition, originalPosition + length, highlightPainter);
					} catch (BadLocationException ble) {
						ble.printStackTrace();
					}
					
				} else {
					originalPosition += line.length();
				}
			}
			
			// Highlight the revised text
			int revisedPosition = 0;
			for (String line : revised) {
				if (delta.getRevised().getLines() != null && delta.getRevised().getLines().size() > 0 && line.equals(delta.getRevised().getLines().get(0))) {
					revisedPosition += delta.getRevised().getPosition();
					
					DefaultHighlighter.DefaultHighlightPainter highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
					try {
						int length = 0;
						for (Object s : delta.getRevised().getLines()) length += s.toString().length();
						textAreaNew.getHighlighter().addHighlight(revisedPosition, revisedPosition + length+1, highlightPainter);
					} catch (BadLocationException ble) {
						ble.printStackTrace();
					}
					
				} else {
					revisedPosition += line.length();
				}
			}
		}
	}
	
	
	private void openPreviewPanel() throws PtoolsErrorException {
		controller.lockToolBarOrganismSelect();
		textAreaOld.setText("");
		textAreaNew.setText("");
		cardLayout.show(contentPane, "PreviewPanel");
		batchEdits = new BatchUpdate(selectedAdaptor.tableToFrameUpdates(tableSpreadSheet.getModel()));
		batchEdits.addPropertyChangeListener(controller);
		
		batchEdits.downloadFrames(controller.getConnection());
		allFramesWithImports = batchEdits.getFrameIDsModel();
		listFrames.setModel(allFramesWithImports);
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
			batchEdits.commitToKB(controller.getConnection());
			
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
			if (cmbFormat.getSelectedIndex() == 0) fileDelimiter = ",";
			else if (cmbFormat.getSelectedIndex() == 1) fileDelimiter = "\t";
			
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
			try {
				if (cmbAdaptor.getSelectedIndex() == 0) selectedAdaptor = new SimpleSlotValueImport();
				else if (cmbAdaptor.getSelectedIndex() == 1) selectedAdaptor = new SimpleAnnotationValueImport();
				else if (cmbAdaptor.getSelectedIndex() == 2) selectedAdaptor = new MaizeAdaptor();
				
				selectedAdaptor.setMultipleValueDelimiter(textMultipleValueDelimiter.getText());
				selectedAdaptor.setAppend(chckbxAppend.getModel().isSelected());
				selectedAdaptor.setIgnoreDuplicates(chckbxIgnoreDuplicate.getModel().isSelected());
			} catch (Exception exception) {
				CycToolsError.showError("Error selecting adaptor, un-implemented adaptor.", "Error");
				return;
			}
			
			try {
				openPreviewPanel();
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
			controller.unLockToolBarOrganismSelect();
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
			if (evt.getNewValue() == State.SHOW_IMPORT) {
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
	
	private class ActionNextDiff extends AbstractAction {
		public ActionNextDiff() {
			putValue(NAME, "Next Diff");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		public void actionPerformed(ActionEvent e) {
			try {
				highlightDiffs();
				Delta delta = patch.getDeltas().get(currentDelta);
				
				// Highlight the original text
				int originalPosition = 0;
//				textAreaOld.getHighlighter().removeAllHighlights();
				for (String line : original) {
					if (delta.getOriginal().getLines() != null && delta.getOriginal().getLines().size() > 0 && line.equals(delta.getOriginal().getLines().get(0))) {
						originalPosition += delta.getOriginal().getPosition();
						
						DefaultHighlighter.DefaultHighlightPainter highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.GREEN);
						try {
							int length = 0;
							for (Object s : delta.getOriginal().getLines()) length += s.toString().length();
							
							//Remove previous highlight
							for (Highlight h : textAreaOld.getHighlighter().getHighlights()) if (h.getStartOffset() == originalPosition) textAreaOld.getHighlighter().removeHighlight(h);
							
							textAreaOld.getHighlighter().addHighlight(originalPosition, originalPosition + length, highlightPainter);
							textAreaOld.scrollRectToVisible(textAreaOld.modelToView(originalPosition + length));
						} catch (BadLocationException ble) {
							ble.printStackTrace();
						}
						
					} else {
						originalPosition += line.length();
					}
				}
				
				// Highlight the revised text
				int revisedPosition = 0;
//				textAreaNew.getHighlighter().removeAllHighlights();
				for (String line : revised) {
					if (delta.getRevised().getLines() != null && delta.getRevised().getLines().size() > 0 && line.equals(delta.getRevised().getLines().get(0))) {
						revisedPosition += delta.getRevised().getPosition();
						
						DefaultHighlighter.DefaultHighlightPainter highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.GREEN);
						try {
							int length = 0;
							for (Object s : delta.getRevised().getLines()) length += s.toString().length();
							
							//Remove previous highlight
							for (Highlight h : textAreaNew.getHighlighter().getHighlights()) if (h.getStartOffset() == revisedPosition) textAreaNew.getHighlighter().removeHighlight(h);
							
							textAreaNew.getHighlighter().addHighlight(revisedPosition, revisedPosition + length+1, highlightPainter);
							textAreaNew.scrollRectToVisible(textAreaNew.modelToView(revisedPosition + length));
						} catch (BadLocationException ble) {
							ble.printStackTrace();
						}
						
					} else {
						revisedPosition += line.length();
					}
				}
				
				currentDelta++;
			} catch (Exception exception) {
				currentDelta = 0;
				textAreaOld.getHighlighter().removeAllHighlights();
				textAreaNew.getHighlighter().removeAllHighlights();
				CycToolsError.showWarning("Reached end of text, will continue searching from the beginning.", "End of file");
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
