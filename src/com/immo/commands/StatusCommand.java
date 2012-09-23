package com.immo.commands;

import com.immo.bot.ImmoBot;
import com.immo.bot.Preferences;

public class StatusCommand extends Command {

	public StatusCommand(String name) {
		super(name);
	}

	@Override
	public void handle(ImmoBot bot, Preferences pref, String channel,
			String user, String mask, String... args) {
		if(args.length == 1) {
			return;
		} else {
			String response = bot.getCommandStatus(args[1]);
			if(!response.equals("")) {
				bot.outputResponse(args[0], this.isPublic, user,mask,  channel, String.format("[STATUS] - Command [%s] - %s",args[1], response));
			//bot.sendMessage(channel, String.format("[STATUS] - Command [%s] is set to %s",args[1], response)); 
			}
		}
		
	}

}
