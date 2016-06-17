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
    public int currentWarn;
    public Boolean running = true;

    public Schedule()
    {
    	pluginManager = MCCRestart.server.getPluginManager();
    	plugin = pluginManager.getPlugin(MCCRestart.name);
    	
    	if( !ConfigUtils.autorestart )
    	{
    		running = false;
    	}
    	
    	if( ConfigUtils.type == null )
    	{
    		Utils.Log("warning", "type property empty");
    		running = false;
    	}
    	
    	if( ConfigUtils.type.equalsIgnoreCase(ConfigUtils.TYPE_TIMES) && ConfigUtils.times == null )
    	{
    		Utils.Log("warning", "times property empty");
    		running = false;
    	}
    	
    	if( ConfigUtils.type.equalsIgnoreCase(ConfigUtils.TYPE_DELAY) && ConfigUtils.delay == null )
    	{
    		Utils.Log("warning", "delay property empty");
    		running = false;
    	}
    	
    	if( ConfigUtils.launcher == null )
    	{
    		Utils.Log("warning", "launcher property empty");
    		running = false;
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
        	}
        }
    }

    public void run()
    {
        while( true )
        {
        	if( running || wait != null )
        	{
	        	if( wait != null )
	        	{
	                if( displayWarn(wait) )
	                {
	                	if( reason != null )
	                	{
	                		String[] thereason = {reason};
	                		MCCRestart.server.broadcastMessage(ChatColor.GOLD + ConfigUtils.GetParams(ConfigUtils.reasonMsg, thereason));
	                	}
	                }
	        		
	        		if( wait.isNow() )
	        		{
	        			wait = null;
	        			restart();
	        		}
	        	}
	        	else
	        	{
	        		if( ConfigUtils.type.equalsIgnoreCase(ConfigUtils.TYPE_TIMES) )
	            	{
	            		for( TimeUtils t : times )
	                    {
	                        displayWarn(t);
	                        if( t.isNow() )
	                        {
	                        	restart();
	                        }
	                    }
	            	}
	            	else if( ConfigUtils.type.equalsIgnoreCase(ConfigUtils.TYPE_DELAY) )
	            	{
	                    displayWarn(next);
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
    }
    
    public void restart()
    {
    	MCCRestart.server.broadcastMessage(ChatColor.RED + ConfigUtils.warnMsg);
    	currentWarn = 0;
    	
    	MCCRestart.server.savePlayers();
    	Utils.Log("info", "Players saved");
    	
        for( org.bukkit.World w : MCCRestart.server.getWorlds() )
        {
        	w.save();
        	Utils.Log("info", w.getName() + " saved");
        }
        
        for( Plugin p : pluginManager.getPlugins() )
        {
        	if( !p.getDescription().getName().equals(MCCRestart.name) )
        	{
        		p.onDisable();
        	}
        }
        
        Utils.Log("info", "Plugins saved and disabled");
        
        for( org.bukkit.entity.Player p : MCCRestart.server.getOnlinePlayers() )
        {
        	String reasonStr = "";
        	if( reason != null )
        	{
        		String[] thereason = {reason};
        		reasonStr = "\r\n" + ChatColor.GOLD + ConfigUtils.GetParams(ConfigUtils.reasonMsg, thereason);
        		reason = null;
        	}
            p.kickPlayer(ChatColor.RED + ConfigUtils.restartMsg + reasonStr);
        }
        
        Utils.Log("info", "Players kicked");
    	
    	try
        {
        	Utils.Log("info", "Waiting for " + ConfigUtils.stoptime + " before restarting. Please wait...");
        	String[] stoptime = ConfigUtils.stoptime.split(":");
        	Thread.sleep((Integer.parseInt(stoptime[0]) * 60 + Integer.parseInt(stoptime[1])) * 1000);
        	
        	((CraftServer) MCCRestart.server).getServer().a();
            Runtime.getRuntime().exec("java -jar plugins/MCCRestart.jar restart " + ConfigUtils.launcher);
            Utils.Log("info", "Restarting server...");
            
            Thread.sleep(2000);
            System.exit(0);
        }
        catch(Exception e)
        {
        	Utils.Log("warning", "Error while restarting server...");
            e.printStackTrace();
        }
    }
    
    public boolean displayWarn(TimeUtils time)
    {
    	for( TimeUtils w : warn )
        {
	    	if( time.doWarn(w) && currentWarn != w.Second )
	    	{
	    		currentWarn = w.Second;
	    		String[] warntime = {String.valueOf(w.Second)};
            	MCCRestart.server.broadcastMessage(ChatColor.GOLD + ConfigUtils.GetParams(ConfigUtils.warnTimeMsg, warntime));
	    		return true;
	    	}
        }
    	return false;
    }
}
