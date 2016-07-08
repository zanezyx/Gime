package com.gime.locationsb;

import java.util.ArrayList;
import java.util.List;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
							if(op.getLatitude()!=0 && op.getLongitude()!=0
									&& op.getLocationStatus()!=LsbConst.LOCATION_STATE_SUCCESS)
							{
								Log.i(LsbConst.LOG_TAG, "completeOperation latitude:"+op.getLatitude()
										+" longitude:"+op.getLongitude());
								operation.setLatitude(op.getLatitude());
								operation.setLongitude(op.getLongitude());
								operation.setLocationStatus(LsbConst.LOCATION_STATE_SUCCESS);
								res = true;
							}
						}
					}else{
						if(op.getWechat().equals(operation.getWechat()))
						{
							if(op.getLatitude()!=0 && op.getLongitude()!=0
									&& op.getLocationStatus()==LsbConst.LOCATION_STATE_SUCCESS)
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
		}
		if(!res)
		{
			Log.i(LsbConst.LOG_TAG, "complete operation error!");
		}else{
			Log.i(LsbConst.LOG_TAG, "complete operation success!");
		}
		return res;
	}
	
	
	public String generateSmsText(int addId, LocationOperation op)
	{
		String smsText = null;
		if(addId<0 || op==null)
		{
			return null;
		}		
		smsText = LsbConst.LSB_HTTP_URL_GET_LOCATION+"?id="+addId+" "+op.getWenhou();
		Log.i(LsbConst.LOG_TAG, "generateSmsText text:"+smsText);
		return smsText;
			
	}
	
	public void sendSms(int addId, LocationOperation op)
	{
		String text = generateSmsText(addId, op);
		sendSMS(op.getPhoneNumber(),text);
	}
	
	/**
	 * 直接调用短信接口发短信
	 * 
	 * @param phoneNumber
	 * @param message
	 */
	public void sendSMS(String phoneNumber, String message) {
		Log.i(LsbConst.LOG_TAG, "sendSMS message:"+message+" phoneNumber:"+phoneNumber);
		if(phoneNumber==null || message==null)
		{
			return;
		}
		// 获取短信管理器
		android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
		// 拆分短信内容（手机短信长度限制）
		List<String> divideContents = smsManager.divideMessage(message);
		for (String text : divideContents) {
			Log.i(LsbConst.LOG_TAG, "sendSMS text:"+text);
			if(text!=null)
			{
				smsManager.sendTextMessage(phoneNumber, null, text, null,null);
			}
		}
	}
	
//	
//    //处理返回的发送状态   
//    String SENT_SMS_ACTION = "SENT_SMS_ACTION";  
//    Intent sentIntent = new Intent(SENT_SMS_ACTION);  
//    PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, sentIntent,  
//            0);  
//    // register the Broadcast Receivers  
//    context.registerReceiver(new BroadcastReceiver() {  
//        @Override  
//        public void onReceive(Context _context, Intent _intent) {  
//            switch (getResultCode()) {  
//            case Activity.RESULT_OK:  
////                Toast.makeText(context,  
////            "短信发送成功", Toast.LENGTH_SHORT)  
//            .show();  
//            break;  
//            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:  
//            break;  
//            case SmsManager.RESULT_ERROR_RADIO_OFF:  
//            break;  
//            case SmsManager.RESULT_ERROR_NULL_PDU:  
//            break;  
//            }  
//        }  
//    }, new IntentFilter(SENT_SMS_ACTION));  
//
//    //处理返回的接收状态   
//    String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION";  
//    // create the deilverIntent parameter  
//    Intent deliverIntent = new Intent(DELIVERED_SMS_ACTION);  
//    PendingIntent deliverPI = PendingIntent.getBroadcast(context, 0,  
//           deliverIntent, 0);  
//    context.registerReceiver(new BroadcastReceiver() {  
//       @Override  
//       public void onReceive(Context _context, Intent _intent) {  
//           Toast.makeText(context,  
//      "收信人已经成功接收", Toast.LENGTH_SHORT)  
//      .show();  
//       }  
//    }, new IntentFilter(DELIVERED_SMS_ACTION));  
//    
    
}







