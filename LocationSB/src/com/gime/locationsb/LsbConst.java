package com.gime.locationsb;

public class LsbConst {

	public final static  String LOG_TAG = "LSB";
	public final static  String LSB_HTTP_URL_SEND_OP = "http://www.baidu.com";
	public final static  String LSB_HTTP_URL_QUERY = "http://www.baidu.com";
	public final static  int LOCATION_TYPE_PHONE = 0;
	public final static  int LOCATION_TYPE_WECHAT = 1;
	
	public final static  int LOCATION_STATE_NORMAL = 0;
	public final static  int LOCATION_STATE_LOCATION_WAIT = 1;
	public final static  int LOCATION_STATE_SUCCESS = 2;
	
	public final static  int MSG_RECEIVE_LOCATION = 1001;
	public final static  int MSG_ADD_LOCATION_NET_FAIL = 1002;
	public final static  int MSG_QUERY_LOCATION_NET_FAIL = 1003;
	
	public final static  int NET_REQUEST_NONE = 0;
	public final static  int NET_REQUEST_SEND_OP = 1;
	public final static  int NET_REQUEST_QUERY = 2;
}
