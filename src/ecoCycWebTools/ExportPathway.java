package ecoCycWebTools;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;

import edu.iastate.javacyco.*;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.LayoutStyle;
import javax.swing.ListModel;
import javax.swing.SwingConstants;

import javax.swing.WindowConstants;
import javax.swing.SwingUtilities;


/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class ExportPathway extends javax.swing.JFrame {
	//TODO My Objects
	private HashMap<String,String> PathwayMap = new HashMap<String,String>();
	//
	
	private JButton buttonCancel;
	private JButton buttonRun;
	private JScrollPane jScrollPaneRun;
	private JList listPathways;
	private JButton buttonLeft;
	private JButton buttonAllRight;
	private JButton buttonRight;
	private AbstractAction actionCloseProgram;
	private AbstractAction actionCloseAbout;
	private JButton buttonOK;
	private JLabel labelAbout;
	private JDialog dialogAbout;
	private AbstractAction actionAbout;
	private JMenuItem jMenuItem1;
	private JMenu menuHelp;
	private JMenu menuFile;
	private JMenuBar menuBar;
	private JButton buttonAllLeft;
	private JPanel panelButtons;
	private JScrollPane jScrollPanePathways;
	private JList listRun;

	/**
	* Auto-generated main method to display this JFrame
	*/
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				ExportPathway inst = new ExportPathway();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}
	
	public ExportPathway() {
		super();
		initGUI();
		
		//TODO Initialize Values
		initializeObjectValues();
	}
	
	private void initGUI() {
		try {
			GroupLayout thisLayout = new GroupLayout((JComponent)getContentPane());
			getContentPane().setLayout(thisLayout);
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			this.setTitle("Export Pathways");
			{
				menuBar = new JMenuBar();
				setJMenuBar(menuBar);
				{
					menuFile = new JMenu();
					menuBar.add(menuFile);
					menuFile.setText("File");
				}
				{
					menuHelp = new JMenu();
					menuBar.add(menuHelp);
					menuHelp.setText("Help");
					{
						jMenuItem1 = new JMenuItem();
						menuHelp.add(jMenuItem1);
						jMenuItem1.setText("jMenuItem1");
						jMenuItem1.setAction(getActionAbout());
					}
				}
			}
			{
				buttonCancel = new JButton();
				buttonCancel.setText("Cancel");
				buttonCancel.setAction(getActionCloseProgram());
			}
			{
				buttonRun = new JButton();
				buttonRun.setText("Run");
			}
			{
				jScrollPaneRun = new JScrollPane();
				{
					ListModel listRunModel = 
						new DefaultComboBoxModel(
								new String[] { "Item One", "Item Two" });
					listRun = new JList();
					jScrollPaneRun.setViewportView(listRun);
					listRun.setModel(listRunModel);
					listRun.setPreferredSize(new java.awt.Dimension(308, 208));
				}
			}
			{
				jScrollPanePathways = new JScrollPane();
				{
					ListModel listPathwaysModel = 
						new DefaultComboBoxModel(
								new String[] { "Item One", "Item Two" });
					listPathways = new JList();
					jScrollPanePathways.setViewportView(listPathways);
					listPathways.setModel(listPathwaysModel);
				}
			}
			{
				panelButtons = new JPanel();
				GroupLayout panelButtonsLayout = new GroupLayout((JComponent)panelButtons);
				panelButtons.setLayout(panelButtonsLayout);
				{
					buttonAllLeft = new JButton();
					buttonAllLeft.setText("<<");
				}
				{
					buttonLeft = new JButton();
					buttonLeft.setText("<");
				}
				{
					buttonRight = new JButton();
					buttonRight.setText(">");
				}
				{
					buttonAllRight = new JButton();
					buttonAllRight.setText(">>");
				}
					panelButtonsLayout.setHorizontalGroup(panelButtonsLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(panelButtonsLayout.createParallelGroup()
					    .addGroup(GroupLayout.Alignment.LEADING, panelButtonsLayout.createSequentialGroup()
					        .addComponent(buttonAllLeft, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
					    .addGroup(panelButtonsLayout.createSequentialGroup()
					        .addComponent(buttonLeft, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE))
					    .addGroup(panelButtonsLayout.createSequentialGroup()
					        .addComponent(buttonRight, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE))
					    .addGroup(panelButtonsLayout.createSequentialGroup()
					        .addComponent(buttonAllRight, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(34, Short.MAX_VALUE));
					panelButtonsLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {buttonAllRight, buttonRight, buttonLeft, buttonAllLeft});
					panelButtonsLayout.setVerticalGroup(panelButtonsLayout.createSequentialGroup()
					.addContainerGap(57, 57)
					.addComponent(buttonAllLeft, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
					.addComponent(buttonLeft, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
					.addComponent(buttonRight, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
					.addComponent(buttonAllRight, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(43, Short.MAX_VALUE));
			}
				thisLayout.setVerticalGroup(thisLayout.createSequentialGroup()
					.addGroup(thisLayout.createParallelGroup()
					    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
					        .addComponent(panelButtons, GroupLayout.PREFERRED_SIZE, 217, GroupLayout.PREFERRED_SIZE)
					        .addGap(14))
					    .addGroup(thisLayout.createSequentialGroup()
					        .addGap(12)
					        .addGroup(thisLayout.createParallelGroup()
					            .addComponent(jScrollPaneRun, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 217, GroupLayout.PREFERRED_SIZE)
					            .addComponent(jScrollPanePathways, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 219, GroupLayout.PREFERRED_SIZE))))
					.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
					.addGroup(thisLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					    .addComponent(buttonCancel, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					    .addComponent(buttonRun, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(37, 37));
				thisLayout.setHorizontalGroup(thisLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(jScrollPaneRun, GroupLayout.PREFERRED_SIZE, 312, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
					.addComponent(panelButtons, GroupLayout.PREFERRED_SIZE, 76, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
					.addGroup(thisLayout.createParallelGroup()
					    .addGroup(thisLayout.createSequentialGroup()
					        .addComponent(jScrollPanePathways, GroupLayout.PREFERRED_SIZE, 312, GroupLayout.PREFERRED_SIZE)
					        .addGap(0, 0, Short.MAX_VALUE))
					    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
					        .addGap(0, 129, Short.MAX_VALUE)
					        .addComponent(buttonRun, GroupLayout.PREFERRED_SIZE, 86, GroupLayout.PREFERRED_SIZE)
					        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, 1, GroupLayout.PREFERRED_SIZE)
					        .addComponent(buttonCancel, GroupLayout.PREFERRED_SIZE, 86, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap());
				thisLayout.linkSize(SwingConstants.VERTICAL, new Component[] {panelButtons, jScrollPaneRun});
				thisLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {jScrollPaneRun, jScrollPanePathways});
				thisLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {buttonCancel, buttonRun});
			pack();
			this.setSize(754, 330);
		} catch (Exception e) {
		    //add your error handling code here
			e.printStackTrace();
		}
	}
	
	private AbstractAction getActionAbout() {
		if(actionAbout == null) {
			actionAbout = new AbstractAction("About", null) {
				public void actionPerformed(ActionEvent evt) {
					getDialogAbout().pack();
					getDialogAbout().setLocationRelativeTo(null);
					getDialogAbout().setVisible(true);
				}
			};
		}
		return actionAbout;
	}
	
	private JDialog getDialogAbout() {
		if(dialogAbout == null) {
			dialogAbout = new JDialog(this);
			GroupLayout dialogAboutLayout = new GroupLayout((JComponent)dialogAbout.getContentPane());
			dialogAbout.setLayout(dialogAboutLayout);
			dialogAbout.setTitle("About");
			dialogAbout.setResizable(false);
			dialogAbout.setSize(346, 195);
			dialogAboutLayout.setHorizontalGroup(dialogAboutLayout.createSequentialGroup()
				.addContainerGap()
				.addGroup(dialogAboutLayout.createParallelGroup()
				    .addComponent(getLabelAbout(), GroupLayout.Alignment.LEADING, 0, 316, Short.MAX_VALUE)
				    .addGroup(GroupLayout.Alignment.LEADING, dialogAboutLayout.createSequentialGroup()
				        .addGap(0, 252, Short.MAX_VALUE)
				        .addComponent(getButtonOK(), GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE)))
				.addContainerGap());
			dialogAboutLayout.setVerticalGroup(dialogAboutLayout.createSequentialGroup()
				.addContainerGap()
				.addComponent(getLabelAbout(), GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
				.addComponent(getButtonOK(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		}
		return dialogAbout;
	}
	
	private JLabel getLabelAbout() {
		if(labelAbout == null) {
			labelAbout = new JLabel();
			labelAbout.setText("This is a trial of the Jigloo GUI editor");
		}
		return labelAbout;
	}
	
	private JButton getButtonOK() {
		if(buttonOK == null) {
			buttonOK = new JButton();
			buttonOK.setText("OK");
			buttonOK.setAction(getActionCloseAbout());
		}
		return buttonOK;
	}
	
	private AbstractAction getActionCloseAbout() {
		if(actionCloseAbout == null) {
			actionCloseAbout = new AbstractAction("OK", null) {
				public void actionPerformed(ActionEvent evt) {
					getDialogAbout().dispose();
				}
			};
		}
		return actionCloseAbout;
	}
	
	private AbstractAction getActionCloseProgram() {
		if(actionCloseProgram == null) {
			actionCloseProgram = new AbstractAction("Close", null) {
				public void actionPerformed(ActionEvent evt) {
					dispose();
				}
			};
		}
		return actionCloseProgram;
	}

	//TODO Custom Code for program functionality
	// Custom Functions //
	
	private void initializeObjectValues() {
		listPathways.setListData(getAllPathways());
		listRun.setListData(new Object[0]);
	}
	
 	private Object[] getAllPathways() {
		// Get information from EcoCyc
		JavacycConnection conn = null;
		Object[] pathwayNames = null;
		try {
			conn = new JavacycConnection("jrwalsh.student.iastate.edu",4444);
			conn.selectOrganism("ECOLI");
		
			ArrayList<Pathway> allPwys = null;
			try {
				allPwys = Pathway.all(conn);
			} catch (PtoolsErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			pathwayNames = new Object[allPwys.size()]; 
			int i = 0;
			for (Pathway pwy : allPwys) {
				PathwayMap.put(pwy.getCommonName(), pwy.getLocalID());
				pathwayNames[i] = pwy.getCommonName();
				i++;
			}
		} catch(Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Caught a "+e.getClass().getName()+". Shutting down...");
		}
		finally
		{
			conn.close();
		}
		java.util.Arrays.sort(pathwayNames);
		return pathwayNames;
	}
	
}
