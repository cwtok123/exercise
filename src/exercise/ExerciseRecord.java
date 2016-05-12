package exercise;

import java.util.ArrayList;
import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.OnEntityListener;
import com.baidu.trace.OnStartTraceListener;
import com.baidu.trace.OnStopTraceListener;
import com.baidu.trace.Trace;
import com.example.exercise.R;

import android.app.Activity;
import android.graphics.Color;

import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;
import baidu.trackutils.GsonService;
import baidu.trackutils.RealtimeTrackData;
import util.GetExerInfo;

public class ExerciseRecord extends Activity{
	//页面相关
	private MapView mMapView = null;
	private BaiduMap mBaiduMap = null;	
	public Button btn1;
	public Button btn2;
	public Button btn3;
	public TextView speed;
	public TextView distance;
	public Chronometer time;
	private long recordingTime = 0;
	
	int gatherInterval = 2;  //位置采集周期 (s)
    int packInterval = 10;  //打包周期 (s)
    
    static String entityName = "小米4c";  // entity标识
    static long serviceId = 115376;// 鹰眼服务ID
    
    int traceType = 2;  //轨迹服务类型
    private static OnStartTraceListener startTraceListener = null;  //开启轨迹服务监听器
    private static OnStopTraceListener stopTraceListener = null;
    

