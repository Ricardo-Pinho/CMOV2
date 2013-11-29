package fe.up.stocks;




import android.os.Bundle;

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
                NavMenuItem.create(102, "Manage Stocks", "navdrawer_manage_stocks", true, this),
                NavMenuItem.create(103, "Options", "navdrawer_options", true, this),
                NavMenuSection.create(200, "Stocks"),
                NavMenuItem.create(201, "AAPL", "navdrawer_aapl", true, this),
                NavMenuItem.create(202, "AMZN", "navdrawer_amzn", true, this),
                NavMenuItem.create(203, "CSCO", "navdrawer_csco", true, this),
                NavMenuItem.create(203, "DELL", "navdrawer_dell", true, this),
                NavMenuItem.create(203, "FB", "navdrawer_fb", true, this),
                NavMenuItem.create(203, "GOOG", "navdrawer_goog", true, this),
                NavMenuItem.create(203, "HPQ", "navdrawer_hpq", true, this),
                NavMenuItem.create(203, "IBM", "navdrawer_ibm", true, this),
                NavMenuItem.create(203, "MSFT", "navdrawer_msft", true, this),
                NavMenuItem.create(203, "ORCL", "navdrawer_orcl", true, this)};
       
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
        /*case 101:
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new FriendMainFragment()).commit();
            break;
        case 102:
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new AirportFragment()).commit();
            break;*/
        }
    }
}

