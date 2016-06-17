package fr.crazyws.mccrestart.utils;

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
}