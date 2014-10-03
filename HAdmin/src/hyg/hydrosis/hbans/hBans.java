package hyg.hydrosis.hbans;

import hyg.hydrosis.hbans.Commands.Ban;
import hyg.hydrosis.hbans.Commands.BanIP;
import hyg.hydrosis.hbans.Commands.Banstatus;
import hyg.hydrosis.hbans.Commands.HyGAdminHelp;
import hyg.hydrosis.hbans.Commands.IPHistory;
import hyg.hydrosis.hbans.Commands.Kick;
import hyg.hydrosis.hbans.Commands.Mute;
import hyg.hydrosis.hbans.Commands.Mutestatus;
import hyg.hydrosis.hbans.Commands.PlayerHistory;
import hyg.hydrosis.hbans.Commands.Unban;
import hyg.hydrosis.hbans.Commands.UnbanIP;
import hyg.hydrosis.hbans.Commands.Unmute;
import hyg.hydrosis.hbans.util.Message;
import hyg.hydrosis.hbans.util.db.mysql.MySQL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * 
 * @author Radwan Faci
 * TODO: Unmute
 * TODO: IP Logging
 */
public class hBans extends JavaPlugin{
	public MySQL db;
	private static hBans plugin;
	public String NameHistoryTable;
	public String IPHistoryTable;
	public String BansTable;
	public String IPBansTable;
	public String HistoryTable;
	public String MuteTable;
	public String BannedIPsView;
	public static String appealMessage;
	public Map<String, Long> muted = new HashMap<String, Long>(); //Stores player's identification and time until their mute
	public Map<String, String> mutedReason = new HashMap<String, String>(); //Stores reason a player is muted
	Set<String> blockedCommands = new HashSet<String>();
	@Override
	public void onEnable()
	{
		plugin = this;
		this.saveDefaultConfig();
		String hostname = this.getConfig().getString("hostname");
		String port = this.getConfig().getString("port");
		String username = this.getConfig().getString("user");
		String password = this.getConfig().getString("password");
		String database = this.getConfig().getString("database");
		String prefix = this.getConfig().getString("table prefix");
		appealMessage = Message.colorize(this.getConfig().getString("appeal message"));
		loadBlockedCommands();
		NameHistoryTable = prefix + "NameHistory";
		IPHistoryTable = prefix + "IPHistory";
		BansTable = prefix + "Bans";
		IPBansTable = prefix + "IPBans";
		HistoryTable = prefix + "History";
		MuteTable = prefix + "Mutes";
		BannedIPsView = prefix + "BannedIPs";
		db = new MySQL(this, hostname, port, database, username, password);
		db.openConnection();
		new Listeners(this);
		initDB(prefix);
		registerCommands();
		loadMutes();
	}
	
	/**
	 * Loads commands from the config that players cannot use while muted
	 */
	private void loadBlockedCommands() {
		List<String> commands = this.getConfig().getStringList("Blocked Commands");
		System.out.println(commands.size());
		for(String s : commands)
		{
			blockedCommands.add("/"+s);
		}
	}

	private void loadMutes() {
		String query = "SELECT * FROM " + MuteTable;
		ResultSet rs = db.querySQL(query);
		try {
			while(rs.next())
			{
				muted.put(rs.getString("UUID"), rs.getTimestamp("Time").getTime());
				mutedReason.put(rs.getString("UUID"), rs.getString("Reason"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void registerCommands() {
		getCommand("hygadmin").setExecutor(new HyGAdminHelp());
		getCommand("ban").setExecutor(new Ban());
		getCommand("unban").setExecutor(new Unban());
		getCommand("mute").setExecutor(new Mute());
		getCommand("unmute").setExecutor(new Unmute());
		getCommand("banip").setExecutor(new BanIP());
		getCommand("unbanip").setExecutor(new UnbanIP());
		getCommand("kick").setExecutor(new Kick());
		getCommand("banstatus").setExecutor(new Banstatus());
		getCommand("mutestatus").setExecutor(new Mutestatus());
		getCommand("iphistory").setExecutor(new IPHistory());
		getCommand("playerhistory").setExecutor(new PlayerHistory());
	}

	@Override
	public void onDisable()
	{
		db.closeConnection();
	}
	
	public void initDB(String prefix)
	{
		db.updateSQL("CREATE TABLE IF NOT EXISTS " + NameHistoryTable + "(UUID varchar(128) NOT NULL, Name varchar(128), UNIQUE INDEX(UUID, Name));");
		db.updateSQL("CREATE TABLE IF NOT EXISTS " + IPHistoryTable + "(UUID varchar(128) NOT NULL, IP varchar(15), UNIQUE INDEX(UUID, IP));");
		db.updateSQL("CREATE TABLE IF NOT EXISTS " + BansTable + "(UUID varchar(128) NOT NULL, Time datetime, Reason varchar(128), Admin varchar(128), PRIMARY KEY(UUID));");
		db.updateSQL("CREATE TABLE IF NOT EXISTS " + HistoryTable + "(UUID varchar(128) NOT NULL, Time datetime, Reason varchar(128), Admin varchar(128), Action varchar(64));");
		db.updateSQL("CREATE TABLE IF NOT EXISTS " + MuteTable+ "(UUID varchar(128) NOT NULL, Time datetime, Reason varchar(128), Admin varchar(128), PRIMARY KEY(UUID));");
		db.updateSQL("CREATE TABLE IF NOT EXISTS " + IPBansTable + "(IP varchar(128) NOT NULL, Time datetime, Reason varchar(128), Admin varchar(128), PRIMARY KEY(IP));");
		db.updateSQL("CREATE OR REPLACE VIEW "+ BannedIPsView +" AS SELECT H.IP, H.UUID FROM "+ IPHistoryTable +" H JOIN "+ BansTable +" B WHERE H.UUID=B.UUID;");

	}
	
	public static hBans getInstance()
	{
		return plugin;
	}

}
