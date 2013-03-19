package no.hig.haalaptob.irc;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JButton;

/**
 * BlinkingDesktopButtons.java
 * 
 * This class is a Runnable class that receives a <code>JButton</code> and make it 
 * "blink" by changing border and background color until the owner cancel it
 *
 * @author Lap
 * @version 1.0
 * @see javax.swing.JButton
 */

public class BlinkingDesktopButtons extends JButton implements Runnable {
	
	private static final long serialVersionUID = 1L;
	private boolean blink;
	
	/**
	 * Setting name name of the Buttons
	 * @param name The name of the channel
	 */
	public BlinkingDesktopButtons(String name){
		this.setText(name);
	}
	
	/**
	 * This function is used when we want the tread to stop blinking
	 * @param shouldBlink <code>boolean</code> that can disable the blinking function
	 */
	public void blinking(Boolean shouldBlink){
		blink = shouldBlink;
		
	}
	
	/**
	 * The Runnable Thread function that shift the border and background of the JButton so it "blinks" as long as the
	 * Boolean blink is <code>true</code>
	 */
	
	@Override
	public void run() {
		while(blink == true){
			try {
				setBackground(Color.red);
				setBorder(BorderFactory.createLineBorder(Color.red));
				Thread.sleep(1000);
				setBackground(null);
				setBorder(BorderFactory.createEmptyBorder());
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
