package fe.up.stocks;


import java.util.ArrayList;

import com.jjoe64.graphview.GraphViewSeries;

import android.util.Pair;

public class realtimeGraph {

	public String stockName,stockAbrev;
	public int NoPoints, id;
	public int pos, graphPos;
	public ArrayList<Pair<Double,Long>> points;
	public ArrayList<String> times;
	public boolean turnLeft, isPlay;
	public Double minY, maxY;
	public GraphViewSeries gvs;
	public realtimeGraph()
	{
		NoPoints=0;
		stockName="Error";
		pos=0;
		turnLeft=true;
		minY=Double.MAX_VALUE;
		maxY=-Double.MAX_VALUE;
		isPlay=false;
		times = new ArrayList<String>();
		points = new ArrayList<Pair<Double,Long>>();
	}
	
	/*public Stock(int NoStocks, Date BoughtTime, String Name, String subName)
	{
		NoPoints=0;
		beginDate = new Date();
		endDate = new Date();
		stockName="Error";
		points = new ArrayList<Pair<Integer,Integer>>();
	}*/
}
