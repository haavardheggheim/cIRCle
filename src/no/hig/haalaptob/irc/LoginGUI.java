package no.hig.haalaptob.irc;

import javax.swing.*;
import no.hig.haalaptob.I18N;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * The class that present a GridbagLayout with dropdownboxes containing 
 * available servers, textfield that accept user input for needed 
 * information such as nickname and button with option to add/edit server information.
 * 
 * @author Lap
 * @version 1.0
 * @see javax.swing.JPanel
 */

public class LoginGUI extends JPanel{
	
	private static final long serialVersionUID = 1L;
	
	private String[] drop1, drop2;
	private static Prefs prefs = Prefs.getPrefs();		//fetches preferences
	private JComboBox groups, info;
	private JTextField name;
	private JTextField nick;
	private JTextField alt;
	private JButton newServer, editServer, deleteServer, sortServer, ok, abort;
	private JDesktopPane desktoppane;
	private JInternalFrame window;
	
	ServerFileRead read;
	
	/**
	 * Function for setting this read object to the parameter's
	 * @param read Object of the class ServerFileRead that reads the server.ini file
	 * and constructs the ServerCustomInfo.txt. This file holds all the available servers
	 * which the users can connect to. 
	 *
	 * @see no.hig.haalaptob.irc.ServerFileRead 
	 */
	public void serverFileReadObjectSync(ServerFileRead read){
		this.read = read;		
	}
	/**
	 * Function for setting this <code>JInternalFrame</code> to the incoming object in the constructor
	 * @param myWindow <code>JInternalFrame</code> object which represents a window. 
	 */
	public void windowSync(JInternalFrame myWindow){
		window = myWindow;
	}
	
	/**
	 * Function for syncing the desktopPane. 
	 * @param desktoppane
	 */
	public void desktopPaneSync(JDesktopPane desktoppane){
		this.desktoppane = desktoppane;
	}

