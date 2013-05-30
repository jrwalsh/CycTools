package edu.iastate.cyctools.view;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;

import edu.iastate.cyctools.DefaultController;
import edu.iastate.cyctools.DefaultStateModel.State;
import edu.iastate.cyctools.externalSourceCode.AbstractViewPanel;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComboBox;
import java.awt.Dimension;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.LayoutStyle.ComponentPlacement;

@SuppressWarnings({"serial", "rawtypes", "unchecked"})
public class ToolPanel extends AbstractViewPanel {
	public final static String noOrganism = "None Available";
	
	DefaultController controller;
	private JComboBox comboBoxOrganism;
	private JButton btnBack;
	private JButton btnForward;
	private JButton btnHome;
	private final Action actionSetOrganism = new ActionSetOrganism();
	private final Action action = new ActionHome();
	
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
    	//Add self as property change event listener of the controller
    	controller.addView(this);
    	controller.setToolPanel(this);
    	
    	comboBoxOrganism.addItem("None Available");
		comboBoxOrganism.setSelectedItem("None Available");
		
		this.setVisible(false);
    }
    
    private void initComponents() {
    	setPreferredSize(new Dimension(800, 35));
		
		comboBoxOrganism = new JComboBox();
		comboBoxOrganism.setAction(actionSetOrganism);
		
		btnBack = new JButton("Back");
		btnBack.setVisible(false);
		
		btnForward = new JButton("Forward");
		btnForward.setVisible(false);
		
		btnHome = new JButton("Home");
		btnHome.setAction(action);
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(btnHome)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnBack)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnForward)
					.addGap(550)
					.addComponent(comboBoxOrganism, GroupLayout.PREFERRED_SIZE, 132, GroupLayout.PREFERRED_SIZE)
					.addGap(41))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(5)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(comboBoxOrganism, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnHome)
						.addComponent(btnBack)
						.addComponent(btnForward))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		setLayout(groupLayout);
	}
    
	private class ActionSetOrganism extends AbstractAction {
		public ActionSetOrganism() {
			putValue(NAME, "Set Organism");
			putValue(SHORT_DESCRIPTION, "Changes the organism used for all queries in the BioCyc database.");
		}
		public void actionPerformed(ActionEvent e) {
			if (comboBoxOrganism.getItemCount() > 0 && !comboBoxOrganism.getSelectedItem().toString().equalsIgnoreCase(noOrganism)){
				controller.selectOrganism(comboBoxOrganism.getSelectedItem().toString());
			}
		}
	}


	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(DefaultController.BROWSER_STATE_PROPERTY) && evt.getNewValue() != null) {
			if (evt.getNewValue() == State.NOT_CONNECTED) {
				if (comboBoxOrganism.getItemCount() > 0) {
					comboBoxOrganism.removeAllItems();
				}
//				comboBoxOrganism.addItem(noOrganism);
//				comboBoxOrganism.setSelectedItem(noOrganism);
//				comboBoxOrganism.setEnabled(false);
				this.setVisible(false);
			} else if (evt.getNewValue() == State.MAIN_SCREEN) {
				if (comboBoxOrganism.getItemCount() > 0) {
					comboBoxOrganism.removeAllItems();
				}
				for (String org : controller.getAvailableOrganisms()) {
					comboBoxOrganism.addItem(org);
				}
//				comboBoxOrganism.setEnabled(true);
				btnBack.setEnabled(false);
				btnForward.setEnabled(false);
				btnHome.setEnabled(false);
				this.setVisible(true);
			} else {
				btnHome.setEnabled(true);
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
