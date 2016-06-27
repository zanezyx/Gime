package com.gime.locationsb;

public class LsbMgr {
	
	static LsbMgr lsbMgr;
	
	public static LsbMgr getInstance()
	{
		if(lsbMgr==null)
		{
			lsbMgr = new LsbMgr();
		}
		return lsbMgr;
	}
	
	
	public int currLocationType;

}
