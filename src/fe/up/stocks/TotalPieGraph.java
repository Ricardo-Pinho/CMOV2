package fe.up.stocks;

import java.util.ArrayList;

import org.achartengine.ChartFactory;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

public class TotalPieGraph {
	
	public Portfolio port= new Portfolio();

	public Intent getIntent(Context context) {
		ArrayList<Integer> positions = new ArrayList<Integer>();
		CategorySeries series = new CategorySeries("Total Stock Value");
		double Total=0;
		if(port.stocks.size()>6)
		{
			for (int i = 0; i< port.stocks.size();i++) {
				Total+=port.stocks.get(i).NoStocks*port.stocks.get(i).currentPrice;
				if(positions.size()<6)
					positions.add(i);
				else
				{
					int minpos=0;
					double minval=Integer.MAX_VALUE;
					for (int j = 0; j< positions.size();j++) {
						if(port.stocks.get(positions.get(j)).NoStocks*port.stocks.get(positions.get(j)).currentPrice<minval)
						{
							minpos=j;
							minval=port.stocks.get(positions.get(j)).NoStocks*port.stocks.get(positions.get(j)).currentPrice;
							//positions.set(j, i);
						}

					}
					if(minval<port.stocks.get(i).NoStocks)
						positions.set(minpos, i);
					
				}
			}
			for (int i = 0; i< positions.size();i++) {
				double value = (port.stocks.get(positions.get(i)).NoStocks*port.stocks.get(positions.get(i)).currentPrice)/Total;
				value = Math.round(value*10000.0)/100.0;
				double tval = Math.round(port.stocks.get(positions.get(i)).NoStocks*port.stocks.get(positions.get(i)).currentPrice*100)/100;
				series.add(port.stocks.get(positions.get(i)).subName+" - "+Double.toString(value)+"%"+"($"+tval+")", tval);
			}
		}
		else
		{
			for (int i = 0; i< port.stocks.size();i++) {
				Total+=port.stocks.get(i).NoStocks*port.stocks.get(i).currentPrice;
			}
			for (int i = 0; i< port.stocks.size();i++) {
				double value = (port.stocks.get(i).NoStocks*port.stocks.get(i).currentPrice)/Total;
				value = Math.round(value*10000.0)/100.0;
				double tval = Math.round(port.stocks.get(i).NoStocks*port.stocks.get(i).currentPrice*100)/100;
				series.add(port.stocks.get(i).subName+" - "+Double.toString(value)+"%"+"($"+tval+")", tval);
				positions.add(i);
			}
		}
		
		int[] colors = null;
		switch(positions.size())
		{
			case(1): colors = new int[] { Color.BLUE}; break;
			case(2): colors = new int[] { Color.BLUE, Color.GREEN}; break;
			case(3): colors = new int[] { Color.BLUE, Color.GREEN, Color.CYAN}; break;
			case(4): colors = new int[] { Color.BLUE, Color.GREEN, Color.CYAN, Color.YELLOW}; break;
			case(5): colors = new int[] { Color.BLUE, Color.GREEN, Color.CYAN, Color.YELLOW, Color.RED}; break;
			case(6): colors = new int[] { Color.BLUE, Color.GREEN, Color.CYAN, Color.YELLOW, Color.RED, Color.WHITE}; break;
			default: break;
		}

		DefaultRenderer renderer = new DefaultRenderer();
		for (int color : colors) {
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(color);
			renderer.addSeriesRenderer(r);
		}
		renderer.setChartTitle("Total Stock Value");
		renderer.setChartTitleTextSize(50);
		renderer.setLabelsTextSize(25);
	
		renderer.setApplyBackgroundColor(true);
		renderer.setBackgroundColor(Color.BLACK);
		renderer.setLegendTextSize(30);
		renderer.setLabelsColor(Color.WHITE);
		renderer.setZoomButtonsVisible(true);

		Intent intent = ChartFactory.getPieChartIntent(context, series, renderer, "Stock Portfolio");
		return intent;
	}
}
