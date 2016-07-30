package com.gime.locationsb;

import cn.waps.AppConnect;
import cn.waps.AppListener;
import cn.waps.UpdatePointsListener;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

public class AddWallMgr implements UpdatePointsListener {
	
	private Context mContext;
	static AddWallMgr mInstance = null;
	public static final int PASS_POINTS = 100;
	private int mPoints = 0;
	private Handler mHandler;
	private boolean needShowPoint = false;


	AddWallMgr(Context context) {
		// TODO Auto-generated constructor stub
		this.mContext = context;
	}
	
	public static AddWallMgr getInstance(Context context)
	{
		if(mInstance==null)
		{
			mInstance = new AddWallMgr(context);
		}
		return mInstance;
	}
	
	
	public void init()
	{
		AppConnect.getInstance(mContext);

	}
	
	public void destroy()
	{
		AppConnect.getInstance(mContext).close();

	}
	
	public void showAddWall() {
		
		// 设置关闭积分墙癿监听接口,必须在showOffers接口之前调用
		AppConnect.getInstance(mContext).setOffersCloseListener(
				new AppListener() {
					@Override
					public void onOffersClose() {
						// TODO 关闭积分墙时癿操作代码
						Log.i(LsbConst.LOG_TAG, "waps add wall closed");
						queryPoints(true);
					}
				});
		AppConnect.getInstance(mContext).showOffers(mContext);
	}
	
	
	public void queryPoints(boolean needShow)
	{
		needShowPoint = needShow;
		AppConnect.getInstance(mContext).getPoints(this);
	}

	
	@Override
	public void getUpdatePoints(String arg0, int arg1) {
		// TODO Auto-generated method stub
		Log.i(LsbConst.LOG_TAG, "waps add update points "+arg0+":"+arg1);
		mPoints = arg1;
		if(mHandler!=null)
		{
			if(needShowPoint)
			{
				Message message = new Message();
				message.what = LsbConst.MSG_SHOW_POINTS;
				message.arg1 = arg1;
				mHandler.sendMessage(message);
			}

			if(arg1>PASS_POINTS)
			{
				Message message1 = new Message();
				message1.what = LsbConst.MSG_ACTIVE_APP_SUCCESS;
				mHandler.sendMessage(message1);
				spendPoints(PASS_POINTS);
			}
		}
	}

	@Override
	public void getUpdatePointsFailed(String arg0) {
		// TODO Auto-generated method stub
		Log.i(LsbConst.LOG_TAG, "waps add update points failed");
	}
	
	public void showBannerAdd(Activity activity)
	{
//		LinearLayout adlayout =(LinearLayout)activity.findViewById(R.id.AdLinearLayout);
//		AppConnect.getInstance(mContext).showBannerAd( mContext, adlayout);

	}
	
	public void spendPoints(int points)
	{
		AppConnect.getInstance(mContext).spendPoints(points,this);
	}
	
	public void checkUpdate()
	{
		AppConnect.getInstance(mContext).checkUpdate(mContext);
	}
	
	public int getmPoints() {
		return mPoints;
	}

	
	
	public void setmPoints(int mPoints) {
		this.mPoints = mPoints;
	}
	
	public Handler getmHandler() {
		return mHandler;
	}

	public void setmHandler(Handler mHandler) {
		this.mHandler = mHandler;
	}
}




