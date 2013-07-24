package edu.iastate.cyctools.view;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.util.Map;
import java.util.Map.Entry;

import edu.iastate.cyctools.CycDataBaseAccess.Item;
import edu.iastate.cyctools.DefaultController;
import edu.iastate.cyctools.DefaultStateModel.State;
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
import javax.swing.LayoutStyle.ComponentPlacement;

@SuppressWarnings({"serial", "rawtypes", "unchecked"})
public class ToolPanel extends AbstractViewPanel {
	public final static String noOrganism = "None Available";
	
	DefaultController controller;
	private JComboBox<Item> comboBoxOrganism;
	private JButton btnHome;
	private final Action actionSetOrganism = new ActionSetOrganism();
	private final Action action = new ActionHome();
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
    	setPreferredSize(new Dimension(800, 35));
		
		comboBoxOrganism = new JComboBox();
		// Cosmetic modification of the display of items in the combobox.  Change from "key=value" to "key: value"
		comboBoxOrganism.setRenderer(new DefaultListCellRenderer() {
	        @Override
	        public Component getListCellRendererComponent(final JList list, Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
	        	if (value != null) value = value.toString().replace("=", ": ");
	            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
	        }
	    });
		comboBoxOrganism.setAction(actionSetOrganism);
		
		btnHome = new JButton("Home");
		btnHome.setAction(action);
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(btnHome)
					.addGap(437)
					.addComponent(comboBoxOrganism, GroupLayout.PREFERRED_SIZE, 284, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(5)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnHome)
						.addComponent(comboBoxOrganism, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		setLayout(groupLayout);
	}
    
    @Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(DefaultController.BROWSER_STATE_PROPERTY) && evt.getNewValue() != null) {
			if (evt.getNewValue() == State.NOT_CONNECTED) {
				comboBoxOrganism.setModel(new KeyValueComboboxModel());
				this.setVisible(false);
			} else if (evt.getNewValue() == State.MAIN_SCREEN) {
				try {
					lastSelectedOrganism = (Map.Entry<String, String>)comboBoxOrganism.getSelectedItem();
				} catch (NullPointerException e) {
					lastSelectedOrganism = null;
				}
				
				KeyValueComboboxModel model = new KeyValueComboboxModel();
				for (OrgStruct org : controller.getAvailableOrganisms()) {
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
			} else {
				btnHome.setEnabled(true);
			}
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
			controller.showMainScreen();
		}
	}
}
