package no.hig.haalaptob.irc;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


import javax.swing.JFrame;


import no.hig.haalaptob.irc.Layout;

/**
* This class is where the application starts in the first case. 
* @author Håvard, Lap & Tobias
* @version 1.0
*/

public class Main {
	private static Layout layout;
	
	public static void main(String[] args)	{
		layout = new Layout();										//constructs a new Layout
		layout.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);	//Close if the user wants to
		layout.addWindowListener(new WindowAdapter() {				
			public void windowClosed(WindowEvent arg0) {
				layout.read.sort.saveToFile();						//saveToFile if program closes
				System.exit(0); 									//exit the program
			}
		});
	}

	
/**
 * Returns the existing layout object. Its used if a object created by layout need somthing from the layout class.
 * @return The layout Object
 */
	public static Layout getLayout() {
		return layout;
	}
}