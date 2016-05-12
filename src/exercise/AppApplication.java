package exercise;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.baidu.mapapi.SDKInitializer;

import android.app.Application;

public class AppApplication extends Application {

	public static RequestQueue requestQueue;
	@Override
	public void onCreate() {
		super.onCreate();
		// 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
		SDKInitializer.initialize(this);
		requestQueue = Volley.newRequestQueue(this);
	}
	public static RequestQueue getHttpRequestQueue() {
		return requestQueue;
	}
}
