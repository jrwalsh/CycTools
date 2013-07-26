package edu.iastate.cyctools;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import edu.iastate.cyctools.CycDataBaseAccess.Item;
import edu.iastate.cyctools.DefaultStateModel.State;
import edu.iastate.cyctools.externalSourceCode.AbstractViewPanel;
import edu.iastate.cyctools.tools.load.fileAdaptors.FileAdaptor;
import edu.iastate.cyctools.tools.load.model.AbstractFrameEdit;
import edu.iastate.cyctools.tools.load.model.DocumentModel;
import edu.iastate.cyctools.view.StatusPanel;
import edu.iastate.cyctools.view.ToolPanel;
import edu.iastate.javacyco.Frame;
import edu.iastate.javacyco.JavacycConnection;
import edu.iastate.javacyco.OrgStruct;
import edu.iastate.javacyco.Organism;
import edu.iastate.javacyco.PtoolsErrorException;

public class DefaultController implements PropertyChangeListener {
	private CycDataBaseAccess dataAccess;
	private DefaultStateModel state;
	private ArrayList<AbstractViewPanel> registeredViews;
	public static JFrame mainJFrame;
	public ToolPanel toolPanel;
	public StatusPanel statusPanel;
	private DocumentModel documentModel;
	private WindowAdapter winAdaptor;
	
	public static String DOCUMENT_FILEPATH_PROPERTY = "FilePath";
	public static String DOCUMENT_TABLEMODEL_PROPERTY = "TableModel";
	public static String BROWSER_STATE_PROPERTY = "State";
	public static String REPORT_PROPERTY_FRAME_EDITS_PROCESSED = "FrameEditsProcessed";
	public static String REPORT_PROPERTY_FRAME_EDITS_SUCCESS = "FrameEditsSuccess";
	public static String REPORT_PROPERTY_FRAME_EDITS_FAIL = "FrameEditsFail";
	
    public DefaultController(DefaultStateModel state) {
    	this.dataAccess = null;
    	this.documentModel = null;
    	this.state = state;
    	this.registeredViews = new ArrayList<AbstractViewPanel>();
    }
    
    // Getters and Setters
    public void addView(AbstractViewPanel view) {
    	this.registeredViews.add(view);
    }

    public void removeView(AbstractViewPanel view) {
    	this.registeredViews.remove(view);
    }
    
    public void setDocumentModel(DocumentModel documentModel) {
    	this.documentModel = documentModel;
    }
    
    public void changeDocumentFile(File newFile, String delimiter) {
    	this.documentModel.setFile(newFile, delimiter);
    }
    
    public DocumentModel getDocumentModel() {
    	return this.documentModel;
    }
    
    public void setMainJFrame(JFrame jframe) {
    	DefaultController.mainJFrame = jframe;
    }

    public void setToolPanel(ToolPanel toolPanel) {
		this.toolPanel = toolPanel;
	}
    
    public void setStatusPanel(StatusPanel statusPanel) {
		this.statusPanel = statusPanel;
	}
    
    public JavacycConnection getConnection() {
    	return dataAccess.getConnection();
    }

    
    // Actions
    public void showMainScreen() {
    	state.setState(State.MAIN_SCREEN);
    }
    
    public void setState(State state) {
    	this.state.setState(state);
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
    
    public void lockDatabaseOperation() {
    	state.setState(State.LOCK_DATABASE);
    	mainJFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    	winAdaptor = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if(JOptionPane.showConfirmDialog(mainJFrame, "Are you sure ?") == JOptionPane.OK_OPTION){
                	mainJFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                	mainJFrame.setVisible(false);
                	mainJFrame.dispose();
                }
            }
        };
    	mainJFrame.addWindowListener(winAdaptor);
    }
    
    public void unlockDatabaseOperation() {
    	state.setState(State.UNLOCK_DATABASE);
    	mainJFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    	mainJFrame.removeWindowListener(winAdaptor);
    }
    
    public void disconnect() {
    	dataAccess = null;
		state.setState(State.NOT_CONNECTED);
    }
    
    public void selectOrganism(String organism) {
    	dataAccess.selectOrganism(organism);
    }
    
    
    // Queries
    public ArrayList<OrgStruct> getAvailableOrganisms() {
    	return dataAccess.getAvailableOrganisms();
    }
    
    public String frameToString(String frameID) {
    	return dataAccess.frameToString(frameID);
    }
    
    public String frameToString(Frame frame) {
    	return dataAccess.frameToString(frame);
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
	
	public void submitTable(FileAdaptor interpreter) {
    	ArrayList<AbstractFrameEdit> frameUpdates = interpreter.tableToFrameUpdates(documentModel.getTableModel());
		try {
			dataAccess.commitFrameUpdates(frameUpdates);
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		}
	}
	
	public Frame updateLocalFrame(String frameID, ArrayList<AbstractFrameEdit> frameUpdates) {
		try {
			return dataAccess.updateLocalFrame(frameID, frameUpdates);
		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		}
		return null;
	}
    
	public void revertDataBase() {
		dataAccess.revertDataBase();
	}
	
	public void saveDataBase() {
		dataAccess.saveDataBase();
	}
    
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		for (AbstractViewPanel view: registeredViews) {
            view.modelPropertyChange(evt);
        }
	}

	public String getSelectedOrganism() {
		return dataAccess.getSelectedOrganism();
	}

	public String getOrganismCommonName(String organism) {
		JavacycConnection conn = dataAccess.getConnection();
		Organism org;
		String orgCommonName = "";
		try {
			org = conn.getOrganism();
			orgCommonName = org.getCommonName();
		} catch (PtoolsErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return orgCommonName;
	}
}