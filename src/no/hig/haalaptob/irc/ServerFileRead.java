package no.hig.haalaptob.irc;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;


/**
 * This class will either start the process of reading from the "ServerCustomInfo.txt" file if it exist, or else
 * it will read from mIrc's "Server.ini" file and then generate the file "ServerCustomInfo.txt" and then read it 
 * @author Lap
 * @version 1.0
 */
public class ServerFileRead {
	Server server[] = new Server[1000];	
	SortedServers sort = new SortedServers();
	
	/**
	 * Constructor that does a function call to the function ReadFile
	 */
	public ServerFileRead(){
		ReadFile();
	}
	
	/**
	 * Reading the servers.ini file and constructs
	 * a new .txt file containing all the 
	 * networks and server that are available
	 */
	public void ReadFile(){
		File sorted = new File("ServerCustomInfo.txt");								//create a new file
		if(sorted.exists() == false){												//does not exist
			try {	
				FileInputStream fstream = new FileInputStream("servers.ini");		//read the file 
				DataInputStream in = new DataInputStream(fstream);					//Data inputstream 
				BufferedReader br = new BufferedReader(new InputStreamReader(in));	
				String strLine;
				int count = 0;
			
				for(int i = 0; i < 18; i++) {
					strLine = br.readLine();
				}
			
				while ( (strLine = br.readLine() ) != null) {						//not the end of the stream
					server[count] = new Server();									//construct a new Server
					server[count++].Read(strLine);									//read info about this server
				}
			
			in.close();
		
			} catch (Exception e) {
				System.err.println("Error: " + e.getMessage());
			  }
			MakeCustomServerInfoFile();												
			File temp = new File("temp.txt");										//making a temporary file				
			sort.Readin("temp.txt");												//reading in to this file
			sort.saveSortedFileOut();												//save to file
			temp.delete();															//delete the object
			sort.delete();										
			sort.Readin("ServerCustomInfo.txt");									//read in to the ServerCustomInfo
		}
		else{
			sort.Readin("ServerCustomInfo.txt");									//or just read in the ServerCustomInfo
		}
	}
	
	/**
	 * This function makes the CustomServerInfoFile
	 * also know as the file containing all the servers
	 * that the user can connect to. 
	 */
	public void MakeCustomServerInfoFile(){
		int count = 0;
		ServerFileOut output = new ServerFileOut();
		while(server[count]!= null){
			output.CustomFileOut(server[count++].getOutput(),"temp.txt");				
		}			
	}
	
	
	/**
	 * Function that sorts the servers in the .txt file. 
	 */
	public void SortChannels(){
		File temp = new File("ServerCustomInfo.txt");
		temp.delete();
		sort.saveSortedFileOut(); 						//Make a sorted list and write it out
		sort.delete(); 									//delete the unsorted arrays
		ReadFile(); 									//read back the sorted servers and channels
	}
}	