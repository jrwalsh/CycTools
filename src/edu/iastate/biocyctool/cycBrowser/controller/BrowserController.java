package edu.iastate.biocyctool.cycBrowser.controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.table.DefaultTableModel;

import edu.iastate.biocyctool.cycBrowser.model.BrowserStateModel;
import edu.iastate.biocyctool.cycBrowser.model.BrowserStateModel.State;
import edu.iastate.biocyctool.util.da.CycDataBaseAccess;
import edu.iastate.biocyctool.util.view.AbstractViewPanel;
import edu.iastate.javacyco.PtoolsErrorException;

public class BrowserController implements PropertyChangeListener {
	private CycDataBaseAccess dataAccess;
	private BrowserStateModel state;
	private ArrayList<AbstractViewPanel> registeredViews;
	
	public static String BROWSER_STATE_PROPERTY = "State";
	
    public BrowserController(BrowserStateModel state) {
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
    		state.setState(State.CONNECTED);
    	} catch (Exception e) {
    		dataAccess = null;
    		state.setState(State.NOT_CONNECTED);
    	}
    }
    
    public void disconnect() {
    	dataAccess = null;
		state.setState(State.NOT_CONNECTED);
    }
    
    public void setState(State state) {
    	this.state.setState(state);
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
    
    public DefaultTableModel getSearchResultsTable(String text, String type) {
		try {
			return dataAccess.getSearchResultsTable(text, type);
		} catch (PtoolsErrorException e) {
			//TODO State failed search
			return new DefaultTableModel();
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