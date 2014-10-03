package hyg.hydrosis.hbans;

import hyg.hydrosis.hbans.util.Message;
import hyg.hydrosis.hbans.util.UUIDFetcher;
import hyg.hydrosis.hbans.util.db.mysql.MySQL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

public class Listeners implements Listener{
	
	hBans plugin;
	MySQL db;
	public Listeners(hBans plugin)
	{
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		db = plugin.db;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLogin(PlayerLoginEvent event)
	{
		String IP = event.getAddress().getHostAddress();
		Player player = event.getPlayer();
		UUID uuid = UUIDFetcher.getSafeUUID(player);
		String query = null;
		ResultSet rs = null;
		query = "SELECT * FROM "+ plugin.BansTable + " WHERE UUID = \"" + uuid.toString() + "\";";
		rs = db.querySQL(query);
		query = "SELECT * FROM " + plugin.IPBansTable+";";
		ResultSet rsIP = db.querySQL(query);
		query = "SELECT * FROM " + plugin.BannedIPsView + ";";
		ResultSet rsIP2 = db.querySQL(query);
		boolean empty = true;
		try {
			empty = !rs.first();
			if(empty)
			{
				if(rsIP.first())
				{
					do //check specific IPs banned
					{
						if(rsIP.getString("IP").equalsIgnoreCase(IP))
						{
							Timestamp time = rsIP.getTimestamp("Time");
							String message = Message.banMessage(rsIP.getString("Reason"), time, player.getAddress().getHostName());
							event.disallow(Result.KICK_BANNED, message);//message+"\n"+reason+"\n"+unbanDay);
							return;
						}
					}while(rsIP.next());
				}
				if(rsIP2.first())
				{
					do//check if IP matches that of a banned player's IP
					{
						if(rsIP2.getString("IP").equalsIgnoreCase(IP))
						{
							query = "SELECT * FROM "+ plugin.BansTable + " WHERE UUID = \"" + rsIP2.getString("UUID") + "\";";
							ResultSet rsIPUUID = db.querySQL(query);
							rsIPUUID.first();
							Timestamp time = rsIPUUID.getTimestamp("Time");
							//
							if(new java.sql.Timestamp(Calendar.getInstance().getTime().getTime()).after(time))
							{
								query = "DELETE FROM "+plugin.BansTable+" WHERE UUID = \"" + rsIP2.getString("UUID") + "\";";
								db.updateSQL(query);
							}
							else
							{
								String message = Message.banMessage(rsIPUUID.getString("Reason"), time, IP);
								event.disallow(Result.KICK_BANNED, message);							}
							//
							return;
						}
					}while(rsIP2.next());
				}
				query = "INSERT IGNORE INTO "+ plugin.IPHistoryTable +" (UUID, IP) VALUES (\""+uuid.toString()+"\",\""+IP+"\")";
				db.updateSQL(query);
				query = "INSERT IGNORE INTO "+ plugin.NameHistoryTable +" (UUID, Name) VALUES (\""+uuid.toString()+"\",\""+player.getName()+"\")";
				db.updateSQL(query);
				query = "SELECT TIME FROM " + plugin.BansTable + " WHERE UUID=\"" + uuid.toString()+"\"";
				return;
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		Timestamp time = null;
		try { //if their UUID is found in the bans list
			time = rs.getTimestamp(2);
			//if ban expired
			if(time.getTime() < System.currentTimeMillis() && time.getTime()!=0)
			{
				query = "DELETE FROM "+plugin.BansTable+" WHERE UUID = \"" + uuid.toString() + "\";";
				db.updateSQL(query);
			}
			else
			{
				event.disallow(Result.KICK_BANNED, Message.banMessage(rs.getString("Reason"), time, IP));//message+"\n"+reason+"\n"+unbanDay);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event)
	{
		Player player = event.getPlayer();
		String uuid = UUIDFetcher.getSafeUUID(player).toString();
		if(plugin.muted.containsKey(uuid))
		{
			long time = plugin.muted.get(uuid);
			if(System.currentTimeMillis()>time && time!=0) //if mute has expired
			{
				String query = "DELETE FROM "+plugin.MuteTable+" WHERE UUID = \"" + uuid.toString() + "\";";
				db.updateSQL(query);
				plugin.muted.remove(uuid);
			}
			else
			{
				event.setCancelled(true);
				player.sendMessage(Message.muteMessage(plugin.mutedReason.get(uuid), time));
			}
		}
	}
	
	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event)
	{
		if(plugin.muted.containsKey(UUIDFetcher.getSafeUUID(event.getPlayer()).toString()))
		{
			String command = event.getMessage().split(" ")[0];
			if(plugin.blockedCommands.contains(command))
			{
				event.setCancelled(true);
				event.getPlayer().sendMessage(Message.commandBlocked());
			}
		}
	}
	
}
