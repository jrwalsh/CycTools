package edu.iastate.cyctools.tools.load.view;

import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.table.DefaultTableModel;
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
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Vector;

import edu.iastate.cyctools.DefaultController;
import edu.iastate.cyctools.DefaultStateModel.State;
import edu.iastate.cyctools.externalSourceCode.AbstractViewPanel;
import edu.iastate.cyctools.tools.load.fileAdaptors.FileAdaptor;
import edu.iastate.cyctools.tools.load.fileAdaptors.MaizeAdaptor;
import edu.iastate.cyctools.tools.load.fileAdaptors.SimpleInterpreter;
import edu.iastate.cyctools.tools.load.model.AbstractFrameEdit;
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

@SuppressWarnings("serial")
public class LoadPanel extends AbstractViewPanel {
	// Selectable Options
	private FileAdaptor selectedAdaptor;
	private String fileType;
	private String multipleValueDelimiter;
	private boolean append;
	private boolean ignoreDuplicates;
	
	DefaultController controller;
	private JTable tableSpreadSheet;
	private CardLayout cardLayout;
	private JPanel contentPane;
	private JList listFrames;
	private JTextArea textAreaOld;
	private JTextPane textAreaNew;
	private JTextArea textFrameEdits;
	private JTextArea textSuccess;
	private JTextArea textFail;
	private JTextArea textConverted;
	private JTextArea textLog;
	private BatchEditModel batchEdits;
	private JCheckBox chckbxAppend;
	private JCheckBox chckbxIgnoreDuplicate;
	
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
	private JTextField textField;
	private final Action actionBack2 = new ActionBack2();
	
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
        SpreadsheetScrollPane.setViewportView(tableSpreadSheet);
        
        setLayout(new CardLayout(0, 0));
        cardLayout = (CardLayout)(this.getLayout());
		add(optionsPanel, "OptionsPanel");
		add(filePanel, "FilePanel");
		add(previewPanel, "PreviewPanel");
		
		JButton btnBack2 = new JButton("Back");
		btnBack2.setAction(actionBack2);
		
		JButton btnUpload = new JButton("Upload");
		btnUpload.setAction(actionUpload);
		
		JPanel panel = new JPanel();
		
		JSplitPane splitPane = new JSplitPane();
		
