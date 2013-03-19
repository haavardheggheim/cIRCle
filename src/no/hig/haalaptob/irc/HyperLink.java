package no.hig.haalaptob.irc;

import java.io.IOException;
import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * Class for handling different events triggered by the Help class
 * 
 * @author Tobias
 * @version 1.0
 * @see javax.swing.event.HyperlinkListener
 */

public class HyperLink implements HyperlinkListener {

	JEditorPane localPane;
	
	/**
	 * Function for updating the hyperlinks
	 * @param event Which HyperLink event triggered this function? 
	 */
	@Override
	public void hyperlinkUpdate(HyperlinkEvent event) {
		
		//has a Hyperlink been clicked?
		if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
		   
			/*
			 * Set the pane to contain the url
			 */
			localPane = (JEditorPane)event.getSource(); 
			try {
				
				localPane.setPage(event.getURL());
		 
				} 	catch(IOException ioe) { ioe.getMessage(); }
		}	
	}
}
