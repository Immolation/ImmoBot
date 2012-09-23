package com.immo.commands;

import com.immo.bot.ImmoBot;
import com.immo.bot.Preferences;

public class SetCommand extends Command {

	public SetCommand(String name) {
		super(name);
		
	}

	@Override
	public void handle(ImmoBot bot, Preferences pref, String channel,
			String user, String mask, String... args) {
		if(this.hasValidAccess(pref, user, mask)) {
			//Probably Admin Only
			if(args.length < 3) {
				//There needs to be three or more args
				//Format >set <commandNames> <owner, white, normal, off>
				return;
			}
			else {
				Command.Status status = Command.stringToStatus(args[args.length-1]);
				for(int i=1;i<(args.length-1);i++) {
					bot.setCommandStatus(args[i], status);
					
				}
			}
		}
		
	}

}
