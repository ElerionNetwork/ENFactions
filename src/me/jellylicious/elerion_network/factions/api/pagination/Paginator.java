package me.jellylicious.elerion_network.factions.api.pagination;

import java.util.HashMap;

import me.jellylicious.elerion_network.factions.Core;

public class Paginator {

	enum MessageStyle {
		RICHEST_PLAYERS_ECONOMY, HELP_MESSAGE_COMMAND_LIST; 
	}
	
	Core core;
	public Paginator(Core core) {
		this.core = core;
	}
	
	public String[] paginate(MessageStyle messageStyle, String startingMessage, int resultsPerPage, HashMap<String, String> commandsAndDescriptions) {
		String[] messages = null;
		if(startingMessage != null) {
			messages = new String[resultsPerPage+1];
			messages[0] = startingMessage;
		}else messages = new String[resultsPerPage];
		if(messageStyle == MessageStyle.RICHEST_PLAYERS_ECONOMY) {
			return messages;
		}else if(messageStyle == MessageStyle.HELP_MESSAGE_COMMAND_LIST) {
			for(int i = 1; i <= commandsAndDescriptions.size(); i++) {
				if(!(i > resultsPerPage)) {
					for(String command : commandsAndDescriptions.keySet()) {
						messages[i] = formHelpMessageCommand(command, commandsAndDescriptions.get(command));
					}
				}
			}
			return messages;
		}
		return messages;
	}
	
	private String formHelpMessageCommand(String commandUsage, String description) {
		return "§e" + commandUsage + " §8- §7" + description + ".";
	}

}
