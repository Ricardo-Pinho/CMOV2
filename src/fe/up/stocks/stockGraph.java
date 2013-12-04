package fe.up.stocks;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.jjoe64.graphview.GraphViewSeries;

import android.util.Pair;

public class stockGraph {

	public String stockName,stockAbrev;
	public int NoPoints, id;
	public int pos;
	public Calendar beginDate, endDate;
	public ArrayList<Pair<Double,Long>> points;
	public Double minY, maxY;
	public boolean turnLeft;
	public GraphViewSeries gvs;
	public stockGraph()
	{
		NoPoints=0;
		beginDate = Calendar.getInstance();
		endDate = Calendar.getInstance();
		stockName="Error";
		pos=0;
		turnLeft=true;
		minY=Double.MAX_VALUE;
		maxY=-Double.MAX_VALUE;
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
