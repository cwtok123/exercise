package util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.CoordinateConverter.CoordType;
import com.baidu.platform.comapi.map.J;

import android.R.array;
import android.R.string;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import exercise.AppApplication;

public class VolleyHttp {
	
	    public static float h = 0;
	    public static elevationAndPointList hPoint = new elevationAndPointList();
	    
		public static float getPointElevation(double latitude,double longitude) {				
		String url = "http://192.168.253.4:8002/return.ashx?lat="+latitude+"&lon="+longitude;
		//后台改了接口
		JsonArrayRequest pointJson = new JsonArrayRequest(url,
				new Listener<JSONArray>() {

					@Override
					public void onResponse(JSONArray response) {
						// TODO 自动生成的方法存根
						Log.e("海拔json", response.toString());
						JSONObject info = response.optJSONObject(0);
						try {
							h = Float.parseFloat(info.getString("height"));
						} catch (NumberFormatException e) {
							// TODO 自动生成的 catch 块
							e.printStackTrace();
						} catch (JSONException e) {
							// TODO 自动生成的 catch 块
							e.printStackTrace();
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO 自动生成的方法存根
						Log.e("后台返回值", error.getMessage(), error);
						h=0;
						
					}
				});
		
		pointJson.setTag("pointget");
		AppApplication.getHttpRequestQueue().add(pointJson);
		AppApplication.getHttpRequestQueue().start();
		return h;
		
	}
		public static float getPointElevationTest(double latitude,double longitude) {				
		String url = "http://192.168.253.3:8088/pointtest/servlet/WelcomeUserServlet?lat="+latitude+"&lng="+longitude;
		
		JsonObjectRequest pointJsonTest = new JsonObjectRequest(Method.GET,url,null,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// TODO 自动生成的方法存根
						Log.e("TAG", response.toString());
						try {
							JSONArray info = response.getJSONArray("information");
							for(int i = 0;i<info.length();i++)
							{
								JSONObject elev = (JSONObject)info.opt(i);
								h = Float.parseFloat(elev.getString("elevation"));
							}
						} catch (JSONException e) {
							// TODO 自动生成的 catch 块
							e.printStackTrace();
						}
						
						
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO 自动生成的方法存根
						Log.e("测试数据返回值", error.getMessage(), error);
						h=0;
						
					}
				});

		
		pointJsonTest.setTag("pointget");
		AppApplication.getHttpRequestQueue().add(pointJsonTest);
		AppApplication.getHttpRequestQueue().start();
		return h;
		
	}
	public static elevationAndPointList postLineElevation(List<LatLng>Linelist) throws JSONException {
		
				
	    JSONObject jsonlist = new JSONObject();
	    JSONArray searchArray = new JSONArray();
	    for(int i=0 ; i < Linelist.size() ;i++)
		    {
		    	JSONObject LL = new JSONObject();
		    	LL.put("lat", BDtoWGS84.bd09ToWGS84(Linelist.get(i)).latitude);
		    	LL.put("lng", BDtoWGS84.bd09ToWGS84(Linelist.get(i)).longitude);
		    	searchArray.put(LL);
		    }
	    jsonlist.put("search", searchArray);
	    
	    String url = "http://123.456.789.0:8080/?type=line"
	    		+ "";
		JsonObjectRequest lineJson = new JsonObjectRequest(Method.POST,url, 
	    		jsonlist , 
	    		new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						// TODO 自动生成的方法存根
						hPoint.h.clear();
						hPoint.point.clear();
						Log.e("TAG", response.toString());
						try {
							JSONArray info = response.getJSONArray("information");
							for(int i = 0;i<info.length();i++)
							{
								JSONObject elev = (JSONObject)info.opt(i);
								float lineH = Float.parseFloat(elev.getString("elevation"));
								hPoint.h.add(lineH);
								
								double lat = Double.parseDouble(elev.getString("lat"));
								double lng = Double.parseDouble(elev.getString("lng"));
								LatLng p = new LatLng(lat, lng);
								
								//坐标转换
								CoordinateConverter converter = new CoordinateConverter();
								converter.from(CoordType.GPS);
								converter.coord(p);								
								hPoint.point.add(converter.convert());
								
							}
						} catch (JSONException e) {
							// TODO 自动生成的 catch 块
							e.printStackTrace();
							hPoint = null;
						}
					}
				}, 
	    		new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO 自动生成的方法存根
						Log.e("TAG", error.getMessage(), error);
					}
				});
		lineJson.setTag("linepost");
		AppApplication.getHttpRequestQueue().add(lineJson);
		AppApplication.getHttpRequestQueue().start();
		return hPoint;
	}
	
	
}
