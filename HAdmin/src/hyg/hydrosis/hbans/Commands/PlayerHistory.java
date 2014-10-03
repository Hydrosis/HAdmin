package hyg.hydrosis.hbans.Commands;

import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

import hyg.hydrosis.hbans.hBans;
import hyg.hydrosis.hbans.util.Message;
import hyg.hydrosis.hbans.util.db.mysql.MySQL;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class PlayerHistory implements CommandExecutor{
	private hBans plugin = hBans.getInstance();
	private MySQL db = plugin.db;
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("playerhistory"))
		{
			if(args.length!=1)
			{
				Message.playerhistoryHelp(sender);
				return true;
			}
			try
			{
				String query = "SELECT DISTINCT name FROM " + plugin.IPHistoryTable + " NATURAL JOIN " + plugin.NameHistoryTable + " WHERE IP = '"+args[0]+"';";
				ResultSet rs = db.querySQL(query);
				if(!rs.first())
				{
					sender.sendMessage(Message.IPDoesNotExist(args[0]));
					return true;
				}
				Set<String> playerList = new HashSet<String>();
				do{
					playerList.add(rs.getString("name"));
				}
				while(rs.next());
				sender.sendMessage(Message.nameList(args[0], playerList));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				sender.sendMessage("An error has occured...");
			}
		}
		return true;
	}

}
