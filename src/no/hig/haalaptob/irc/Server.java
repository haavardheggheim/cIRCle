package no.hig.haalaptob.irc;

import java.io.Serializable;

/**
 * This class will only be used if the program doesn't find any customized server information file.
 * The class holds information from the mIrc servers.ini file but in a re-arranged style so it is easier to 
 * extract the needed information to create the customized server information file
 * 
 * @author Lap
 * @version 1.0
 * @see java.io.Serializable
 */

public class Server implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	String name, server, network;
	int port1;

	/**
	 * The function receives a line from the caller and split it up 
	 * and place it in separate containers.
	 * @param strLine is a line from mIRC servers.ini file
	 */
	public void Read(String strLine) {
		String tmp[] = strLine.split (":");					
		String tmp2[] = tmp[0].substring(0, tmp[0].length()-6).split("="); 		//Splits NOxx=Name
		name = tmp2[1];
		server = tmp[1];
		network = tmp[3];
		port1 = Integer.parseInt (tmp[2].substring(0,4));
	}
	
	/**
	 * This function will return all the needed information about 
	 * a server to the caller in a special arranged way.
	 * @return The entire server info as a string with a fix 
	 * 		   format ready for ServerFileOut class
	 */
	public String getOutput() {
		String p1;
		p1 = Integer.toString(port1);
		return network+":"+name+":"+server+":"+p1;
	}
}
