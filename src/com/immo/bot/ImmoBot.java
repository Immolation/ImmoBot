package com.immo.bot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;

import com.immo.commands.ChannelsCommand;
import com.immo.commands.Command;
import com.immo.commands.LinkCommand;
import com.immo.commands.ListCommand;
import com.immo.commands.NickCommand;
import com.immo.commands.PublicCommand;
import com.immo.commands.QuitCommand;
import com.immo.commands.SetCommand;
import com.immo.commands.StatusCommand;
import com.immo.commands.UserCommand;

public class ImmoBot extends PircBot {
	
	final static String PROPERTY_FILE_NAME = "immo.prefs";
	final static String PROPERTY_NICKNAMES = "nicks";
	final static String PROPERTY_NETWORK = "network";
	final static String PROPERTY_CHANNELS = "channels";
	final static String PROPERTY_OWNER_MASK = "owner";
	final static String PROPERTY_ID_PASS = "password";
	final static String PROPERTY_WHITELIST = "white";
	final static String PROPERTY_BLACKLIST = "black";
	final static String PROPERTY_PUBLIC = "public";
	
	// Error Codes
	// 100 - 199: Preference Errors
	// 200 - 299: Network Errors
	// 300 - 399: Command Errors
	
	//Bot Globals
	//Defaults used for writing the init preferences
	static String[] botNames = new String[]{"ImmoBot`", "ImmoBot_"};
	static String network = "irc.freenode.net";
	static String[] channels = new String[]{"#immobot"};
	static String ownerMask = "unaffiliated/immolation";
	static String password = "";
	static boolean publicEnabled = false;
	
	// Global Preferences.
	// Maintains White/Blacklists
	static Preferences pref;
	
	//Maps the name of the command to the respective class
	//Used to simplify the triggering of commands
	Map<String, Command> commands;
	
	static Logger l;
	
	private char priv = '>';
	private char pub = '@';
	
	public ImmoBot() {
		
		l = Logger.getLogger("com.immo.bot");
		loadProperties();
		loadCommands();
		int i = 0;
		boolean passes = false;
		
		while(!passes) {
			passes = true;
			this.setName(botNames[i]);
			try {
				this.connect(network);
			} catch (NickAlreadyInUseException e) {
				passes = false;
				i++;
				if(i >= botNames.length) {
					//Name attempts exceeded. Closing
					System.err.println("Error 202: Bot name list exhausted due to" +
							"nick collision.");
					System.exit(202);
				}
			} catch (IOException e) {
				System.err.println("Error 200: " + e.getMessage());
				e.printStackTrace();
				System.exit(200);
			} catch (IrcException e) {
				System.err.println("Error 201: " + e.getMessage());
				System.exit(201);
			}
		}
		//If we get to here. The bot is connected.
		if(!password.equals("")) {
			this.identify(password);
		}
		
		for(String s: channels) {
			this.joinChannel(s);
		}
		
		
	}
	
	/*
	 * Loading of commands.
	 * Responsible for the creating of the command map, and the definition of triggers and corresponding classes
	 */
	private void loadCommands() {
		commands = new HashMap<String, Command>();
		commands.put("quit", new QuitCommand("quit"));
		commands.put("set", new SetCommand("set"));
		commands.put("list", new ListCommand("list"));
		commands.put("status", new StatusCommand("status"));
		commands.put("channel", new ChannelsCommand("channel"));
		commands.put("nick", new NickCommand("nick"));
		commands.put("public", new PublicCommand("public"));
		commands.put("link", new LinkCommand("link"));
		commands.put("user", new UserCommand("user"));
		
	}

	/*
	 * Enable or disable public commands
	 * @args enabled - Whether the public should be enabled or not. on if true, off if false
	 */
	public void setPublic(boolean enabled) {
		this.publicEnabled = enabled;
	}
	
	public boolean setCommandPublic(String name, boolean isPublic) {
		if(commands.containsKey(name)) {
			commands.get(name).setPublic(isPublic);
			return true;
		}
		return false;
	}
	
	
	@Override
	protected void onMessage(String channel, String sender, String login, String hostname, String message) {
		String[] messageArgs = message.split(" ");
		if(message.startsWith(">>")) {
			commands.get("user").handle(this, pref, channel, sender, hostname, messageArgs);
		}
		
		if(message.charAt(0) == priv || message.charAt(0) == pub ) {
			//Potential Command
			
			String commandName = messageArgs[0].substring(1);
			if(commands.containsKey(commandName)) {
				l.log(Level.INFO, (String.format("Received Command [%s] from %s in channel %s", commandName, sender, channel)));
				commands.get(commandName).handle(this, pref, channel, sender, hostname, messageArgs);
			}
		}
	}
	
