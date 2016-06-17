package fr.crazyws.mccrestart;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

import fr.crazyws.mccrestart.utils.ConfigUtils;
import fr.crazyws.mccrestart.utils.TimeUtils;
import fr.crazyws.mccrestart.utils.Utils;

public class MCCRestart extends JavaPlugin
{
    public Logger Log = Logger.getLogger("Minecraft");
    
    public Schedule ScheduleThread;
    public ConfigUtils ConfigFile;
    public ConfigUtils MessagesFile;
    
    public Permissions permissions;
    public PermissionHandler permissionsHandler = null;
    
    public static MCCRestart instance;
    public static org.bukkit.Server server;
    public static Logger log = Logger.getLogger("Minecraft");
    
    public static String name;
    public static String version;
    public static String path = "plugins/" + name + "/";
    public static String configYML = "config.yml";
    public static String messagesYML = "messages.yml";
    
	public void onEnable()
    {
		server = getServer();
    	instance = this;
        PluginManager pluginManager = server.getPluginManager();
        
        name = instance.getDescription().getName();
        version = instance.getDescription().getVersion();
        
		Utils.Log("info", "v" + version + " launched on " + System.getProperty("os.name"));
		
    	
        if( !loadConfig() ) return;
		
		try
        {
            if( pluginManager.getPlugin("Permissions").isEnabled() )
            {
            	permissions = (Permissions) pluginManager.getPlugin("Permissions");
            	permissions.setupPermissions();
            	permissionsHandler = permissions.getHandler();
            	Utils.Log("info", "Permissions " + permissions.getDescription().getVersion() + " enabled for use.");
            }
        }
        catch(NullPointerException npe)
        {
        	permissions = null;
        }
        
        launchThread();
    }
	
	public void launchThread()
	{
		ScheduleThread = new Schedule();
        
        if( ScheduleThread.running )
        {
        	new Thread(ScheduleThread).start();
            Utils.Log("info", "enabled for " + ConfigUtils.type);
            
            if( ConfigUtils.type.equalsIgnoreCase(ConfigUtils.TYPE_TIMES) )
            {
            	Utils.Log("info", "next restart scheduled at " + TimeUtils.nextTime(instance.ScheduleThread.times));
            }
            else if( ConfigUtils.type.equalsIgnoreCase(ConfigUtils.TYPE_DELAY) )
            {
            	Utils.Log("info", "next restart scheduled at " + TimeUtils.toString(instance.ScheduleThread.next));
            }
        }
        else
        {
        	Utils.Log("info", "disabled");
        }
	}

