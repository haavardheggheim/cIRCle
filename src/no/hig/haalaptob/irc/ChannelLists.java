package no.hig.haalaptob.irc;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import jerklib.Session;
import no.hig.haalaptob.I18N;


/**
 * ChannelList.java
 * 
 * This class presents a list of all the channels in a connected server.
 * It is automatically sorted by channel names, but can also be sorted by number of users in a channel.
 * U can also search for a channel in the input field
 * When u double click a channel in the list u will automatically join the channel. 
 * 
 * @author Håvard & Tobias
 * @version 1.5
 * @see javax.swing.JInternalFrame
 */

public class ChannelLists extends JInternalFrame implements ActionListener {

	 
	 private static final long serialVersionUID = 1L;
	 
	 Vector<Channel> channels = new Vector();
	 JList listChannels;			//list of all channels
	 boolean update = false;		
	 boolean nameSort = true;		//if(true) sort by name else sort by numbers
	 Session session = null;		//session object
	 JButton sortByName;			//Button for sorting
	 JButton sortByUsers;			//Button for sorting
	 int totNumChans;				//total number of channels
	 
	 JScrollPane scrollPane; 		//ScrollPane that holds the List
	 JTextField search; 			//SearchField
	
	/**
	 * Constructor for this class. In this constructor we also 
	 * call the function that is responsible for the GUI-construction
	 * @param _session which sesssion are we in. 
	 */
	public ChannelLists(Session _session) {
		super(I18N.get("ChannelListView.title"), true, true, true, true); 	//Sending to "the motherclass, JInternalFrame"
		this.session = _session;					//this class session object = parameter
		constructGUI(); 							//functioncall for making the GUI
		setSize(new Dimension(350, 310)); 			//and setting the size
	}
	
	/**
	 * Function for constructing the GUI. We use GridBagLayout 
	 * for this GUI. 
	 */
	private void constructGUI() {
	        
        JPanel panel = new JPanel(new GridBagLayout());		//Panel for holdning the GUI
        GridBagConstraints cs = new GridBagConstraints();	//Constraints

        cs.fill = GridBagConstraints.HORIZONTAL; 			//Objects should be horizontal
        cs.anchor = GridBagConstraints.CENTER; 				//and centered
        
        cs.gridy = 1; 										//should be placed in the 1 row
        cs.gridx = 0; 										//and 1 column 
        cs.gridwidth = 2; 									//fill 2 columns
        scrollPane = new JScrollPane(listChannels = new JList());    
        listChannels.setToolTipText(I18N.get("ChannelListView.double")); 
        panel.add(scrollPane, cs);							
       
        cs.gridy = 2;										//should be placed in the 2 row
        cs.gridx = 0;										//should be placed in the 1 column
        search = new JTextField(); 							//new textfield
        search.setToolTipText("Search for channels");
        panel.add(search, cs); 								//add them 
        
        													//And so on!
        cs.gridy = 3; 							
        cs.gridx = 0; 
        cs.gridwidth = 1; 
        sortByUsers = new JButton(I18N.get("ChannelListView.sortByUsers"));
        sortByUsers.addActionListener(this); 
        panel.add(sortByUsers, cs);
        
        cs.gridy = 3;
        cs.gridx = 1; 
        cs.gridwidth = 1; 
        sortByName = new JButton(I18N.get("ChannelListView.sortByName"));
        sortByName.addActionListener(this); 
        panel.add(sortByName, cs);
        
        //with this u can double click to join a channel
       
        this.listChannels.addMouseListener(new MouseAdapter() {
          public void mouseClicked(MouseEvent me) {
            if(me.getClickCount() == 2) {
              int i = ChannelLists.this.listChannels.locationToIndex(me.getPoint());			//getting the location of the mouse
              String str = (String)ChannelLists.this.listChannels.getModel().getElementAt(i);	//getting the element
              str = str.substring(0, str.indexOf(" ")).trim();									//trimming everything, but the channel name
              ChannelLists.this.session.join(str);												//join this channel
              dispose();																		//dispose the window
            }
          }
        });
        
        //with this u can search for a channel in the input field
        this.search.addCaretListener(new CaretListener() {
          public void caretUpdate(CaretEvent ce) {
        	  SwingUtilities.invokeLater(new ChannelLists.UpdateChannelList(channels));			//update the channelList
          }
        });
      
        getContentPane().add(panel, BorderLayout.CENTER);	//add to the panel
      
        setResizable(false);	//the user cant resize this window. 
	}
	
	
	
