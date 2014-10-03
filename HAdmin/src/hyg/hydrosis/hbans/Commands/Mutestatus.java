package hyg.hydrosis.hbans.Commands;

import hyg.hydrosis.hbans.hBans;
import hyg.hydrosis.hbans.util.Message;
import hyg.hydrosis.hbans.util.db.mysql.MySQL;

import java.sql.ResultSet;
import java.sql.Timestamp;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Mutestatus implements CommandExecutor{
	
	private hBans plugin = hBans.getInstance();
	private MySQL db = plugin.db;
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("mutestatus"))
		{
			if(args.length!=1)
			{
				Message.muteStatusHelp(sender);
				return true;
			}
			String query = "SELECT UUID FROM " + plugin.NameHistoryTable + " WHERE Name=\"" + args[0]+"\";";
			ResultSet rs = db.querySQL(query);
			try{
				if(!rs.first())
				{
					sender.sendMessage(Message.playerDoesNotExist(args[0]));
					return true;
				}
				String uuidString = rs.getString("UUID");
				query = "SELECT * FROM " + plugin.MuteTable + " WHERE UUID = \"" + uuidString + "\";";
				rs = db.querySQL(query);
				if(!rs.first())
				{
					sender.sendMessage(Message.playerNotMuted(args[0]));
					return true;
				}
				Timestamp time = rs.getTimestamp("Time");
				if(System.currentTimeMillis()>time.getTime() && time.getTime()!=0) //if mute has expired
				{
					query = "DELETE FROM "+plugin.MuteTable+" WHERE UUID = \"" + uuidString + "\";";
					db.updateSQL(query);
					plugin.muted.remove(uuidString);
					sender.sendMessage(Message.playerNotMuted(args[0]));
					return true;
				}
				String reason = rs.getString("Reason");
				String admin = rs.getString("Admin");
				query = "SELECT Name FROM " + plugin.NameHistoryTable + " WHERE UUID = '" + admin+"';";
				rs = db.querySQL(query);
				rs.first();
				admin = rs.getString("Name");
				sender.sendMessage(Message.banInfo(args[0], reason, time, admin));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}


}
