package edu.iastate.cyctools.tools.load.view;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter.Highlight;
import javax.swing.AbstractAction;
import javax.swing.Action;
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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import edu.iastate.cyctools.CycToolsError;
import edu.iastate.cyctools.DefaultController;
import edu.iastate.cyctools.InternalStateModel.State;
import edu.iastate.cyctools.externalSourceCode.AbstractViewPanel;
import edu.iastate.cyctools.externalSourceCode.DiffMatchPatch.Patch;
import edu.iastate.cyctools.externalSourceCode.KeyValueComboboxModel;
import edu.iastate.cyctools.externalSourceCode.DiffMatchPatch;
import edu.iastate.cyctools.externalSourceCode.DiffMatchPatch.Diff;
import edu.iastate.cyctools.tools.load.fileAdaptors.AbstractFileAdaptor;
import edu.iastate.cyctools.tools.load.fileAdaptors.CreateRegulationAdaptor;
import edu.iastate.cyctools.tools.load.fileAdaptors.DeleteFrameAdaptor;
import edu.iastate.cyctools.tools.load.fileAdaptors.GOTermAdaptor;
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
import java.awt.event.ActionListener;
import javax.swing.border.EtchedBorder;

@SuppressWarnings({"serial", "rawtypes", "unchecked"})
public class LoadPanel extends AbstractViewPanel {
	private AbstractFileAdaptor selectedAdaptor;
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
	private String original;
	private String revised;
	private HashMap<String, ArrayList<Frame>> searchResults;
	private JLabel labelSearchResults;
	private JTable tableSearchExactMatches;
	private JTable tableSearchGoodMatches;
	private JTable tableSearchMultipleMatches;
	private JTabbedPane tabbedSearchResults;
	private JComboBox cmbImportType = new JComboBox();
	private JComboBox comboBoxAuthor;
	private JPanel panelCredits;
	private JCheckBox checkBoxCredits;
	private JComboBox comboBoxOrganization;
	private JLabel lblChooseOrganization;
	private JLabel lblChooseIndividualAuthor;
	private JLabel lblSaveOrRejectNote;
	private JLabel confirmImageLabel;
	
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
    	
		JPanel databasePanel = initDatabasePanel();
		JPanel optionsPanel = initOptionsPanel();
    	JPanel filePanel = initFilePanel();
    	JPanel searchPanel = initSearchPanel();
    	JPanel previewPanel = initPreviewPanel();
    	JPanel reviewPanel = initReviewPanel();
        
        setLayout(new CardLayout(0, 0));
        cardLayout = (CardLayout)(this.getLayout());
        
