package edu.iastate.cyctools.tools.load.view;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter.Highlight;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
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
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import edu.iastate.cyctools.CycToolsError;
import edu.iastate.cyctools.DefaultController;
import edu.iastate.cyctools.InternalStateModel.State;
import edu.iastate.cyctools.externalSourceCode.AbstractViewPanel;
import edu.iastate.cyctools.externalSourceCode.KeyValueComboboxModel;
import edu.iastate.cyctools.tools.load.fileAdaptors.FileAdaptor;
import edu.iastate.cyctools.tools.load.fileAdaptors.MaizeAdaptor;
import edu.iastate.cyctools.tools.load.fileAdaptors.SimpleAnnotationValueImport;
import edu.iastate.cyctools.tools.load.fileAdaptors.SimpleSlotValueImport;
import edu.iastate.cyctools.tools.load.model.BatchUpdate.Event;
import edu.iastate.cyctools.tools.load.model.BatchUpdate.Status;
import edu.iastate.cyctools.tools.load.model.DocumentModel;
import edu.iastate.cyctools.tools.load.model.BatchUpdate;
import edu.iastate.javacyco.Frame;
import edu.iastate.javacyco.JavacycConnection;
import edu.iastate.javacyco.PtoolsErrorException;
import java.awt.CardLayout;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.JLabel;
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
import net.miginfocom.swing.MigLayout;
import javax.swing.JRadioButton;
import java.awt.event.ActionListener;

