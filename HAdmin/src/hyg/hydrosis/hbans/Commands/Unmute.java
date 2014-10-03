package hyg.hydrosis.hbans.Commands;

import hyg.hydrosis.hbans.hBans;
import hyg.hydrosis.hbans.util.Message;
import hyg.hydrosis.hbans.util.db.mysql.MySQL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Unmute implements CommandExecutor{
	
	private hBans plugin = hBans.getInstance();
	private MySQL db = plugin.db;
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("unmute"))
		{
			if(args[0]==null)
			{
				Message.unmuteHelp(sender);
				return false;
			}
			if(args.length>1)
			{
				sender.sendMessage(Message.validName());
				return false;
			}
			String query = "SELECT UUID FROM " + plugin.MuteTable + " NATURAL JOIN " + plugin.NameHistoryTable + " WHERE Name = \"" + args[0] + "\";";
			ResultSet rs = db.querySQL(query);
			boolean isMuted = true;
			String uuid = null;
			try {
				isMuted = rs.first();
				uuid = rs.getString("UUID");
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			if(!isMuted)
			{
				sender.sendMessage("That person is not muted!");
			}
			else
			{
				plugin.muted.remove(uuid);
				query = "DELETE FROM "+plugin.MuteTable+" WHERE UUID = \"" + uuid+ "\";";
				db.updateSQL(query);
				sender.sendMessage(Message.playerUnmuted(args[0], uuid, plugin));
				plugin.getServer().getPlayer(UUID.fromString(uuid)).sendMessage(Message.notifyPlayerUnmuted(sender.getName()));
			}
		}
		return true;
	}

}
