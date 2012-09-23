package com.immo.commands;

import com.immo.bot.ImmoBot;
import com.immo.bot.Preferences;

public class ChannelsCommand extends Command {

	public ChannelsCommand(String name) {
		super(name);
	}

	@Override
	/*
	 * Command Format: >channel [join|leave] <channel>
	 */
	public void handle(ImmoBot bot, Preferences pref, String channel,
			String user, String mask, String... args) {
		if(hasValidAccess(pref, user, mask) && args.length > 1) {
			if(args[1].equals("join") && args.length > 2) {
				bot.joinChannel(args[2]);
				bot.sendMessage(args[2], "Requested by: " + user);
			} else if(args[1].equals("leave")) {
				if(args.length == 2) {
					//Then leave channel command was sent from
					bot.partChannel(channel);
				} else if(args.length > 2) {
					bot.partChannel(args[2]);
				}
			}
			
		}
		
	}

}
