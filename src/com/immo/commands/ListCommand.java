package com.immo.commands;

import com.immo.bot.ImmoBot;
import com.immo.bot.Preferences;

public class ListCommand extends Command {

	public ListCommand(String name) {
		super(name);
		
	}

	/*
	 * Command is formatted as below
	 * >lists [white|black] [add|remove|clear] <Name/Mask>
	 * 
	 */
	public void handle(ImmoBot bot, Preferences pref, String channel,
			String user, String mask, String... args) {
		
		boolean isWhite = true;
		if(args[1].equals("black")) {
			isWhite = false;
		} else if(!args[1].equals("white")) {
			bot.outputResponse(args[0], this.isPublic, user,mask,  channel, "Expected black or white. Got: " + args[1]);
			return;
		}
		if(args.length == 2) {
			//Just print the list
			String prefix = (isWhite)? "White List: " : "Black List: ";
			bot.outputResponse(args[0], this.isPublic, user,mask,  channel, prefix + pref.getList(isWhite));
			//bot.sendMessage(channel, prefix + pref.getList(isWhite));
		}
		if(args.length < 3) {
			return;
		}
		else {
			if(args[2].equals("add")) {
				if(hasValidAccess(pref, user, mask) && args.length > 3) {
					if(isWhite) {
						pref.addToWhiteList(args[3]); 
						bot.outputResponse(args[0], this.isPublic, user, mask, channel, args[3] + " added to White List");
					}
					else {
						pref.addToBlackList(args[3]);
						bot.outputResponse(args[0], this.isPublic, user,mask,  channel, args[3] + " added to Black List");
						}
					bot.writeProperties();
				} else {
					
				}
			}
			else if(args[2].equals("remove")) {
				if(hasValidAccess(pref, user, mask) && args.length > 3) {
					if(isWhite) {
						pref.removeFromWhiteList(args[3]); 
						bot.outputResponse(args[0], this.isPublic, user,mask, channel, args[3] + " removed from White List");
					}
					else {
						pref.removeFromBlackList(args[3]); 
						bot.outputResponse(args[0], this.isPublic, user,mask,  channel, args[3] + " removed from Black List");
					}
					bot.writeProperties();
					
				}
				else {
					
				}
			}
			else if(args[2].equals("clear")) {
				if(hasValidAccess(pref, user, mask)) {
					pref.clearList(isWhite);
					bot.outputResponse(args[0], this.isPublic, user,mask,  channel, ((isWhite)?"White":"Black") + "List cleared" );
					bot.writeProperties();
				} else {
					
				}
			}
		}
		
	}

}
