package fe.up.stocks;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Login extends Activity {
	private Button login;
	private ProgressDialog pd;
	private EditText et1, et2;

//test
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.login);
		addListenerOnButton();
		
		Typeface blockFonts = Typeface.createFromAsset(getAssets(),"fonts/Ubuntu.ttf");
		Typeface blockFonts2 = Typeface.createFromAsset(getAssets(),"fonts/Ubuntu-M.ttf");
		Button txtSampleTxt1 = (Button) findViewById(R.id.login_button);
		EditText txtSampleTxt2 = (EditText) findViewById(R.id.username);
		EditText txtSampleTxt3 = (EditText) findViewById(R.id.password);
		TextView txtSampleTxt4 = (TextView) findViewById(R.id.textView2);
		TextView txtSampleTxt5 = (TextView) findViewById(R.id.textView3);
		txtSampleTxt4.setTypeface(blockFonts);
		txtSampleTxt5.setTypeface(blockFonts);
		txtSampleTxt1.setTypeface(blockFonts2);
		txtSampleTxt2.setTypeface(blockFonts2);
		txtSampleTxt3.setTypeface(blockFonts2);
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
	    overridePendingTransition  (R.anim.left_slide_in, R.anim.left_slide_out);
	    return;
	}


	public void addListenerOnButton() {
		 
		final Context context = this;
	
		login = (Button) findViewById(R.id.login_button);
	
		login.setOnClickListener(new OnClickListener() {
	
			@Override
			public void onClick(View v) {
				v.setEnabled(false);
				AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
					boolean response=true;
					boolean loadresponse=false;
					@Override
					protected void onPreExecute() {
						pd = new ProgressDialog(context);
						pd.setTitle("Authenticating...");
						pd.setMessage("Please wait.");
						pd.setCancelable(false);
						pd.setIndeterminate(true);
						pd.show();
					}
						
					@Override
					protected Void doInBackground(Void... arg0) {
						try {
								et1 = (EditText) findViewById(R.id.username);
								et2 = (EditText) findViewById(R.id.password);
								
								MainActivity.usr.Nickname = et1.getText().toString();
								loadresponse = MainActivity.usr.Load();
								MainActivity.usr.Password = et2.getText().toString();
								if(loadresponse)
								{
									response=false;
								}
								if(response)
								{
									try {
										response = MainActivity.usr.authenticate(MainActivity.usr.Password, MainActivity.usr.getEncryptedPassword(), MainActivity.usr.getSalt());
									} catch (NoSuchAlgorithmException e) {
										Log.d("Error", "error deSerializing User: NoSuchAlgorithmxception");
										e.printStackTrace();
									} catch (InvalidKeySpecException e) {
										Log.d("Error", "error deSerializing User: InvalidKeySpecException");
										e.printStackTrace();
									}
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
							login.setEnabled(true);
							if(response)
								{
									Intent intent = new Intent(context, MainMenu.class);
									startActivity(intent);
									overridePendingTransition  (R.anim.right_slide_in, R.anim.right_slide_out);
								}
								else if(loadresponse)
								{
									new User();
									AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
											context);
							 
										// set title
										alertDialogBuilder.setTitle("User Not Found");
							 
										// set dialog message
										alertDialogBuilder
											.setMessage("You need to Register before logging in.")
											.setCancelable(false)
											.setNeutralButton("Ok",new DialogInterface.OnClickListener() {
												public void onClick(DialogInterface dialog,int id) {
													dialog.cancel();
													Login.this.finish();
													overridePendingTransition  (R.anim.right_slide_in, R.anim.right_slide_out);
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
										alertDialogBuilder.setTitle("Login Failed");
							 
										// set dialog message
										alertDialogBuilder
											.setMessage("Wrong Username or Password. Please try again.")
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
	}

}