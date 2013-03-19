package no.hig.haalaptob.irc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import jerklib.Channel;
import jerklib.ConnectionManager;
import jerklib.Profile;
import jerklib.Session;
import jerklib.events.ChannelListEvent;
import jerklib.events.ErrorEvent;
import jerklib.events.IRCEvent;
import jerklib.events.IRCEvent.Type;
import jerklib.events.JoinCompleteEvent;
import jerklib.events.JoinEvent;
import jerklib.events.NickChangeEvent;
import jerklib.events.MessageEvent;
import jerklib.events.QuitEvent;
import jerklib.events.PartEvent;
import jerklib.events.TopicEvent;
import jerklib.events.WhoisEvent;
import jerklib.events.modes.ModeEvent;
import jerklib.listeners.IRCEventListener;
import no.hig.haalaptob.I18N;

/**
 * This is the view that holds the server connection information.
 * It is also a super class. 
 * 
 * @author Håvard, Tobias, Lap
 * @version 2.0
 * @see javax.swing.JInternalFrame
 */

public class ConView extends JInternalFrame implements IRCEventListener, ActionListener {	

	/*
	 * Declaring all the variables and objects we need for this class
	 */
	
	private static final long serialVersionUID = 1L;
	
	public JTextPane view = new JTextPane();
	public JTextField input = new JTextField();
	public JScrollPane scroll;
	public Vector<ChannelView> channels = new Vector();
	public Vector<PrivateMessageView> privateMessages = new Vector();
	public ConnectionManager conman;
	public ChannelLists chanlist;
	public Session session;
	public Profile profil;
	private SimpleAttributeSet localSimpleAttributeSet;
	public ChannelView cv; 
	public WindowBar windowbar; 
	
	private Prefs prefs = Prefs.getPrefs();		//fetches preferences
	
	/**
	 * Sync the windowbar
	 * @param bar which bar should we sync? 
	 */
	public void syncWindowbar(WindowBar bar){
		windowbar = bar;
	    windowbar.synCon(this, I18N.get("ConView.title"));
	}
	
	/**
	 * Constructor for connecting to a server
	 * @param server which server should we connect to?
	 */
	public ConView(String server) {
		super(server, true, true, true, true);	//sending parameters to the mother.
			
		view.setEditable(false); 
		add(new JScrollPane(view), BorderLayout.CENTER);
		add(scroll = new JScrollPane(this.view), "Center");
		setSize(600,300);
		
		 input = new JTextField();
	     input.setBorder(BorderFactory.createLineBorder(Color.BLACK));
	     input.addActionListener(this);
	     add(input, BorderLayout.SOUTH); 
		 this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		 setInternalFrameLisener();
		 
	}
	/*
	 * (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent ae) {
		
		
		if(ae.getSource().equals(input)) {
			String channel = input.getText();
			
			if(channel.startsWith("/j #")) {			//starts with /j # for joing
				session.join( channel.substring(3) );	//Connect to this channel, ignore the /j #
				input.setText(""); 						//clear the textfield
			}
			//skriver ut liste over kanaler
			else if(channel.startsWith("/list")) {			//starts with /list
				chanlist = new ChannelLists(this.session);	//construct a new ChannelList
				Main.getLayout().DesktopPane.add(chanlist);	//add it to the DesktopPane
				chanlist.setVisible(true);					//set it to be visible
				session.chanList();							
			}
				
			else if(channel.startsWith("/quit")) {			//quit all open windows 
				quitAll();	
			}
		}
	}
	/**
	 * Iterates through all open channels and dispose them and the and dispose this window.
	 * its used by the /quit command in the connectionWindow
	 * connectionManager. 
	 */
	public void quitAll(){
        Enumeration<ChannelView> ce = this.channels.elements();
        while (ce.hasMoreElements()) {							//has more elements?
           ChannelView ne = (ChannelView) ce.nextElement();		//casting next element to ChannelView
        	   ne.dispose();									//dispose
        }
           conman.quit(); 										//quit connectionManager
           Main.getLayout().removeWindowBar();					//remove windowbar
           this.dispose();										//close this window
           						
	}
	
	/**
	 * A listener for the ConView Frame, which will
	 * remove the statusbar and all other channels
	 * if this is closed. 
	 */
	public void setInternalFrameLisener() {						
		addInternalFrameListener(new InternalFrameAdapter(){
			public void internalFrameClosed(InternalFrameEvent e) {
				if( e.getInternalFrame().getName() == I18N.get("ConView.title") || e.getInternalFrame().getName() == "ConnectionWindow" ){
					guiQuitServer();
					Main.getLayout().removeWindowBar();
				}
			}
		});
	}
	    
