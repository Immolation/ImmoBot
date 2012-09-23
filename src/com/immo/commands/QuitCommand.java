package com.immo.commands;

import com.immo.bot.ImmoBot;
import com.immo.bot.Preferences;

public class QuitCommand extends Command {

	public QuitCommand(String name) {
		super(name);
		
	}

	@Override
	public void handle(ImmoBot bot, Preferences pref, String channel, String user, String mask, String... args) {
		if(this.hasValidAccess(pref, user, mask)) {

			bot.disconnect();
			System.exit(0);
		}
		
	}

}