	/**
	   * This class constructs new Channel objects.
	   * Synchronize is implemented so that each time a new channel is generated, the display will update itself.
	   * This also updates the totNumChans variable so that we know how many channels exists.
	   * 
	   * @param name 			name of channel
	   * @param numberofusers	number of users in channel
	   */
	  public void addChannel(String name, int numberofusers) {
	    synchronized (this.channels) {						
	    	  this.channels.add(new Channel(name, numberofusers));	//adding a new Channel
	    	  this.totNumChans++;									//couting numbers of channel
	    }
	    if (!this.update) {
	      this.update = true;	//setting update = true
	      SwingUtilities.invokeLater(new ChannelLists.UpdateChannelList(channels));	//update the list
	    }
	    
	  }
	  
	  /**
	   * Innerclass that handels the sorting of the channelList
	   * @author Håvard
	   * @version 1.0
	   */
	  public class Channel implements Comparable {
	    String name;
	    int users;

	    /**
	     * Constructor
	     * @param name 			name of channel
	     * @param numberofusers	number of users in channel
	     */
	    public Channel(String name, int numberofusers) {
	      this.name = name;
	      this.users = numberofusers;
	    }
	    
	    /**
	     * if namesort is set to true this will sort channels by names, but if namesort is set to false
	     * this will sort by number of users in channel in descending order.
	     * @param ob Object to compare
	     */
	   public int compareTo(Object ob) {
		   if(nameSort)			//boolean is set to be nameSort 
			   return this.name.compareTo(((Channel)ob).name);	
		   else if(this.users>=((Channel)ob).users)
				return -1;
		   return 1;
	   }
	  }

	  /**
	   * This class updates the channel list. This puts all the channel elements in a list, and goes through the whole
	   * list displaying only the channels we want(searched for)
	   * @author Håvard
	   * @version 1.0
	   */
	  class UpdateChannelList implements Runnable {
	    Vector<Channel> channels;
		
	    /**
		 * Constructor for UpdateChannelList
		 * @param channels vector for all the channels 
		 */
		UpdateChannelList(Vector<Channel> channels) {
			this.channels = channels;
		}

		/**
		 * Iterates through the generated list of channels, displaying only the ones we want.
		 * Synchronize is implemented so that each time a new channel is generated, the display will update itself.
		 */
	    public void run() {
	      DefaultListModel localDefaultListModel = new DefaultListModel();
	      synchronized (channels) {
	        List localList = channels.subList(0, channels.size() - 1);	//generates the list of channel objects
	        TreeSet localTreeSet = new TreeSet(localList);
	        Iterator localIterator = localTreeSet.iterator();
	        localDefaultListModel.addElement(I18N.get("ChannelListView.numberOfChans")+": "+ChannelLists.this.totNumChans);	//displays the total number of channels in the list
	        while (localIterator.hasNext()) {		//iterates through the list
	        	ChannelLists.Channel localChannel = (ChannelLists.Channel)localIterator.next();
	        	if(((ChannelLists.this.search.getText().length() == 0) || (localChannel.name.toLowerCase().indexOf(ChannelLists.this.search.getText().toLowerCase()) > 0))) //if searched for nothing displays everything, if searched for something displays only something 
	        		localDefaultListModel.addElement(localChannel.name+" "+I18N.get("ChannelListView.numberOfUsers")+": "+localChannel.users);	//add the element to the list with internationalization
	        }
	        ChannelLists.this.listChannels.setModel(localDefaultListModel);
	      }
	      ChannelLists.this.update = false;
	    }
	  }

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent ae) {
		if(ae.getSource().equals(sortByName))
			nameSort = true;
		if(ae.getSource().equals(sortByUsers))
			nameSort = false;
		SwingUtilities.invokeLater(new ChannelLists.UpdateChannelList(channels));
	}
}