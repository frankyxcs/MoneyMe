package com.devmoroz.moneyme;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.devmoroz.moneyme.adapters.TabsPagerFragmentAdapter;
import com.devmoroz.moneyme.eventBus.AppInitCompletedEvent;
import com.devmoroz.moneyme.eventBus.BusProvider;

import com.devmoroz.moneyme.eventBus.WalletChangeEvent;
import com.devmoroz.moneyme.helpers.CurrencyHelper;
import com.devmoroz.moneyme.helpers.DBHelper;
import com.devmoroz.moneyme.logging.L;
import com.devmoroz.moneyme.models.Account;
import com.devmoroz.moneyme.models.Currency;
import com.devmoroz.moneyme.utils.Constants;
import com.devmoroz.moneyme.utils.CurrencyCache;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.squareup.otto.Subscribe;

import java.sql.SQLException;

public class MainActivity extends AppCompatActivity {

    final int REQUEST_CODE_INCOME = 918;
    final int REQUEST_CODE_OUTCOME = 1218;
    private int createdOutcomeId = -1;
    private int createdIncomeId = -1;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FloatingActionButton fabIn;
    private FloatingActionButton fabOut;
    private FloatingActionsMenu fab;
    private View coordinator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        fab = (FloatingActionsMenu) findViewById(R.id.fab_main);
        fabIn = (FloatingActionButton) findViewById(R.id.fab_main_income);
        fabOut = (FloatingActionButton) findViewById(R.id.fab_main_outcome);
        coordinator = findViewById(R.id.coordinator);

