package edu.iastate.cyctools.tools.frameView;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import edu.iastate.cyctools.CycToolsError;
import edu.iastate.cyctools.externalSourceCode.KeyValueComboboxModel;
import edu.iastate.javacyco.Frame;
import edu.iastate.javacyco.PtoolsErrorException;


// Design based on example code at http://darksleep.com/player/DialogExample/CustomDialog.java.html
@SuppressWarnings({"unchecked", "rawtypes", "serial"})
public class SearchResultDialog extends JDialog implements ActionListener {
	private JList listMatches;
	private JList listExact;
	private String selectedFrameID;
	private JButton btnNewButton;
	private JButton btnNewButton_1;
	
	private SearchResultDialog(JFrame parentFrame, KeyValueComboboxModel exactMatch, KeyValueComboboxModel matchingFrames) {
		super(parentFrame, true);
		setTitle("Search Results");
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.CENTER);
		
		btnNewButton = new JButton("Cancel");
		btnNewButton.addActionListener(this);
		
		btnNewButton_1 = new JButton("OK");
		btnNewButton_1.addActionListener(this);
		
		JLabel lblExactMatch = new JLabel("Exact Match");
		
		listExact = new JList();
		listExact.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				listMatches.clearSelection();
			}
		});
		listExact.setCellRenderer(new DefaultListCellRenderer() {
	        @Override
	        public Component getListCellRendererComponent(final JList list, Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
	        	if (value != null) value = "(" + value.toString().replace("=", ") ");
	            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
	        }
	    });
		listExact.setModel(exactMatch);
		if (exactMatch.isEmpty()) listExact.setEnabled(false);
		
		JLabel lblPossibleMatches = new JLabel("Possible Matches");
		
		JScrollPane scrollPane = new JScrollPane();
		
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(listExact, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 626, Short.MAX_VALUE)
						.addGroup(Alignment.TRAILING, gl_panel.createSequentialGroup()
							.addComponent(btnNewButton_1)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnNewButton))
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 626, Short.MAX_VALUE)
						.addComponent(lblPossibleMatches)
						.addComponent(lblExactMatch))
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblExactMatch)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(listExact, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)
					.addGap(11)
					.addComponent(lblPossibleMatches)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
					.addGap(15)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnNewButton)
						.addComponent(btnNewButton_1))
					.addContainerGap())
		);
		
		listMatches = new JList();
		listMatches.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				listExact.clearSelection();
			}
		});
		listMatches.setCellRenderer(new DefaultListCellRenderer() {
	        @Override
	        public Component getListCellRendererComponent(final JList list, Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
	        	if (value != null) value = "(" + value.toString().replace("=", ") ");
	            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
	        }
	    });
		listMatches.setModel(matchingFrames);
		if (matchingFrames.isEmpty()) listMatches.setEnabled(false);
		
		scrollPane.setViewportView(listMatches);
		panel.setLayout(gl_panel);
		
		pack();
		setLocationRelativeTo(parentFrame);
		setVisible(true);
	}

	public static SearchResultDialog showResults(JFrame parentFrame, Frame exactMatch, ArrayList<Frame> matchingFrames) {
		ArrayList<Frame> exactMatchList = new ArrayList<Frame>();
		if (exactMatch != null) exactMatchList.add(exactMatch);
		SearchResultDialog dialog = new SearchResultDialog(parentFrame, convertToModel(exactMatchList), convertToModel(matchingFrames));
		
		return dialog;
	}
	
	public String getSelection() {
		return selectedFrameID;
	}
	
	private static KeyValueComboboxModel convertToModel(ArrayList<Frame> frames) {
		KeyValueComboboxModel model = new KeyValueComboboxModel();
		if (frames == null || frames.isEmpty()) return model;
		
		for (Frame frame : frames) {
			try {
				model.put(frame.getLocalID(), frame.getCommonName());
			} catch (PtoolsErrorException e) {
				model.put(frame.getLocalID(), frame.getLocalID());
			}
		}
		
		return model;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (btnNewButton_1 == e.getSource()) {
			selectedFrameID = null;
			if (!listExact.isSelectionEmpty()) selectedFrameID = ((Map.Entry<String, String>) listExact.getSelectedValue()).getKey();
			else if (!listMatches.isSelectionEmpty()) selectedFrameID = ((Map.Entry<String, String>) listMatches.getSelectedValue()).getKey();
			
			if (selectedFrameID == null) CycToolsError.showError("Please select a frame", "No Selection");
			else setVisible(false);
		} else if (btnNewButton == e.getSource()) {
			selectedFrameID = null;
			setVisible(false);
		}
	}
}
