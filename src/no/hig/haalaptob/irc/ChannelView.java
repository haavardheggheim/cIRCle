package no.hig.haalaptob.irc;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;


import jerklib.Channel;
import jerklib.events.modes.ModeAdjustment;

/**
 * Class for holding the functions and GUI for the channels
 * The purpose of this class is to provide the application
 * with channel GUI and functions for all the 
 * action that will be triggered by different user actions.
 * This class inherited from the class.
 *
 * @author Tobias
 * @version 1.5
 * @see no.hig.haalaptob.irc.ConView
 */

public class ChannelView extends ConView implements ActionListener {
	
	private static final long serialVersionUID = 1L;

	private JScrollPane listScroller;
	private JScrollPane listScrollPane;
	private JPopupMenu userMenu;
	
	Channel channel;							//Channel Object
	private Prefs prefs = Prefs.getPrefs();		//fetches preferences
	
	private JList list;							//List containing the users
	private DefaultListModel listModel;			//ListModel

	Object[] users; 							//Users in a channel
	WindowBar windowbar;						//Windowbar Object

	/**
	 *  Constructor for this class. The class
	 *  is responsible for setting the 
	 *  channel name as title, construct a new
	 *  statusbar, chatwindow and a usermenu
	 *  
	 *  @param chan Channel object to retrieve information about
	 *  @param bar for adding the channel to the statusbar in the GUI
	 */	
	public ChannelView(Channel chan, WindowBar bar) {
		super(chan.getName());							//set the internalframe name equal to the channelname
		windowbar = bar;								
		setTitle(chan.getName()+" "+chan.getTopic());	//sets the titel of this window
		channel = chan;									//this channelobject equals the parameter
		constructChatWindow();							//construction the GUI for the chatwindow
		userMenu = createMenu();						//Constructing the menu
		list.addMouseListener(new PopupMenu());			//Constructing the popup-menu
		addUsersToList();								//add users to the list
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);	//change the default operation when using x	
		addInternalFrameListener(new InternalFrameAdapter(){	//add a windowadapter
			public void internalFrameClosed(InternalFrameEvent e) {
				channel.part("");								//part the channel when using x
			}
			public void internalFrameActivated(InternalFrameEvent e){
				gotFocus();										//call this function when window has focus
			}
		});

