package edu.iastate.cycspreadsheetloader.view;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import edu.iastate.cycspreadsheetloader.controller.DefaultController;

public class SubmitViewPanel {
	private DefaultController controller;
	
	private class ActionRevertDB extends AbstractAction {
		public ActionRevertDB() {
			putValue(NAME, "Revert DB");
			putValue(SHORT_DESCRIPTION, "Revert currently connected database, losing all unsaved changes.");
		}
		public void actionPerformed(ActionEvent e) {
			controller.revertDataBase();
		}
	}
}
