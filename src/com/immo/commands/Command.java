package com.immo.commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import com.immo.bot.ImmoBot;
import com.immo.bot.Preferences;

public abstract class Command {
	
	//Directory that contains the Commands
	static final String PREF_DIR = "commands/";
	
	// Bot Status 
	// Disabled - Accessible to no-one. Not even owner (Although owner can enable)
	// White List - Access to the Command is controlled by a White List (Only users who's name or mask can operate the command)
	// Normal - Permissable to be used by everyone who isnt blacklisted
	// Owner_Only - Access only by the owner. These will concern commands like status controls, i.e !set <repeat> DISABLED
	public static enum Status {DISABLED, WHITE_LIST, NORMAL, OWNER_ONLY}; 
	
	// The current status of each command.
	// By default, all commands are owner-only
	
	private Status currentStatus = Status.OWNER_ONLY; 
	
	//Command name. Used to find the prefs file.
	String name;
	
	//Whether the command is set to produce public chat, or just notice the user
	boolean isPublic;
	
	public Command(String name) {
		this.name = name;
		File f = new File(PREF_DIR+name);
		if(!f.exists()) {
			writeProperties();
		} else {
			loadProperties(f);
		}
		
	}
	
	public abstract void handle(ImmoBot bot, Preferences pref, String channel, String user, String mask, String... args);
	
	/*
	 * Tests whether a given user/mask have the permissions to access a given command
	 * 
	 *  - 	If the user is the owner, return true. The owner can use all commands, regardless of current status
	 *  - 	If the user is blacklisted, they are denied rights, even if the command is set to normal, or they are on the whitelist
	 *  - 	If the command is disabled, no-one is able to use it (Except owner, as dealt with above)
	 *  - 	If the command is set to normal, every non-blacklisted user is able to use it
	 *  -	If the command is whitelisted, check whitelist for access
	 *  - 	Otherwise the command is set to Owner only, and as they failed the earlier check, return false. 
	 *  		(Obviously not the owner)
	 */
	protected boolean hasValidAccess(Preferences pref, String user, String mask) {
		
		if(pref.matchesOwnerMask(mask)) {
			return true;
		}
		
		if(this.currentStatus == Status.DISABLED) {
			return false;
		}
		
		if(pref.userOnBlackList(user) || pref.userOnBlackList(mask)) {
			return false;
		}
		
		if(this.currentStatus == Status.NORMAL) {
			return true;
		}
		if(this.currentStatus == Status.WHITE_LIST) {
			return ((pref.userOnWhiteList(user)) || pref.userOnWhiteList(mask));
		}
		
		return false;
	}
	
	
	public void setStatus(String str) {
		this.currentStatus = Command.stringToStatus(str);
	}
	
	public void setStatus(Status stat) {
		this.currentStatus = stat;
	}
	
	public String getCurrentStatus() {
		return Command.statusToString(currentStatus);
	}
	
	/*
	 * Loads the properties from a file.
	 * 
	 * @args f - File to read the properties from
	 */
	private void loadProperties(File f) {
		Properties prop = new Properties();
		
		try {
			prop.loadFromXML(new FileInputStream(f));
			this.currentStatus = Command.stringToStatus(prop.getProperty("status"));
			this.isPublic = (prop.getProperty("public").equals("true"))?true:false;
		} catch (InvalidPropertiesFormatException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}


	/*
	 * Writes the Properties to a file.
	 * Used when the command is first created Or on any subsequent update. 
	 */
	private void writeProperties() {
		File f = new File(PREF_DIR+name);
		Properties prop = new Properties();
		prop.setProperty("status", Command.statusToString(currentStatus));
		prop.setProperty("public", (this.isPublic)? "true": "false");
		try {
			prop.storeToXML(new FileOutputStream(f), "");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Helper method that converts a given status into a String
	 * Used to generate the String used in the properties file
	 */
	public static String statusToString(Status stat) {
		if(stat.equals(Status.DISABLED)) {
			return "disabled";
		} else if(stat.equals(Status.NORMAL)) {
			return "normal";
		} else if(stat.equals(Status.WHITE_LIST)) {
			return "white";
		} else if(stat.equals(Status.OWNER_ONLY)) {
			return "owner";
		} else {
			return "";
		}
	}
	
	/*
	 * Helper method that converts a String to a status
	 * Used to load the status back in from the properties file
	 */
	public static Status stringToStatus(String str) {
		if(str.equals("owner")) {
			return Status.OWNER_ONLY;
		}  else if(str.equals("white")) {
			return Status.WHITE_LIST;
		} else if(str.equals("normal")) {
			return Status.NORMAL;
		} else {
			return Status.DISABLED;
		}
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
		this.writeProperties();
		
	}

	public boolean isPublicEnabled() {
		return this.isPublic;
	}
}
