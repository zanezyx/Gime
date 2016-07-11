package com.gime.locationsb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import android.support.v4.app.Fragment;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class MainActivity extends Activity {

	private TextView tvLocationStatus = null;
	private SimpleAdapter mAdapter;
	private ListView mListView;
	private EditText etPhone;
	private EditText etSmsContent;
	private LsbNetworkThread netThread = null;
	private EditText etWechat;
	private EditText etWechatContent;
	
	private Button btnPhone;
	private Button btnWechat;
	
	private LinearLayout llPhone;
	private LinearLayout llWechat;
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
//		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_main);
		initView();
		
	}

	private Handler mHandler = new Handler() {
		@SuppressLint("NewApi")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LsbConst.MSG_RECEIVE_LOCATION:
				Log.i(LsbConst.LOG_TAG, "mHandler receive MSG_RECEIVE_LOCATION msg");
				ArrayList<LocationOperation> opList = (ArrayList<LocationOperation>)msg.obj;
				for(LocationOperation op:opList)
				{
					LsbMgr.getInstance().completeOperation(op);
				}
				refreshListView();
				break;
			case LsbConst.MSG_QUERY_LOCATION_NET_FAIL:
			case LsbConst.MSG_ADD_LOCATION_NET_FAIL:
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.net_fail),
						Toast.LENGTH_SHORT).show();
				break;
			case LsbConst.MSG_ADD_LOCATION_NET_SUCCESS:
				LocationOperation op = (LocationOperation)msg.obj;
				int addId = msg.arg1;
                LsbMgr.getInstance().sendSms(addId, op);
    			LsbMgr.getInstance().addLocationOperation(op);
    			refreshListView();
				break;
			case LsbConst.MSG_ADD_LOCATION_NET_EXIST:
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.location_repeat),
						Toast.LENGTH_SHORT).show();
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	
	void initView() {

		btnPhone = (Button)findViewById(R.id.btnPhone);
		btnWechat = (Button)findViewById(R.id.btnWechat);
		
		llPhone = (LinearLayout)findViewById(R.id.llPhone);
		llWechat = (LinearLayout)findViewById(R.id.llWechat);
		
		selectWechatType(null);
		tvLocationStatus = (TextView) findViewById(R.id.tvlocationStatus);
		mListView = (ListView)findViewById(R.id.listView1);
		etPhone = (EditText)findViewById(R.id.et1);
		etSmsContent = (EditText)findViewById(R.id.et2);
		etPhone.clearFocus();
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
					Intent intent = new Intent(MainActivity.this, LsbMapActivity.class);
					intent.putExtra("latitude", op.getLatitude());
					intent.putExtra("longitude", op.getLongitude());
//					intent.putExtra("latitude", 22.526411);
//					intent.putExtra("longitude", 113.936918);
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

		String phone = etPhone.getText().toString();
		String wenhou = etSmsContent.getText().toString();
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
		
		final LocationOperation op = new LocationOperation();
		op.setLocationType(LsbConst.LOCATION_TYPE_PHONE);
		op.setImei(LsbMgr.getInstance().getImei(this));
		op.setLocationStatus(LsbConst.LOCATION_STATE_LOCATION_WAIT);
		op.setPhoneNumber(phone);
		op.setWenhou(wenhou);
		
		if(!LsbMgr.getInstance().checkIfContainOperation(op))
		{
	        AlertDialog.Builder builder=new AlertDialog.Builder(this);  //���寰���版��������  
	        builder.setTitle(getResources().getString(R.string.notice)); //璁剧疆���棰�  
	        builder.setMessage(getResources().getString(R.string.send_sms_text)); //璁剧疆���瀹�  
	        builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() { //璁剧疆纭�瀹�������  
	            @Override  
	            public void onClick(DialogInterface dialog, int which) {  
	                dialog.dismiss(); //��抽��dialog  
	    			sendLocationRequest(op);
	            }  
	        });  
	        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() { //璁剧疆���娑�������  
	            @Override  
	            public void onClick(DialogInterface dialog, int which) {  
	                dialog.dismiss();  
	            }  
	        });  
	  
	        //�����伴�借�剧疆瀹����浜�锛����寤哄苟��剧ず��烘��  
	        builder.create().show();  

		}else{
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.location_repeat),
					Toast.LENGTH_SHORT).show();
		}

	}

	private void refreshListView()
	{
		mAdapter = new SimpleAdapter(this, getData(), R.layout.list_item,
				new String[] { "target", "status" }, new int[] { R.id.tvTarget,
						R.id.tvStatus });
		mListView.setAdapter(mAdapter);
	}
	
	private void sendLocationRequest(final LocationOperation op)
	{
		if(netThread==null)
		{
			netThread = new LsbNetworkThread(this, mHandler);
			netThread.start();
		}
		final JSONObject jop = new JSONObject();
		try {
			jop.put("imei", op.getImei());
			jop.put("type", op.getLocationType());
			if(op.getLocationType()==LsbConst.LOCATION_TYPE_PHONE)
			{
				jop.put("number", op.getPhoneNumber());
			}else{
				jop.put("number", op.getWechat());
			}
			jop.put("wenhou", op.getWenhou());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String res = NetUtil.HttpPostData(LsbConst.LSB_HTTP_URL_SEND_OP, jop.toString());
				Log.i(LsbConst.LOG_TAG, "LSB_HTTP_URL_SEND_OP res:"+res);
				if(res==null)
				{
					if (null!=mHandler) {
						Message message = new Message();
						message.what = LsbConst.MSG_ADD_LOCATION_NET_FAIL;
						mHandler.sendMessage(message);
					}
					return;
				}
				if(res.contains("fail"))
				{
					if (null!=mHandler) {
						Message message = new Message();
						message.what = LsbConst.MSG_ADD_LOCATION_NET_FAIL;
						mHandler.sendMessage(message);
					}
				}else if(res.contains("exist"))
				{
					if (null!=mHandler) {
						Message message = new Message();
						message.what = LsbConst.MSG_ADD_LOCATION_NET_EXIST;
						mHandler.sendMessage(message);
					}
				}else{
					if (null!=mHandler) {
						
						try {
							Message message = new Message();
							message.what = LsbConst.MSG_ADD_LOCATION_NET_SUCCESS;
							message.obj = op;
							message.arg1 = Integer.parseInt(res);
							mHandler.sendMessage(message);
						} catch (Exception e) {
							// TODO: handle exception
							Log.i(LsbConst.LOG_TAG, "send add op request res exception:"+e.toString());
							Message message = new Message();
							message.what = LsbConst.MSG_ADD_LOCATION_NET_FAIL;
							mHandler.sendMessage(message);
						}
					}
				}
			}
		}).start();
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();

	}

	@Override
	protected void onResume() {
		super.onResume();
		netThread = new LsbNetworkThread(this, mHandler);
		netThread.start();
	}

	@Override
	protected void onPause() {
		super.onPause();
		netThread.setEnableRun(false);
		netThread = null;
	}
	
    private void sendSmsDialog(){  

    }  
    
    
    public void selectPhoneType(View v)
    {
    	LsbMgr.getInstance().setCurrLactionType(LsbConst.LOCATION_TYPE_PHONE);
    	btnPhone.setTextColor(Color.WHITE);
    	btnWechat.setTextColor(Color.GRAY);
    	llWechat.setVisibility(View.INVISIBLE);
    	llPhone.setVisibility(View.VISIBLE);
    }
    

    public void selectWechatType(View v)
    {
    	LsbMgr.getInstance().setCurrLactionType(LsbConst.LOCATION_TYPE_WECHAT);
    	btnWechat.setTextColor(Color.WHITE);
    	btnPhone.setTextColor(Color.GRAY);
    	llPhone.setVisibility(View.INVISIBLE);
    	llWechat.setVisibility(View.VISIBLE);
    }
    
    
}
