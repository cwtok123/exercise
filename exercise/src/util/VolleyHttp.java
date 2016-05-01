package util;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.baidu.mapapi.model.LatLng;
import com.example.exercise.AppApplication;

import android.R.array;
import android.R.string;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

public class VolleyHttp {
	
		public static void getPointElevation(double latitude,double longitude) {
				
		String url1 = "http://123.456.789.0:8080/?type=point&lat="+latitude+"&lng="+longitude;
		String url = "http://192.168.1.128:8089/pointtext/servlet/WelcomeUserServlet?lat="+latitude+"&lng="+longitude;
		
		JsonObjectRequest pointJson = new JsonObjectRequest(Method.GET,url,null,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// TODO 自动生成的方法存根
						Log.e("TAG", response.toString());
						
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO 自动生成的方法存根
						Log.e("TAG", error.getMessage(), error);
					}
				});
		pointJson.setTag("pointget");
		AppApplication.getHttpRequestQueue().add(pointJson);
		AppApplication.getHttpRequestQueue().start();
	}

	public void postLineElevation(List<LatLng>Linelist) throws JSONException {
		
				
	    JSONObject jsonlist = new JSONObject();
	    JSONArray searchArray = new JSONArray();
	    for(int i=0 ; i < Linelist.size() ;i++)
		    {
		    	JSONObject LL = new JSONObject();
		    	LL.put("lat", Linelist.get(i).latitude);
		    	LL.put("lng", Linelist.get(i).longitude);
		    	searchArray.put(LL);
		    }
	    jsonlist.put("search", searchArray);
	    
	    String url = "http://123.456.789.0:8080/?type=line";
		JsonObjectRequest lineJson = new JsonObjectRequest(Method.POST,url, 
	    		jsonlist , 
	    		new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						// TODO 自动生成的方法存根
						Log.e("TAG", response.toString());
						
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
	}
	
	
}
