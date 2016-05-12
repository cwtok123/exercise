package exercise;

import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.OnTrackListener;
import com.example.exercise.R;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import baidu.trackutils.DateDialog;
import baidu.trackutils.DateDialog.CallBack;
import baidu.trackutils.DateDialog.PriorityListener;
import baidu.trackutils.DateUtils;
import baidu.trackutils.GsonService;
import baidu.trackutils.HistoryTrackData;

public class ExerciseInfo extends Activity implements OnClickListener{
	
	private int startTime = 0;
    private int endTime = 0;

    private int year = 0;
    private int month = 0;
    private int day = 0;

    private static BitmapDescriptor bmStart;
    private static BitmapDescriptor bmEnd;
    private static MarkerOptions startMarker = null;
    private static MarkerOptions endMarker = null;
    private static PolylineOptions polyline = null;
    private MapStatusUpdate msUpdate = null;
    private LBSTraceClient client;
    protected OnTrackListener trackListener = null;
    
    private static int isProcessed = 0;
    private Button btnDate = null;
    private Button btnProcessed = null;

	MapView mMapView = null;    // 地图View
	BaiduMap mBaiduMap = null;
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exercise_info);

		mMapView = (MapView)findViewById(R.id.map_info);
		mBaiduMap = mMapView.getMap();
		
        btnDate = (Button) findViewById(R.id.btn_queryInfo);
        btnDate.setOnClickListener(this);
        btnProcessed = (Button) findViewById(R.id.btn_processed);
        btnProcessed.setOnClickListener(this);
        
        initOnTrackListener();
		
	}
	
	  /**
     * 初始化OnTrackListener
     */
    private void initOnTrackListener() {

        trackListener = new OnTrackListener() {

            // 请求失败回调接口
            @Override
            public void onRequestFailedCallback(String arg0) {
                // TODO Auto-generated method stub
                Looper.prepare();
                Toast.makeText(getApplicationContext(), "track请求失败回调接口消息 : " + arg0, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            // 查询历史轨迹回调接口
            @Override
            public void onQueryHistoryTrackCallback(String arg0) {
                // TODO Auto-generated method stub
                super.onQueryHistoryTrackCallback(arg0);
                showHistoryTrack(arg0);
            }
        };
    }
    
    /**
     * 查询历史轨迹
     */
    public void queryHistoryTrack() {

        // 是否返回精简的结果（0 : 否，1 : 是）
        int simpleReturn = 0;
        // 开始时间
        if (startTime == 0) {
            startTime = (int) (System.currentTimeMillis() / 1000 - 12 * 60 * 60);
        }
        if (endTime == 0) {
            endTime = (int) (System.currentTimeMillis() / 1000);
        }
        // 分页大小
        int pageSize = 1000;
        // 分页索引
        int pageIndex = 1;
        
       

        client.queryHistoryTrack(ExerciseRecord.serviceId, ExerciseRecord.entityName, 
        		simpleReturn, startTime, endTime,pageSize,pageIndex,trackListener);
    }
    
    /**
     * 查询纠偏后的历史轨迹
     */
    private void queryProcessedHistoryTrack() {

        // 是否返回精简的结果（0 : 否，1 : 是）
        int simpleReturn = 0;
        // 是否返回纠偏后轨迹（0 : 否，1 : 是）
        int isProcessed = 1;
        // 开始时间
        if (startTime == 0) {
            startTime = (int) (System.currentTimeMillis() / 1000 - 12 * 60 * 60);
        }
        if (endTime == 0) {
            endTime = (int) (System.currentTimeMillis() / 1000);
        }
        // 分页大小
        int pageSize = 1000;
        // 分页索引
        int pageIndex = 1;

        client.queryProcessedHistoryTrack(ExerciseRecord.serviceId, ExerciseRecord.entityName, simpleReturn, isProcessed,
                startTime, endTime,
                pageSize,
                pageIndex,
                trackListener);
    }
    
    
    /**
     * 轨迹查询(先选择日期，再根据是否纠偏，发送请求)
     */
    private void queryTrack() {
        // 选择日期
        int[] date = null;
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        if (year == 0 && month == 0 && day == 0) {
            String curDate = DateUtils.getCurrentDate();
            date = DateUtils.getYMDArray(curDate, "-");
        }

        if (date != null) {
            year = date[0];
            month = date[1];
            day = date[2];
        }

        DateDialog dateDiolog = new DateDialog(this, new PriorityListener() {

            public void refreshPriorityUI(String sltYear, String sltMonth,
                    String sltDay, CallBack back) {

                Log.d("TGA", sltYear + sltMonth + sltDay);
                year = Integer.parseInt(sltYear);
                month = Integer.parseInt(sltMonth);
                day = Integer.parseInt(sltDay);
                String st = year + "年" + month + "月" + day + "日0时0分0秒";
                String et = year + "年" + month + "月" + day + "日23时59分59秒";

                startTime = Integer.parseInt(DateUtils.getTimeToStamp(st));
                endTime = Integer.parseInt(DateUtils.getTimeToStamp(et));

                back.execute();
            }

        }, new CallBack() {

            public void execute() {

                // 选择完日期，根据是否纠偏发送轨迹查询请求
                if (0 == isProcessed) {
                    Toast.makeText(getApplicationContext(), "正在查询历史轨迹，请稍候", Toast.LENGTH_SHORT).show();
                    queryHistoryTrack();
                } else {
                    Toast.makeText(getApplicationContext(), "正在查询纠偏后的历史轨迹，请稍候", Toast.LENGTH_SHORT).show();
                    queryProcessedHistoryTrack();
                }
            }
        }, year, month, day, width, height, "选择日期", 1);

        Window window = dateDiolog.getWindow();
        window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
        dateDiolog.setCancelable(true);
        dateDiolog.show();

    }
    
    /**
     * 显示历史轨迹
     * 
     * @param trackData
     */
    public void showHistoryTrack(String historyTrack) {

        HistoryTrackData historyTrackData = GsonService.parseJson(historyTrack,
                HistoryTrackData.class);
        List<LatLng> latLngList = new ArrayList<LatLng>();
        if (historyTrackData != null && historyTrackData.getStatus() == 0) {
            if (historyTrackData.getListPoints() != null) {
                latLngList.addAll(historyTrackData.getListPoints());
            }

            // 绘制历史轨迹
            drawHistoryTrack(latLngList);

        }

    }
    
    /**
     * 绘制历史轨迹
     * 
     * @param points
     */
    public void drawHistoryTrack(final List<LatLng> points) {
        // 绘制新覆盖物前，清空之前的覆盖物
        mBaiduMap.clear();

        if (points == null || points.size() == 0) {
            Looper.prepare();
            Toast.makeText(getApplicationContext(), "当前查询无轨迹点", Toast.LENGTH_SHORT).show();
            Looper.loop();
            resetMarker();
        } else if (points.size() > 1) {

            LatLng llC = points.get(0);
            LatLng llD = points.get(points.size() - 1);
            LatLngBounds bounds = new LatLngBounds.Builder()
                    .include(llC).include(llD).build();

            msUpdate = MapStatusUpdateFactory.newLatLngBounds(bounds);

            bmStart = BitmapDescriptorFactory.fromResource(R.drawable.icon_start);
            bmEnd = BitmapDescriptorFactory.fromResource(R.drawable.icon_end);

            // 添加起点图标
            startMarker = new MarkerOptions()
                    .position(points.get(points.size() - 1)).icon(bmStart)
                    .zIndex(9).draggable(true);

            // 添加终点图标
            endMarker = new MarkerOptions().position(points.get(0))
                    .icon(bmEnd).zIndex(9).draggable(true);

            // 添加路线（轨迹）
            polyline = new PolylineOptions().width(10)
                    .color(Color.RED).points(points);

            addMarker();

        }

    }
    
    /**
     * 添加覆盖物
     */
    public void addMarker() {

        if (null != msUpdate) {
            mBaiduMap.setMapStatus(msUpdate);
        }

        if (null != startMarker) {
            mBaiduMap.addOverlay(startMarker);
        }

        if (null != endMarker) {
            mBaiduMap.addOverlay(endMarker);
        }

        if (null != polyline) {
            mBaiduMap.addOverlay(polyline);
        }

    }
    
    /**
     * 重置覆盖物
     */
    private void resetMarker() {
        startMarker = null;
        endMarker = null;
        polyline = null;
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
	public void onClick(View v) {
		// TODO 自动生成的方法存根
        switch (v.getId()) {
            case R.id.btn_queryInfo:
                // 查询轨迹
                queryTrack();
                break;

            case R.id.btn_processed:
                isProcessed = isProcessed ^ 1;
                if (0 == isProcessed) {
                    btnProcessed.setBackgroundColor(Color.rgb(0xff, 0xff, 0xff));
                    btnProcessed.setTextColor(Color.rgb(0x00, 0x00, 0x00));
                    Toast.makeText(getApplicationContext(), "正在查询历史轨迹，请稍候", Toast.LENGTH_SHORT).show();
                    queryHistoryTrack();
                } else {
                    btnProcessed.setBackgroundColor(Color.rgb(0x99, 0xcc, 0xff));
                    btnProcessed.setTextColor(Color.rgb(0x00, 0x00, 0xd8));
                    Toast.makeText(getApplicationContext(), "正在查询纠偏后的历史轨迹，请稍候", Toast.LENGTH_SHORT).show();
                    queryProcessedHistoryTrack();
                }
                break;

            default:
                break;
        }
	} 
	


}
