package com.immo.bot;

import java.util.HashSet;
import java.util.Properties;

public class Preferences {

	
	/*
	 * Global Whitelist.
	 * Stores a list of usernames or masks that are able to access whitelisted commands
	 * 
	 */
	private HashSet<String> white = new HashSet<String>();
	
	/*
	 * Global Blacklist.
	 * 
	 * Stores a list of names/masks unabled to access any bot commands
	 */
	private HashSet<String> black = new HashSet<String>();;
	
	
	/*
	 * Hostmask of the bot owner.
	 * Used to allow the bot owner to change statuses of the commands, etc.
	 */
	private String ownerMask;
	
	public Preferences(HashSet<String> white, HashSet<String> black, String ownerMask) {
		this.white = white;
		this.black = black;
		this.ownerMask = ownerMask;
	}
	
	public boolean userOnWhiteList(String user) {
		return white.contains(user);
	}
	
	public boolean userOnBlackList(String user) {
		return black.contains(user);
	}
	
	/*
	 * Adds a user to the given commands whitelist
	 * 
	 */
	public boolean addToWhiteList(String user) {
		return white.add(user);
	}
	
	public boolean addToBlackList(String user) {
		return black.add(user);
	}
	
	public boolean removeFromWhiteList(String user) {
		return white.remove(user);
	}
	
	public boolean removeFromBlackList(String user) {
		return black.remove(user);
	}
	public Properties getProperties() {
		Properties prop = new Properties();
		prop.setProperty("white", white.toString());
		prop.setProperty("black", black.toString());
		
		return prop;
		
	}
	
	public boolean matchesOwnerMask(String mask) {
		return this.ownerMask.equals(mask);
	}
	/*
	 * Clears either the global white or blacklist
	 * Simpler than removing everyone
	 * 
	 * @arg isWhite - true is the whitelist is to be cleared, otherwise clear the blacklist.
	 */
	public void clearList(boolean isWhite) {
		if(isWhite) {
			white.clear();
		} else {
			black.clear();
		}
		
	}
	/*
	 * Generates the string representation of the global lists
	 * Used for Generating channel lists, or to be written out to preferences
	 * 
	 * @arg isWhite - Whether the list requested is the whitelist or not
	 * @return String representation of the requested list
	 */
	public String getList(boolean isWhite) {
		if(isWhite) {
			return white.toString();
		} else {
			return black.toString();
		}
	}
	
	
}