@SuppressWarnings({"serial", "rawtypes", "unchecked"})
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
	private JRadioButton rdbtnProtein;
	private String importType;
	private ButtonGroup groupImportType;
	private HashMap<String, ArrayList<Frame>> searchResults;
	private JLabel labelSearchResults;
	private JTextArea textSearchExactMatches;
	private JTextArea textSearchGoodMatches;
	private JTextArea textSearchMultipleMatches;
	private JTextArea textSearchNoMatches;
	private JTabbedPane tabbedSearchResults;
	private JComboBox cmbImportType = new JComboBox();
	
	private final Action actionBrowse = new ActionBrowse();
	private final Action actionUpload = new ActionUpload();
	private final Action actionSave = new ActionSave();
	private final Action actionRevert = new ActionRevert();
	private final Action actionPreview = new ActionPreview();
	private final Action actionSaveLog = new ActionSaveLog();
	private final Action actionNextDiff = new ActionNextDiff();
	private JLabel noticeLabel;
	
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
    	
		JPanel optionsPanel = initOptionsPanel();
    	JPanel filePanel = initFilePanel();
    	JPanel searchPanel = initSearchPanel();
    	JPanel previewPanel = initPreviewPanel();
    	JPanel reviewPanel = initReviewPanel();
    	JPanel finalPanel = initFinalPanel();
        
        setLayout(new CardLayout(0, 0));
        cardLayout = (CardLayout)(this.getLayout());
        
		add(optionsPanel, "OptionsPanel");
		add(filePanel, "FilePanel");
		add(searchPanel, "SearchPanel");
		add(previewPanel, "PreviewPanel");
		add(reviewPanel, "ReviewPanel");
		add(finalPanel, "FinalPanel");
	}

    private JPanel initOptionsPanel() {
    	JPanel optionsPanel = new JPanel();
    	
    	DefaultComboBoxModel<String> modelFormat = new DefaultComboBoxModel<String>();
        modelFormat.addElement("Comma-separated values (CSV)");
        modelFormat.addElement("Tab-delimited file (tab)");
        
        DefaultComboBoxModel<String> modelImportType = new DefaultComboBoxModel<String>();
        modelImportType.addElement("");
        modelImportType.addElement("");
        
        KeyValueComboboxModel modelTypes = new KeyValueComboboxModel();
    	modelTypes.put("|Proteins|", "Proteins");
    	modelTypes.put("|RNAs|", "RNAs");
    	modelTypes.put("|Enzymatic-Reactions|", "Enzymatic-Reactions");
    	modelTypes.put("|Gene-Ontology-Terms|", "Gene-Ontology-Terms");
    	modelTypes.put("|Compounds|", "Compounds");
    	modelTypes.put("|Transcription-Units|", "Transcription-Units");
    	modelTypes.put("|Reactions|", "Reactions");
    	modelTypes.put("|Organisms|", "Organisms");
    	modelTypes.put("|Pathways|", "Pathways");
    	modelTypes.put("|Extragenic-Sites|", "Extragenic-Sites");
    	modelTypes.put("|All-Genes|", "All-Genes");
    	modelTypes.put("|Growth-Media|", "Growth-Media");
    	modelTypes.put("|GO-Term Annotations (proteins)|", "GO-Term Annotations (proteins)");
    	
    	optionsPanel.setLayout(new MigLayout("", "[][108px][grow][][]", "[23px][][20px][20px][23px][23px][23px][14px][23px][][][][grow][]"));
    	
    	JLabel imageLabel = new JLabel("");
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        imageLabel.setOpaque(true);
        imageLabel.setIcon(new ImageIcon(LoadPanel.class.getResource("/resources/step1.png")));
        optionsPanel.add(imageLabel, "cell 1 0 4 1,alignx center,aligny center");
		
        JLabel importTypeLabel = new JLabel("Select Import Type");
		optionsPanel.add(importTypeLabel, "flowx,cell 1 2,alignx trailing,aligny center");
		
		cmbImportType = new JComboBox();
		cmbImportType.setModel(modelTypes);
		cmbImportType.setRenderer(new DefaultListCellRenderer() {
	        @Override
	        public Component getListCellRendererComponent(final JList list, Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
	        	if (value != null) value = value.toString().substring(value.toString().indexOf("=")+1, value.toString().length());
	            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
	        }
	    });
		cmbImportType.setSelectedIndex(0);
		optionsPanel.add(cmbImportType, "cell 2 2,alignx left,aligny center");
		
		JLabel inputFileLabel = new JLabel("Select Input File");
		optionsPanel.add(inputFileLabel, "flowx,cell 1 3,alignx right,aligny center");
		
		JButton btnBrowse = new JButton("Browse");
		btnBrowse.setAction(actionBrowse);
		optionsPanel.add(btnBrowse, "flowx,cell 2 3,alignx left,aligny center");
		
		JLabel fileSelectLabel = new JLabel("Select file format");
		optionsPanel.add(fileSelectLabel, "cell 1 4,alignx right,aligny center");
		cmbFormat = new JComboBox<String>(modelFormat);
		optionsPanel.add(cmbFormat, "cell 2 4,alignx left,aligny center");
		
		JLabel delimiterLabel = new JLabel("Multiple value delimiter");
		optionsPanel.add(delimiterLabel, "cell 1 5,alignx right,aligny center");
		textMultipleValueDelimiter = new JTextField();
		textMultipleValueDelimiter.setText("$");
		textMultipleValueDelimiter.setColumns(10);
		optionsPanel.add(textMultipleValueDelimiter, "cell 2 5,alignx left,aligny center");
		
		JLabel appendLabel = new JLabel("Append values or overwrite existing");
		optionsPanel.add(appendLabel, "cell 1 6,alignx right,aligny center");
		
		chckbxAppend = new JCheckBox("Append new data to existing values?");
		chckbxAppend.setSelected(true);
		optionsPanel.add(chckbxAppend, "cell 2 6,alignx left,aligny center");
		
		JLabel duplicateLabel = new JLabel("Check if this value exists before importing");
		optionsPanel.add(duplicateLabel, "cell 1 7,alignx right,aligny center");
		
		chckbxIgnoreDuplicate = new JCheckBox("Ignore Duplicates?");
		chckbxIgnoreDuplicate.setSelected(true);
		optionsPanel.add(chckbxIgnoreDuplicate, "cell 2 7,growx,aligny center");
		
		noticeLabel = new JLabel();
		noticeLabel.setText("Always backup your database file before modifying it!");
		optionsPanel.add(noticeLabel, "cell 1 10 4 1,alignx left,aligny top");
		
		JButton btnNext = new JButton("Open");
		btnNext.addActionListener(new ActionListener() {
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
		});
		optionsPanel.add(btnNext, "cell 4 13,alignx center,aligny center");
		
		textFilePath = new JTextField();
		textFilePath.setEditable(false);
		textFilePath.setColumns(10);
		optionsPanel.add(textFilePath, "cell 2 3 2 1,growx,alignx right,aligny center");
		
    	return optionsPanel;
    }
    
    private JPanel initFilePanel() {
    	JPanel filePanel = new JPanel();
    	
        filePanel.setLayout(new MigLayout("", "[grow]", "[][grow][]"));
        
        JLabel imageLabel = new JLabel("");
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        imageLabel.setIcon(new ImageIcon(LoadPanel.class.getResource("/resources/step2.png")));
        filePanel.add(imageLabel, "cell 0 0,alignx center,aligny center");
        
        JScrollPane SpreadsheetScrollPane = new JScrollPane();
        tableSpreadSheet = new JTable();
        tableSpreadSheet.setFillsViewportHeight(true);
        JTableHeader th = tableSpreadSheet.getTableHeader();  
        th.setFont(new Font("Serif", Font.BOLD, 15)); 
        SpreadsheetScrollPane.setViewportView(tableSpreadSheet);
        filePanel.add(SpreadsheetScrollPane, "cell 0 1 2097051 1,growx,alignx left,aligny top");
        
        DefaultComboBoxModel<String> modelAdaptor = new DefaultComboBoxModel<String>();
		modelAdaptor.addElement("Standard CSV: Column header determines slot label");
        modelAdaptor.addElement("Annotation Mod: FrameID, SlotValue, AnnotationValue.  Column header determines label");
        modelAdaptor.addElement("MaizeGDB Custom: frameID, goTerm, pubMedID, evCode, timeStampString (dd-mm-yyyy hh-mm-ss), curator");
        cmbAdaptor = new JComboBox<String>(modelAdaptor);
        filePanel.add(cmbAdaptor, "flowx,cell 0 2,alignx left,aligny center");
        
        JButton btnPreview = new JButton("Preview");
        btnPreview.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
				searchFrameIDs();
//				clearSearchPanel();
        		cardLayout.show(contentPane, "SearchPanel");
        	}
        });
        
        JButton btnBack = new JButton("Back");
        btnBack.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		cardLayout.show(contentPane, "OptionsPanel");
        	}
        });
        filePanel.add(btnBack, "flowx,cell 0 2,alignx right,aligny center");
        btnPreview.setAction(actionPreview);
        filePanel.add(btnPreview, "cell 0 2,alignx right,aligny top");