		sortList();
	}
	
	/**
	 * Sorts the listModel by putting all the elements into an arrayList. Then using
	 * collections to sort the list alphabetically. Then putting the sorted elements
	 * back into the listModel.
	 */
	public void sortList() {
		Object o;
		ArrayList al = new ArrayList();
		for (int i=0;i<listModel.size();i++) {	//iterates through the listModel
			o = listModel.elementAt(i);			//picking out i'te element
			al.add(o);							//add it to the arraylist
		}		
		Collections.sort(al);					//sort the arraylist
		listModel.clear();						//clear the list
		for (int i=0;i<al.size();i++) {			//this is only done because + comes before @ in the ASCII table
			o = al.get(i);						//getting i'te element
			if(((String)o).charAt(0) == '@')	//if the first textelement is @ for OP
				listModel.addElement(o);		//add to the list
		}
		for (int i=0;i<al.size();i++) {
			o = al.get(i);
			if(((String)o).charAt(0) != '@')
				listModel.addElement(o);
		}
	}
	
	/**
	 * Call the function that tells the Thread that the 
	 * internalframe has focus and it can stop the blinking
	 */
	public void gotFocus(){
		windowbar.stopMessageWarning(this);
	}
	
	/**
	 * Function for supporting the rightclick function in the
	 * userlist and adding object to it
	 * @return Returns the menu that appears when somebody rightclicks a user
	 */	
	private JPopupMenu createMenu() {
		JPopupMenu userMenu = new JPopupMenu();							//creates the menu. 
		JMenuItem whois = new JMenuItem("Whois");						//putting items in the menu
		whois.addActionListener(new ActionListener() {					//actionlistener for events
			public void actionPerformed(ActionEvent ae) {
				Object user = ChannelView.this.list.getSelectedValue(); //gets the marked user
				channel.getSession().whois((String)user);				//getting the whois from the user list
			}		
		});
													
		
		JMenuItem privchat = new JMenuItem("Private Chat");				
		privchat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				String user = ChannelView.this.list.getSelectedValue().toString();							//get the rightclicked user
				PrivateMessageView pmv = new PrivateMessageView(channel.getSession(), getCleanNick(user));	//new private chatwindow
				ChannelView.this.getDesktopPane().add(pmv);													//add to desktopPane
				pmv.setVisible(true);
			}
		});
		JMenuItem op = new JMenuItem("@OP");				
	    op.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent ae) {
	    		String user = ChannelView.this.list.getSelectedValue().toString();
	    		if(isUserVoiced(getCleanNick(user))){														//get clean nick
	    			removeMode("+",getCleanNick(user));														//remove voice
	    			addMode("@", getCleanNick(user)); 														//add OP
	    		}
				channel.op(getCleanNick(user));																//Change the nick to @nick
				
	    	}
	    });
		
	    userMenu.add(op);
		
		JMenuItem deop = new JMenuItem("DeOP");				
		deop.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
			String user = ChannelView.this.list.getSelectedValue().toString();	//iterates through the userlist
			if(isUserVoiced(getCleanNick(user))){								//retrieving the clean nick without signs and check if voiced
				removeMode("@",getCleanNick(user));								//removing the mode. 
				addMode("+",getCleanNick(user));								//adding the mode. 
			}
			channel.deop(getCleanNick(user));		
					
		}
		});
		userMenu.add(deop);
		
		JMenuItem voice = new JMenuItem("+Voice");				
	    voice.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent ae) {
	    		String user = ChannelView.this.list.getSelectedValue().toString();	//get the user
				channel.voice(getCleanNick(user));									//voice the user
	    	}
	    });
	    userMenu.add(voice);
	    
		JMenuItem deVoice = new JMenuItem("DeVoice");				
		deVoice.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
			String user = ChannelView.this.list.getSelectedValue().toString();		//get the username
				channel.deVoice(getCleanNick(user));								//devoice the user
			
		}
		});
		userMenu.add(deVoice);
	
		
		JMenuItem slap = new JMenuItem("Slap");				
		slap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				String user = ChannelView.this.list.getSelectedValue().toString();			//get the user
				channel.action("slap " +(String)user);										//slap him
				
			}
		});
		userMenu.add(slap);
		
		JMenuItem kick = new JMenuItem("Kick");				
			kick.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if(isUserOP(prefs.get("nick", "")) || isUserOP(prefs.get("alt", ""))){		//Receiving the nick or alternate nick
					String user = ChannelView.this.list.getSelectedValue().toString();		//get the user
					channel.kick(getCleanNick(user), "bye bye");							//kick him
					removeUserFromList(user);												//remove from the list
				}	
			}
		});
		userMenu.add(kick);																	//add to menu
		userMenu.add(privchat);																//add to menu
		userMenu.add(whois); 																//add to menu	

		return userMenu;
	}
	
	
	/**
	 * Function for constructing the chatWindow 
	 */
	public void constructChatWindow() {
			setLayout(new BorderLayout());	//	borderlayout 
			setVisible(true);				//	this window should be visible 
			
			
			/*
			 * Constructing the list
			 */
			
			listModel = new DefaultListModel();		// Listmodel for handling the users in the channel
	        list = new JList(listModel);			// adding the listmodel to a JList. 
	        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);	
	        list.setSelectedIndex(0);
	        list.setVisibleRowCount(5);
	        listScrollPane = new JScrollPane(list);
	        
	     
	        /*
	         * Field that allow the user to write something in the channel
	         */
	        input.addActionListener(this);
	      
	   
	        /*
	         * Adding the objects to a panel
	         */
	        add(input, BorderLayout.SOUTH); 
	        add(listScrollPane, BorderLayout.EAST); 
	        add(view, BorderLayout.CENTER);
	        add(scroll = new JScrollPane(this.view), "Center");
	        
	}
	
	/**
	 * Function to add users to the channel-list
	 */
	public void addUsersToList() {
		Iterator<String> nicks = ChannelView.this.channel.getNicks().iterator();
	
	    while (nicks.hasNext()){		//does the list contain anymore users? 
	    	String nextNick; 
	    	nextNick = nicks.next();	//next element in the list 
	    	if(isUserOP(nextNick)) {	//is the user oped in this channel?			
	    		channel.addNick("@" + nicks.toString());	//add a @-sign to that
		    	ChannelView.this.listModel.addElement((String)"@" + nextNick.toString());
	    	}
	    	
	    	else if(isUserVoiced(nextNick)) {	//is the user voiced in this channel?
	    		channel.addNick("+" + nicks.toString());	//add a +-sign to that
		    	ChannelView.this.listModel.addElement((String)"+" + nextNick.toString());
	    	}
	    	else{
	    		channel.addNick(nicks.toString());			//ain't OPed or Voiced? Too bad
	    		ChannelView.this.listModel.addElement((String)nextNick.toString());
	    	}
	    }
	} 
	

	/**
	 * Function to part/leave from a channel and remove 
	 * this channel from jerklibs list over active channels and 
	 * dispose the internalFrame
	 */
	public void part() {
		channels.remove(this);
		this.dispose();
		channel = null;
	}
	
	/**
	 * Check if the user has any modes
	 * @param nick which nick should we check
	 * @return <code>true</code> if mode, <code>false</code> if not
	 */
	public Boolean hasmode (String nick) {
		if(nick.charAt(0) =='@' || nick.charAt(0) =='+'){  //If the nick starts with a mode sign characther
			return true;
		}
		return false;
	}
	
	/**
	 * Get nick without mode sign
	 * @return clean nick without any signs like OP or Voice
	 */
	public String getCleanNick(String nick){
		if (hasmode(nick) == true){
			return nick.substring(1);
		}
		return nick;
		
	}
	
	/**
	 * Function to add users the the list 
	 * when he joins the channel
	 * 
	 * @param nick Name of the user to add
	 */
	public void addnewUserToList(String nick){
		listModel.addElement(nick);
		this.appendMsg("the user " +nick+ " has joined us", false);
	}
	
	/**
	 * Function the remove user from a list in a channel
	 * @param nick The nickname of a user
	 */
	public void removeUserFromList(String nick){
		for(int i = 0; i < listModel.size(); i ++){
			if ( listModel.get(i).toString().equals(nick) || listModel.get(i).toString().equals(nick.substring(1))) {
				listModel.remove(i);
			}
		}
	}
	
	/**
	 * Function for changing nick 
	 * @param oldNick 	The users old nick
	 * @param newNick	The users new nick
	 */
	public void changeNick(String oldNick, String newNick){
		for(int i = 0; i < listModel.size(); i ++) {
			//iterate thorugh the listModel and looking for the old nick
			if ( listModel.get(i).toString().equals(oldNick) || listModel.get(i).toString().substring(1).equals(oldNick)) {
					if(isUserOP(newNick)){	//setting the new nick to persons that have Op
						listModel.set(i, "@"+newNick);
					}
					else if(isUserVoiced(newNick)){	//setting new nick to persons that have voice
						listModel.set(i, "+"+newNick);
					}
					else	
						listModel.set(i, newNick);
			}
		}
	}
	/**
	 * Function for adding a mode to a nick
	 * @param mode Which mode should be set?
	 * @param nick 	Which nick should be change mode to? 
	 */
	public void addMode(String mode, String nick){
		for(int i = 0; i < listModel.size(); i ++){
			if ( listModel.get(i).toString().equals(nick)) {		//iterates through the listModel and if it finds the nick
				listModel.set(i, mode+nick);						//set the mode
			}
		}
	}
	
	/**
	 * Function for removing mode
	 * @param mode	Which mode should be removed
	 * @param nick	Which nick should be take away a mode?		
	 */
	public void removeMode(String mode, String nick){
		for(int i = 0; i < listModel.size(); i ++){
			if ( listModel.get(i).toString().equals(mode+nick)) {	//iterates thorugh til listModel and if it finds the nick
				listModel.set(i, nick);								//remove the mode
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see no.hig.haalaptob.irc.ConView#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent ae) {
		if(ae.getSource().equals(ChannelView.this.input)) {
			String sayToChannel = ChannelView.this.input.getText();
			if(sayToChannel.matches("")){}	//do nothing
			
			else if(sayToChannel.matches("/part")) {
				channel.part("");
				
			}
			
			else if(sayToChannel.length() > 6 && sayToChannel.substring(0, 5).matches("/nick")){
				channel.getSession().changeNick(sayToChannel.substring(5));
				ChannelView.this.input.setText(""); 
			}
			else if(sayToChannel.length() > 10 && sayToChannel.substring(0, 9).matches("/setTopic")){
				//check whenever this person is an operator of the channel
				if(isUserOP(prefs.get("nick", "")) || isUserOP(prefs.get("alt", ""))) {
					channel.setTopic(sayToChannel.substring(9));
					ChannelView.this.input.setText("");
				}	
			}
			
			else{
				ChannelView.this.channel.say(sayToChannel);
				this.appendMsg("<"+prefs.get("nick", "")+">"+sayToChannel, true);
				ChannelView.this.input.setText(""); 
			}
		}
	}
	
	
	/**
	 * Anonymous innerclass for handling mousevents
	 * @author Håvard
	 * @version 1.0
	 */
	class PopupMenu extends MouseAdapter {
	    PopupMenu()	{ }
	    
	    public void mousePressed(MouseEvent me)	{
	      maybeShowPopup(me);
	    }

	    public void mouseReleased(MouseEvent me) {
	      maybeShowPopup(me);
	    }

	    private void maybeShowPopup(MouseEvent me) {
	      if (me.isPopupTrigger())
	        ChannelView.this.userMenu.show(me.getComponent(), me.getX(), me.getY());
	    }
	  }

	/**
	 * Function the check if a channel is a channel
	 * @param gc Channel object to check if this channel is "itself"
	 * @return <code>true</code> if it is, and <code>false</code> if not
	 */
	public boolean isChannel(Channel gc) {
		return gc.equals(this.channel);
	}
	
	/**
	 * Function for checking if a user is OPed in a channel
	 * @param nick The username to desire whenever this user is OPed
	 * @return <code>true</code> if OPed, <code>false</code> if not. 
	 */
	public boolean isUserOP(String nick) {
		String userName = nick;
		Iterator<String> usersWithOP = channel.getNicksForMode(ModeAdjustment.Action.PLUS, 'o').iterator();
		while (usersWithOP.hasNext()) {		//iterates through the list
			String u = usersWithOP.next();	//String u is the next element i the list
			if(u.equals(userName)) 			//equals to the preferences nick?
				return true; 				//okey, the user is the channel operator. 
		}	
				return false;				//NO he isn't! 
	}


	/**
	 * Function for checking if a user is voiced 
	 * @param nick The nick to check
	 * @return <code>true</code> if voiced, <code>false</code> if not. 
	 */
	public boolean isUserVoiced(String nick) {
		String userName = nick;
		Iterator<String> usersWithOP = channel.getNicksForMode(ModeAdjustment.Action.PLUS, 'v').iterator();
		while (usersWithOP.hasNext()) {		//iterates through the list
			String u = usersWithOP.next();	//String u is the next element i the list
			if(u.equals(userName)) 			//equals to the preferences nick?
				return true; 				//okey, the user is voiced. 
		}
		return false;
	}
}