package util;

import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.utils.DistanceUtil;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

public class DrawCharts  {
    
	// 设置显示的样式  
    public static void showChart(LineChart lineChart, LineData lineData) {
    	
        lineChart.setDrawBorders(false);  //是否在折线图上添加边框  
        lineChart.setDescription("");// 
        lineChart.setNoDataTextDescription("You need to provide data for the chart.");   
        lineChart.setDrawGridBackground(false); 
        lineChart.setGridBackgroundColor(Color.WHITE & 0x70FFFFFF); 
        lineChart.setTouchEnabled(true); 
        lineChart.setDragEnabled(true); 
        lineChart.setScaleEnabled(true);
        lineChart.setBackgroundColor(Color.rgb(255, 255, 255));        
        lineChart.setData(lineData);  
         
        Legend mLegend = lineChart.getLegend();         
        mLegend.setForm(LegendForm.CIRCLE); 
        mLegend.setFormSize(6f); 
        mLegend.setTextColor(Color.WHITE); 
        
        lineChart.invalidate();
        lineChart.animateXY(3000,3000);  
      }  
      
    //测试表格数据
    public static LineData getTestData() {  
        ArrayList<String> xValues = new ArrayList<String>();  
        for (int i = 0; i < 100; i++) {  
         
            xValues.add("" + i);  
        }  
  
         
        ArrayList<Entry> yValues = new ArrayList<Entry>();  
        for (int i = 0; i < 100; i++) {  
            float value = (float) (Math.random() * 1000) + 3;  
            yValues.add(new Entry(value, i));  
        }  
  
          
        LineDataSet lineDataSet = new LineDataSet(yValues, "测试折线图" );  
         
        lineDataSet.setLineWidth(1.75f); 
        lineDataSet.setColor(Color.WHITE); 
        lineDataSet.setDrawValues(false);
        
        lineDataSet.setDrawCircles(false);
        lineDataSet.setDrawCubic(true);
        lineDataSet.setCubicIntensity(0.2f);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setFillColor(Color.rgb(168,133,18));
  
        List<ILineDataSet> lineDataSets = new ArrayList<ILineDataSet>();  
        lineDataSets.add(lineDataSet); 
        LineData lineData = new LineData(xValues, lineDataSets); 
  
        return lineData;  
    }
    
    //海拔数据
    public static LineData getData(elevationAndPointList hAndPointList) {
    	//x轴数据
        ArrayList<String> xValues = new ArrayList<String>(); 
        xValues.add("0");
        double dd = 0;
        for (int i = 0; i < hAndPointList.point.size()-1; i++) {   
        	double d = DistanceUtil.getDistance(hAndPointList.point.get(i),hAndPointList.point.get(i+1));
        	dd+=d;
            xValues.add((int)dd+"");  
        }  
  
        // y轴的数据  
        ArrayList<Entry> yValues = new ArrayList<Entry>();  
        for (int i = 0; i < hAndPointList.h.size(); i++) {  
            float value = hAndPointList.h.get(i);
            yValues.add(new Entry((int)value, i));  
        }  
        
        LineDataSet lineDataSet = new LineDataSet(yValues, "海拔图" );  
         
        lineDataSet.setLineWidth(1.75f);  
        lineDataSet.setColor(Color.WHITE); 
        lineDataSet.setDrawValues(false);
        
        lineDataSet.setDrawCircles(false);
        lineDataSet.setDrawCubic(true);
        lineDataSet.setCubicIntensity(0.2f);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setFillColor(Color.rgb(168,133,18));
  
        ArrayList<ILineDataSet> lineDataSets = new ArrayList<ILineDataSet>();  
        lineDataSets.add(lineDataSet);
        
        LineData lineData = new LineData(xValues, lineDataSets);  
  
        return lineData;  
    } 
}