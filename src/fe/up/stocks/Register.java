package fe.up.stocks;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;


public class Register extends Activity {

	private Button register;
	private ProgressDialog pd;
	private EditText et1, et2, et3;
//test
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.register);
		addListenerOnButton();
		et1 = (EditText) findViewById(R.id.name);
	    et2 = (EditText) findViewById(R.id.username);
	    et3 = (EditText) findViewById(R.id.password);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void onBackPressed() 
	{

	    this.finish();
	    overridePendingTransition  (R.anim.right_slide_in, R.anim.right_slide_out);
	    return;
	}
	
	public void addListenerOnButton() {
		 
		final Context context = this;
 
		register = (Button) findViewById(R.id.register_button);

		register.setOnClickListener(new OnClickListener() {
 
			@Override
			public void onClick(View v) {
				v.setEnabled(false);

				AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
				int response=-1;	
					@Override
					protected void onPreExecute() {
						pd = new ProgressDialog(context);
						pd.setTitle("Registering...");
						pd.setMessage("Please wait");
						pd.setCancelable(false);
						pd.setIndeterminate(true);
						pd.show();
					}
						
					@Override
					protected Void doInBackground(Void... arg0) {
						try {
							
							User usr = new User(et1.getText().toString(),et2.getText().toString(), et3.getText().toString());
							File file = new File(Environment.getExternalStorageDirectory()+"/Stock/User_data/", usr.Nickname+".data");
							if (!file.exists()) {
							    MainActivity.usr=usr;
							    MainActivity.usr.Save();
							    response=1;
							}
							else{
								response=-2;
							}
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
							register.setEnabled(true);
							switch(response) {
								case 1:
								{
									AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
											context);
							 
										// set title
										alertDialogBuilder.setTitle("Success");
							 
										// set dialog message
										alertDialogBuilder
											.setMessage("You've been successfully Registered")
											.setCancelable(false)
											.setNeutralButton("Ok",new DialogInterface.OnClickListener() {
												public void onClick(DialogInterface dialog,int id) {
													Register.this.finish();
												    overridePendingTransition  (R.anim.right_slide_in, R.anim.right_slide_out);
												}
											  });
							 
											// create alert dialog
											AlertDialog alertDialog = alertDialogBuilder.create();
							 
											// show it
											alertDialog.show();
								}
									break;
								case -1:
								{
									AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
											context);
							 
										// set title
										alertDialogBuilder.setTitle("Error");
							 
										// set dialog message
										alertDialogBuilder
											.setMessage("Something went wrong. Please try again.")
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
									break;
								case -2:
								{
									AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
											context);
							 
										// set title
										alertDialogBuilder.setTitle("Error");
							 
										// set dialog message
										alertDialogBuilder
											.setMessage("User Already Registered.")
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
									break;
							}
						}
					}
						
				};
				task.execute((Void[])null);
			}

 
		});
 
	}

}
