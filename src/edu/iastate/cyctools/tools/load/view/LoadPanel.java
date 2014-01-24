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

import edu.iastate.cyctools.CycToolsError;
import edu.iastate.cyctools.DefaultController;
import edu.iastate.cyctools.InternalStateModel.State;
import edu.iastate.cyctools.externalSourceCode.AbstractViewPanel;
import edu.iastate.cyctools.externalSourceCode.DiffMatchPatch.Patch;
import edu.iastate.cyctools.externalSourceCode.KeyValueComboboxModel;
import edu.iastate.cyctools.externalSourceCode.DiffMatchPatch;
import edu.iastate.cyctools.externalSourceCode.DiffMatchPatch.Diff;
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
	private LinkedList<Patch> patches;
	private int currentPatch;
//	private List<String> original;
//	private List<String> revised;
	private String original;
	private String revised;
	private String importType;
	private ButtonGroup groupImportType;
	private HashMap<String, ArrayList<Frame>> searchResults;
	private JLabel labelSearchResults;
	private JTable tableSearchExactMatches;
	private JTable tableSearchGoodMatches;
	private JTable tableSearchMultipleMatches;
	private JTable tableSearchNoMatches;
	private JTabbedPane tabbedSearchResults;
	private JComboBox cmbImportType = new JComboBox();
	
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
    	
    	optionsPanel.setLayout(new MigLayout("", "[25%][108px][grow][25%]", "[23px][][20px][20px][23px][23px][23px][14px][23px][][][grow]"));
    	
    	JLabel imageLabel = new JLabel("");
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        imageLabel.setOpaque(true);
        imageLabel.setIcon(new ImageIcon(LoadPanel.class.getResource("/resources/step1.png")));
        optionsPanel.add(imageLabel, "cell 0 0 4 1,alignx center,aligny center");
		
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
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser();
				if (fileChooser.showOpenDialog((Component) arg0.getSource()) == JFileChooser.APPROVE_OPTION) {
					textFilePath.setText(fileChooser.getSelectedFile().getAbsolutePath());
				}
			}
		});
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
		optionsPanel.add(btnNext, "cell 2 8,alignx left,aligny center");
		
		noticeLabel = new JLabel();
		noticeLabel.setText("Always backup your database file before modifying it!");
		optionsPanel.add(noticeLabel, "cell 1 10 3 1,alignx left,aligny top");
		
		textFilePath = new JTextField();
		textFilePath.setEditable(false);
		textFilePath.setColumns(50);
		optionsPanel.add(textFilePath, "cell 2 3,alignx left,aligny center");
		
    	return optionsPanel;
    }
    
    private JPanel initFilePanel() {
    	JPanel filePanel = new JPanel();
    	
        filePanel.setLayout(new MigLayout("", "[grow][]", "[][grow][]"));
        
        JLabel imageLabel = new JLabel("");
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        imageLabel.setIcon(new ImageIcon(LoadPanel.class.getResource("/resources/step2.png")));
        filePanel.add(imageLabel, "cell 0 0 2 1,alignx center,aligny center");
        
        JScrollPane SpreadsheetScrollPane = new JScrollPane();
        tableSpreadSheet = new JTable();
        tableSpreadSheet.setFillsViewportHeight(true);
        JTableHeader th = tableSpreadSheet.getTableHeader();
        th.setFont(new Font("Serif", Font.BOLD, 15));
        SpreadsheetScrollPane.setViewportView(tableSpreadSheet);
        filePanel.add(SpreadsheetScrollPane, "cell 0 1 2 1,growx,growy,alignx left,aligny top");
        
        DefaultComboBoxModel<String> modelAdaptor = new DefaultComboBoxModel<String>();
		modelAdaptor.addElement("Standard CSV: Column header determines slot label");
        modelAdaptor.addElement("Annotation Mod: FrameID, SlotValue, AnnotationValue.  Column header determines label");
        modelAdaptor.addElement("MaizeGDB Custom: frameID, goTerm, pubMedID, evCode, timeStampString (dd-mm-yyyy hh-mm-ss), curator");
        cmbAdaptor = new JComboBox<String>(modelAdaptor);
        filePanel.add(cmbAdaptor, "flowx,cell 0 2,alignx right,aligny center");
        
        JButton btnBack = new JButton("Back");
        btnBack.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		cardLayout.show(contentPane, "OptionsPanel");
        	}
        });
        filePanel.add(btnBack, "flowx,cell 1 2,alignx right,aligny center");
        
        JButton btnPreview = new JButton("Preview");
        btnPreview.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
				if (searchFrameIDs()) cardLayout.show(contentPane, "SearchPanel");
        	}
        });
        btnPreview.setAction(actionPreview);
        filePanel.add(btnPreview, "cell 1 2,alignx right,aligny center");
        
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
		tabbedSearchResults.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		searchPanel.add(tabbedSearchResults, "cell 0 2,grow");
		
		tableSearchExactMatches = new JTable();
		JScrollPane scroll1 = new JScrollPane();
		scroll1.setViewportView(tableSearchExactMatches);
		tabbedSearchResults.addTab("Exact Matches", null, scroll1, null);
		
		tableSearchGoodMatches = new JTable();
		JScrollPane scroll2 = new JScrollPane();
		scroll2.setViewportView(tableSearchGoodMatches);
		tabbedSearchResults.addTab("Good Matches", null, scroll2, null);
		
		tableSearchMultipleMatches = new JTable();
		JScrollPane scroll3 = new JScrollPane();
		scroll3.setViewportView(tableSearchMultipleMatches);
		tabbedSearchResults.addTab("Multiple Matches", null, scroll3, null);
		
		tableSearchNoMatches = new JTable();
		JScrollPane scroll4 = new JScrollPane();
		scroll4.setViewportView(tableSearchNoMatches);
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
    	
    	previewPanel.setLayout(new MigLayout("", "[50%,grow][50%,grow]", "[][150:n][grow][]"));
		
		
    	JLabel imageLabel = new JLabel("");
		imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		imageLabel.setIcon(new ImageIcon(LoadPanel.class.getResource("/resources/step3.png")));
		previewPanel.add(imageLabel, "cell 0 0 2 1,alignx center,aligny center");
		
		listFrames = new JList<String>(new DefaultListModel<String>());
		listFrames.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				updateComparison();
			}
		});
		listFrames.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		JScrollPane scrollPanelList = new JScrollPane();
		scrollPanelList.setViewportView(listFrames);
		JLabel lblList = new JLabel("Frames to be Updated");
		lblList.setFont(new Font("Arial", Font.BOLD, 16));
		lblList.setAlignmentX(Component.CENTER_ALIGNMENT);
		scrollPanelList.setColumnHeaderView(lblList);
		previewPanel.add(scrollPanelList, "cell 0 1 2 1,aligny top,grow");
		
		JScrollPane scrollPaneOld = new JScrollPane();
		textAreaOld = new JTextPane();
		scrollPaneOld.setViewportView(textAreaOld);
		JLabel lblOld = new JLabel("Existing Frame Data");
		lblOld.setFont(new Font("Arial", Font.BOLD, 16));
		scrollPaneOld.setColumnHeaderView(lblOld);
		previewPanel.add(scrollPaneOld, "cell 0 2,aligny top,grow");
		
		JScrollPane scrollPaneNew = new JScrollPane();
		textAreaNew = new JTextPane();
		scrollPaneNew.setViewportView(textAreaNew);
		JLabel lblNew = new JLabel("Frame Data after Update");
		lblNew.setFont(new Font("Arial", Font.BOLD, 16));
		scrollPaneNew.setColumnHeaderView(lblNew);
		previewPanel.add(scrollPaneNew, "cell 1 2 2 1,aligny top,grow");
		
		JButton btnNextDiff = new JButton("New button");
		btnNextDiff.setAction(actionNextDiff);
		previewPanel.add(btnNextDiff, "flowx,cell 0 3,alignx left,aligny bottom");
		
		chckbxFilter = new JCheckBox("Show only frames which are altered");
		chckbxFilter.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if (chckbxFilter.isSelected()) {
					//TODO can i filter out go-term annotation changes here, so that a diff curator is ignored?
					framesWhichModifyKB = batchEdits.framesWhichModifyKB(controller.getConnection());
					if (framesWhichModifyKB.isEmpty()) framesWhichModifyKB.addElement("No Frames will modify KB");
					listFrames.setModel(framesWhichModifyKB);
				} else {
					listFrames.setModel(allFramesWithImports);
				}
			}
		});
		previewPanel.add(chckbxFilter, "cell 0 3,alignx left,aligny bottom");
		
		JButton btnBack2 = new JButton("Back");
		btnBack2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cardLayout.show(contentPane, "SearchPanel");
			}
		});
		previewPanel.add(btnBack2, "flowx,cell 1 3,alignx right,aligny bottom");
		
		JButton btnUpload = new JButton("Upload");
		btnUpload.setAction(actionUpload);
		previewPanel.add(btnUpload, "cell 1 3,alignx right,aligny bottom");
		
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
    
