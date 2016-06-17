package fr.crazyws.mccrestart.utils;

import java.util.ArrayList;
import java.util.Calendar;

public class TimeUtils {

    public int Hour, Minute, Second;

    public TimeUtils(int h, int m, int s)
    {
        this.Hour = h;
        this.Minute = m;
        this.Second = s;
    }

    public Boolean isNow()
    {
        int h, m, s;
        h = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        m = Calendar.getInstance().get(Calendar.MINUTE);
        s = Calendar.getInstance().get(Calendar.SECOND);
        
        if(this.Hour == h && this.Minute == m && this.Second == s)
        {
            return true;
        }
        
        return false;
    }

    public Boolean doWarn(TimeUtils warn)
    {
        int warnSec = warn.toSeconds();
        int h, m, s, wh, wm, ws;
        h = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        m = Calendar.getInstance().get(Calendar.MINUTE);
        s = Calendar.getInstance().get(Calendar.SECOND);

        ws = warnSec % 60 + s;
        wm = (ws/60) + m;
        wh = (wm/60) + h;

        if(ws>60)
        	ws%=60;
        if(ws==60)
        	ws=0;
        
        if(wm>60)
        	wm%=60;
        if(wm==60)
        	wm=0;
        
        if(wh>23)
        	wh%=24;
        if(wh==24)
        	wh=0;

        if(this.Hour == wh && this.Minute == wm && this.Second == ws)
        {
            return true;
        }

        return false;
    }
    
    public static String nextTime(ArrayList<TimeUtils> times)
    {
        int today = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) * 60 * 60;
        today += Calendar.getInstance().get(Calendar.MINUTE) * 60;
        today += Calendar.getInstance().get(Calendar.SECOND);
        
        String result = "";
        int current = 0;
        for( TimeUtils t : times )
        {
        	int stopSec = t.toSeconds();
        	if( (stopSec - today < current || current == 0) && stopSec - today > 0 )
        	{
        		current = stopSec - today;
        		String hS = t.Hour < 10 ? "0" + String.valueOf(t.Hour) : String.valueOf(t.Hour);
        		String mS = t.Minute < 10 ? "0" + String.valueOf(t.Minute) : String.valueOf(t.Minute);
        		String sS = t.Second < 10 ? "0" + String.valueOf(t.Second) : String.valueOf(t.Second);
        		result = hS + ":" + mS + ":" + sS;
        	}
        }
        
        int tmp = 0;
        if( current == 0 )
        {
        	for( TimeUtils t : times )
            {
        		int stopSec = t.toSeconds();
        		if( stopSec < tmp || tmp == 0 )
        		{
        			tmp = stopSec;
            		result = toString(t);
        		}
            }
        }
        
        return result;
    }
    
    public void reset(TimeUtils delay)
    {
    	int today = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) * 60 * 60;
        today += Calendar.getInstance().get(Calendar.MINUTE) * 60;
        today += Calendar.getInstance().get(Calendar.SECOND);
        
        int next = today + delay.Hour * 60 * 60;
        next += delay.Minute * 60;
        next += delay.Second;
        next = next > 86400 ? (next - 86400) : ( next == 86400 ? 0 : next );
        
        if( next > 0 )
        {
        	this.Second = next % 60;
        	this.Minute = next / 60;
        	this.Hour = this.Minute / 60;
        	this.Minute = this.Minute % 60;
        }
        else if( next == 0 )
        {
        	this.Hour = this.Minute = this.Second = 0;
        }
    }

    public int toSeconds()
    {
        int s = 0;
        s += Hour * 60 * 60;
        s += Minute * 60;
        s += Second;
        return s;
    }
    
    public static String toString(TimeUtils time)
    {
    	String hS = time.Hour < 10 ? "0" + String.valueOf(time.Hour) : String.valueOf(time.Hour);
		String mS = time.Minute < 10 ? "0" + String.valueOf(time.Minute) : String.valueOf(time.Minute);
		String sS = time.Second < 10 ? "0" + String.valueOf(time.Second) : String.valueOf(time.Second);
		return hS + ":" + mS + ":" + sS;
    }
}
