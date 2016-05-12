package util;

import com.baidu.mapapi.model.LatLng;

public class BDtoWGS84 {      
    public static double pi = 3.1415926535897932384626;
    public static double a = 6378245.0;
    public static double ee = 0.00669342162296594323;

    //百度坐标系转WGS84，先从百度坐标系转成GCJ02，再转成WGS84
    public static LatLng bd09ToWGS84(LatLng bd) {
    	
        LatLng gcj02 = bd09ToGcj02(bd);
        LatLng wgs84 = GCJ02ToWGS84(gcj02);
        return wgs84;
    }
    
    //bd09 to GCJ02
    public static LatLng bd09ToGcj02(LatLng bd09) {
        double x = bd09.longitude - 0.0065;
        double y = bd09.latitude - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * pi);
        double a = Math.atan2(y, x) - 0.000003 * Math.cos(x * pi);
        double Lon = z * Math.cos(a);
        double Lat = z * Math.sin(a);
        LatLng gcj02 = new LatLng(Lat, Lon);
        return gcj02;
    }
    //GCJ-02 to WGS84    
    public static LatLng GCJ02ToWGS84(LatLng GCJ02) {
        LatLng a = transform(GCJ02);        
        double lat = GCJ02.latitude * 2 - a.latitude;
        double lng= GCJ02.longitude * 2 - a.longitude;
        LatLng wgs84 = new LatLng(lat, lng);
        return wgs84;
    }


    //判断是否在中国内
    public static boolean outOfChina(LatLng ll) {
        if (ll.latitude < 72.004 || ll.longitude > 137.8347)
            return true;
        if (ll.latitude < 0.8293 || ll.longitude > 55.8271)
            return true;
        return false;
    }
    
    //坐标变换工具
    public static LatLng transform(LatLng ll) {
        if (outOfChina(ll)) {
            return new LatLng(ll.latitude, ll.longitude);
        }
        double dLat = transformLat(ll.longitude - 105, ll.latitude - 35);
        double dLon = transformLon(ll.longitude - 105, ll.latitude - 35);
        double radLat = ll.latitude / 180 * pi;
        double a = Math.sin(radLat);
        a = 1 - ee * a * a;
        double sqrta = Math.sqrt(a);
        dLat = (dLat * 180) / ((a * (1 - ee)) / (a * sqrta) * pi);
        dLon = (dLon * 180) / (a / sqrta * Math.cos(radLat) * pi);
        double Lat = ll.latitude + dLat;
        double Lon = ll.longitude + dLon;
        return new LatLng(Lat, Lon);
    }

    public static double transformLat(double x, double y) {
        double r = -100 + 2 * x + 3 * y + 0.2 * y * y + 0.1 * x * y
                + 0.2 * Math.sqrt(Math.abs(x));
        r += (20 * Math.sin(6 * x * pi) + 20 * Math.sin(2 * x * pi)) * 2 / 3;
        r += (20 * Math.sin(y * pi) + 40 * Math.sin(y / 3 * pi)) * 2 / 3;
        r += (160 * Math.sin(y / 12 * pi) + 320 * Math.sin(y * pi / 30)) * 2 / 3;
        return r;
    }

    public static double transformLon(double x, double y) {
        double r = 300 + x + 2 * y + 0.1 * x * x + 0.1 * x * y + 0.1
                * Math.sqrt(Math.abs(x));
        r += (20 * Math.sin(6 * x * pi) + 20.0 * Math.sin(2 * x * pi)) * 2 / 3;
        r += (20 * Math.sin(x * pi) + 40 * Math.sin(x / 3 * pi)) * 2 / 3;
        r += (150 * Math.sin(x / 12 * pi) + 300 * Math.sin(x / 30
                * pi)) * 2 / 3;
        return r;
    }
}
