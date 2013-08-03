package edu.iastate.cyctools;

import edu.iastate.cyctools.externalSourceCode.AbstractModel;

public class InternalStateModel extends AbstractModel {
	private State state;
    
    public InternalStateModel() {
    	initDefault();
    }
    
    public void initDefault() {
    	this.state = State.NOT_CONNECTED;
    }
    
    public void setState(State state) {
    	State oldState = this.state;
    	this.state = state;
    	firePropertyChange(DefaultController.BROWSER_STATE_PROPERTY, oldState, state);
    }
    
    public State getState() {
    	return this.state;
    }
    
    public enum State {
    	NOVALUE, NOT_CONNECTED, SHOW_MAIN_SCREEN, SHOW_FRAMEBROWSE, SHOW_EXPORT, SHOW_SEARCH, SHOW_STRUCTURE_EXPORT, SHOW_DATABASE_COMPARE, SHOW_IMPORT;
    	
    	public static State value(String state) {
	        try {
	            return valueOf(state.toUpperCase());
	        } catch (Exception e) {
	            return NOVALUE;
	        }
	    }
    }
}
    
