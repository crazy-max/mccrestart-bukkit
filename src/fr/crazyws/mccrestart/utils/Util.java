package fr.crazyws.mccrestart.utils;

import fr.crazyws.mccrestart.MCCRestart;

public class Util {
	
	public static void Log(String type, String what)
	{
		if(type.equals("severe")) MCCRestart.log.severe("[" + MCCRestart.name + "] " + what);
		else if(type.equals("info")) MCCRestart.log.info("[" + MCCRestart.name + "] " + what);
		else if(type.equals("warning")) MCCRestart.log.warning("[" + MCCRestart.name + "] " + what);
	}
}