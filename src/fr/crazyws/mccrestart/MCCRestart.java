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

import fr.crazyws.mccrestart.utils.Config;
import fr.crazyws.mccrestart.utils.Util;

public class MCCRestart extends JavaPlugin
{
    public Logger Log = Logger.getLogger("Minecraft");
    
    public Schedule ScheduleThread;
    public Config ConfigFile;
    public Config MessagesFile;
    
    public Permissions permissions;
    public PermissionHandler permissionsHandler = null;
    
    public static MCCRestart instance;
    public static org.bukkit.Server server;
    public static Logger log = Logger.getLogger("Minecraft");
    
    public static String name = "MCCRestart";
    public static String version = "1.1.5";
    public static String path = "plugins/" + name + "/";
    public static String configYML = "config.yml";
    public static String messagesYML = "messages.yml";
    
	public void onEnable()
    {
    	server = getServer();
    	instance = this;
        PluginManager pluginManager = server.getPluginManager();
        if( !loadConfig() ) return;
		
		try
        {
            if( pluginManager.getPlugin("Permissions").isEnabled() )
            {
            	permissions = (Permissions) pluginManager.getPlugin("Permissions");
            	permissions.setupPermissions();
            	permissionsHandler = permissions.getHandler();
            	Util.Log("info", "Permissions " + permissions.getDescription().getVersion() + " enabled for use.");
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
            Util.Log("info", "enabled");
        }
        else
        {
        	Util.Log("info", "disabled");
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
        		if( args.length > 0 )
                {
        			if( args[0].compareToIgnoreCase("help") == 0 )
        			{
        				sender.sendMessage(ChatColor.WHITE + name + " commands:");
						sender.sendMessage(ChatColor.GREEN + "/mccrestart help" + ChatColor.BLACK + " - " + ChatColor.WHITE + " List MCCRestart commands");
						sender.sendMessage(ChatColor.GREEN + "/mccrestart on|off" + ChatColor.BLACK + " - " + ChatColor.WHITE + " Actives/deactivates MCCRestart");
						sender.sendMessage(ChatColor.GREEN + "/mccrestart reload" + ChatColor.BLACK + " - " + ChatColor.WHITE + " Reload the plugin configuration");
						sender.sendMessage(ChatColor.GREEN + "/restart" + ChatColor.BLACK + " - " + ChatColor.WHITE + " Restart the server immediately");
						return true;
        			}
        			else if( args[0].compareToIgnoreCase("on") == 0 )
        			{
        				Config.active = true;
        				
        				if( !instance.ScheduleThread.running )
        				{
        					launchThread();
        				}
        				
        				if (sender instanceof Player)
        				{
        					sender.sendMessage(ChatColor.GREEN + Config.activeMsg);
        				}
        				
        				return true;
        			}
        			else if( args[0].compareToIgnoreCase("off") == 0 )
        			{
        				Config.active = false;
        				instance.ScheduleThread.running = false;
        				if (sender instanceof Player)
        				{
        					sender.sendMessage(ChatColor.RED + Config.inactiveMsg);
        				}
        				
        				Util.Log("info", "disabled");
        				return true;
        			}
        			else if( args[0].compareToIgnoreCase("reload") == 0 )
        			{
        				pluginManager.disablePlugin(((Plugin) (this)));
        				pluginManager.enablePlugin(((Plugin) (this)));
        				
        				if( pluginManager.isPluginEnabled(((Plugin) (this))) )
        				{
        					if (sender instanceof Player)
            				{
            					sender.sendMessage(ChatColor.RED + Config.reloadMsg);
            				}
            				
            				Util.Log("info", "reloaded");
        				}
        				return true;
        			}
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
    		sender.sendMessage(ChatColor.RED + "You cannot use this command.");
    	}
    	
    	return false;
    }
    
    private boolean loadConfig()
    {
    	boolean result = true;
    	
    	MessagesFile = new Config("messages", MCCRestart.path, messagesYML);
        if( !new File(MCCRestart.path + messagesYML).exists() || MessagesFile.GetString("messages", "") == null )
		{
        	if( !copyFile(messagesYML, MCCRestart.path) )
        	{
        		Util.Log("severe", messagesYML + " could not be created");
        		result = false;
        	}
        }
        
        ConfigFile = new Config("config", MCCRestart.path, configYML);
        if( !new File(MCCRestart.path + configYML).exists() || MessagesFile.GetString("config", "") == null )
		{
			if( !copyFile(configYML, MCCRestart.path) )
			{
				Util.Log("severe", configYML + " could not be created");
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
    	if( Config.active )
		{
    		if( !canUse && sender instanceof Player && instance.permissions != null )
            {
                canUse = instance.permissionsHandler.has((Player)sender, "mccrestart.use");
            }
		}
    	return canUse;
    }
}
