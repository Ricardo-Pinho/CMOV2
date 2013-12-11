package fe.up.stocks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

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
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public abstract class AbstractNavDrawerActivity extends FragmentActivity implements SearchView.OnQueryTextListener {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
   
    private ListView mDrawerList;
    private SearchView mSearchView;
   
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
   
    private NavDrawerActivityConfiguration navConf ;
   
    protected abstract NavDrawerActivityConfiguration getNavDrawerConfiguration();
   
    protected abstract void onNavItemSelected( int id );
   
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        navConf = getNavDrawerConfiguration();
       
        setContentView(navConf.getMainLayout());
       
        mTitle = mDrawerTitle = getTitle();
       
        mDrawerLayout = (DrawerLayout) findViewById(navConf.getDrawerLayoutId());
        mDrawerList = (ListView) findViewById(navConf.getLeftDrawerId());
        mDrawerList.setAdapter(navConf.getBaseAdapter());
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
       
        this.initDrawerShadow();
       
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
       
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                getDrawerIcon(),
                navConf.getDrawerOpenDesc(),
                navConf.getDrawerCloseDesc()
                ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
 
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.searchview_in_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) searchItem.getActionView();
        setupSearchView(searchItem);
 
        return true;
    }
    
    private void setupSearchView(MenuItem searchItem) {
    	 
        if (isAlwaysExpanded()) {
            mSearchView.setIconifiedByDefault(false);
        } else {
            searchItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM
                    | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        }
 
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (searchManager != null) {
            List<SearchableInfo> searchables = searchManager.getSearchablesInGlobalSearch();
 
            SearchableInfo info = searchManager.getSearchableInfo(getComponentName());
            for (SearchableInfo inf : searchables) {
                if (inf.getSuggestAuthority() != null
                        && inf.getSuggestAuthority().startsWith("applications")) {
                    info = inf;
                }
            }
            mSearchView.setSearchableInfo(info);
        }
 
        mSearchView.setOnQueryTextListener(this);
    }
    
    public boolean onQueryTextChange(String newText) {
        return false;
    }
 
    public boolean onQueryTextSubmit(String query) {
    	getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new EmptyFragment()).commit();
    	final String querymatch=query;
    	AsyncTask<View, Void, View> task = new AsyncTask<View, Void, View>() {
    		
        	ProgressDialog pd;
        	ArrayList<stockGraph> querygraphs = new ArrayList<stockGraph>();
        	double totaldiff=0;
        	double minVal=0;
        	int iter=0;
        	SimpleDateFormat dateFormat = new SimpleDateFormat("d/M");
        	SimpleDateFormat dateYearFormat = new SimpleDateFormat("MMM");
			@Override
			protected void onPreExecute() {
				pd = new ProgressDialog(AbstractNavDrawerActivity.this);
				pd.setTitle("Getting Portfolios");
				pd.setMessage("Please wait.");
				pd.setCancelable(false);
				pd.setIndeterminate(true);
				pd.show();
				
			}
				
			@Override
			protected View doInBackground(View... params) {
				for(int i=0; i<MainActivity.sgraph.size(); i++)
				{
					MainActivity.graphs = new ArrayList<GraphView>();
					if(MainActivity.sgraph.get(i).stockName.toUpperCase(Locale.US).contains(querymatch.toUpperCase(Locale.US)) || MainActivity.sgraph.get(i).stockAbrev.contains(querymatch.toUpperCase(Locale.US)))
					{
						querygraphs.add(MainActivity.sgraph.get(i));
					}
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return params[0];
			}
			
			@Override
			protected void onPostExecute(View mainView) {
				if (pd!=null) {
					pd.dismiss();
					if(querygraphs.size()==0)
					{
						LinearLayout LL = new LinearLayout(AbstractNavDrawerActivity.this);
						LL.setOrientation(LinearLayout.VERTICAL);
						LayoutParams LLParams = new LayoutParams(LayoutParams.MATCH_PARENT,400);
						LLParams.gravity=Gravity.CENTER;
						LL.setLayoutParams(LLParams);
						LL.setWeightSum(1);

						TextView title = new TextView(AbstractNavDrawerActivity.this);
				        title.setText("Sorry, No Stock Companies found!");
				        //title.setId(i+1);
				        title.setGravity(Gravity.CENTER);
				        title.setTextColor(Color.WHITE);
				        title.setTextSize(23f);
				        title.setLayoutParams(new LayoutParams(
				                LayoutParams.FILL_PARENT,
				                LayoutParams.WRAP_CONTENT));    
					    
					    LL.addView(title);

						LinearLayout linl= (LinearLayout) findViewById(R.id.graphs);
					    linl.addView(LL);
					}
					for(int i=0; i<querygraphs.size();i++)
					{

						if(querygraphs.get(i).monthPeriod != MainActivity.monthPeriodGraph)
						{}
				        //Log.d(querygraphs.get(i).stockName, Integer.toString(querygraphs.get(i).NoPoints));
						else if(querygraphs.get(i).NoPoints<=1)							
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
									AbstractNavDrawerActivity.this
									, ""
							);
						((LineGraphView) graphView).setDrawBackground(true);
						

						graphView.addSeries(exampleSeries);
						graphView.getGraphViewStyle().setVerticalLabelsColor(Color.WHITE);
						graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.WHITE);
						graphView.getGraphViewStyle().setVerticalLabelsWidth(120);
						graphView.getGraphViewStyle().setTextSize(20);
						LinearLayout LL = new LinearLayout(AbstractNavDrawerActivity.this);
						LL.setOrientation(LinearLayout.VERTICAL);
						LayoutParams LLParams = new LayoutParams(LayoutParams.MATCH_PARENT,400);
						LLParams.gravity=Gravity.CENTER;
						LL.setLayoutParams(LLParams);
						LL.setWeightSum(2);
						
						LinearLayout LL3 = new LinearLayout(AbstractNavDrawerActivity.this);
						LL3.setOrientation(LinearLayout.VERTICAL);
						LayoutParams LLParams3 = new LayoutParams(LayoutParams.MATCH_PARENT,250);
						LLParams3.weight=1;
						LL3.setId(i+1);
						LL3.setLayoutParams(LLParams3);

						MainActivity.graphs.add(graphView);
						LL3.addView(MainActivity.graphs.get(MainActivity.graphs.size()-1));
						TextView title = new TextView(AbstractNavDrawerActivity.this);
				        title.setText(querygraphs.get(i).stockName);
				        //title.setId(i+1);
				        title.setGravity(Gravity.CENTER);
				        title.setTextColor(Color.WHITE);
				        title.setTextSize(23f);
				        title.setLayoutParams(new LayoutParams(
				                LayoutParams.FILL_PARENT,
				                LayoutParams.WRAP_CONTENT));    
					    
					    LL.addView(title);
						LL.addView(LL3);

						LinearLayout linl= (LinearLayout) mainView.findViewById(R.id.graphs);
					    linl.addView(LL);

					}
					else
					{
					//int num = querygraphs.get(i).NoPoints;
					final int currentPos = i;
					GraphViewData[] data = new GraphViewData[5];
					int k=4;
					int position=5;
					querygraphs.get(i).pos=position;
					for (int j=0; j<position; j++) {
						long x=querygraphs.get(i).points.get(j).second;
						double y=querygraphs.get(i).points.get(j).first;
						data[k] = new GraphViewData(x, y); // next day
						k--;
					}
					totaldiff = (querygraphs.get(i).maxY-querygraphs.get(i).minY)/3;
					minVal=querygraphs.get(i).minY;
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
					GraphViewSeries exampleSeries = new GraphViewSeries("stock price variance", seriesStyle1, data);
					stockGraph gs = querygraphs.get(i);
					gs.gvs=exampleSeries;
					querygraphs.set(i, gs);
					
					GraphView graphView;
					graphView = new LineGraphView(
								AbstractNavDrawerActivity.this
								, ""
						);
					((LineGraphView) graphView).setDrawBackground(true);
					graphView.addSeries(exampleSeries); // data
					graphView.setManualYAxisBounds(querygraphs.get(i).maxY, querygraphs.get(i).minY);
					/*
					 * date as label formatter
					 */
					graphView.setCustomLabelFormatter(new CustomLabelFormatter() {
						@Override
						public String formatLabel(double value, boolean isValueX) {
							if (isValueX) {
								if(querygraphs.get(currentPos).monthPeriod)
									return dateFormat.format(querygraphs.get(currentPos).times.get((int) value));
								else
									return dateYearFormat.format(querygraphs.get(currentPos).times.get((int) value));
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
					graphView.getGraphViewStyle().setVerticalLabelsColor(Color.WHITE);
					graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.WHITE);
					graphView.setId(i);
					
					
					
					
					LinearLayout LL = new LinearLayout(AbstractNavDrawerActivity.this);
					LL.setOrientation(LinearLayout.VERTICAL);
					LayoutParams LLParams = new LayoutParams(LayoutParams.MATCH_PARENT,400);
					LLParams.gravity=Gravity.CENTER;
					LL.setLayoutParams(LLParams);
					LL.setWeightSum(2);
					
					LinearLayout LL2 = new LinearLayout(AbstractNavDrawerActivity.this);
					LL2.setOrientation(LinearLayout.HORIZONTAL);
					LayoutParams LLParams2 = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
					LLParams2.weight=1;
					LLParams2.gravity=Gravity.CENTER;
					LL2.setLayoutParams(LLParams2);
					
					LinearLayout LL3 = new LinearLayout(AbstractNavDrawerActivity.this);
					LL3.setOrientation(LinearLayout.VERTICAL);
					LayoutParams LLParams3 = new LayoutParams(LayoutParams.MATCH_PARENT,250);
					LLParams3.weight=1;
					LL3.setId(i);
					LL3.setLayoutParams(LLParams3);

					MainActivity.graphs.add(graphView);
					querygraphs.get(i).graphPos=MainActivity.graphs.size()-1;
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
					
					
					
					ImageButton btnm1 = new ImageButton(AbstractNavDrawerActivity.this);
					btnm1.setImageResource(R.drawable.ic_action_rewind);
					btnm1.setBackgroundResource(R.drawable.button_customize);
					btnm1.setLayoutParams(LLParams8);
					btnm1.setTag(5);
					btnm1.setId(i);
			        
					btnm1.setOnClickListener(new OnClickListener() {
			       	 
						@Override
						public void onClick(View v) {
							View p = (View)v.getParent();
							View parent = (View)p.getParent();
							for(int i=0; i<querygraphs.size(); i++)
		    				{
								Random rand = new Random();
								long now = new Date().getTime();
		    					stockGraph sg = querygraphs.get(i);
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
		        					querygraphs.get(i).pos=position;
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
		        					totaldiff = (querygraphs.get(i).maxY-querygraphs.get(i).minY)/3;
									minVal=querygraphs.get(i).minY;
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
									GraphViewSeries exampleSeries = new GraphViewSeries("stock price variance", seriesStyle1, data);
		        					sg.gvs = exampleSeries;
		        					querygraphs.set(i, sg);
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
		        					break;
		        				}
		        				//Log.i("test", "got here2");
		    				}

						}
			        });
								
			        LL2.addView(btnm1);
					
					
					
					ImageButton btn = new ImageButton(AbstractNavDrawerActivity.this);
					btn.setImageResource(R.drawable.ic_action_previous_item);
			        btn.setBackgroundResource(R.drawable.button_customize);
			        btn.setLayoutParams(LLParams5);
			        btn.setTag(1);
			        btn.setId(i);
			        
			        btn.setOnClickListener(new OnClickListener() {
			       	 
						@Override
						public void onClick(View v) {
							View p = (View)v.getParent();
							View parent = (View)p.getParent();
							for(int i=0; i<querygraphs.size(); i++)
		    				{
								Random rand = new Random();
								long now = new Date().getTime();
		    					stockGraph sg = querygraphs.get(i);
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
		        					querygraphs.get(i).pos=position;
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
		        					totaldiff = (querygraphs.get(i).maxY-querygraphs.get(i).minY)/3;
									minVal=querygraphs.get(i).minY;
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
									GraphViewSeries exampleSeries = new GraphViewSeries("stock price variance", seriesStyle1, data);
		        					sg.gvs = exampleSeries;
		        					querygraphs.set(i, sg);
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
		        					break;
		        				}
		        				//Log.i("test", "got here2");
		    				}

						}
			        });
								
			        LL2.addView(btn);
			        
			        ImageButton btn1 = new ImageButton(AbstractNavDrawerActivity.this);
			        btn1.setImageResource(R.drawable.line_graph);
			        btn1.setBackgroundResource(R.drawable.button_customize3);
			        btn1.setLayoutParams(LLParams4);
			        btn1.setTag(2);
			        btn1.setId(i);
			        btn1.setOnClickListener(new OnClickListener() {
				       	 
							@Override
							public void onClick(View v) {
								View p = (View)v.getParent();
								View parent = (View)p.getParent();
								if(MainActivity.betterCharts)
								{
									for(int i=0; i<querygraphs.size(); i++)
				    				{
				    					stockGraph sg = querygraphs.get(i);
				        				if(v.getId()==i)
				        				{
											LinearGraph lin = new LinearGraph();
											lin.sgraph = querygraphs.get(i);
									    	Intent lineIntent = lin.getIntent(AbstractNavDrawerActivity.this);
									        startActivity(lineIntent);
				        					break;
				        				}
				    				}

								}
								else
								{
									for(int i=0; i<querygraphs.size(); i++)
				    				{
				    					stockGraph sg = querygraphs.get(i);
				        				if(v.getId()==i)
				        				{
				        					GraphView graphView;
				        					graphView = new LineGraphView(
				        								AbstractNavDrawerActivity.this
				        								, ""
				        						);
				        					/*
				        					 * date as label formatter
				        					 */
				        					graphView.addSeries(sg.gvs); // data

				        					graphView.getGraphViewStyle().setNumHorizontalLabels(5);
				        					graphView.getGraphViewStyle().setVerticalLabelsColor(Color.WHITE);
											graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.WHITE);
				        					graphView.setManualYAxisBounds(querygraphs.get(i).maxY, querygraphs.get(i).minY);
				        					graphView.getGraphViewStyle().setVerticalLabelsWidth(120);
				        					graphView.getGraphViewStyle().setTextSize(20);
				        					graphView.setCustomLabelFormatter(new CustomLabelFormatter() {
												@Override
												public String formatLabel(double value, boolean isValueX) {
													if (isValueX) {
														if(querygraphs.get(currentPos).monthPeriod)
															return dateFormat.format(querygraphs.get(currentPos).times.get((int) value));
														else
															return dateYearFormat.format(querygraphs.get(currentPos).times.get((int) value));
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
				        					Animation animation = AnimationUtils.loadAnimation(AbstractNavDrawerActivity.this, R.anim.right_slide_graph);
				        					animation.setStartOffset(0);
				        					ln.removeAllViews();
				        					MainActivity.graphs.set(sg.graphPos,graphView);
				        					ln.addView(MainActivity.graphs.get(sg.graphPos));

				        					MainActivity.graphs.get(sg.graphPos).startAnimation(animation);
				        					break;
				        				}
				        				//Log.i("test", "got here2");
				    				}

								}
							}
				        });
			        LL2.addView(btn1);
			        
			        ImageButton btn2 = new ImageButton(AbstractNavDrawerActivity.this);
			        btn2.setImageResource(R.drawable.bar_graph);
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
									for(int i=0; i<querygraphs.size(); i++)
				    				{
				    					stockGraph sg = querygraphs.get(i);
				        				if(v.getId()==i)
				        				{
											BarGraph bar = new BarGraph();
											bar.sgraph = querygraphs.get(i);
									    	Intent lineIntent = bar.getIntent(AbstractNavDrawerActivity.this);
									        startActivity(lineIntent);
				        					break;
				        				}
				    				}
								}
								else
								{
									for(int i=0; i<querygraphs.size(); i++)
				    				{
				    					stockGraph sg = querygraphs.get(i);
				        				if(v.getId()==i)
				        				{
				        					GraphView graphView;
				        					graphView = new BarGraphView(
				        								AbstractNavDrawerActivity.this
				        								, ""
				        						);
				        					graphView.addSeries(sg.gvs); // data

				        					graphView.setManualYAxisBounds(querygraphs.get(i).maxY, querygraphs.get(i).minY);
				        					/*
				        					 * date as label formatter
				        					 */

				        					graphView.getGraphViewStyle().setNumHorizontalLabels(5);
				        					graphView.getGraphViewStyle().setVerticalLabelsWidth(120);
				        					graphView.getGraphViewStyle().setTextSize(20);
				        					graphView.getGraphViewStyle().setVerticalLabelsColor(Color.WHITE);
											graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.WHITE);
											graphView.setCustomLabelFormatter(new CustomLabelFormatter() {
												@Override
												public String formatLabel(double value, boolean isValueX) {
													if (isValueX) {
														if(querygraphs.get(currentPos).monthPeriod)
															return dateFormat.format(querygraphs.get(currentPos).times.get((int) value));
														else
															return dateYearFormat.format(querygraphs.get(currentPos).times.get((int) value));
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
											Log.d("Id", Integer.toString(v.getId()));
				        					LinearLayout ln = (LinearLayout) parent.findViewById(v.getId());
				        					Animation animation = AnimationUtils.loadAnimation(AbstractNavDrawerActivity.this, R.anim.right_slide_graph);
				        					animation.setStartOffset(0);
				        					ln.removeAllViews();
				        					MainActivity.graphs.set(sg.graphPos,graphView);
				        					ln.addView(MainActivity.graphs.get(sg.graphPos));

				        					MainActivity.graphs.get(sg.graphPos).startAnimation(animation);
				        					break;
				        				}
				    				}

								}
							}
				        });
			        LL2.addView(btn2);
			        
			        ImageButton btn3 = new ImageButton(AbstractNavDrawerActivity.this);
			        btn3.setImageResource(R.drawable.ic_action_next_item);
			        btn3.setBackgroundResource(R.drawable.button_customize);
			        btn3.setLayoutParams(LLParams6);
			        btn3.setTag(4);
			        btn3.setId(i);
			        btn3.setOnClickListener(new OnClickListener() {
				       	 
						@Override
						public void onClick(View v) {
							View p = (View)v.getParent();
							View parent = (View)p.getParent();
							for(int i=0; i<querygraphs.size(); i++)
		    				{
								
		    					stockGraph sg = querygraphs.get(i);
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
		        					querygraphs.get(i).pos=position;
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
		        					totaldiff = (querygraphs.get(i).maxY-querygraphs.get(i).minY)/3;
									minVal=querygraphs.get(i).minY;
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
									GraphViewSeries exampleSeries = new GraphViewSeries("stock price variance", seriesStyle1, data);
		        					sg.gvs = exampleSeries;
		        					querygraphs.set(i, sg);
		        					
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
		        					break;
		        				}
		        				//Log.i("test", "got here2");
		    				}

						}
			        });
			        LL2.addView(btn3);
			        //LL2.addView(graphView);
			        
			        ImageButton btnm2 = new ImageButton(AbstractNavDrawerActivity.this);
					btnm2.setImageResource(R.drawable.ic_action_fast_forward);
					btnm2.setBackgroundResource(R.drawable.button_customize);
					btnm2.setLayoutParams(LLParams7);
					btnm2.setTag(6);
					btnm2.setId(i);
			        
					btnm2.setOnClickListener(new OnClickListener() {
			       	 
						@Override
						public void onClick(View v) {
							View p = (View)v.getParent();
							View parent = (View)p.getParent();
							for(int i=0; i<querygraphs.size(); i++)
		    				{
								
		    					stockGraph sg = querygraphs.get(i);
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
		        					querygraphs.get(i).pos=position;
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
		        					totaldiff = (querygraphs.get(i).maxY-querygraphs.get(i).minY)/3;
									minVal=querygraphs.get(i).minY;
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
									GraphViewSeries exampleSeries = new GraphViewSeries("stock price variance", seriesStyle1, data);
		        					sg.gvs = exampleSeries;
		        					querygraphs.set(i, sg);
		        					
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
		        					break;
		        				}
		        				//Log.i("test", "got here2");
		    				}

						}
			        });
								
			        LL2.addView(btnm2);
			        
			        

			        TextView title = new TextView(AbstractNavDrawerActivity.this);
			        title.setText(querygraphs.get(i).stockName);
			        title.setGravity(Gravity.CENTER);
			        title.setTextColor(Color.WHITE);
			        title.setTextSize(23f);
			        title.setLayoutParams(new LayoutParams(
			                LayoutParams.FILL_PARENT,
			                LayoutParams.WRAP_CONTENT));    
				    
				    LL.addView(title);
					LL.addView(LL3);
					LL.addView(LL2);

					LinearLayout linl= (LinearLayout) findViewById(R.id.graphs);
				    linl.addView(LL);


					}

						
					} 
				}
			}
				
		};
		View[] params = new View[2];
			task.execute(params);
        return false;
    }
 
    public boolean onClose() {
        return false;
    }
 
    protected boolean isAlwaysExpanded() {
        return false;
    }
   
    protected void initDrawerShadow() {
        mDrawerLayout.setDrawerShadow(navConf.getDrawerShadow(), GravityCompat.START);
    }
   
    protected int getDrawerIcon() {
        return R.drawable.ic_drawer;
    }
   
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
   
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if ( navConf.getActionMenuItemsToHideWhenDrawerOpen() != null ) {
            boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
            for( int iItem : navConf.getActionMenuItemsToHideWhenDrawerOpen()) {
                menu.findItem(iItem).setVisible(!drawerOpen);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }
   
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        else {
            return false;
        }
    }
   
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ( keyCode == KeyEvent.KEYCODE_MENU ) {
            if ( this.mDrawerLayout.isDrawerOpen(this.mDrawerList)) {
                this.mDrawerLayout.closeDrawer(this.mDrawerList);
            }
            else {
                this.mDrawerLayout.openDrawer(this.mDrawerList);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
   
    protected DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    protected ActionBarDrawerToggle getDrawerToggle() {
        return mDrawerToggle;
    }
   
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }
   
    public void selectItem(int position) {
        NavDrawerItem selectedItem = navConf.getNavItems()[position];
       
        this.onNavItemSelected(selectedItem.getId());
        mDrawerList.setItemChecked(position, true);
       
        if ( selectedItem.updateActionBarTitle()) {
            setTitle(selectedItem.getLabel());
        }
       
        if ( this.mDrawerLayout.isDrawerOpen(this.mDrawerList)) {
            mDrawerLayout.closeDrawer(mDrawerList);
        }
    }
   
    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }
}