	/**
	 * Function for login GUI, with a GridBagLayout
	 * Inside this function every component is internationalized 
	 */
	public void makeLoginGUI() {
		GridBagLayout layout = new GridBagLayout();					//new GridBagLayout
		setLayout(layout);											//setting the layout
		GridBagConstraints constraint = new GridBagConstraints();	//and constraints
		constraint.fill = GridBagConstraints.NONE;					//should not fill
		constraint.insets = new Insets (2,2,2,2);
		constraint.gridwidth = 1;									
		constraint.gridheight = 4;	
		constraint.gridx = 3;										//placed in column 3
		constraint.gridy = 1;										//and row 1
		
		JPanel lay2 = new JPanel();
		lay2.setLayout(new GridLayout(4,0));
		lay2.add(newServer = new JButton (I18N.get("new")));		//adding new buttons
		lay2.add(editServer = new JButton (I18N.get("edit")));
		lay2.add(deleteServer = new JButton (I18N.get("delete")));
		lay2.add(sortServer = new JButton (I18N.get("sort")));	
		layout.setConstraints (lay2,constraint);
		add(lay2);													//add the JPanel

		constraint.fill = GridBagConstraints.HORIZONTAL;
		constraint.anchor = GridBagConstraints.WEST;	
		constraint.gridheight = 1;	
		constraint.gridwidth = 2;
		constraint.gridx = 0;
		constraint.gridy = 1;
		JPanel drops = new JPanel();
		drops.setLayout(new GridLayout(1,3));
		drops.add(new JLabel(I18N.get("LoginGUI.ircNetwork")));
		drops.add(groups = new JComboBox(read.sort.returnUsedList()));
		drops.add(info = new JComboBox(read.sort.getChannelArrayinfo(groups.getSelectedIndex())));
		layout.setConstraints (drops,constraint);
		add(drops);

		constraint.gridx = 0;
		constraint.gridy = 9;
		constraint.gridwidth = 3;
		constraint.anchor = GridBagConstraints.CENTER;
		JPanel south = new JPanel();
		south.setLayout(new GridLayout(1,3));
		south.add(ok = new JButton(I18N.get("ok")));
		south.add(abort = new JButton(I18N.get("abort")));
		south.add(new JButton(I18N.get("help")));
		layout.setConstraints (south, constraint);
		add(south);
	
		constraint.gridwidth = 1;
		constraint.gridx = 0;
		constraint.gridy = 5;	

		
		constraint.gridy = 6;
		JLabel label2 = new JLabel (I18N.get("name"));
		layout.setConstraints (label2, constraint);
		add (label2);

		constraint.gridy = 7;
		JLabel label3 = new JLabel (I18N.get("nickname"));
		layout.setConstraints (label3, constraint);
		add (label3);

		constraint.gridy = 8;
		JLabel label4 = new JLabel (I18N.get("alternative"));
		layout.setConstraints (label4, constraint);
		add (label4);

		constraint.gridx = 1;
		constraint.gridy = 5;

		constraint.gridy = 6;
		name = new JTextField (prefs.get("name", ""));
		name.setColumns(16);
		layout.setConstraints (name, constraint);
		add (name);

		constraint.gridy = 7;
		nick = new JTextField (prefs.get("nick",""));
		nick.setColumns(16);
		layout.setConstraints (nick, constraint);
		add (nick);	

		constraint.gridy = 8;
		alt = new JTextField (prefs.get("alt", ""));
		alt.setColumns(16);
		layout.setConstraints (alt, constraint);
		add (alt);

		constraint.gridheight = 1;
		constraint.gridy = 4;
		constraint.fill = GridBagConstraints.BOTH;
		JButton connect = new JButton(I18N.get("LoginGUI.ConToServer"));
		layout.setConstraints(connect,constraint);
		add (connect);
		
		/*
		 * ActionListener for connecting to a server
		 * this actionlistener also puts the info into the preferences 
		 */
		connect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae)
		    {
			  String connect[] = read.sort.getServerAdress(groups.getSelectedIndex(), info.getSelectedIndex());	
		      LoginGUI.prefs.put("name", LoginGUI.this.name.getText());
		      LoginGUI.prefs.put("nick", LoginGUI.this.nick.getText());
		      LoginGUI.prefs.put("alt", LoginGUI.this.alt.getText());
		      LoginGUI.prefs.put("server", connect[0]);	
		      LoginGUI.prefs.putInt("port", Integer.parseInt(connect[1]));	
		      Main.getLayout().connect();
		    }
		});
		
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae)
		    {
		      LoginGUI.prefs.put("name", LoginGUI.this.name.getText());
		      LoginGUI.prefs.put("nick", LoginGUI.this.nick.getText());
		      LoginGUI.prefs.put("alt", LoginGUI.this.alt.getText());
		      window.dispose();
		      
		    }
		});
		
		abort.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae)
		    {	
		      window.dispose();
		    }
		});
		
	
		groups.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae){
				info.setModel(read.sort.getChannelArrayinfo(groups.getSelectedIndex()));
			}	
		});	
		
		
		/*
		 * Sorting the servers 
		 */
		sortServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae){
				read.SortChannels();
				groups.setModel(new DefaultComboBoxModel(read.sort.returnUsedList()));	//sets the DataModel		
				JOptionPane.showMessageDialog( new JFrame(""),"Arrays are sorted");				
			}	
		});	
		
		/*
		 * Actionlistener for adding a new server to the list. 
		 */
		newServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae){;
				JInternalFrame addNewServerGUIWindow = new JInternalFrame(I18N.get("LoginGUI.add"), true, true, true, true);
				AddNewServerGUI newServerInfo = new AddNewServerGUI(read.sort, groups,info, addNewServerGUIWindow);
				addNewServerGUIWindow.add( newServerInfo);
				addNewServerGUIWindow.pack();
				desktoppane.add(addNewServerGUIWindow);
				addNewServerGUIWindow.setVisible(true);			
			}	
		});	
		
		/*
		 * ActionListener for editing a server. 
		 */
		editServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae){;
				JInternalFrame EditServerGUIWindow = new JInternalFrame(I18N.get("LoginGUI.edit"), true, true, true, true);
				EditServerGUI editServerGUI = new EditServerGUI(read.sort, info, groups.getSelectedIndex(), info.getSelectedIndex(), EditServerGUIWindow);
				EditServerGUIWindow.add( editServerGUI);
				EditServerGUIWindow.pack();
				desktoppane.add(EditServerGUIWindow);
				EditServerGUIWindow.setVisible(true);
			}	
		});	
		/*
		 * ActionListener for deleting a server from the list. 
		 */
		deleteServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae){
				if ( read.sort.DeleteAServer(groups.getSelectedIndex(), info.getSelectedIndex()) == true){
				info.setModel(read.sort.getChannelArrayinfo(groups.getSelectedIndex()));
				groups.setModel(new DefaultComboBoxModel(read.sort.returnUsedList()));
				}
				else{
					info.setModel(read.sort.getChannelArrayinfo(groups.getSelectedIndex()));
				}
				
			}	
		});	
	}	
}  