package no.hig.haalaptob.irc;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import no.hig.haalaptob.I18N;

/**
 * AddNewServerGUI.java
 * 
 * This class presents a simple gridlayout with the needed textfields to create 
 * and add a new server. This class is trigged by the "new" button in the 
 * LoginGUI class.
 * 
 * @author Lap
 * @version 1.0
 * @see javax.swing.JPanel 
 * @see no.hig.haalaptob.irc.LoginGUI
 */

public class AddNewServerGUI extends JPanel {
	
	/*
	 * Declaring and initializing objects that we need 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField groupName = new JTextField(); 
	private JTextField serverName = new JTextField();
	private JTextField serverAdress = new JTextField();
	private JTextField serverPort = new JTextField();
	private JButton ok, abort;
	private SortedServers sortedServers;
	private JComboBox groups, info;
	private JInternalFrame window;
	
	/**
	 * Constructor for this class 
	 * @param sortedServers	Object for the sorted servers
	 * @param groups The JComboBox object that contain groupname we wish to add the server under.
	 * @param info The JComboBox object that contains servers under the same groupname and the one we will add it too.
	 * @param myWindow Which frame does this belong to. 
	 */
	public AddNewServerGUI(SortedServers sortedServers, JComboBox groups, JComboBox info, JInternalFrame myWindow){
		this.sortedServers = sortedServers;
		this.groups = groups;
		this.info = info;
		window = myWindow;
		
	    GridLayout layout = new GridLayout(0,2);					//new Layout
	    layout.setVgap(3);											//setting the vertical gap between components 
	    layout.setHgap(4);											//====||====  horizontal gap =======||========	
	    setBorder(new EmptyBorder(4,4,4,4));
	    
		setLayout(layout);											//setting the layout
		add(new JLabel(I18N.get("AddNewServerGUI.groupName")));		//adding components to it
		add(groupName);
		add(new JLabel(I18N.get("AddNewServerGUI.serverName")));
		add(serverName);
		add(new JLabel(I18N.get("AddNewServerGUI.serverAddress")));
		add(serverAdress);
		add(new JLabel(I18N.get("AddNewServerGUI.serverPort")));
		add(serverPort);
		add(ok = new JButton(I18N.get("ok")));
		add(abort = new JButton(I18N.get("abort")));	
	
		ok.addActionListener(new ActionListener() {					//actionlistener for the OK Button
			public void actionPerformed(ActionEvent ae){
				addInput();											//call the function that perform the input check
			}	
		});
		
		abort.addActionListener(new ActionListener() {				//actionlistener for the Abort button 
			public void actionPerformed(ActionEvent ae) {
				window.dispose();									//Close the window and let the autogarbage cleaner remove it
			}	
		});
	}
	
	/**
	 * This Function will check that none of the textfield is left empty. or else it will corrupt our savefiles.
	 * @return <code>true</code> if no field is empty, <code>false</code> if empty fields
	 */
	public Boolean checkInfo(){										//Function for checking every field 
		if(groupName.getText().isEmpty() == true|| serverName.getText().isEmpty() == true || 
		   serverAdress.getText().isEmpty() == true || serverPort.getText().isEmpty() == true)	{
				return false;
			}
		return true;
	}
	
	/**
	 * Function for adding a new server to the list.
	 */
	public void addInput(){
		String newServerInfo;
		newServerInfo = groupName.getText()+":"+serverName.getText()+":"+serverAdress.getText()+":"+serverPort.getText();
		if(checkInfo() == true){
			sortedServers.AddNewServer(newServerInfo);		//call the function that sort and add the server to the right place
			info.setModel(sortedServers.getChannelArrayinfo(groups.getSelectedIndex()));   //Update the dropdownbox
			groups.setModel(new DefaultComboBoxModel(sortedServers.returnUsedList()));	   //Update the dropdownbox	
			window.dispose();															   //Close the window	
			JOptionPane.showMessageDialog( new JFrame(""),I18N.get("AddNewServerGUI.okMessage")); 			
	    }
		else {
			JOptionPane.showMessageDialog( new JFrame(""),I18N.get("AddNewServerGUI.warningMessage"));
		}
	}					
}
