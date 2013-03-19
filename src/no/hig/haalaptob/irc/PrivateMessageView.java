package no.hig.haalaptob.irc;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import no.hig.haalaptob.I18N;

import jerklib.Session;

/**
 * This class manages the private message view.
 * Users can chat with other users in their own chat window.
 * 
 * @author Håvard
 * @version 1.0
 */
public class PrivateMessageView extends ConView {
	
	private static final long serialVersionUID = 1L;
	
	Session session;
	String nick;
	private Prefs prefs = Prefs.getPrefs();		//fetches preferences

	/**
	 * Constructor that initializes GUI, though most GUI components are inherited from ConView Class.
	 * @param session Which session are we in
	 * @param nick	The person you want to chat with 
	 */
	public PrivateMessageView(Session session, String nick) {
		super("");		
		setTitle(I18N.get("PrivateMessageView.title")+" "+nick);
		this.session = session;
		this.nick = nick;
		this.input.addActionListener(this);
		this.addInternalFrameListener(new Close());
	}
	
	/**
	 * This listens for a message in the input field and display it on screen.
	 * Also listens for commands.
	 */
	public void actionPerformed(ActionEvent ae) {
		String message = input.getText();
		if(message.matches("")){}
		
		else if(message.matches("/part")){
			this.dispose();												//just closes the window
		}
	
		else{
			this.session.sayPrivate(nick, message);						//say private 
			appendMsg("<"+prefs.get("nick", "")+">"+message, true);		//write text to the window
			input.setText("");											//clear the field
		}
	}
	
	/**
	 * This class acts like an JInternalFrame close button eventListener
	 * @author Håvard
	 * @version 1.0
	 */
	class Close extends InternalFrameAdapter {
		
		/**
		 *Empty Constructor 
		 */
		Close() {}
		
		/**
		 * This removes the private chat object when u close the window
		 * @param frame which frame should be removed 
		 */
		public void frameClose(InternalFrameEvent frame) {
			privateMessages.remove(frame.getInternalFrame());			//remove this JInternalFrame
		}
	}
	
	/**
	 * Function to check if string object equals the user nick in use
	 * @param ob2 the user we are chatting with
	 * @return 1 if equals the parameter or 0 if not. 
	 */
	public boolean chattingWith(String ob2) {
		return this.nick.equals(ob2);
	}
}