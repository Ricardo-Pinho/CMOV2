package fe.up.stocks;

import java.io.Serializable;
import java.util.Date;

public class Stock implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int NoStocks;
	public Date BoughtTime;
	public String Name, subName;
	//public boolean visited=false;
	public Stock()
	{
		NoStocks=0;
		BoughtTime = new Date();
		Name="Error";
		subName="Err";
	}
	
	public Stock(int NoStocks, Date BoughtTime, String Name, String subName)
	{
		this.NoStocks=NoStocks;
		this.BoughtTime = BoughtTime;
		this.Name=Name;
		this.subName=subName;
	}
}
