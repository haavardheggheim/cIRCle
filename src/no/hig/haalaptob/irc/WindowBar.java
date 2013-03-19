package no.hig.haalaptob.irc;

import java.awt.Color;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JToolBar;
import javax.swing.border.Border;

/**
 * This class represents the statusbar in the program,
 * which shows all open channels + connection window. 
 * 
 * @author Lap
 * @version 1.0
 * @see javax.swing.JToolBar
 */


public class WindowBar extends JToolBar implements ActionListener {

	private static final long serialVersionUID = 1L;
	
	private BlinkingDesktopButtons [] blinkingButton = new BlinkingDesktopButtons[20];
	private ConView[] windowList = new ConView[20];
	private ConView con;
	private Border redborder = BorderFactory.createLineBorder(Color.red);
	private Border defaultborder = BorderFactory.createEmptyBorder();


	
	/**
	 * This Function receives the object conview that's created in another class and establish a link
	 * And the create the button for "main window"
	 * @param conview is an Object of ConView created in another class
	 * @param name	contains the name of the connection window
	 */
	public void synCon(ConView conview, String name){
		con = conview;
		blinkingButton[0] = new BlinkingDesktopButtons(name);
		blinkingButton[0].addActionListener(this);	
		add(blinkingButton[0]);
		updateUI();
	}
	

	/**
	 * Function for adding a new channel to the bar
	 * @param windowName Name of the window
	 * @param chan	Which channel should we add?
	 */
	
	public void addNew(String windowName, ChannelView chan){
		int tell = 1;
		while(blinkingButton[tell] != null){		//find a empty spot
			tell++;
		}
		add(blinkingButton[tell] = new BlinkingDesktopButtons(windowName)); //add the new JButton
		blinkingButton[tell].addActionListener(this);						//add actionlisener
		windowList[tell] = chan;											//copy the object link
		
	}
	/**
	 * Function for remove a channel from the list
	 * @param chanName Name of this channel
	 */

		public void removeButton(String chanName){
		  for(int i = 0; i < 20; i++){
			if(blinkingButton[i] != null &&chanName == blinkingButton[i].getText()){	//is in the bar?
				this.remove(blinkingButton[i]);			//ok, remove it
				updateUI();								//update UI
				blinkingButton[i] = null;					//free space
				break;
			}
		}
	}
	

	

		/**
		 * The function check if the channel GUI that has received a new message has focus, if not it
		 * will start a new tread that makes the <code>JButton</code> that belongs to that channel to blink. 
		 * @param chan The object that has received a new message
		 */
		public void newMessageWarning(ChannelView chan){
			for(int i = 0; i <= 20; i++){
				if(chan.channel.getName() == blinkingButton[i].getText()){		//if the channel match the button name
						if(windowList[i].hasFocus() == false){					// if it doesn't have the focus
							Thread myTread = new Thread(blinkingButton[i]);		
							blinkingButton[i].blinking(true);					
							myTread.start();
						}	
					}
				}
			}
	
		/**
		 *This Function receives the channel that has focus and stop the <code>JButton<code> blinking if its blinking
		 *  @param chan the ChannelView object we want to stop the <code>JButton</code> blinking on
		 */
		public void stopMessageWarning(ChannelView chan){
			for(int i = 0; i < 20; i++){
				if( blinkingButton[i] != null && chan.channel.getName() == blinkingButton[i].getText()){
					blinkingButton[i].blinking(false);
				}
			}
		}
	
	/*
	 * (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent ae){
		if(ae.getSource().equals(blinkingButton[0])){
				con.moveToFront();					//move the window to front
				con.input.requestFocus();			//and request focus
		}
		else {
			for(int i = 1; i < blinkingButton.length; i++) {
				if(ae.getSource().equals(blinkingButton[i])) {
					windowList[i].moveToFront();
					windowList[i].requestFocus();
					blinkingButton[i].blinking(false);
				}
			}
	    }	
	}
}