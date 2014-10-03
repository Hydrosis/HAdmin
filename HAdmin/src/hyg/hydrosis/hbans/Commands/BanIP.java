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

public class BanIP implements CommandExecutor{
	private hBans plugin = hBans.getInstance();
	private MySQL db = plugin.db;
	/**
	 * ban Player Time[m|h|d] reason
	 * blank time = perm ban
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("banip"))
		{
			if(args.length<2)
			{
				Message.unbanHelp(sender);
				return true;
			}
			long banTime = System.currentTimeMillis();
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
					sender.sendMessage("Please enter a valid time!");
					return false;
				}
			}
			else if(args[1].equalsIgnoreCase("perm"))
			{
				banTime = 253402300799L;	//set date to far time for perm ban
			}
			else
			{
				sender.sendMessage("Please enter a valid time!");
				return false;
			}
			Timestamp dt;
			dt = new Timestamp(banTime);
			String reason = " ";
			for(int i=2; i<args.length; i++)
			{
				reason+=args[i]+" ";
			}
			String query = "SELECT * FROM " + plugin.IPBansTable + " WHERE IP = \"" + args[0]+"\";";
			ResultSet rs = db.querySQL(query);
			boolean alreadyBanned = false;
			Actions action = Actions.IPBAN;
			try {
				alreadyBanned = rs.first();
				if(alreadyBanned && rs.getDate("Time").getTime()<System.currentTimeMillis())
				{
					action = Actions.UPDATE_IPBAN;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			String adminUUID;
			if((sender instanceof Player)){ Player player = (Player) sender; adminUUID = UUIDFetcher.getSafeUUID(player).toString();}
			else adminUUID = "console";
			query = "INSERT INTO " + plugin.HistoryTable + " VALUES (\""+args[0]+"\",\""+dt.toString()+"\",\""+reason+"\",\""+adminUUID+"\",\""+action+"\");";
			db.updateSQL(query);
			query = "REPLACE INTO " + plugin.IPBansTable + " VALUES (\""+args[0]+"\",\""+dt.toString()+"\",\""+reason+"\",\""+adminUUID+"\");";
			db.updateSQL(query);
			for(Player p : plugin.getServer().getOnlinePlayers())
			{
				if(p.getAddress().getHostName().equalsIgnoreCase(args[0]))
				{
					p.kickPlayer(Message.banMessage(reason, dt, p.getAddress().getHostName()));
				}
			}
			sender.sendMessage(Message.confirmIPBan(reason, dt, args[0]));
		}
		return true;
	}

}
