package hyg.hydrosis.hbans.Commands;

import hyg.hydrosis.hbans.hBans;
import hyg.hydrosis.hbans.util.Message;
import hyg.hydrosis.hbans.util.db.mysql.MySQL;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class UnbanIP implements CommandExecutor{
	private hBans plugin = hBans.getInstance();
	private MySQL db = plugin.db;
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("unbanip"))
		{
			if(args.length!=1)
			{
				Message.unbanipHelp(sender);
				return true;
			}
			String query = "SELECT IP FROM "+ plugin.IPBansTable + " WHERE IP = \"" + args[0] + "\";";
			ResultSet rs = db.querySQL(query);
			boolean isBanned = true;
			try {
				isBanned = rs.first();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			if(!isBanned)
			{
				sender.sendMessage(Message.ipNotBanned());
			}
			else
			{
				query = "DELETE FROM "+plugin.IPBansTable+" WHERE IP = \"" + args[0]+ "\";";
				sender.sendMessage(Message.playerUnbanned(args[0]));
				db.updateSQL(query);
			}
		}
		return true;
	}
}
