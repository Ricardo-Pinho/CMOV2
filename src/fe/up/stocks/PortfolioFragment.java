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



import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewDataInterface;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;

import fe.up.stocks.R.color;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;



public class PortfolioFragment extends Fragment {

	private final SimpleDateFormat dateFormat = new SimpleDateFormat("d/M");
	private ProgressDialog pd;
	private LinearLayout mLayout;
	private ImageButton button1;
	private Button currentAddStock;
	private boolean addingPortfolio=false, addingStock=false, addKeyboard=false;
	private LinearLayout linl, LLcurrentaddStock;
	private EditText AddPortfolio;
	private int Counter=0, Stockcounter=0;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    	
        View mainView = inflater
                .inflate(R.layout.manage_portfolios, container, false);
        
        mainView.setEnabled(false);
        setRetainInstance(true);
		//getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		AsyncTask<View, Void, View> task = new AsyncTask<View, Void, View>() {
			int response=1;
			@Override
			protected void onPreExecute() {
				pd = new ProgressDialog(getActivity());
				pd.setTitle("Getting Portfolios");
				pd.setMessage("Please wait.");
				pd.setCancelable(false);
				pd.setIndeterminate(true);
				pd.show();
				
			}
				
			@Override
			protected View doInBackground(View... params) {
				
				for(int i=0; i<MainActivity.usr.portfolios.size();i++)
				{
					Portfolio  port = MainActivity.usr.portfolios.get(i);
					for(int j=0; j<port.stocks.size();j++)
			        {
						if(port.stocks.get(j).unitPrice==0.01)
						{
						HttpURLConnection con = null;
						String payload = "Error";
						double val=0;
						try {
							  
						      // Build RESTful query (GET)
						      URL url = new URL("http://finance.yahoo.com/d/quotes?f=sl1d1t1v&s="+port.stocks.get(j).subName);
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
						      while((payload = reader.readLine())!=null)
						      {

						    		  //Log.d("payload", payload);
						    		  String [] split = payload.split(",");
						    		  val=Double.parseDouble(split[1]);
						    		  MainActivity.usr.portfolios.get(i).stocks.get(j).unitPrice=val;
						    		  
						      }
						     
						      reader.close();
							
						  } catch (IOException e) {
							  Log.d("error", e.getMessage());
						} finally {
						  if (con != null)
						    con.disconnect();
							}
						}
			        }
				}
				MainActivity.usr.Save();
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
							for(int i=0; i<MainActivity.usr.portfolios.size();i++)
							{
								Counter=i;
								Portfolio  port = MainActivity.usr.portfolios.get(i);
								LinearLayout LL = new LinearLayout(getActivity());
								LL.setOrientation(LinearLayout.VERTICAL);
								//LL.setPadding(16,16,16,16);
								LL.setBackgroundResource(R.color.grey);
								LL.setGravity(Gravity.CENTER);
								LayoutParams LLParams = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
								LL.setLayoutParams(LLParams);
								
								
								LinearLayout LL1 = new LinearLayout(getActivity());
								LL1.setOrientation(LinearLayout.VERTICAL);
								LL1.setBackgroundResource(R.color.blue_light);
								LayoutParams LLParams1 = new LayoutParams(LayoutParams.MATCH_PARENT,1);
								LL1.setLayoutParams(LLParams1);
								
								LinearLayout LLR1 = new LinearLayout(getActivity());
								LLR1.setOrientation(LinearLayout.VERTICAL);
								//LL.setPadding(16,16,16,16);
								LLR1.setBackgroundResource(R.color.blue_dark);
								LLR1.setGravity(Gravity.CENTER);
								LayoutParams LLParamsR1 = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
								LLR1.setLayoutParams(LLParamsR1);
							    
								LLR1.addView(createNewTitleTextView(MainActivity.usr.portfolios.get(i).Name));
								
								LinearLayout LL3 = new LinearLayout(getActivity());
								LL3.setOrientation(LinearLayout.HORIZONTAL);
								//LL.setPadding(16,16,16,16);
								LL3.setGravity(Gravity.CENTER);
								LayoutParams LLParams3 = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
								LL3.setLayoutParams(LLParams3);
								
								LayoutParams LLParams4 = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
								LLParams4.setMargins(20, 0, 20, 0);
								
								ImageButton btn1 = new ImageButton(getActivity());
						        btn1.setImageResource(R.drawable.ic_action_edit);
						        btn1.setOnClickListener(buttonClickListener);
						        btn1.setBackgroundResource(R.drawable.button_customize);
						        btn1.setLayoutParams(LLParams4);
						        btn1.setTag(1);
						        btn1.setId(i);
						        btn1.setOnClickListener(new OnClickListener() {
							       	 
										@Override
										public void onClick(View v) {
											View p = (View)v.getParent();
											View parent = (View)p.getParent();
											AlertDialog.Builder editalert = new AlertDialog.Builder(getActivity());

											editalert.setTitle("Set Portfolio Name");


											final EditText input = new EditText(getActivity());
											input.setText(MainActivity.usr.portfolios.get(Counter).Name);
											LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
											        400,
											        LinearLayout.LayoutParams.WRAP_CONTENT);
											input.setLayoutParams(lp);
											editalert.setView(input);

											editalert.setPositiveButton("ok", new DialogInterface.OnClickListener() {
											    public void onClick(DialogInterface dialog, int whichButton) {
											    	MainActivity.usr.portfolios.get(Counter).Name = input.getText().toString();
											    	MainActivity.usr.Save();
											    	dialog.cancel();
											    	/*if(!addKeyboard)
											    	{
											    		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
											    		imm.toggleSoftInput(InputMethodManager.RESULT_SHOWN, 0);
											    		addKeyboard=true;
											    	}*/
											    	
											    	getActivity().finish();
									                Intent intent = new Intent(getActivity(), MainMenu.class);
													startActivity(intent);
													getActivity().overridePendingTransition  (R.anim.fade_in, R.anim.fade_out);
											    	//getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new PortfolioFragment()).commit();
											

											    }
											});
											// create alert dialog
											AlertDialog alertDialog = editalert.create();
							 
											// show it
											alertDialog.show();
											input.requestFocus();
											/*if(addKeyboard)
											{
												InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
												imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
												addKeyboard=false;
											}*/

										}
							        });
						        LL3.addView(btn1);
						        
						        ImageButton btn2 = new ImageButton(getActivity());
						        btn2.setImageResource(R.drawable.ic_action_remove);
						        btn2.setOnClickListener(buttonClickListener);
						        btn2.setBackgroundResource(R.drawable.button_customize);
						        btn2.setLayoutParams(LLParams4);
						        btn2.setTag(3);
						        btn2.setId(i);
						        btn2.setOnClickListener(new OnClickListener() {
							       	 
										@Override
										public void onClick(View v) {
											View p = (View)v.getParent();
											View parent = (View)p.getParent();
											AlertDialog.Builder editalert = new AlertDialog.Builder(getActivity());
											editalert.setTitle("Are you sure?");

											editalert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
											    public void onClick(DialogInterface dialog, int whichButton) {
											    	MainActivity.usr.portfolios.remove(Counter);
											    	MainActivity.usr.Save();
											    	MainActivity.inPortfolio=true;
											    	//getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new PortfolioFragment()).commit();
											    	dialog.cancel();	
											    	getActivity().finish();
									                Intent intent = new Intent(getActivity(), MainMenu.class);
													startActivity(intent);
													getActivity().overridePendingTransition  (R.anim.fade_in, R.anim.fade_out);
											    }
											});
											
											editalert.setNegativeButton("No", new DialogInterface.OnClickListener() {
											    public void onClick(DialogInterface dialog, int whichButton) {
											    	dialog.cancel();
											    }
											});
											// create alert dialog
											AlertDialog alertDialog = editalert.create();
							 
											// show it
											alertDialog.show();

										}
							        });
						        LL3.addView(btn2);
						        
						        LLR1.addView(LL3);
						        
						        LL.addView(LLR1);
								
								
								TableLayout table = new TableLayout(getActivity());
								TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
						        table.setLayoutParams(tableParams);
						        //table.setGravity(Gravity.CENTER);
						        // create a new TableRow
						        TableRow row = new TableRow(getActivity());
						        TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
						        rowParams.setMargins(40, 20, 40, 20);
						        row.setLayoutParams(rowParams);
						        //row.setGravity(Gravity.CENTER);
						 

						        if(port.stocks.size()>0)
						        {
							        // create a new TextView
							        TextView t = new TextView(getActivity());
							        // set the text to "text xx"       
							        t.setText("Name");
							        t.setTextSize(25);
							        t.setTextColor(Color.WHITE);
							        
							     // create a new TextView
							        TextView t2 = new TextView(getActivity());
							        // set the text to "text xx"       
							        t2.setText("Share Number");
							        t2.setTextSize(25);
							        t2.setTextColor(Color.WHITE);
							        
							     // create a new TextView
							        TextView t3 = new TextView(getActivity());
							        // set the text to "text xx"       
							        t3.setText("Price/Share");
							        t3.setTextSize(25);
							        t3.setTextColor(Color.WHITE);
							        
							     // create a new TextView
							        TextView t4 = new TextView(getActivity());
							        // set the text to "text xx"       
							        t4.setText("Options");
							        t4.setTextSize(25);
							        t4.setTextColor(Color.WHITE);
							        
							        // add the TextView and the CheckBox to the new TableRow
							        row.addView(t);
							        row.addView(t2);
							        row.addView(t3);
							        row.addView(t4);
							        // add the TableRow to the TableLayout
							        table.addView(row,new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
						        }
						        
						        for(int j=0;j<port.stocks.size();j++)
						        {
						        	Stockcounter = j;
						        	TableRow stockrow = new TableRow(getActivity());
							        TableRow.LayoutParams stockrowParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
							        stockrowParams.setMargins(40, 20, 40, 20);
							        stockrow.setLayoutParams(rowParams);
							        //stockrow.setGravity(Gravity.CENTER);
							 

								        // create a new TextView
								        TextView t = new TextView(getActivity());
								        // set the text to "text xx"       
								        t.setText(port.stocks.get(j).Name);
								        t.setTextSize(25);
								        t.setTextColor(Color.WHITE);
								        
								     // create a new TextView
								        TextView t2 = new TextView(getActivity());
								        // set the text to "text xx"       
								        t2.setText(Integer.toString(port.stocks.get(j).NoStocks));
								        t2.setTextSize(25);
								        t2.setTextColor(Color.WHITE);
								        
								     // create a new TextView
								        TextView t3 = new TextView(getActivity());
								        // set the text to "text xx"       
								        t3.setText("$"+Double.toString(port.stocks.get(j).unitPrice));
								        t3.setTextSize(25);
								        t3.setTextColor(Color.WHITE);
								        
								        // add the TextView and the CheckBox to the new TableRow
								        stockrow.addView(t);
								        stockrow.addView(t2);
								        stockrow.addView(t3);
								        
								        LinearLayout l4 = new LinearLayout(getActivity());
										l4.setOrientation(LinearLayout.HORIZONTAL);
										//LL.setPadding(16,16,16,16);
										//l4.setGravity(Gravity.CENTER);
										LayoutParams LLParamsl4 = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
										l4.setLayoutParams(LLParamsl4);
										
										LayoutParams LLParamsl5 = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
										LLParamsl5.setMargins(10, 0, 0, 0);
										
										LayoutParams LLParamsl6 = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
										LLParamsl6.setMargins(0, 0, 10, 0);
										
										ImageButton edit = new ImageButton(getActivity());
								        edit.setImageResource(R.drawable.ic_action_edit);
								        edit.setOnClickListener(buttonClickListener);
								        edit.setBackgroundResource(R.drawable.button_customize3);
								        edit.setLayoutParams(LLParamsl6);
								        edit.setTag(1);
								        edit.setId(j);
								        edit.setOnClickListener(new OnClickListener() {
									       	 
												@Override
												public void onClick(View v) {
													View p = (View)v.getParent();
													View parent = (View)p.getParent();
													AlertDialog.Builder editalert = new AlertDialog.Builder(getActivity());

													editalert.setTitle("Edit Stock");


													final EditText input = new EditText(getActivity());
													input.setText(MainActivity.usr.portfolios.get(Counter).Name);
													LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
													        400,
													        LinearLayout.LayoutParams.WRAP_CONTENT);
													input.setLayoutParams(lp);
													editalert.setView(input);

													editalert.setPositiveButton("ok", new DialogInterface.OnClickListener() {
													    public void onClick(DialogInterface dialog, int whichButton) {
													    	MainActivity.usr.portfolios.get(Counter).Name = input.getText().toString();
													    	MainActivity.usr.Save();
													    	dialog.cancel();
													    	/*if(!addKeyboard)
													    	{
													    		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
													    		imm.toggleSoftInput(InputMethodManager.RESULT_SHOWN, 0);
													    		addKeyboard=true;
													    	}*/
													    	
													    	getActivity().finish();
											                Intent intent = new Intent(getActivity(), MainMenu.class);
															startActivity(intent);
															getActivity().overridePendingTransition  (R.anim.fade_in, R.anim.fade_out);
													    	//getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new PortfolioFragment()).commit();
													

													    }
													});
													// create alert dialog
													AlertDialog alertDialog = editalert.create();
									 
													// show it
													alertDialog.show();
													input.requestFocus();
													/*if(addKeyboard)
													{
														InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
														imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
														addKeyboard=false;
													}*/

												}
									        });
								        l4.addView(edit);
								        
								        ImageButton remove = new ImageButton(getActivity());
								        remove.setImageResource(R.drawable.ic_action_remove);
								        remove.setOnClickListener(buttonClickListener);
								        remove.setBackgroundResource(R.drawable.button_customize3);
								        remove.setLayoutParams(LLParamsl5);
								        remove.setTag(3);
								        remove.setId(j);
								        remove.setOnClickListener(new OnClickListener() {
									       	 
												@Override
												public void onClick(View v) {
													View p = (View)v.getParent();
													View parent = (View)p.getParent();
													AlertDialog.Builder editalert = new AlertDialog.Builder(getActivity());
													editalert.setTitle("Are you sure?");

													editalert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
													    public void onClick(DialogInterface dialog, int whichButton) {
													    	MainActivity.usr.portfolios.get(Counter).stocks.remove(Stockcounter);
													    	MainActivity.usr.Save();
													    	//MainActivity.inPortfolio=true;
													    	//getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new PortfolioFragment()).commit();
													    	dialog.cancel();
													    	getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new PortfolioFragment()).commit();
													    	/*getActivity().finish();
											                Intent intent = new Intent(getActivity(), MainMenu.class);
															startActivity(intent);
															getActivity().overridePendingTransition  (R.anim.fade_in, R.anim.fade_out);*/
													    }
													});
													
													editalert.setNegativeButton("No", new DialogInterface.OnClickListener() {
													    public void onClick(DialogInterface dialog, int whichButton) {
													    	dialog.cancel();
													    }
													});
													// create alert dialog
													AlertDialog alertDialog = editalert.create();
									 
													// show it
													alertDialog.show();

												}
									        });
								        //l4.addView(remove);
								        
								     // create a new TextView
								        TextView t4 = new TextView(getActivity());
								        // set the text to "text xx"       
								        t4.setText(port.stocks.get(j).Name);
								        t4.setTextSize(25);
								        t4.setTextColor(Color.WHITE);
								        
								        stockrow.addView(remove);
								        
								        // add the TableRow to the TableLayout
								        table.addView(stockrow,new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
						        }

						        
						        LL.addView(table);
						        
						        final LinearLayout LLaddStock = new LinearLayout(getActivity());
								LLaddStock.setOrientation(LinearLayout.VERTICAL);
								//LL.setPadding(16,16,16,16);
								//LLaddStock.setBackgroundResource(R.color.grey);
								LLaddStock.setGravity(Gravity.CENTER);
								LayoutParams LLParamsaddStock = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
								LLaddStock.setLayoutParams(LLParamsaddStock);
								
								LL.addView(LLaddStock);
								
								LayoutParams LLParams2 = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
								LLParams2.setMargins(20, 20, 20, 20);
								final Button addingButton = new Button(getActivity());
								addingButton.setText("Add Stock");
								addingButton.setTextSize(30);
								addingButton.setTextColor(Color.WHITE);
								addingButton.setGravity(Gravity.CENTER);
								addingButton.setOnClickListener(buttonClickListener);
								addingButton.setBackgroundResource(R.drawable.button_customize3);
								addingButton.setLayoutParams(LLParams2);
								addingButton.setTag(1);
								addingButton.setPadding(8,8,8,8);
								addingButton.setOnClickListener(new OnClickListener() {
							       	 
										@Override
										public void onClick(View v) {
											if(!addingStock)
											{
												addingButton.setText("Close");
												addingStock=true;
												LinearLayout LL1 = new LinearLayout(getActivity());
												LL1.setOrientation(LinearLayout.VERTICAL);
												LL1.setBackgroundResource(R.color.blue_light);
												LayoutParams LLParams1 = new LayoutParams(LayoutParams.MATCH_PARENT,1);
												LL1.setLayoutParams(LLParams1);
												LLaddStock.addView(LL1);
												

												final EditText AddStockName = new EditText(getActivity());
												AddStockName.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
												AddStockName.setFocusableInTouchMode(true);
												AddStockName.requestFocus();
												AddStockName.setGravity(Gravity.CENTER);
												AddStockName.setTextColor(Color.WHITE);
												AddStockName.setTextSize(30);
												AddStockName.setLayoutParams(new LayoutParams(
											            LayoutParams.WRAP_CONTENT,
											            LayoutParams.WRAP_CONTENT));
												AddStockName.setWidth(400);
												
												
												final EditText AddStockAbbrev = new EditText(getActivity());
												AddStockAbbrev.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
												AddStockAbbrev.setFocusableInTouchMode(true);
												AddStockAbbrev.requestFocus();
												AddStockAbbrev.setGravity(Gravity.CENTER);
												AddStockAbbrev.setTextColor(Color.WHITE);
												AddStockAbbrev.setTextSize(30);
												AddStockAbbrev.setLayoutParams(new LayoutParams(
											            LayoutParams.WRAP_CONTENT,
											            LayoutParams.WRAP_CONTENT));
												AddStockAbbrev.setWidth(400);
												
												final EditText AddStockNum = new EditText(getActivity());
												AddStockNum.setInputType(InputType.TYPE_CLASS_NUMBER);
												AddStockNum.setFocusableInTouchMode(true);
												AddStockNum.requestFocus();
												AddStockNum.setGravity(Gravity.CENTER);
												AddStockNum.setTextColor(Color.WHITE);
												AddStockNum.setTextSize(30);
												AddStockNum.setLayoutParams(new LayoutParams(
											            LayoutParams.WRAP_CONTENT,
											            LayoutParams.WRAP_CONTENT));
												AddStockNum.setWidth(400);
												
												//LinearLayout linl= (LinearLayout) parent.findViewById(R.id.portfolios);
											    //linl.addView(toAdd);
												/*if(!addKeyboard)
												{
													InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
													imm.toggleSoftInput(InputMethodManager.RESULT_SHOWN, 0);
													addKeyboard=true;
												}*/
												LLaddStock.addView(createNewTextView("Name"));
												LLaddStock.addView(AddStockName);
												
												LLaddStock.addView(createNewTextView("Abbreviation"));
												LLaddStock.addView(AddStockAbbrev);
												
												LLaddStock.addView(createNewTextView("Number of Stocks"));
												LLaddStock.addView(AddStockNum);
												
												
												LayoutParams LLParams2 = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
												LLParams2.setMargins(20, 20, 20, 20);
												
												final Button addingStockButton = new Button(getActivity());
												addingStockButton.setText("Add");
												addingStockButton.setTextSize(30);
												addingStockButton.setTextColor(Color.WHITE);
												addingStockButton.setGravity(Gravity.CENTER);
												addingStockButton.setOnClickListener(buttonClickListener);
												addingStockButton.setBackgroundResource(R.drawable.button_customize4);
												//addingStockButton.setBackgroundColor(getResources().getColor(R.color.blue_dark));
												addingStockButton.setLayoutParams(LLParams2);
												addingStockButton.setTag(1);
												addingStockButton.setPadding(16,16,16,16);
												addingStockButton.setOnClickListener(new OnClickListener() {
											       	 
														@Override
														public void onClick(View v) {
															Calendar date = Calendar.getInstance();
															Stock s = new Stock(Integer.parseInt(AddStockNum.getText().toString()),date,AddStockName.getText().toString(),AddStockAbbrev.getText().toString());
															MainActivity.usr.portfolios.get(Counter).stocks.add(s);
															MainActivity.usr.Save();
															LLaddStock.removeAllViews();
															addingStock=false;
															
										                	addingButton.setText("Add Stock");
															getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new PortfolioFragment()).commit();		
														}
											        });
													LLaddStock.addView(addingStockButton);
												
												LLcurrentaddStock=LLaddStock;
												currentAddStock=addingButton;
												
												
											}
											else
											{
												if(addingButton.getText().equals("Add Stock"))
												{
													LLcurrentaddStock.removeAllViews();
													currentAddStock.setText("Add Stock");
													addingButton.setText("Close");
													addingStock=true;
													LinearLayout LL1 = new LinearLayout(getActivity());
													LL1.setOrientation(LinearLayout.VERTICAL);
													LL1.setBackgroundResource(R.color.blue_light);
													LayoutParams LLParams1 = new LayoutParams(LayoutParams.MATCH_PARENT,1);
													LL1.setLayoutParams(LLParams1);
													LLaddStock.addView(LL1);
													

													final EditText AddStockName = new EditText(getActivity());
													AddStockName.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
													AddStockName.setFocusableInTouchMode(true);
													AddStockName.requestFocus();
													AddStockName.setGravity(Gravity.CENTER);
													AddStockName.setTextColor(Color.WHITE);
													AddStockName.setTextSize(30);
													AddStockName.setLayoutParams(new LayoutParams(
												            LayoutParams.WRAP_CONTENT,
												            LayoutParams.WRAP_CONTENT));
													AddStockName.setWidth(400);
													
													
													final EditText AddStockAbbrev = new EditText(getActivity());
													AddStockAbbrev.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
													AddStockAbbrev.setFocusableInTouchMode(true);
													AddStockAbbrev.requestFocus();
													AddStockAbbrev.setGravity(Gravity.CENTER);
													AddStockAbbrev.setTextColor(Color.WHITE);
													AddStockAbbrev.setTextSize(30);
													AddStockAbbrev.setLayoutParams(new LayoutParams(
												            LayoutParams.WRAP_CONTENT,
												            LayoutParams.WRAP_CONTENT));
													AddStockAbbrev.setWidth(400);
													
													final EditText AddStockNum = new EditText(getActivity());
													AddStockNum.setInputType(InputType.TYPE_CLASS_NUMBER);
													AddStockNum.setFocusableInTouchMode(true);
													AddStockNum.requestFocus();
													AddStockNum.setGravity(Gravity.CENTER);
													AddStockNum.setTextColor(Color.WHITE);
													AddStockNum.setTextSize(30);
													AddStockNum.setLayoutParams(new LayoutParams(
												            LayoutParams.WRAP_CONTENT,
												            LayoutParams.WRAP_CONTENT));
													AddStockNum.setWidth(400);
													
													//LinearLayout linl= (LinearLayout) parent.findViewById(R.id.portfolios);
												    //linl.addView(toAdd);
													/*if(!addKeyboard)
													{
														InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
														imm.toggleSoftInput(InputMethodManager.RESULT_SHOWN, 0);
														addKeyboard=true;
													}*/
													LLaddStock.addView(createNewTextView("Name"));
													LLaddStock.addView(AddStockName);
													
													LLaddStock.addView(createNewTextView("Abbreviation"));
													LLaddStock.addView(AddStockAbbrev);
													
													LLaddStock.addView(createNewTextView("Number of Stocks"));
													LLaddStock.addView(AddStockNum);
													
													
													LayoutParams LLParams2 = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
													LLParams2.setMargins(20, 20, 20, 20);
													
													final Button addingStockButton = new Button(getActivity());
													addingStockButton.setText("Add");
													addingStockButton.setTextSize(30);
													addingStockButton.setTextColor(Color.WHITE);
													addingStockButton.setGravity(Gravity.CENTER);
													addingStockButton.setOnClickListener(buttonClickListener);
													addingStockButton.setBackgroundResource(R.drawable.button_customize4);
													//addingStockButton.setBackgroundColor(getResources().getColor(R.color.blue_dark));
													addingStockButton.setLayoutParams(LLParams2);
													addingStockButton.setTag(1);
													addingStockButton.setPadding(16,16,16,16);
													addingStockButton.setOnClickListener(new OnClickListener() {
												       	 
															@Override
															public void onClick(View v) {
																Calendar date = Calendar.getInstance();
																Stock s = new Stock(Integer.parseInt(AddStockNum.getText().toString()),date,AddStockName.getText().toString(),AddStockAbbrev.getText().toString());
																MainActivity.usr.portfolios.get(Counter).stocks.add(s);
																MainActivity.usr.Save();
																LLaddStock.removeAllViews();
																addingStock=false;
																
											                	addingButton.setText("Add Stock");
																getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new PortfolioFragment()).commit();		
															}
												        });
														LLaddStock.addView(addingStockButton);
													
													LLcurrentaddStock=LLaddStock;
													currentAddStock=addingButton;
												}
												else{
													LLaddStock.removeAllViews();
													addingStock=false;
													/*if(addKeyboard)
													{
														InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
														imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
														addKeyboard=false;
													}*/
								                	addingButton.setText("Add Stock");
												}
											}
											
											
										}
							        });
									LL.addView(addingButton);

									LL.addView(LL1);
								
								linl.addView(LL);
								
							}
							
							
							/*LinearLayout LL = new LinearLayout(getActivity());
							LL.setOrientation(LinearLayout.VERTICAL);
							LayoutParams LLParams = new LayoutParams(LayoutParams.MATCH_PARENT,350);
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
							
							LinearLayout linl= (LinearLayout) mainView.findViewById(R.id.graphs);
						    linl.addView(LL);*/
							
						}
							break;

					}
				}
			}
				
		};
		linl= (LinearLayout) mainView.findViewById(R.id.llholder);
		View[] params = new View[2];
		params[0] = mainView;
			task.execute(params);
		mLayout = (LinearLayout) mainView.findViewById(R.id.portfolios);
		mLayout.setGravity(Gravity.CENTER);
		addListenerOnButton(mainView);
        	return mainView;
    }
    
    public void addListenerOnButton(View mainView) {
		 
		final Context context = getActivity();
 
		button1 = (ImageButton) mainView.findViewById(R.id.add);
 
		button1.setOnClickListener(new OnClickListener() {
 
			@Override
			public void onClick(View v) {
				if(!addingPortfolio)
				{
					View p = (View)v.getParent();
					View parent = (View)p.getParent();

					button1.setImageResource(R.drawable.ic_action_collapse);
					addingPortfolio=true;
					AddPortfolio = new EditText(getActivity());
					AddPortfolio.setFocusableInTouchMode(true);
					AddPortfolio.requestFocus();
					AddPortfolio.setGravity(Gravity.CENTER);
					AddPortfolio.setTextColor(Color.WHITE);
					AddPortfolio.setTextSize(30);
					AddPortfolio.setLayoutParams(new LayoutParams(
				            LayoutParams.WRAP_CONTENT,
				            LayoutParams.WRAP_CONTENT));
					AddPortfolio.setWidth(400);
					/*if(!addKeyboard)
					{
						InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.toggleSoftInput(InputMethodManager.RESULT_SHOWN, 0);
						addKeyboard=true;
					}*/
					
					//LinearLayout linl= (LinearLayout) parent.findViewById(R.id.portfolios);
				    //linl.addView(toAdd);
					mLayout.addView(createNewTextView("Name"));
					mLayout.addView(AddPortfolio);
					
					LayoutParams LLParams = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
					LLParams.setMargins(40, 40, 40, 40);
					
					Button addingButton = new Button(getActivity());
					addingButton.setText("Add Portfolio");
					addingButton.setTextSize(30);
					addingButton.setTextColor(Color.WHITE);
					addingButton.setOnClickListener(buttonClickListener);
					addingButton.setBackgroundResource(R.drawable.button_customize3);
					addingButton.setLayoutParams(LLParams);
					addingButton.setTag(1);
					addingButton.setPadding(8,8,8,8);
					addingButton.setOnClickListener(new OnClickListener() {
				       	 
							@Override
							public void onClick(View v) {
								/*LinearLayout LL = new LinearLayout(getActivity());
								LL.setOrientation(LinearLayout.VERTICAL);
								//LL.setPadding(16,16,16,16);
								LL.setBackgroundResource(R.color.grey);
								LayoutParams LLParams = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
								LL.setLayoutParams(LLParams);
								
								
								LinearLayout LL1 = new LinearLayout(getActivity());
								LL1.setOrientation(LinearLayout.VERTICAL);
								LL1.setBackgroundResource(R.color.blue_light);
								LayoutParams LLParams1 = new LayoutParams(LayoutParams.MATCH_PARENT,1);
								LL1.setLayoutParams(LLParams1);*/
							    
								MainActivity.usr.portfolios.add(new Portfolio(AddPortfolio.getText().toString()));
								MainActivity.usr.Save();
								MainActivity.inPortfolio=true;
								/*LL.addView(createNewTitleTextView(AddPortfolio.getText().toString()));
								LL.addView(LL1);
								linl.addView(LL);
								button1.setImageResource(R.drawable.ic_action_new);
								mLayout.removeAllViews();
								addingPortfolio=false;*/
								/*if(!addKeyboard)
								{
									InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
									imm.toggleSoftInput(InputMethodManager.RESULT_SHOWN, 0);
									addKeyboard=true;
								}*/
				                getActivity().finish();
				                Intent intent = new Intent(context, MainMenu.class);
								startActivity(intent);
								getActivity().overridePendingTransition  (R.anim.fade_in, R.anim.fade_out);
				                
				              
				                

							}
				        });
			        mLayout.addView(addingButton);
				}
				else
				{
					mLayout.removeAllViews();
					addingPortfolio=false;
					/*if(addKeyboard)
					{
						InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
						addKeyboard=false;
					}*/
	                button1.setImageResource(R.drawable.ic_action_new);
				}
			}
 
		});
 
	}
    
    private TextView createNewTextView(String text) {
        final LayoutParams lparams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        final TextView textView = new TextView(getActivity());
        textView.setLayoutParams(lparams);
        textView.setText(text);
        textView.setTextColor(Color.WHITE);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(30);
        return textView;
    }
    
    private TextView createNewTitleTextView(String text) {
        LayoutParams lparams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        TextView textView = new TextView(getActivity());
        textView.setLayoutParams(lparams);
        textView.setText(text);
        textView.setTextColor(Color.WHITE);
        textView.setGravity(Gravity.CENTER);
        textView.setAllCaps(true);
        textView.setTextSize(40);
        //textView.setBackgroundResource(R.color.blue_dark);
        //textView.setBackgroundColor(color.blue_dark);
        return textView;
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