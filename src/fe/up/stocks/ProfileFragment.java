package fe.up.stocks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
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
import android.content.res.ColorStateList;
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



public class ProfileFragment extends Fragment {


	private ProgressDialog pd;
	private ImageButton editButton;
	private Button changePassword;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    	
        View mainView = inflater
                .inflate(R.layout.profile, container, false);

        TextView tv = (TextView) mainView.findViewById(R.id.ProfileName);
        tv.setText(MainActivity.usr.Name);
        TextView tv2 = (TextView) mainView.findViewById(R.id.ProfileNickname);
        tv2.setText(MainActivity.usr.Nickname);
        tv2.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
		addListenerOnButton1(mainView);
		addListenerOnButton2(mainView);
        	return mainView;
    }
    
    public void addListenerOnButton1(View mainView) {
		 
		final Context context = getActivity();
 
		editButton = (ImageButton) mainView.findViewById(R.id.editNameButton);
 
		editButton.setOnClickListener(new OnClickListener() {
 
			@Override
			public void onClick(View v) {
				AlertDialog.Builder editalert = new AlertDialog.Builder(getActivity());

				editalert.setTitle("Change your Name");


				final EditText input = new EditText(getActivity());
				input.setText(MainActivity.usr.Name);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				        400,
				        LinearLayout.LayoutParams.WRAP_CONTENT);
				input.setLayoutParams(lp);
				editalert.setView(input);

				editalert.setPositiveButton("ok", new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int whichButton) {
				    	MainActivity.usr.Name = input.getText().toString();
				    	MainActivity.usr.Save();
				    	dialog.cancel();
				    	/*if(!addKeyboard)
				    	{
				    		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				    		imm.toggleSoftInput(InputMethodManager.RESULT_SHOWN, 0);
				    		addKeyboard=true;
				    	}*/
				    	MainActivity.inProfile=true;
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
			}
 
		});
 
	}
    
    public void addListenerOnButton2(View mainView) {
    	final Context context = getActivity();
 
		changePassword = (Button) mainView.findViewById(R.id.ProfileChange_Password);
 
		changePassword.setOnClickListener(new OnClickListener() {
 
			@Override
			public void onClick(View v) {
				final View p = v;
				AlertDialog.Builder editalert = new AlertDialog.Builder(getActivity());
				LinearLayout.LayoutParams tvl = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
				        LinearLayout.LayoutParams.WRAP_CONTENT);
				tvl.setMargins(0, 10, 0, 10);
				editalert.setTitle("Change Password");
				LinearLayout layout = new LinearLayout(getActivity());
				layout.setOrientation(LinearLayout.VERTICAL);
				layout.setGravity(Gravity.CENTER);
				
				final TextView tv = new TextView(getActivity());
				tv.setText("Current Password");
				tv.setLayoutParams(tvl);
				//tv.setTextColor(getResources().getColor(R.color.blue_middle));
				tv.setTextSize(20);
				tv.setGravity(Gravity.CENTER);
				layout.addView(tv);
				
				final EditText input = new EditText(getActivity());
				input.setTextColor(getResources().getColor(R.color.blue_middle));
				input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
				input.setGravity(Gravity.CENTER);
				//input.setInputType(InputType.);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				        300,
				        LinearLayout.LayoutParams.WRAP_CONTENT);
				input.setLayoutParams(lp);
				layout.addView(input);
				
				final TextView tv2 = new TextView(getActivity());
				tv2.setText("New Password");
				//tv2.setTextColor(getResources().getColor(R.color.blue_middle));
				tv2.setTextSize(20);
				tv2.setLayoutParams(tvl);
				tv2.setGravity(Gravity.CENTER);
				layout.addView(tv2);
				
				final EditText input2 = new EditText(getActivity());
				input2.setTextColor(getResources().getColor(R.color.blue_middle));
				input2.setGravity(Gravity.CENTER);
				input2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
				LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(
				        300,
				        LinearLayout.LayoutParams.WRAP_CONTENT);
				input2.setLayoutParams(lp2);
				layout.addView(input2);
				
				final TextView tv3 = new TextView(getActivity());
				tv3.setTextSize(20);
				tv3.setLayoutParams(tvl);
				tv3.setGravity(Gravity.CENTER);
				//tv3.setTextColor(getResources().getColor(R.color.blue_middle));
				tv3.setText("Confirm New Password");
				layout.addView(tv3);
				
				final EditText input3 = new EditText(getActivity());
				input3.setTextColor(getResources().getColor(R.color.blue_middle));
				input3.setGravity(Gravity.CENTER);
				input3.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
				LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(
				        300,
				        LinearLayout.LayoutParams.WRAP_CONTENT);
				input3.setLayoutParams(lp3);
				layout.addView(input3);
				
				editalert.setView(layout);

				editalert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int whichButton) {			    	
				    	dialog.cancel();
				    	p.setEnabled(false);
						AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
							boolean response=true;
							boolean differentpass=false;
							@Override
							protected void onPreExecute() {
								pd = new ProgressDialog(context);
								pd.setTitle("Loading...");
								pd.setMessage("Please wait.");
								pd.setCancelable(false);
								pd.setIndeterminate(true);
								pd.show();
							}
								
							@Override
							protected Void doInBackground(Void... arg0) {
								try {
										if(!input2.getText().toString().equals(input3.getText().toString()))
										{
											differentpass=true;
										}
										if(MainActivity.usr.Password.equals(input.getText().toString()))
											{
												MainActivity.usr.Password=input2.getText().toString();
												MainActivity.usr.Save();
												response=true;
											}
										else
											response=false;
										Thread.sleep(1000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									return null;
								}
								return null;
							}
							
							@Override
							protected void onPostExecute(Void result) {
								if (pd!=null) {
									pd.dismiss();
									p.setEnabled(true);
									if(differentpass)
									{
									AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
											context);
							 
										// set title
										alertDialogBuilder.setTitle("Couldn't Change the Password");
							 
										// set dialog message
										alertDialogBuilder
											.setMessage("Confirmation different than New Password.")
											.setCancelable(false)
											.setNeutralButton("Ok",new DialogInterface.OnClickListener() {
												public void onClick(DialogInterface dialog,int id) {
													dialog.cancel();
												}
											  });
							 
											// create alert dialog
											AlertDialog alertDialog = alertDialogBuilder.create();
							 
											// show it
											alertDialog.show();
									}
									else if(!response)
										{
										AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
												context);
								 
											// set title
											alertDialogBuilder.setTitle("Couldn't Change the Password");
								 
											// set dialog message
											alertDialogBuilder
												.setMessage("Wrong Current Password. Please try again.")
												.setCancelable(false)
												.setNeutralButton("Ok",new DialogInterface.OnClickListener() {
													public void onClick(DialogInterface dialog,int id) {
														dialog.cancel();
													}
												  });
								 
												// create alert dialog
												AlertDialog alertDialog = alertDialogBuilder.create();
								 
												// show it
												alertDialog.show();
										}
										else
										{
											new User();
											AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
													context);
									 
												// set title
												alertDialogBuilder.setTitle("Success");
									 
												// set dialog message
												alertDialogBuilder
													.setMessage("Password Changed.")
													.setCancelable(false)
													.setNeutralButton("Ok",new DialogInterface.OnClickListener() {
														public void onClick(DialogInterface dialog,int id) {
															dialog.cancel();
														}
													  });
									 
													// create alert dialog
													AlertDialog alertDialog = alertDialogBuilder.create();
									 
													// show it
													alertDialog.show();
										}
								}
							}
								
						};
						task.execute((Void[])null);
			
					}
			
				});
				
				editalert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int whichButton) {
				    	dialog.cancel();
				    }
				});
				// create alert dialog
				AlertDialog alertDialog = editalert.create();
 
				// show it
				alertDialog.show();
				input.requestFocus();
				
			}
 
		});
 
	}
   
}