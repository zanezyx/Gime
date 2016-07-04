package com.gime.locationsb;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

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

	public boolean hasOperation()
	{
		if(operationList!=null)
		{
			if(operationList.size()>0)
			{
				return true;
			}
		}
		return false;
	}
	
	
	public boolean hasUncompletedOperation()
	{
		int count = 0;
		if(operationList!=null)
		{
			for(LocationOperation op:operationList)
			{
				if(op.getLocationStatus()==LsbConst.LOCATION_STATE_LOCATION_WAIT)
				{
					count++;
				}
			}
		}
		if(count>0)
		{
			return true;
		}
		return false;
	}
	
	
	public boolean completeOperation(LocationOperation op)
	{
		boolean res = false;
		if(operationList!=null)
		{
			for(LocationOperation operation:operationList)
			{
				if(op.getLocationType()==operation.getLocationType())
				{
					if(op.getLocationType()==LsbConst.LOCATION_TYPE_PHONE)
					{
						if(op.getPhoneNumber().equals(operation.getPhoneNumber()))
						{
							operation.setLatitude(op.getLatitude());
							operation.setLongitude(op.getLongitude());
							operation.setLocationStatus(LsbConst.LOCATION_STATE_SUCCESS);
							res = true;
						}
					}else{
						if(op.getWechat().equals(operation.getWechat()))
						{
							operation.setLatitude(op.getLatitude());
							operation.setLongitude(op.getLongitude());
							operation.setLocationStatus(LsbConst.LOCATION_STATE_SUCCESS);
							res = true;
						}
					}
				}
			}	
		}
		if(!res)
		{
			Log.i(LsbConst.LOG_TAG, "complete operation error!");
		}else{
			Log.i(LsbConst.LOG_TAG, "complete operation success!");
		}
		return res;
	}
}
