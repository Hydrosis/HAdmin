package hyg.hydrosis.hbans.Commands;

import hyg.hydrosis.hbans.hBans;
import hyg.hydrosis.hbans.util.Message;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Kick implements CommandExecutor{
	private hBans plugin = hBans.getInstance();
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("kick"))
		{
			if(args.length==1)
			{
				for(Player p : plugin.getServer().getOnlinePlayers())
				{
					if(p.getName().equalsIgnoreCase(args[0]))
					{
						p.kickPlayer(Message.kick(null));
						plugin.getServer().broadcastMessage(Message.broadcastKick(args[0],null));
						return true;
					}
					sender.sendMessage(Message.playerNotOnline(args[0]));
				}
			}
			else if(args.length>1)
			{
				for(Player p : plugin.getServer().getOnlinePlayers())
				{
					if(p.getName().equalsIgnoreCase(args[0]))
					{
						String reason = " ";
						for(int i = 1; i<args.length; i++)
						{
							reason+=args[i]+" ";
						}
						p.kickPlayer(Message.kick(reason));
						plugin.getServer().broadcastMessage(Message.broadcastKick(args[0],reason));
					}
				}
			}
			else
			{
				Message.kickHelp(sender);
			}
		}
		return true;
	}
}
