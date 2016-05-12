package exercise;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapLongClickListener;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.platform.comapi.map.r;
import com.example.exercise.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import util.BDtoWGS84;
import util.VolleyHttp;
import util.isNumeric;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

public class PointElevation extends Activity implements OnGetGeoCoderResultListener, OnMapLongClickListener {
	MapView mMapView = null;
	BaiduMap mBaiduMap = null;
	GeoCoder mSearch = null;
	boolean isFirstLoc = true;
	
	LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    BitmapDescriptor mCurrentMarker;
    
	public Button btn1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_point_elevation);
		btn1 = (Button) findViewById(R.id.pointSerach);
		mMapView = (MapView)findViewById(R.id.map_point);
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setOnMapLongClickListener(this);
		// 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
		// 初始化搜索模块，注册事件监听
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);
		Button();
		
	}
	
	public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
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
	
	private void Button() {
		// TODO 自动生成的方法存根
		btn1.setOnClickListener(new  View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				EditText editCity = (EditText) findViewById(R.id.pointCity);
				EditText editGeoCodeKey = (EditText) findViewById(R.id.pointAddress);
				// Geo搜索
				if (isNumeric.isNumeric(editCity.getText().toString())==false&isNumeric.isNumeric(editGeoCodeKey.getText().toString())==false)
				{
				mSearch.geocode(new GeoCodeOption().city(
						editCity.getText().toString()).address(
						editGeoCodeKey.getText().toString()));
				}
				else if (isNumeric.isNumeric(editCity.getText().toString())==true&isNumeric.isNumeric(editGeoCodeKey.getText().toString())==true){
					EditText lat = (EditText) findViewById(R.id.pointCity);
					EditText lon = (EditText) findViewById(R.id.pointAddress);
					LatLng ptCenter = new LatLng((Float.valueOf(lat.getText()
							.toString())), (Float.valueOf(lon.getText().toString())));
					// 反Geo搜索
					mSearch.reverseGeoCode(new ReverseGeoCodeOption()
							.location(ptCenter));
				}
				else Toast.makeText(PointElevation.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
				.show();
				
			}
			
		});
	}


	@Override  
    protected void onDestroy() {  
        super.onDestroy();  
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理  
        mMapView.onDestroy();  
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
    
	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(PointElevation.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
					.show();
			return;
		}
		mBaiduMap.clear();
		mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.icon_marka)));
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
				.getLocation()));
		
		LatLng pointWGS84 = BDtoWGS84.bd09ToWGS84(result.getLocation());
        float h = VolleyHttp.getPointElevation(pointWGS84.latitude, pointWGS84.longitude);
        
		String strInfo = String.format("纬度：%3.3f 经度：%3.3f 海拔：%3.3f",
				result.getLocation().latitude, result.getLocation().longitude, h);
		Toast.makeText(PointElevation.this, strInfo, Toast.LENGTH_LONG).show();
		
		LatLng llText = new LatLng(result.getLocation().latitude, result.getLocation().longitude);
        OverlayOptions ooText = new TextOptions().bgColor(0x00FFFF00)
                .fontSize(30).fontColor(0xFFFF00FF).text(strInfo).rotate(0)
                .position(llText);
        mBaiduMap.addOverlay(ooText);
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(PointElevation.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
					.show();
			return;
		}
		mBaiduMap.clear();
		mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.icon_marka)));
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
				.getLocation()));
		
		LatLng pointWGS84 = BDtoWGS84.bd09ToWGS84(result.getLocation());
        float h = VolleyHttp.getPointElevation(pointWGS84.latitude, pointWGS84.longitude);
		
		String strInfo = String.format(" 海拔：%3.3f",
				 h);
		Toast.makeText(PointElevation.this, result.getAddress()+strInfo, Toast.LENGTH_LONG).show();
		
		LatLng llText = new LatLng(result.getLocation().latitude, result.getLocation().longitude);
        OverlayOptions ooText = new TextOptions().bgColor(0x00FFFF00)
                .fontSize(30).fontColor(0xFFFF00FF).text(result.getAddress()+strInfo).rotate(0)
                .position(llText);
        mBaiduMap.addOverlay(ooText);		

	}

	@Override
	public void onMapLongClick(LatLng point) {
		// TODO 自动生成的方法存根
		mBaiduMap.clear();
		mBaiduMap.addOverlay(new MarkerOptions().position(point)
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.icon_marka)));
		
		LatLng pointWGS84 = BDtoWGS84.bd09ToWGS84(point);
        float h = VolleyHttp.getPointElevation(pointWGS84.latitude, pointWGS84.longitude);
        
		String strInfo = String.format("纬度：%3.3f 经度：%3.3f 海拔：%3.3f",point.latitude, point.longitude, h);
        OverlayOptions ooText = new TextOptions().bgColor(0x00FFFF00)
                .fontSize(30).fontColor(0xFFFF00FF).text(strInfo).rotate(0)
                .position(point);
        mBaiduMap.addOverlay(ooText);
        
	}

	
}