    public void onDisable()
    {
    	instance.ScheduleThread.running = false;
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
    	server = getServer();
    	PluginManager pluginManager = server.getPluginManager();
    	
    	if( canUseCommands(sender) )
    	{
    		if( commandLabel.equalsIgnoreCase("mccrestart") )
        	{
    			if( args.length == 0 || args[0].compareToIgnoreCase("help") == 0 )
    			{
					Utils.SendMessage(sender, ChatColor.WHITE + name + " commands:");
					Utils.SendMessage(sender, ChatColor.GREEN + "/mccrestart help" + ChatColor.BLACK + " - " + ChatColor.WHITE + " List MCCRestart commands");
					Utils.SendMessage(sender, ChatColor.GREEN + "/mccrestart on|off" + ChatColor.BLACK + " - " + ChatColor.WHITE + " Actives/deactivates MCCRestart");
					Utils.SendMessage(sender, ChatColor.GREEN + "/mccrestart reload" + ChatColor.BLACK + " - " + ChatColor.WHITE + " Reload the plugin configuration");
					Utils.SendMessage(sender, ChatColor.GREEN + "/mccrestart next" + ChatColor.BLACK + " - " + ChatColor.WHITE + " Give the next time to restart");
					Utils.SendMessage(sender, ChatColor.GREEN + "/restart" + ChatColor.BLACK + " - " + ChatColor.WHITE + " Restart the server immediately");
					return true;
    			}
    			else if( args[0].compareToIgnoreCase("on") == 0 )
    			{
    				ConfigUtils.active = true;
    				if( !instance.ScheduleThread.running ) launchThread();
    				Utils.SendMessage(sender, ChatColor.GREEN + ConfigUtils.activeMsg);
    				Utils.Log("info", "enabled");
    				return true;
    			}
    			else if( args[0].compareToIgnoreCase("off") == 0 )
    			{
    				ConfigUtils.active = false;
    				instance.ScheduleThread.running = false;
    				Utils.SendMessage(sender, ChatColor.RED + ConfigUtils.inactiveMsg);
    				Utils.Log("info", "disabled");
    				return true;
    			}
    			else if( args[0].compareToIgnoreCase("reload") == 0 )
    			{
    				pluginManager.disablePlugin(((Plugin) (this)));
    				pluginManager.enablePlugin(((Plugin) (this)));
    				if( pluginManager.isPluginEnabled(((Plugin) (this))) )
    				{
    					Utils.SendMessage(sender, ChatColor.RED + ConfigUtils.reloadMsg);	
        				Utils.Log("info", "reloaded");
    				}
    				return true;
    			}
    			else if( args[0].compareToIgnoreCase("next") == 0 )
    			{
    				if( instance.ScheduleThread.running )
    				{
	    				String tempnext = "";
	    				if( ConfigUtils.type.equalsIgnoreCase(ConfigUtils.TYPE_TIMES) )
	    	            {
	    					tempnext = TimeUtils.nextTime(instance.ScheduleThread.times);
	    	            }
	    	            else if( ConfigUtils.type.equalsIgnoreCase(ConfigUtils.TYPE_DELAY) )
	    	            {
	    	            	tempnext = TimeUtils.toString(instance.ScheduleThread.next);
	    	            }
	    				
	    				String[] nextrestart = {tempnext};
	    				Utils.SendMessage(sender, ChatColor.GOLD + ConfigUtils.GetParams(ConfigUtils.nextMsg, nextrestart));
	    				Utils.Log("info", "next restart scheduled at " + nextrestart[0]);
    				}
    				else
    				{
    					Utils.SendMessage(sender, ChatColor.RED + ConfigUtils.inactiveMsg);
    					Utils.Log("info", "disabled");
    				}
    				return true;
    			}
        	}
        	else if( commandLabel.equalsIgnoreCase("restart") )
        	{
        		instance.ScheduleThread.running = false;
                instance.ScheduleThread.restart();
                return true;
        	}
    	}
    	else
    	{
    		Utils.SendMessage(sender, ChatColor.RED + "You cannot use this command.");
    	}
    	
    	return false;
    }
    
    private boolean loadConfig()
    {
    	boolean result = true;
    	
    	MessagesFile = new ConfigUtils("messages", MCCRestart.path, messagesYML);
        if( !new File(MCCRestart.path + messagesYML).exists() || MessagesFile.GetString("messages", "") == null )
		{
        	if( !copyFile(messagesYML, MCCRestart.path) )
        	{
        		Utils.Log("severe", messagesYML + " could not be created");
        		result = false;
        	}
        }
        
        ConfigFile = new ConfigUtils("config", MCCRestart.path, configYML);
        if( !new File(MCCRestart.path + configYML).exists() || MessagesFile.GetString("config", "") == null )
		{
			if( !copyFile(configYML, MCCRestart.path) )
			{
				Utils.Log("severe", configYML + " could not be created");
        		result = false;
			}
		}
        
        return result;
    }
    
    private boolean copyFile(String filename, String dest)
    {
    	dest = !dest.isEmpty() ? dest + "/" : "";
    	Boolean result = Boolean.TRUE;
    	
    	File folder = new File(path);
		if( !folder.exists() ) new File(path).mkdir();
        
        InputStream f1 = this.getClass().getResourceAsStream("/" + filename);
        FileOutputStream f2 = null;
        
        try
        {
        	f2 = new FileOutputStream(new File(dest + filename));
            byte[] buf = new byte[1024];
            int len;
            
            while( (len = f1.read(buf)) > 0 )
            {
            	f2.write(buf, 0, len);
            }
            
            f2.flush();
            f1.close();
            f2.close();
        }
        catch (Exception ex)
        {
        	result = Boolean.FALSE;
        	ex.printStackTrace();
        }
        
        return result;
    }
    
    private boolean canUseCommands(CommandSender sender)
    {
    	boolean canUse = sender.isOp();
    	if( ConfigUtils.active )
		{
    		if( !canUse && sender instanceof Player && instance.permissions != null )
            {
                canUse = instance.permissionsHandler.has((Player)sender, "mccrestart.use");
            }
		}
    	return canUse;
    }
}
