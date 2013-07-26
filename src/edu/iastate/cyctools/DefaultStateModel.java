package edu.iastate.cyctools;

import edu.iastate.cyctools.externalSourceCode.AbstractModel;

public class DefaultStateModel extends AbstractModel {
	private State state;
    
    public DefaultStateModel() {
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
    	NOVALUE, MAIN_SCREEN, NOT_CONNECTED, FRAMEBROWSE, EXPORT, SEARCH, STRUCTURE_EXPORT, DATABASE_COMPARE, LOAD, LOCK_DATABASE, UNLOCK_DATABASE;
    	
    	public static State value(String state) {
	        try {
	            return valueOf(state.toUpperCase());
	        } catch (Exception e) {
	            return NOVALUE;
	        }
	    }
    }
}
    
