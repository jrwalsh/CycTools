package edu.iastate.biocyctool.cycBrowser.model;

import edu.iastate.biocyctool.cycBrowser.controller.BrowserController;
import edu.iastate.biocyctool.util.model.AbstractModel;

public class BrowserStateModel extends AbstractModel {
	private State state;
    
    public BrowserStateModel() {
    	initDefault();
    }
    
    public void initDefault() {
    	this.state = State.NOT_CONNECTED;
    }
    
    public void setState(State state) {
    	State oldState = this.state;
    	this.state = state;
    	firePropertyChange(BrowserController.BROWSER_STATE_PROPERTY, oldState, state);
    }
    
    public State getState() {
    	return this.state;
    }
    
    public enum State {
    	CONNECTED, NOT_CONNECTED, FRAMEBROWSE, EXPORT, SEARCH, STRUCTURE_EXPORT, NOVALUE;
    	
    	public static State value(String state) {
	        try {
	            return valueOf(state.toUpperCase());
	        } catch (Exception e) {
	            return NOVALUE;
	        }
	    }
    }
}
    
