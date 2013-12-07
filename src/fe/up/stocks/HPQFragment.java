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
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


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
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class HPQFragment extends Fragment {

	private final SimpleDateFormat dateFormat = new SimpleDateFormat("d/M");
	private ProgressDialog pd;
	private boolean firstRunning=true;
	private stockGraph hpq;
	private realtimeGraph hpq2;
	private int graphPos=0, graphPos2=0;
	private final Handler mHandler = new Handler();
	private Runnable mTimer2;
	private GraphViewSeries graphView2;
	private String time="0:00";
	private int iter=0;
	private double totaldiff=0;
	
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
				stockGraph sg = new stockGraph();
				sg.stockAbrev = "HPQ";
				sg.stockName= "Hewlett-Packard Company";
				sg.id=7;
				sg.endDate = Calendar.getInstance();
				sg.beginDate = Calendar.getInstance();
				sg.beginDate.add(Calendar.DATE, -31);
				boolean exists=false;
				for(int i=0; i< MainActivity.sgraph.size();i++)
				{
					if(sg.stockName.equals(MainActivity.sgraph.get(i).stockName))
					{
						if(sg.beginDate.DAY_OF_MONTH==MainActivity.sgraph.get(i).beginDate.DAY_OF_MONTH 
								&& sg.beginDate.MONTH == MainActivity.sgraph.get(i).beginDate.MONTH
								&& sg.beginDate.YEAR == MainActivity.sgraph.get(i).beginDate.YEAR
								&& sg.endDate.DAY_OF_MONTH==MainActivity.sgraph.get(i).endDate.DAY_OF_MONTH 
								&& sg.endDate.MONTH == MainActivity.sgraph.get(i).endDate.MONTH
								&& sg.endDate.YEAR == MainActivity.sgraph.get(i).endDate.YEAR)
						{
							exists=true;
							hpq = MainActivity.sgraph.get(i);
							//hpq2 = MainActivity.sgraph.get(i);
							graphPos=i;
							break;
						}
					}
				}
				if(!exists)
					{
						MainActivity.sgraph.add(sg);
						graphPos = MainActivity.sgraph.size()-1;
						hpq = sg;
						//hpq2 = sg;
					}
				  //Log.d(Integer.toString(i),Integer.toString(MainActivity.sgraph.get(i).points.size()));
				  if(hpq.points.size()<1)
				  {
					HttpURLConnection con = null;
				    String payload = "Error";
				    int numDays=0;
				    double val=0;
				    try {
				    	  boolean first=true;
				    	  //Log.d("date", df.format(MainActivity.sgraph.get(i).beginDate.getTime()));
				    	  int bmonth = hpq.beginDate.get(Calendar.MONTH);
				    	  int bday = hpq.beginDate.get(Calendar.DAY_OF_MONTH);
				    	  int byear = hpq.beginDate.get(Calendar.YEAR);
				    	  
				    	  int emonth = hpq.endDate.get(Calendar.MONTH);
				    	  int eday = hpq.endDate.get(Calendar.DAY_OF_MONTH);
				    	  int eyear = hpq.endDate.get(Calendar.YEAR);
				          // Build RESTful query (GET)
				          URL url = new URL("http://ichart.finance.yahoo.com/table.txt?a="+bmonth+"&b="+bday+"&c="+byear+"&d="+emonth+"&e="+eday+"&f="+eyear+"&g=d&s="+hpq.stockAbrev);
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
				          sg = hpq;
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
				        		  Pair<Double,Long> p = new Pair<Double,Long>(val,dat.getTime());
				        		  //Log.d("pair", Double.parseDouble(split[4])+" and "+dat.getTime());
				        		  sg.points.add(p);
				        	  }
				          }
				          sg.NoPoints=numDays;
				          MainActivity.sgraph.set(graphPos, sg);
				          hpq=sg;
				          reader.close();
				      } catch (IOException e) {
				    } finally {
				      if (con != null)
				        con.disconnect();
				    }

				    MainActivity.sgraph.get(graphPos).NoPoints=numDays;
				    hpq.NoPoints=numDays;
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

						        //Log.d(MainActivity.sgraph.get(i).stockName, Integer.toString(MainActivity.sgraph.get(i).NoPoints));
								if(hpq.NoPoints<=1)							
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
											, hpq.stockName
									);
								((LineGraphView) graphView).setDrawBackground(true);
								

								graphView.addSeries(exampleSeries);
								LinearLayout LL = new LinearLayout(getActivity());
								LL.setOrientation(LinearLayout.VERTICAL);
								LayoutParams LLParams = new LayoutParams(LayoutParams.MATCH_PARENT,350);
								LLParams.gravity=Gravity.CENTER;
								LL.setLayoutParams(LLParams);
								LL.setWeightSum(2);
								
								LinearLayout LL3 = new LinearLayout(getActivity());
								LL3.setOrientation(LinearLayout.VERTICAL);
								LayoutParams LLParams3 = new LayoutParams(LayoutParams.MATCH_PARENT,250);
								LLParams3.weight=1;
								LL3.setId(1);
								LL3.setLayoutParams(LLParams3);

								MainActivity.graphs.add(graphView);
								LL3.addView(MainActivity.graphs.get(MainActivity.graphs.size()-1));
								LL.addView(LL3);

								LinearLayout linl= (LinearLayout) mainView.findViewById(R.id.graphs);
							    linl.addView(LL);

							}
								else
							{
							GraphViewData[] data = new GraphViewData[5];
							int k=4;
							int position=5;
							hpq.pos=position;
							for (int j=0; j<position; j++) {
								long x=hpq.points.get(j).second;
								double y=hpq.points.get(j).first;
								data[k] = new GraphViewData(x, y); // next day
								k--;
							}
							totaldiff = (hpq.maxY-hpq.minY)/3;
							GraphViewSeriesStyle seriesStyle1 = new GraphViewSeriesStyle();
							seriesStyle1.setValueDependentColor(new ValueDependentColor() {
								@Override
								public int get(GraphViewDataInterface data) {
									if(data.getY()<= (hpq.minY+totaldiff*1))
										return Color.rgb(0, 50, 150);
									else if(data.getY()<= (hpq.minY+totaldiff*2))
										return Color.rgb(50, 100, 200);
									else
										return Color.rgb(100, 150, 255);
									
									
									//return Color.rgb((int)(150-((data.getY()/10)*100)), (int)(150+((data.getY()/10)*150)), (int)(150-((data.getY()/10)*150)));
								}
							});
							GraphViewSeries exampleSeries = new GraphViewSeries("stock price variance", seriesStyle1, data);
							hpq.gvs=exampleSeries;
							GraphView graphView;
							graphView = new LineGraphView(
										getActivity()
										, ""
								);
							((LineGraphView) graphView).setDrawBackground(true);
							graphView.addSeries(exampleSeries); // data
							graphView.setManualYAxisBounds(hpq.maxY, hpq.minY);
							/*
							 * date as label formatter
							 */
							graphView.setCustomLabelFormatter(new CustomLabelFormatter() {
								@Override
								public String formatLabel(double value, boolean isValueX) {
									if (isValueX) {
										Date d = new Date((long) value);
										return dateFormat.format(d);
									}
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
							graphView.setId(graphPos);
							
							
							
							
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
							LL3.setId(graphPos);
							LL3.setLayoutParams(LLParams3);

							MainActivity.graphs.add(graphView);
							hpq.graphPos=MainActivity.graphs.size()-1;
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
							btnm1.setId(graphPos);
					        
							btnm1.setOnClickListener(new OnClickListener() {
					       	 
								@Override
								public void onClick(View v) {
									View p = (View)v.getParent();
									View parent = (View)p.getParent();
										Random rand = new Random();
										long now = new Date().getTime();
				    					stockGraph sg = MainActivity.sgraph.get(graphPos);
				    					//Log.d("graph Pos",v.getId()+"-"+sg.id+"-"+MainActivity.sgraph.get(0).stockAbrev+"-"+MainActivity.sgraph.get(graphPos).stockAbrev);

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
				        					MainActivity.sgraph.get(graphPos).pos=position;
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
				        					totaldiff = (hpq.maxY-hpq.minY)/3;
											GraphViewSeriesStyle seriesStyle1 = new GraphViewSeriesStyle();
											seriesStyle1.setValueDependentColor(new ValueDependentColor() {
												@Override
												public int get(GraphViewDataInterface data) {
													if(data.getY()<= (hpq.minY+totaldiff*1))
														return Color.rgb(0, 50, 150);
													else if(data.getY()<= (hpq.minY+totaldiff*2))
														return Color.rgb(50, 100, 200);
													else
														return Color.rgb(100, 150, 255);
													
													
													//return Color.rgb((int)(150-((data.getY()/10)*100)), (int)(150+((data.getY()/10)*150)), (int)(150-((data.getY()/10)*150)));
												}
											});
											GraphViewSeries exampleSeries = new GraphViewSeries("stock price variance", seriesStyle1, data);
				        					sg.gvs = exampleSeries;
				        					MainActivity.sgraph.set(graphPos, sg);
				        					MainActivity.graphs.get(sg.graphPos).removeAllSeries();
				        					MainActivity.graphs.get(sg.graphPos).addSeries(exampleSeries); // data
				        					
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
				        				
				        				//Log.i("test", "got here2");

								}
					        });
										
					        LL2.addView(btnm1);
							
							
							
							ImageButton btn = new ImageButton(getActivity());
							btn.setImageResource(R.drawable.ic_action_previous_item);
					        btn.setOnClickListener(buttonClickListener);
					        btn.setBackgroundResource(R.drawable.button_customize);
					        btn.setLayoutParams(LLParams5);
					        btn.setTag(1);
					        btn.setId(graphPos);
					        
					        btn.setOnClickListener(new OnClickListener() {
					       	 
								@Override
								public void onClick(View v) {
									View p = (View)v.getParent();
									View parent = (View)p.getParent();
										Random rand = new Random();
										long now = new Date().getTime();
				    					stockGraph sg = MainActivity.sgraph.get(graphPos);

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
				        					MainActivity.sgraph.get(graphPos).pos=position;
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
				        					totaldiff = (hpq.maxY-hpq.minY)/3;
											GraphViewSeriesStyle seriesStyle1 = new GraphViewSeriesStyle();
											seriesStyle1.setValueDependentColor(new ValueDependentColor() {
												@Override
												public int get(GraphViewDataInterface data) {
													if(data.getY()<= (hpq.minY+totaldiff*1))
														return Color.rgb(0, 50, 150);
													else if(data.getY()<= (hpq.minY+totaldiff*2))
														return Color.rgb(50, 100, 200);
													else
														return Color.rgb(100, 150, 255);
													
													
													//return Color.rgb((int)(150-((data.getY()/10)*100)), (int)(150+((data.getY()/10)*150)), (int)(150-((data.getY()/10)*150)));
												}
											});
											GraphViewSeries exampleSeries = new GraphViewSeries("stock price variance", seriesStyle1, data);
				        					sg.gvs = exampleSeries;
				        					MainActivity.sgraph.set(graphPos, sg);
				        					MainActivity.graphs.get(sg.graphPos).removeAllSeries();
				        					MainActivity.graphs.get(sg.graphPos).addSeries(exampleSeries); // data
				        					
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
				        				
				        				//Log.i("test", "got here2");
				    				}
					        });
										
					        LL2.addView(btn);
					        
					        ImageButton btn1 = new ImageButton(getActivity());
					        btn1.setImageResource(R.drawable.line_graph);
					        btn1.setOnClickListener(buttonClickListener);
					        btn1.setBackgroundResource(R.drawable.button_customize3);
					        btn1.setLayoutParams(LLParams4);
					        btn1.setTag(2);
					        btn1.setId(graphPos);
					        btn1.setOnClickListener(new OnClickListener() {
						       	 
									@Override
									public void onClick(View v) {
										View p = (View)v.getParent();
										View parent = (View)p.getParent();
					    					stockGraph sg = MainActivity.sgraph.get(graphPos);
					        					GraphView graphView;
					        					graphView = new LineGraphView(
					        								getActivity()
					        								, ""
					        						);
					        					/*
					        					 * date as label formatter
					        					 */
					        					graphView.addSeries(sg.gvs); // data

					        					graphView.getGraphViewStyle().setNumHorizontalLabels(5);
					        					graphView.setManualYAxisBounds(MainActivity.sgraph.get(graphPos).maxY, MainActivity.sgraph.get(graphPos).minY);
					        					graphView.getGraphViewStyle().setVerticalLabelsWidth(120);
					        					graphView.getGraphViewStyle().setTextSize(20);
					        					graphView.setCustomLabelFormatter(new CustomLabelFormatter() {
					        						@Override
					        						public String formatLabel(double value, boolean isValueX) {
					        							if (isValueX) {
					        								Date d = new Date((long) value);
					        								return dateFormat.format(d);
					        							}
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
					        					MainActivity.graphs.set(hpq.graphPos,graphView);
					        					ln.addView(MainActivity.graphs.get(hpq.graphPos));

					        					MainActivity.graphs.get(hpq.graphPos).startAnimation(animation);
					        				}
					        				//Log.i("test", "got here2");

						        });
					        LL2.addView(btn1);
					        
					        ImageButton btn2 = new ImageButton(getActivity());
					        btn2.setImageResource(R.drawable.bar_graph);
					        btn2.setOnClickListener(buttonClickListener);
					        btn2.setBackgroundResource(R.drawable.button_customize3);
					        btn2.setLayoutParams(LLParams4);
					        btn2.setTag(3);
					        btn2.setId(graphPos);
					        btn2.setOnClickListener(new OnClickListener() {
						       	 
									@Override
									public void onClick(View v) {
										View p = (View)v.getParent();
										View parent = (View)p.getParent();
					    					stockGraph sg = MainActivity.sgraph.get(graphPos);
					        					GraphView graphView;
					        					graphView = new BarGraphView(
					        								getActivity()
					        								, ""
					        						);
					        					graphView.addSeries(sg.gvs); // data

					        					graphView.setManualYAxisBounds(MainActivity.sgraph.get(graphPos).maxY, MainActivity.sgraph.get(graphPos).minY);
					        					/*
					        					 * date as label formatter
					        					 */

					        					graphView.getGraphViewStyle().setNumHorizontalLabels(5);
					        					graphView.setManualYAxisBounds(MainActivity.sgraph.get(graphPos).maxY, MainActivity.sgraph.get(graphPos).minY);
					        					graphView.getGraphViewStyle().setVerticalLabelsWidth(120);
					        					graphView.getGraphViewStyle().setTextSize(20);
					        					graphView.setCustomLabelFormatter(new CustomLabelFormatter() {
					        						@Override
					        						public String formatLabel(double value, boolean isValueX) {
					        							if (isValueX) {
					        								Date d = new Date((long) value);
					        								return dateFormat.format(d);
					        							}
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
					        					MainActivity.graphs.set(hpq.graphPos,graphView);
					        					ln.addView(MainActivity.graphs.get(hpq.graphPos));

					        					MainActivity.graphs.get(hpq.graphPos).startAnimation(animation);
					        				}
						        });
					        LL2.addView(btn2);
					        
					        ImageButton btn3 = new ImageButton(getActivity());
					        btn3.setImageResource(R.drawable.ic_action_next_item);
					        btn3.setOnClickListener(buttonClickListener);
					        btn3.setBackgroundResource(R.drawable.button_customize);
					        btn3.setLayoutParams(LLParams6);
					        btn3.setTag(4);
					        btn3.setId(graphPos);
					        btn3.setOnClickListener(new OnClickListener() {
						       	 
								@Override
								public void onClick(View v) {
									View p = (View)v.getParent();
									View parent = (View)p.getParent();
				    					stockGraph sg = MainActivity.sgraph.get(graphPos);

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
				        					MainActivity.sgraph.get(graphPos).pos=position;
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
				        					totaldiff = (hpq.maxY-hpq.minY)/3;
											GraphViewSeriesStyle seriesStyle1 = new GraphViewSeriesStyle();
											seriesStyle1.setValueDependentColor(new ValueDependentColor() {
												@Override
												public int get(GraphViewDataInterface data) {
													if(data.getY()<= (hpq.minY+totaldiff*1))
														return Color.rgb(0, 50, 150);
													else if(data.getY()<= (hpq.minY+totaldiff*2))
														return Color.rgb(50, 100, 200);
													else
														return Color.rgb(100, 150, 255);
													
													
													//return Color.rgb((int)(150-((data.getY()/10)*100)), (int)(150+((data.getY()/10)*150)), (int)(150-((data.getY()/10)*150)));
												}
											});
											GraphViewSeries exampleSeries = new GraphViewSeries("stock price variance", seriesStyle1, data);
				        					sg.gvs = exampleSeries;
				        					MainActivity.sgraph.set(graphPos, sg);
				        					
				        					MainActivity.graphs.get(sg.graphPos).removeAllSeries();
				        					MainActivity.graphs.get(sg.graphPos).addSeries(exampleSeries); // data
				        					
				        					
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
				        				
				        				//Log.i("test", "got here2");
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
							btnm2.setId(graphPos);
					        
							btnm2.setOnClickListener(new OnClickListener() {
					       	 
								@Override
								public void onClick(View v) {
									View p = (View)v.getParent();
									View parent = (View)p.getParent();
										
				    					stockGraph sg = MainActivity.sgraph.get(graphPos);
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
				        					MainActivity.sgraph.get(graphPos).pos=position;
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
				        					totaldiff = (hpq.maxY-hpq.minY)/3;
											GraphViewSeriesStyle seriesStyle1 = new GraphViewSeriesStyle();
											seriesStyle1.setValueDependentColor(new ValueDependentColor() {
												@Override
												public int get(GraphViewDataInterface data) {
													if(data.getY()<= (hpq.minY+totaldiff*1))
														return Color.rgb(0, 50, 150);
													else if(data.getY()<= (hpq.minY+totaldiff*2))
														return Color.rgb(50, 100, 200);
													else
														return Color.rgb(100, 150, 255);
													
													
													//return Color.rgb((int)(150-((data.getY()/10)*100)), (int)(150+((data.getY()/10)*150)), (int)(150-((data.getY()/10)*150)));
												}
											});
											GraphViewSeries exampleSeries = new GraphViewSeries("stock price variance", seriesStyle1, data);
				        					sg.gvs = exampleSeries;
				        					MainActivity.sgraph.set(graphPos, sg);
				        					
				        					MainActivity.graphs.get(sg.graphPos).removeAllSeries();
				        					MainActivity.graphs.get(sg.graphPos).addSeries(exampleSeries); // data
				        					
				        					
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
				        				
				        		}
				        				//Log.i("test", "got here2");
				    				
					        });
										
					        LL2.addView(btnm2);
						        
						        
					        TextView title = new TextView(getActivity());
					        title.setText(hpq.stockName);
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
	
								LinearLayout linl= (LinearLayout) mainView.findViewById(R.id.graphs);
							    linl.addView(LL);
							    
							    
							    realtimeGraph sg = new realtimeGraph();
							    sg.stockAbrev = "HPQ";
								sg.stockName= "Hewlett-Packard Company";
								sg.id=7;
							    boolean exists=false;
								for(int i=0; i< MainActivity.rgraph.size();i++)
								{
									if(hpq.stockName.equals(MainActivity.rgraph.get(i).stockName))
									{
										{
											exists=true;
											hpq2 = MainActivity.rgraph.get(i);
											//hpq2 = MainActivity.sgraph.get(i);
											graphPos2=i;
											graphView2 = hpq2.gvs;
											iter=MainActivity.rgraph.get(i).points.size();
											Log.d("points", MainActivity.rgraph.get(i).points.get(MainActivity.rgraph.get(i).points.size()-1).second+" "+MainActivity.rgraph.get(i).points.get(MainActivity.rgraph.get(i).points.size()-1).first
													);
											break;
										}
									}
								}
								if(!exists)
									{
										MainActivity.rgraph.add(sg);
										graphPos2 = MainActivity.rgraph.size()-1;
										hpq2 = sg;
										/*for (int j=0; j<31; j++) {
											long x=0;
											double y=hpq.points.get(hpq.points.size()-1).first;
											hpq2.points.add(new Pair<Double,Long>(y,x));
										}*/
										data = new GraphViewData[0];
										//position=5;
										hpq2.pos=0;
										/*for (int j=0; j<31; j++) {
											long x=hpq2.points.get(j).second;
											double y=hpq2.points.get(j).first;
											data[j] = new GraphViewData(x, y); // next day
											//k--;
										}*/
									    graphView2 = new GraphViewSeries(data);
									    graphView2.getStyle().color = Color.RED;
									    hpq2.maxY=(double) hpq.points.get(hpq.points.size()-1).first;
										hpq2.minY=(double) 0;
									}

								// graph with custom labels and drawBackground
									graphView = new LineGraphView(
											getActivity()
											, ""/*hpq2.stockName+" Realtime Graph"*/
									);
									//((LineGraphView) graphView).setDrawBackground(true);
									
									
								graphView.addSeries(graphView2); // data
								hpq2.gvs = graphView2;
								MainActivity.rgraph.set(graphPos2, hpq2);
								graphView.setViewPort(hpq2.pos, 4);
								graphView.setScrollable(true);
								graphView.getGraphViewStyle().setVerticalLabelsWidth(120);
								graphView.getGraphViewStyle().setNumHorizontalLabels(5);
								graphView.getGraphViewStyle().setTextSize(20);
								
								graphView.setManualYAxisBounds(hpq2.maxY, hpq2.minY);
								
								graphView.setCustomLabelFormatter(new CustomLabelFormatter() {
									@Override
									public String formatLabel(double value, boolean isValueX) {
										if (isValueX) {
											if(value<0 || value>=hpq2.times.size())
												{
													Log.d(Double.toString(value),Integer.toString(hpq2.times.size()));
													return "0:00";
												}
											else
												return hpq2.times.get((int) value);
										}
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
								
								LinearLayout LLR = new LinearLayout(getActivity());
								LLR.setOrientation(LinearLayout.VERTICAL);
								LayoutParams LLParamsR = new LayoutParams(LayoutParams.MATCH_PARENT,400);
								LLParamsR.gravity=Gravity.CENTER;
								LLR.setLayoutParams(LLParamsR);
								LLR.setWeightSum(2);
								
								LinearLayout LLR1 = new LinearLayout(getActivity());
								LLR1.setOrientation(LinearLayout.VERTICAL);
								LayoutParams LLParamsR1 = new LayoutParams(LayoutParams.MATCH_PARENT,250);
								LLParamsR1.weight=1;
								LLR1.setId(graphPos2);
								LLR1.setLayoutParams(LLParamsR1);

								MainActivity.graphs.add(graphView);
								hpq2.graphPos=MainActivity.graphs.size()-1;
								LLR1.addView(MainActivity.graphs.get(MainActivity.graphs.size()-1));

								
								
								
								LinearLayout LLR2 = new LinearLayout(getActivity());
								LLR2.setOrientation(LinearLayout.HORIZONTAL);
								LayoutParams LLParamsR2 = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
								LLParamsR2.weight=1;
								LLParamsR2.gravity=Gravity.CENTER;
								LLR2.setLayoutParams(LLParamsR2);
								
								//MainActivity.graphs.add(graphView);
								//hpq.graphPos=MainActivity.graphs.size()-1;
								//LL3.addView(MainActivity.graphs.get(MainActivity.graphs.size()-1));
								LayoutParams LLParamsR4 = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
								LLParamsR4.setMargins(20, 0, 20, 0);
								
								LayoutParams LLParamsR6 = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
								LLParamsR6.setMargins(80, 0, 0, 0);
								
								LayoutParams LLParamsR5 = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
								LLParamsR5.setMargins(0, 0, 80, 0);
								
								LayoutParams LLParamsR7 = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
								LLParamsR7.setMargins(20, 0, 0, 0);
								
								LayoutParams LLParamsR8 = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
								LLParamsR8.setMargins(0, 0, 20, 0);
								
								
								ImageButton btnrm1 = new ImageButton(getActivity());
								btnrm1.setImageResource(R.drawable.ic_action_rewind);
								btnrm1.setOnClickListener(buttonClickListener);
								btnrm1.setBackgroundResource(R.drawable.button_customize);
								btnrm1.setLayoutParams(LLParams8);
								btnrm1.setTag(6);
								btnrm1.setId(graphPos2);
						        
								btnrm1.setOnClickListener(new OnClickListener() {
						       	 
									@Override
									public void onClick(View v) {


										View p = (View)v.getParent();
										View parent = (View)p.getParent();
					    					realtimeGraph sg = MainActivity.rgraph.get(graphPos2);
					    					MainActivity.graphs.get(sg.graphPos).removeAllSeries();
					    					MainActivity.graphs.get(sg.graphPos).addSeries(graphView2);
					    					if(sg.pos-4<=0)
					    					{
					        					if(iter>4)
					    						{
					        						MainActivity.graphs.get(sg.graphPos).setViewPort(0, 4);
					        						MainActivity.rgraph.get(graphPos2).pos=0;
					    						}
					    					}
					        				else
					        				{
					        					MainActivity.graphs.get(sg.graphPos).setViewPort(sg.pos-4, 4);
					        					MainActivity.rgraph.get(graphPos2).pos=sg.pos-4;
					        				}
					        				
					        				
					    					/*int position= sg.pos-5;
					        					int end = sg.pos;
					        					if(!sg.turnLeft)
				        						{
				        							position=position-5;
				        							end=sg.pos-5;
				        							sg.turnLeft=true;
				        						}
					        					
					        					if(position<0)
					        						position=0;
					        					
					        					int diff = end-position;
					        					MainActivity.rgraph.get(graphPos2).pos=position;
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
					        					GraphViewSeries exampleSeries = new GraphViewSeries(data);
					        					sg.gvs = exampleSeries;
					        					MainActivity.rgraph.set(graphPos2, sg);
					        					MainActivity.graphs.get(hpq2.graphPos).removeAllSeries();
					        					MainActivity.graphs.get(hpq2.graphPos).addSeries(exampleSeries); // data*/
					        					
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
					        				}
					        				//Log.i("test", "got here2");
						        });
											
						        LLR2.addView(btnrm1);
								
								ImageButton btnR = new ImageButton(getActivity());
								btnR.setImageResource(R.drawable.ic_action_previous_item);
						        btnR.setOnClickListener(buttonClickListener);
						        btnR.setBackgroundResource(R.drawable.button_customize);
						        btnR.setLayoutParams(LLParamsR5);
						        btnR.setTag(1);
						        btnR.setId(graphPos2);
						        
						        btnR.setOnClickListener(new OnClickListener() {
						       	 
						        	@Override
									public void onClick(View v) {


										View p = (View)v.getParent();
										View parent = (View)p.getParent();
					    					realtimeGraph sg = MainActivity.rgraph.get(graphPos2);
					    					MainActivity.graphs.get(sg.graphPos).removeAllSeries();
					    					MainActivity.graphs.get(sg.graphPos).addSeries(graphView2);
					    					if(sg.pos-1<0)
					    					{
					        					/*if(iter>4)
					    						{
					        						MainActivity.graphs.get(sg.graphPos).setViewPort(0, 4);
					        						MainActivity.rgraph.get(graphPos2).pos=0;
					    						}*/
					    					}
					        				else
					        				{
					        					MainActivity.graphs.get(sg.graphPos).setViewPort(sg.pos-1, 4);
					        					MainActivity.rgraph.get(graphPos2).pos=sg.pos-1;
					        				}
					        				
						        	}

						        });
											
						        LLR2.addView(btnR);
						        
						        ImageButton btnR1 = new ImageButton(getActivity());
						        if(hpq2.isPlay)
						        	Log.d("play", "true");
						        else
						        	Log.d("play", "false");
						        
						        if(hpq2.isPlay)
								 	btnR1.setImageResource(R.drawable.ic_action_play);
							       else
							    	 btnR1.setImageResource(R.drawable.ic_action_pause);
						        btnR1.setOnClickListener(buttonClickListener);
						        btnR1.setBackgroundResource(R.drawable.button_customize);
						        btnR1.setLayoutParams(LLParamsR4);
						        btnR1.setTag(2);
						        btnR1.setId(graphPos2);
						        btnR1.setOnClickListener(new OnClickListener() {
							       	 
										@Override
										public void onClick(View v) {
											if(hpq2.isPlay)
									        	Log.d("play", "true");
									        else
									        	Log.d("play", "false");
											
											 if(!hpq2.isPlay){
												 	mHandler.removeCallbacks(mTimer2);
												 	((ImageButton) v).setImageResource(R.drawable.ic_action_play);
												 	hpq2.isPlay=true;
												 	MainActivity.rgraph.set(graphPos,hpq2);
											       }
											       else{
											    	   hpq2.isPlay=false;
											    	   mHandler.post(mTimer2);
											    	   MainActivity.rgraph.set(graphPos,hpq2);
											    	   ((ImageButton) v).setImageResource(R.drawable.ic_action_pause); 
											       }
										}

							        });
						        LLR2.addView(btnR1);
						        
						        ImageButton btnR2 = new ImageButton(getActivity());
						        btnR2.setImageResource(R.drawable.ic_action_refresh);
						        btnR2.setOnClickListener(buttonClickListener);
						        btnR2.setBackgroundResource(R.drawable.button_customize);
						        btnR2.setLayoutParams(LLParamsR4);
						        btnR2.setTag(3);
						        btnR2.setId(graphPos2);
						        btnR2.setOnClickListener(new OnClickListener() {
							       	 
										@Override
										public void onClick(View v) {
											if(!hpq2.isPlay)
											{
												mHandler.removeCallbacks(mTimer2);
												mHandler.post(mTimer2);
											}
										}
							        });
						        LLR2.addView(btnR2);
						        
						        ImageButton btnR3 = new ImageButton(getActivity());
						        btnR3.setImageResource(R.drawable.ic_action_next_item);
						        btnR3.setOnClickListener(buttonClickListener);
						        btnR3.setBackgroundResource(R.drawable.button_customize);
						        btnR3.setLayoutParams(LLParamsR6);
						        btnR3.setTag(4);
						        btnR3.setId(graphPos2);
						        btnR3.setOnClickListener(new OnClickListener() {
							       	 
									@Override
									public void onClick(View v) {
										View p = (View)v.getParent();
										View parent = (View)p.getParent();	
				    					realtimeGraph sg = MainActivity.rgraph.get(graphPos2);
				    					MainActivity.graphs.get(sg.graphPos).removeAllSeries();
				    					MainActivity.graphs.get(sg.graphPos).addSeries(graphView2);
				    					if(sg.pos+6>iter)
				    					{
				    						/*if(iter>4)
				    						{
				    							MainActivity.graphs.get(sg.graphPos).setViewPort(iter-5, 4);
				    							MainActivity.rgraph.get(graphPos2).pos=iter-5;
				    						}*/
				    					}
				        				else
				        				{
				        					MainActivity.graphs.get(sg.graphPos).setViewPort(sg.pos+1, 4);
				        					MainActivity.rgraph.get(graphPos2).pos=sg.pos+1;
				        				}
									}	

						        });
							        LLR2.addView(btnR3);
							        
							        ImageButton btnrm2 = new ImageButton(getActivity());
									btnrm2.setImageResource(R.drawable.ic_action_fast_forward);
									btnrm2.setOnClickListener(buttonClickListener);
									btnrm2.setBackgroundResource(R.drawable.button_customize);
									btnrm2.setLayoutParams(LLParams7);
									btnrm2.setTag(6);
									btnrm2.setId(graphPos2);
							        
									btnrm2.setOnClickListener(new OnClickListener() {
							       	 
										@Override
										public void onClick(View v) {
											View p = (View)v.getParent();
											View parent = (View)p.getParent();	
					    					realtimeGraph sg = MainActivity.rgraph.get(graphPos2);
					    					MainActivity.graphs.get(sg.graphPos).removeAllSeries();
					    					MainActivity.graphs.get(sg.graphPos).addSeries(graphView2);
					    					if(sg.pos+10>=iter)
					    					{
					    						if(iter>4)
					    						{
					    							MainActivity.graphs.get(sg.graphPos).setViewPort(iter-5, 4);
					    							MainActivity.rgraph.get(graphPos2).pos=iter-5;
					    						}
					    					}
					        				else
					        				{
					        					MainActivity.graphs.get(sg.graphPos).setViewPort(sg.pos+4, 4);
					        					MainActivity.rgraph.get(graphPos2).pos=sg.pos+4;
					        				}
											
						    					/*realtimeGraph sg = MainActivity.rgraph.get(graphPos2);
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
						        					MainActivity.sgraph.get(graphPos).pos=position;
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
						        					GraphViewSeries exampleSeries = new GraphViewSeries(data);
						        					sg.gvs = exampleSeries;
						        					MainActivity.rgraph.set(graphPos2, sg);
						        					
						        					MainActivity.graphs.get(hpq2.graphPos).removeAllSeries();
						        					MainActivity.graphs.get(hpq2.graphPos).addSeries(exampleSeries); // data*/
						        					
						        					
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
						        				}
						        				//Log.i("test", "got here2");

							        });
												
							        LLR2.addView(btnrm2);
								
								
						        TextView valueTV = new TextView(getActivity());
						        valueTV.setText(hpq2.stockName+" Realtime Graph");
						        valueTV.setId(5);
						        valueTV.setGravity(Gravity.CENTER);
						        valueTV.setTextColor(Color.WHITE);
						        valueTV.setTextSize(23f);
						        valueTV.setLayoutParams(new LayoutParams(
						                LayoutParams.FILL_PARENT,
						                LayoutParams.WRAP_CONTENT));    
							    
							    LLR.addView(valueTV);
								LLR.addView(LLR1);
								LLR.addView(LLR2);
								
								linl.addView(LLR);
								

							}
								if(hpq2.isPlay)
									mHandler.removeCallbacks(mTimer2);
								
						        setRetainInstance(false);
								
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
    
	private double getRandom() {
		double high = 3;
		double low = 0.5;
		return Math.random() * (high - low) + low;
	}
    
    @Override
	public void onPause() {
		mHandler.removeCallbacks(mTimer2);
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();

		mTimer2 = new Runnable() {
			@Override
			public void run() {

				AsyncTask<Void, Void, Void> updater = new AsyncTask<Void, Void, Void>() {

					boolean response=false;
					@Override
					protected void onPreExecute() {
					}
						
					@Override
					protected Void doInBackground(Void... arg0) {
						HttpURLConnection con = null;
						String payload = "Error";
						int numDays=0;
						double val=0;
						try {
							  
						      // Build RESTful query (GET)
						      URL url = new URL("http://finance.yahoo.com/d/quotes?f=sl1d1t1v&s="+hpq.stockAbrev);
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
						      realtimeGraph sg = hpq2;
						      while((payload = reader.readLine())!=null)
						      {
						    		  numDays++;
						    		  //Log.d("payload", payload);
						    		  String [] split = payload.split(",");
						    		  time = split[3].substring(1, split[3].length()-3);
						    		  if(split[3].charAt(split[3].length()-3)=='p' && !split[3].substring(1,3).equals("12"))
						    		  {
						    			  int timer = Integer.parseInt(split[3].substring(1,split[3].indexOf(":")));
						    			  timer = timer+12;
						    			  time = Integer.toString(timer)+split[3].substring(split[3].indexOf(":"),split[3].length()-3);
						    		  }
						    		  val=Double.parseDouble(split[1]);
						    		  if(val>hpq2.maxY)
						    			  hpq2.maxY=val;
						    		  if(val<hpq2.minY || hpq2.minY==0)
						    			  hpq2.minY=val;
						    		  //time= Long.parseLong(split[3]);
						    		  Pair<Double,Long> p = new Pair<Double,Long>(val,(long) iter);
						    		  iter++;
						    		  Log.d("pair", val+" and "+time);
						    		  //Log.d("pair", Double.parseDouble(split[4])+" and "+dat.getTime());
						    		  /*if(sg.times.size()==0 || !sg.times.get(sg.times.size()-1).equals(time) )
						    		  {*/
						    		  if(sg.times.size()==0)
						    		  {
						    			  sg.points.add(p);
							    		  sg.times.add(time);
							    		  response=true;
						    		  }
						    		  else if(sg.times.get(sg.times.size()-1).equals(time) )
						    		  {
						    			  time=time.concat("(2)");
							    		  sg.points.add(p);
							    		  sg.times.add(time); 
							    		  response=true;
						    		  }
						    		  else if(sg.times.get(sg.times.size()-1).substring(0,5).equals(time))
						    		  {
						    			  String rest = sg.times.get(sg.times.size()-1).substring(6,sg.times.get(sg.times.size()-1).length()-1);
						    			  Log.d("rest", rest);
						    			  int num = Integer.parseInt(rest);
						    			  num=num+1;
						    			  time=time.concat("("+num+")");
						    			  sg.points.add(p);
							    		  sg.times.add(time);
							    		  response=true;
						    		  }
						    		  else
						    		  {
						    			  sg.points.add(p);
							    		  sg.times.add(time); 
							    		  response=true;
						    		  }
						    		  /*else
						    			  sameNumber=true;*/
						      }
						      /*if(!sameNumber)
						      {*/
						    	sg.NoPoints=numDays;
						      	hpq2=sg;
						      	MainActivity.rgraph.set(graphPos2,hpq2);
						      //}
						      reader.close();
						  } catch (IOException e) {
							  Log.d("error", e.getMessage());
						} finally {
						  if (con != null)
						    con.disconnect();
						}
						return null;
					}
					
					@Override
					protected void onPostExecute(Void result) {
						//Log.d("damn", "ok");
						Log.d("point", Long.toString(hpq2.points.get(hpq2.points.size()-1).second)+"-"+Double.toString(hpq2.points.get(hpq2.points.size()-1).first));
						
							if(response)
							 {
								graphView2.appendData(new GraphViewData(hpq2.points.get(hpq2.points.size()-1).second ,hpq2.points.get(hpq2.points.size()-1).first), true, 31);
								hpq2.gvs=graphView2;
								MainActivity.graphs.get(hpq2.graphPos).setManualYAxisBounds(hpq2.maxY, hpq2.minY);
								hpq2.pos=iter-5;
							}
						 }
						
				};
				
					mHandler.postDelayed(this, 10000);
					updater.execute((Void[])null);
					//graphView2.appendData(new GraphViewData(getRandom() ,getRandom()), true, 10);
			}
		};
		mHandler.postDelayed(mTimer2, 1000);
	}
    
    private View.OnClickListener buttonClickListener = new View.OnClickListener() {

    	@Override
    	public void onClick(View v) {

    	    //check which green ball was clicked
    	        int selected_item = (Integer) v.getTag();
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
