package hyg.hydrosis.hbans.Commands;

import hyg.hydrosis.hbans.hBans;
import hyg.hydrosis.hbans.util.Actions;
import hyg.hydrosis.hbans.util.Message;
import hyg.hydrosis.hbans.util.UUIDFetcher;
import hyg.hydrosis.hbans.util.db.mysql.MySQL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Ban implements CommandExecutor{
	
	private hBans plugin = hBans.getInstance();
	private MySQL db = plugin.db;
	/**
	 * ban Player Time[m|h|d] reason
	 * blank time = perm ban
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("ban"))
		{
			if(args.length<3)
			{
				Message.banHelp(sender);
				return true;
			}
			String query = "SELECT UUID FROM " + plugin.NameHistoryTable + " WHERE Name=\""+args[0]+"\"";
			ResultSet rs = db.querySQL(query);
			String uuid = null;
			try {
				rs.first();
				uuid = rs.getString(1);
			} catch (SQLException e) {
				sender.sendMessage("That player does not exist!");
			}
			long banTime = System.currentTimeMillis();
			if(args[1]!=null)
			{
				if(args[1].contains("m") ^ args[1].contains("h") ^ args[1].contains("d"))
				{
					String timeInput = "";
					int type = 0;
					if(args[1].contains("m"))
					{
						timeInput = (args[1].split("m"))[0];
						type = 1000*60;
					}
					else if(args[1].contains("h"))
					{
						timeInput = (args[1].split("h"))[0];
						type = 1000*60*60;
					}
					else if(args[1].contains("d"))
					{
						timeInput = (args[1].split("d"))[0];
						type = 1000*60*60*24;
					}
					double timeInt = 0.0;
					try
					{
						timeInt = Double.parseDouble(timeInput);
						banTime+=timeInt*type;
					}
					catch(Exception e)
					{
						if(args[1].equalsIgnoreCase("perm"))
						{
							banTime = 0L;	//set date to far time for perm ban
						}
						else
						{
							sender.sendMessage("Please enter a valid time!");
							return false;
						}
					}
				}
			}
			Timestamp dt;
			dt = new Timestamp(banTime);
			String reason = " ";
			for(int i=2; i<args.length; i++)
			{
				reason+=args[i]+" ";
			}
			query = "SELECT * FROM " + plugin.BansTable + " WHERE UUID = \"" + uuid+"\";";
			rs = db.querySQL(query);
			boolean alreadyBanned = false;
			Actions action = Actions.BAN;
			try {
				alreadyBanned = rs.first();
				if(alreadyBanned && rs.getDate("Time").getTime()<System.currentTimeMillis())
				{
					action = Actions.UPDATE_BAN;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			String adminUUID;
			if((sender instanceof Player)){ Player player = (Player) sender; adminUUID = UUIDFetcher.getSafeUUID(player).toString();}
			else adminUUID = "console";
			query = "INSERT INTO " + plugin.HistoryTable + " VALUES (\""+uuid+"\",\""+dt.toString()+"\",\""+reason+"\",\""+adminUUID+"\",\""+action+"\");";
			db.updateSQL(query);
			query = "REPLACE INTO " + plugin.BansTable + " VALUES (\""+uuid+"\",\""+dt.toString()+"\",\""+reason+"\",\""+adminUUID+"\");";
			db.updateSQL(query);
			@SuppressWarnings("deprecation")
			Player player = plugin.getServer().getPlayer(args[0]);
			if(player!=null)
			{
				player.kickPlayer(Message.banMessage(reason, dt, player.getAddress().getHostName() ));
			}
			plugin.getServer().broadcastMessage(Message.broadcastBan(reason, dt, player.getName(), sender.getName()));
		}
		return true;
	}

}
