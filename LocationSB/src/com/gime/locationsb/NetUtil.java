package com.gime.locationsb;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.util.Log;


public class NetUtil {

	public static String HttpPostData(String url, String buf) {
		
		String res = null;
		if( url == null || buf == null)
		{
			Log.i(LsbConst.LOG_TAG, "HttpPostData input null");
			return res;
		}
		Log.i(LsbConst.LOG_TAG, "HttpPostData "+url+"  "+buf);	
		try {
			HttpClient httpClient = new DefaultHttpClient(); 
			HttpPost httppost = new HttpPost(url);
			// 添加http头信息
			httppost.addHeader("Authorization", "your token"); // 认证token
			httppost.addHeader("Content-Type", "application/json; charset=utf-8");
			httppost.addHeader("User-Agent", "imgfornote");
			//HttpEntity entity = new UrlEncodedFormEntity(pairs,HTTP.UTF_8);//设置编码，防止中午乱码
			httppost.setEntity(new StringEntity(buf,HTTP.UTF_8));
			HttpResponse response;
			response = httpClient.execute(httppost);
			// 检验状态码，如果成功接收数据
			int code = response.getStatusLine().getStatusCode();
			Log.i(LsbConst.LOG_TAG, "response : "+code);	
			if (code == 200) {

				// String rev =
				// EntityUtils.toString(response.getEntity());//返回json格式： {"id":
				// "27JpL~j4vsL0LX00E00005","version": "abc"}
				// System.out.println(rev+"11111111");
				// obj = new JSONObject(rev);
				//
				// String id = obj.getString("id");
				// System.out.println(rev+"222222222");
				// String version = obj.getString("version");
				// System.out.println(rev+"333333333");
				res =  EntityUtils.toString(response.getEntity());
				res.trim();
				res = res.replaceAll("\r|\n", "");
				res = res.replaceAll(" ", "");
				res = res.replaceAll("	", "");
				//Log.i(LsbConst.LOG_TAG, "res:" + res);	
				return res;
			}
			
		} catch (ClientProtocolException e) {
			Log.i(LsbConst.LOG_TAG, e.toString());	
		} catch (IOException e) {
			Log.i(LsbConst.LOG_TAG, e.toString());	
		} catch (Exception e) {
			Log.i(LsbConst.LOG_TAG, e.toString());	
		}
		return res;
	}

}
