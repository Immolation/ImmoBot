package com.immo.commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Properties;

import com.immo.bot.ImmoBot;
import com.immo.bot.Preferences;

public class UserCommand extends Command {

	/*
	 * Used to generate responses.
	 * Can use argument substitution instead
	 * i.e $1 will be substituted with the first argument if it exists
	 */
	
	private final String savedCommands = "savedcommands.pref";
	private Map<String, String> commands;
	Properties p = new Properties();
	
	public UserCommand(String name) {
		super(name);
		loadCommands();
	}

	
	@Override
	public void handle(ImmoBot bot, Preferences pref, String channel, String user, String mask, String... args) {
		if(args.length >= 2) 
		
	}

	private void generate() {
		File f = new File(PREF_DIR+savedCommands);
		generate(f);
	}
	
	private void generate(File f) {
		String s = "";
		int i=0;
		for(Map.Entry<String, String> entry: commands.entrySet()) {
			if(i!=0) {
				s+=",";
			}
			i++;
			s+=(entry.getKey()+"->"+entry.getValue());
		}
		p.setProperty("commands", s);
		try {
			p.storeToXML(new FileOutputStream(f), "");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void loadCommands() {
		File f = new File(PREF_DIR+savedCommands);
		commands = new HashMap<String, String>();
		if(!f.exists()) {
			generate(f);
		}
		try {
			p.loadFromXML(new FileInputStream(f));
			String[] links = p.getProperty("commands").split(",");
			for(String s: links) {
				if(!s.trim().equals("")) {
					String[] str = s.split("->");
					commands.put(str[0], str[1]);
				}
			}
		} catch (InvalidPropertiesFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	private void addLink(String name, String URL) {
		commands.put(name, URL);
		generate();
	}
	
	private void removeLink(String name) {
		commands.remove(name);
		generate();
	}
	
}
