package no.hig.haalaptob.irc;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
 * 
 * This class present a simple Gridlayout so the user 
 * can edit informasion about an existing server.
 * 
 * @author Lap
 * @version 1.0
 * @see javax.swing.JPanel
 */
public class EditServerGUI extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private JTextField groupName = new JTextField(); 
	private JTextField serverName = new JTextField();
	private JTextField serverAddress = new JTextField();
	private JTextField serverPort = new JTextField();
	private JButton ok, abort;
	private SortedServers sortedServers;
	private JComboBox info;
	private int firstDimensionPosistion, secondDimensionPosistion;
	private JInternalFrame window;
	
	/**
	 * Constructor for this class. It will create a Gridbaglayout with the needed Textfield to display and 
	 * allow the user to change the server information.
	 * 
	 * @param sortedServers Object of sorted servers
	 * @param info	The object of the dropdownbox that contains the servers under the same group
	 * @param first	An <code>Integer</code> that tells witch slot this server has in the first array of the two-dimensionarray
	 * @param second An <code>Integer</code> that tells witch slot this server has in the Second array of the two-dimensionarray
	 * @param myWindow Which frame does this belong to
	 */
	public EditServerGUI(SortedServers sortedServers, JComboBox info, int first, int second, JInternalFrame myWindow){
		this.sortedServers = sortedServers;
		this.info = info;
		firstDimensionPosistion = first;
		secondDimensionPosistion = second;
		window = myWindow;
		
	    GridLayout layout = new GridLayout(0,2);					//new gridLayout with 2 columns.	
	    layout.setVgap(3);											//vertical gap between components	
	    layout.setHgap(4);											//Horizontal gap between components
	    setBorder(new EmptyBorder(4,4,4,4));
	    
		setLayout(layout);
		add(new JLabel(I18N.get("EditServerGUI.serverName")));
		add(serverName);
		add(new JLabel(I18N.get("EditServerGUI.serverAddress")));
		add(serverAddress);
		add(new JLabel(I18N.get("EditServerGUI.serverPort")));
		add(serverPort);
		add(ok = new JButton(I18N.get("ok")));
		add(abort = new JButton(I18N.get("abort")));	
		showOldValues();
	
		ok.addActionListener(new ActionListener() {					//ationListener for adding server information
			public void actionPerformed(ActionEvent ae){
				addNewInfo(firstDimensionPosistion,secondDimensionPosistion);				
			}	
		});
				
		abort.addActionListener(new ActionListener() {				//cancel actionListener, closes all the windows 
			public void actionPerformed(ActionEvent ae){
				window.dispose();				
			}
		});	
	}
	
	/**
	 * Function that will show the old values in the fields
	 * This is the ensure that the user edits the right
	 * serverinformation. 
	 */
	public void showOldValues(){
		String tempArray[] = sortedServers.getServerInfo(firstDimensionPosistion, secondDimensionPosistion).split(":");
		groupName.setText(tempArray[0]);
		serverName.setText(tempArray[1]);
		serverAddress.setText(tempArray[2]);
		serverPort.setText(tempArray[3]);
	}
	/**
	 * Function for checking if some fields is empty
	 */
	public Boolean checkInfo(){
		if(groupName.getText().isEmpty() == true|| serverName.getText().isEmpty() == true|| 
		   serverAddress.getText().isEmpty() == true || serverPort.getText().isEmpty() == true)
		{
			return false;
		}
		return true;
	}
	
	/**
	 * Function for adding new information. 
	 */
	public void addNewInfo(int first, int second){
		String newServerInfo;
		newServerInfo = groupName.getText()+":"+serverName.getText()+":"+serverAddress.getText()+":"+serverPort.getText();
		if(checkInfo() == true){
			sortedServers.EditServerInfo(first, second, newServerInfo);
			info.setModel(sortedServers.getChannelArrayinfo(first));
			window.dispose();
			JOptionPane.showMessageDialog( new JFrame(""),I18N.get("EditServerGUI.okMessage"));
	    }
		else{
			JOptionPane.showMessageDialog( new JFrame(""),I18N.get("EditServerGUI.warningMessage"));
		}
	}	
				
}