package no.hig.haalaptob.irc;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JDesktopPane;

/**
 * Class that extends JDesktopPane. Class for setting our own background to JDesktopPane
 * @author Tobias
 * @version 1.0
 * @see javax.swing.JDesktopPane
 */

class MyDesktopPane extends JDesktopPane {
  
	private static final long serialVersionUID = 1L;
	
	Image img;		//Background Image
  
	public MyDesktopPane(){
    
		//Try to read the image from the resources 
		try {
			img = javax.imageio.ImageIO.read(new java.net.URL(getClass().getResource("/images/bluebg.jpg"), "bluebg.jpg"));
		}
			catch(Exception e){}
	}


  /**
   * Function for drawing the background
   * @param g <code>Graphics</code> to be painted
   */
  public void paintComponent(Graphics g)
  {
    super.paintComponent(g);
    if(img != null) g.drawImage(img, 0,0,this.getWidth(),this.getHeight(),this);
    else g.drawString("Image not found", 50,50);
  }
}