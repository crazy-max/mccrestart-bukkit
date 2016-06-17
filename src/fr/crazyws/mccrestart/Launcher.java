package fr.crazyws.mccrestart;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.OutputStreamWriter;

public class Launcher
{
    public static void main(String[] args)
    {
    	if( args.length > 0 && args[0].equalsIgnoreCase("restart") && args[1] != null )
    	{
	        try
	        {
	        	Thread.sleep(3000);
	        	
	        	String launcher = "";

	        	int i = 0;
	            for(String arg : args)
	            {
	                if( i > 0 ) launcher += arg + " ";
	                i++;
	            }
	            
	        	Process p;
		        
		        if( System.getProperty("os.name").toLowerCase().contains("win") )
		        {
		            BufferedWriter bw;
		            p = Runtime.getRuntime().exec("cmd.exe /c start " + launcher);
		            bw = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
		            bw.write(launcher + "\r\n");
		            bw.flush();
		        }
		        else
		        {
		            Runtime.getRuntime().exec("screen -dmS MinecraftServer java -Xmx256M -Xms256M -jar craftbukkit.jar");
		        }
	        }
	        catch(Exception e)
	        {
	            try
	            {
	                FileWriter fw = new FileWriter("mccrestart.log");
	                fw.write(e.toString().replace("\n", "\r\n"));
	                fw.flush();
	                fw.close();
	            }
	            catch(Exception ex)
	            {
	            	
	            }
	        }
    	}
    }
}
