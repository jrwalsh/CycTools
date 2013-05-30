package edu.iastate.cyctools.view.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JDialog;

import edu.iastate.cyctools.DefaultController;

@SuppressWarnings("serial")
public class SearchResultDialog extends JDialog implements ActionListener, PropertyChangeListener {
    private JOptionPane optionPane;
    private DefaultController controller;

    private String btnString1 = "Enter";
    private String btnString2 = "Cancel";

    public SearchResultDialog(JFrame aFrame, String searchTerm, DefaultController controller) {
        super(aFrame, true);
        
        this.controller = controller;

        optionPane = new JOptionPane();
        setContentPane(optionPane);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
        	public void windowClosing(WindowEvent we) {
        		optionPane.setValue(new Integer(JOptionPane.CLOSED_OPTION));
            }
        });

        //Register an event handler that reacts to option pane state changes.
        optionPane.addPropertyChangeListener(this);
    }

    /** This method reacts to state changes in the option pane. */
    public void propertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName();

        if (isVisible()
         && (e.getSource() == optionPane)
         && (JOptionPane.VALUE_PROPERTY.equals(prop) ||
             JOptionPane.INPUT_VALUE_PROPERTY.equals(prop))) {
            Object value = optionPane.getValue();

            if (value == JOptionPane.UNINITIALIZED_VALUE) {
                //ignore reset
                return;
            }

            //Reset the JOptionPane's value.
            //If you don't do this, then if the user
            //presses the same button next time, no
            //property change event will be fired.
            optionPane.setValue(
                    JOptionPane.UNINITIALIZED_VALUE);

            if (btnString1.equals(value)) {
                    JOptionPane.showMessageDialog(
                    		SearchResultDialog.this,
                                    "Sorry, \"" + "\" "
                                    + "isn't a valid response.\n"
                                    + "Please enter "
                                    + ".",
                                    "Try again",
                                    JOptionPane.ERROR_MESSAGE);
            } else {
                clearAndHide();
            }
        }
    }
    
    /** This method clears the dialog and hides it. */
    public void clearAndHide() {
        setVisible(false);
    }

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
