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
   
	private int menuIter=4;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ( savedInstanceState == null ) {
        	if(MainActivity.inProfile)
        	{
        		MainActivity.inProfile=false;
        		getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new ProfileFragment()).commit();
        	}
        else if(MainActivity.inPortfolio)
        	{
        		MainActivity.inPortfolio=false;
        		getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new PortfolioFragment()).commit();
        	}
        	else
        		getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new MainFragment()).commit();
        }
    }
   
    @Override
    protected NavDrawerActivityConfiguration getNavDrawerConfiguration() {
       int stockNumber =0;
       for(int i=0; i<MainActivity.usr.portfolios.size();i++)
		{
			stockNumber+=MainActivity.usr.portfolios.get(i).stocks.size();
		}
        NavDrawerItem[] menu = new NavDrawerItem[2*MainActivity.usr.portfolios.size()+stockNumber+16]; 
                menu[0] = NavMenuSection.create( 100, MainActivity.usr.Name+"'s Account");
                menu[1] = NavMenuItem.create(101,"Profile", "navdrawer_profile", true, this);
                menu[2] = NavMenuItem.create(102, "Manage Portfolio", "navdrawer_manage_portfolio", true, this);
				menu[3] = NavMenuItem.create(103, "Options", "navdrawer_options", true, this);
				
				for(int i=0; i<MainActivity.usr.portfolios.size();i++)
				{
					int index = i*1000;
					menu[menuIter] = NavMenuSection.create(index, MainActivity.usr.portfolios.get(i).Name.toUpperCase());
					menuIter=menuIter+1;
					index=index+1;
					menu[menuIter] = NavMenuItem.create(index, "Show Stocks", "navdrawer_"+MainActivity.usr.portfolios.get(i).Name, true, this);
					menuIter=menuIter+1;
					for(int j=0; j<MainActivity.usr.portfolios.get(i).stocks.size();j++)
					{
						int stIndex = index+j+1;
						menu[menuIter] = NavMenuItem.create(stIndex, MainActivity.usr.portfolios.get(i).stocks.get(j).subName, "navdrawer_"+MainActivity.usr.portfolios.get(i).stocks.get(j).subName, true, this);
						menuIter=menuIter+1;
					}
				}
				
				menu[menuIter] = NavMenuSection.create(200, "Stock Market");
				menu[menuIter+1] = NavMenuItem.create(201, "All", "navdrawer_all", true, this);
				menu[menuIter+2] = NavMenuItem.create(202, "Amazon", "navdrawer_amzn", true, this);
				menu[menuIter+3] = NavMenuItem.create(203, "Apple", "navdrawer_aapl", true, this);
				menu[menuIter+4] = NavMenuItem.create(204, "Cisco", "navdrawer_csco", true, this);
				menu[menuIter+5] = NavMenuItem.create(205, "Dell", "navdrawer_dell", true, this);
				menu[menuIter+6] = NavMenuItem.create(206, "Facebook", "navdrawer_fb", true, this);
				menu[menuIter+7] = NavMenuItem.create(207, "Google", "navdrawer_goog", true, this);
				menu[menuIter+8] = NavMenuItem.create(208, "Hewlett-Packard", "navdrawer_hpq", true, this);
				menu[menuIter+9] = NavMenuItem.create(209, "IBM", "navdrawer_ibm", true, this);
				menu[menuIter+10] = NavMenuItem.create(210, "Microsoft", "navdrawer_msft", true, this);
				menu[menuIter+11] = NavMenuItem.create(211, "Oracle", "navdrawer_orcl", true, this);
        
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
        case 101:
        	getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new ProfileFragment()).commit();
            break;
        case 102:
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new PortfolioFragment()).commit();
            break;
        case 103:
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new OptionsFragment()).commit();
            break;
        case 201:
        	getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new MainFragment()).commit();
            break;
        case 202:
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new AmazonFragment()).commit();
            break;
        case 203:
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new AppleFragment()).commit();
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
        default:
        	{
        		int k = id % 1000-1;
        		MainActivity.portIndex=id/1000;
        		if(k==0)
        			getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new StockFragment()).commit();
        		else
        		{
        			MainActivity.stockIndex=k-1;
        			getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new OneStockFragment()).commit();
        		}
        	}
        	break;
        }
    }
}

