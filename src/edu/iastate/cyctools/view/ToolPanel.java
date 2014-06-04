package edu.iastate.cyctools.view;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.util.Map;
import java.util.Map.Entry;

import edu.iastate.cyctools.DefaultController;
import edu.iastate.cyctools.InternalStateModel.State;
import edu.iastate.cyctools.externalSourceCode.AbstractViewPanel;
import edu.iastate.cyctools.externalSourceCode.KeyValueComboboxModel;
import edu.iastate.javacyco.OrgStruct;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComboBox;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.DefaultListCellRenderer;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JList;
import net.miginfocom.swing.MigLayout;

@SuppressWarnings({"serial", "rawtypes", "unchecked"})
public class ToolPanel extends AbstractViewPanel {
	public final static String noOrganism = "None Available";
	
	DefaultController controller;
	private JComboBox comboBoxOrganism;
	private JButton btnHome;
	public final Action actionSetOrganism = new ActionSetOrganism();
	public final Action action = new ActionHome();
	private Entry<String, String> lastSelectedOrganism;
	
	/**
	 * Create the frame.
	 * @param controller 
	 */
	public ToolPanel(DefaultController controller) {
		this.controller = controller;
		
		initComponents();
        localInitialization();
    }

    public void localInitialization() {
    	lastSelectedOrganism = null;
    	
    	//Add self as property change event listener of the controller
    	controller.addView(this);
    	controller.setToolPanel(this);
    	
		this.setVisible(false);
    }
    
    private void initComponents() {
		
		comboBoxOrganism = new JComboBox();
		// Cosmetic modification of the display of items in the combobox.  Change from "key=value" to "key: value"
		comboBoxOrganism.setRenderer(new DefaultListCellRenderer() {
	        @Override
	        public Component getListCellRendererComponent(final JList list, Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
	        	if (value != null) {
	        		value = value.toString().replace("=", ": ");
//	        		if (value.toString().contains(":") && controller.isKBModified(value.toString().substring(0, value.toString().indexOf(":")))) { //TODO so many errors from this.... why why why?
//	        			value = "*" + value.toString();
//	        		}
	        	}
	            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
	        }
	    });
		comboBoxOrganism.setAction(actionSetOrganism);
		setLayout(new MigLayout("", "[59px][grow]", "[23px]"));
		
		btnHome = new JButton("Home");
		btnHome.setAction(action);
		add(btnHome, "cell 0 0,alignx left,aligny center");
		add(comboBoxOrganism, "cell 1 0,alignx right,aligny center");
	}
    
    public void lockToolBar() {
    	action.setEnabled(false);
		actionSetOrganism.setEnabled(false);
	}
	
	public void unLockToolBar() {
		action.setEnabled(true);
		actionSetOrganism.setEnabled(true);
	}
	
	public void lockToolBarOrganismSelect() {
		actionSetOrganism.setEnabled(false);
	}
    
	public void unLockToolBarOrganismSelect() {
		actionSetOrganism.setEnabled(true);
	}
    
    @Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(DefaultController.BROWSER_STATE_PROPERTY) && evt.getNewValue() != null) {
			if (evt.getNewValue() == State.NOT_CONNECTED) {
				comboBoxOrganism.setModel(new KeyValueComboboxModel());
				this.setVisible(false);
			} else if (evt.getNewValue() == State.SHOW_MAIN_SCREEN) {
				refreshOrganismList();
			} else {
				btnHome.setEnabled(true);
			}
			
//			if (evt.getNewValue() == State.LOCK) {
//				action.setEnabled(false);
//				actionSetOrganism.setEnabled(false);
//			}
//			
//			if (evt.getNewValue() == State.UNLOCK) {
//				action.setEnabled(true);
//				actionSetOrganism.setEnabled(true);
//			}
//			
//			if (evt.getNewValue() == State.LOCK_DATABASE) {
//				actionSetOrganism.setEnabled(false);
//			}
//			
//			if (evt.getNewValue() == State.UNLOCK_DATABASE) {
//				actionSetOrganism.setEnabled(true);
//			} 
		}
	}
    
	private class ActionSetOrganism extends AbstractAction {
		public ActionSetOrganism() {
			putValue(NAME, "Set Organism");
			putValue(SHORT_DESCRIPTION, "Changes the organism used for all queries in the BioCyc database.");
		}
		public void actionPerformed(ActionEvent e) {
			if (comboBoxOrganism.getItemCount() > 0 && !comboBoxOrganism.getSelectedItem().toString().equalsIgnoreCase("")){
				controller.selectOrganism(((Map.Entry<String, String>)comboBoxOrganism.getSelectedItem()).getKey());
			}
		}
	}

	private class ActionHome extends AbstractAction {
		public ActionHome() {
			putValue(NAME, "Home");
			putValue(SHORT_DESCRIPTION, "Return to select menu");
		}
		public void actionPerformed(ActionEvent e) {
			controller.unlockDatabaseOperation();
			controller.showMainScreen();
		}
	}

	public void refreshOrganismList() {
		try {
			lastSelectedOrganism = (Map.Entry<String, String>)comboBoxOrganism.getSelectedItem();
		} catch (NullPointerException e) {
			lastSelectedOrganism = null;
		}
		
		KeyValueComboboxModel model = new KeyValueComboboxModel();
		for (OrgStruct org : controller.getAvailableOrganisms()) {
//			if (controller.isKBModified(org.getLocalID())) {
//				model.put(org.getLocalID(), "*" + org.getSpecies());
//			} else model.put(org.getLocalID(), org.getSpecies());
			model.put(org.getLocalID(), org.getSpecies());
		}
		comboBoxOrganism.setModel(model);
		if (comboBoxOrganism.getItemCount() > 0) comboBoxOrganism.setSelectedIndex(0);
		
		try {
			if (lastSelectedOrganism != null)
				comboBoxOrganism.setSelectedItem(lastSelectedOrganism);
		} catch (Exception e) {
			//ignore
		}
		
		btnHome.setEnabled(false);
		this.setVisible(true);
	}
}
