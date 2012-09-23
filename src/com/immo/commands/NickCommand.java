package com.immo.commands;

import com.immo.bot.ImmoBot;
import com.immo.bot.Preferences;

public class NickCommand extends Command {

	public NickCommand(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handle(ImmoBot bot, Preferences pref, String channel,
			String user, String mask, String... args) {
		
		if(hasValidAccess(pref, user, mask) && args.length > 1) {
			bot.changeName(args[1]);
		}
		
	}

}
