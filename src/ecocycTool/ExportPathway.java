package ecocycTool;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;

import javacyco.*;

import javax.swing.*;

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
	private HashMap<String,String> pathwayMap = new HashMap<String,String>();
	private DefaultListModel runModel;
	private DefaultListModel pathwaysModel;
	//
	
	private JButton buttonCancel;
	private JButton buttonRun;
	private JScrollPane jScrollPaneRun;
	private JList listPathways;
	private JButton buttonLeft;
	private JButton buttonAllRight;
	private JButton buttonRight;
	private AbstractAction abstractActionLeft;
	private AbstractAction abstractActionAllLeft;
	private AbstractAction abstractActionExportPathways;
	private AbstractAction abstractActionRight;
	private AbstractAction abstractActionAllRight;
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
		
		// For testing purposes....
		//ToolBox.createAffyProbeIDTranslationFile("/home/Jesse/Desktop/probeIDs.txt", "", true);
		ToolBox.regulators();
		
//		// Run GUI
//		SwingUtilities.invokeLater(new Runnable() {
//			public void run() {
//				ExportPathway inst = new ExportPathway();
//				inst.setLocationRelativeTo(null);
//				inst.setVisible(true);
//			}
//		});
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
			this.setResizable(false);
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
				buttonRun.setAction(getAbstractActionExportPathways());
			}
			{
				jScrollPaneRun = new JScrollPane();
				{
//					ListModel listRunModel = 
//						new DefaultComboBoxModel(
//								new String[] { "Item One", "Item Two" });
					listRun = new JList();
					jScrollPaneRun.setViewportView(listRun);
//					listRun.setModel(listRunModel);
//					listRun.setPreferredSize(new java.awt.Dimension(308, 208));
				}
			}
			{
				jScrollPanePathways = new JScrollPane();
				{
//					ListModel listPathwaysModel = 
//						new DefaultComboBoxModel(
//								new String[] { "Item One", "Item Two" });
					listPathways = new JList();
					jScrollPanePathways.setViewportView(listPathways);
//					listPathways.setModel(listPathwaysModel);
				}
			}
			{
				panelButtons = new JPanel();
				GroupLayout panelButtonsLayout = new GroupLayout((JComponent)panelButtons);
				panelButtons.setLayout(panelButtonsLayout);
				{
					buttonAllLeft = new JButton();
					buttonAllLeft.setText("<<");
					buttonAllLeft.setAction(getAbstractActionAllLeft());
				}
				{
					buttonLeft = new JButton();
					buttonLeft.setText("<");
					buttonLeft.setAction(getAbstractActionLeft());
				}
				{
					buttonRight = new JButton();
					buttonRight.setText(">");
					buttonRight.setAction(getAbstractActionRight());
					buttonRight.setEnabled(false);
				}
				{
					buttonAllRight = new JButton();
					buttonAllRight.setText(">>");
					buttonAllRight.setAction(getAbstractActionAllRight());
					buttonAllRight.setEnabled(false);
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
					.addContainerGap()
					.addGroup(thisLayout.createParallelGroup()
					    .addComponent(jScrollPaneRun, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 217, GroupLayout.PREFERRED_SIZE)
					    .addComponent(jScrollPanePathways, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 219, GroupLayout.PREFERRED_SIZE)
					    .addComponent(panelButtons, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 217, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
					.addGroup(thisLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					    .addComponent(buttonCancel, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					    .addComponent(buttonRun, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(33, 33));
				thisLayout.setHorizontalGroup(thisLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(jScrollPaneRun, GroupLayout.PREFERRED_SIZE, 312, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, 1, Short.MAX_VALUE)
					.addComponent(panelButtons, GroupLayout.PREFERRED_SIZE, 76, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
					.addGroup(thisLayout.createParallelGroup()
					    .addComponent(jScrollPanePathways, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 312, GroupLayout.PREFERRED_SIZE)
					    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
					        .addGap(0, 129, GroupLayout.PREFERRED_SIZE)
					        .addComponent(buttonRun, GroupLayout.PREFERRED_SIZE, 86, GroupLayout.PREFERRED_SIZE)
					        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, 1, GroupLayout.PREFERRED_SIZE)
					        .addComponent(buttonCancel, GroupLayout.PREFERRED_SIZE, 86, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap());
				thisLayout.linkSize(SwingConstants.VERTICAL, new Component[] {panelButtons, jScrollPaneRun});
				thisLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {buttonCancel, buttonRun});
				thisLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {jScrollPaneRun, jScrollPanePathways});
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
		runModel = new DefaultListModel();
		pathwaysModel = new DefaultListModel();
		pathwayMap = ToolBox.getAllPathways();
		
		Object[] pathwayNames = pathwayMap.keySet().toArray();
		java.util.Arrays.sort(pathwayNames);
		
		for (Object o : pathwayNames) pathwaysModel.addElement(o);
		
		listPathways.setModel(pathwaysModel);
		listRun.setModel(runModel);
	}
 	//

 	
 	private AbstractAction getAbstractActionAllLeft() {
 		if(abstractActionAllLeft == null) {
 			abstractActionAllLeft = new AbstractAction("<<", null) {
 				public void actionPerformed(ActionEvent evt) {
 					for (Object o : pathwaysModel.toArray()) runModel.addElement(o);
 					pathwaysModel.clear();
 					
 					// Enable/Disable Buttons
 					if (pathwaysModel.size() == 0) {
 						buttonAllLeft.setEnabled(false);
 	 					buttonLeft.setEnabled(false);
 					}
 					if (runModel.size() != 0) {
 						buttonAllRight.setEnabled(true);
 	 					buttonRight.setEnabled(true);
 					}
 				}
 			};
 		}
 		return abstractActionAllLeft;
 	}
 	
 	private AbstractAction getAbstractActionLeft() {
 		if(abstractActionLeft == null) {
 			abstractActionLeft = new AbstractAction("<", null) {
 				public void actionPerformed(ActionEvent evt) {
 					Object[] objects = new Object[listPathways.getSelectedIndices().length];
 					int index = 0;
 					
 					for (int i : listPathways.getSelectedIndices()) {
 						objects[index] = pathwaysModel.get(i);
 						index++;
 					}
 					
 					for (Object o : objects) {
 						runModel.addElement(o);
 						pathwaysModel.removeElement(o);
 					}
 					
 					// Enable/Disable Buttons
 					if (pathwaysModel.size() == 0) {
 						buttonAllLeft.setEnabled(false);
 	 					buttonLeft.setEnabled(false);
 					}
 					if (runModel.size() != 0) {
 						buttonAllRight.setEnabled(true);
 	 					buttonRight.setEnabled(true);
 					}
 				}
 			};
 		}
 		return abstractActionLeft;
 	}
 	
 	private AbstractAction getAbstractActionAllRight() {
 		if(abstractActionAllRight == null) {
 			abstractActionAllRight = new AbstractAction(">>", null) {
 				public void actionPerformed(ActionEvent evt) {
 					for (Object o : runModel.toArray()) pathwaysModel.addElement(o);
 					runModel.clear();
 					
 					// Enable/Disable Buttons
 					if (runModel.size() == 0) {
 						buttonAllRight.setEnabled(false);
 	 					buttonRight.setEnabled(false);
 					}
 					if (pathwaysModel.size() != 0) {
 						buttonAllLeft.setEnabled(true);
 	 					buttonLeft.setEnabled(true);
 					}
 				}
 			};
 		}
 		return abstractActionAllRight;
 	}
 	
 	private AbstractAction getAbstractActionRight() {
 		if(abstractActionRight == null) {
 			abstractActionRight = new AbstractAction(">", null) {
 				public void actionPerformed(ActionEvent evt) {
 					Object[] objects = new Object[listRun.getSelectedIndices().length];
 					int index = 0;
 					
 					for (int i : listRun.getSelectedIndices()) {
 						objects[index] = runModel.get(i);
 						index++;
 					}
 					
 					for (Object o : objects) {
 						pathwaysModel.addElement(o);
 						runModel.removeElement(o);
 					}
 					
 					// Enable/Disable Buttons
 					if (runModel.size() == 0) {
 						buttonAllRight.setEnabled(false);
 	 					buttonRight.setEnabled(false);
 					}
 					if (pathwaysModel.size() != 0) {
 						buttonAllLeft.setEnabled(true);
 	 					buttonLeft.setEnabled(true);
 					}
 				}
 			};
 		}
 		return abstractActionRight;
 	}
 	
 	private AbstractAction getAbstractActionExportPathways() {
 		if(abstractActionExportPathways == null) {
 			abstractActionExportPathways = new AbstractAction("Run", null) {
 				public void actionPerformed(ActionEvent evt) {
 					//TODO
 					//0) Get all pways
 					if (pathwaysModel.size() == 0) {
 						//TEST>>
 						//ToolBox.exportPathway("GLYCOLYSIS-TCA-GLYOX-BYPASS");
 						//<<TEST
 					}
 					//1) Get a single pway
 					else if (runModel.size() == 1) {
 						ToolBox.exportPathway(pathwayMap.get(runModel.firstElement()));
 					}
 					//2) Get multiple pways
 					else if (runModel.size() > 1) {
 						
 					}
 					//3) Ignore if empty
 					else {
 						
 					}
 				}
 			};
 		}
 		return abstractActionExportPathways;
 	}

}
