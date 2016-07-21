package com.gime.locationsb;

import net.youmi.android.AdManager;
import net.youmi.android.offers.OffersManager;
import net.youmi.android.offers.PointsManager;
import android.content.Context;
import android.util.Log;

public class AddWallMgr {
	
	private Context mContext;
	static AddWallMgr mInstance = null;
	
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
		AdManager.getInstance(mContext).init("appId", "appSecret", true);
		OffersManager.getInstance(mContext).onAppLaunch();
	}
	
	public void destroy()
	{
		OffersManager.getInstance(mContext).onAppExit();
	}
	

	public void showAddWall()
	{
		OffersManager.getInstance(mContext).showOffersWall();
		//OffersManager.getInstance(Context context).showOffersWall(Interface_ActivityListener listener);
	}
	
	
//	public interface Interface_ActivityListener {
//
//	    /**
//	     * 全屏积分墙Activity 调用onDestory的时候回调，执行在ui线程中
//	     */
//	    public void onActivityDestroy(Context context);
//	}
	
	public float queryPoints()
	{
		float pointsBalance = PointsManager.getInstance(mContext).queryPoints();
		Log.i(LsbConst.LOG_TAG, "query youmi add points:"+pointsBalance);
		return pointsBalance;
	}
	
}




