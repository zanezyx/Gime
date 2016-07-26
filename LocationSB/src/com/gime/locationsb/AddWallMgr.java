package com.gime.locationsb;

import cn.waps.AppConnect;
import cn.waps.AppListener;
import cn.waps.UpdatePointsListener;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.LinearLayout;

public class AddWallMgr implements UpdatePointsListener {
	
	private Context mContext;
	static AddWallMgr mInstance = null;
	public static final int PASS_POINTS = 100;
	
	
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
		AppConnect.getInstance("APP_ID","APP_PID",mContext);

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
						queryPoints();
					}
				});
		AppConnect.getInstance(mContext).showOffers(mContext);
	}
	
	
	public void queryPoints()
	{
		AppConnect.getInstance(mContext).getPoints(this);

	}

	@Override
	public void getUpdatePoints(String arg0, int arg1) {
		// TODO Auto-generated method stub
		Log.i(LsbConst.LOG_TAG, "waps add update points "+arg0+":"+arg1);
		if(arg1>PASS_POINTS)
		{
			Log.i(LsbConst.LOG_TAG, "set app active");
			LsbMgr.getInstance().setAppActive(mContext);
		}
	}

	@Override
	public void getUpdatePointsFailed(String arg0) {
		// TODO Auto-generated method stub
		Log.i(LsbConst.LOG_TAG, "waps add update points failed");
	}
	
	public void showBannerAdd(Activity activity)
	{
		LinearLayout adlayout =(LinearLayout)activity.findViewById(R.id.AdLinearLayout);
		AppConnect.getInstance(mContext).showBannerAd( mContext, adlayout);

	}
	
}