        start();
    }

    private void selectCurrency() {
        Currency c = CurrencyCache.getCurrencyOrEmpty();
        if (c.isEmpty()) {
            CurrencyHelper ch = new CurrencyHelper(MainActivity.this, MoneyApplication.getInstance().GetDBHelper());
            ch.show();
        }
    }

    private void start() {
        if (MoneyApplication.isInitialized()) {
            initialize();
            switchToContentView();
        } else {
            switchToSplashView();
        }
    }

    private void initialize() {
        initToolbar();
        initNavigationView();
        initTabs();
        initFloatingActionMenu();

        selectCurrency();
    }

    private void switchToSplashView() {
        toolbar.setVisibility(View.GONE);
        fab.setVisibility(View.GONE);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    private void switchToContentView() {
        toolbar.setVisibility(View.VISIBLE);
        fab.setVisibility(View.VISIBLE);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    @Subscribe
    public void onAppInitCompleted(AppInitCompletedEvent event) {
        initialize();
        switchToContentView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void onPause() {
        BusProvider.getInstance().unregister(this);
        super.onPause();
    }

    private void initToolbar() {
        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });

        toolbar.inflateMenu(R.menu.menu_main);
    }

    private void initNavigationView() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.view_navigation_open, R.string.view_navigation_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                drawerLayout.closeDrawers();
                switch (menuItem.getItemId()) {
                    case R.id.item_navigation_drawer_accounts:
                        menuItem.setChecked(true);
                        jumpToTab(Constants.TAB_ACCOUNTS);
                        return true;
                    case R.id.item_navigation_drawer_history:
                        menuItem.setChecked(true);
                        jumpToTab(Constants.TAB_HISTORY);
                        return true;
                    case R.id.item_navigation_drawer_chart:
                        menuItem.setChecked(true);
                        jumpToTab(Constants.TAB_CHART);
                        return true;
                    case R.id.item_navigation_drawer_goals:
                        menuItem.setChecked(true);
                        jumpToTab(Constants.TAB_GOALS);
                        return true;
                    case R.id.item_navigation_drawer_about:
                        navigate(AboutActivity.class);
                        return true;
                    case R.id.item_navigation_drawer_settings:
                        navigate(SettingsActivity.class);
                        return true;
                }
                return true;
            }
        });

    }

    private void jumpToTab(int tab) {
        viewPager.setCurrentItem(tab);
    }

    private void navigate(final Class<? extends Activity> activityClass) {
        if (!getClass().equals(activityClass)) {
            final Intent intent = new Intent(this, activityClass);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }
    }

    private void initTabs() {
        TabsPagerFragmentAdapter adapter = new TabsPagerFragmentAdapter(getSupportFragmentManager(), getTabsTitle(), fab);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initFloatingActionMenu() {
        fabIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAddActivity(Constants.INCOME_ACTIVITY);
            }
        });

        fabOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAddActivity(Constants.OUTCOME_ACTIVITY);
            }
        });
    }

    private void startAddActivity(String activity) {
        fab.collapse();
        Intent intent;
        switch (activity) {
            case Constants.INCOME_ACTIVITY:
                intent = new Intent(this, AddIncomeActivity.class);
                startActivityForResult(intent, REQUEST_CODE_INCOME);
                break;
            case Constants.OUTCOME_ACTIVITY:
                intent = new Intent(this, AddOutcomeActivity.class);
                startActivityForResult(intent, REQUEST_CODE_OUTCOME);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            switch (requestCode) {
                case REQUEST_CODE_INCOME:
                    createdIncomeId = extras.getInt(Constants.CREATED_ITEM_ID);
                    if (createdIncomeId != -1) {
                        String accountName = extras.getString(Constants.CREATED_ITEM_CATEGORY);
                        final int accId = extras.getInt(Constants.CREATED_ITEM_ACCOUNT);
                        final double amount = extras.getDouble(Constants.CREATED_ITEM_AMOUNT);
                        String sign = CurrencyCache.getCurrencyOrEmpty().getSymbol();
                        String info = getString(R.string.added_income, accountName, amount, sign);
                        final Snackbar snackBar = Snackbar.make(coordinator, info, Snackbar.LENGTH_LONG);
                        snackBar.setCallback(new Snackbar.Callback() {
                            boolean mShown = false;

                            @Override
                            public void onShown(Snackbar snackbar) {
                                mShown = true;
                            }

                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                if (mShown) {
                                    mShown = false;
                                    if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT || event == Snackbar.Callback.DISMISS_EVENT_SWIPE) {
                                        BusProvider.postOnMain(new WalletChangeEvent());
                                    }
                                }
                            }
                        });
                        snackBar.setAction(R.string.text_undo, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    DBHelper dbhelper = MoneyApplication.getInstance().GetDBHelper();
                                    Account acc = dbhelper.getAccountDAO().queryForId(accId);
                                    acc.setBalance(acc.getBalance()-amount);
                                    dbhelper.getAccountDAO().update(acc);
                                    dbhelper.getIncomeDAO().deleteById(createdIncomeId);
                                    BusProvider.postOnMain(new WalletChangeEvent());
                                } catch (SQLException ex) {

                                }
                            }
                        })
                        .setActionTextColor(Color.RED);
                        snackBar.show();
                    } else {
                        L.t(this, "Something went wrong.Please,try again.");
                    }
                    break;
                case REQUEST_CODE_OUTCOME:
                    createdOutcomeId = extras.getInt(Constants.CREATED_ITEM_ID);
                    if (createdOutcomeId != -1) {
                        String category = extras.getString(Constants.CREATED_ITEM_CATEGORY);
                        final int accId = extras.getInt(Constants.CREATED_ITEM_ACCOUNT);
                        final double amount = extras.getDouble(Constants.CREATED_ITEM_AMOUNT);
                        String sign = CurrencyCache.getCurrencyOrEmpty().getSymbol();
                        String info = getString(R.string.added_outcome, category,amount,sign);
                        final Snackbar snackBar = Snackbar.make(coordinator, info, Snackbar.LENGTH_LONG);
                        snackBar.setCallback(new Snackbar.Callback() {
                            boolean mShown = false;

                            @Override
                            public void onShown(Snackbar snackbar) {
                                mShown = true;
                            }

                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                if (mShown) {
                                    mShown = false;
                                    if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT || event == Snackbar.Callback.DISMISS_EVENT_SWIPE) {
                                        BusProvider.postOnMain(new WalletChangeEvent());
                                    }
                                }
                            }
                        });
                        snackBar.setAction(R.string.text_undo, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    DBHelper dbhelper = MoneyApplication.getInstance().GetDBHelper();
                                    Account acc = dbhelper.getAccountDAO().queryForId(accId);
                                    acc.setBalance(acc.getBalance() + amount);
                                    dbhelper.getAccountDAO().update(acc);
                                    dbhelper.getOutcomeDAO().deleteById(createdOutcomeId);
                                    BusProvider.postOnMain(new WalletChangeEvent());
                                } catch (SQLException ex) {

                                }
                            }
                        })
                        .setActionTextColor(Color.RED);
                        snackBar.show();

                    } else {
                        L.t(this, "Something went wrong.Please,try again.");
                    }
                    break;
            }
        } else {
            L.t(this, "Something went wrong.Please,try again.");
        }
    }


    private String[] getTabsTitle() {
        return getResources().getStringArray(R.array.drawer_tabs);
    }


}
