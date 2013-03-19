package no.hig.haalaptob.irc;


import java.awt.Color;

import java.awt.Dimension;

import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;


import no.hig.haalaptob.I18N;

/**
 * This Class allows the users to change 
 * certain textcolor, fonts and sizes. The restriction is set
 * in the AppendMsg function in the ConView class. 
 *
 * @author Tobias
 * @version 1.0 
 * @see javax.swing.JDialog
 */

public class UserDefinedGUI extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;

	Color color; 																	//Color-Object. 
	GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();	//Getting the graphics
	String fonts[] = env.getAvailableFontFamilyNames(); 							//retrieving fonts from the OS
	Integer[] fontSizes1 = {8, 12, 14, 16, 18, 20, 32, 64};							//Array containing font sizes
		
	I18N languages = new I18N(); 													//Internationalization 

	JComboBox fontSizeBox, fontBox;													//ComboBox containing fontSize and fonts
	JLabel textSizeLabel;			
	JLabel textFontLabel;
	JLabel textColorLabel; 
	JButton colorChooser; 
	
	Prefs prefs = Prefs.getPrefs();													//preferences 
	
	/**
	 * Constructor for this class
	 * @param f Which frame is this Dialog "owner"
	 */
	public UserDefinedGUI(Frame f) {

		super(f, I18N.get("UDG.title")); 
		
		setLayout(new GridBagLayout());					 //gridbag Layout
	   
		fontSizeBox = new JComboBox(fontSizes1);  		 //new ComboBox with fontSizes 
		fontBox = new JComboBox(fonts); 		  		 //ComboBox for fonts
		
		textSizeLabel = new JLabel(I18N.get("UDG.size")); 
		textFontLabel = new JLabel(I18N.get("UDG.type")); 
		textColorLabel = new JLabel(I18N.get("UDG.color")); 
		
		colorChooser = new JButton("Colors"); 			 //button for choosing colors
		
		makeLayout(); 									 //function call for constructing the layout
	
	}
	
	/**
	 * Function for constructing the layout in this class
	 * The layout is a GridBagLayout.
	 */
	public void makeLayout() {
		
		  GridBagLayout gridbag = new GridBagLayout();
		  GridBagConstraints c = new GridBagConstraints();
		  this.getContentPane().setLayout( gridbag );
		  c.weightx = 3.0;
		  c.weighty = 0;
		  c.ipadx = 2;											//padding in x-axis 
		

		  // TextSize Label
		  c.gridx = 0;											//column 0
		  c.gridy = 0;											//row 0	
		  c.anchor = GridBagConstraints.EAST;						
		  gridbag.setConstraints( textSizeLabel, c );			//set Constraints
		  this.getContentPane().add( textSizeLabel );			//add to panel
		  
		  // Add TextSize ComboBox
		  c.gridx = 1;
		  c.anchor = GridBagConstraints.WEST;
		  gridbag.setConstraints( fontSizeBox, c );
		  this.getContentPane().add( fontSizeBox );
		  fontSizeBox.addActionListener(this); 
		  
		  // Add font Label
		  c.gridx = 0;
		  c.gridy = 1;
		  c.anchor = GridBagConstraints.EAST;
		  gridbag.setConstraints( textFontLabel, c );
		  this.getContentPane().add( textFontLabel );
		 
		  //add font dropdown
		  c.gridx = 1;
		  c.anchor = GridBagConstraints.WEST;
		  gridbag.setConstraints( fontBox, c );
		  this.getContentPane().add( fontBox );
		  fontBox.addActionListener(this); 

		  
		  // Add Color swatches! 
		  c.gridx = 0;
		  c.gridy = 2;
		  c.anchor = GridBagConstraints.EAST;
		  gridbag.setConstraints( textColorLabel, c );
		  this.getContentPane().add( textColorLabel );
		 
		  c.gridx = 1;
		  c.anchor = GridBagConstraints.WEST;
		  gridbag.setConstraints( colorChooser, c );
		  this.getContentPane().add( colorChooser );
		  colorChooser.addActionListener(this); 
		 
		 
		pack(); 
		setSize(new Dimension(320, 150)); 
	    setVisible(true); 
		
	}
	
	
	/*	Function for handling events performed by clicking objects such as fontSizes, FontTypes and colors E.G
	 * (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent ae) {
		
		if(ae.getSource().equals(colorChooser)) {
			
			//showing the colordialog
			color = JColorChooser.showDialog(UserDefinedGUI.this, languages.get("UDG.ChangeBGColor") , colorChooser.getForeground()); 
			
			//put the color to the prefs
			if(color != null) 
				prefs.putInt("foregroundColor", color.getRGB()); 
		}
		
		if(ae.getSource().equals(fontSizeBox)) {

			 //put the fontsize to the prefs
			 prefs.putInt("fontSize", Integer.parseInt(fontSizeBox.getSelectedItem().toString()));
		}
		
		if(ae.getSource().equals(fontBox)) {
			
			//put the textype to the prefs
			String textType = fontBox.getSelectedItem().toString(); 
			prefs.put("fontType", textType); 
		}
	}
}
