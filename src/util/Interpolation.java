package util;

import java.util.ArrayList;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;


public class Interpolation {

	@SuppressWarnings("null")
	public ArrayList<LatLng> interpolation(ArrayList<LatLng> list)
	{		
		double listLong=0;
		for(int i=0;i<list.size()-1;i++)
		{
			listLong += DistanceUtil.getDistance(list.get(i), list.get(i+1));			
		}		
		if (listLong<29)		
		{
			return list;
		}
		
		else
		{
			ArrayList<LatLng> llist = null;
			double end = 29;
			double beg = 29-end;
			int n=0;
			for(int i=0;i<list.size();i++)
			{
				
				double d = DistanceUtil.getDistance(list.get(i), list.get(i+1))-beg;
				double k = (list.get(i).latitude-list.get(i+1).latitude)/(list.get(i).longitude-list.get(i+1).longitude);
				if (d > 29) {
					int shang = (int) Math.floor(d/29);
					end = d%29;
					beg = 29-end;
					
					for(int j=0;j<shang;j++)
					{
						
					}
				}
			}
			return llist;
		}
	}
}
