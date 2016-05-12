package util;

import java.util.List;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

public class GetExerInfo {
	//返回里程	
	public static double lineDisetance(List<LatLng> pointline) {
		double distance = 0;
		int num=pointline.size();
		if (num>1){
			for(int i=0 ;i<num-1 ;i++){
				distance += DistanceUtil.getDistance(pointline.get(i), pointline.get(i+1));
			}
		}
		return distance/1000;		
	}
	
	//返回均速
	public static double avgSpeedInfo(double distance,long time) {		
		double avgspeedinfo = 0;	
		if (time>0) {
			return avgspeedinfo=distance*3.6/time;
		}
		return avgspeedinfo;		
	}
	

}
