package no.hig.haalaptob.irc;



import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.DefaultComboBoxModel;


/**
 * This class contains the information from the customized server information file ("ServerCustomInfo.txt" by default). 
 * The class will detect servers with the same group name and keep those under same group under the same arrays.
 * It also have the needed information to add or edit server manually and to sort the available servers descending  
 * 
 * @author Lap
 * @version 1.0
 */


public class SortedServers {
	public String groupedServers[][] = new String[300][100];
	public String netWorkList[] = new String[300];

	/**
	 * This function checks if a groupname is already in use by receiving a string to compare to the array
	 * @param s The <code>String</code> we want to check if its already in use
	 * @return An <code>Integer</code> indicating where the string name is in used if found, or else return 0;
	 */	
	public int findGroupposistion(String s){
		int count = 0;
		while(netWorkList[count] != null ) {
			if((netWorkList[count].equals(s) ) == true){
				return count;
			}
			count++;
		}
		netWorkList[count] = s;
		return 0;	
	}
	
	/**
	 * This function will overwrite old information with new information entered by the user
	 * @param first  Tells what group name the server belong to 
	 * @param second Tells what position it has in the array
	 * @param newInfo Overwrites the old information with the edited one
	 */
	
	public void EditServerInfo(int first, int second, String newInfo){
		groupedServers[first][second]= newInfo;
	}
	
	/**
	 * This function handle the removal of a server. If the server was the last server under a group name, it will also
	 * handle the deleting of the group name.
	 * 
	 * @param first Tells what group name the server belong to 
	 * @param second Tells what position it has in the array
	 * @return <code>true</code> if a server and a group was deleted, </code>false</code> if it was just a server
	 */
	public Boolean DeleteAServer(int first, int second){
		ServerFileOut output = new ServerFileOut();
		File temp = new File("ServerCustomInfo.txt");
		ArrayList arraylist = new ArrayList(); 
		int count, count2;
		count = count2 = 0;
		if(second == 0 && groupedServers[first][second+1] == null){ //Hvis dette var siste objektet i en gruppe/network
			while(count < first){;
				while(groupedServers[count][count2] != null){
					arraylist.add(groupedServers[count][count2++]);
				}
				count++;
			}
			count = (first+1);
			count2 = 0;
			while(groupedServers[count][0]!= null){;
				while(groupedServers[count][count2] != null){
					arraylist.add(groupedServers[count][count2++]);
				}
			count2 = 0;	
			count++;
			}
			temp.delete();
			for(int i = 0; i < arraylist.size(); i++){
				output.CustomFileOut(arraylist.get(i).toString(),"ServerCustomInfo.txt");
			}
			delete();
			Readin("ServerCustomInfo.txt");
			return true;
	   }
		else{
			count2 = second;
			while(count2 < (sizeOfUsedSecondGroupArray(first) -second) ){
				groupedServers[first][count2] = groupedServers[first][++count2];
			}
			return false;
		}
			
		
	}
	
	/**
	 * This function will delete the previously arrays so all the old information will be deleted by auto garbage collector 
	 */
	public void delete(){
		groupedServers = new String[300][100];
		netWorkList = new String[300];
		
	}
	
