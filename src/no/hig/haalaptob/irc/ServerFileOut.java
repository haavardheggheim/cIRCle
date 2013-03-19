package no.hig.haalaptob.irc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;


/**
 * This class handle saving server information to file. The caller will send the file name, information line by line and
 * if the function should continue to write on the file or delete it and start a new one
 * 
 * @author Lap
 * @version 1.0
 */

public class ServerFileOut {

	/**
	 * The function that handle all the output
	 * @param output	information that is to be added.
	 * @param name		name of the file
	 */
	public void CustomFileOut(String output, String name){

		try{
			// Create file 
			FileWriter fstream = new FileWriter(name,true);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(output);
			out.newLine();
			//Close the output stream
			out.close();	
		}
		catch (Exception e){//Catch exception if any
		System.err.println("Error: " + e.getMessage());
		}
   }
}
