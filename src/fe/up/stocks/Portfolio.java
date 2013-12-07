package fe.up.stocks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Portfolio implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String Name;
	public ArrayList<Stock> stocks;
	//public boolean visited=false;
	public Portfolio()
	{
		Name="Error";
		stocks= new ArrayList<Stock>();
	}
	
	public Portfolio(String Name, ArrayList<Stock> stocks)
	{
		this.Name=Name;
		this.stocks=stocks;
	}
	
	public Portfolio(String Name)
	{
		this.Name=Name;
		stocks= new ArrayList<Stock>();
	}
}
