package fe.up.stocks;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class Stock implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int NoStocks, idsgraph, idrgraph;
	public double unitPrice, currentPrice;
	public Calendar BoughtTime;
	public String Name, subName;
	//public boolean visited=false;
	public Stock()
	{
		NoStocks=0;
		BoughtTime = Calendar.getInstance();;
		Name="Error";
		subName="Err";
	}
	
	public Stock(int NoStocks, Calendar BoughtTime, String Name, String subName)
	{
		this.NoStocks=NoStocks;
		this.BoughtTime = BoughtTime;
		this.Name=Name;
		this.subName=subName;
		this.unitPrice=0.01;
	}
	
	public Stock(int NoStocks, String Name, String subName)
	{
		this.NoStocks=NoStocks;
		this.Name=Name;
		this.subName=subName;
	}
}
