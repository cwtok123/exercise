package baidu.mapapi.overlayutil;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.search.route.TransitRouteLine;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于显示换乘路线的Overlay，自3.4.0版本起可实例化多个添加在地图中显�?
 */
public class TransitRouteOverlay extends OverlayManager {

    private TransitRouteLine mRouteLine = null;

    /**
     * 构�?�函�?
     * 
     * @param baiduMap
     *            该TransitRouteOverlay引用�? BaiduMap 对象
     */
    public TransitRouteOverlay(BaiduMap baiduMap) {
        super(baiduMap);
    }

    @Override
    public final List<OverlayOptions> getOverlayOptions() {

        if (mRouteLine == null) {
            return null;
        }

        List<OverlayOptions> overlayOptionses = new ArrayList<OverlayOptions>();
        // step node
        if (mRouteLine.getAllStep() != null
                && mRouteLine.getAllStep().size() > 0) {
            
            for (TransitRouteLine.TransitStep step : mRouteLine.getAllStep()) {
                Bundle b = new Bundle();
                b.putInt("index", mRouteLine.getAllStep().indexOf(step));
                if (step.getEntrance() != null) {
                    overlayOptionses.add((new MarkerOptions())
                            .position(step.getEntrance().getLocation())
                                    .anchor(0.5f, 0.5f).zIndex(10).extraInfo(b)
                                            .icon(getIconForStep(step)));
                }
                // �?后路段绘制出口点
                if (mRouteLine.getAllStep().indexOf(step) == (mRouteLine
                        .getAllStep().size() - 1) && step.getExit() != null) {
                    overlayOptionses.add((new MarkerOptions())
                            .position(step.getExit().getLocation())
                                    .anchor(0.5f, 0.5f).zIndex(10)
                                            .icon(getIconForStep(step)));
                }
            }
        }

        if (mRouteLine.getStarting() != null) {
            overlayOptionses.add((new MarkerOptions())
                    .position(mRouteLine.getStarting().getLocation())
                            .icon(getStartMarker() != null ? getStartMarker() :
                                    BitmapDescriptorFactory
                                            .fromAssetWithDpi("Icon_start.png")).zIndex(10));
        }
        if (mRouteLine.getTerminal() != null) {
            overlayOptionses
                    .add((new MarkerOptions())
                            .position(mRouteLine.getTerminal().getLocation())
                                    .icon(getTerminalMarker() != null ? getTerminalMarker() :
                                            BitmapDescriptorFactory
                                                    .fromAssetWithDpi("Icon_end.png"))
                                                            .zIndex(10));
        }
        // polyline
        if (mRouteLine.getAllStep() != null
                && mRouteLine.getAllStep().size() > 0) {
            
            for (TransitRouteLine.TransitStep step : mRouteLine.getAllStep()) {
                if (step.getWayPoints() == null) {
                    continue;
                }
                int color = 0;
                if (step.getStepType() != TransitRouteLine.TransitStep.TransitRouteStepType.WAKLING) {
//                    color = Color.argb(178, 0, 78, 255);
                    color = getLineColor() != 0 ? getLineColor() : Color.argb(178, 0, 78, 255);
                } else {
//                    color = Color.argb(178, 88, 208, 0);
                    color = getLineColor() != 0 ? getLineColor() : Color.argb(178, 88, 208, 0);
                }
                overlayOptionses.add(new PolylineOptions()
                        .points(step.getWayPoints()).width(10).color(color)
                        .zIndex(0));
            }
        }
        return overlayOptionses;
    }

    private BitmapDescriptor getIconForStep(TransitRouteLine.TransitStep step) {
        switch (step.getStepType()) {
            case BUSLINE:
                return BitmapDescriptorFactory.fromAssetWithDpi("Icon_bus_station.png");
            case SUBWAY:
                return BitmapDescriptorFactory.fromAssetWithDpi("Icon_subway_station.png");
            case WAKLING:
                return BitmapDescriptorFactory.fromAssetWithDpi("Icon_walk_route.png");
            default:
                return null;
        }
    }

    /**
     * 设置路线数据
     * 
     * @param routeOverlay
     *            路线数据
     */
    public void setData(TransitRouteLine routeOverlay) {
        this.mRouteLine = routeOverlay;
    }

    /**
     * 覆写此方法以改变默认起点图标
     * 
     * @return 起点图标
     */
    public BitmapDescriptor getStartMarker() {
        return null;
    }

    /**
     * 覆写此方法以改变默认终点图标
     * 
     * @return 终点图标
     */
    public BitmapDescriptor getTerminalMarker() {
        return null;
    }

    public int getLineColor() {
        return 0;
    }
    /**
     * 覆写此方法以改变起默认点击行�?
     * 
     * @param i
     *            被点击的step�?
     *            {@link com.baidu.mapapi.search.route.TransitRouteLine#getAllStep()}
     *            中的索引
     * @return 是否处理了该点击事件
     */
    public boolean onRouteNodeClick(int i) {
        if (mRouteLine.getAllStep() != null
                && mRouteLine.getAllStep().get(i) != null) {
            Log.i("baidumapsdk", "TransitRouteOverlay onRouteNodeClick");
        }
        return false;
    }

    @Override
    public final boolean onMarkerClick(Marker marker) {
        for (Overlay mMarker : mOverlayList) {
            if (mMarker instanceof Marker && mMarker.equals(marker)) {
                if (marker.getExtraInfo() != null) {
                    onRouteNodeClick(marker.getExtraInfo().getInt("index"));
                }
            }
        }
        return true;
    }

    @Override
    public boolean onPolylineClick(Polyline polyline) {
        // TODO Auto-generated method stub
        return false;
    }

}
