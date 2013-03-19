package no.hig.haalaptob.irc;

import java.util.prefs.Preferences;

/**
 * This class holds the user preferences in the program
 * This function stores for instance userDefinedColors,
 * nicknames and so on. It also allows the developer to
 * retrieve userpreferences for later use in the program. 
 * 
 * @author Håvard, Lap, Tobias
 * @version 1.0
 */

public class Prefs {
	private static Preferences preferences = null;
	static Prefs prefs = null;
	
	/**
	 * Constructor for this class
	 */
	private Prefs() {
		preferences = Preferences.userNodeForPackage(getClass());
	}
	
	/**
	 * Function that will construct new preferences if it is not
	 * already is set. Else it will return the preferences 
	 * @return the preferences 
	 */
	public static Prefs getPrefs() {
		if(prefs == null)
			prefs = new Prefs();
		return prefs;
	}
	
	/**
	 * Function the get preferences which is saved by strings 
	 * @param a The name of the preference which is set when adding the preferences
	 * @param b Alternate <code>String</code>
	 * @return Returning the value of this preference
	 */
	public String get(String a, String b) {
		return preferences.get(a, b);
	}
	
	/**
	 * Function the get preferences which is saved by a int
	 * @param a The name of the preference which is set when adding the preferences
	 * @param b Alternate <code>Integer</code> value
	 * @return Returning the value of this preference
	 */
	public int getInt(String a, int b) {
		return preferences.getInt(a,b);
	}

	/**
	 * Function for adding preferences
	 * @param a Name of this preference
	 * @param b <code>String</code> value of this preference
	 */
	public void put(String a, String b) {
		preferences.put(a, b);		
	}
	
	/**
	 * Function for adding preferences
	 * @param a Name of this preference
	 * @param b <code>Integer</code> value of this preference 
	 */
	public void putInt(String a, int b) {
		preferences.putInt(a, b);
	}
}