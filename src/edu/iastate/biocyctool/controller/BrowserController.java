package edu.iastate.biocyctool.controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.UnknownHostException;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.table.DefaultTableModel;
import edu.iastate.biocyctool.model.ApplicationStateModel;
import edu.iastate.biocyctool.model.ApplicationStateModel.State;
import edu.iastate.biocyctool.util.da.CycDataBaseAccess;
import edu.iastate.biocyctool.util.view.AbstractViewPanel;
import edu.iastate.biocyctool.view.StatusPanel;
import edu.iastate.biocyctool.view.ToolPanel;
import edu.iastate.javacyco.Frame;
import edu.iastate.javacyco.JavacycConnection;
import edu.iastate.javacyco.PtoolsErrorException;

public class BrowserController implements PropertyChangeListener {
	private CycDataBaseAccess dataAccess;
	private ApplicationStateModel state;
	private ArrayList<AbstractViewPanel> registeredViews;
	public static JFrame mainJFrame;
	public ToolPanel toolPanel;
	public StatusPanel statusPanel;
	
	public static String BROWSER_STATE_PROPERTY = "State";
	
    public BrowserController(ApplicationStateModel state) {
    	dataAccess = null;
    	this.state = state;
    	registeredViews = new ArrayList<AbstractViewPanel>();
    }
    
    public void addView(AbstractViewPanel view) {
        registeredViews.add(view);
    }

    public void removeView(AbstractViewPanel view) {
        registeredViews.remove(view);
    }
    
    public void connect(String host, int port, String userName, String password) {
    	try {
    		dataAccess = new CycDataBaseAccess(host, port, userName, password);
    		state.setState(State.MAIN_SCREEN);
    	} catch (Exception e) {
    		dataAccess = null;
    		state.setState(State.NOT_CONNECTED);
    		
    		if (e.getMessage().equalsIgnoreCase("Unknown host")) {
    			JOptionPane.showMessageDialog(mainJFrame, e.getMessage() + "\nCould not determine host", "Connection error", JOptionPane.ERROR_MESSAGE);
    		} else if (e.getMessage().equalsIgnoreCase("Connection timed out")) {
    			JOptionPane.showMessageDialog(mainJFrame, e.getMessage() + "\nServer not available", "Connection error", JOptionPane.ERROR_MESSAGE);
    		} else if (e.getMessage().equalsIgnoreCase("Read timed out")) {
    			JOptionPane.showMessageDialog(mainJFrame, e.getMessage() + "\nServer found, but connection timed out. Possibly requires user login.", "Connection error", JOptionPane.ERROR_MESSAGE);
    		} else if (e.getMessage().equalsIgnoreCase("Problem connecting to remote socket")) {
    			JOptionPane.showMessageDialog(mainJFrame, e.getMessage() + "\nJavaCycServer is not accessible", "Connection error", JOptionPane.ERROR_MESSAGE);
    		} else if (e.getMessage().equalsIgnoreCase("Problem logging in to remote server")) {
    			JOptionPane.showMessageDialog(mainJFrame, e.getMessage() + "\nIncorrect username and password", "Login error", JOptionPane.ERROR_MESSAGE);
    		} else {
    			JOptionPane.showMessageDialog(mainJFrame, e.getMessage(), "Connection error", JOptionPane.ERROR_MESSAGE);
    		}
    	}
    }
    
    public void showMainScreen() {
    	state.setState(State.MAIN_SCREEN);
    }
    
    public void disconnect() {
    	dataAccess = null;
		state.setState(State.NOT_CONNECTED);
    }
    
    public JavacycConnection getConnection() {
    	return dataAccess.getConnection();
    }
    
    public void setState(State state) {
    	this.state.setState(state);
    }
    
    public void setMainJFrame(JFrame jframe) {
    	this.mainJFrame = jframe;
    }

    public void setToolPanel(ToolPanel toolPanel) {
		this.toolPanel = toolPanel;
	}
    
    public void setStatusPanel(StatusPanel statusPanel) {
		this.statusPanel = statusPanel;
	}
    
    public ArrayList<String> getAvailableOrganisms() {
    	return dataAccess.getAvailableOrganisms();
    }
    
    public void selectOrganism(String organism) {
    	dataAccess.selectOrganism(organism);
    }
    
    public String frameToString(String frameID) {
    	return dataAccess.frameToString(frameID);
    }
    
    public ArrayList<String> substringSearch(String text, String type) {
    	try {
			return dataAccess.substringSearch(text, type);
		} catch (PtoolsErrorException e) {
			return new ArrayList<String>();
		}
    }
    
    public ArrayList<Frame> getFramesOfType(String type) {
		try {
			return dataAccess.getFramesOfType(type);
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
			return new ArrayList<Frame>();
		}
	}
    
    public int printFramesToXML(String path, String type) {
		try {
			return dataAccess.printFramesToXML(path, type);
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
			return -1;
		}
	}
    
    public int printFramesToCSV(String path, String type) {
		try {
			return dataAccess.printFramesToCSV(path, type);
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
			return -1;
		}
	}
    
    public DefaultTableModel getSearchResultsTable(String text, String type) {
		try {
			return dataAccess.getSearchResultsTable(text, type);
		} catch (PtoolsErrorException e) {
			//TODO State failed search
			return new DefaultTableModel();
		}
	}
    
    public ArrayList<String> getPGDBChildrenOfFrame(String rootGFPtype) {
		try {
			return dataAccess.getPGDBChildrenOfFrame(rootGFPtype);
		} catch (PtoolsErrorException e) {
			//TODO State failed
			return new ArrayList<String>();
		}
	}
    
	public DefaultTableModel getPGDBStructure(String rootGFPtype, boolean includeInstances, boolean directionForward) {
		try {
			return dataAccess.getPGDBStructureTable(rootGFPtype, includeInstances, directionForward);
		} catch (PtoolsErrorException e) {
			//TODO State failed
			return new DefaultTableModel();
		}
	}
    
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		for (AbstractViewPanel view: registeredViews) {
            view.modelPropertyChange(evt);
        }
	}
}