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

public class LinkCommand extends Command {

	private final String extraFile = "linksStore.pref";
	
	private Map<String, String> link;
	Properties p = new Properties();
	public LinkCommand(String name) {
		super(name);
		loadLinks();
		
	}

	private void generate() {
		File f = new File(PREF_DIR+extraFile);
		generate(f);
	}
	
	private void generate(File f) {
		String s = "";
		int i=0;
		for(Map.Entry<String, String> entry: link.entrySet()) {
			if(i!=0) {
				s+=",";
			}
			i++;
			s+=(entry.getKey()+"->"+entry.getValue());
		}
		p.setProperty("links", s);
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
	
	private void loadLinks() {
		File f = new File(PREF_DIR+extraFile);
		link = new HashMap<String, String>();
		if(!f.exists()) {
			generate(f);
		}
		try {
			p.loadFromXML(new FileInputStream(f));
			String[] links = p.getProperty("links").split(",");
			for(String s: links) {
				if(!s.trim().equals("")) {
					String[] str = s.split("->");
					link.put(str[0], str[1]);
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
		link.put(name, URL);
		generate();
	}
	
	private void removeLink(String name) {
		link.remove(name);
		generate();
	}
	
	@Override
	public void handle(ImmoBot bot, Preferences pref, String channel,
			String user, String mask, String... args) {
		if(args.length < 2) {
			return;
		}
		if(args[1].equals("add") && args.length ==4 && hasValidAccess(pref, user, mask)) {
			addLink(args[2], args[3]);
		} else if(args[1].equals("remove") && hasValidAccess(pref, user, mask)) {
			for(int i=2;i<args.length;i++) {
				removeLink(args[i]);
			}
		} else {
			if(link.containsKey(args[1])) {
				bot.outputResponse(args[0], this.isPublic, user, mask, channel, link.get(args[1]));
			}
		}
		
	}

}
