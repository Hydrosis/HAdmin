package hyg.hydrosis.hbans.util;

import hyg.hydrosis.hbans.hBans;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Message {
	public static String validName()
	{
		return ChatColor.RED +  "Please enter a valid name!";
	}

	public static String playerUnbanned(String name) {
		return ChatColor.GREEN + name + ChatColor.YELLOW +" was successfully unbanned!";
	}

	public static String banMessage(String reason, Timestamp ts, String ip) {
		if(ts.getTime()!=0)
		{
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy 'at' HH:mm:ss");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			Timestamp ts2 = new Timestamp(ts.getTime()+IPUtils.timezoneOffset(ip));
			String time = sdf.format(ts2);
			String message = ChatColor.DARK_RED+"You are banned!";
			String unbanDay = ChatColor.YELLOW + "Your ban will expire on " + ChatColor.GOLD + time+ ChatColor.AQUA + "\n( "+timeLeft(ts.getTime())+")";
			String appeal = colorize(hBans.appealMessage);//ChatColor.DARK_AQUA+"Please make an appeal at www.HydrocityGaming.com";
			return message+"\n"+"Reason:" + reason + "\n" + unbanDay + "\n\n" + appeal;
		}
		else
		{
			String message = ChatColor.DARK_RED+"You are "+ChatColor.YELLOW + "PERMANENTLY "+ChatColor.DARK_RED+"banned!";
			String appeal = colorize(hBans.appealMessage);//ChatColor.DARK_AQUA + "Please make an appeal at www.HydrocityGaming.com";
			return message+"\n"+"Reason:" + reason + "\n\n" + appeal;
		}
	}

	public static String timeLeft(long time)
	{
		long tLeft = TimeUnit.MILLISECONDS.toSeconds(time-System.currentTimeMillis());
		long seconds = tLeft;
		long minutes = seconds / 60;
		long hours = minutes/60;
		long days = hours/24;
		String timeLeft = "";
		if(days!=0)
			timeLeft+=days + " Days ";
		if(hours!=0)
			timeLeft+=hours%60 + " Hours ";
		if(minutes!=0)
			timeLeft+=minutes%60 + " Minutes ";
		timeLeft+=seconds%60 + " Seconds ";
		return timeLeft;
	}
	
	public static String playerDoesNotExist(String player)
	{
		return ChatColor.GREEN + player + ChatColor.RED + " could not be found!";
	}

	public static String muteMessage(String reason, long muteTime) {
		if(muteTime!=0)
			return ChatColor.YELLOW + "You have been muted for" + reason +"\nYou will be unmuted in " + timeLeft(muteTime);
		else
			return ChatColor.YELLOW + "You have been muted for" + reason +"\nYour mute is permanent!"+ " " + hBans.appealMessage;
	}

	public static String playerUnmuted(String name, String uuid,hBans plugin) {
		Player player = plugin.getServer().getPlayer(UUID.fromString(uuid));
		if(player!=null)
		{
			player.sendMessage("You have been unmuted, please follow the rules!");
		}
		return ChatColor.GREEN + name + ChatColor.YELLOW + " has been successfully unmuted!";
	}

	public static String enterIP() {
		return ChatColor.RED + "Please enter an IP";
	}

	public static String ipNotBanned() {
		return ChatColor.RED + "This IP is not banned!";
	}

	public static String kick(String message) {
		if(message==null)
		{
			return ChatColor.DARK_RED + "You have been kicked!";
		}
		else
		{
			return ChatColor.DARK_RED + "You have been kicked for " + message;
		}
	}

	public static String broadcastKick(String player, String message) {
		if(message==null)
		{
			return ChatColor.GREEN + player + ChatColor.YELLOW + " has been kicked!";
		}
		else
		{
			return ChatColor.GREEN + player + ChatColor.YELLOW + " has been kicked for " + message;
		}
	}

	public static String playerNotOnline(String player) {
		return ChatColor.GREEN + player + ChatColor.RED + " is not online!";
	}

	public static String playerNotBanned(String string) {
		return ChatColor.GREEN + string + ChatColor.RED + " is not banned!";
	}

	public static String banInfo(String player, String reason, Timestamp ts, String admin) {
		String timeLeft;
		if(ts.getTime()==0)
			timeLeft = "PERMANENT";
		else
			timeLeft = timeLeft(ts.getTime());
		return ChatColor.GOLD + "Player: " + ChatColor.GREEN + player + "\n" +
			   ChatColor.GOLD + "Reason:" + ChatColor.YELLOW + reason + "\n" + 
			   ChatColor.GOLD + "Duration: " + ChatColor.YELLOW + timeLeft + "\n" + 
			   ChatColor.GOLD + "Admin: " + ChatColor.GREEN + admin;
	}

	public static String playerNotMuted(String string) {
		return ChatColor.GREEN + string + ChatColor.RED + " is not muted!";
	}

	public static String ipList(String name , Set<String> ipList) {
		String message = "IP History for " + name + "\n";
		for(String s : ipList)
		{
			message+="- " + s + "\n";
		}
		return ChatColor.YELLOW + message.substring(0, message.length()); //-3 to get rid of new line at the end
	}

	public static String IPDoesNotExist(String ip) {
		return ChatColor.GREEN + ip + ChatColor.RED + " does not exist!";
	}

	public static String nameList(String ip, Set<String> playerList) {
		String message = "Player history for " + ip+"\n";
		for(String s : playerList)
		{
			message+="- " + s + "\n";
		}
		return ChatColor.YELLOW + message.substring(0, message.length()); //-3 to get rid of new line at the end
	}

	public static String broadcastBan(String reason, Timestamp dt, String target, String admin) {
		if(dt.getTime()==0)
		{
			return admin + " has PERMANENTLY banned " + target +
					"\nReason:" + reason;
		}
		else
		{
			return admin + " has banned " + target + " for " + timeLeft(dt.getTime())+"\nReason:" + reason;
		}
	}

	public static String[] confirmIPBan(String reason, Timestamp dt, String target) {
		String timeLeft;
		if(dt.getTime()==0)
			timeLeft = "PERMANENT";
		else
			timeLeft = timeLeft(dt.getTime());
		String[] message = {"You have banned the IP: " + target + " for " + timeLeft,
							"Reason:" + reason};
		return message;
	}

	public static String[] confirmMute(String target, String reason, Timestamp dt) {
		String timeLeft;
		if(dt.getTime()==0)
			timeLeft = "PERMANENT";
		else
			timeLeft = timeLeft(dt.getTime());
		String [] message = {"You have muted " + target + " for " + timeLeft,
							"Reason:" + reason};
		return message;
	}

	public static String notifyPlayerUnmuted(String name) {
		return "You have been unmuted by " + name;
	}
	
	public static void helpHeader(CommandSender sender)
	{
		sender.sendMessage(ChatColor.DARK_PURPLE+"HAdmin Created By: " + ChatColor.DARK_AQUA + "Hydrosis");
		sender.sendMessage(ChatColor.GREEN+"Definitions: "+ChatColor.DARK_RED+"[required]"+ChatColor.GOLD+" <optional>");
		sender.sendMessage("            "+ChatColor.YELLOW+" m=minutes,h=hours,d=days");
		sender.sendMessage(ChatColor.GREEN+"Example: "+ChatColor.BLUE+"/ban "+ChatColor.RED+"hydrosis "+ChatColor.YELLOW+"2d "+ChatColor.GREEN+"Too 1337" );
		sender.sendMessage(ChatColor.AQUA+"Commands:" );
	}
	public static void banHelp(CommandSender sender)
	{
		sender.sendMessage(ChatColor.BLUE + "/ban "+ChatColor.RED+"[name]"+ChatColor.YELLOW+" [time][m|h|d]"+ChatColor.GREEN+" <reason>");
		sender.sendMessage(ChatColor.LIGHT_PURPLE+"    Bans a player for the specified time and reason.\n    Type perm for a permanent ban.");
	}
	public static void unbanHelp(CommandSender sender)
	{
		sender.sendMessage(ChatColor.BLUE +"/unban "+ChatColor.RED+"[name]");
		sender.sendMessage(ChatColor.LIGHT_PURPLE+"    Unbans the specified player.");
	}
	public static void banipHelp(CommandSender sender)
	{
		sender.sendMessage(ChatColor.BLUE +"/banip "+ChatColor.RED+"[IP] "+ChatColor.YELLOW+"[time][m|h|d]"+ChatColor.GREEN+" <reason>");
		sender.sendMessage(ChatColor.LIGHT_PURPLE+"    Bans an IP for the specified time and reason.\n    Type perm for a permanent ban.");
	}
	public static void unbanipHelp(CommandSender sender)
	{
		sender.sendMessage(ChatColor.BLUE +"/unbanip "+ChatColor.RED+"[IP]");
		sender.sendMessage(ChatColor.LIGHT_PURPLE+"    Unbans the specified IP.");
	}
	public static void kickHelp(CommandSender sender)
	{
		sender.sendMessage(ChatColor.BLUE +"/kick "+ChatColor.RED+"[name] "+ChatColor.GREEN+"<reason>");
		sender.sendMessage(ChatColor.LIGHT_PURPLE+"    Kicks the specified player");
	}
	public static void muteHelp(CommandSender sender)
	{
		sender.sendMessage(ChatColor.BLUE +"/mute "+ChatColor.RED+"[name] "+ChatColor.YELLOW+"[time][m|h|d]"+ChatColor.GREEN+" <reason>");
		sender.sendMessage(ChatColor.LIGHT_PURPLE+"    Mutes a player for the specified time and reason.\n    Type perm for a permanent mute.");
	}
	public static void unmuteHelp(CommandSender sender)
	{
		sender.sendMessage(ChatColor.BLUE +"/unmute "+ChatColor.RED+"[name]");
		sender.sendMessage(ChatColor.LIGHT_PURPLE+"    Unmutes the specified player");
	}
	public static void iphistoryHelp(CommandSender sender)
	{
		sender.sendMessage(ChatColor.BLUE +"/iphistory "+ChatColor.RED+"[ip]");
		sender.sendMessage(ChatColor.LIGHT_PURPLE+"    Shows all of the users that have logged in from this IP.");
	}
	public static void playerhistoryHelp(CommandSender sender)
	{
		sender.sendMessage(ChatColor.BLUE +"/playerhistory "+ChatColor.RED+"[name]");
		sender.sendMessage(ChatColor.LIGHT_PURPLE+"    Provides all the IPs the specified player has used to login.");
	}
	public static void banStatusHelp(CommandSender sender)
	{
		sender.sendMessage(ChatColor.BLUE +"/banstatus "+ChatColor.RED+"[name|IP]");
		sender.sendMessage(ChatColor.LIGHT_PURPLE+"    Shows the information about a ban for the specified player or IP");
	}
	public static void muteStatusHelp(CommandSender sender)
	{
		sender.sendMessage(ChatColor.BLUE +"/mutestatus "+ChatColor.RED+"[name]");
		sender.sendMessage(ChatColor.LIGHT_PURPLE+"    Shows information about a mute for the specified player.");
	}

	public static String error() {
		return ChatColor.DARK_RED + "An error has occured. Please report this to an administrator!";
	}

	public static String commandBlocked() {
		return ChatColor.DARK_RED+ "You cannot use this command while muted!";
	}

	public static String connectionError() {
		return "Cannot connect to the database! Please check your connection details in config.yml.";
	}

	public static String colorize(String message)
	{
		return ChatColor.translateAlternateColorCodes('&', message);
	}

}