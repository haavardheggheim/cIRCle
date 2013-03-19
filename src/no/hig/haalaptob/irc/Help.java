package no.hig.haalaptob.irc;

import java.io.IOException;
import java.net.URL;
import javax.swing.JEditorPane;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;


/**
 * Class that holds the Help functionality
 * The class allows the user to be guided
 * "around" the functionality in cIRCle
 * 
 * @author Tobias
 * @version 1.0
 * @see javax.swing.JInternalFrame
 */

public class Help extends JInternalFrame {

	private static final long serialVersionUID = 1L;

	JScrollPane scrollPane; 
	JEditorPane editorPane; 
	
	/**
	 * Constructor for making a HTML help guide
	 * @param url The <code>URL</code> for the site
	 * @throws IOException if the <code>URL</code> cant be found or is invalid. 
	 */
	public Help(URL url) throws IOException {

		//Sendingen parameters to the motherclass JInteralFrame
		super("Help", true, true, true, true);
		
		//constructing a new pane for holding the URL
		scrollPane = new JScrollPane(editorPane = new JEditorPane());
		editorPane.setEditable(false); 
		editorPane.setPage(url); 
		editorPane.addHyperlinkListener(new HyperLink()); //allows us to click on links
		add(scrollPane);
		
		setVisible(true); 
		setSize(600, 400); 
		
	}
}
