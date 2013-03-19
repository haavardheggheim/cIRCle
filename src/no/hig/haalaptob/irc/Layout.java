package no.hig.haalaptob.irc;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import no.hig.haalaptob.I18N;

/**
 * Class that contains the main layout of the program,
 * such as Toolbars, menus, buttons and so on. 
 *  
 * @author Tobias, Lap and Håvard
 * @version 1.5
 * @see javax.swing.JFrame
 */

public class Layout extends JFrame implements ActionListener {
	
	private static final long serialVersionUID = 1L;

	public ServerFileRead read;				//for reading the sorted servers
	private LoginGUI logingui;				//Login to server GUI
	private ConView ConnectionWindow;		//connect to server
	private Prefs prefs = Prefs.getPrefs();	//fetches preferences
	
	private UserDefinedGUI udg; 			//used to define userdefined layout
	private JPanel login = new JPanel();	
	
	
	private JInternalFrame LoginWindow = new JInternalFrame("Login", true,true,true,true);
	
	public MyDesktopPane DesktopPane = new MyDesktopPane();	//our own DesktopPane clas

	private JMenuBar menu = new JMenuBar();			
	private JMenu windowMenu, fileMenu, editMenu, helpMenu;
	private JMenuItem editFontInformation, help, quit, about, joinChannel; 

	private JToolBar toolbar = new JToolBar("Toolbar", JToolBar.HORIZONTAL); 

    private JButton connect, join, colorButton, helpButton, quitServer, channelList; 
	WindowBar windowbar = new WindowBar();
	
   
    /**
     * Constructor for this class, the constructor
     * is responsible for setting the look and feel,
     * initialize the toolbar, loginwindow
     * 
     */
	public Layout() {
		
		super("cIRCle - Java IRC Client");
		setLookAndFeel();
		initToolBar(); 
	
		read = new ServerFileRead();
		
		logingui = new LoginGUI();
		logingui.serverFileReadObjectSync(read);
		logingui.makeLoginGUI();
		
		login.add(logingui);
		LoginWindow.add(login);
		LoginWindow.setVisible(true);
		LoginWindow.pack();
		DesktopPane.add(LoginWindow);
		
		add(DesktopPane);
		logingui.desktopPaneSync(DesktopPane);
		logingui.windowSync(LoginWindow);
		
		setSize(new Dimension(750, 600));
		initMenu();
		
		setDefaultCloseOperation(EXIT_ON_CLOSE); 
		setVisible(true);	
	}
	
	/**
	 * Function for setting the LookAndFeel of the program
	 * in this case it is the Nimbus LookAndFeel. 
	 */
	 public static void setLookAndFeel() {
		 try {
			    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {	//getting the installed look and feels
			        if ("Nimbus".equals(info.getName())) {						
			            UIManager.setLookAndFeel(info.getClassName());
			            break;
			        }
			    }
			} catch (UnsupportedLookAndFeelException e) {
			    System.out.println("ERROR: " + e.getMessage()); 
			} catch (ClassNotFoundException e) {
				 System.out.println("ERROR: " + e.getMessage());
			} catch (InstantiationException e) {
				 System.out.println("ERROR: " + e.getMessage());
			} catch (IllegalAccessException e) {
				 System.out.println("ERROR: " + e.getMessage());
			}
		  }
	 
	 

	 /**
	  * Function for initializing the menu with keystrokes and internationalization
	  */
	private void initMenu() {
		
		fileMenu = new JMenu(I18N.get("file"));
	    editMenu = new JMenu(I18N.get("edit"));
	    helpMenu = new JMenu(I18N.get("help"));
	    windowMenu = new JMenu(I18N.get("windows"));
	    
	    editMenu.add(editFontInformation = new JMenuItem(I18N.get("UDG.ChangeBGColor")));	//from the internationalization
	    editFontInformation.setAccelerator(KeyStroke.getKeyStroke('E', (int)(InputEvent.CTRL_DOWN_MASK))); //User press CTRL + E
	    editFontInformation.addActionListener(this); 	//add an actionListener
	    
		helpMenu.add(help = new JMenuItem(I18N.get("Help.menu"))); 
		help.setAccelerator(KeyStroke.getKeyStroke('H', (int)(InputEvent.CTRL_DOWN_MASK)));
		help.addActionListener(this); 
	    
		helpMenu.add(about = new JMenuItem(I18N.get("about.menu")));
		about.setAccelerator(KeyStroke.getKeyStroke('A', (int)(InputEvent.CTRL_DOWN_MASK))); 
		about.addActionListener(this); 
		
		fileMenu.add(quit = new JMenuItem(I18N.get("quit"))); 
		quit.setAccelerator(KeyStroke.getKeyStroke('Q', (int)(InputEvent.CTRL_DOWN_MASK))); 
		quit.addActionListener(this); 
		
		windowMenu.add(joinChannel = new JMenuItem(I18N.get("joinChannel"))); 
		joinChannel.setAccelerator(KeyStroke.getKeyStroke('J', (int)(InputEvent.CTRL_DOWN_MASK))); 
		joinChannel.addActionListener(this); 
		
		
		//add elements to the menu
	    menu.add(fileMenu);
	    menu.add(editMenu);
	    menu.add(windowMenu);
	    menu.add(helpMenu);	    
	        
	    setJMenuBar(menu);    	    
	}
	
	/**
	 * Initilizing the toolbar with icons from the resource folder
	 */
	
