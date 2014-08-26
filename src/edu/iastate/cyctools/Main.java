package edu.iastate.cyctools;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.iastate.cyctools.tools.frameView.FrameViewPanel;
import edu.iastate.cyctools.tools.load.view.LoadPanel;
import edu.iastate.cyctools.view.LoginPanel;
import edu.iastate.cyctools.view.MainCardPanel;
import edu.iastate.cyctools.view.MenuBar;
import edu.iastate.cyctools.view.SelectPanel;
import edu.iastate.cyctools.view.StatusPanel;
import edu.iastate.cyctools.view.ToolPanel;

import java.awt.Dimension;
import net.miginfocom.swing.MigLayout;

public class Main {
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		// DataAccess object initialized through the loginPanel
		
		// Model
		InternalStateModel state = new InternalStateModel();
		state.initDefault();
		
		// Controller
		DefaultController controller = new DefaultController(state);
		state.addPropertyChangeListener(controller);
		
		// Views
		ToolPanel toolPanel = new ToolPanel(controller);
		StatusPanel statusPanel = new StatusPanel(controller);
		
		MainCardPanel cardPanel = new MainCardPanel(controller);
		cardPanel.add(new LoginPanel(controller), MainCardPanel.loginCard);
		cardPanel.add(new SelectPanel(controller), MainCardPanel.selectCard);
		cardPanel.add(new FrameViewPanel(controller), MainCardPanel.frameBrowseCard);
		cardPanel.add(new LoadPanel(controller), MainCardPanel.loadCard);
		
		JFrame displayFrame = new JFrame("CycTools");
		displayFrame.setMinimumSize(new Dimension(1000, 600));
		controller.setMainJFrame(displayFrame);
		displayFrame.setJMenuBar(new MenuBar(controller));
		displayFrame.getContentPane().setLayout(new MigLayout("", "[grow]", "[1px][grow]"));
		displayFrame.getContentPane().add(toolPanel, "north");
		displayFrame.getContentPane().add(cardPanel, "cell 0 1,growx,growy,aligny top");
		displayFrame.getContentPane().add(statusPanel, "south");
		
        displayFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        displayFrame.pack();
        
        displayFrame.setLocationRelativeTo(null);
        displayFrame.setVisible(true);
	}
}