	/*
	 * Loads the Properties from the given files and does some sanity checking
	 * Looks for the default fileName. TODO: Add -prefs flag to define where to look
	 * 
	 * Sanity Checks (Causing System exits due to required information):
	 * 	- Checks that there are nicknames for the bot to be called
	 * 	- Checks the network isn't undefined (Valid network checked upon connect attempt)
	 *  
	 *  Warning Checks (The bot will still run, as these aren't required)
	 *   - Warns if the owner mask is undefined TODO: Basic Validity check, i.e *~*@*
	 *   - Warns if there are no channels set on connect (Although owner can request bot to join channels)
	 */
	private void loadProperties() {
		File f = new File(PROPERTY_FILE_NAME);
		Properties prop = new Properties();
		try {
			prop.loadFromXML(new FileInputStream(f));
			String temp = prop.getProperty(PROPERTY_NICKNAMES);
			temp = temp.substring(1, temp.length()-1);
			
			botNames = temp.split(",");
			
			network = prop.getProperty(PROPERTY_NETWORK);
			temp = prop.getProperty(PROPERTY_CHANNELS);
			temp = temp.substring(1, temp.length()-1);
			channels = temp.split(",");
			ownerMask = prop.getProperty(PROPERTY_OWNER_MASK);
			password = prop.getProperty(PROPERTY_ID_PASS);
			String white = prop.getProperty(PROPERTY_WHITELIST);
			white = white.substring(1, white.length()-1);
			String[] whiteArr = white.split(",");
			String black = prop.getProperty(PROPERTY_BLACKLIST);
			black = black.substring(1, black.length()-1);
			String[] blackArr = black.split(",");
			HashSet<String> whitelist = new HashSet<String>();
			HashSet<String> blackList = new HashSet<String>();
			
			String publicEnabled = prop.getProperty(PROPERTY_PUBLIC);
			this.publicEnabled = Boolean.parseBoolean(publicEnabled);
			for(String s: whiteArr) {
				if(!s.trim().equals("")) {
				whitelist.add(s);
				}
			}
			for(String s: blackArr) {
				if(!s.trim().equals("")) {
				blackList.add(s);
				}
			}
			pref = new Preferences(whitelist,blackList, ownerMask);
			// Sanity Checking here
			if(botNames.length == 0) {
				//Not Valid bot names found
				System.err.println("Error 112: No Bot names defined.");
				System.exit(112);
			}
			if(network.equals("")) {
				System.err.println("Error 113: No Network defined in Properties.");
				System.exit(113);
			}
			if(channels.length == 0) {
				System.err.println("Warning 114: No Channels defined to connect. Continuing connect to server");
			}
			if(ownerMask.equals("")) {
				System.err.println("Warning 115: No Owner Mask defined. Bot will continue to connect");
			}
		} catch (InvalidPropertiesFormatException e) {
			System.err.println("Error 111: Property file is corrupt, " +
					"or contains invalid entries. Please check the file " +
					"or run with -init to generate new Properties");
			System.err.println(e.getMessage());
			System.exit(111);
		} catch (FileNotFoundException e) {
			System.err.println("Error 110: Property File does not exist. Please run with -init");
			System.exit(110);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		} 
	}
	
	/*
	 * Generates a  Properties file. Required by the bot to run
	 * The Properties file stores the settings the bot will use when running. 
	 * 
	 * Generates a warning message that the file exists, but will overwrite
	 * TODO: Contemplate this behaviour and decide whether it should be a warning or an error, requiring a Sys.Exit
	 * 
	 * Generates:
	 * 		- Nicks to Use. Default - {"ImmoBot", "ImmoBot'"} 
	 * 				- NB: CSV String
	 * 		- Network to Join. Default - "irc.freenode.net"
	 * 		- Channels to Join. Default - "#rswiki" 
	 * 				- NB: CSV String
	 * 		- Owner Mask - Default - "@unaffiliated/immolation" 
	 * 		- Password. Default - ""
	 * 				- NB: If left blank. No Attempt to identify will be performed.
	 * 				- This could lead to unexpected name changes if the nick selected
	 * 				- Has name-protection enabled. 
	 * 		- White List. Default - <Empty>
	 * 			- Used to control access of whitelisted commands
	 * 		- Black List. Default - <Empty>
	 * 			-Used to control users/masks who are forbidden to use the bots commands
	 */
	public static void writeProperties() {
		System.out.println("Writing Bot Properties");
		File f = new File(PROPERTY_FILE_NAME);
		
		Properties prop = new Properties();
		prop.setProperty(PROPERTY_NICKNAMES, Arrays.toString(botNames));
		prop.setProperty(PROPERTY_NETWORK, network);
		prop.setProperty(PROPERTY_CHANNELS, Arrays.toString(channels));
		prop.setProperty(PROPERTY_OWNER_MASK, ownerMask);
		prop.setProperty(PROPERTY_ID_PASS, password);
		prop.setProperty(PROPERTY_WHITELIST, pref.getList(true));
		prop.setProperty(PROPERTY_BLACKLIST, pref.getList(false));
		prop.setProperty(PROPERTY_PUBLIC, (publicEnabled)?"true":"false");
		try {
			prop.storeToXML(new FileOutputStream(f), "");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		if(args.length != 0) {
			for(int i=0;i<args.length;i++) {
				if(args[i].equals("-init")) {
					writeProperties();
				}
			}
		}
		
		new ImmoBot();
	}

	public void setCommandStatus(String name, Command.Status status) {
		if(commands.containsKey(name)) {
			commands.get(name).setStatus(status);
			l.log(Level.INFO, "Changing Status of " + name + " to: " + Command.statusToString(status));
		}
		
	}
	
	public void changeName(String nick) {
		this.changeNick(nick);
	}
	
	public String getCommandStatus(String name) {
		if(commands.containsKey(name)) {
			String str = "User Permission: " + commands.get(name).getCurrentStatus() + ". Public Enabled: " + commands.get(name).isPublicEnabled();
			return str;
		} else {
			return "";
		}
	}
	
	public void outputResponse(String first, boolean isPublic, String user, String mask, String channel, String message) {
		char charAt = first.charAt(0);
		boolean requestedPublic = charAt == pub; //Determine whether the user requested public.
		
		//Owner requesting public over-rides all individual commands
		if(requestedPublic && pref.matchesOwnerMask(mask)) {
			sendMessage(channel, message);
			return;
		}
		if(!this.publicEnabled || !isPublic) {
			//If public is disabled, or the command itself is set to private
			//Send a notice to the user
			sendNotice(user, message);
			return;
		}
		
		//If requested public. Then send the message to the requested channel
		if(requestedPublic) {
			sendMessage(channel, message);
		} else {
			//Otherwise, just notice the user
			sendNotice(user, message);
		}
		
	}
	
}
