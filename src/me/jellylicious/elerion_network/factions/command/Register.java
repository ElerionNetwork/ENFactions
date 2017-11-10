package me.jellylicious.elerion_network.factions.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.jellylicious.elerion_network.factions.Core;

public class Register implements CommandExecutor {

	Core core;
	
	public Register(Core core) {
		this.core = core;
	}
	
	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(cs instanceof Player) {
			Player p = (Player) cs;
			if(args.length == 3) {
				String email = args[0];
				String password = args[1];
				String repeatedPassword = args[2];
				if(core.getEmailValidator().validate(email)) {
					if(core.getPasswordValidator().isStrongEnough(password)) {
						if(core.getPasswordValidator().passwordsMatch(password, repeatedPassword)) {
							if(!core.getPlayerAccount().hasPlayerAccount(p.getUniqueId().toString())) {
								try {
									repeatedPassword = core.getPasswordProtection().getSaltedHash(repeatedPassword);
								} catch (Exception e) {
									e.printStackTrace();
								}
								core.getPlayerAccount().createPlayerAccount(p.getUniqueId().toString(), email, repeatedPassword);
								p.sendMessage(core.getPrefix() + "§aYou've successfully registered your account. From now on, you can purchase items in our online shop, participate in forum discussions and so on!");
							}else{
								p.sendMessage(core.getPrefix() + "§cYou already have an account registered.");
							}
						}else{
							p.sendMessage(core.getPrefix() + "§cPlease, make sure that both passwords match.");
						}
					}else{
						p.sendMessage(core.getPrefix() + "§cYour password is not strong enough. It should be at least 8 characters long, should contain at least 1 uppercase and 1 lowercase letter and it should contain at least one number.");
					}
				}else{
					p.sendMessage(core.getPrefix() + "§cWe could not verify that your e-mail address exists. Please contact our developers immediately.");
				}
			}else{
				p.sendMessage(core.getPrefix() + "§cIncorrect usage. Please use /register <email> <password> <repeatPassword>!");
			}
		}else{
			
		}
		return true;
	}

}
