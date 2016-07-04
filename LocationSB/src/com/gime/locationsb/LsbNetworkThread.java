package com.gime.locationsb;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class LsbNetworkThread extends Thread {

	private Context mContext;
	private final static String TAG = "LsbNetworkThread";
	private Handler mHandler = null;
	private boolean enableRun = true;
	private int currNetRequestType;
	private String currSendBuf;
	private JSONObject jObjOperation;
	
	
	public LsbNetworkThread(Context context, Handler handler) {
		mContext = context;
		mHandler = handler;
		currNetRequestType = LsbConst.NET_REQUEST_NONE;
	}
	
	@Override
	public void run() {

		while(enableRun)
		{
			if(currNetRequestType==LsbConst.NET_REQUEST_QUERY)
			{
				//Log.i(LsbConst.LOG_TAG, "net thread running NET_REQUEST_QUERY");
				currSendBuf = LsbMgr.getInstance().getImei(mContext);
				if(currSendBuf!=null)
				{
					if(LsbMgr.getInstance().hasUncompletedOperation())
					{
						String res = NetUtil.HttpPostData(LsbConst.LSB_HTTP_URL_QUERY, currSendBuf);
						//Log.i(LsbConst.LOG_TAG, "NET_REQUEST_QUERY res:"+res);
						if(!res.equals("fail"))
						{
							JSONObject jsonObj;
							try {
								jsonObj = new JSONObject(res);
								// 得到指定json key对象的value对象
								JSONObject obj = jsonObj.getJSONObject("obj");
								int type = obj.getInt("type");
								String number = obj.getString("number");
								int latitude = obj.getInt("latitude");
								int longitude = obj.getInt("longitude");
								LocationOperation op = new LocationOperation();
								op.setLatitude(latitude);
								op.setLongitude(longitude);
								op.setLocationType(type);
								if(type == LsbConst.LOCATION_TYPE_PHONE)
								{
									op.setPhoneNumber(number);
								}else{
									op.setWechat(number);
								}
								if (null!=mHandler) {
									Message message = new Message();
									message.what = LsbConst.MSG_RECEIVE_LOCATION;
									message.obj = op;
									mHandler.sendMessage(message);
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
//								Log.i(LsbConst.LOG_TAG,
//										"NET_REQUEST_QUERY res exception e:"
//												+ e.toString());
								e.printStackTrace();
							}
						} else {
							if (null != mHandler) {
								Message message = new Message();
								message.what = LsbConst.MSG_QUERY_LOCATION_NET_FAIL;
								mHandler.sendMessage(message);
							}
						}
					}
				}
			}else if(currNetRequestType==LsbConst.NET_REQUEST_SEND_OP)
			{
				Log.i(LsbConst.LOG_TAG, "net thread running NET_REQUEST_SEND_OP");
				if(jObjOperation!=null)
				{
					String res = NetUtil.HttpPostData(LsbConst.LSB_HTTP_URL_SEND_OP, jObjOperation.toString());
					Log.i(LsbConst.LOG_TAG, "NET_REQUEST_SEND_OP res:"+res);
					if(res!=null)
					{
						if(res.equals("fail"))
						{
							if (null!=mHandler) {
								Message message = new Message();
								message.what = LsbConst.MSG_ADD_LOCATION_NET_FAIL;
								mHandler.sendMessage(message);
							}
						}else if(res.equals("exist"))
						{
							if (null!=mHandler) {
								Message message = new Message();
								message.what = LsbConst.MSG_ADD_LOCATION_NET_EXIST;
								mHandler.sendMessage(message);
							}
						}else if(res.equals("success"))
						{
							if (null!=mHandler) {
								Message message = new Message();
								message.what = LsbConst.MSG_ADD_LOCATION_NET_SUCCESS;
								mHandler.sendMessage(message);
							}
						}
					}

				}
				if(LsbMgr.getInstance().hasUncompletedOperation())
				{
					currNetRequestType = LsbConst.NET_REQUEST_QUERY;
				}else{
					currNetRequestType = LsbConst.NET_REQUEST_NONE;
				}
			}else if(currNetRequestType==LsbConst.NET_REQUEST_NONE)
			{
				Log.i(LsbConst.LOG_TAG, "net thread running NET_REQUEST_NONE");
				if(LsbMgr.getInstance().hasUncompletedOperation())
				{
					currNetRequestType = LsbConst.NET_REQUEST_QUERY;
				}
			}

			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void setEnableRun(boolean enableRun) {
		this.enableRun = enableRun;
	}

	public void setCurrNetRequestType(int currNetRequestType) {
		this.currNetRequestType = currNetRequestType;
	}

	public void setjObjOperation(JSONObject jObjOperation) {
		this.jObjOperation = jObjOperation;
	}
	
	
}