		JLabel lblPreviewChanges = new JLabel("<html><h3>Preview Changes</h3></html>");
		GroupLayout gl_previewPanel = new GroupLayout(previewPanel);
		gl_previewPanel.setHorizontalGroup(
			gl_previewPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_previewPanel.createSequentialGroup()
					.addGroup(gl_previewPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_previewPanel.createSequentialGroup()
							.addContainerGap()
							.addGroup(gl_previewPanel.createParallelGroup(Alignment.LEADING)
								.addGroup(Alignment.TRAILING, gl_previewPanel.createSequentialGroup()
									.addComponent(btnBack2)
									.addPreferredGap(ComponentPlacement.RELATED, 660, Short.MAX_VALUE)
									.addComponent(btnUpload))
								.addGroup(gl_previewPanel.createSequentialGroup()
									.addComponent(panel, GroupLayout.PREFERRED_SIZE, 216, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(splitPane, GroupLayout.DEFAULT_SIZE, 558, Short.MAX_VALUE))))
						.addGroup(gl_previewPanel.createSequentialGroup()
							.addGap(303)
							.addComponent(lblPreviewChanges, GroupLayout.PREFERRED_SIZE, 129, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		gl_previewPanel.setVerticalGroup(
			gl_previewPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_previewPanel.createSequentialGroup()
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(lblPreviewChanges)
					.addGap(26)
					.addGroup(gl_previewPanel.createParallelGroup(Alignment.TRAILING, false)
						.addComponent(splitPane)
						.addComponent(panel, GroupLayout.DEFAULT_SIZE, 287, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_previewPanel.createParallelGroup(Alignment.TRAILING)
						.addComponent(btnUpload)
						.addComponent(btnBack2))
					.addContainerGap())
		);
		listFrames = new JList(new DefaultListModel());
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
		scrollPane.setColumnHeaderView(lblFramesToBe);
		panel.setLayout(gl_panel);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		splitPane.setLeftComponent(scrollPane_1);
		
		textAreaOld = new JTextArea();
		textAreaOld.setEditable(false);
		scrollPane_1.setViewportView(textAreaOld);
		
		JLabel lblExistingFrameData = new JLabel("Existing Frame Data");
		scrollPane_1.setColumnHeaderView(lblExistingFrameData);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		splitPane.setRightComponent(scrollPane_2);
		
		textAreaNew = new JTextPane();
		textAreaNew.setEditable(false);
		scrollPane_2.setViewportView(textAreaNew);
		
		JLabel lblFrameDataAfter = new JLabel("Frame Data after Update");
		scrollPane_2.setColumnHeaderView(lblFrameDataAfter);
		splitPane.setDividerLocation(250);
		
		previewPanel.setLayout(gl_previewPanel);
		add(reviewPanel, "ReviewPanel");
		
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
		
		textField = new JTextField();
		textField.setText("$");
		textField.setColumns(10);
		
		JLabel lblNewLabel_4 = new JLabel("Append values or overwrite existing");
		
		JLabel lblNewLabel_5 = new JLabel("Check if this value exists before importing");
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
						.addGroup(gl_optionsPanel.createSequentialGroup()
							.addGroup(gl_optionsPanel.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_optionsPanel.createSequentialGroup()
									.addComponent(btnBrowse)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(textFilePath, GroupLayout.PREFERRED_SIZE, 341, GroupLayout.PREFERRED_SIZE))
								.addComponent(cmbFormat, GroupLayout.PREFERRED_SIZE, 259, GroupLayout.PREFERRED_SIZE)
								.addComponent(chckbxIgnoreDuplicate, GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE)
								.addComponent(btnOpen))
							.addContainerGap(169, Short.MAX_VALUE))
						.addGroup(gl_optionsPanel.createSequentialGroup()
							.addGroup(gl_optionsPanel.createParallelGroup(Alignment.TRAILING, false)
								.addComponent(textField, Alignment.LEADING, 0, 0, Short.MAX_VALUE)
								.addComponent(chckbxAppend, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
							.addContainerGap())))
		);
		gl_optionsPanel.setVerticalGroup(
			gl_optionsPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_optionsPanel.createSequentialGroup()
					.addContainerGap()
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
						.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
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
					.addContainerGap(193, Short.MAX_VALUE))
		);
		optionsPanel.setLayout(gl_optionsPanel);
        
		Vector<AdaptorKeyValue> modelAdaptor = new Vector<AdaptorKeyValue>();
		modelAdaptor.addElement( new AdaptorKeyValue(new SimpleInterpreter(), "Standard CSV: Column header determines slot label"));
		modelAdaptor.addElement( new AdaptorKeyValue(null, "Annotation Mod: FrameID, SlotValue, AnnotationValue.  Column header determines label")); //TODO implement the annotation adaptor
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
        
        JScrollPane scrollPane_3 = new JScrollPane();
        
        JPanel panel_1 = new JPanel();
        
        JLabel lblSummaryResults = new JLabel("Summary Results");
        
        GroupLayout gl_reviewPanel = new GroupLayout(reviewPanel);
        gl_reviewPanel.setHorizontalGroup(
        	gl_reviewPanel.createParallelGroup(Alignment.LEADING)
        		.addGroup(gl_reviewPanel.createSequentialGroup()
        			.addGroup(gl_reviewPanel.createParallelGroup(Alignment.LEADING)
        				.addGroup(gl_reviewPanel.createSequentialGroup()
        					.addContainerGap()
        					.addComponent(btnRevert)
        					.addPreferredGap(ComponentPlacement.RELATED)
        					.addComponent(btnSave))
        				.addGroup(gl_reviewPanel.createSequentialGroup()
        					.addGap(12)
        					.addComponent(scrollPane_3, GroupLayout.DEFAULT_SIZE, 778, Short.MAX_VALUE))
        				.addGroup(gl_reviewPanel.createSequentialGroup()
        					.addContainerGap()
        					.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 399, GroupLayout.PREFERRED_SIZE))
        				.addGroup(gl_reviewPanel.createSequentialGroup()
        					.addContainerGap()
        					.addComponent(lblSummaryResults)))
        			.addContainerGap())
        );
        gl_reviewPanel.setVerticalGroup(
        	gl_reviewPanel.createParallelGroup(Alignment.TRAILING)
        		.addGroup(gl_reviewPanel.createSequentialGroup()
        			.addGap(14)
        			.addComponent(lblSummaryResults)
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addComponent(panel_1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addComponent(scrollPane_3, GroupLayout.PREFERRED_SIZE, 178, GroupLayout.PREFERRED_SIZE)
        			.addGap(18)
        			.addGroup(gl_reviewPanel.createParallelGroup(Alignment.BASELINE)
        				.addComponent(btnRevert)
        				.addComponent(btnSave))
        			.addContainerGap())
        );
        
        textConverted = new JTextArea();
        textConverted.setEditable(false);
        
        JLabel lblTotalFrameEdits = new JLabel("Total Frame Edits Processed");
        
        JLabel lblSuccessfulUpdates = new JLabel("Successful Updates");
        
        JLabel lblFailedUpdates = new JLabel("Failed Updates");
        
        textFrameEdits = new JTextArea();
        textFrameEdits.setEditable(false);
        textSuccess = new JTextArea();
        textSuccess.setEditable(false);
        textFail = new JTextArea();
        textFail.setEditable(false);
        GroupLayout gl_panel_1 = new GroupLayout(panel_1);
        gl_panel_1.setHorizontalGroup(
        	gl_panel_1.createParallelGroup(Alignment.LEADING)
        		.addGroup(gl_panel_1.createSequentialGroup()
        			.addContainerGap()
        			.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
        				.addComponent(textConverted, GroupLayout.DEFAULT_SIZE, 379, Short.MAX_VALUE)
        				.addGroup(gl_panel_1.createSequentialGroup()
        					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
        						.addComponent(lblTotalFrameEdits)
        						.addComponent(lblSuccessfulUpdates)
        						.addComponent(lblFailedUpdates))
        					.addGap(18)
        					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING, false)
        						.addComponent(textFail, GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
        						.addComponent(textSuccess, GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
        						.addComponent(textFrameEdits))
        					.addPreferredGap(ComponentPlacement.RELATED, 98, Short.MAX_VALUE)))
        			.addContainerGap())
        );
        gl_panel_1.setVerticalGroup(
        	gl_panel_1.createParallelGroup(Alignment.LEADING)
        		.addGroup(gl_panel_1.createSequentialGroup()
        			.addContainerGap()
        			.addComponent(textConverted, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
        				.addComponent(lblTotalFrameEdits)
        				.addComponent(textFrameEdits, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
        				.addComponent(lblSuccessfulUpdates)
        				.addComponent(textSuccess, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
        				.addComponent(lblFailedUpdates)
        				.addComponent(textFail, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        			.addContainerGap(25, Short.MAX_VALUE))
        );
        panel_1.setLayout(gl_panel_1);
        
        textLog = new JTextArea();
        scrollPane_3.setViewportView(textLog);
        reviewPanel.setLayout(gl_reviewPanel);
	}
    

	private void updateComparison() {
		String frameID = listFrames.getSelectedValue().toString();
		String originalFrameString = controller.frameToString(frameID);
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
		
		DefaultListModel listModel = (DefaultListModel) listFrames.getModel();
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
			final JFileChooser fileChooser = new JFileChooser();
			if (fileChooser.showOpenDialog((Component) e.getSource()) == JFileChooser.APPROVE_OPTION) {
				textFilePath.setText(fileChooser.getSelectedFile().getAbsolutePath());
				controller.changeDocumentFile(fileChooser.getSelectedFile());
			}
		}
	}
	
	private class ActionUpload extends AbstractAction {
		public ActionUpload() {
			putValue(NAME, "Upload");
			putValue(SHORT_DESCRIPTION, "Upload data table to database");
		}
		public void actionPerformed(ActionEvent e) {
			cardLayout.show(contentPane, "ReviewPanel");
			textConverted.setText("Converted " + batchEdits.getLines() + " lines of updates into " + batchEdits.getFrameEdits().size() + " individual updates.");
			batchEdits.commitAll(controller.getConnection());
			textLog.setText(batchEdits.getLog());
		}
	}
	
	private class ActionSave extends AbstractAction {
		public ActionSave() {
			putValue(NAME, "Save");
			putValue(SHORT_DESCRIPTION, "Save changes to database");
		}
		public void actionPerformed(ActionEvent e) {
			controller.saveDataBase();
			cardLayout.show(contentPane, "OptionsPanel");
		}
	}
	
	private class ActionRevert extends AbstractAction {
		public ActionRevert() {
			putValue(NAME, "Revert");
			putValue(SHORT_DESCRIPTION, "Revert changes to database");
		}
		public void actionPerformed(ActionEvent e) {
			controller.revertDataBase();
			cardLayout.show(contentPane, "OptionsPanel");
		}
	}
	
	private class ActionOpen extends AbstractAction {
		public ActionOpen() {
			putValue(NAME, "Open");
			putValue(SHORT_DESCRIPTION, "Open file");
		}
		public void actionPerformed(ActionEvent e) {
			if (controller.getDocumentModel().getFile() == null) {
				JOptionPane.showMessageDialog(DefaultController.mainJFrame, "Please select an input file", "File error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			fileType = ((KeyValue)cmbFormat.getSelectedItem()).getValue();
			multipleValueDelimiter = textField.getText();
			append = chckbxAppend.getModel().isSelected();
			ignoreDuplicates = chckbxIgnoreDuplicate.getModel().isSelected();
			
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
	
	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(DefaultController.DOCUMENT_TABLEMODEL_PROPERTY) && evt.getNewValue() != null) {
			DefaultTableModel dtm = (DefaultTableModel)evt.getNewValue();
			tableSpreadSheet.setModel(dtm);
			revalidate();
			repaint();
		}
		
		if (evt.getPropertyName().equals(DefaultController.REPORT_PROPERTY_FRAME_EDITS_PROCESSED) && evt.getNewValue() != null) {
			String value = (String)evt.getNewValue().toString();
			textFrameEdits.setText("" + value);
			revalidate();
			repaint();
		}
		
		if (evt.getPropertyName().equals(DefaultController.REPORT_PROPERTY_FRAME_EDITS_SUCCESS) && evt.getNewValue() != null) {
			String value = (String)evt.getNewValue().toString();
			textSuccess.setText("" + value);
			revalidate();
			repaint();
		}
		
		if (evt.getPropertyName().equals(DefaultController.REPORT_PROPERTY_FRAME_EDITS_FAIL) && evt.getNewValue() != null) {
			String value = (String)evt.getNewValue().toString();
			textFail.setText("" + value);
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
}
