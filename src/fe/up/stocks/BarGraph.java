package fe.up.stocks;

import java.text.SimpleDateFormat;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

public class BarGraph extends Activity{

	public stockGraph sgraph;
	public double isport=0;
	public Intent getIntent(Context context) {
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("d/M");
		SimpleDateFormat dateYearFormat = new SimpleDateFormat("MMM");
		TimeSeries series = new TimeSeries("Price per Share");
		
		
		
		for( int i = 0; i < sgraph.points.size(); i++)
		{
			series.add((sgraph.points.size()-1-i), sgraph.points.get(i).first);

			series.addAnnotation("$"+sgraph.points.get(i).first, (double)sgraph.points.get((sgraph.points.size()-1-i)).second, sgraph.points.get(i).first);
		}
	
		/*// Our second data
		int[] x2 = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 }; // x values!
		int[] y2 =  { 145, 123, 111, 100, 89, 77, 57, 45, 34, 30}; // y values!
		TimeSeries series2 = new TimeSeries("Line2"); 
		for( int i = 0; i < x2.length; i++)
		{
			series2.add(x2[i], y2[i]);
		}*/
		
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

		
		XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer(); // Holds a collection of XYSeriesRenderer and customizes the graph
		XYSeriesRenderer renderer = new XYSeriesRenderer(); // This will be used to customize line 1
		
		if(isport!=0)
		{
			TimeSeries series2 = new TimeSeries("Price per Share");
			
			
			
			for( int i = 0; i < sgraph.points.size(); i++)
			{
				series2.add(i, isport);
	
			}
			dataset.addSeries(series2);
			XYSeriesRenderer renderer2 = new XYSeriesRenderer(); // This will be used to customize line 2
			mRenderer.addSeriesRenderer(renderer2);

			renderer2.setColor(Color.RED);
		}
		dataset.addSeries(series);
		mRenderer.addSeriesRenderer(renderer);
		// Customization time for line 1!
		renderer.setColor(Color.BLUE);
		renderer.setPointStyle(PointStyle.CIRCLE);
		renderer.setFillPoints(true);
		renderer.setDisplayChartValues(false);
		renderer.setChartValuesTextSize(25);
		// Customization time for line 2!
		//renderer2.setColor(Color.YELLOW);
		//renderer2.setPointStyle(PointStyle.DIAMOND);
		//renderer2.setFillPoints(true);
		
		mRenderer.setApplyBackgroundColor(true);
		mRenderer.setBackgroundColor(Color.BLACK);
		mRenderer.setChartTitle("");
		mRenderer.setLegendTextSize(30);
		mRenderer.setShowLegend(false);
		//mRenderer.setLegendHeight(30);
		//mRenderer.setYLabelsAlign(Align.LEFT, 0);
		mRenderer.setLabelsTextSize(25);
		mRenderer.addYTextLabel(sgraph.maxY, "Value");
		mRenderer.addXTextLabel(sgraph.points.size()+1.5, "Time");
		mRenderer.setAxisTitleTextSize(30);
		mRenderer.setXAxisMin(-1);
		mRenderer.setXAxisMax(sgraph.points.size()+2);
		mRenderer.setYAxisMin(sgraph.minY-1);
		mRenderer.setYAxisMax(sgraph.maxY+1);
		for( int i = 0; i < sgraph.points.size(); i++)
		{
			/*double d= sgraph.points.get(i).first;
			String text = Double.toString(Math.abs(d));
			int integerPlaces = text.indexOf('.');
			int decimalPlaces = text.length() - integerPlaces - 1;
			if(decimalPlaces>3)
			{
				d=Math.round(d*100.0)/100.0;
			}*/
			
			if(MainActivity.monthPeriodGraph)
				mRenderer.addXTextLabel((double)(sgraph.points.size()-1-i), dateFormat.format(sgraph.times.get(i)));
			else
				mRenderer.addXTextLabel((double)(sgraph.points.size()-1-i), dateYearFormat.format(sgraph.times.get(i)));
			
		}
		mRenderer.setXLabelsAlign(Align.CENTER);
		mRenderer.setBarWidth(4);
	    mRenderer.setXLabels(0);
		mRenderer.setChartTitleTextSize(20);
		
		Intent intent = ChartFactory.getBarChartIntent(context, dataset, mRenderer, Type.STACKED, sgraph.stockName);
		return intent;
		
	}



}
