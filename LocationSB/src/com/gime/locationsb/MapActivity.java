package com.gime.locationsb;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import android.support.v4.app.Fragment;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.os.Build;

public class MapActivity extends Activity {

	private MapView mMapView = null;
	private BaiduMap mBaiduMap = null;	
	private double latitude;
	private double longitude;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
				WindowManager.LayoutParams. FLAG_FULLSCREEN);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_map);
		Intent intent = getIntent();
		latitude = (double)intent.getDoubleExtra("latitude", -1);
		longitude = (double)intent.getDoubleExtra("longitude", -1);
		initView();
	}
	
	
	void initView() {
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();  
//		OverlayOptions options = new MarkerOptions()
//	    .position(llA)  //设置marker的位置
//	    .icon(bdA)  //设置marker图标
//	    .zIndex(9)  //设置marker所在层级
//	    .draggable(true);  //设置手势拖拽
//		//将marker添加到地图上
//		marker = (Marker) (mBaiduMap.addOverlay(options));
		if(latitude!=-1 && longitude!=-1)
		{
			showLocation();
		}
	}

	
	public void showLocation() {

		//定义Maker坐标点  
		LatLng point = new LatLng(latitude, longitude);  
		//构建Marker图标  
		BitmapDescriptor bitmap = BitmapDescriptorFactory  
		    .fromResource(R.drawable.icon_marker);  
		//构建MarkerOption，用于在地图上添加Marker  
		OverlayOptions option = new MarkerOptions()  
		    .position(point)  
		    .icon(bitmap);  
		//在地图上添加Marker，并显示  
		mBaiduMap.addOverlay(option);
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		mMapView.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		mMapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		mMapView.onPause();
	}

}
