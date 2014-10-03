package hyg.hydrosis.hbans.Commands;

import hyg.hydrosis.hbans.util.Message;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HyGAdminHelp implements CommandExecutor{
	
	private final int pages = 2;
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("hadmin"))
		{
			int page;
			if(args.length==0)
				page = 1;
			else
			{
				try{
					page = Integer.parseInt(args[0]);
				}
				catch(NumberFormatException e)
				{
					page = 1;
				}
			}
			if(page==1 || page<1 || page > pages)
			{
				Message.helpHeader(sender);
				Message.banHelp(sender);
				Message.unbanHelp(sender);
				Message.banStatusHelp(sender);
				Message.banipHelp(sender);
				Message.unbanipHelp(sender);
				sender.sendMessage(ChatColor.GOLD+"Page 1 of 2");
			}
			else
			{
				Message.kickHelp(sender);
				Message.muteHelp(sender);
				Message.unmuteHelp(sender);
				Message.muteStatusHelp(sender);
				Message.iphistoryHelp(sender);
				Message.playerhistoryHelp(sender);
				sender.sendMessage(ChatColor.GOLD+"Page 2 of 2");
			}
		}
		return true;
	}

}
