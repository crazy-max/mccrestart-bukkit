package fr.crazyws.mccrestart.utils;

import java.util.Calendar;

public class RestartTime {

    public int Hour, Minute, Second;

    public RestartTime(int h, int m, int s)
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

    public Boolean doWarn(RestartTime warn)
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
        
        /*
        Util.Log("info", "--");
        Util.Log("info", "warntime: " + warn.toSeconds());
        Util.Log("info", "Current time: " + h + ":" + m + ":" + s);
        Util.Log("info", "Next warntime: " + wh + ":" + wm + ":" + ws);
        Util.Log("info", "Next stoptime: " + this.Hour + ":" + this.Minute + ":" + this.Second);
        */

        if(this.Hour == wh && this.Minute == wm && this.Second == ws)
        {
            return true;
        }

        return false;
    }

    public int toSeconds()
    {
        int s = 0;
        s += Hour * 60 * 60;
        s += Minute * 60;
        s += Second;
        return s;
    }
}
