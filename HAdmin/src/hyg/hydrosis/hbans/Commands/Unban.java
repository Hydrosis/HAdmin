package hyg.hydrosis.hbans.Commands;

import hyg.hydrosis.hbans.hBans;
import hyg.hydrosis.hbans.util.Message;
import hyg.hydrosis.hbans.util.db.mysql.MySQL;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Unban implements CommandExecutor{
	private hBans plugin = hBans.getInstance();
	private MySQL db = plugin.db;
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("unban"))
		{
			if(args[0]==null)
			{
				Message.unbanHelp(sender);
				return true;
			}
			if(args.length>1)
			{
				sender.sendMessage(Message.validName());
				return true;
			}
			String query = "SELECT UUID FROM " + plugin.BansTable + " NATURAL JOIN " + plugin.NameHistoryTable + " WHERE Name = \"" + args[0] + "\";";
			ResultSet rs = db.querySQL(query);
			String uuid = null;
			try 
			{
				rs.first();
				uuid = rs.getString("UUID");
			} catch (SQLException e) 
			{
				sender.sendMessage("That person is not banned!");
				return true;
			}
			query = "DELETE FROM "+plugin.BansTable+" WHERE UUID = \"" + uuid+ "\";";
			sender.sendMessage(Message.playerUnbanned(args[0]));
			db.updateSQL(query);
			}
		return true;
	}

}
