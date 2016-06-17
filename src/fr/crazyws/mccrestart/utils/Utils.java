package fr.crazyws.mccrestart.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.crazyws.mccrestart.MCCRestart;

public class Utils {
	
	public static void Log(String type, String what)
	{
		if(type.equals("severe")) MCCRestart.log.severe("[" + MCCRestart.name + "] " + what);
		else if(type.equals("info")) MCCRestart.log.info("[" + MCCRestart.name + "] " + what);
		else if(type.equals("warning")) MCCRestart.log.warning("[" + MCCRestart.name + "] " + what);
	}
	
	public static void SendMessage(CommandSender sender, String message)
	{
		if (sender instanceof Player)
		{
			sender.sendMessage(message);
		}
	}
	
	public static boolean isNumeric(String num){
	    try
	    {
	        Integer.parseInt(num);
	    }
	    catch(NumberFormatException nfe)
	    {
	        return false;
	    }
	    return true;
	}
	
	public static void Help(String type, CommandSender sender)
	{
		Utils.SendMessage(sender, ChatColor.WHITE + MCCRestart.name + " commands:");
		Utils.Log("info", MCCRestart.name + " commands:");
		
		if( type.equalsIgnoreCase("mcc") || type.equalsIgnoreCase("all") )
		{
			Utils.SendMessage(sender, ChatColor.GREEN + "/mccrestart help" + ChatColor.BLACK + " - " + ChatColor.WHITE + " List MCCRestart commands");
			Utils.SendMessage(sender, ChatColor.GREEN + "/mccrestart on|off" + ChatColor.BLACK + " - " + ChatColor.WHITE + " Actives/deactivates MCCRestart");
			Utils.SendMessage(sender, ChatColor.GREEN + "/mccrestart reload" + ChatColor.BLACK + " - " + ChatColor.WHITE + " Reload the plugin configuration");
			Utils.SendMessage(sender, ChatColor.GREEN + "/mccrestart next" + ChatColor.BLACK + " - " + ChatColor.WHITE + " Give the next time to restart");
			Utils.Log("info", "/mccrestart help - List MCCRestart commands");
			Utils.Log("info", "/mccrestart on|off - Actives/deactivates MCCRestart");
			Utils.Log("info", "/mccrestart reload - Reload the plugin configuration");
			Utils.Log("info", "/mccrestart next - Give the next time to restart");
		}
		
		if( type.equalsIgnoreCase("restart") || type.equalsIgnoreCase("all") )
		{
			Utils.SendMessage(sender, ChatColor.GREEN + "/restart" + ChatColor.BLACK + " - " + ChatColor.WHITE + "Restart the server immediately");
			Utils.SendMessage(sender, ChatColor.GREEN + "/restart 59:59" + ChatColor.BLACK + " - " + ChatColor.WHITE + "Restart after a delay (minutes:seconds)");
			Utils.SendMessage(sender, ChatColor.GREEN + "/restart 59:59 \"reason\"" + ChatColor.BLACK + " - " + ChatColor.WHITE + "Restart after a delay with a reason");
			Utils.SendMessage(sender, ChatColor.GREEN + "/restart cancel" + ChatColor.BLACK + " - " + ChatColor.WHITE + "Cancel a manual restart");
			Utils.Log("info", "/restart - Restart the server immediately");
			Utils.Log("info", "/restart 59:59 - Restart after a delay (minutes:seconds)");
			Utils.Log("info", "/restart 59:59 \"reason\" - Restart after a delay with a reason");
			Utils.Log("info", "/restart cancel - Cancel a manual restart");
		}
	}
}