        add(databasePanel, "DatabasePanel");
		add(optionsPanel, "OptionsPanel");
		add(filePanel, "FilePanel");
		add(searchPanel, "SearchPanel");
		add(previewPanel, "PreviewPanel");
		add(reviewPanel, "ReviewPanel");
	}
    
    private JPanel initDatabasePanel() {
    	JPanel databasePanel = new JPanel();
    	databasePanel.setLayout(new MigLayout("", "[][grow][]", "[][grow][]"));
    	
    	DefaultComboBoxModel<String> modelAdaptor = new DefaultComboBoxModel<String>();
		modelAdaptor.addElement("Slot Value Import: FrameID, SlotValues (Column header determines slot label)");
        modelAdaptor.addElement("Annotation Value Import: FrameID, SlotValue, AnnotationValue (Column header determines slot and annotation labels)");
        modelAdaptor.addElement("GO-Term Import: FrameID, GoTerm, PubMedID, EVCode, TimeStampString (mm-dd-yyyy hh-mm-ss), Curator");
        modelAdaptor.addElement("Create Transcriptional Regulation: RegulatorFrameID, RegulateeFrameID, RegulationMode(- or +)");
        modelAdaptor.addElement("Delete Frame: FrameID");
        cmbAdaptor = new JComboBox<String>(modelAdaptor);
    	databasePanel.add(cmbAdaptor, "flowx,cell 1 2,alignx right,aligny center");
    	
    	JButton btnNext_1 = new JButton("Next");
    	btnNext_1.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent arg0) {
    			if (cmbAdaptor.getSelectedIndex() == 0) selectedAdaptor = new SimpleSlotValueImport();
				else if (cmbAdaptor.getSelectedIndex() == 1) selectedAdaptor = new SimpleAnnotationValueImport();
				else if (cmbAdaptor.getSelectedIndex() == 2) selectedAdaptor = new GOTermAdaptor();
				else if (cmbAdaptor.getSelectedIndex() == 3) selectedAdaptor = new CreateRegulationAdaptor();
				else if (cmbAdaptor.getSelectedIndex() == 4) selectedAdaptor = new DeleteFrameAdaptor();
    			
    			controller.lockToolBarOrganismSelect();
    			cardLayout.show(contentPane, "OptionsPanel");
    			loadCreditableEntitiesLists();
    		}
    	});
    	
    	JLabel lblPleaseSelectA = new JLabel("<html>\r\n<p>Please select a database to modify in the upper right corner.</p>\r\n<p>Select an import type below and press Next to continue.</p>\r\n</html>");
    	databasePanel.add(lblPleaseSelectA, "cell 1 1,alignx center,aligny center");
    	databasePanel.add(btnNext_1, "cell 2 2,alignx right,aligny center");
    	return databasePanel;
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
    	
    	optionsPanel.setLayout(new MigLayout("", "[25%][108px,grow][grow][25%]", "[23px][][20px][20px][23px][23px][23px][14px][23px][][grow][]"));
    	
    	JLabel imageLabel = new JLabel("");
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        imageLabel.setOpaque(true);
        imageLabel.setIcon(new ImageIcon(LoadPanel.class.getResource("/resources/step1.png")));
        optionsPanel.add(imageLabel, "cell 0 0 4 1,alignx center,aligny center");
        
        noticeLabel = new JLabel();
        noticeLabel.setText("Always backup your database file before modifying it!");
        optionsPanel.add(noticeLabel, "cell 0 1 4 1,alignx center,aligny top");
		
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
		
		textFilePath = new JTextField();
		textFilePath.setEditable(false);
		textFilePath.setColumns(50);
		optionsPanel.add(textFilePath, "cell 2 3,alignx left,aligny center");
		
		JLabel lblAuthorCredits = new JLabel("<html>\r\n<div style='text-align: center;'>\r\nUpdate Author Credits <br>\r\n(Only applied if frame is modified)\r\n</div>\r\n</html>");
		optionsPanel.add(lblAuthorCredits, "flowx,cell 1 9,alignx right,aligny center");
		
		panelCredits = new JPanel();
		panelCredits.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panelCredits.setEnabled(false);
		optionsPanel.add(panelCredits, "cell 2 9,grow");
		
		lblChooseIndividualAuthor = new JLabel("Choose Individual Author");
		lblChooseIndividualAuthor.setEnabled(false);
		panelCredits.setLayout(new MigLayout("", "[100px][grow]", "[38px][]"));
		panelCredits.add(lblChooseIndividualAuthor, "cell 0 0,alignx right,aligny center");
		
		comboBoxAuthor = new JComboBox();
		comboBoxAuthor.setEnabled(false);
		comboBoxAuthor.setRenderer(new DefaultListCellRenderer() {
	        @Override
	        public Component getListCellRendererComponent(final JList list, Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
	        	if (value != null) {
	        		value = value.toString().substring(value.toString().indexOf("=")+1, value.toString().length());
	        		value = value.toString().replace("\"", "");
	        	}
	            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
	        }
	    });
		panelCredits.add(comboBoxAuthor, "cell 1 0,growx,aligny center");
		
		lblChooseOrganization = new JLabel("Choose Organization");
		lblChooseOrganization.setEnabled(false);
		panelCredits.add(lblChooseOrganization, "cell 0 1,alignx right,aligny center");
		comboBoxOrganization = new JComboBox();
		comboBoxOrganization.setEnabled(false);
		comboBoxOrganization.setRenderer(new DefaultListCellRenderer() {
	        @Override
	        public Component getListCellRendererComponent(final JList list, Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
	        	if (value != null) {
	        		value = value.toString().substring(value.toString().indexOf("=")+1, value.toString().length());
	        		value = value.toString().replace("\"", "");
	        	}
	            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
	        }
	    });
		panelCredits.add(comboBoxOrganization, "cell 1 1,growx,aligny center");
		
		checkBoxCredits = new JCheckBox("");
		checkBoxCredits.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if (checkBoxCredits.isSelected()) {
					panelCredits.setEnabled(true);
					lblChooseIndividualAuthor.setEnabled(true);
					lblChooseOrganization.setEnabled(true);
					comboBoxAuthor.setEnabled(true);
					comboBoxOrganization.setEnabled(true);
				} else {
					panelCredits.setEnabled(false);
					lblChooseIndividualAuthor.setEnabled(false);
					lblChooseOrganization.setEnabled(false);
					comboBoxAuthor.setEnabled(false);
					comboBoxOrganization.setEnabled(false);
				}
			}
		});
		optionsPanel.add(checkBoxCredits, "cell 1 9,alignx right,aligny center");
		
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
    			
				if (checkBoxCredits.isSelected() && comboBoxAuthor.getSelectedIndex() == 0 && comboBoxOrganization.getSelectedIndex() == 0) {
    				JOptionPane.showMessageDialog(DefaultController.mainJFrame, "If authorship credits checkbox is selected, at least 1 author or organization is required", "Error", JOptionPane.ERROR_MESSAGE);
    				return;
				}
    			
    			cardLayout.show(contentPane, "FilePanel");
			}
		});
		
		JButton btnBack_2 = new JButton("Back");
		btnBack_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resetForm();
			}
		});
		optionsPanel.add(btnBack_2, "flowx,cell 3 11,alignx right,aligny center");
		optionsPanel.add(btnNext, "cell 3 11,alignx right,aligny center");
		
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
        		if (cmbAdaptor.getSelectedIndex() == 3) {
        			try {
						openPreviewPanel();
					} catch (PtoolsErrorException e1) {
						e1.printStackTrace();
					}
        		} else if (searchFrameIDs()) cardLayout.show(contentPane, "SearchPanel");
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
        imageLabel.setIcon(new ImageIcon(LoadPanel.class.getResource("/resources/step3.png")));
        searchPanel.add(imageLabel, "cell 0 0,alignx center,aligny center");
		
		labelSearchResults = new JLabel("Waiting for search results....");
		searchPanel.add(labelSearchResults, "cell 0 1,alignx left,aligny center");
		
		tabbedSearchResults = new JTabbedPane(JTabbedPane.TOP);
		tabbedSearchResults.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		searchPanel.add(tabbedSearchResults, "cell 0 2,grow");
		
		tableSearchExactMatches = new JTable();
		JScrollPane scroll1 = new JScrollPane();
		scroll1.setViewportView(tableSearchExactMatches);
		tabbedSearchResults.addTab("FrameID Matches", null, scroll1, null);
		
		tableSearchGoodMatches = new JTable();
		JScrollPane scroll2 = new JScrollPane();
		scroll2.setViewportView(tableSearchGoodMatches);
		tabbedSearchResults.addTab("Synonym Matches", null, scroll2, null);
		
		tableSearchMultipleMatches = new JTable();
		JScrollPane scroll3 = new JScrollPane();
		scroll3.setViewportView(tableSearchMultipleMatches);
		tabbedSearchResults.addTab("Multiple Matches", null, scroll3, null);
		
		JButton btnAccept = new JButton("Accept");
		btnAccept.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
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
		imageLabel.setIcon(new ImageIcon(LoadPanel.class.getResource("/resources/step4.png")));
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
		chckbxFilter.setRolloverEnabled(false);
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
    	
    	reviewPanel.setLayout(new MigLayout("", "[109px,grow][155px]", "[][251px,grow][57px]"));
    	
    	confirmImageLabel = new JLabel("");
    	confirmImageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    	confirmImageLabel.setIcon(new ImageIcon(LoadPanel.class.getResource("/resources/step5.png")));
        reviewPanel.add(confirmImageLabel, "cell 0 0 2 1,alignx center,aligny center");
        
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
        reviewPanel.add(tabbedPane, "cell 0 1 2 1,grow");
        
        JButton btnSave = new JButton("Save");
        btnSave.setAction(actionSave);
        reviewPanel.add(btnSave, "flowx,cell 1 2,alignx right,aligny center");
        
        JButton btnRevert = new JButton("Revert");
        btnRevert.setAction(actionRevert);
        reviewPanel.add(btnRevert, "cell 1 2,alignx right,aligny center");
        
        JButton btnSaveTransactionLog = new JButton("Save Log");
        btnSaveTransactionLog.setAction(actionSaveLog);
        reviewPanel.add(btnSaveTransactionLog, "flowx,cell 0 2,alignx left,aligny center");
        
        lblSaveOrRejectNote = new JLabel("Save changes to database? *Warning, changes will be permanent if saved.");
        reviewPanel.add(lblSaveOrRejectNote, "cell 0 2,alignx right,aligny center");
        
        return reviewPanel;
    }
    
    protected void loadCreditableEntitiesLists() {
    	JavacycConnection conn = controller.getConnection();
		try {
			KeyValueComboboxModel modelAuthors = new KeyValueComboboxModel();
			modelAuthors.put("-None Selected-", "-None Selected-");
	    	ArrayList<String> authors;
			authors = conn.getClassAllInstances("|People|");
			for (String author : authors) {
				String commonName = conn.getSlotValue(author, "Common-Name");
				if (commonName == null || commonName.length() == 0 || commonName.equalsIgnoreCase("NIL")) commonName = author;
				modelAuthors.put(author, commonName);
			}
			comboBoxAuthor.setModel(modelAuthors);
			comboBoxAuthor.setSelectedIndex(0);
			
			
			KeyValueComboboxModel modelOrganizations = new KeyValueComboboxModel();
			modelOrganizations.put("-None Selected-", "-None Selected-");
	    	ArrayList<String> organizations;
			organizations = conn.getClassAllInstances("|Organizations|");
			for (String organization : organizations) {
				String commonName = conn.getSlotValue(organization, "Common-Name");
				if (commonName == null || commonName.length() == 0 || commonName.equalsIgnoreCase("NIL")) commonName = organization;
				modelOrganizations.put(organization, commonName);
			}
			comboBoxOrganization.setModel(modelOrganizations);
			comboBoxOrganization.setSelectedIndex(0);
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		}
	}
    
	private void updateComparison() {
		String frameID = "";
		original = null;
		revised = null;
		currentPatch = 0;
		patches = null;
		actionNextDiff.setEnabled(false);
		try {
			frameID = listFrames.getSelectedValue().toString();
			if (frameID == null) return; // Ignore request to compare frame if no frame is selected (or list is empty)
			if (frameID.equalsIgnoreCase("No Frames will modify KB")) {
				textAreaOld.setText("");
				textAreaNew.setText("");
				return; // This is the place holder for an empty list
			}
		} catch (NullPointerException e) {
			return;  // Ignore request to compare frame if no frame is selected (or list is empty)
		}
		
		Frame originalFrame = batchEdits.getFrameByID(frameID);
		String originalFrameString = controller.frameToString(originalFrame);
		
		if (originalFrame == null || originalFrameString == null || originalFrameString.equalsIgnoreCase("")) {
			String message = "Frame " + frameID + " does not exist in database " + controller.getSelectedOrganism();
			textAreaOld.setText(message);
			originalFrame = new Frame(controller.getConnection(), frameID);
			originalFrameString = controller.frameToString(originalFrame);
		} else textAreaOld.setText(originalFrameString);
		textAreaOld.setCaretPosition(0);
		original = textAreaOld.getText();
		
		// apply frame edits to local copy of frame
		Frame updatedFrame = batchEdits.updateLocalFrame(originalFrame);
		
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
		textAreaOld.setText("");
		textAreaNew.setText("");
		
		DefaultTableModel model = new DefaultTableModel();
		if (cmbAdaptor.getSelectedIndex() == 3) {
			model = getDataFromFile();
		} else model = getDataFromMatches();
	    
		if (model.getRowCount() == 0) {
			JOptionPane.showMessageDialog(this, "No data could be matched to frames in this database for the selected frame type. Unable to proceed with import", "No Data to Import!", JOptionPane.WARNING_MESSAGE);
			return;
		}
	    
	    batchEdits = new BatchUpdate(selectedAdaptor.tableToFrameUpdates(model), this);
		batchEdits.addPropertyChangeListener(controller);
		
		batchEdits.downloadFrames(controller.getConnection());
		allFramesWithImports = batchEdits.getFrameIDsModel();
		listFrames.setModel(allFramesWithImports);
	}
	
	public DefaultTableModel getDataFromFile() {
		DefaultTableModel model = new DefaultTableModel();
		DefaultTableModel dtmFile = (DefaultTableModel) tableSpreadSheet.getModel();
		int nRow = dtmFile.getRowCount();
	    int nCol = dtmFile.getColumnCount();
	    
	    Object[] tableHeaders = new Object[nCol];
	    for (int i = 1; i < nCol; i++) {
			if (i < 1) tableHeaders[i] = tableSpreadSheet.getColumnModel().getColumn(i-1).getHeaderValue();
	    }
	    
	    if (nCol != 0) { // If no data, then don't try to parse it out
		    Object[][] tableData = new Object[nRow][nCol];
			
		    for (int i = 0 ; i < nRow; i++) {
		        for (int j = 0 ; j < nCol ; j++) {
		        	tableData[i][j] = dtmFile.getValueAt(i,j);
		        }
		    }
		    
		    model = new DefaultTableModel(tableData, tableHeaders);
	    }
	    
	    return model;
	}
	
	public DefaultTableModel getDataFromMatches() {
		// Get data from exact frame matches
		DefaultTableModel model = new DefaultTableModel();
		DefaultTableModel dtmExactMatches = (DefaultTableModel) tableSearchExactMatches.getModel();
		int nRow1 = dtmExactMatches.getRowCount();
		DefaultTableModel dtmGoodMatches = (DefaultTableModel) tableSearchGoodMatches.getModel();
	    int nRow2 = dtmGoodMatches.getRowCount();
	    
	    int nCol; Object[] tableHeaders = new Object[0];
	    if (dtmExactMatches.getColumnCount() > 0) {
	    	nCol = dtmExactMatches.getColumnCount();
	    	tableHeaders = new Object[nCol-1];
	    	for (int i=0; i < nCol; i++) { // Skip 2nd column, headers are same in both tables and only need to be retrieved once
				if (i < 1) tableHeaders[i] = tableSearchExactMatches.getColumnModel().getColumn(i).getHeaderValue();
				if (i > 1) tableHeaders[i-1] = tableSearchExactMatches.getColumnModel().getColumn(i).getHeaderValue();
			}
	    } else if (dtmGoodMatches.getColumnCount() > 0) {
	    	nCol = dtmGoodMatches.getColumnCount();
	    	tableHeaders = new Object[nCol-1];
	    	for (int i=0; i < nCol; i++) { // Skip 2nd column, headers are same in both tables and only need to be retrieved once
				if (i < 1) tableHeaders[i] = tableSearchGoodMatches.getColumnModel().getColumn(i).getHeaderValue();
				if (i > 1) tableHeaders[i-1] = tableSearchGoodMatches.getColumnModel().getColumn(i).getHeaderValue();
			}
	    } else {
	    	nCol = 0;
	    }
	    
	    if (nCol != 0) { // If no data, then don't try to parse it out
		    Object[][] tableData = new Object[nRow1+nRow2][nCol-1];
			
			// Get data from exact frame matches
		    for (int i = 0 ; i < nRow1 ; i++) {
		        for (int j = 0 ; j < nCol ; j++) {
		        	if (j < 1) tableData[i][j] = dtmExactMatches.getValueAt(i,j);
		        	if (j > 1) tableData[i][j-1] = dtmExactMatches.getValueAt(i,j);
		        }
		    }
		    
		    // Get data from synonym matches (headers will be same as frame id matches)
		    for (int i = 0 ; i < nRow2 ; i++) {
		        for (int j = 0 ; j < nCol ; j++) {
		        	if (j < 1) tableData[nRow1+i][j] = dtmGoodMatches.getValueAt(i,j);
		        	if (j > 1) tableData[nRow1+i][j-1] = dtmGoodMatches.getValueAt(i,j);
		        }
		    }
		    
		    model = new DefaultTableModel(tableData, tableHeaders);
	    }
	    
	    return model;
	}
	
	public void showPreviewPanel() {
		cardLayout.show(contentPane, "PreviewPanel");
	}
	
	public void addAuthorCredits() {
		// Add in AuthorCreditUpdates for frames which are modified, must have downloadFrames first
		if (checkBoxCredits.isSelected() && (comboBoxAuthor.getSelectedIndex() != 0 || comboBoxOrganization.getSelectedIndex() != 0)) {
			String author;
			if (comboBoxAuthor.getSelectedIndex() == 0) author = "";
			else author = ((Entry<String, String>)comboBoxAuthor.getSelectedItem()).getKey();
			
			String organization;
			if (comboBoxOrganization.getSelectedIndex() == 0) organization = "";
			else organization = ((Entry<String, String>)comboBoxOrganization.getSelectedItem()).getKey();
			
			batchEdits.createAuthorCredits(author, organization, controller.getConnection());
		}
	}
	
	private boolean searchFrameIDs() { //TODO convert to worker string task
		JavacycConnection conn = controller.getConnection();
		
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
		
		// Copy the table model, but insert an extra column at the beginning to store the matched frame IDs
		SortResults results = consumeSearchResults();
		
		tabbedSearchResults.setTitleAt(0, "Found " + results.getExactMatches().size() + " terms with FrameID matches in database");
		tabbedSearchResults.setTitleAt(1, "Found " + results.getGoodMatches().size() + " terms with Synonym matches");
		tabbedSearchResults.setTitleAt(2, (results.getAmbiguousMatches().size() + results.getNoMatches().size()) + " terms with ambiguous matches or no matches in database");
		
		labelSearchResults.setText("<html><body style='width: 100%'>We were able to match " + results.getExactMatches().size() + " out of " + searchResults.keySet().size() + " based on FrameIDs.  An " +
				"additional " + results.getGoodMatches().size() + " out of " + searchResults.keySet().size() + " terms were matched based on synonyms.  This search was performed " +
						"on " + ((Entry<String, String>)cmbImportType.getSelectedItem()).getKey() + " in the database " + controller.getSelectedOrganism() + ".");
		
		return true;
	}
	
	private SortResults consumeSearchResults() {
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
			if (searchResults.get(userProvidedID) == null || searchResults.get(userProvidedID).size() == 0) noMatches.add(row); // Case: No matches returned by search
			else {
				boolean frameIDMatchFound = false;
				int synonymMatchesCount = 0;
				for (Frame match : searchResults.get(userProvidedID)) {
					if (userProvidedID.equalsIgnoreCase(match.getLocalID())) {
						frameIDMatchFound = true;
						break;
					}
					
					try {
						for (String name : match.getNames()) {
							if (userProvidedID.equalsIgnoreCase(name.replaceAll("\"", ""))) {
								synonymMatchesCount++;
								break; // Only increment by 1 per frame with a matching synonym
							}
						}
					} catch (PtoolsErrorException e) {
						e.printStackTrace();
					}
				}
				
				if (frameIDMatchFound) exactMatches.add(row); // Case: FrameID matched search exactly, can't possibly have better match to something else so we can safely stop looking
				else if (synonymMatchesCount == 0) {
					noMatches.add(row); // Case: No exact matches found by search
				} else if (synonymMatchesCount == 1) {
					goodMatches.add(row); // Case: 1 and only 1 synonym had an exact match
				} else {
					ambiguousMatches.add(row); // Case: We have exact matches on the synonyms of two or more frames.  Unlikely, but if it happens we can't use this match.
				}
			}
		}
		
		// Exact Matches to frame ID's in one table
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
			
			TableColumnModel tcm = tableSearchExactMatches.getColumnModel();
			TableColumn tm = tcm.getColumn(0);
			tm.setCellRenderer(new CustomRenderer());
		}
		
		// Exact Matches to synonyms in one table
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
				String matchID = ""; // Need to do search again to make sure we have the one frame with a matching synonym.  We already checked to make sure that only 1 frame will be a match.
				String userProvidedID = (String) row[0];
				for (Frame match : searchResults.get(userProvidedID)) {
					try {
						for (String name : match.getNames()) {
							if (userProvidedID.equalsIgnoreCase(name.replaceAll("\"", ""))) {
								matchID = match.getLocalID();
							}
						}
					} catch (PtoolsErrorException e) {
						e.printStackTrace();
					}
				}
				extendedRow[0] = matchID;
				for (int i = 0; i < row.length; i++) {
					extendedRow[i+1] = row[i];
				}
				data[rowIndex] = extendedRow;
				rowIndex++;
			}
			
			DefaultTableModel newModel = new DefaultTableModel(data, header);
			tableSearchGoodMatches.setModel(newModel);
			
			TableColumnModel tcm = tableSearchGoodMatches.getColumnModel();
			TableColumn tm = tcm.getColumn(0);
			tm.setCellRenderer(new CustomRenderer());
		}
		
		// Ambiguous and Empty Matches in another table
		if (ambiguousMatches.size() > 0 || noMatches.size() > 0) {
			int nRow = ambiguousMatches.size() + noMatches.size();
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
			
			for (Object[] row : noMatches) {
				data[rowIndex] = row;
				rowIndex++;
			}
			
			DefaultTableModel newModel = new DefaultTableModel(data, header);
			tableSearchMultipleMatches.setModel(newModel);
		}
		
		SortResults results = new SortResults();
		results.setExactMatches(exactMatches);
		results.setGoodMatches(goodMatches);
		results.setAmbiguousMatches(ambiguousMatches);
		results.setNoMatches(noMatches);
		return results;
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
			actionSave.setEnabled(false);
			actionRevert.setEnabled(false);
			lblSaveOrRejectNote.setText("Updates permanently saved to database!");
			confirmImageLabel.setIcon(new ImageIcon(LoadPanel.class.getResource("/resources/step6.png")));
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
			actionSave.setEnabled(false);
			actionRevert.setEnabled(false);
			lblSaveOrRejectNote.setText("Updates rejected by user. Database returned to original state!");
			confirmImageLabel.setIcon(new ImageIcon(LoadPanel.class.getResource("/resources/step6.png")));
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
		cardLayout.show(contentPane, "DatabasePanel");
		checkBoxCredits.setSelected(false);
		
		cmbImportType.setSelectedIndex(0);
		selectedAdaptor = null;
		tableSpreadSheet.setModel(new DefaultTableModel());
		tableSearchExactMatches.setModel(new DefaultTableModel());
		tableSearchGoodMatches.setModel(new DefaultTableModel());
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
		controller.unLockToolBarOrganismSelect();
		
		actionSave.setEnabled(true);
		actionRevert.setEnabled(true);
		lblSaveOrRejectNote.setText("Save changes to database? *Warning, changes will be permanent if saved.");
		confirmImageLabel.setIcon(new ImageIcon(LoadPanel.class.getResource("/resources/step5.png")));
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
			putValue(NAME, "Save Transaction Log");
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
	
	private class SortResults {
		ArrayList<Object[]> exactMatches = new ArrayList<Object[]>();
		ArrayList<Object[]> goodMatches = new ArrayList<Object[]>();
		ArrayList<Object[]> ambiguousMatches = new ArrayList<Object[]>();
		ArrayList<Object[]> noMatches = new ArrayList<Object[]>();
		
		public SortResults() {
			exactMatches = new ArrayList<Object[]>();
			goodMatches = new ArrayList<Object[]>();
			ambiguousMatches = new ArrayList<Object[]>();
			noMatches = new ArrayList<Object[]>();
		}
		
		public ArrayList<Object[]> getExactMatches() {
			return exactMatches;
		}
		public ArrayList<Object[]> getGoodMatches() {
			return goodMatches;
		}
		public ArrayList<Object[]> getAmbiguousMatches() {
			return ambiguousMatches;
		}
		public ArrayList<Object[]> getNoMatches() {
			return noMatches;
		}
		public void setExactMatches(ArrayList<Object[]> exactMatches) {
			this.exactMatches = exactMatches;
		}
		public void setGoodMatches(ArrayList<Object[]> goodMatches) {
			this.goodMatches = goodMatches;
		}
		public void setAmbiguousMatches(ArrayList<Object[]> ambiguousMatches) {
			this.ambiguousMatches = ambiguousMatches;
		}
		public void setNoMatches(ArrayList<Object[]> noMatches) {
			this.noMatches = noMatches;
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
	
	class CustomRenderer extends DefaultTableCellRenderer {
	    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	    	setBackground(Color.LIGHT_GRAY);
	        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	        return this;
	    }
	}
}
