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
			active = GetConfigBoolean("config.active", true);
			launcher = GetConfigString("config.launcher", "java -Xms512M -Xmx512M -jar craftbukkit.jar");
			stoptimes = GetConfigString("config.stoptimes", "12:00:00,23:00:00");
			warntimes = GetConfigString("config.warntimes", "30,10");
		}
		else if( config.equals("messages") )
		{
			warnMsg = GetConfigString("messages.warn", "The server is being restarted...");
			warnTimeMsg = GetConfigString("messages.warnTime", "The server restarts in {0} seconds...");
			restartMsg = GetConfigString("messages.restart", "The server is restarting...");
			activeMsg = GetConfigString("messages.active", MCCRestart.name + " v" + MCCRestart.version + " enabled!");
			inactiveMsg = GetConfigString("messages.inactive", MCCRestart.name + " v" + MCCRestart.version + " enabled!");
		}
	}
	
	public boolean Save(String key, String line)
	{
		try
		{
			template.load();
			template.setProperty(key, line);
			template.save();
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}
	
	public static String GetConfigParams(String msg, String[] params)
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
	
	public static String GetConfigString(String key, String defaultvalue)
	{
		return template.getString(key, defaultvalue).trim();
	}
	
	public static boolean GetConfigBoolean(String key, boolean defaultvalue)
	{
		return template.getBoolean(key, defaultvalue);
	}
	
	public void DeleteConfigValue(String key) 
	{
		template.removeProperty(key);
	}
}