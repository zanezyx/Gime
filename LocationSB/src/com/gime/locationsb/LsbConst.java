package com.gime.locationsb;

public class LsbConst {

	public final static  String LOG_TAG = "LSB";
	public final static  String LSB_HTTP_URL_SEND_OP = "http://120.25.197.60/LocationSB/location.php";
	public final static  String LSB_HTTP_URL_QUERY = "http://120.25.197.60/LocationSB/query.php";
	public final static  String LSB_HTTP_URL_GET_LOCATION = "http://120.25.197.60/LocationSB/getlocation.php";
	
	//debug
//	public final static  String LSB_HTTP_URL_SEND_OP = "http://192.168.0.5/LocationSB/location.php";
//	public final static  String LSB_HTTP_URL_QUERY = "http://192.168.0.5/LocationSB/query.php";
//	public final static  String LSB_HTTP_URL_GET_LOCATION = "http://192.168.0.5/LocationSB/getlocation.php";
	
	
	public final static  int LOCATION_TYPE_PHONE = 0;
	public final static  int LOCATION_TYPE_WECHAT = 1;
	
	public final static  int LOCATION_STATE_NORMAL = 0;
	public final static  int LOCATION_STATE_LOCATION_WAIT = 1;
	public final static  int LOCATION_STATE_SUCCESS = 2;
	
	public final static  int MSG_RECEIVE_LOCATION = 1001;
	public final static  int MSG_ADD_LOCATION_NET_FAIL = 1002;
	public final static  int MSG_ADD_LOCATION_NET_SUCCESS = 1003;
	public final static  int MSG_ADD_LOCATION_NET_EXIST = 1004;
	public final static  int MSG_QUERY_LOCATION_NET_FAIL = 1005;
	public final static  int MSG_ACTIVE_APP_SUCCESS = 1006;
	public final static  int MSG_SHOW_POINTS = 1007;
	
	public final static  int NET_REQUEST_NONE = 0;
	public final static  int NET_REQUEST_QUERY = 1;
	public final static  int NET_REQUEST_SEND_OP = 2;
	public static final String WX_APP_ID = "wx381a65a215eaf7b0";
}
