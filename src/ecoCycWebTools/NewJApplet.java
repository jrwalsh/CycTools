package ecoCycWebTools;
import javax.swing.JApplet;
import javax.swing.SwingUtilities;

public class NewJApplet extends JApplet {
    //Called when this applet is loaded into the browser.
    public void init() {
        //Execute a job on the event-dispatching thread; creating this applet's GUI.
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    createGUI();
                }
            });
        } catch (Exception e) { 
            System.err.println("createGUI didn't complete successfully");
        }
    }
    
    private void createGUI() {
        //Create and set up the content pane.
        ExportPathway newContentPane = new ExportPathway();
        setContentPane(newContentPane);
        newContentPane.setLocationRelativeTo(null);
        newContentPane.setVisible(true);
        //newContentPane.setOpaque(true);
        
    }
}