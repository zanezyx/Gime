package com.gime.locationsb;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.telephony.TelephonyManager;

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
	
	public void addLocationOperation(LocationOperation op)
	{
		if(operationList!=null)
		{
			operationList.add(op);
		}
			
	}
	
	public void delLocationOperation(LocationOperation op)
	{
		if(operationList!=null)
		{
			operationList.remove(op);
		}
	}
	
	public boolean checkIfContainOperation(LocationOperation op)
	{
		for(LocationOperation ope:operationList)
		{
			if(op.getPhoneNumber()!=null && ope.getPhoneNumber()!=null)
			{
				if( op.getPhoneNumber().equals(ope.getPhoneNumber()))
				{
					return true;
				}
			}

			if(op.getWechat()!=null && ope.getWechat()!=null)
			{
				if(op.getWechat().equals(ope.getWechat()))
				{
					return true;
				}
			}
		}
		return false;
	}
	
	public String getImei(Context context)
	{
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);   
		String imei = tm.getDeviceId();
		return imei;
	}

}