	/**
	 * Function that initiates connection and construct 
	 * a new Profil for this user
	 */
	public void connect() {
		conman = new ConnectionManager(profil = new Profile(prefs.get("name", ""), prefs.get("nick", ""), prefs.get("alt", ""), "needbetternick"));
		conman.requestConnection(prefs.get("server", ""), prefs.getInt("port", 6666)).addIRCEventListener(this);
	}
	
	
	/**
	 * JerkLibs event listener that will automatically trigger when a event happens.
	 * @param e Which IRCEvent triggered this function? 
	 */
	@Override
	public void receiveEvent(IRCEvent e) {
		if(e.getType() == Type.CONNECT_COMPLETE) {		
			session = e.getSession();		
		}
		
		else if(e.getType() == Type.JOIN_COMPLETE) {			
			JoinCompleteEvent jce = (JoinCompleteEvent)e;												//Join complete event 
			ChannelView chan = new ChannelView(((JoinCompleteEvent)jce).getChannel(), windowbar);		//creates new channelView window
			chan.setName(jce.getChannel().getName());
	        Main.getLayout().DesktopPane.add(chan);														//adds channelView to the main window
	        chan.appendMsg("Now talking in "+jce.getChannel().getName() + "\n", true);					//appending which channel we are in.
	        chan.appendMsg("Topic of the day: " + jce.getChannel().getTopic() + "\n\n", false); 		//set the topic
	        chan.moveToFront();		
	        chan.input.requestFocus();
	        chan.move(150, 50); 
	        chan.setVisible(true);
	        this.channels.add(chan);
	        windowbar.addNew(jce.getChannel().getName(), chan);											//add to statusbar
		}

		else if(e.getType() == Type.CHANNEL_MESSAGE) {
            MessageEvent me = (MessageEvent)e;															//message event
            Channel gc = me.getChannel();																//getting the channel
            Enumeration<ChannelView> ce = this.channels.elements();										//iterates through the ChannelList
            while (ce.hasMoreElements()) {
              ChannelView ne = (ChannelView) ce.nextElement();
              if (ne.isChannel(gc)) {
            		ne.appendMsg("<"+me.getNick()+"> "+me.getMessage(), true);							//say to channel
                break;
              }
            }
        }
		else if(e.getType() == Type.TOPIC) {															//event for setting topic 
            TopicEvent topic = (TopicEvent)e;															//object to the Topic 
            Channel gc = topic.getChannel();															//getting the channel
            Enumeration<ChannelView> ce = this.channels.elements();
            while (ce.hasMoreElements()) {
              ChannelView ne = (ChannelView) ce.nextElement();
              if (ne.isChannel(gc)) {
            		ne.appendMsg(topic.getSetBy()+ "has set the topic to " +topic.getTopic(), false );	//setting the topic
                break;
              }
            }
        }
	
		else if(e.getType() == Type.CHANNEL_LIST_EVENT) {
			ChannelListEvent chanevt = (ChannelListEvent)e;												//Cast to ChannelListEvent
			chanlist.addChannel(chanevt.getChannelName(), chanevt.getNumberOfUser());					//add channel to the list
			
		}
		else if (e.getType() == Type.WHOIS_EVENT) {														//event for Whois
          WhoisEvent whois = (WhoisEvent)e;
          appendMsg("Host: "+whois.getHost()+"\nNick: "+whois.getNick()+"\nUsername: "+whois.getUser()+"\nRealname: "+whois.getRealName(), false);
        
		
		}
		else if (e.getType() == Type.PRIVATE_MESSAGE) {													//Private message 
			MessageEvent pm = (MessageEvent)e;															//message event
			String nick = ((MessageEvent)e).getNick();													//getting the nick
			Object[] frames = Main.getLayout().DesktopPane.getAllFrames();
			for(int i=0;i<frames.length;i++) {															//iterates through the frames
				if(!(frames[i] instanceof PrivateMessageView))											//already exists? 
					continue;																			//no? Continue
				PrivateMessageView ob1 = (PrivateMessageView) frames[i];
				if(!(ob1.chattingWith(nick)))															//Talking to the same person?
					continue;
				this.privateMessages.add(ob1);
				ob1.appendMsg("<"+pm.getNick()+">"+pm.getMessage(), true);								//append message in the chat window 
				return;
			}
			PrivateMessageView ob2 = new PrivateMessageView(e.getSession(), pm.getNick());				//new privatemessage window
			this.privateMessages.add(ob2);
			Main.getLayout().DesktopPane.add((Component)ob2);
			ob2.appendMsg("<"+pm.getNick()+">"+pm.getMessage(), true);
			ob2.setVisible(true);			
		}
	
		else if(e.getType() == Type.JOIN){																//iterates through channels and adding the nick
			JoinEvent joinEvent = (JoinEvent)e;
			Channel gc = joinEvent.getChannel();
            Enumeration<ChannelView> ce = this.channels.elements();
            while (ce.hasMoreElements()) {
              ChannelView ne = (ChannelView) ce.nextElement();
              if (ne.isChannel(gc)) {																	//is the object a channel? 
            	  ne.addnewUserToList(joinEvent.getNick());												//add to nickname to the list
            	  break;
              }
            }
		}
	
		else if(e.getType() == Type.MODE_EVENT){
			ModeEvent modeEvent = (ModeEvent)e;															//casting the object
			Channel gc = modeEvent.getChannel();														//getting the channel
            Enumeration<ChannelView> ce = this.channels.elements();										
            while (ce.hasMoreElements()) {																//iterates through elements
              ChannelView ne = (ChannelView) ce.nextElement();											//nextElement?
              if (ne.isChannel(gc)) {																	//is channel?
            	  String tmp = modeEvent.getModeAdjustments().toString();
            	  appendMsg(tmp, false);
            	  if(tmp.charAt(1) == '+'){		
            	  	if(tmp.charAt(2) == 'o'|| tmp.charAt(2) == 'O' ){									//is the user OPed? 
            	  		ne.addMode("@", tmp.substring(4, tmp.length()-1));								//add the mode
            	  		ne.appendMsg(modeEvent.setBy() +" has OP-ed " + tmp.substring(4, tmp.length()-1) , false);	//append to the chatwindow
            	  	}
            	  	if(tmp.charAt(2) == 'v'|| tmp.charAt(2) == 'V' ){									//is the user Voiced? 
            	  		ne.addMode("+", tmp.substring(4, tmp.length()-1));								//add the mode
             	  		ne.appendMsg(modeEvent.setBy() +" has voiced " + tmp.substring(4, tmp.length()-1) , false);

            	  	}
            	  }
            	  if(tmp.charAt(1) == '-'){
              	  	 if(tmp.charAt(2) == 'o'|| tmp.charAt(2) == 'O' ){									//deop and devoice the user
            	  		ne.removeMode("@", tmp.substring(4, tmp.length()-1));
             	  		ne.appendMsg(modeEvent.setBy() +" has DeOP-ed " + tmp.substring(4, tmp.length()-1) , false);
            	  	 }
              	  	 if(tmp.charAt(2) == 'v'|| tmp.charAt(2) == 'V' ){
            	  		ne.removeMode("+", tmp.substring(4, tmp.length()-1));
             	  		ne.appendMsg(modeEvent.setBy() +" has de-voiced " + tmp.substring(4, tmp.length()-1) , false);
            	  	 } 
            	  }
            		  
            	  break;
              }
            }
		}
		else if(e.getType() == Type.NICK_CHANGE){
			NickChangeEvent nickChangeEvent = (NickChangeEvent)e;										//casting the objects
			Object[] temp = nickChangeEvent.getSession().getChannels().toArray();						//temporary Object-array
            Enumeration<ChannelView> ce = this.channels.elements();
            for(int i = 0; i < nickChangeEvent.getSession().getChannels().size(); i++) {				//iterate through channels
            	Channel gc = (Channel) temp[i];      
            	while (ce.hasMoreElements()) {
            		ChannelView ne = (ChannelView) ce.nextElement();
            			ne.changeNick(nickChangeEvent.getOldNick(),nickChangeEvent.getNewNick());		//getting the gold nick and new Nick
            			ne.appendMsg("The user "+nickChangeEvent.getOldNick()+ " has changed his nick to " 	
            						 + nickChangeEvent.getNewNick(), false);
            	}
		
            }
		}
		else if(e.getType() == Type.QUIT){
			QuitEvent quitEvent = (QuitEvent)e;
            Enumeration<ChannelView> ce = this.channels.elements();
			for (int i = 0; i <quitEvent.getChannelList().size(); i++){									//iterate thorugh the channels
				Channel gc = quitEvent.getChannelList().get(i);											//getting the i'th element in the channelList	
	            while (ce.hasMoreElements()) {															//more elements?
	              ChannelView ne = (ChannelView) ce.nextElement();
	              if (ne.isChannel(gc)) {
	            	  ne.removeUserFromList(quitEvent.getNick());										//remove users from the list
	            	  ne.appendMsg("The user "+quitEvent.getNick() +" has quited", false);				//append to the channel which user that quitted
	                  break;
	              }
                }
            }
		}	
		else if(e.getType() == Type.PART){
			
			PartEvent partEvent = (PartEvent)e;
			Channel gc = partEvent.getChannel();
			Enumeration<ChannelView> ce = this.channels.elements();
	        while (ce.hasMoreElements()) {
	            ChannelView ne = (ChannelView) ce.nextElement();
	            if (ne.isChannel(gc)) {
	            	if( partEvent.getWho().equals(profil.getActualNick()) ){							//get the nick to deside which user who quitted 
	            		String temp = ne.channel.getName();												//get the channelname
	            		ne.part();																		//part from this channel
	            		windowbar.removeButton(temp);													//remove from tabbedPane
	            	}
	            	else {
	            		ne.removeUserFromList(partEvent.getWho());
	            		ne.appendMsg("The user "+partEvent.getWho() +" has parted us", false);
	            	}	
	              break;
	            }
	        }
		}
		else if(e.getType() == Type.ERROR){
			ErrorEvent errorEvent = (ErrorEvent)e; 
		}
		
		else {
			appendMsg("\n"+e.getType()+":"+e.getRawEventData(), true);
		}
		
	}

	
	/**
	 * Function to add text to all of the windows in the program. 
	 * @param msg the text you want to add
	 * @param chooseAble Can the user edit this text color? 
	 */
	
