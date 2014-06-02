package edu.iastate.cyctools;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import edu.iastate.cyctools.InternalStateModel.State;
import edu.iastate.cyctools.externalSourceCode.AbstractViewPanel;
import edu.iastate.cyctools.tools.load.fileAdaptors.AbstractFileAdaptor;
import edu.iastate.cyctools.tools.load.model.AbstractFrameEdit;
import edu.iastate.cyctools.tools.load.model.DocumentModel;
import edu.iastate.cyctools.view.MenuBar;
import edu.iastate.cyctools.view.StatusPanel;
import edu.iastate.cyctools.view.ToolPanel;
import edu.iastate.javacyco.Frame;
import edu.iastate.javacyco.JavacycConnection;
import edu.iastate.javacyco.OrgStruct;

public class DefaultController implements PropertyChangeListener {
	private CycDataBaseAccess dataAccess;
	private InternalStateModel state;
	private ArrayList<AbstractViewPanel> registeredViews;
	public static JFrame mainJFrame;
	public ToolPanel toolPanel;
	public StatusPanel statusPanel;
	public MenuBar menuPanel;
	private DocumentModel documentModel;
	private WindowAdapter winAdaptor;
	
	public static String DOCUMENT_FILEPATH_PROPERTY = "FilePath";
	public static String DOCUMENT_TABLEMODEL_PROPERTY = "TableModel";
	public static String BROWSER_STATE_PROPERTY = "State";
	public static String REPORT_PROPERTY_FRAME_EDITS_PROCESSED = "FrameEditsProcessed";
	public static String REPORT_PROPERTY_FRAME_EDITS_SUCCESS = "FrameEditsSuccess";
	public static String REPORT_PROPERTY_FRAME_EDITS_FAIL = "FrameEditsFail";
	
    public DefaultController(InternalStateModel state) {
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
    
    public void setMenuPanel(MenuBar menuPanel) {
		this.menuPanel = menuPanel;
	}
    
    public JavacycConnection getConnection() {
    	return dataAccess.getConnection();
    }

    
    // Actions
    public void showMainScreen() {
    	state.setState(State.SHOW_MAIN_SCREEN);
    }
    
    public void setState(State state) {
    	// Changing program state propagates this change to all DefaultController listeners.  Listeners are responsible for complying with the current state of the program.
    	this.state.setState(state);
    }
    
    public void connect(String host, int port, String userName, String password) {
    	try {
    		dataAccess = new CycDataBaseAccess(host, port, userName, password);
    		if (dataAccess.testConnection()) state.setState(State.SHOW_MAIN_SCREEN);
    		else {
    			dataAccess = null;
        		state.setState(State.NOT_CONNECTED);
        		CycToolsError.showError("Unable to connect to server", "Unable to connect");
    		}
    	} catch (Exception e) {
    		dataAccess = null;
    		state.setState(State.NOT_CONNECTED);
    		CycToolsError.checkForConnectionError(e);
    	}
    }

    public void lockDatabaseOperation() {
    	toolPanel.lockToolBar();
    	mainJFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    	winAdaptor = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if(JOptionPane.showConfirmDialog(mainJFrame, "The changes to the databae have not been saved yet!  Do you want to quit without saving changes?") == JOptionPane.OK_OPTION){
                	mainJFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                	mainJFrame.setVisible(false);
                	mainJFrame.dispose();
                }
            }
        };
    	mainJFrame.addWindowListener(winAdaptor);
    }
    
    public void unlockDatabaseOperation() {
    	toolPanel.unLockToolBar();
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
    
    public ArrayList<Frame> substringSearch(String text, String type) {
		return dataAccess.substringSearch(text, type);
    }
    
    public ArrayList<Frame> getFramesOfType(String type) {
		return dataAccess.getFramesOfType(type);
	}
    
    public int printFramesToXML(String path, String type) {
		return dataAccess.printFramesToXML(path, type);
	}
    
    public int printFramesToCSV(String path, String type) {
		return dataAccess.printFramesToCSV(path, type);
	}
    
    public DefaultTableModel getSearchResultsTable(String text, String type) {
		return dataAccess.getSearchResultsTable(text, type);
	}
    
    public ArrayList<String> getPGDBChildrenOfFrame(String rootGFPtype) {
		return dataAccess.getPGDBChildrenOfFrame(rootGFPtype);
	}
    
	public DefaultTableModel getPGDBStructure(String rootGFPtype, boolean includeInstances, boolean directionForward) {
		return dataAccess.getPGDBStructureTable(rootGFPtype, includeInstances, directionForward);
	}
	
	public void submitTable(AbstractFileAdaptor interpreter) {
    	ArrayList<AbstractFrameEdit> frameUpdates = interpreter.tableToFrameUpdates(documentModel.getTableModel());
		dataAccess.commitFrameUpdates(frameUpdates);
	}
	
	public Frame updateLocalFrame(String frameID, ArrayList<AbstractFrameEdit> frameUpdates) {
		return dataAccess.updateLocalFrame(frameID, frameUpdates);
	}
    
	public void revertDataBase() {
		dataAccess.revertDataBase();
	}
	
	public void saveDataBase() {
		dataAccess.saveDataBase();
	}
	
	public String getSelectedOrganism() {
		return dataAccess.getSelectedOrganism();
	}

	public String getOrganismCommonName(String organismID) {
		return dataAccess.getOrganismCommonName(organismID);
	}
	
	public void lockToolBarOrganismSelect() {
		toolPanel.lockToolBarOrganismSelect();
	}
    
	public void unLockToolBarOrganismSelect() {
		toolPanel.unLockToolBarOrganismSelect();
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		for (AbstractViewPanel view: registeredViews) {
            view.modelPropertyChange(evt);
        }
	}

	public boolean isKBModified(String kb) {
		return dataAccess.isCurrentKBModified(kb);
	}

	public void setDisconnectActionEnabled(boolean enabled) {
		menuPanel.setDisconnectActionEnabled(enabled);
	}
}