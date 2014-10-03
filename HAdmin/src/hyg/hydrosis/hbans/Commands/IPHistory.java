package hyg.hydrosis.hbans.Commands;

import hyg.hydrosis.hbans.hBans;
import hyg.hydrosis.hbans.util.Message;
import hyg.hydrosis.hbans.util.db.mysql.MySQL;

import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class IPHistory implements CommandExecutor{
	
	private hBans plugin = hBans.getInstance();
	private MySQL db = plugin.db;
	/**
	 * ban Player Time[m|h|d] reason
	 * blank time = perm ban
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("iphistory"))
		{
			if(args.length!=1)
			{
				Message.iphistoryHelp(sender);
				return true;
			}
			try{
				String query = "SELECT DISTINCT IP FROM " + plugin.IPHistoryTable + " NATURAL JOIN " + plugin.NameHistoryTable + " WHERE name = '" + args[0] + "';";
				ResultSet rs = db.querySQL(query);
				if(!rs.first())
				{
					sender.sendMessage(Message.playerDoesNotExist(args[0]));
					return true;
				}
				Set<String> ipList = new HashSet<String>();
				do{
					ipList.add(rs.getString("IP"));
				}
				while(rs.next());
				sender.sendMessage(Message.ipList(args[0], ipList));
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
