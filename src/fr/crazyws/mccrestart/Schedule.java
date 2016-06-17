package fr.crazyws.mccrestart;

import java.util.ArrayList;
import java.util.Calendar;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import fr.crazyws.mccrestart.utils.Config;
import fr.crazyws.mccrestart.utils.RestartTime;
import fr.crazyws.mccrestart.utils.Util;

public class Schedule implements Runnable {
	
	public Calendar calendar;
	public Plugin plugin;
	public PluginManager pluginManager;
    public ArrayList<RestartTime> stopTimes, warnTimes;
    public Boolean running = true;

    public Schedule()
    {
    	pluginManager = MCCRestart.server.getPluginManager();
    	plugin = pluginManager.getPlugin(MCCRestart.name);
    	
    	if( !Config.active )
    	{
    		running = false;
		    return;
    	}
    	
    	if( Config.stoptimes == null )
    	{
    		Util.Log("warning", "stoptimes property empty");
    		running = false;
		    return;
    	}
    	
    	if( Config.launcher == null )
    	{
    		Util.Log("warning", "launcher property empty");
    		running = false;
		    return;
    	}
    	
        this.stopTimes = new ArrayList<RestartTime>();
        this.warnTimes = new ArrayList<RestartTime>();
        
        String[] t;
        for( String chars : Config.stoptimes.split(",") )
        {
            try
            {
                t = chars.split(":");
                stopTimes.add(new RestartTime(Integer.parseInt(t[0]), Integer.parseInt(t[1]), Integer.parseInt(t[2])));
            }
            catch(Exception e)
            {
            	Util.Log("warning", "Error to add stoptime " + chars);
            	running = false;
			    return;
            }
        }
        
        for( String chars : Config.warntimes.split(",") )
        {
        	try
            {
        		warnTimes.add(new RestartTime(0, 0, Integer.parseInt(chars)));
            }
        	catch(Exception e)
        	{
        		Util.Log("warning", "Error to add warntime " + chars);
            	running = false;
			    return;
        	}
        }
    }

    public void run()
    {
        while( running )
        {
            for( RestartTime t : stopTimes )
            {
                for( RestartTime w : warnTimes )
                {
                    if( t.doWarn(w) )
                    {
                    	String[] warntime = {String.valueOf(w.Second)};
                    	MCCRestart.server.broadcastMessage(ChatColor.GOLD + Config.GetParams(Config.warnTimeMsg, warntime));
                    }
                }

                if( t.isNow() )
                {
                	restart();
                }
            }

            try
            {
                Thread.sleep(750);
            }
            catch(Exception e)
            {
            }
        }
    }
    
    public void restart()
    {
    	MCCRestart.server.broadcastMessage(ChatColor.GOLD + Config.warnMsg);
    	
    	MCCRestart.server.savePlayers();
        for( org.bukkit.World w : MCCRestart.server.getWorlds() )
        {
            w.save();
        }
        
        for( Plugin p : pluginManager.getPlugins() )
        {
        	if( !p.getDescription().getName().equals(MCCRestart.name) )
        	{
        		p.onDisable();
        	}
        }
        
        for( org.bukkit.entity.Player p : MCCRestart.server.getOnlinePlayers() )
        {
            p.kickPlayer(ChatColor.RED + Config.restartMsg);
        }
    	
    	try
        {
        	((CraftServer) MCCRestart.server).getServer().a();
            Runtime.getRuntime().exec("java -jar plugins/MCCRestart.jar restart " + Config.launcher);
            Util.Log("info", "Restarting server...");
            Util.Log("info", "It will be launched in 4 seconds...");
            Thread.sleep(2000);
            System.exit(0);
        }
        catch(Exception e)
        {
        	Util.Log("warning", "Error while restarting server");
            e.printStackTrace();
        }
    }
}
