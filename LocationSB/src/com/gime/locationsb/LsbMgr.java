package com.gime.locationsb;

import java.util.ArrayList;
import java.util.List;

public class LsbMgr {
	
	static LsbMgr lsbMgr;
	private List<LocationOperation> operationList;
	
	
	public static LsbMgr getInstance()
	{
		if(lsbMgr==null)
		{
			lsbMgr = new LsbMgr();
		}
		return lsbMgr;
	}
	
	LsbMgr()
	{
		operationList = new ArrayList<LocationOperation>();
	}

	public List getOperaionList()
	{
		return operationList;
	}

}
