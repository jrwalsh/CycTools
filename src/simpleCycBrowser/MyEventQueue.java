package simpleCycBrowser;
import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

/*
 * Suggested that this method is very dangerous and should be avoided.
 * 
 * Make this work by adding the line:
 * Toolkit.getDefaultToolkit().getSystemEventQueue().push(new MyEventQueue());
 * to the begining of the main method of the Frame class.
 */


// @author Santhosh Kumar T - santhosh@in.fiorano.com 
public class MyEventQueue extends EventQueue{ 
    protected void dispatchEvent(AWTEvent event){ 
        super.dispatchEvent(event); 
 
        // interested only in mouseevents 
        if(!(event instanceof MouseEvent)) 
            return; 
 
        MouseEvent me = (MouseEvent)event; 
 
        // interested only in popuptriggers 
        if(!me.isPopupTrigger()) 
            return; 
 
        // me.getComponent(...) retunrs the heavy weight component on which event occured 
        Component comp = SwingUtilities.getDeepestComponentAt(me.getComponent(), me.getX(), me.getY()); 
 
        // interested only in textcomponents 
        if(!(comp instanceof JTextComponent)) 
            return; 
 
        // no popup shown by user code 
        if(MenuSelectionManager.defaultManager().getSelectedPath().length>0) 
            return; 
 
        // create popup menu and show 
        JTextComponent tc = (JTextComponent)comp; 
        JPopupMenu menu = new JPopupMenu(); 
        menu.add(new CutAction(tc)); 
        menu.add(new CopyAction(tc)); 
        menu.add(new PasteAction(tc)); 
        menu.add(new DeleteAction(tc)); 
        menu.addSeparator(); 
        menu.add(new SelectAllAction(tc)); 
 
        Point pt = SwingUtilities.convertPoint(me.getComponent(), me.getPoint(), tc);
        menu.show(tc, pt.x, pt.y);
    }
    
 // @author Santhosh Kumar T - santhosh@in.fiorano.com 
    class CutAction extends AbstractAction{ 
        JTextComponent comp; 
     
        public CutAction(JTextComponent comp){ 
            super("Cut"); 
            this.comp = comp; 
        } 
     
        public void actionPerformed(ActionEvent e){ 
            comp.cut(); 
        } 
     
        public boolean isEnabled(){ 
            return comp.isEditable() 
                    && comp.isEnabled() 
                    && comp.getSelectedText()!=null; 
        } 
    } 
     
    // @author Santhosh Kumar T - santhosh@in.fiorano.com 
    class PasteAction extends AbstractAction{ 
        JTextComponent comp; 
     
        public PasteAction(JTextComponent comp){ 
            super("Paste"); 
            this.comp = comp; 
        } 
     
        public void actionPerformed(ActionEvent e){ 
            comp.paste(); 
        } 
     
        public boolean isEnabled(){ 
            if (comp.isEditable() && comp.isEnabled()){ 
                Transferable contents = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this); 
                return contents.isDataFlavorSupported(DataFlavor.stringFlavor); 
            }else 
                return false; 
        } 
    } 
     
    // @author Santhosh Kumar T - santhosh@in.fiorano.com 
    class DeleteAction extends AbstractAction{ 
        JTextComponent comp; 
     
        public DeleteAction(JTextComponent comp){ 
            super("Delete"); 
            this.comp = comp; 
        } 
     
        public void actionPerformed(ActionEvent e){ 
            comp.replaceSelection(null); 
        } 
     
        public boolean isEnabled(){ 
            return comp.isEditable() 
                    && comp.isEnabled() 
                    && comp.getSelectedText()!=null; 
        } 
    } 
     
    // @author Santhosh Kumar T - santhosh@in.fiorano.com 
    class CopyAction extends AbstractAction{ 
        JTextComponent comp; 
     
        public CopyAction(JTextComponent comp){ 
            super("Copy"); 
            this.comp = comp; 
        } 
     
        public void actionPerformed(ActionEvent e){ 
            comp.copy(); 
        } 
     
        public boolean isEnabled(){ 
            return comp.isEnabled() 
                    && comp.getSelectedText()!=null; 
        } 
    } 
     
    // @author Santhosh Kumar T - santhosh@in.fiorano.com 
    class SelectAllAction extends AbstractAction{ 
        JTextComponent comp; 
     
        public SelectAllAction(JTextComponent comp){ 
            super("Select All"); 
            this.comp = comp; 
        } 
     
        public void actionPerformed(ActionEvent e){ 
            comp.selectAll(); 
        } 
     
        public boolean isEnabled(){ 
            return comp.isEnabled() 
                    && comp.getText().length()>0; 
        } 
    } 
} 