	public void initToolBar() {
		
		connect = new JButton(new ImageIcon(getClass().getResource("/images/connect.png"))); 
		connect.addActionListener(this);
		connect.setToolTipText(I18N.get("connect"));
		
		join = new JButton(new ImageIcon(getClass().getResource("/images/new_channel.png"))); 
		join.addActionListener(this);
		join.setToolTipText(I18N.get("joinChannel")); 
		
		colorButton = new JButton(new ImageIcon(getClass().getResource("/images/color.png")));
		colorButton.addActionListener(this);
		colorButton.setToolTipText(I18N.get("changeColors"));
		
		helpButton = new JButton(new ImageIcon(getClass().getResource("/images/help.png"))); 
		helpButton.addActionListener(this); 
		helpButton.setToolTipText(I18N.get("helpMenu")); 
	
		quitServer = new JButton(new ImageIcon(getClass().getResource("/images/logout.png"))); 
		quitServer.addActionListener(this); 
		quitServer.setToolTipText(I18N.get("quitServer")); 
		
		channelList = new JButton(new ImageIcon(getClass().getResource("/images/channels.png"))); 
		channelList.addActionListener(this); 
		channelList.setToolTipText(I18N.get("showChannelList")); 
		
		
		//adding elements to the toolbar
		toolbar.add(connect); 
		toolbar.add(quitServer);
		toolbar.addSeparator(); 
		toolbar.add(join); 
		toolbar.add(channelList); 
		toolbar.addSeparator(); 
		toolbar.add(colorButton);
		toolbar.add(helpButton); 
		getContentPane().add(toolbar, BorderLayout.NORTH); 
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 * ActionListener for everything in the Layout class
	 * Done this way, I think it looks more beautiful rather than
	 * making an actionlistener for every singel object. 
	 */
	public void actionPerformed(ActionEvent ae) {
		
		if(ae.getSource().equals(editFontInformation) || ae.getSource().equals(colorButton)) {
				udg = new UserDefinedGUI(this); 
		}
		
		
		if(ae.getSource().equals(help) || ae.getSource().equals(helpButton)) {
			
			Help localHelpWindow = null;
			
			try {
				localHelpWindow = new Help(Layout.class.getResource("/helpURLs/index.html"));	//getting the index from the projectfolder
			} catch (IOException e) { System.out.println(e.getMessage()); }
			
			/*
			 * Adding the window to the DesktopPane and move it to the front
			 */
	        Layout.this.DesktopPane.add(localHelpWindow);
	        localHelpWindow.moveToFront(); 
	        localHelpWindow.move(120, 50); 
	        localHelpWindow.setVisible(true);
		}
		
	
		if(ae.getSource().equals(about)) {
			 JOptionPane.showMessageDialog(this, I18N.get("about.text")); 
		}
		
		if(ae.getSource().equals(quit)) {
			System.exit(0); 
		}
		
		if(ae.getSource().equals(join) || ae.getSource().equals(joinChannel)) {
			

			String message = JOptionPane.showInputDialog(this, 
					 I18N.get("chooseChannel"), 
					 I18N.get("channelTitle"), 
					 JOptionPane.QUESTION_MESSAGE);
		 
			
			//check if the user didn't type anything just loop and show error dialog 
			while(message.equals("")) {
						JOptionPane.showMessageDialog(Layout.this,
					    I18N.get("channelError"),
					    "Error",
					    JOptionPane.ERROR_MESSAGE);

						message = JOptionPane.showInputDialog(this, 
										 I18N.get("chooseChannel"), 
										 I18N.get("channelTitle"), 
										 JOptionPane.QUESTION_MESSAGE);		
			}
			
			ConnectionWindow.guiChannelJoin("#"+message);
		}
		
		if(ae.getSource().equals(channelList)) {
			
			ConnectionWindow.guiChannelList(); 
			
		}
		
		if(ae.getSource().equals(connect)) {
			showLoginWindow();
		}
		
		if(ae.getSource().equals(quitServer)) {
			ConnectionWindow.guiQuitServer(); 
		}
	}
		
	
	/**
	 * connects and displays the connection view window
	 */
	public void connect() {
		LoginWindow.setVisible(false);
		LoginWindow.dispose();
	    ConnectionWindow = new ConView(I18N.get("ConView.title"));		//ConnectionView window
	    ConnectionWindow.syncWindowbar(windowbar);
	    ConnectionWindow.connect();										//initiates connection
	    ConnectionWindow.setName("ConnectionWindow");					//This is so the connectionwindow internalframe can stand out
	    DesktopPane.add(ConnectionWindow);								//adds view to main panel
	    ConnectionWindow.setVisible(true);
	    getContentPane().add(windowbar,BorderLayout.SOUTH);
	}
	
	/**
	 * Function for showing the login window when 
	 * a user press the "connect to server" button
	 */
	@SuppressWarnings("deprecation")
	public void showLoginWindow() {
		LoginWindow.setVisible(true);									//setting the loginwindow to visible
		LoginWindow.moveToFront();										//move it to the front
		LoginWindow.move(150, 50);
		DesktopPane.add(LoginWindow); 									//add it to the desktopPane. 
	}
	
	/**
	 * Function for removing the WindowBar/Statusbar from the program
	 */
	public void removeWindowBar(){
		getContentPane().remove(windowbar);
		windowbar = new WindowBar();
	}
}
