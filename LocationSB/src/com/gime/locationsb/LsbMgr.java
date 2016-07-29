package com.gime.locationsb;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

public class LsbMgr {
	
	public static final String LSB_CONFIG = "lsb_config";
	public static final String ACTIVE_FLAG = "active";
	public static final String TRY_FLAG = "try";
	static LsbMgr lsbMgr;
	private List<LocationOperation> operationList;
	private int currLactionType;
	private boolean isFreeVersion = false;
	
	
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
	
	public List<LocationOperation> getWaitingLocationOperations()
	{
		List<LocationOperation> opList = new ArrayList<LocationOperation>();
		for(LocationOperation op:operationList)
		{
			if(op.getLocationStatus()==LsbConst.LOCATION_STATE_LOCATION_WAIT)
			{
				opList.add(op);
			}
		}
		return opList;
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
				if(op.getId()==operation.getId())
				{
					if(op.getLocationType()==LsbConst.LOCATION_TYPE_PHONE)
					{
						if(op.getLatitude()!=0 && op.getLongitude()!=0
								&& op.getLocationStatus()==LsbConst.LOCATION_STATE_SUCCESS)
						{
							Log.i(LsbConst.LOG_TAG, "completeOperation latitude:"+op.getLatitude()
									+" longitude:"+op.getLongitude());
							operation.setLatitude(op.getLatitude());
							operation.setLongitude(op.getLongitude());
							operation.setLocationStatus(LsbConst.LOCATION_STATE_SUCCESS);
							res = true;
						}
					}else{
						Log.i(LsbConst.LOG_TAG, "completeOperation "+op.getWechat()+" "+operation.getWechat());
						Log.i(LsbConst.LOG_TAG, "completeOperation 1");
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
	 * ��存�ヨ����ㄧ��淇℃�ュ�ｅ�����淇�
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
		// ��峰�����淇＄�＄�����
		android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
		// ���������淇″��瀹癸�������虹��淇￠�垮害�����讹��
		List<String> divideContents = smsManager.divideMessage(message);
		for (String text : divideContents) {
			Log.i(LsbConst.LOG_TAG, "sendSMS text:"+text);
			if(text!=null)
			{
				smsManager.sendTextMessage(phoneNumber, null, text, null,null);
			}
		}
	}

	public int getCurrLactionType() {
		return currLactionType;
	}

	public void setCurrLactionType(int currLactionType) {
		this.currLactionType = currLactionType;
	}
	
	
    public static String format(String s){
  	  String str=s.replaceAll("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……& amp;*（）——+|{}【】‘；：”“’。，、？|-]", "");
  	  return str;
  	 } 
    
    
	/**
	 * 验证手机格式
	 */
	public boolean isMobileNO(String mobiles) {
		/*
		 * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
		 * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
		 * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
		 */
		String telRegex = "[1][358]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
		if (TextUtils.isEmpty(mobiles))
			return false;
		else
			return mobiles.matches(telRegex);
	}
	
	
	public boolean isAppActive(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				LSB_CONFIG, Activity.MODE_PRIVATE);
		// 使用getString方法获得value，注意第2个参数是value的默认值
		boolean isActive = sharedPreferences.getBoolean(ACTIVE_FLAG, false);
		return isActive;
	}

	public void setAppActive(Context context) {
		SharedPreferences mySharedPreferences = context.getSharedPreferences(
				LSB_CONFIG, Activity.MODE_PRIVATE);
		// 实例化SharedPreferences.Editor对象（第二步）
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		// 用putString的方法保存数据
		editor.putBoolean(ACTIVE_FLAG, true);
		// 提交当前数据
		editor.commit();
	}

	
	
	public boolean isFirstTry(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				LSB_CONFIG, Activity.MODE_PRIVATE);
		// 使用getString方法获得value，注意第2个参数是value的默认值
		boolean firstTry = sharedPreferences.getBoolean(TRY_FLAG, true);
		return firstTry;
	}

	public void setFirstTryOver(Context context) {
		SharedPreferences mySharedPreferences = context.getSharedPreferences(
				LSB_CONFIG, Activity.MODE_PRIVATE);
		// 实例化SharedPreferences.Editor对象（第二步）
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		// 用putString的方法保存数据
		editor.putBoolean(TRY_FLAG, false);
		// 提交当前数据
		editor.commit();
	}
	
	public void setFirstTryTrue(Context context) {
		SharedPreferences mySharedPreferences = context.getSharedPreferences(
				LSB_CONFIG, Activity.MODE_PRIVATE);
		// 实例化SharedPreferences.Editor对象（第二步）
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		// 用putString的方法保存数据
		editor.putBoolean(TRY_FLAG, true);
		// 提交当前数据
		editor.commit();
	}
	
	public boolean isFreeVersion() {
		return isFreeVersion;
	}

	public void setFreeVersion(boolean isFreeVersion) {
		this.isFreeVersion = isFreeVersion;
	}
}







