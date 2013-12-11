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
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;



public class OptionsFragment extends Fragment {


	private ProgressDialog pd;
	private Button saveOptions;
	private RadioButton rb, rb1, rb2, rb3, rb4, rb5, rb6;
	private RadioGroup rg, rg2, rg3;
	private DatePicker d1, d2;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    	
        final View mainView = inflater
                .inflate(R.layout.options, container, false);
        if(!MainActivity.otherPeriod)
        {
        	mainView.findViewById(R.id.BeginDate).setVisibility(View.GONE);
        	mainView.findViewById(R.id.endDate).setVisibility(View.GONE);
        	mainView.findViewById(R.id.datePicker1).setVisibility(View.GONE);
        	mainView.findViewById(R.id.datePicker2).setVisibility(View.GONE);
        	mainView.findViewById(R.id.radioGroup3).setVisibility(View.GONE);
        }
        
        rb = (RadioButton)mainView.findViewById(R.id.MonthOption);
		rb1 = (RadioButton)mainView.findViewById(R.id.YearOption );
		rb4 = (RadioButton)mainView.findViewById(R.id.OtherOption );
		d1 = (DatePicker)mainView.findViewById(R.id.datePicker1 );
		d2 = (DatePicker)mainView.findViewById(R.id.datePicker2 );
		rb.setId(1);
		rb1.setId(2);
		rb4.setId(3);
		
		rb5 = (RadioButton)mainView.findViewById(R.id.ChartEachDayOption);
		rb6 = (RadioButton)mainView.findViewById(R.id.ChartEachMonthOption );
		rb5.setId(1);
		rb6.setId(2);
		rg3 = (RadioGroup)mainView.findViewById(R.id.radioGroup3);
		
		rb4.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {

                //handle the boolean flag here. 
                  if(arg1==true)
                  {
                	mainView.findViewById(R.id.BeginDate).setVisibility(View.VISIBLE);
                  	mainView.findViewById(R.id.endDate).setVisibility(View.VISIBLE);
                  	mainView.findViewById(R.id.datePicker1).setVisibility(View.VISIBLE);
                  	mainView.findViewById(R.id.datePicker2).setVisibility(View.VISIBLE);
                  	mainView.findViewById(R.id.radioGroup3).setVisibility(View.VISIBLE);
                  }

                else 
                {
                	mainView.findViewById(R.id.BeginDate).setVisibility(View.GONE);
                	mainView.findViewById(R.id.endDate).setVisibility(View.GONE);
                	mainView.findViewById(R.id.datePicker1).setVisibility(View.GONE);
                	mainView.findViewById(R.id.datePicker2).setVisibility(View.GONE);
                	mainView.findViewById(R.id.radioGroup3).setVisibility(View.GONE);
                }

            }
        });
		rg = (RadioGroup)mainView.findViewById(R.id.radioGroup1);
		if(MainActivity.otherPeriod)
			rg.check(3);
		else if(MainActivity.monthPeriodGraph)
			rg.check(1);
		else
			rg.check(2);
		
		rb2 = (RadioButton)mainView.findViewById(R.id.ChartYesOption);
		rb3 = (RadioButton)mainView.findViewById(R.id.ChartNoOption );
		rb2.setId(1);
		rb3.setId(2);
		rg2 = (RadioGroup)mainView.findViewById(R.id.radioGroup2);
		if(MainActivity.betterCharts)
			rg2.check(1);
		else
			rg2.check(2);
		

		if(MainActivity.monthPeriodGraph)
			rg3.check(1);
		else
			rg3.check(2);
		addListenerOnButton1(mainView);
        	return mainView;
    }
    
    public void addListenerOnButton1(View mainView) {
		 
    	final Context context = getActivity();
 
		saveOptions = (Button) mainView.findViewById(R.id.OptionsSave);
 
		saveOptions.setOnClickListener(new OnClickListener() {
 
			@Override
			public void onClick(View v) {
				if(rb.isChecked())
				{
					MainActivity.monthPeriodGraph=true;
				}
				else if(rb1.isChecked())
				{
					MainActivity.monthPeriodGraph=false;
				}
				else
				{
					if(rb5.isChecked())
						MainActivity.monthPeriodGraph=true;
					else
						MainActivity.monthPeriodGraph=false;
					MainActivity.otherPeriod=true;
					MainActivity.beginDate.set(Calendar.MONTH, d1.getMonth());
					MainActivity.beginDate.set(Calendar.DAY_OF_MONTH, d1.getDayOfMonth());
					MainActivity.beginDate.set(Calendar.YEAR, d1.getYear());
					
					MainActivity.endDate.set(Calendar.MONTH, d2.getMonth());
					MainActivity.endDate.set(Calendar.DAY_OF_MONTH, d2.getDayOfMonth());
					MainActivity.endDate.set(Calendar.YEAR, d2.getYear());
					
				}
				if(rb2.isChecked())
				{
					MainActivity.betterCharts=true;
				}
				else if(rb3.isChecked())
				{
					MainActivity.betterCharts=false;
				}
				
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						context);
		 
					// set title
					alertDialogBuilder.setTitle("Success");
		 
					// set dialog message
					alertDialogBuilder
						.setMessage("Changes have been saved.")
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
 
		});
 
	}
    
    public void addListenerOnButton2(View mainView) {
		 
    	final Context context = getActivity();
 
		saveOptions = (Button) mainView.findViewById(R.id.OptionsSave);
 
		saveOptions.setOnClickListener(new OnClickListener() {
 
			@Override
			public void onClick(View v) {
				if(rb.isChecked())
				{
					MainActivity.monthPeriodGraph=true;
				}
				if(rb1.isChecked())
				{
					MainActivity.monthPeriodGraph=false;
				}
				
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						context);
		 
					// set title
					alertDialogBuilder.setTitle("Success");
		 
					// set dialog message
					alertDialogBuilder
						.setMessage("Graphic Window has changed.")
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
 
		});
 
	}
   
}