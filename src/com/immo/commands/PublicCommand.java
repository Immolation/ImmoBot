package com.immo.commands;

import com.immo.bot.ImmoBot;
import com.immo.bot.Preferences;

public class PublicCommand extends Command {

	public PublicCommand(String name) {
		super(name);
	}

	@Override
	public void handle(ImmoBot bot, Preferences pref, String channel,
			String user, String mask, String... args) {

		if(args.length <3 ) {
			return;
		}
		boolean enable = args[2].equals("on")?true:false;
		if(args[1].equals("global")) {
			bot.setPublic(enable);
			bot.outputResponse(args[0], this.isPublic, user,mask,  channel, "Global public output set to: " + Boolean.toString(enable));
		}
		else {
			if(bot.setCommandPublic(args[1], enable) ) {
				bot.outputResponse(args[0], this.isPublic, user,mask,  channel, "[PUBLIC] Command: " + args[1] + "set to: " + Boolean.toString(enable));
			} else {
				bot.outputResponse(args[0], this.isPublic, user,mask,  channel, "No Such Command");
			}
		}
		
	}

}
