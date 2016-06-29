package com.gime.locationsb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class MainActivity extends Activity {

	private TextView tvLocationStatus = null;
	private SimpleAdapter mAdapter;
	private ListView mListView;
	private EditText et1;
	private EditText et2;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		initView();
	}

	
	void initView() {

		tvLocationStatus = (TextView) findViewById(R.id.tvlocationStatus);
		mListView = (ListView)findViewById(R.id.listView1);
		et1 = (EditText)findViewById(R.id.et1);
		et2 = (EditText)findViewById(R.id.et2);
		mAdapter = new SimpleAdapter(this, getData(), R.layout.list_item,
				new String[] { "target", "status"}, new int[] {
						R.id.tvTarget, R.id.tvStatus });
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				List<LocationOperation> opList = LsbMgr.getInstance().getOperaionList();
				LocationOperation op = (LocationOperation)opList.get(arg2);
				if(op.getLocationStatus()==LsbConst.LOCATION_STATE_SUCCESS)
				{
					Intent intent = new Intent(MainActivity.this, MapActivity.class);
					intent.putExtra("latitude", op.getLatitude());
					intent.putExtra("longitude", op.getLongitude());
					startActivity(intent);
				}else{
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.please_wait),
							Toast.LENGTH_SHORT).show();
				}
			}
			
		});
	}

	
	private List<Map<String, Object>> getData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<LocationOperation> opList = LsbMgr.getInstance().getOperaionList();
		
		for(LocationOperation op:opList)
		{
			Map<String, Object> map = new HashMap<String, Object>();
			if(op.getLocationType()==LsbConst.LOCATION_TYPE_PHONE)
			{
				map.put("target", getResources().getString(R.string.phone)+" "+op.getPhoneNumber());
			}else{
				map.put("target", getResources().getString(R.string.wechat)+" "+op.getWechat());
			}
			if(op.getLocationStatus()==LsbConst.LOCATION_STATE_LOCATION_WAIT)
			{
				map.put("status", getResources().getString(R.string.location_wait));
			}else if(op.getLocationStatus()==LsbConst.LOCATION_STATE_SUCCESS)
			{
				map.put("status", getResources().getString(R.string.location_suscess));
			}else{
				map.put("status", "");
			}
			
			list.add(map);
		}

		return list;
	}

	
	public void addLocationOperation(View v) {

		String phone = et1.getText().toString();
		String wenhou = et2.getText().toString();
		if(phone==null || phone.equals(""))
		{
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.input_phone),
					Toast.LENGTH_SHORT).show();
			return;
		}
		if(phone.length()<11)
		{
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.input_correct_phone),
					Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(wenhou==null || wenhou.equals(""))
		{
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.input_wenhou1),
					Toast.LENGTH_SHORT).show();
			return;
		}
		
		LocationOperation op = new LocationOperation();
		op.setLocationType(LsbConst.LOCATION_TYPE_PHONE);
		op.setImei(LsbMgr.getInstance().getImei(this));
		op.setLocationStatus(LsbConst.LOCATION_STATE_LOCATION_WAIT);
		op.setPhoneNumber(phone);
		op.setWenhou(wenhou);
		
		if(!LsbMgr.getInstance().checkIfContainOperation(op))
		{
			LsbMgr.getInstance().addLocationOperation(op);
			mAdapter = new SimpleAdapter(this, getData(), R.layout.list_item,
					new String[] { "target", "status" }, new int[] { R.id.tvTarget,
							R.id.tvStatus });
			mListView.setAdapter(mAdapter);
		}else{
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.location_repeat),
					Toast.LENGTH_SHORT).show();
		}

	}

	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();

	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	protected void onPause() {
		super.onPause();

	}

}
