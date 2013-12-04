package fe.up.stocks;




import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;

import com.jjoe64.graphview.GraphView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class MainMenu extends AbstractNavDrawerActivity {
   
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ( savedInstanceState == null ) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new MainFragment()).commit();
        }
    }
   
    @Override
    protected NavDrawerActivityConfiguration getNavDrawerConfiguration() {
       
        NavDrawerItem[] menu = new NavDrawerItem[] {
                NavMenuSection.create( 100, "Account"),
                NavMenuItem.create(101,"Profile", "navdrawer_profile", true, this),
                NavMenuItem.create(102, "Manage Portfolio", "navdrawer_manage_stocks", true, this),
                NavMenuItem.create(103, "Options", "navdrawer_options", true, this),
                NavMenuSection.create(200, "Stock Market"),
                NavMenuItem.create(201, "All", "navdrawer_all", true, this),
                NavMenuItem.create(202, "AAPL", "navdrawer_aapl", true, this),
                NavMenuItem.create(203, "AMZN", "navdrawer_amzn", true, this),
                NavMenuItem.create(204, "CSCO", "navdrawer_csco", true, this),
                NavMenuItem.create(205, "DELL", "navdrawer_dell", true, this),
                NavMenuItem.create(206, "FB", "navdrawer_fb", true, this),
                NavMenuItem.create(207, "GOOG", "navdrawer_goog", true, this),
                NavMenuItem.create(208, "HPQ", "navdrawer_hpq", true, this),
                NavMenuItem.create(209, "IBM", "navdrawer_ibm", true, this),
                NavMenuItem.create(210, "MSFT", "navdrawer_msft", true, this),
                NavMenuItem.create(211, "ORCL", "navdrawer_orcl", true, this)};
        
        //MainActivity.sgraph = new ArrayList<stockGraph>();
        //MainActivity.graphs = new ArrayList<GraphView>(); 
        
  
       
        NavDrawerActivityConfiguration navDrawerActivityConfiguration = new NavDrawerActivityConfiguration();
        navDrawerActivityConfiguration.setMainLayout(R.layout.main_menu);
        navDrawerActivityConfiguration.setDrawerLayoutId(R.id.drawer_layout);
        navDrawerActivityConfiguration.setLeftDrawerId(R.id.left_drawer);
        navDrawerActivityConfiguration.setNavItems(menu);
        navDrawerActivityConfiguration.setDrawerShadow(R.drawable.drawer_shadow);      
        navDrawerActivityConfiguration.setDrawerOpenDesc(R.string.drawer_open);
        navDrawerActivityConfiguration.setDrawerCloseDesc(R.string.drawer_close);
        navDrawerActivityConfiguration.setBaseAdapter(
            new NavDrawerAdapter(this, R.layout.navdrawer_item, menu ));
        return navDrawerActivityConfiguration;
    }
   
	@Override
	public void onBackPressed() 
	{

	    this.finish();
	    overridePendingTransition  (R.anim.left_slide_in, R.anim.left_slide_out);
	    return;
	}
    
    @Override
    protected void onNavItemSelected(int id) {
        switch ((int)id) {
        case 201:
        	getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new MainFragment()).commit();
            break;
        case 202:
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new AppleFragment()).commit();
            break;
        case 203:
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new AmazonFragment()).commit();
            break;
        case 204:
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new CiscoFragment()).commit();
            break;
        case 205:
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new DellFragment()).commit();
            break;
        case 206:
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new FacebookFragment()).commit();
            break;
        case 207:
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new GoogleFragment()).commit();
            break;
        case 208:
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new HPQFragment()).commit();
            break;
        case 209:
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new IBMFragment()).commit();
            break;
        case 210:
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new MicrosoftFragment()).commit();
            break;
        case 211:
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new OracleFragment()).commit();
            break;
        }
    }
}

