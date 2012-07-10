package cycCurator;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Desktop.Action;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JMenuBar;
import javax.swing.border.EmptyBorder;
import java.awt.FlowLayout;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.border.EtchedBorder;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class CycSpreadsheetLoader extends JFrame {
	private JPanel contentPane;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField txtFilePath;
	private JButton button_1;
	private JComboBox comboBox;
	private JButton button_2;
	private JSplitPane splitPane;
	private JSplitPane splitPane_1;
	private JScrollPane scrollPane_2;
	private JScrollPane scrollPane_3;
	private JSplitPane splitPane_2;
	private JScrollPane scrollPane;
	private JScrollPane scrollPaneTable;
	private final ActionLoadTable actionLoadTable = new ActionLoadTable();
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CycSpreadsheetLoader frame = new CycSpreadsheetLoader();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Create the frame.
	 */
	public CycSpreadsheetLoader() {
		setMinimumSize(new Dimension(800, 525));
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		JPanel panel_1 = new JPanel();
		panel.add(panel_1);
		
		JButton btnLoadFile = new JButton("Load");
		panel_1.add(btnLoadFile);
		
		txtFilePath = new JTextField();
		txtFilePath.setText("C:\\Users\\Jesse\\Desktop\\test.csv");
		txtFilePath.setColumns(10);
		panel_1.add(txtFilePath);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel.add(panel_2);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[]{160, 86, 0};
		gbl_panel_2.rowHeights = new int[]{0, 0, 23, 0, 0};
		gbl_panel_2.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		gbl_panel_2.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel_2.setLayout(gbl_panel_2);
		
		JLabel label = new JLabel("Connection");
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.gridwidth = 2;
		gbc_label.insets = new Insets(0, 0, 5, 0);
		gbc_label.gridx = 0;
		gbc_label.gridy = 0;
		panel_2.add(label, gbc_label);
		
		textField_1 = new JTextField();
		textField_1.setText("jrwalsh.student.iastate.edu");
		textField_1.setColumns(10);
		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_1.insets = new Insets(0, 0, 5, 5);
		gbc_textField_1.gridx = 0;
		gbc_textField_1.gridy = 1;
		panel_2.add(textField_1, gbc_textField_1);
		
		JLabel label_1 = new JLabel("not connected");
		GridBagConstraints gbc_label_1 = new GridBagConstraints();
		gbc_label_1.insets = new Insets(0, 0, 5, 0);
		gbc_label_1.gridx = 1;
		gbc_label_1.gridy = 1;
		panel_2.add(label_1, gbc_label_1);
		
		textField_2 = new JTextField();
		textField_2.setText("4444");
		textField_2.setColumns(10);
		GridBagConstraints gbc_textField_2 = new GridBagConstraints();
		gbc_textField_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_2.insets = new Insets(0, 0, 5, 5);
		gbc_textField_2.gridx = 0;
		gbc_textField_2.gridy = 2;
		panel_2.add(textField_2, gbc_textField_2);
		
		JButton button_1 = new JButton("Connect");
		GridBagConstraints gbc_button_1 = new GridBagConstraints();
		gbc_button_1.anchor = GridBagConstraints.SOUTH;
		gbc_button_1.insets = new Insets(0, 0, 5, 0);
		gbc_button_1.gridx = 1;
		gbc_button_1.gridy = 2;
		panel_2.add(button_1, gbc_button_1);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setEnabled(false);
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.insets = new Insets(0, 0, 0, 5);
		gbc_comboBox.gridx = 0;
		gbc_comboBox.gridy = 3;
		panel_2.add(comboBox, gbc_comboBox);
		
		JButton button_2 = new JButton("Disconnect");
		GridBagConstraints gbc_button_2 = new GridBagConstraints();
		gbc_button_2.gridx = 1;
		gbc_button_2.gridy = 3;
		panel_2.add(button_2, gbc_button_2);
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setOneTouchExpandable(true);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setDividerSize(2);
		contentPane.add(splitPane, BorderLayout.CENTER);
		
		JSplitPane splitPane_1 = new JSplitPane();
		splitPane.setRightComponent(splitPane_1);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		splitPane_1.setLeftComponent(scrollPane_2);
		
		JScrollPane scrollPane_3 = new JScrollPane();
		splitPane_1.setRightComponent(scrollPane_3);
		
		JSplitPane splitPane_2 = new JSplitPane();
		splitPane.setLeftComponent(splitPane_2);
		
		JScrollPane scrollPane = new JScrollPane();
		splitPane_2.setLeftComponent(scrollPane);
		
		JScrollPane scrollPaneTable = new JScrollPane();
		splitPane_2.setRightComponent(scrollPaneTable);
	}
	
	private class ActionLoadTable extends AbstractAction {
		public ActionLoadTable() {
			putValue(NAME, "LoadTable");
			putValue(SHORT_DESCRIPTION, "Open an excel file into the Bulk Load tab.");
		}
		public void actionPerformed(ActionEvent e) {
			String excelFileName = txtFilePath.getText();
			File file = new File(excelFileName);   //excelFileName is the file you want to load.
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(file));
				String text = null;
				
				String header = reader.readLine();
				ArrayList<String> dataRows = new ArrayList<String>();
				while ((text = reader.readLine()) != null) {
					dataRows.add(text);
				}
				
				String[] headings = header.split(",");
				Object[][] data = new Object[dataRows.size()][dataRows.get(0).split(",").length];
				for (int i=0; i<dataRows.size(); i++) {
					data[i] = dataRows.get(i).split(",");
				}
				
				JTable jTable = new JTable(data, headings);
				scrollPaneTable.setViewportView(jTable);
				
			} catch (FileNotFoundException exception) {
				exception.printStackTrace();
			} catch (IOException exception) {
				exception.printStackTrace();
			} catch (Exception exception) {
				exception.printStackTrace();
			}
			finally {
				try {
				} catch (Exception exception) {
					exception.printStackTrace();
				}
				try {
					if (reader != null) {
						reader.close();
					}
				} catch (IOException exception) {
					exception.printStackTrace();
				}
			}
		}
	}

}
