package fr.crazyws.mccrestart.utils;

import java.io.File;

import org.bukkit.util.config.Configuration;

import fr.crazyws.mccrestart.MCCRestart;

public class Config {
	
	public static Configuration template = null;
	
	// config.yml
	public static boolean active;
	public static String launcher;
	public static String stoptimes;
	public static String warntimes;
	
	// messages.yml
	public static String warnMsg;
	public static String warnTimeMsg;
	public static String restartMsg;
	public static String activeMsg;
	public static String inactiveMsg;
	public static String reloadMsg;

	public Config(String config, String directory, String filename)
	{
		template = new Configuration(new File(directory, filename));
		template.load();
		
		DefaultSettings(config);
	}
	
	private void DefaultSettings(String config)
	{
		if( config.equals("config") ) 
		{
			active = GetBoolean("config.autorestart", true);
			launcher = GetString("config.launcher", "java -Xms512M -Xmx512M -jar craftbukkit.jar");
			stoptimes = GetString("config.stoptimes", "12:00:00,23:00:00");
			warntimes = GetString("config.warntimes", "30,10");
		}
		else if( config.equals("messages") )
		{
			warnMsg = GetString("messages.warn", "The server is being restarted...");
			warnTimeMsg = GetString("messages.warnTime", "The server restarts in {0} seconds...");
			restartMsg = GetString("messages.restart", "The server is restarting...");
			activeMsg = GetString("messages.active", MCCRestart.name + " enabled!");
			inactiveMsg = GetString("messages.inactive", MCCRestart.name + " disabled!");
			reloadMsg = GetString("messages.reload", MCCRestart.name + " reloaded!");
		}
	}
	
	public static String GetParams(String msg, String[] params)
	{
		String finalMsg = msg;
		if( params.length > 0 ){
			int i = 0;
			for( String param : params )
			{
				finalMsg = msg.replace("{" + i + "}", param);
				i++;
			}
		}
		return finalMsg;
	}
	
	public static String GetString(String key, String defaultvalue)
	{
		return template.getString(key, defaultvalue).trim();
	}
	
	public static boolean GetBoolean(String key, boolean defaultvalue)
	{
		String parse = template.getString(key, String.valueOf(defaultvalue));
		return Boolean.valueOf(parse);
	}
}