	private static OnEntityListener entityListener = null;
	private RefreshThread refreshThread = null;  //刷新地图线程以获取实时点
	private static MapStatusUpdate msUpdate = null;
	private static BitmapDescriptor realtimeBitmap;  //图标
	private static OverlayOptions overlay;  //覆盖物
	private static List<LatLng> pointList = new ArrayList<LatLng>();  //定位点的集合
	
	
	private static PolylineOptions polyline = null;  //路线覆盖物
	
	
    private Trace trace;  // 实例化轨迹服务
    private LBSTraceClient client;  // 实例化轨迹服务客户端


	
    //*定位相关
	LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    BitmapDescriptor mCurrentMarker;
    private boolean isFirstLoc = true;
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exercise_record);
		btn1 = (Button) findViewById(R.id.btn_exerRecordBegin);
		btn2 = (Button) findViewById(R.id.btn_exerSuspended);
		btn3 = (Button) findViewById(R.id.btn_exerRecordEnd);
		speed = (TextView) findViewById(R.id.text_speed);
		distance = (TextView) findViewById(R.id.text_distance);
		time = (Chronometer) findViewById(R.id.text_time);
				
		mMapView = (MapView)findViewById(R.id.map_record);
		mBaiduMap = mMapView.getMap();
		
		
		// 开启定位图层初始化
        mBaiduMap.setMyLocationEnabled(true);
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
        
        init();
		button();
		

	}
    @Override  
    protected void onDestroy() {  
        super.onDestroy();  
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理  
        mMapView.onDestroy(); 
        initOnStopTraceListener();
        
    }  
    @Override  
    protected void onResume() {  
        super.onResume();  
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理  
        mMapView.onResume();  
        }  
    @Override  
    protected void onPause() {  
        super.onPause();  
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理  
        mMapView.onPause();
        
        }

	/**
	 * 初始化各个参数
	 */
	 private void init() {
		 
		 mMapView.showZoomControls(false);
		 
		 
         client = new LBSTraceClient(getApplicationContext());  //实例化轨迹服务客户端
         
         trace = new Trace(getApplicationContext(), serviceId, entityName, traceType);  //实例化轨迹服务
         
         client.setInterval(gatherInterval, packInterval);  //设置位置采集和打包周期
         
	 }
	 
	 
	 /**
	  * 初始化设置实体状态监听器
	  */
	 private void initOnEntityListener(){
		 
		 //实体状态监听器
		 entityListener = new OnEntityListener(){

			@Override
			public void onRequestFailedCallback(String arg0) {
				Looper.prepare();
				Toast.makeText(
						getApplicationContext(), 
						"entity请求失败的回调接口信息："+arg0, 
						Toast.LENGTH_SHORT)
						.show();
				Looper.loop();
			}
			
			@Override
			public void onQueryEntityListCallback(String arg0) {
				/**
				 * 查询实体集合回调函数，此时调用实时轨迹方法
				 */
				showRealtimeTrack(arg0); 
				
			}
			
			
		 };
	 }
	 
	 
	 
	/** 追踪开始 */
	private void initOnStartTraceListener() {		
		// 实例化开启轨迹服务回调接口
		startTraceListener = new OnStartTraceListener() {
			// 开启轨迹服务回调接口（arg0 : 消息编码，arg1 : 消息内容，详情查看类参考）
			@Override
			public void onTraceCallback(int arg0, String arg1) {
				Log.i("TAG", "onTraceCallback=" + arg1);
				if(arg0 == 0 || arg0 == 10006){
					startRefreshThread(true);
				}
			}

			// 轨迹服务推送接口（用于接收服务端推送消息，arg0 : 消息类型，arg1 : 消息内容，详情查看类参考）
			@Override
			public void onTracePushCallback(byte arg0, String arg1) {
				Log.i("TAG", "onTracePushCallback=" + arg1);
			}
			
		};
		client.startTrace(trace, startTraceListener);
	}
	
	//追踪结束
	private void initOnStopTraceListener(){
		stopTraceListener = new OnStopTraceListener() {
			
			@Override
			public void onStopTraceSuccess() {
				// TODO 自动生成的方法存根
				Log.i("TAG", "onStopTraceSuccess is ok");
			}
			
			@Override
			public void onStopTraceFailed(int arg0, String arg1) {
				// TODO 自动生成的方法存根
				Log.i("TAG", "onStopTraceFailed=" + arg1);
			}
		};
		client.stopTrace(trace, stopTraceListener);
		
	}
	
	
	/**
	 * 轨迹刷新线程
	 * @author BLYang
	 */
	private class RefreshThread extends Thread{
		 
		protected boolean refresh = true;  
		
		public void run(){
			
			while(refresh){
				queryRealtimeTrack();
				try{
					Thread.sleep(packInterval * 1000);
				}catch(InterruptedException e){
					System.out.println("线程休眠失败");
				}
			}
			
		}
	}
	 
	/**
	 * 查询实时线路
	 */
	private void queryRealtimeTrack(){
		
		String entityName = this.entityName;
		String columnKey = "";
		int returnType = 0;
		int activeTime = 0;
		int pageSize = 10;
		int pageIndex = 1;
		
		this.client.queryEntityList(
				serviceId, 
				entityName, 
				columnKey, 
				returnType,
				activeTime, 
				pageSize, 
				pageIndex, 
				entityListener
				);
		
	}
	
	
	/**
	 * 展示实时线路图
	 * @param realtimeTrack
	 */
	protected void showRealtimeTrack(String realtimeTrack){
		
		if(refreshThread == null || !refreshThread.refresh){
			return;
		}
		
		//数据以JSON形式存取
		RealtimeTrackData realtimeTrackData = GsonService.parseJson(realtimeTrack, RealtimeTrackData.class);
		
		if(realtimeTrackData != null && realtimeTrackData.getStatus() ==0){
			
			LatLng latLng = realtimeTrackData.getRealtimePoint();
			
			if(latLng != null){
				pointList.add(latLng);
				drawRealtimePoint(latLng);
			}
			else{
				Toast.makeText(getApplicationContext(), "当前无轨迹点", Toast.LENGTH_LONG).show();
			}
			
		}
		
	}
	
	/**
	 * 画出实时线路点
	 * @param point
	 */
	private void drawRealtimePoint(LatLng point){
		
		mBaiduMap.clear();
		MapStatus mapStatus = new MapStatus.Builder().target(point).zoom(18).build();
		msUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
		realtimeBitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
		overlay = new MarkerOptions().position(point)
				.icon(realtimeBitmap).zIndex(9).draggable(true);
		
		if(pointList.size() >= 2  && pointList.size() <= 1000){
			polyline = new PolylineOptions().width(10).color(Color.RED).points(pointList);
		}
		
		addMarker();
		String distanceNUM = String.format("%.3f", GetExerInfo.lineDisetance(pointList));
		distance.setText(distanceNUM+"km");
		String avgSpeedNUM = String.format("%.2f", GetExerInfo.avgSpeedInfo(GetExerInfo.lineDisetance(pointList), time.getBase()));
		
	}
	
	private void stopDrawPoint() {
		mBaiduMap.clear();		
	}
	
	
	private void addMarker(){
		
		if(msUpdate != null){
			mBaiduMap.setMapStatus(msUpdate);
		}
		
		if(polyline != null){
			mBaiduMap.addOverlay(polyline);
		}
		
		if(overlay != null){
			mBaiduMap.addOverlay(overlay);
		}
		
		
	}
	
	
	/**
	 * 启动刷新线程
	 * @param isStart
	 */
	private void startRefreshThread(boolean isStart){
		
		if(refreshThread == null){
			refreshThread = new RefreshThread();
		}
		
		refreshThread.refresh = isStart;
		
		if(isStart){
			if(!refreshThread.isAlive()){
				refreshThread.start();
			}
		}
		else{
			refreshThread = null;
		}
		
		
	}

    
    private void button() {
    	//计时器和跳转按钮
		//默认暂停和停止不可用
    	
		btn2.setEnabled(false);
		btn3.setEnabled(false);
		btn1.setOnClickListener(new View.OnClickListener() {//开始按钮			
			@Override
			public void onClick(View arg0) {
				time.setBase(SystemClock.elapsedRealtime()-recordingTime); //跳过已经记录的时间，继续计时
				time.start();
				btn1.setEnabled(false);
				btn2.setEnabled(true);
				btn3.setEnabled(true);//按下开始后仅暂停和停止可用	
				
				initOnEntityListener();				
				initOnStartTraceListener();			
				
				

			}
		});
		
		btn2.setOnClickListener(new View.OnClickListener() {//暂停按钮		
			@Override
			public void onClick(View arg0) {
				time.stop();
				recordingTime = SystemClock.elapsedRealtime()- time.getBase();//暂停时保存已经记录的时间，初始值为0
				btn1.setEnabled(true);
				btn2.setEnabled(false);
				btn3.setEnabled(true);//暂停时仅开始和暂停可用
				initOnStopTraceListener();
			}
		});
		
		btn3.setOnClickListener(new View.OnClickListener() {//停止按钮			
			@Override
			public void onClick(View arg0) {
				recordingTime = 0;
				time.setBase(SystemClock.elapsedRealtime());
				time.stop();                                   //已记录时间清零，时间设0同时停止技术
				btn1.setEnabled(true);
				btn2.setEnabled(false);
				btn3.setEnabled(false);//停止时仅开始可用
				stopDrawPoint();
				initOnStopTraceListener();
				
			}
		});
		
	}

	public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            if(location.getLocType() == BDLocation.TypeGpsLocation){
            	String speedText =  String.format("%.3f", location.getSpeed());
            	speed.setText("时速："+speedText+"km/h");
            }
            if(location.getLocType() != BDLocation.TypeGpsLocation){
            	speed.setText("请室外运动以显示时速");
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));                              
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }
	

}