//        
//        GroupLayout gl_filePanel = new GroupLayout(filePanel);
//        gl_filePanel.setHorizontalGroup(
//        	gl_filePanel.createParallelGroup(Alignment.LEADING)
//        		.addGroup(Alignment.TRAILING, gl_filePanel.createSequentialGroup()
//        			.addContainerGap()
//        			.addGroup(gl_filePanel.createParallelGroup(Alignment.TRAILING)
//        				.addComponent(SpreadsheetScrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 1184, Short.MAX_VALUE)
//        				.addComponent(imageLabel, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 777, Short.MAX_VALUE)
//        				.addGroup(Alignment.LEADING, gl_filePanel.createSequentialGroup()
//        					.addComponent(btnBack)
//        					.addPreferredGap(ComponentPlacement.RELATED, 665, Short.MAX_VALUE)
//        					.addComponent(cmbAdaptor, GroupLayout.PREFERRED_SIZE, 375, GroupLayout.PREFERRED_SIZE)
//        					.addGap(18)
//        					.addComponent(btnPreview)))
//        			.addGap(13))
//        );
//        gl_filePanel.setVerticalGroup(
//        	gl_filePanel.createParallelGroup(Alignment.TRAILING)
//        		.addGroup(gl_filePanel.createSequentialGroup()
//        			.addContainerGap()
//        			.addComponent(imageLabel, GroupLayout.PREFERRED_SIZE, 54, GroupLayout.PREFERRED_SIZE)
//        			.addPreferredGap(ComponentPlacement.RELATED)
//        			.addComponent(SpreadsheetScrollPane, GroupLayout.DEFAULT_SIZE, 284, Short.MAX_VALUE)
//        			.addPreferredGap(ComponentPlacement.UNRELATED)
//        			.addGroup(gl_filePanel.createParallelGroup(Alignment.BASELINE)
//        				.addComponent(btnPreview)
//        				.addComponent(btnBack)
//        				.addComponent(cmbAdaptor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
//        			.addContainerGap())
//        );
//        filePanel.setLayout(gl_filePanel);
        
    	return filePanel;
    }

    private JPanel initSearchPanel() {
    	JPanel searchPanel = new JPanel();
    	
    	searchPanel.setLayout(new MigLayout("", "[grow]", "[][][grow][]"));
    	
    	JLabel imageLabel = new JLabel("");
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        imageLabel.setIcon(new ImageIcon(LoadPanel.class.getResource("/resources/step2.png")));
        searchPanel.add(imageLabel, "cell 0 0,alignx center,aligny center");
		
		labelSearchResults = new JLabel("Waiting for search results....");
		searchPanel.add(labelSearchResults, "cell 0 1,alignx left,aligny center");
		
		tabbedSearchResults = new JTabbedPane(JTabbedPane.TOP);
		searchPanel.add(tabbedSearchResults, "cell 0 2,grow");
		
		textSearchExactMatches = new JTextArea();
		JScrollPane scroll1 = new JScrollPane();
		scroll1.setViewportView(textSearchExactMatches);
		tabbedSearchResults.addTab("Exact Matches", null, scroll1, null);
		
		textSearchGoodMatches = new JTextArea();
		JScrollPane scroll2 = new JScrollPane();
		scroll2.setViewportView(textSearchGoodMatches);
		tabbedSearchResults.addTab("Good Matches", null, scroll2, null);
		
		textSearchMultipleMatches = new JTextArea();
		JScrollPane scroll3 = new JScrollPane();
		scroll3.setViewportView(textSearchMultipleMatches);
		tabbedSearchResults.addTab("Multiple Matches", null, scroll3, null);
		
		textSearchNoMatches = new JTextArea();
		JScrollPane scroll4 = new JScrollPane();
		scroll4.setViewportView(textSearchNoMatches);
		tabbedSearchResults.addTab("No Matches", null, scroll4, null);
		
		JButton btnAccept = new JButton("Accept");
		btnAccept.addActionListener(new ActionListener() {
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
		});
		
		JButton btnBack_1 = new JButton("Back");
		btnBack_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.unLockToolBarOrganismSelect();
				cardLayout.show(contentPane, "FilePanel");
			}
		});
		searchPanel.add(btnBack_1, "flowx,cell 0 3,alignx right,aligny center");
		searchPanel.add(btnAccept, "cell 0 3,alignx right,aligny center");
		
        return searchPanel;
    }
    
    private JPanel initPreviewPanel() {
    	JPanel previewPanel = new JPanel();
    	
    	previewPanel.setLayout(new MigLayout("", "[20%,grow][40%,grow][40%,grow]", "[][grow][]"));
    	
    	listFrames = new JList<String>(new DefaultListModel<String>());
		listFrames.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				updateComparison();
			}
		});
		listFrames.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		
    	JLabel imageLabel = new JLabel("");
		imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		imageLabel.setIcon(new ImageIcon(LoadPanel.class.getResource("/resources/step3.png")));
		previewPanel.add(imageLabel, "cell 0 0 3 1,alignx center,aligny center");
		
		JScrollPane scrollPanelList = new JScrollPane();
		scrollPanelList.setViewportView(listFrames);
		JLabel lblList = new JLabel("Frames to be Updated");
		lblList.setFont(new Font("Arial", Font.BOLD, 16));
		lblList.setAlignmentX(Component.CENTER_ALIGNMENT);
		scrollPanelList.setColumnHeaderView(lblList);
		previewPanel.add(scrollPanelList, "cell 0 1,aligny top,grow");
		
		JScrollPane scrollPaneOld = new JScrollPane();
		textAreaOld = new JTextPane();
		textAreaOld.setEditable(false);
		scrollPaneOld.setViewportView(textAreaOld);
		JLabel lblOld = new JLabel("Existing Frame Data");
		lblOld.setFont(new Font("Arial", Font.BOLD, 16));
		scrollPaneOld.setColumnHeaderView(lblOld);
		previewPanel.add(scrollPaneOld, "cell 1 1,aligny top,grow");
		
		JScrollPane scrollPaneNew = new JScrollPane();
		textAreaNew = new JTextPane();
		textAreaNew.setEditable(false);
		scrollPaneNew.setViewportView(textAreaNew);
		JLabel lblNew = new JLabel("Frame Data after Update");
		lblNew.setFont(new Font("Arial", Font.BOLD, 16));
		scrollPaneNew.setColumnHeaderView(lblNew);
		previewPanel.add(scrollPaneNew, "cell 2 1,aligny top,grow");
		
		JButton btnNextDiff = new JButton("New button");
		btnNextDiff.setAction(actionNextDiff);
		previewPanel.add(btnNextDiff, "flowx,cell 0 2,alignx left,aligny center");
		
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
		previewPanel.add(chckbxFilter, "cell 0 2,alignx left,aligny center");
		
		JButton btnBack2 = new JButton("Back");
		btnBack2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cardLayout.show(contentPane, "SearchPanel");
			}
		});
		previewPanel.add(btnBack2, "flowx,cell 2 2,alignx right,aligny center");
		
		JButton btnUpload = new JButton("Upload");
		btnUpload.setAction(actionUpload);
		previewPanel.add(btnUpload, "cell 2 2,alignx right,aligny center");
		
    	return previewPanel;
    }
    
    private JPanel initReviewPanel() {
    	JPanel reviewPanel = new JPanel();
    	
    	reviewPanel.setLayout(new MigLayout("", "[109px][509px][155px]", "[49px][251px][57px]"));
    	
    	JLabel imageLabel = new JLabel("");
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        imageLabel.setIcon(new ImageIcon(LoadPanel.class.getResource("/resources/step4.png")));
        reviewPanel.add(imageLabel, "cell 0 0 3 1,alignx center,aligny center");
        
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        JScrollPane scrollPaneAll = new JScrollPane();
        tabbedPane.addTab("All Events", null, scrollPaneAll, null);
        textLog = new JTextArea();
        scrollPaneAll.setViewportView(textLog);
        JScrollPane scrollPaneSuccess = new JScrollPane();
        tabbedPane.addTab("Successful Imports", null, scrollPaneSuccess, null);
        textArea = new JTextArea();
        scrollPaneSuccess.setViewportView(textArea);
        JScrollPane scrollPaneFailed = new JScrollPane();
        tabbedPane.addTab("Failed Imports", null, scrollPaneFailed, null);
        textArea_1 = new JTextArea();
        scrollPaneFailed.setViewportView(textArea_1);
        reviewPanel.add(tabbedPane, "cell 0 1 3 1,grow");
        
        JButton btnRevert = new JButton("Revert");
        btnRevert.setAction(actionRevert);
        reviewPanel.add(btnRevert, "cell 2 2,alignx right,aligny center");
        
        JButton btnSave = new JButton("Save");
        btnSave.setAction(actionSave);
        reviewPanel.add(btnSave, "cell 0 2,alignx left,aligny center");
        
        return reviewPanel;
    }
    
    private JPanel initFinalPanel() {
    	JPanel finalPanel = new JPanel();
    	
    	finalPanel.setLayout(new MigLayout("", "[780px]", "[49px][23px]"));
    	
    	JButton button = new JButton("Save Log");
		button.setAction(actionSaveLog);
		
		JLabel imageLabel = new JLabel("");
		imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		imageLabel.setIcon(new ImageIcon(LoadPanel.class.getResource("/resources/step5.png")));
		finalPanel.add(button, "cell 0 1,alignx right,aligny center");
		finalPanel.add(imageLabel, "cell 0 0,alignx center,aligny center");
		
		return finalPanel;
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
		if (controller.isKBModified(controller.getSelectedOrganism())) {
			int n = JOptionPane.showConfirmDialog(
					DefaultController.mainJFrame,
			    "The selected database is already in a modified state. \n" +
				"Recommend saving or undoing changes to this database \n" +
			    "before making additional changes. \n\n" +
				"Do you wish to continue anyway?",
			    "Database in Modified State",
			    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if (n == 1) {
				return;
			}
		}
//		controller.lockToolBarOrganismSelect();
		textAreaOld.setText("");
		textAreaNew.setText("");
		cardLayout.show(contentPane, "PreviewPanel");
		
		// Convert user provided IDs to frame IDs, remove imports that can't be matched to an existing frame
		DefaultTableModel model = (DefaultTableModel) tableSpreadSheet.getModel();
		int nRow = model.getRowCount(), nCol = model.getColumnCount();
		for (int i = nRow-1; i >= 0; i--) {
			String givenID = (String) model.getValueAt(i, 0); // User provided ID must always be in first column
			ArrayList<Frame> matches = searchResults.get(givenID);
			if (matches.size() == 1) {
				model.setValueAt(matches.get(0).getLocalID(), i, 0);
			} else {
				model.removeRow(i);//if there is not a good matching FrameID, we skip this line as a condition of the import.  Can't import data if we don't know where to put it.
			}
		}
	    
	    batchEdits = new BatchUpdate(selectedAdaptor.tableToFrameUpdates(model), controller.getConnection());
//	    batchEdits = new BatchUpdate(selectedAdaptor.tableToFrameUpdates(tableSpreadSheet.getModel()), controller.getConnection());
		batchEdits.addPropertyChangeListener(controller);
		
		batchEdits.downloadFrames(controller.getConnection());
		allFramesWithImports = batchEdits.getFrameIDsModel();
		listFrames.setModel(allFramesWithImports);
	}
	
	private void searchFrameIDs() { //TODO convert to worker string task
		JavacycConnection conn = controller.getConnection();
		
		controller.lockToolBarOrganismSelect();
		TableModel tb = tableSpreadSheet.getModel();
		
		// For each user provided ID, do a search of that ID in the current DB.  Store results.
		searchResults = new HashMap<String, ArrayList<Frame>>();
		for (int rowIndex = 0; rowIndex < tb.getRowCount(); rowIndex++) {
			String userProvidedID = (String) tb.getValueAt(rowIndex, 0);
			try {
				if (!searchResults.containsKey(userProvidedID)) {
					searchResults.put(userProvidedID, conn.search(userProvidedID, ((Entry<String, String>)cmbImportType.getSelectedItem()).getKey()));
				}
			} catch (PtoolsErrorException e) {
				e.printStackTrace();
				searchResults.put(userProvidedID, null);
			}
		}
		
		// Process search results
		ArrayList<String> exactMatches = new ArrayList<String>();
		ArrayList<String> goodMatches = new ArrayList<String>();
		ArrayList<String> ambiguousMatches = new ArrayList<String>();
		ArrayList<String> noMatches = new ArrayList<String>();
		
		for (String key : searchResults.keySet()) {
			if (searchResults.get(key) == null || searchResults.get(key).size() == 0) noMatches.add(key);
			else if (searchResults.get(key).size() == 1) {
				Frame match = searchResults.get(key).get(0);
				if (key.equalsIgnoreCase(match.getLocalID())) exactMatches.add(key);
				else goodMatches.add(key);
			} else {
				ambiguousMatches.add(key);
			}
		}
		
		tabbedSearchResults.setTitleAt(0, "Found " + exactMatches.size() + " terms with exact matches");
		tabbedSearchResults.setTitleAt(1, "Found " + goodMatches.size() + " terms with good matches");
		tabbedSearchResults.setTitleAt(2, "Found " + ambiguousMatches.size() + " terms with abmiguous matches");
		tabbedSearchResults.setTitleAt(3, "Found " + noMatches.size() + " terms with no matches");
		
		int totalGoodMatches = exactMatches.size() + goodMatches.size();
		labelSearchResults.setText("We were able to match " + totalGoodMatches + " out of " + searchResults.keySet().size() + " terms.");
		for (String ID : exactMatches) textSearchExactMatches.setText(textSearchExactMatches.getText() + ID + "\n");
		for (String ID : goodMatches) textSearchGoodMatches.setText(textSearchGoodMatches.getText() + ID + "\n");
		for (String ID : ambiguousMatches) textSearchMultipleMatches.setText(textSearchMultipleMatches.getText() + ID + "\n");
		for (String ID : noMatches) textSearchNoMatches.setText(textSearchNoMatches.getText() + ID + "\n");
		
		textSearchExactMatches.setCaretPosition(0);
		textSearchGoodMatches.setCaretPosition(0);
		textSearchMultipleMatches.setCaretPosition(0);
		textSearchNoMatches.setCaretPosition(0);
	}
	
	private void clearSearchPanel() {
		String output = "";
		int frameIDCount = 0;
		int exactMatchCount = 0;
		int oneMatchCount = 0;
		int multipleMatchesCount = 0;
		int noMatchesCount = 0;
		
		for (String key : searchResults.keySet()) {
			if (searchResults.get(key) == null || searchResults.get(key).size() == 0) noMatchesCount++;
			else if (searchResults.get(key).size() == 1) {
				Frame match = searchResults.get(key).get(0);
				if (key.equalsIgnoreCase(match.getLocalID())) frameIDCount++;
				else oneMatchCount++;
			} else {
				multipleMatchesCount++;
			}
		}
		
		output += "FrameID's provided : " + frameIDCount + "\n";
		output += "Single matches found : " + oneMatchCount + "\n";
		output += "Terms with multiple matches : " + multipleMatchesCount + "\n";
		output += "Terms with no matches : " + noMatchesCount + "\n";
		
		textSearchExactMatches.setText(output);
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
	
	private class ActionPreview extends AbstractAction {
		public ActionPreview() {
			putValue(NAME, "Preview");
			putValue(SHORT_DESCRIPTION, "Preview file before import");
		}
		public void actionPerformed(ActionEvent e) {
			
		}
	}
	
	private void resetForm() {
		cardLayout.show(contentPane, "OptionsPanel");
		cmbImportType.setSelectedIndex(0);
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
