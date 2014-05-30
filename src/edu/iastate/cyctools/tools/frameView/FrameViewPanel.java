package edu.iastate.cyctools.tools.frameView;

import javax.swing.border.EmptyBorder;
import javax.swing.DefaultListCellRenderer;
import javax.swing.GroupLayout;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTextArea;

import javax.swing.JScrollPane;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.Action;

import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Map.Entry;

import javax.swing.GroupLayout.Alignment;

import edu.iastate.cyctools.CycToolsError;
import edu.iastate.cyctools.DefaultController;
import edu.iastate.cyctools.externalSourceCode.AbstractViewPanel;
import edu.iastate.cyctools.externalSourceCode.KeyValueComboboxModel;
import edu.iastate.cyctools.externalSourceCode.MenuPopupUtil;
import edu.iastate.javacyco.*;

import java.awt.event.ActionListener;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JComboBox;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import net.miginfocom.swing.MigLayout;

@SuppressWarnings({"serial", "rawtypes", "unchecked"})
public class FrameViewPanel extends AbstractViewPanel {
	DefaultController controller;
	private final Action actionSubmit = new ActionSubmit();
	private JScrollPane scrollPane;
	private JTextArea textArea;
	private JTextField txtEnterFrameid;
	private JButton button;
	private JComboBox cmbType;
	
	/**
	 * Create the frame.
	 */
	public FrameViewPanel(DefaultController controller) {
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent arg0) {
				textArea.setText("");
			}
		});
		this.controller = controller;
		
		initComponents();
        localInitialization();
    }

    public void localInitialization() {
    	//Add self as property change event listener of the controller
    	controller.addView(this);
    	
    	
    	MenuPopupUtil.installContextMenu(txtEnterFrameid);
    	MenuPopupUtil.installContextMenu(textArea);
    	
    	KeyValueComboboxModel model = new KeyValueComboboxModel();
    	model.put(Compound.GFPtype, "Compounds");
    	model.put(Gene.GFPtype, "Genes");
    	model.put(Pathway.GFPtype, "Pathways");
    	model.put(Protein.GFPtype, "Proteins");
//    	model.put(Regulation.GFPtype, "Regulation"); // Not an indexed frame type, cannot be searched with substringsearch
    	model.put(Reaction.GFPtype, "Reactions");
    	cmbType.setModel(model);
    	cmbType.setRenderer(new DefaultListCellRenderer() {
	        @Override
	        public Component getListCellRendererComponent(final JList list, Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
	        	if (value != null) value = value.toString().substring(value.toString().indexOf("=")+1, value.toString().length());
	            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
	        }
	    });
    	cmbType.setSelectedIndex(0);
    }
    
    private void initComponents() {
    	setPreferredSize(new Dimension(800, 400));
		setName("SimpleBrowser");
		
		setBorder(new EmptyBorder(5, 5, 5, 5));
		
		txtEnterFrameid = new JTextField();
		txtEnterFrameid.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				actionSubmit.actionPerformed(event);
			}
		});
		txtEnterFrameid.setColumns(10);
		
		button = new JButton("Search");
		button.setAction(actionSubmit);
		
		textArea = new JTextArea();
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		textArea.setEditable(false);
		
		scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new Dimension(800, 300));
		scrollPane.setViewportView(textArea);
		
		cmbType = new JComboBox();
		setLayout(new MigLayout("", "[165px][6px][131px][6px][462px,grow]", "[23px][351px,grow]"));
		add(txtEnterFrameid, "cell 0 0,growx,aligny center");
		add(cmbType, "cell 2 0,growx,aligny center");
		add(button, "cell 4 0,alignx left,aligny top");
		add(scrollPane, "cell 0 1 5 1,grow");
	}

	private class ActionSubmit extends AbstractAction {
		public ActionSubmit() {
			putValue(NAME, "Submit");
			putValue(SHORT_DESCRIPTION, "Submits query from text field and returns results in the primary tab.");
		}
		
		public void actionPerformed(ActionEvent e) {
			textArea.setText("");
			
			String searchValue = txtEnterFrameid.getText();
			String selectedFrameType = ((Entry<String, String>)cmbType.getSelectedItem()).getKey();
			
			Frame exactMatch;
			try {
				exactMatch = controller.getConnection().frameExists(searchValue) ? Frame.load(controller.getConnection(), searchValue) : null;
			} catch (PtoolsErrorException e1) {
				exactMatch = null;
			}
			ArrayList<Frame> matchingFrames = controller.substringSearch(searchValue, selectedFrameType);
			
			String result = "";
			if (matchingFrames.size() > 0 || exactMatch != null) {
				SearchResultDialog dialog = SearchResultDialog.showResults(DefaultController.mainJFrame, exactMatch, matchingFrames);
				String selectedFrameID = dialog.getSelection();
				if (selectedFrameID != null) {
					result = controller.frameToString(selectedFrameID);
					textArea.setText(result);
				}
			} else {
				CycToolsError.showWarning("No search results found", "");
			}
		}
	}
	
	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		
	}
}
