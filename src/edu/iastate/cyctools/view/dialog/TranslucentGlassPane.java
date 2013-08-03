package edu.iastate.cyctools.view.dialog;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;

import javax.swing.JComponent;


// Simple class which can be added as a glass panel to the main JFrame,
// blocking user input (mouse input at least) and darkening the panel to indicate "busy".
@SuppressWarnings("serial")
public class TranslucentGlassPane extends JComponent {
   public TranslucentGlassPane() {
	   setLayout(new GridLayout());
       setBackground(Color.RED);
       this.addMouseListener(new MouseAdapter() {});
   }

   @Override
   protected void paintComponent(Graphics g) {
	   Graphics2D g2 = (Graphics2D) g;
	   g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.65f));
	   Rectangle clip = g.getClipBounds();
	   g2.fillRect(clip.x, clip.y, clip.width, clip.height);
       g2.dispose();
   }
}