package edu.iastate.cycspreadsheetloader.model;

import edu.iastate.cycspreadsheetloader.controller.DefaultController;
import edu.iastate.javacyco.JavacycConnection;

import java.beans.PropertyChangeEvent;

public class ConnectionModel extends AbstractModel {
	JavacycConnection conn;
    private String host;
    private int port;
    private String organism;
   
    public ConnectionModel() {
    }
    
    public void initDefault() {
    	setConnection(null);
    	setHost("");
        setPort(4444);
        setOrganism("");
    }
    
    //  Accessors
    public JavacycConnection getConnection() {
    	return conn;
    }
    
    public void setConnection(JavacycConnection conn) {
    	JavacycConnection oldConn = this.conn;
        this.conn = conn;
//        firePropertyChange(DefaultController.CONNECTION_CONNECTION_PROPERTY, oldConn, conn);
    }
    
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        String oldHost = this.host;
        this.host = host;
//        firePropertyChange(DefaultController.CONNECTION_HOST_PROPERTY, oldHost, host);
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        int oldPort = this.port;
        this.port = port;
//        firePropertyChange(DefaultController.CONNECTION_PORT_PROPERTY, oldPort, port);
    }

    public String getOrganism() {
        return organism;
    }

    public void setOrganism(String organism) {
        String oldOrganism = this.organism;
        this.organism = organism;
//        firePropertyChange(DefaultController.CONNECTION_ORGANISM_PROPERTY, oldOrganism, organism);
    }
}