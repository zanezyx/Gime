package com.gime.locationsb;

import java.util.ArrayList;

import org.json.JSONArray;
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
				Log.i(LsbConst.LOG_TAG, "net thread running NET_REQUEST_QUERY");
				currSendBuf = LsbMgr.getInstance().getImei(mContext);
				if(currSendBuf!=null)
				{
					if(LsbMgr.getInstance().hasUncompletedOperation())
					{
						String res = NetUtil.HttpPostData(LsbConst.LSB_HTTP_URL_QUERY, currSendBuf);
						Log.i(LsbConst.LOG_TAG, "NET_REQUEST_QUERY res:"+res);
						if(res!=null)
						{
							if(!res.equals("none"))
							{
								JSONObject jsonObj;
								try {
									jsonObj = new JSONObject(res);
									// 得到指定json key对象的value对象
									JSONArray jarray = jsonObj.getJSONArray("locationlist");
									ArrayList<LocationOperation> arrayOp = new ArrayList<LocationOperation>();
									for(int i=0;i<jarray.length();i++)
									{
										JSONObject jo = jarray.getJSONObject(i);
										int id = jo.getInt("id");
										int type = jo.getInt("type");
										String number = jo.getString("number");
										double latitude = jo.getDouble("latitude");
										double longitude = jo.getDouble("longitude");
										LocationOperation op = new LocationOperation();
										op.setId(id);
										op.setLatitude(latitude);
										op.setLongitude(longitude);
										op.setLocationType(type);
										op.setLocationStatus(jo.getInt("locationStatus"));
										if(type == LsbConst.LOCATION_TYPE_PHONE)
										{
											op.setPhoneNumber(number);
										}else{
											op.setWechat(number);
										}
										arrayOp.add(op);
									}
					
									if (null!=mHandler) {
										if(arrayOp!=null && arrayOp.size()>0)
										{
											Message message = new Message();
											message.what = LsbConst.MSG_RECEIVE_LOCATION;
											message.obj = arrayOp;
											mHandler.sendMessage(message);
											for(LocationOperation op:arrayOp)
											{
												Log.i(LsbConst.LOG_TAG, "arrayOp op type:"+op.getLocationType());
												Log.i(LsbConst.LOG_TAG, "arrayOp op number:"+op.getPhoneNumber());
												Log.i(LsbConst.LOG_TAG, "arrayOp op wechat:"+op.getWechat());
												Log.i(LsbConst.LOG_TAG, "arrayOp op status:"+op.getLocationStatus());
												Log.i(LsbConst.LOG_TAG, "arrayOp op Latitude:"+op.getLatitude());
												Log.i(LsbConst.LOG_TAG, "arrayOp op Longitude:"+op.getLongitude());
											}
										}else{
											Log.i(LsbConst.LOG_TAG, "arrayOp null or size:0");
										}
									}
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									Log.i(LsbConst.LOG_TAG,
											"NET_REQUEST_QUERY res exception e:"
													+ e.toString());
									e.printStackTrace();
								}
							} else {
//								if (null != mHandler) {
//									Message message = new Message();
//									message.what = LsbConst.MSG_QUERY_LOCATION_NET_FAIL;
//									mHandler.sendMessage(message);
//								}
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
				Thread.sleep(1000);
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