//    private static List<String> textToLines(String text) {
//    	// This method is used to convert text (a string) into a list of strings, broken on newline characters.
//    	// Necessary for the text diff tool.
//		List<String> lines = new LinkedList<String>();
//		String line = "";
//		BufferedReader in = null;
//		try {
//		        in = new BufferedReader(new StringReader(text));
//		        while ((line = in.readLine()) != null) {
//		                lines.add(line);
//		        }
//		} catch (IOException e) {
//		        e.printStackTrace();
//		} finally {
//			try {
//				in.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		return lines;
//    }

    
//    private void diff_lineMode(String text1, String text2) {
//    	  diff_match_patch dmp = new diff_match_patch();
//    	  var a = dmp.diff_linesToChars_(text1, text2);
//    	  var lineText1 = a[0];
//    	  var lineText2 = a[1];
//    	  var lineArray = a[2];
//
//    	  var diffs = dmp.diff_main(lineText1, lineText2, false);
//
//    	  dmp.diff_charsToLines_(diffs, lineArray);
//    	  return diffs;
//    	}
    
	private void updateComparison() {
		String frameID = "";
		original = null;
		revised = null;
		currentPatch = 0;
		patches = null;
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
		textAreaOld.setCaretPosition(0);
		original = textAreaOld.getText();
		
		// apply frame edits to local copy of frame
		Frame updatedFrame = batchEdits.updateLocalFrame(originalFrame);//controller.updateLocalFrame(frameID, frameEditArray);
		
		// return print of the updated frame
		String updatedFrameString = controller.frameToString(updatedFrame);
		textAreaNew.setText(updatedFrameString);
		textAreaNew.setCaretPosition(0);
		revised  = textAreaNew.getText();
		
		DiffMatchPatch dmp = new DiffMatchPatch();
		currentPatch = 0;
		LinkedList<Diff> diffs = dmp.diff_lines_only(original, revised);
		patches = dmp.patch_make(original, diffs);
		actionNextDiff.setEnabled(true);
		
		highlightDiffs();
	}
	
	private void highlightDiffs() {
		// Highlight diffs
		textAreaOld.getHighlighter().removeAllHighlights();
		textAreaNew.getHighlighter().removeAllHighlights();
		
		// DiffMatchPatch assumes a rolling context (i.e. patches must be applied in order from start to finish).  Thus, when calculating the what to highlight
		// on the original text, we have to modify our context since we are showing the original and not applying the patches to the original text.
		// Also, DiffMatchPatch pads the beginning and end of a patch with (default 4) extra chars to provide overlap and context. Don't want to highlight the padding though.
		int contextShift = 0;
		for (Patch patch : patches) {
			// Highlight the original text
			DefaultHighlighter.DefaultHighlightPainter highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
			try {
				textAreaOld.getHighlighter().addHighlight(patch.start1 + 4 - contextShift, patch.start1 + patch.length1 - 4 - contextShift, highlightPainter);
				// Context difference is the difference in lengths of all previous patches
				contextShift = (patch.start2 + patch.length2) - (patch.start1 + patch.length1);
			} catch (BadLocationException ble) {
				ble.printStackTrace();
			}
		}

		// Highlight the revised text
		for (Patch patch : patches) {
			// Highlight the original text
			DefaultHighlighter.DefaultHighlightPainter highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
			try {
				textAreaNew.getHighlighter().addHighlight(patch.start2 + 4, patch.start2 + patch.length2 - 4, highlightPainter);
			} catch (BadLocationException ble) {
				ble.printStackTrace();
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
		
		// Convert user provided IDs to frame IDs, remove imports that can't be matched to an existing frame
		DefaultTableModel model = (DefaultTableModel) tableSpreadSheet.getModel();
		int nRow = model.getRowCount(), nCol = model.getColumnCount();
		for (int i = nRow-1; i >= 0; i--) {
			String givenID = (String) model.getValueAt(i, 0); // User provided ID must always be in first column
			ArrayList<Frame> matches = searchResults.get(givenID);
			if (matches.size() == 1) {
				model.setValueAt(matches.get(0).getLocalID(), i, 0);
			} else {
				model.removeRow(i);//if there is not a good matching FrameID, we skip this line as a condition of the import.  Can't import data if we don't know where to put it. //TODO Critical error, this removal is destructive and causes the line to be removed from the original table on the file view screen!!!!
			}
		}
		
		if (model.getRowCount() == 0) {
			JOptionPane.showMessageDialog(this, "No data could be matched to frames in this database for the selected frame type. Unable to proceed with import", "No Data to Import!", JOptionPane.WARNING_MESSAGE);
			return;
		}
		cardLayout.show(contentPane, "PreviewPanel");
	    
	    batchEdits = new BatchUpdate(selectedAdaptor.tableToFrameUpdates(model), controller.getConnection());
//	    batchEdits = new BatchUpdate(selectedAdaptor.tableToFrameUpdates(tableSpreadSheet.getModel()), controller.getConnection());
		batchEdits.addPropertyChangeListener(controller);
		
		batchEdits.downloadFrames(controller.getConnection());
		allFramesWithImports = batchEdits.getFrameIDsModel();
		listFrames.setModel(allFramesWithImports);
	}
	
	private boolean searchFrameIDs() { //TODO convert to worker string task
		JavacycConnection conn = controller.getConnection();
		
		controller.lockToolBarOrganismSelect();
		TableModel tb = tableSpreadSheet.getModel();
		
		// Do a fast preliminary check to see if any user provided ID's are not frames in this database
//		JOptionPane.showMessageDialog(this, "Validating import objects", "Validating", JOptionPane.PLAIN_MESSAGE);
		int noMatchCount = 0;
		for (int rowIndex = 0; rowIndex < tb.getRowCount(); rowIndex++) {
			String userProvidedID = (String) tb.getValueAt(rowIndex, 0);
			try {
				if (!conn.frameExists(userProvidedID)) noMatchCount++;
			} catch (PtoolsErrorException e) {
				e.printStackTrace();
			}
		}
		if (noMatchCount > 0) {
			int n = JOptionPane.showConfirmDialog(this, "Some IDs do not match any objects in the database.  Would you like to perform a search for these objects in the database?"
					, "Validating", JOptionPane.YES_NO_OPTION);
			if (n == JOptionPane.NO_OPTION) return false;
		}
				
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
		
		// Copy the table model, but insert an extra column at the beginning to store the matched frame IDs
		consumeSearchResults();
		
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
		tabbedSearchResults.setTitleAt(2, ambiguousMatches.size() + " terms with abmiguous matches");
		tabbedSearchResults.setTitleAt(3, noMatches.size() + " terms not found");
		
		int totalGoodMatches = exactMatches.size() + goodMatches.size();
		labelSearchResults.setText("We were able to match " + totalGoodMatches + " out of " + searchResults.keySet().size() + " terms.");
		
		return true;
	}
	
	private void consumeSearchResults() {
		ArrayList<Object[]> exactMatches = new ArrayList<Object[]>();
		ArrayList<Object[]> goodMatches = new ArrayList<Object[]>();
		ArrayList<Object[]> ambiguousMatches = new ArrayList<Object[]>();
		ArrayList<Object[]> noMatches = new ArrayList<Object[]>();
		
		TableModel tb = tableSpreadSheet.getModel();
		int nCol = tb.getColumnCount();
		
		// Sort out the original table into different groups based on the quality of the match
		// Copy whole lines from the original table
		for (int rowIndex = 0; rowIndex < tb.getRowCount(); rowIndex++) {
			Object[] row = new Object[nCol];
			for (int i = 0; i < nCol; i++) {
				row[i] = tb.getValueAt(rowIndex, i);
			}
			
			String userProvidedID = (String) tb.getValueAt(rowIndex, 0);
			if (searchResults.get(userProvidedID) == null || searchResults.get(userProvidedID).size() == 0) noMatches.add(row);
			else if (searchResults.get(userProvidedID).size() == 1) {
				Frame match = searchResults.get(userProvidedID).get(0);
				if (userProvidedID.equalsIgnoreCase(match.getLocalID())) exactMatches.add(row);
				else goodMatches.add(row);
			} else {
				ambiguousMatches.add(row);
			}
		}
		
		// Exact Matches
		if (exactMatches.size() > 0) {
			int nRow = exactMatches.size();
			Object[] header = new Object[nCol+1];
			Object[][] data = new Object[nRow][nCol+1];
			header[0] = "Matched Frame";
			for (int j = 0; j < nCol; j++) {
				header[j+1] = tableSpreadSheet.getColumnName(j);
			}
			int rowIndex = 0;
			for (Object[] row : exactMatches) {
				Object[] extendedRow = new Object[row.length+1];
				extendedRow[0] = searchResults.get(row[0]).get(0).getLocalID();
				for (int i = 0; i < row.length; i++) {
					extendedRow[i+1] = row[i];
				}
				data[rowIndex] = extendedRow;
				rowIndex++;
			}
			DefaultTableModel newModel = new DefaultTableModel(data, header);
			tableSearchExactMatches.setModel(newModel);
		}
		
		// Good Matches
		if (goodMatches.size() > 0) {
			int nRow = goodMatches.size();
			Object[] header = new Object[nCol+1];
			Object[][] data = new Object[nRow][nCol+1];
			header[0] = "Matched Frame";
			for (int j = 0; j < nCol; j++) {
				header[j+1] = tableSpreadSheet.getColumnName(j);
			}
			int rowIndex = 0;
			for (Object[] row : goodMatches) {
				Object[] extendedRow = new Object[row.length+1];
				extendedRow[0] = searchResults.get(row[0]).get(0).getLocalID();
				for (int i = 0; i < row.length; i++) {
					extendedRow[i+1] = row[i];
				}
				data[rowIndex] = extendedRow;
				rowIndex++;
			}
			DefaultTableModel newModel = new DefaultTableModel(data, header);
			tableSearchGoodMatches.setModel(newModel);
		}
		
		// Ambiguous Matches
		if (ambiguousMatches.size() > 0) {
			int nRow = ambiguousMatches.size();
			Object[] header = new Object[nCol];
			Object[][] data = new Object[nRow][nCol];
			for (int j = 0; j < nCol; j++) {
				header[j] = tableSpreadSheet.getColumnName(j);
			}
			int rowIndex = 0;
			for (Object[] row : ambiguousMatches) {
				data[rowIndex] = row;
				rowIndex++;
			}
			DefaultTableModel newModel = new DefaultTableModel(data, header);
			tableSearchMultipleMatches.setModel(newModel);
		}
		
		// No Matches
		if (noMatches.size() > 0) {
			int nRow = noMatches.size();
			Object[] header = new Object[nCol];
			Object[][] data = new Object[nRow][nCol];
			for (int j = 0; j < nCol; j++) {
				header[j] = tableSpreadSheet.getColumnName(j);
			}
			int rowIndex = 0;
			for (Object[] row : noMatches) {
				data[rowIndex] = row;
				rowIndex++;
			}
			DefaultTableModel newModel = new DefaultTableModel(data, header);
			tableSearchNoMatches.setModel(newModel);
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
				
				int contextShift = 0;
				for (int i = 0; i < currentPatch; i++) {
					Patch patch = patches.get(i);
					contextShift += (patch.start2 + patch.length2) - (patch.start1 + patch.length1);
				}
				
				Patch patch = patches.get(currentPatch);
				currentPatch++;
				
				// Highlight the original text
				DefaultHighlighter.DefaultHighlightPainter highlightPainterOld = new DefaultHighlighter.DefaultHighlightPainter(Color.GREEN);
				
				// Remove previous highlight
				for (Highlight h : textAreaOld.getHighlighter().getHighlights()) {
					if (h.getStartOffset() == patch.start1 + 4 - contextShift) {
						textAreaOld.getHighlighter().removeHighlight(h);
					}
				}
				
				// Highlight this change
				try {
					textAreaOld.getHighlighter().addHighlight(patch.start1 + 4 - contextShift, patch.start1 + patch.length1 - 4 - contextShift, highlightPainterOld);
				} catch (BadLocationException ble) {
					ble.printStackTrace();
				}
				textAreaOld.scrollRectToVisible(textAreaOld.modelToView(patch.start1 + 4 - contextShift));
				
				
				// Highlight the revised text
				DefaultHighlighter.DefaultHighlightPainter highlightPainterNew = new DefaultHighlighter.DefaultHighlightPainter(Color.GREEN);
				
				// Remove previous highlight
				for (Highlight h : textAreaNew.getHighlighter().getHighlights()) {
					if (h.getStartOffset() == patch.start2 + 4) {
						textAreaNew.getHighlighter().removeHighlight(h);
					}
				}
				
				// Highlight this change
				try {
					textAreaNew.getHighlighter().addHighlight(patch.start2 + 4, patch.start2 + patch.length2 - 4, highlightPainterNew);
				} catch (BadLocationException ble) {
					ble.printStackTrace();
				}
				textAreaNew.scrollRectToVisible(textAreaNew.modelToView(patch.start2 + 4));
			} catch (Exception exception) {
				currentPatch = 0;
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
