package edu.iastate.cyctools;

import javax.swing.JOptionPane;

public class CycToolsError {
	
	// Check for connection related errors which are thrown by JavaCycConnection objects
	public static void checkForConnectionError(Exception e) {
		if (e.getMessage().equalsIgnoreCase("Unknown host")) {
			CycToolsError.showError(e.getMessage() + "\nCould not determine host", "Connection error");
		} else if (e.getMessage().equalsIgnoreCase("Connection timed out")) {
			CycToolsError.showError(e.getMessage() + "\nServer not available", "Connection error");
		} else if (e.getMessage().equalsIgnoreCase("Read timed out")) {
			CycToolsError.showError(e.getMessage() + "\nServer found, but connection timed out. Possibly requires user login.", "Connection error");
		} else if (e.getMessage().equalsIgnoreCase("Problem connecting to remote socket")) {
			CycToolsError.showError(e.getMessage() + "\nJavaCycServer is not accessible", "Connection error");
		} else if (e.getMessage().equalsIgnoreCase("Problem logging in to remote server")) {
			CycToolsError.showError(e.getMessage() + "\nIncorrect username and password", "Login error");
		} else {
			throw new RuntimeException(e);
		}
	}
	
	public static void showError(String message, String title) {
		JOptionPane.showMessageDialog(DefaultController.mainJFrame, message, title, JOptionPane.ERROR_MESSAGE);
	}

	public static void showWarning(String message, String title) {
		JOptionPane.showMessageDialog(DefaultController.mainJFrame, message, title, JOptionPane.WARNING_MESSAGE);
	}
}