	public void appendMsg(String msg, boolean chooseAble) {		
		
		localSimpleAttributeSet = new SimpleAttributeSet();
		
		if(chooseAble){
			//Retrieving the fonts, sizes and foreground color from the preferences 
			StyleConstants.setForeground(localSimpleAttributeSet, new Color(prefs.getInt("foregroundColor",  Color.BLACK.getRGB()))); 
			StyleConstants.setFontFamily(localSimpleAttributeSet, prefs.get("fontType", "Times new Roman")); 
			StyleConstants.setFontSize(localSimpleAttributeSet, prefs.getInt("fontSize", 12)); 
		}
		else {
			StyleConstants.setForeground(localSimpleAttributeSet, Color.BLUE);
			StyleConstants.setFontFamily(localSimpleAttributeSet, "Arial");
			StyleConstants.setFontSize(localSimpleAttributeSet, 14); 
		}
		int i = this.view.getStyledDocument().getEndPosition().getOffset();
		
		//inserting the string
	    try {
			this.view.getStyledDocument().insertString(i, msg + "\n", localSimpleAttributeSet);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    SwingUtilities.invokeLater(new Thread()	{
	      public void run()	{
	        JScrollBar scrollbar = scroll.getVerticalScrollBar();
	        scrollbar.setValue(scrollbar.getMaximum());
	      }
	    });
	}
	
	/**
	 * Function to join a channel by GUI
	 * @param channel which channel to join? 
	 */
	
	public void guiChannelJoin(String channel) {
		session.join(channel); 
	}
	
	/**
	 * This Function is used when the user quits by clicking "x". Unlike the other similar function
	 * this function doesn't contain dispose() call that would trigger the windowlistener that would trigger this
	 * again, ect. 
	 */
	public void guiQuitServer() {

		Enumeration<ChannelView> ce = this.channels.elements();	//parting all the channels 
        while (ce.hasMoreElements()) {
            ChannelView ne = (ChannelView) ce.nextElement();
            ne.part(); 
        }
	    conman.quit();  //quiting the connectionManager
	}
	
	/**
	 * Show ChannelList by GUI
	 */
	public void guiChannelList() {
		/*
		 * Dialog for questioning the user if he want to show the list! 
		 */
		int n = JOptionPane.showConfirmDialog(
			    this,
			    I18N.get("ChanList"),
			    I18N.get("showChannelList"),
			    JOptionPane.YES_NO_OPTION);
		
		if(n == 0) {		//if user chooses 'YES'
			chanlist = new ChannelLists(this.session);
			Main.getLayout().DesktopPane.add(chanlist);
			chanlist.setVisible(true);	
			session.chanList();
		}
	}
}