package fe.up.stocks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;


import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewDataInterface;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;



public class StockFragment extends Fragment {

	private final SimpleDateFormat dateFormat = new SimpleDateFormat("d/M");
	private final SimpleDateFormat dateYearFormat = new SimpleDateFormat("MMM");
	private ProgressDialog pd;
	private boolean firstRunning=true;
	private double totaldiff=0;
	private double minVal=0;
	
	/** the available AWS regions */
	private HashMap<String, String> regionData;
	
	/** The connectivity data */
	private HashMap<String, String> connectionData;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    	
        View mainView = inflater
                .inflate(R.layout.stockmarket, container, false);
        
        mainView.setEnabled(false);
        setRetainInstance(true);
		//getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		AsyncTask<View, Void, View> task = new AsyncTask<View, Void, View>() {
			int response=1;
			@Override
			protected void onPreExecute() {
				pd = new ProgressDialog(getActivity());
				pd.setTitle("Getting Stock Data");
				pd.setMessage("Please wait.");
				pd.setCancelable(false);
				pd.setIndeterminate(true);
				pd.show();
			}
				
			@Override
			protected View doInBackground(View... params) {
				//MainActivity.sgraph = new ArrayList<stockGraph>();
				//MainActivity.graphs = new ArrayList<GraphView>();
				final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				MainActivity.graphs = new ArrayList<GraphView>(); 
				stockGraph sg;
				for(int i=0; i<MainActivity.usr.portfolios.get(MainActivity.portIndex).stocks.size();i++)
				{
					Stock stock = MainActivity.usr.portfolios.get(MainActivity.portIndex).stocks.get(i);
					sg = new stockGraph();
					sg.stockAbrev = stock.subName;
					sg.stockName= stock.Name;
					sg.id=i*1000;
					sg.endDate = Calendar.getInstance();
					sg.beginDate = Calendar.getInstance();
					if(MainActivity.monthPeriodGraph && !MainActivity.otherPeriod)
					{
						sg.beginDate.add(Calendar.DATE, -31);
						sg.monthPeriod=true;
					}
					else if(!MainActivity.otherPeriod)
					{
						sg.beginDate.add(Calendar.DATE, -365);
						sg.monthPeriod=false;
					}
					else
					{
						sg.beginDate = MainActivity.beginDate;
						sg.endDate = MainActivity.endDate;
					}
					boolean exists=false;
					for(int j=0; j< MainActivity.sgraph.size();j++)
					{
						if(sg.stockAbrev.equals(MainActivity.sgraph.get(j).stockAbrev))
						{
							if(sg.beginDate.get(Calendar.DAY_OF_MONTH)==MainActivity.sgraph.get(j).beginDate.get(Calendar.DAY_OF_MONTH) 
									&& sg.beginDate.get(Calendar.MONTH) == MainActivity.sgraph.get(j).beginDate.get(Calendar.MONTH)
									&& sg.beginDate.get(Calendar.YEAR) == MainActivity.sgraph.get(j).beginDate.get(Calendar.YEAR)
									&& sg.endDate.get(Calendar.DAY_OF_MONTH)==MainActivity.sgraph.get(j).endDate.get(Calendar.DAY_OF_MONTH) 
									&& sg.endDate.get(Calendar.MONTH) == MainActivity.sgraph.get(j).endDate.get(Calendar.MONTH)
									&& sg.endDate.get(Calendar.YEAR) == MainActivity.sgraph.get(j).endDate.get(Calendar.YEAR))
							{
								MainActivity.usr.portfolios.get(MainActivity.portIndex).stocks.get(i).idsgraph=j;
								exists=true;
								break;
							}
						}
					}
					if(!exists)
						{
							MainActivity.sgraph.add(sg);
							MainActivity.usr.portfolios.get(MainActivity.portIndex).stocks.get(i).idsgraph=MainActivity.sgraph.size()-1;
						}
				}
				
        for(int i=0; i< MainActivity.usr.portfolios.get(MainActivity.portIndex).stocks.size();i++)
        {
        		  Stock stock= MainActivity.usr.portfolios.get(MainActivity.portIndex).stocks.get(i);
				  //Log.d(Integer.toString(i),Integer.toString(MainActivity.sgraph.get(i).points.size()));
				  if(MainActivity.sgraph.get(stock.idsgraph).points.size()<1)
				  {
					HttpURLConnection con = null;
				    String payload = "Error";
				    int numDays=0;
				    double val=0;
				    try {
				    	  boolean first=true;
				    	  Log.d("date", df.format(MainActivity.sgraph.get(stock.idsgraph).beginDate.getTime()));
				    	  int bmonth = MainActivity.sgraph.get(stock.idsgraph).beginDate.get(Calendar.MONTH);
				    	  int bday = MainActivity.sgraph.get(stock.idsgraph).beginDate.get(Calendar.DAY_OF_MONTH);
				    	  int byear = MainActivity.sgraph.get(stock.idsgraph).beginDate.get(Calendar.YEAR);
				    	  
				    	  int emonth = MainActivity.sgraph.get(stock.idsgraph).endDate.get(Calendar.MONTH);
				    	  int eday = MainActivity.sgraph.get(stock.idsgraph).endDate.get(Calendar.DAY_OF_MONTH);
				    	  int eyear = MainActivity.sgraph.get(stock.idsgraph).endDate.get(Calendar.YEAR);
				          // Build RESTful query (GET)
				    	  URL url;
				    	  if(MainActivity.monthPeriodGraph)
				    		  url = new URL("http://ichart.finance.yahoo.com/table.txt?a="+bmonth+"&b="+bday+"&c="+byear+"&d="+emonth+"&e="+eday+"&f="+eyear+"&g=d&s="+MainActivity.sgraph.get(stock.idsgraph).stockAbrev);
				    	  else
					          url = new URL("http://ichart.finance.yahoo.com/table.txt?a="+bmonth+"&b="+bday+"&c="+byear+"&d="+emonth+"&e="+eday+"&f="+eyear+"&g=m&s="+MainActivity.sgraph.get(stock.idsgraph).stockAbrev);
				    	  //Log.d("url", "http://ichart.finance.yahoo.com/table.txt?a="+bmonth+"&b="+bday+"&c="+byear+"&d="+emonth+"&e="+eday+"&f="+eyear+"&g=d&s="+MainActivity.sgraph.get(i).stockAbrev);
				          con = (HttpURLConnection) url.openConnection();
				          con.setReadTimeout(10000);
				          con.setConnectTimeout(15000);
				          con.setRequestMethod("GET");
				          con.setDoInput(true);

				          // Start the query
				          con.connect();
				          
				          // Read results from the query
				          BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8" ));
				          sg = MainActivity.sgraph.get(stock.idsgraph);
				          int iter=0;
				          while((payload = reader.readLine())!=null)
				          {
				        	  if(first)
				        		  first=false;
				        	  else{
				        		  numDays++;
				        		  //Log.d("payload", payload);
				        		  String [] split = payload.split(",");
				        		  Date dat = null;
								try {
										dat = df.parse(split[0]);
									} catch (ParseException e) {
										Log.d("error", e.getMessage());
										e.printStackTrace();
									}
								val=Double.parseDouble(split[4]);
								  if(sg.minY>val)
									  sg.minY=val;
								  if(sg.maxY<val)
									  sg.maxY=val;
								  sg.times.add(dat);
				        		  Pair<Double,Long> p = new Pair<Double,Long>(val,(long)iter);
				        		  iter=iter+1;
				        		  //Log.d("pair", Double.parseDouble(split[4])+" and "+dat.getTime());
				        		  sg.points.add(p);
				        	  }
				          }
				          sg.NoPoints=numDays;
				          MainActivity.sgraph.set(stock.idsgraph, sg);
				          reader.close();
				      } catch (IOException e) {
				    } finally {
				      if (con != null)
				        con.disconnect();
				    }

				    MainActivity.sgraph.get(stock.idsgraph).NoPoints=numDays;
				  }
        		}
				return params[0];
			}
			
