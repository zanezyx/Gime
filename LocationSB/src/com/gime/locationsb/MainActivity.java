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
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
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
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;
import android.provider.ContactsContract;

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
	private TextView tvInputWechat = null;
	private Button btnStart;
	private ProgressBar progressBar;
	private LinearLayout llPhone;
	private LinearLayout llWechat;
	private IWXAPI api;
	public static final int REPUEST_CODE_CONTACTS = 0;
	public static final int REPUEST_CODE_SEL_SMS_TEXT = 1;
	public static final int REPUEST_CODE_SEL_WX_TEXT = 2;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		initView();
		registerToWX();
		AddWallMgr.getInstance(this).init();
	}

	private Handler mHandler = new Handler() {
		@SuppressLint("NewApi")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LsbConst.MSG_RECEIVE_LOCATION:
				Log.i(LsbConst.LOG_TAG,
						"mHandler receive MSG_RECEIVE_LOCATION msg");
				ArrayList<LocationOperation> opList = (ArrayList<LocationOperation>) msg.obj;
				for (LocationOperation op : opList) {
					LsbMgr.getInstance().completeOperation(op);
				}
				refreshListView();
				break;
			case LsbConst.MSG_QUERY_LOCATION_NET_FAIL:
			case LsbConst.MSG_ADD_LOCATION_NET_FAIL:
				Toast.makeText(getApplicationContext(),
						getResources().getString(R.string.net_fail),
						Toast.LENGTH_SHORT).show();
				progressBar.setVisibility(View.GONE);
				break;
			case LsbConst.MSG_ADD_LOCATION_NET_SUCCESS:
				LocationOperation op = (LocationOperation) msg.obj;
				int addId = msg.arg1;
				op.setId(addId);
				if (op.getLocationType() == LsbConst.LOCATION_TYPE_WECHAT) {
					sendWxFriend(addId, op);
				} else {
					LsbMgr.getInstance().sendSms(addId, op);
				}

				LsbMgr.getInstance().addLocationOperation(op);
				refreshListView();
				if(LsbMgr.getInstance().isFirstTry(MainActivity.this))
				{
					LsbMgr.getInstance().setFirstTryOver(MainActivity.this);
				}
				progressBar.setVisibility(View.GONE);
				break;
			case LsbConst.MSG_ADD_LOCATION_NET_EXIST:
				Toast.makeText(getApplicationContext(),
						getResources().getString(R.string.location_repeat),
						Toast.LENGTH_SHORT).show();
				progressBar.setVisibility(View.GONE);
				break;
			}
			super.handleMessage(msg);
		}
	};

	void initView() {

		btnPhone = (Button) findViewById(R.id.btnPhone);
		btnWechat = (Button) findViewById(R.id.btnWechat);
		btnStart = (Button) findViewById(R.id.btnStart);
		llPhone = (LinearLayout) findViewById(R.id.llPhone);
		llWechat = (LinearLayout) findViewById(R.id.llWechat);
		tvInputWechat = (TextView) findViewById(R.id.tvInputWxMsg);
		tvLocationStatus = (TextView) findViewById(R.id.tvlocationStatus);
		mListView = (ListView) findViewById(R.id.listView1);
		etPhone = (EditText) findViewById(R.id.et1);
		etSmsContent = (EditText) findViewById(R.id.et2);
		etPhone.clearFocus();
		etWechat = (EditText) findViewById(R.id.et11);
		etWechatContent = (EditText) findViewById(R.id.et21);
		progressBar = (ProgressBar)findViewById(R.id.progressBar1);
		mAdapter = new SimpleAdapter(this, getData(), R.layout.list_item,
				new String[] { "target", "status" }, new int[] { R.id.tvTarget,
						R.id.tvStatus });
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				List<LocationOperation> opList = LsbMgr.getInstance()
						.getOperaionList();
				LocationOperation op = (LocationOperation) opList.get(arg2);
				if (op.getLocationStatus() == LsbConst.LOCATION_STATE_SUCCESS) {
					Intent intent = new Intent(MainActivity.this,
							LsbMapActivity.class);
					intent.putExtra("latitude", op.getLatitude());
					intent.putExtra("longitude", op.getLongitude());
					// intent.putExtra("latitude", 22.526411);
					// intent.putExtra("longitude", 113.936918);
					startActivity(intent);
				} else {
					Toast.makeText(getApplicationContext(),
							getResources().getString(R.string.please_wait),
							Toast.LENGTH_SHORT).show();
				}
			}

		});
		selectPhoneType(null);
	}

	private List<Map<String, Object>> getData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<LocationOperation> opList = LsbMgr.getInstance().getOperaionList();

		for (LocationOperation op : opList) {
			Map<String, Object> map = new HashMap<String, Object>();
			if (op.getLocationType() == LsbConst.LOCATION_TYPE_PHONE) {
				map.put("target", getResources().getString(R.string.phone)
						+ " " + op.getPhoneNumber());
			} else {
				map.put("target", getResources().getString(R.string.wechat)
						+ " " + op.getWechat());
			}
			if (op.getLocationStatus() == LsbConst.LOCATION_STATE_LOCATION_WAIT) {
				map.put("status",
						getResources().getString(R.string.location_wait));
			} else if (op.getLocationStatus() == LsbConst.LOCATION_STATE_SUCCESS) {
				map.put("status",
						getResources().getString(R.string.location_suscess));
			} else {
				map.put("status", "");
			}

			list.add(map);
		}

		return list;
	}

	public void addLocationOperation(View v) {

		if(LsbMgr.getInstance().isAppActive(this) || LsbMgr.getInstance().isFreeVersion() 
				|| LsbMgr.getInstance().isFirstTry(this))
		{
			if (LsbMgr.getInstance().getCurrLactionType() == LsbConst.LOCATION_TYPE_PHONE) {
				phoneLocationStart();
			} else {
				wechatLocationStart();
			}
		}else{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getResources().getString(R.string.sw_active));
			builder.setMessage(getResources().getString(R.string.sw_active_tip));
			builder.setPositiveButton(getResources().getString(R.string.download_app),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							AddWallMgr.getInstance(MainActivity.this).showAddWall();
						}
					});
			builder.setNegativeButton(getResources().getString(R.string.cancel),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});

			builder.create().show();
		}
	}

	public void wechatLocationStart() {
		String wechatName = etWechat.getText().toString();
		String wenhou = etWechatContent.getText().toString();

		if (wechatName == null || wechatName.equals("")) {
			Toast.makeText(getApplicationContext(),
					getResources().getString(R.string.input_wechat),
					Toast.LENGTH_SHORT).show();
			return;
		}

		if (wenhou == null || wenhou.equals("")) {
			Toast.makeText(getApplicationContext(),
					getResources().getString(R.string.input_wechat_msg),
					Toast.LENGTH_SHORT).show();
			return;
		}
		final LocationOperation op = new LocationOperation();
		op.setLocationType(LsbConst.LOCATION_TYPE_WECHAT);
		op.setImei(LsbMgr.getInstance().getImei(this));
		op.setLocationStatus(LsbConst.LOCATION_STATE_LOCATION_WAIT);
		op.setPhoneNumber("100");
		op.setWechat(wechatName);
		op.setWenhou(wenhou);
		sendLocationRequest(op);

	}

	public void phoneLocationStart() {

		String phone = etPhone.getText().toString();
		String wenhou = etSmsContent.getText().toString();
		if (phone == null || phone.equals("")) {

			Toast.makeText(getApplicationContext(),
					getResources().getString(R.string.input_phone),
					Toast.LENGTH_SHORT).show();

			return;
		}
		
		if (!LsbMgr.getInstance().isMobileNO(phone)) {
			Toast.makeText(getApplicationContext(),
					getResources().getString(R.string.input_correct_phone),
					Toast.LENGTH_SHORT).show();
			return;
		}

		if (wenhou == null || wenhou.equals("")) {
			Toast.makeText(getApplicationContext(),
					getResources().getString(R.string.input_wenhou),
					Toast.LENGTH_SHORT).show();
			return;
		}

		final LocationOperation op = new LocationOperation();
		op.setLocationType(LsbConst.LOCATION_TYPE_PHONE);
		op.setImei(LsbMgr.getInstance().getImei(this));
		op.setLocationStatus(LsbConst.LOCATION_STATE_LOCATION_WAIT);
		op.setPhoneNumber(phone);
		op.setWenhou(wenhou);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getResources().getString(R.string.notice));
		builder.setMessage(getResources().getString(R.string.send_sms_text));
		builder.setPositiveButton(getResources().getString(R.string.ok),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						sendLocationRequest(op);
					}
				});
		builder.setNegativeButton(getResources().getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.create().show();
	}

	private void refreshListView() {
		mAdapter = new SimpleAdapter(this, getData(), R.layout.list_item,
				new String[] { "target", "status" }, new int[] { R.id.tvTarget,
						R.id.tvStatus });
		mListView.setAdapter(mAdapter);
	}

	private void sendLocationRequest(final LocationOperation op) {
		progressBar.setVisibility(View.VISIBLE);
		if (netThread == null) {
			netThread = new LsbNetworkThread(this, mHandler);
			netThread.start();
		}
		final JSONObject jop = new JSONObject();
		try {
			jop.put("imei", op.getImei());
			jop.put("type", op.getLocationType());
			if (op.getLocationType() == LsbConst.LOCATION_TYPE_PHONE) {
				jop.put("number", op.getPhoneNumber());
			} else {
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
				String res = NetUtil.HttpPostData(
						LsbConst.LSB_HTTP_URL_SEND_OP, jop.toString());
				Log.i(LsbConst.LOG_TAG, "LSB_HTTP_URL_SEND_OP res:" + res);
				if (res == null) {
					if (null != mHandler) {
						Message message = new Message();
						message.what = LsbConst.MSG_ADD_LOCATION_NET_FAIL;
						mHandler.sendMessage(message);
					}
					return;
				}
				if (res.contains("fail")) {
					if (null != mHandler) {
						Message message = new Message();
						message.what = LsbConst.MSG_ADD_LOCATION_NET_FAIL;
						mHandler.sendMessage(message);
					}
				} else if (res.contains("exist")) {
					if (null != mHandler) {
						Message message = new Message();
						message.what = LsbConst.MSG_ADD_LOCATION_NET_EXIST;
						mHandler.sendMessage(message);
					}
				} else {
					if (null != mHandler) {

						try {
							Message message = new Message();
							message.what = LsbConst.MSG_ADD_LOCATION_NET_SUCCESS;
							message.obj = op;
							message.arg1 = Integer.parseInt(res);
							mHandler.sendMessage(message);
						} catch (Exception e) {
							// TODO: handle exception
							Log.i(LsbConst.LOG_TAG,
									"send add op request res exception:"
											+ e.toString());
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
		AddWallMgr.getInstance(this).destroy();

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

	private void sendSmsDialog() {

	}

	public void selectPhoneType(View v) {
		LsbMgr.getInstance().setCurrLactionType(LsbConst.LOCATION_TYPE_PHONE);
		btnPhone.setTextColor(Color.BLACK);
		btnWechat.setTextColor(Color.GRAY);
		llWechat.setVisibility(View.INVISIBLE);
		llPhone.setVisibility(View.VISIBLE);
		btnStart.setText(getResources()
				.getString(R.string.start_location_phone));
	}

	public void selectWechatType(View v) {
		LsbMgr.getInstance().setCurrLactionType(LsbConst.LOCATION_TYPE_WECHAT);
		btnWechat.setTextColor(Color.BLACK);
		btnPhone.setTextColor(Color.GRAY);
		llPhone.setVisibility(View.INVISIBLE);
		llWechat.setVisibility(View.VISIBLE);
		tvInputWechat.setText(getResources().getString(
				R.string.input_wechat_msg));
		btnStart.setText(getResources()
				.getString(R.string.start_location_wexin));
	}

	public void selectContact(View v) {

		Intent intent = new Intent(Intent.ACTION_PICK,
				ContactsContract.Contacts.CONTENT_URI);
		MainActivity.this.startActivityForResult(intent, REPUEST_CODE_CONTACTS);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REPUEST_CODE_CONTACTS:
			if (resultCode == RESULT_OK) {
				Uri contactData = data.getData();
				Cursor cursor = managedQuery(contactData, null, null, null,
						null);
				cursor.moveToFirst();
				String num = this.getContactPhone(cursor);
				// show.setText("所选手机号为：" + num);
				etPhone.setText("" + num);
			}
			break;
		case REPUEST_CODE_SEL_SMS_TEXT:
			if (resultCode == RESULT_OK) {
				String text = data.getStringExtra("text");
				etSmsContent.setText(text);
			}
			break;
		case REPUEST_CODE_SEL_WX_TEXT:
			if (resultCode == RESULT_OK) {
				String text = data.getStringExtra("text");
				etWechatContent.setText(text);
			}
			break;
		default:
			break;
		}
	}

	private String getContactPhone(Cursor cursor) {
		// TODO Auto-generated method stub
		int phoneColumn = cursor
				.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
		int phoneNum = cursor.getInt(phoneColumn);
		String result = "";
		if (phoneNum > 0) {
			// 获得联系人的ID号
			int idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID);
			String contactId = cursor.getString(idColumn);
			// 获得联系人电话的cursor
			Cursor phone = getContentResolver().query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
					null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "="
							+ contactId, null, null);
			if (phone.moveToFirst()) {
				for (; !phone.isAfterLast(); phone.moveToNext()) {
					int index = phone
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
					int typeindex = phone
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
					int phone_type = phone.getInt(typeindex);
					String phoneNumber = phone.getString(index);
					result = phoneNumber;
					// switch (phone_type) {//此处请看下方注释
					// case 2:
					// result = phoneNumber;
					// break;
					//
					// default:
					// break;
					// }
				}
				if (!phone.isClosed()) {
					phone.close();
				}
			}
		}
		String res = LsbMgr.format(result);
		return res;
	}

	public void clearLocationList(View view) {
		
		if (LsbMgr.getInstance().getOperaionList().size() == 0) {
			Toast.makeText(getApplicationContext(),
					getResources().getString(R.string.list_empty),
					Toast.LENGTH_SHORT).show();
			return;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getResources().getString(R.string.notice));
		builder.setMessage(getResources().getString(R.string.comfirm_del));
		builder.setPositiveButton(getResources().getString(R.string.ok),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						if (LsbMgr.getInstance().getOperaionList().size() > 0) {
							LsbMgr.getInstance().getOperaionList().clear();
							refreshListView();
						} else {
							Toast.makeText(getApplicationContext(),
									getResources().getString(R.string.list_empty),
									Toast.LENGTH_SHORT).show();
						}
					}
				});
		builder.setNegativeButton(getResources().getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.create().show();
	}

	
	public void toSelectMsgText(View view) {
		Intent intent = new Intent(MainActivity.this, SelectTextActivity.class);
		//intent.putExtra("data", "传递给登录界面的数据");
		startActivityForResult(intent, REPUEST_CODE_SEL_SMS_TEXT);
	}

	
	public void toSelectWxText(View view) {
		Intent intent = new Intent(MainActivity.this, SelectTextActivity.class);
		//intent.putExtra("data", "传递给登录界面的数据");
		startActivityForResult(intent, REPUEST_CODE_SEL_WX_TEXT);
	}
	
	
	// weixin api use

	private void registerToWX() {
		api = WXAPIFactory.createWXAPI(this, LsbConst.WX_APP_ID, true);
		api.registerApp(LsbConst.WX_APP_ID);
	}

	private void sendTextToWxFriend(String text) {
		try {
			WXTextObject textObj = new WXTextObject();
			textObj.text = text;

			WXMediaMessage msg = new WXMediaMessage();
			msg.mediaObject = textObj;
			msg.setThumbImage(null);
			msg.mediaTagName = "aa";
			msg.title = "cc";
			msg.messageAction = "bb";
			msg.messageExt = "dd";
			msg.description = text;
			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = buildTransaction("text");
			req.message = msg;
			req.scene = SendMessageToWX.Req.WXSceneSession;
			api.sendReq(req);
		} catch (Exception e) {
			// TODO: handle exception
			Log.i(LsbConst.LOG_TAG, "sendTextToWxFriend e:" + e.toString());
		}

	}

	
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis())
				: type + System.currentTimeMillis();
	}

	
	public void sendWxFriend(int addId, LocationOperation op) {
		String wxText = null;
		if (addId < 0 || op == null) {
			return;
		}
		wxText = LsbConst.LSB_HTTP_URL_GET_LOCATION + "?id=" + addId + " "
				+ op.getWenhou();
		Log.i(LsbConst.LOG_TAG, "sendWxFriend text:" + wxText);
		sendTextToWxFriend(wxText);
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getResources().getString(R.string.notice));
		builder.setMessage(getResources().getString(R.string.quit_comfirm_tip));
		builder.setPositiveButton(getResources().getString(R.string.quit),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						MainActivity.super.finish();
					}
				});
		builder.setNegativeButton(getResources().getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.create().show();
		
	}
	    
}