	/**
	 * This function check what's the next available array position under a existing group
	 * @param first Tells what group name the server belong to 
	 * @return An <code>Integer</code> that tells witch position to use
	 */
	public int NextAvaibleSecond(int first){
		int count = 0;
		while(groupedServers[first][count] != null){
			count++;
		}
		return 
			count;		
	}
	
	
	/**
	 * This function will read from the customized server information file and arrange the servers under the same
	 * group name.
	 * @param name the name of the file, (default name is "ServerCustomInfo.txt")
	 */
	public void Readin( String name){
		try {
			FileInputStream fstream = new FileInputStream(name);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine; int temp;
			
			while ( (strLine = br.readLine() ) != null) {
				String tmp[] = strLine.split (":");
				temp = findGroupposistion(tmp[0]);
				if(temp != 0){	//If the group already exist
					groupedServers[temp][NextAvaibleSecond(temp)] = strLine;
				}
				else{			//If the group name doesn't exist, find a empty spot and add it
					groupedServers[temp=findGroupposistion(tmp[0])][NextAvaibleSecond(temp)] = strLine;
				}
			}
			
			in.close();
		
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());			
		}
	}
	
	/**
	 * This function handles adding a new server information manually created by the users.
	 * If an existing group matches the user typed group name, it will be placed in that array.
	 * Or else a new group will be created
	 * @param info information about the new server. 
	 */
	public void AddNewServer(String info){
		String tmp[] = info.split(":");
		int temp;
		temp = findGroupposistion(tmp[0]);
		
		if(temp != 0){	//If the group already exist
			groupedServers[temp][NextAvaibleSecond(temp)] = info;
		}
		else{			//If the group name doesn't exist, find a empty spot and add it
			groupedServers[temp=findGroupposistion(tmp[0])][NextAvaibleSecond(temp)] = info;
		}
		
	}

	/**
	 * This function checks how many groups that exist 
	 * @return the number of existing groups
	 */
	public int SizeOfUsedList(){
		int count = 0;
		while(netWorkList[count] != null){
			count++;
		}
		return count;
	}
	
	/**
	 * This function checks how many position that is in used under a specified group name
	 * @param first tells what group we want to check 
	 * @return An <code>Integer</code> that tells how many position that is currently in use
	 */
	public int sizeOfUsedSecondGroupArray(int first){
		int count = 0;
		while(groupedServers[first][count] != null){
			count++;
		}
		return count;
		
	}

	/**
	 * This function will extract all the existing groupname and return it to the caller
	 * @return an array that contains all the existing group names
	 */
	
	public String[] returnUsedList(){
		String temp[] = new String[SizeOfUsedList()];
		for(int i = 0; i<SizeOfUsedList(); i++ ){
			temp[i] = netWorkList[i];
		}
	
		return temp;
		
	}
	
	/**
	 * This function is used by JComboBoxes that displays the servers under a specified group name by returning all the
	 * name of servers available under that group
	 * @param first Tells what group we are interested inn
	 * @return All the server information under that group name as a DefaultComboBoxModel
	 * so the Combobox can be updated easlier
	 */
	
	public DefaultComboBoxModel getChannelArrayinfo(int first){
		String templine ;
		String temparray[] = new String[sizeOfUsedSecondGroupArray(first)];
		String done[] = new String[sizeOfUsedSecondGroupArray(first)];
		int count = 0;
		while(groupedServers[first][count]!= null){
			templine = groupedServers[first][count];
			temparray = templine.split(":");
			done[count] = temparray[1];
			count++;
		}
		DefaultComboBoxModel box = new DefaultComboBoxModel(done);
		return box;
	}
	

	/**
	 * Function that saves the sorted server list to a .txt file. 
	 */
	public void saveSortedFileOut(){
		String templine;
		ArrayList arraylist = new ArrayList(); 
		int count, count2, count3;
		count= count2= count3 = 0;
		
		while(groupedServers[count][0]!= null){
			count2 = 0;
			while(groupedServers[count][count2] != null){
				arraylist.add(groupedServers[count][count2++]);
			}
			count++;
		}	
		Collections.sort(arraylist);
		ServerFileOut output = new ServerFileOut();
		for(int i = 0; i < arraylist.size(); i++){
				output.CustomFileOut(arraylist.get(i).toString(),"ServerCustomInfo.txt");
        }
	}
	
	/**
	 * Function that will save some additional servers to the ServerCustomInfo.txt 
	 */
	public void saveToFile(){
		ServerFileOut output = new ServerFileOut();
		int count, count2, count3;
		File temp = new File("ServerCustomInfo.txt");
		temp.delete();
		count= count2= count3 = 0;
		
		while(groupedServers[count][0]!= null){
			count2 = 0;
			while(groupedServers[count][count2] != null){
				output.CustomFileOut(groupedServers[count][count2++].toString(),"ServerCustomInfo.txt");
			}
			count++;
		}	
	}
	
	/**
	 * Function for getting the server address 
	 * @param first  Tells what group name the server belong to 
	 * @param second Tells what position it has in the array
	 * @return Information about the connection 
	 */
	public String[] getServerAdress(int first, int second){
		String templine;
		templine = groupedServers[first][second];
		String temparray[] = templine.split(":");
		String connectInfo[] = new String[2];
		connectInfo[0] = temparray[2];
		connectInfo[1] = temparray[3];
		return connectInfo;
	}
	
	/**
	 * Function for getting the serverinfo
	 * @param first  Tells what group name the server belong to 
	 * @param second Tells what position it has in the array
	 * @return The server located at this posistion 
	 */
	public String getServerInfo(int first, int second){
	
		return groupedServers[first][second];
	}	
}