package no.hig.haalaptob;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * This class is used to simplify internationalization
 * @author Håvard
 * @version 1.0
 */
public class I18N {
	static ResourceBundle messages = ResourceBundle.getBundle("i18n/irc", Locale.getDefault());
	
	/**
	 * This method calls the resource bundles.
	 * Making this method static enables calls to i18n.getMessage
	 * @return a string with the requested resource
	 */
	public static String get(String key)	{
	    return messages.getString(key);
	}
}
