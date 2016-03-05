package com.sockfullofpennies.epytome.world;

import java.util.Date;
import java.util.TimeZone;

public class WorldTime {
	private TimeZone tz = TimeZone.getTimeZone("US/Eastern");
	
	public long getTime() {
		long utc = new Date().getTime();
		return utc + tz.getOffset(utc);
	}
	
	public long getPriorMidnight() {
		long time = getTime();
		
		return time - (time % (24*60*60*1000));
	}
}