			@Override
			protected void onPostExecute(View mainView) {
				if (pd!=null) {
					pd.dismiss();
					switch(response) {
						case -1:
						{
							AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
									getActivity());
					 
								// set title
								alertDialogBuilder.setTitle("Error");
					 
								// set dialog message
								alertDialogBuilder
									.setMessage("Something went wrong. Please try again.")
									.setCancelable(false)
									.setNeutralButton("Ok",new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog,int id) {
											dialog.cancel();
											//getActivity().finish();
										}
									  });
					 
									// create alert dialog
									AlertDialog alertDialog = alertDialogBuilder.create();
					 
									// show it
									alertDialog.show();
						}
							break;
						default:
						{
							
							
							final Portfolio port = MainActivity.usr.portfolios.get(MainActivity.portIndex);
							LinearLayout LLpie = new LinearLayout(getActivity());
							LLpie.setOrientation(LinearLayout.VERTICAL);
							//LL.setPadding(16,16,16,16);
							LLpie.setGravity(Gravity.CENTER);
							LayoutParams LLParamspie = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
							LLpie.setLayoutParams(LLParamspie);
							
							LayoutParams LLParamspiebutton = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
							LLParamspiebutton.setMargins(0, 10, 0, 20);
							
							LinearLayout LL1 = new LinearLayout(getActivity());
							LL1.setOrientation(LinearLayout.VERTICAL);
							LL1.setBackgroundResource(R.color.blue_light);
							LayoutParams LLParams1 = new LayoutParams(LayoutParams.MATCH_PARENT,1);
							LLParams1.setMargins(0, 0, 0, 30);
							LL1.setLayoutParams(LLParams1);
							
							LinearLayout LLH = new LinearLayout(getActivity());
							LLH.setOrientation(LinearLayout.HORIZONTAL);
							LLH.setGravity(Gravity.CENTER);
							LayoutParams LLParamsH = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
							LLH.setLayoutParams(LLParamsH);
							
							LinearLayout LLV1 = new LinearLayout(getActivity());
							LLV1.setOrientation(LinearLayout.VERTICAL);
							LLV1.setGravity(Gravity.CENTER);
							LayoutParams LLParamsV1 = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
							LLParamsV1.setMargins(0, 0, 30, 10);
							LLV1.setLayoutParams(LLParamsV1);
							
							LinearLayout LLV2 = new LinearLayout(getActivity());
							LLV2.setOrientation(LinearLayout.VERTICAL);
							LLV2.setGravity(Gravity.CENTER);
							LayoutParams LLParamsV2 = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
							LLParamsV2.setMargins(30, 0, 0, 10);
							LLV2.setLayoutParams(LLParamsV2);
							
							final ImageButton piebtn = new ImageButton(getActivity());
							piebtn.setImageResource(R.drawable.chart_256);
							piebtn.setOnClickListener(buttonClickListener);
							piebtn.setLayoutParams(LLParamspiebutton);
							piebtn.setBackgroundResource(R.drawable.button_customize5);

							piebtn.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										PieGraph pie = new PieGraph();
										pie.port = port;
								    	Intent lineIntent = pie.getIntent(getActivity());
								        startActivity(lineIntent);

									}
						        });
					        LLV1.addView(piebtn);
					        TextView Piegraphtitle1 = new TextView(getActivity());
					        Piegraphtitle1.setText("Number of Stocks");
					        Piegraphtitle1.setGravity(Gravity.CENTER);
					        Piegraphtitle1.setTextColor(Color.WHITE);
					        Piegraphtitle1.setTextSize(23f);
					        Piegraphtitle1.setLayoutParams(new LayoutParams(
					                LayoutParams.WRAP_CONTENT,
					                LayoutParams.WRAP_CONTENT));    
						    
						    LLV1.addView(Piegraphtitle1);
					        
					        
					        final ImageButton piebtn2 = new ImageButton(getActivity());
							piebtn2.setImageResource(R.drawable.chart_256);
							piebtn2.setOnClickListener(buttonClickListener);
							piebtn2.setLayoutParams(LLParamspiebutton);
							piebtn2.setBackgroundResource(R.drawable.button_customize5);

							piebtn2.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										TotalPieGraph pie = new TotalPieGraph();
										pie.port = port;
								    	Intent lineIntent = pie.getIntent(getActivity());
								        startActivity(lineIntent);

									}
						        });
					        LLV2.addView(piebtn2);
					        
					        TextView Piegraphtitle2 = new TextView(getActivity());
					        Piegraphtitle2.setText("Value of Stocks");
					        Piegraphtitle2.setGravity(Gravity.CENTER);
					        Piegraphtitle2.setTextColor(Color.WHITE);
					        Piegraphtitle2.setTextSize(23f);
					        Piegraphtitle2.setLayoutParams(new LayoutParams(
					                LayoutParams.WRAP_CONTENT,
					                LayoutParams.WRAP_CONTENT));    
						    
						    LLV2.addView(Piegraphtitle2);
					        
					        LLH.addView(LLV1);
					        LLH.addView(LLV2);
					        
					        LLpie.addView(LLH);
					        
					        
							LLpie.addView(LL1);
							LinearLayout lnl= (LinearLayout) mainView.findViewById(R.id.graphs);
							if(port.stocks.size()>0)
								lnl.addView(LLpie);
							
							for(int i=0; i<MainActivity.usr.portfolios.get(MainActivity.portIndex).stocks.size();i++)
							{
								Stock stock= MainActivity.usr.portfolios.get(MainActivity.portIndex).stocks.get(i);
								if(MainActivity.sgraph.get(stock.idsgraph).monthPeriod != MainActivity.monthPeriodGraph)
								{}
						        //Log.d(MainActivity.sgraph.get(i).stockName, Integer.toString(MainActivity.sgraph.get(i).NoPoints));
								else if(MainActivity.sgraph.get(stock.idsgraph).NoPoints<=1)							
								{
									int num = 1;
									GraphViewData[] data = new GraphViewData[2];
									int k=1;
									for (int j=0; j<2; j++) {
										long x=j;
										double y=0;
										data[k] = new GraphViewData(x, y); // next day
										k--;
									}
								GraphViewSeries exampleSeries = new GraphViewSeries(data);
								GraphView graphView;
								graphView = new LineGraphView(
											getActivity()
											, ""
									);
								((LineGraphView) graphView).setDrawBackground(true);
								

								graphView.addSeries(exampleSeries);
								graphView.getGraphViewStyle().setVerticalLabelsColor(Color.WHITE);
								graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.WHITE);
								graphView.getGraphViewStyle().setVerticalLabelsWidth(120);
								graphView.getGraphViewStyle().setTextSize(20);
								LinearLayout LL = new LinearLayout(getActivity());
								LL.setOrientation(LinearLayout.VERTICAL);
								LayoutParams LLParams = new LayoutParams(LayoutParams.MATCH_PARENT,400);
								LLParams.gravity=Gravity.CENTER;
								LL.setLayoutParams(LLParams);
								LL.setWeightSum(2);
								
								LinearLayout LL3 = new LinearLayout(getActivity());
								LL3.setOrientation(LinearLayout.VERTICAL);
								LayoutParams LLParams3 = new LayoutParams(LayoutParams.MATCH_PARENT,250);
								LLParams3.weight=1;
								LL3.setId(i);
								LL3.setLayoutParams(LLParams3);

								MainActivity.graphs.add(graphView);
								LL3.addView(MainActivity.graphs.get(MainActivity.graphs.size()-1));
								
								TextView title = new TextView(getActivity());
						        title.setText(stock.Name);
						        title.setGravity(Gravity.CENTER);
						        title.setTextColor(Color.WHITE);
						        title.setTextSize(23f);
						        title.setLayoutParams(new LayoutParams(
						                LayoutParams.FILL_PARENT,
						                LayoutParams.WRAP_CONTENT));    
							    
							    LL.addView(title);
								LL.addView(LL3);

								LinearLayout linla= (LinearLayout) mainView.findViewById(R.id.graphs);
							    linla.addView(LL);

							}
								else
							{
							final int stockId = stock.idsgraph;
							//int num = MainActivity.sgraph.get(i).NoPoints;
							GraphViewData[] data = new GraphViewData[5];
							int k=4;
							int position=5;
							MainActivity.sgraph.get(stock.idsgraph).pos=position;
							for (int j=0; j<position; j++) {
								long x=MainActivity.sgraph.get(stock.idsgraph).points.get(j).second;
								double y=MainActivity.sgraph.get(stock.idsgraph).points.get(j).first;
								data[k] = new GraphViewData(x, y); // next day
								k--;
							}
							totaldiff = (MainActivity.sgraph.get(stock.idsgraph).maxY-MainActivity.sgraph.get(stock.idsgraph).minY)/3;
							minVal=MainActivity.sgraph.get(stock.idsgraph).minY;
							GraphViewSeriesStyle seriesStyle1 = new GraphViewSeriesStyle();
							seriesStyle1.setValueDependentColor(new ValueDependentColor() {
								@Override
								public int get(GraphViewDataInterface data) {
									if(data.getY()<= (minVal+totaldiff*1))
										return Color.rgb(0, 50, 150);
									else if(data.getY()<= (minVal+totaldiff*2))
										return Color.rgb(50, 100, 200);
									else
										return Color.rgb(100, 150, 255);
									
									
									//return Color.rgb((int)(150-((data.getY()/10)*100)), (int)(150+((data.getY()/10)*150)), (int)(150-((data.getY()/10)*150)));
								}
							});
							
							GraphViewSeries exampleSeries = new GraphViewSeries("Stock Price", seriesStyle1, data);
							
							
							stockGraph gs = MainActivity.sgraph.get(stock.idsgraph);
							gs.gvs=exampleSeries;
							//gs.gvs2=BoughtPriceSeries;
							
							GraphView graphView;
							graphView = new LineGraphView(
										getActivity()
										, ""
								);
							((LineGraphView) graphView).setDrawBackground(true);
							//int num = MainActivity.sgraph.get(stock.idsgraph).NoPoints;
							GraphViewData[] data1 = new GraphViewData[5];
							k=4;
							for (int l=0; l<position; l++) {
								data1[k] = new GraphViewData(l, stock.unitPrice);
								k--;
							}
							GraphViewSeries BoughtPriceSeries = new GraphViewSeries("Bought Price", new GraphViewSeriesStyle(Color.rgb(0, 0, 250), 3), data1);
							graphView.addSeries(BoughtPriceSeries); // data
							graphView.addSeries(exampleSeries); // data
							gs.gvs2=BoughtPriceSeries;
							MainActivity.sgraph.set(stock.idsgraph, gs);
							graphView.setShowLegend(true);
							
							
							graphView.setManualYAxisBounds(MainActivity.sgraph.get(stock.idsgraph).maxY, MainActivity.sgraph.get(stock.idsgraph).minY);
							/*
							 * date as label formatter
							 */
							graphView.setCustomLabelFormatter(new CustomLabelFormatter() {
								@Override
								public String formatLabel(double value, boolean isValueX) {
									if (isValueX) {
										if(MainActivity.sgraph.get(stockId).monthPeriod)
											return dateFormat.format(MainActivity.sgraph.get(stockId).times.get((int) value));
										else
											return dateYearFormat.format(MainActivity.sgraph.get(stockId).times.get((int) value));									}
									else{
										double d= value;
										String text = Double.toString(Math.abs(d));
										int integerPlaces = text.indexOf('.');
										int decimalPlaces = text.length() - integerPlaces - 1;
										if(decimalPlaces>3)
										{
											value=Math.round(d*100.0)/100.0;
										}
										return ("$"+Double.toString(value));
									}
								}
							});
							
							//graphView.setViewPort(0, 7);
							//graphView.setScalable(true);
							graphView.getGraphViewStyle().setNumHorizontalLabels(5);
							graphView.getGraphViewStyle().setVerticalLabelsWidth(120);
							graphView.getGraphViewStyle().setTextSize(20);
							graphView.getGraphViewStyle().setVerticalLabelsColor(Color.WHITE);
							graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.WHITE);
							graphView.getGraphViewStyle().setLegendWidth(160);
							graphView.setId(i+1);
							
							
							
							
							LinearLayout LL = new LinearLayout(getActivity());
							LL.setOrientation(LinearLayout.VERTICAL);
							LayoutParams LLParams = new LayoutParams(LayoutParams.MATCH_PARENT,400);
							LLParams.gravity=Gravity.CENTER;
							LL.setLayoutParams(LLParams);
							LL.setWeightSum(2);
							
							LinearLayout LL2 = new LinearLayout(getActivity());
							LL2.setOrientation(LinearLayout.HORIZONTAL);
							LayoutParams LLParams2 = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
							LLParams2.weight=1;
							LLParams2.gravity=Gravity.CENTER;
							LL2.setLayoutParams(LLParams2);
							
							LinearLayout LL3 = new LinearLayout(getActivity());
							LL3.setOrientation(LinearLayout.VERTICAL);
							LayoutParams LLParams3 = new LayoutParams(LayoutParams.MATCH_PARENT,250);
							LLParams3.weight=1;
							LL3.setId(i);
							LL3.setLayoutParams(LLParams3);

							MainActivity.graphs.add(graphView);
							LL3.addView(MainActivity.graphs.get(MainActivity.graphs.size()-1));
							LayoutParams LLParams4 = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
							LLParams4.setMargins(20, 0, 20, 0);
							
							LayoutParams LLParams6 = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
							LLParams6.setMargins(80, 0, 0, 0);
							
							LayoutParams LLParams5 = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
							LLParams5.setMargins(0, 0, 80, 0);
							
							LayoutParams LLParams7 = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
							LLParams7.setMargins(20, 0, 0, 0);
							
							LayoutParams LLParams8 = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
							LLParams8.setMargins(0, 0, 20, 0);
							
							
							
							ImageButton btnm1 = new ImageButton(getActivity());
							btnm1.setImageResource(R.drawable.ic_action_rewind);
							btnm1.setOnClickListener(buttonClickListener);
							btnm1.setBackgroundResource(R.drawable.button_customize);
							btnm1.setLayoutParams(LLParams8);
							btnm1.setTag(5);
							btnm1.setId(i);
					        
							btnm1.setOnClickListener(new OnClickListener() {
					       	 
								@Override
								public void onClick(View v) {
									View p = (View)v.getParent();
									View parent = (View)p.getParent();
									for(int i=0; i<MainActivity.usr.portfolios.get(MainActivity.portIndex).stocks.size(); i++)
				    				{
										Log.d("i", Integer.toString(i));
										Stock stock= MainActivity.usr.portfolios.get(MainActivity.portIndex).stocks.get(i);
										Random rand = new Random();
										long now = new Date().getTime();
				    					stockGraph sg = MainActivity.sgraph.get(stock.idsgraph);
				        				if(v.getId()==i)
				        				{
				        					int position= sg.pos+5;
				        					int begin = sg.pos;
				        					if(!sg.turnLeft)
			        						{
			        							position=position+5;
			        							begin=sg.pos+5;
			        							sg.turnLeft=true;
			        						}
				        					
				        					if(position>sg.NoPoints)
				        						position=sg.NoPoints;
				        					
				        					int diff = position-begin;
				        					MainActivity.sgraph.get(stock.idsgraph).pos=position;
				        					if(diff<5)
				        					{
				        						begin = begin-(5-diff);
				        					}
				        					int k=4;
				        					GraphViewData[] data = new GraphViewData[5];
				        					for (int j=begin; j<position; j++) {
												long x=sg.points.get(j).second;
												double y=sg.points.get(j).first;
												data[k] = new GraphViewData(x, y); // next day
												k--;
											}
				        					totaldiff = (MainActivity.sgraph.get(stock.idsgraph).maxY-MainActivity.sgraph.get(stock.idsgraph).minY)/3;
											minVal=MainActivity.sgraph.get(stock.idsgraph).minY;
											GraphViewSeriesStyle seriesStyle1 = new GraphViewSeriesStyle();
											seriesStyle1.setValueDependentColor(new ValueDependentColor() {
												@Override
												public int get(GraphViewDataInterface data) {
													if(data.getY()<= (minVal+totaldiff*1))
														return Color.rgb(0, 50, 150);
													else if(data.getY()<= (minVal+totaldiff*2))
														return Color.rgb(50, 100, 200);
													else
														return Color.rgb(100, 150, 255);
													
													
													//return Color.rgb((int)(150-((data.getY()/10)*100)), (int)(150+((data.getY()/10)*150)), (int)(150-((data.getY()/10)*150)));
												}
											});
											GraphViewSeries exampleSeries = new GraphViewSeries("Stock Price", seriesStyle1, data);
				        					sg.gvs = exampleSeries;
				        					GraphViewData[] data1 = new GraphViewData[5];
											k=4;
											for (int l=begin; l<position; l++) {
												data1[k] = new GraphViewData(l, stock.unitPrice);
												k--;
											}
											GraphViewSeries BoughtPriceSeries = new GraphViewSeries("Bought Price", new GraphViewSeriesStyle(Color.rgb(0, 0, 250), 3), data1);
											sg.gvs2=BoughtPriceSeries;
				        					MainActivity.sgraph.set(stock.idsgraph, sg);
				        					MainActivity.graphs.get(i).removeAllSeries();
				        					MainActivity.graphs.get(i).addSeries(BoughtPriceSeries);
				        					MainActivity.graphs.get(i).addSeries(exampleSeries); // data
				        					
				        					/*MainActivity.graphs.get(i).setCustomLabelFormatter(new CustomLabelFormatter() {
				        						@Override
				        						public String formatLabel(double value, boolean isValueX) {
				        							if (isValueX) {
				        								Date d = new Date((long) value);
				        								return dateFormat.format(d);
				        							}
				        							else{
				        								return ("$"+Double.toString(value));
				        							}
				        						}
				        					});*/
				        					break;
				        				}
				        				//Log.i("test", "got here2");
				    				}

								}
					        });
										
					        LL2.addView(btnm1);
							
							
							
							ImageButton btn = new ImageButton(getActivity());
							btn.setImageResource(R.drawable.ic_action_previous_item);
					        btn.setOnClickListener(buttonClickListener);
					        btn.setBackgroundResource(R.drawable.button_customize);
					        btn.setLayoutParams(LLParams5);
					        btn.setTag(1);
					        btn.setId(i);
					        
					        btn.setOnClickListener(new OnClickListener() {
					       	 
								@Override
								public void onClick(View v) {
									View p = (View)v.getParent();
									View parent = (View)p.getParent();
									for(int i=0; i<MainActivity.usr.portfolios.get(MainActivity.portIndex).stocks.size(); i++)
				    				{
										Stock stock= MainActivity.usr.portfolios.get(MainActivity.portIndex).stocks.get(i);
										Random rand = new Random();
										long now = new Date().getTime();
				    					stockGraph sg = MainActivity.sgraph.get(stock.idsgraph);
				        				if(v.getId()==i)
				        				{
				        					int position= sg.pos+1;
				        					int begin = sg.pos-4;
				        					if(!sg.turnLeft)
			        						{
			        							position=position+5;
			        							begin=sg.pos+1;
			        							sg.turnLeft=true;
			        						}
				        					
				        					if(position>sg.NoPoints)
				        						position=sg.NoPoints;
				        					
				        					int diff = position-begin;
				        					MainActivity.sgraph.get(stock.idsgraph).pos=position;
				        					if(diff<5)
				        					{
				        						begin = begin-(5-diff);
				        					}
				        					int k=4;
				        					GraphViewData[] data = new GraphViewData[5];
				        					for (int j=begin; j<position; j++) {
												long x=sg.points.get(j).second;
												double y=sg.points.get(j).first;
												data[k] = new GraphViewData(x, y); // next day
												k--;
											}
				        					totaldiff = (MainActivity.sgraph.get(stock.idsgraph).maxY-MainActivity.sgraph.get(stock.idsgraph).minY)/3;
											minVal=MainActivity.sgraph.get(stock.idsgraph).minY;
											GraphViewSeriesStyle seriesStyle1 = new GraphViewSeriesStyle();
											seriesStyle1.setValueDependentColor(new ValueDependentColor() {
												@Override
												public int get(GraphViewDataInterface data) {
													if(data.getY()<= (minVal+totaldiff*1))
														return Color.rgb(0, 50, 150);
													else if(data.getY()<= (minVal+totaldiff*2))
														return Color.rgb(50, 100, 200);
													else
														return Color.rgb(100, 150, 255);
													
													
													//return Color.rgb((int)(150-((data.getY()/10)*100)), (int)(150+((data.getY()/10)*150)), (int)(150-((data.getY()/10)*150)));
												}
											});
											GraphViewSeries exampleSeries = new GraphViewSeries("Stock Price", seriesStyle1, data);
				        					sg.gvs = exampleSeries;
				        					GraphViewData[] data1 = new GraphViewData[5];
											k=4;
											for (int l=begin; l<position; l++) {
												data1[k] = new GraphViewData(l, stock.unitPrice);
												k--;
											}
											GraphViewSeries BoughtPriceSeries = new GraphViewSeries("Bought Price", new GraphViewSeriesStyle(Color.rgb(0, 0, 250), 3), data1);
											sg.gvs2=BoughtPriceSeries;
				        					MainActivity.sgraph.set(stock.idsgraph, sg);
				        					MainActivity.graphs.get(i).removeAllSeries();
				        					MainActivity.graphs.get(i).addSeries(BoughtPriceSeries);

				        					MainActivity.graphs.get(i).addSeries(exampleSeries); // data
				        					
				        					/*MainActivity.graphs.get(i).setCustomLabelFormatter(new CustomLabelFormatter() {
				        						@Override
				        						public String formatLabel(double value, boolean isValueX) {
				        							if (isValueX) {
				        								Date d = new Date((long) value);
				        								return dateFormat.format(d);
				        							}
				        							else{
				        								return ("$"+Double.toString(value));
				        							}
				        						}
				        					});*/
				        					break;
				        				}
				        				//Log.i("test", "got here2");
				    				}

								}
					        });
										
					        LL2.addView(btn);
					        
					        ImageButton btn1 = new ImageButton(getActivity());
					        btn1.setImageResource(R.drawable.line_graph);
					        btn1.setOnClickListener(buttonClickListener);
					        btn1.setBackgroundResource(R.drawable.button_customize3);
					        btn1.setLayoutParams(LLParams4);
					        btn1.setTag(2);
					        btn1.setId(i);
					        btn1.setOnClickListener(new OnClickListener() {
						       	 
									@Override
									public void onClick(View v) {
										if(MainActivity.betterCharts)
										{
											for(int i=0; i<MainActivity.usr.portfolios.get(MainActivity.portIndex).stocks.size(); i++)
						    				{
												Stock stock= MainActivity.usr.portfolios.get(MainActivity.portIndex).stocks.get(i);
						    					stockGraph sg = MainActivity.sgraph.get(stock.idsgraph);
						        				if(v.getId()==i)
						        				{
													LinearGraph lin = new LinearGraph();
													lin.sgraph = sg;
													lin.isport=stock.unitPrice;
											    	Intent lineIntent = lin.getIntent(getActivity());
											        startActivity(lineIntent);
						        					break;
						        				}
						    				}

										}
										else
										{
										View p = (View)v.getParent();
										View parent = (View)p.getParent();
										for(int i=0; i<MainActivity.usr.portfolios.get(MainActivity.portIndex).stocks.size(); i++)
					    				{
											Stock stock= MainActivity.usr.portfolios.get(MainActivity.portIndex).stocks.get(i);
					    					stockGraph sg = MainActivity.sgraph.get(stock.idsgraph);
					        				if(v.getId()==i)
					        				{
					        					GraphView graphView;
					        					graphView = new LineGraphView(
					        								getActivity()
					        								, ""
					        						);
					        					/*
					        					 * date as label formatter
					        					 */
					        					graphView.addSeries(sg.gvs2); // data
					        					graphView.addSeries(sg.gvs);

					        					graphView.getGraphViewStyle().setNumHorizontalLabels(5);
					        					graphView.setManualYAxisBounds(MainActivity.sgraph.get(stock.idsgraph).maxY, MainActivity.sgraph.get(stock.idsgraph).minY);
					        					graphView.getGraphViewStyle().setVerticalLabelsWidth(120);
					        					graphView.getGraphViewStyle().setTextSize(20);
												graphView.getGraphViewStyle().setVerticalLabelsColor(Color.WHITE);
												graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.WHITE);
												graphView.getGraphViewStyle().setLegendWidth(160);
												graphView.setShowLegend(true);
					        					graphView.setCustomLabelFormatter(new CustomLabelFormatter() {
													@Override
													public String formatLabel(double value, boolean isValueX) {
														if (isValueX) {
															if(MainActivity.sgraph.get(stockId).monthPeriod)
																return dateFormat.format(MainActivity.sgraph.get(stockId).times.get((int) value));
															else
																return dateYearFormat.format(MainActivity.sgraph.get(stockId).times.get((int) value));														}
														else{
															double d= value;
															String text = Double.toString(Math.abs(d));
															int integerPlaces = text.indexOf('.');
															int decimalPlaces = text.length() - integerPlaces - 1;
															if(decimalPlaces>3)
															{
																value=Math.round(d*100.0)/100.0;
															}
															return ("$"+Double.toString(value));
														}
													}
												});
					        					((LineGraphView) graphView).setDrawBackground(true);
					        					LinearLayout ln = (LinearLayout) parent.findViewById(v.getId());
					        					Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.right_slide_graph);
					        					animation.setStartOffset(0);
					        					ln.removeAllViews();
					        					MainActivity.graphs.set(i,graphView);
					        					ln.addView(MainActivity.graphs.get(i));

					        					MainActivity.graphs.get(i).startAnimation(animation);
					        					break;
					        				}
					        				//Log.i("test", "got here2");
					    					}
										}
									}
						        });
					        LL2.addView(btn1);
					        
					        ImageButton btn2 = new ImageButton(getActivity());
					        btn2.setImageResource(R.drawable.bar_graph);
					        btn2.setOnClickListener(buttonClickListener);
					        btn2.setBackgroundResource(R.drawable.button_customize3);
					        btn2.setLayoutParams(LLParams4);
					        btn2.setTag(3);
					        btn2.setId(i);
					        btn2.setOnClickListener(new OnClickListener() {
						       	 
									@Override
									public void onClick(View v) {
										View p = (View)v.getParent();
										View parent = (View)p.getParent();
										if(MainActivity.betterCharts)
										{
											for(int i=0; i<MainActivity.usr.portfolios.get(MainActivity.portIndex).stocks.size(); i++)
						    				{
												Stock stock= MainActivity.usr.portfolios.get(MainActivity.portIndex).stocks.get(i);
						    					stockGraph sg = MainActivity.sgraph.get(stock.idsgraph);
						        				if(v.getId()==i)
						        				{
													BarGraph bar = new BarGraph();
													bar.sgraph = sg;
													bar.isport=stock.unitPrice;
											    	Intent lineIntent = bar.getIntent(getActivity());
											        startActivity(lineIntent);
						        					break;
						        				}
						    				}
										}
										else
										{
										for(int i=0; i<MainActivity.usr.portfolios.get(MainActivity.portIndex).stocks.size(); i++)
					    				{
											Stock stock= MainActivity.usr.portfolios.get(MainActivity.portIndex).stocks.get(i);
					    					stockGraph sg = MainActivity.sgraph.get(stock.idsgraph);
					        				if(v.getId()==i)
					        				{
					        					GraphView graphView;
					        					graphView = new BarGraphView(
					        								getActivity()
					        								, ""
					        						);
					        					graphView.addSeries(sg.gvs2); // data
					        					graphView.addSeries(sg.gvs);

					        					graphView.setManualYAxisBounds(MainActivity.sgraph.get(stock.idsgraph).maxY, MainActivity.sgraph.get(stock.idsgraph).minY);
					        					/*
					        					 * date as label formatter
					        					 */

					        					graphView.getGraphViewStyle().setNumHorizontalLabels(5);
					        					graphView.getGraphViewStyle().setVerticalLabelsWidth(120);
					        					graphView.getGraphViewStyle().setTextSize(20);
					        					graphView.getGraphViewStyle().setVerticalLabelsColor(Color.WHITE);
												graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.WHITE);
												graphView.getGraphViewStyle().setLegendWidth(160);
												graphView.setShowLegend(true);
					        					graphView.setCustomLabelFormatter(new CustomLabelFormatter() {
													@Override
													public String formatLabel(double value, boolean isValueX) {
														if (isValueX) {
															if(MainActivity.sgraph.get(stockId).monthPeriod)
																return dateFormat.format(MainActivity.sgraph.get(stockId).times.get((int) value));
															else
																return dateYearFormat.format(MainActivity.sgraph.get(stockId).times.get((int) value));														}
														else{
															double d= value;
															String text = Double.toString(Math.abs(d));
															int integerPlaces = text.indexOf('.');
															int decimalPlaces = text.length() - integerPlaces - 1;
															if(decimalPlaces>3)
															{
																value=Math.round(d*100.0)/100.0;
															}
															return ("$"+Double.toString(value));
														}
													}
												});
					        					LinearLayout ln = (LinearLayout) parent.findViewById(v.getId());
					        					Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.right_slide_graph);
					        					animation.setStartOffset(0);
					        					ln.removeAllViews();
					        					MainActivity.graphs.set(i,graphView);
					        					ln.addView(MainActivity.graphs.get(i));

					        					MainActivity.graphs.get(i).startAnimation(animation);
					        					break;
					        				}
					    					}
										}
									}
						        });
					        LL2.addView(btn2);
					        
					        ImageButton btn3 = new ImageButton(getActivity());
					        btn3.setImageResource(R.drawable.ic_action_next_item);
					        btn3.setOnClickListener(buttonClickListener);
					        btn3.setBackgroundResource(R.drawable.button_customize);
					        btn3.setLayoutParams(LLParams6);
					        btn3.setTag(4);
					        btn3.setId(i);
					        btn3.setOnClickListener(new OnClickListener() {
						       	 
								@Override
								public void onClick(View v) {
									View p = (View)v.getParent();
									View parent = (View)p.getParent();
									for(int i=0; i<MainActivity.usr.portfolios.get(MainActivity.portIndex).stocks.size(); i++)
				    				{
										Stock stock= MainActivity.usr.portfolios.get(MainActivity.portIndex).stocks.get(i);
				    					stockGraph sg = MainActivity.sgraph.get(stock.idsgraph);
				        				if(v.getId()==i)
				        				{
				        					int position= sg.pos-1;
				        					int end = sg.pos+4;
				        					if(sg.turnLeft)
				        						{
				        							position=position-5;
				        							end=sg.pos-1;
				        							sg.turnLeft=false;
				        						}
				        					if(position<0)
				        						position=0;
				        					int diff = end-position;
				        					MainActivity.sgraph.get(stock.idsgraph).pos=position;
				        					if(diff<5)
				        					{
				        						end = end+(5-diff);
				        					}
				        					int k=4;
				        					GraphViewData[] data = new GraphViewData[5];

				        					for (int j=position; j<end; j++) {
												long x=sg.points.get(j).second;
												double y=sg.points.get(j).first;
												data[k] = new GraphViewData(x, y); // next day
												k--;
											}
				        					totaldiff = (MainActivity.sgraph.get(stock.idsgraph).maxY-MainActivity.sgraph.get(stock.idsgraph).minY)/3;
											minVal=MainActivity.sgraph.get(stock.idsgraph).minY;
											GraphViewSeriesStyle seriesStyle1 = new GraphViewSeriesStyle();
											seriesStyle1.setValueDependentColor(new ValueDependentColor() {
												@Override
												public int get(GraphViewDataInterface data) {
													if(data.getY()<= (minVal+totaldiff*1))
														return Color.rgb(0, 50, 150);
													else if(data.getY()<= (minVal+totaldiff*2))
														return Color.rgb(50, 100, 200);
													else
														return Color.rgb(100, 150, 255);
													
													
													//return Color.rgb((int)(150-((data.getY()/10)*100)), (int)(150+((data.getY()/10)*150)), (int)(150-((data.getY()/10)*150)));
												}
											});
											GraphViewSeries exampleSeries = new GraphViewSeries("Stock Price", seriesStyle1, data);
				        					sg.gvs = exampleSeries;
				        					GraphViewData[] data1 = new GraphViewData[5];
											k=4;
											for (int l=position; l<end; l++) {
												data1[k] = new GraphViewData(l, stock.unitPrice);
												k--;
											}
											GraphViewSeries BoughtPriceSeries = new GraphViewSeries("Bought Price", new GraphViewSeriesStyle(Color.rgb(0, 0, 250), 3), data1);
											sg.gvs2=BoughtPriceSeries;
				        					MainActivity.sgraph.set(stock.idsgraph, sg);
				        					
				        					MainActivity.graphs.get(i).removeAllSeries();
				        					MainActivity.graphs.get(i).addSeries(BoughtPriceSeries);
				        					MainActivity.graphs.get(i).addSeries(exampleSeries); // data
				        					
				        					
				        					/*MainActivity.graphs.get(i).setCustomLabelFormatter(new CustomLabelFormatter() {
				        						@Override
				        						public String formatLabel(double value, boolean isValueX) {
				        							if (isValueX) {
				        								Date d = new Date((long) value);
				        								return dateFormat.format(d);
				        							}
				        							else{
				        								return ("$"+Double.toString(value));
				        							}
				        						}
				        					});*/
				        					break;
				        				}
				        				//Log.i("test", "got here2");
				    				}

								}
					        });
					        LL2.addView(btn3);
					        //LL2.addView(graphView);
					        
					        ImageButton btnm2 = new ImageButton(getActivity());
							btnm2.setImageResource(R.drawable.ic_action_fast_forward);
							btnm2.setOnClickListener(buttonClickListener);
							btnm2.setBackgroundResource(R.drawable.button_customize);
							btnm2.setLayoutParams(LLParams7);
							btnm2.setTag(6);
							btnm2.setId(i);
					        
							btnm2.setOnClickListener(new OnClickListener() {
					       	 
								@Override
								public void onClick(View v) {
									View p = (View)v.getParent();
									View parent = (View)p.getParent();
									for(int i=0; i<MainActivity.usr.portfolios.get(MainActivity.portIndex).stocks.size(); i++)
				    				{
										Stock stock= MainActivity.usr.portfolios.get(MainActivity.portIndex).stocks.get(i);
				    					stockGraph sg = MainActivity.sgraph.get(stock.idsgraph);
				        				if(v.getId()==i)
				        				{
				        					int position= sg.pos-5;
				        					int end = sg.pos;
				        					if(sg.turnLeft)
				        						{
				        							position=position-5;
				        							end=sg.pos-5;
				        							sg.turnLeft=false;
				        						}
				        					if(position<0)
				        						position=0;
				        					int diff = end-position;
				        					MainActivity.sgraph.get(stock.idsgraph).pos=position;
				        					if(diff<5)
				        					{
				        						end = end+(5-diff);
				        					}
				        					int k=4;
				        					GraphViewData[] data = new GraphViewData[5];

				        					for (int j=position; j<end; j++) {
												long x=sg.points.get(j).second;
												double y=sg.points.get(j).first;
												data[k] = new GraphViewData(x, y); // next day
												k--;
											}
				        					totaldiff = (MainActivity.sgraph.get(stock.idsgraph).maxY-MainActivity.sgraph.get(stock.idsgraph).minY)/3;
											minVal=MainActivity.sgraph.get(stock.idsgraph).minY;
											GraphViewSeriesStyle seriesStyle1 = new GraphViewSeriesStyle();
											seriesStyle1.setValueDependentColor(new ValueDependentColor() {
												@Override
												public int get(GraphViewDataInterface data) {
													if(data.getY()<= (minVal+totaldiff*1))
														return Color.rgb(0, 50, 150);
													else if(data.getY()<= (minVal+totaldiff*2))
														return Color.rgb(50, 100, 200);
													else
														return Color.rgb(100, 150, 255);
													
													
													//return Color.rgb((int)(150-((data.getY()/10)*100)), (int)(150+((data.getY()/10)*150)), (int)(150-((data.getY()/10)*150)));
												}
											});
											GraphViewSeries exampleSeries = new GraphViewSeries("Stock Price", seriesStyle1, data);
				        					sg.gvs = exampleSeries;
				        					GraphViewData[] data1 = new GraphViewData[5];
											k=4;
											for (int l=position; l<end; l++) {
												data1[k] = new GraphViewData(l, stock.unitPrice);
												k--;
											}
											GraphViewSeries BoughtPriceSeries = new GraphViewSeries("Bought Price", new GraphViewSeriesStyle(Color.rgb(0, 0, 250), 3), data1);
											sg.gvs2=BoughtPriceSeries;
				        					MainActivity.sgraph.set(stock.idsgraph, sg);
				        					
				        					MainActivity.graphs.get(i).removeAllSeries();
				        					MainActivity.graphs.get(i).addSeries(BoughtPriceSeries);
				        					MainActivity.graphs.get(i).addSeries(exampleSeries); // data
				        					
				        					
				        					/*MainActivity.graphs.get(i).setCustomLabelFormatter(new CustomLabelFormatter() {
				        						@Override
				        						public String formatLabel(double value, boolean isValueX) {
				        							if (isValueX) {
				        								Date d = new Date((long) value);
				        								return dateFormat.format(d);
				        							}
				        							else{
				        								return ("$"+Double.toString(value));
				        							}
				        						}
				        					});*/
				        					break;
				        				}
				        				//Log.i("test", "got here2");
				    				}

								}
					        });
										
					        LL2.addView(btnm2);
					        
					        

					        TextView title = new TextView(getActivity());
					        title.setText(stock.Name);
					        title.setId(5);
					        title.setGravity(Gravity.CENTER);
					        title.setTextColor(Color.WHITE);
					        title.setTextSize(23f);
					        title.setLayoutParams(new LayoutParams(
					                LayoutParams.FILL_PARENT,
					                LayoutParams.WRAP_CONTENT));    
						    
						    LL.addView(title);
							LL.addView(LL3);
							LL.addView(LL2);

							LinearLayout linl1= (LinearLayout) mainView.findViewById(R.id.graphs);
						    linl1.addView(LL);

					        setRetainInstance(false);

							}

								
							} 
						}
							break;

					}
				}
			}
				
		};
		View[] params = new View[2];
		params[0] = mainView;
			task.execute(params);
        	
        	return mainView;
    }
    

    
    
    
    
    private View.OnClickListener buttonClickListener = new View.OnClickListener() {

    	@Override
    	public void onClick(View v) {

    	    //check which green ball was clicked
    	        //int selected_item = (Integer) v.getTag();
    	        /*switch(selected_item)
    	        {
    	        case 1: {
    	        			
    	        		}
    	        	break;
    	        case 2:{
    	        	
    	        		}
    	        	break;
    	        case 3: {
    	        			for(int i=0; i<MainActivity.sgraph.size();i++)
    	        				{
    	        					stockGraph sg = MainActivity.sgraph.get(i);
	    	        				if(v.getId()==sg.id)
	    	        				{
	    	        					GraphViewData[] data = new GraphViewData[sg.NoPoints];
	    	        					for (int j=0; j<sg.NoPoints; j++) {
	    	        						long x=sg.points.get(j).first;
	    	        						long y=sg.points.get(j).second;
	    	        						data[i] = new GraphViewData(x, y); // next day
	    	        					}
	    	        					
	    	        					GraphViewSeries exampleSeries = new GraphViewSeries(data);
	    	        					GraphView graphView;
	    	        					graphView = new BarGraphView(
	    	        								getActivity()
	    	        								, sg.stockName
	    	        						);
	    	        					graphView.addSeries(exampleSeries); // data
	
	    	        					
	    	        					final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d");
	    	        					graphView.setCustomLabelFormatter(new CustomLabelFormatter() {
	    	        						@Override
	    	        						public String formatLabel(double value, boolean isValueX) {
	    	        							if (isValueX) {
	    	        								Date d = new Date((long) value);
	    	        								return dateFormat.format(d);
	    	        							}
	    	        							else{
	    	        								return ("$"+Double.toString(value));
	    	        								}
	    	        						}
	    	        					});
	    	        				}
    	        					break;
    	        				}
    	        		}
    	        	break;
    	        case 4:{
    	        	
    	        		}
    	        	break;
    	        }
    	        

    	Log.i("button","btn"+selected_item);*/
    	}
    };
}