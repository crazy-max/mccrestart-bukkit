package fr.crazyws.mccrestart;

import java.util.ArrayList;
import java.util.Calendar;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import fr.crazyws.mccrestart.utils.ConfigUtils;
import fr.crazyws.mccrestart.utils.TimeUtils;
import fr.crazyws.mccrestart.utils.Utils;

public class Schedule implements Runnable {
	
	public Calendar calendar;
	public Plugin plugin;
	public PluginManager pluginManager;
    public ArrayList<TimeUtils> times, warn;
    public TimeUtils delay, next;
    public TimeUtils wait = null;
    public String reason = null;
    public Boolean running = true;

    public Schedule()
    {
    	pluginManager = MCCRestart.server.getPluginManager();
    	plugin = pluginManager.getPlugin(MCCRestart.name);
    	
    	if( !ConfigUtils.active )
    	{
    		running = false;
		    return;
    	}
    	
    	if( ConfigUtils.type == null )
    	{
    		Utils.Log("warning", "type property empty");
    		running = false;
		    return;
    	}
    	
    	if( ConfigUtils.type.equalsIgnoreCase(ConfigUtils.TYPE_TIMES) && ConfigUtils.times == null )
    	{
    		Utils.Log("warning", "times property empty");
    		running = false;
		    return;
    	}
    	
    	if( ConfigUtils.type.equalsIgnoreCase(ConfigUtils.TYPE_DELAY) && ConfigUtils.delay == null )
    	{
    		Utils.Log("warning", "delay property empty");
    		running = false;
		    return;
    	}
    	
    	if( ConfigUtils.launcher == null )
    	{
    		Utils.Log("warning", "launcher property empty");
    		running = false;
		    return;
    	}
    	
        times = new ArrayList<TimeUtils>();
        warn = new ArrayList<TimeUtils>();
        
        try
		{
        	String[] t = ConfigUtils.delay.split(":");
        	delay = next = new TimeUtils(Integer.parseInt(t[0]), Integer.parseInt(t[1]), Integer.parseInt(t[2]));
			next.reset(delay);
		}
		catch(Exception e)
        {
        	Utils.Log("warning", "Error to add delay " + ConfigUtils.delay);
        	running = false;
		    return;
        }
        
        String[] t;
        for( String chars : ConfigUtils.times.split(",") )
        {
            try
            {
                t = chars.split(":");
                times.add(new TimeUtils(Integer.parseInt(t[0]), Integer.parseInt(t[1]), Integer.parseInt(t[2])));
            }
            catch(Exception e)
            {
            	Utils.Log("warning", "Error to add time " + chars);
            	running = false;
			    return;
            }
        }
        
        for( String chars : ConfigUtils.warn.split(",") )
        {
        	try
            {
        		warn.add(new TimeUtils(0, 0, Integer.parseInt(chars)));
            }
        	catch(Exception e)
        	{
        		Utils.Log("warning", "Error to add warn " + chars);
            	running = false;
			    return;
        	}
        }
    }

    public void run()
    {
        while( running || wait != null )
        {
        	if( wait != null )
        	{
        		for( TimeUtils w : warn )
                {
                    if( wait.doWarn(w) )
                    {
                    	String[] warntime = {String.valueOf(w.Second)};
                    	MCCRestart.server.broadcastMessage(ChatColor.GOLD + ConfigUtils.GetParams(ConfigUtils.warnTimeMsg, warntime));
                    	if( reason != null )
                    	{
                    		String[] thereason = {reason};
                    		MCCRestart.server.broadcastMessage(ChatColor.GOLD + ConfigUtils.GetParams(ConfigUtils.reasonMsg, thereason));
                    	}
                    }
                }
        		
        		if( wait.isNow() )
        		{
        			wait = null;
        			reason = null;
        			restart();
        		}
        	}
        	else
        	{
        		if( ConfigUtils.type.equalsIgnoreCase(ConfigUtils.TYPE_TIMES) )
            	{
            		for( TimeUtils t : times )
                    {
                        for( TimeUtils w : warn )
                        {
                            if( t.doWarn(w) )
                            {
                            	String[] warntime = {String.valueOf(w.Second)};
                            	MCCRestart.server.broadcastMessage(ChatColor.GOLD + ConfigUtils.GetParams(ConfigUtils.warnTimeMsg, warntime));
                            }
                        }

                        if( t.isNow() )
                        {
                        	restart();
                        }
                    }
            	}
            	else if( ConfigUtils.type.equalsIgnoreCase(ConfigUtils.TYPE_DELAY) )
            	{
            		for( TimeUtils w : warn )
                    {
                        if( next.doWarn(w) )
                        {
                        	String[] warntime = {String.valueOf(w.Second)};
                        	MCCRestart.server.broadcastMessage(ChatColor.GOLD + ConfigUtils.GetParams(ConfigUtils.warnTimeMsg, warntime));
                        }
                    }
            		
            		if( next.isNow() )
            		{
            			next.reset(delay);
            			restart();
            		}
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
    	MCCRestart.server.broadcastMessage(ChatColor.RED + ConfigUtils.warnMsg);
    	
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
            p.kickPlayer(ChatColor.RED + ConfigUtils.restartMsg);
        }
    	
    	try
        {
        	((CraftServer) MCCRestart.server).getServer().a();
            Runtime.getRuntime().exec("java -jar plugins/MCCRestart.jar restart " + ConfigUtils.launcher);
            Utils.Log("info", "Restarting server. Please wait...");
            Thread.sleep(2000);
            System.exit(0);
        }
        catch(Exception e)
        {
        	Utils.Log("warning", "Error while restarting server...");
            e.printStackTrace();
        }
    }
}
