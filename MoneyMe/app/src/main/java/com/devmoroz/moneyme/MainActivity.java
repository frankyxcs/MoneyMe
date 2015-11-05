package com.devmoroz.moneyme;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import com.devmoroz.moneyme.eventBus.BusProvider;

import com.devmoroz.moneyme.eventBus.WalletChangeEvent;
import com.devmoroz.moneyme.logging.L;
import com.devmoroz.moneyme.utils.Constants;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

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

        initToolbar();
        initNavigationView();
        initTabs();
        initFloatingActionMenu();
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
        super.onPause();
        BusProvider.getInstance().unregister(this);
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
                switch (menuItem.getItemId()) {
                    case R.id.item_navigation_drawer_settings:
                        menuItem.setChecked(true);
                        Intent intent = new Intent(MainActivity.this, null);
                        startActivity(intent);
                        drawerLayout.closeDrawers();
                        return true;
                    case R.id.item_navigation_drawer_help_and_about:
                        menuItem.setChecked(true);
                        Toast.makeText(MainActivity.this, "MoneyMe 2015", Toast.LENGTH_LONG).show();
                        drawerLayout.closeDrawers();
                        return true;
                }
                return true;
            }
        });

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
                Snackbar.make(coordinator, "Added", Snackbar.LENGTH_LONG).show();
            }
        });

        fabOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAddActivity(R.string.outcome_toolbar_name);
            }
        });
    }

    private void startAddActivity(int headerText) {
        fab.collapse();
        Intent intent = new Intent(this, AddOutcomeActivity.class);
        intent.putExtra("toolbar_header_text", headerText);
        startActivityForResult(intent, REQUEST_CODE_OUTCOME);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_INCOME:

                    break;
                case REQUEST_CODE_OUTCOME:
                    Bundle extras = data.getExtras();
                    createdOutcomeId = extras.getInt(Constants.CREATED_ITEM_ID);
                    if (createdOutcomeId != -1) {
                        Snackbar.make(coordinator, R.string.added_outcome_record, Snackbar.LENGTH_LONG)
                                .setCallback(new Snackbar.Callback() {
                                    @Override
                                    public void onDismissed(Snackbar snackbar, int event) {
                                        if(event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT || event == Snackbar.Callback.DISMISS_EVENT_SWIPE ){
                                                BusProvider.postOnMain(new WalletChangeEvent());
                                        }
                                    }
                                })
                                .setAction(R.string.text_undo, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        try {
                                            MoneyApplication.getInstance().GetDBHelper().getOutcomeDAO().deleteById(createdOutcomeId);
                                            BusProvider.postOnMain(new WalletChangeEvent());
                                        } catch (SQLException ex) {

                                        }
                                    }
                                })
                                .setActionTextColor(Color.RED)
                                .show();
                    }else{
                        L.t(this,"Something went wrong.Please,try again.");
                    }
                    break;
            }
        } else {
            L.t(this,"Something went wrong.Please,try again.");
        }
    }


    private String[] getTabsTitle() {
        return getResources().getStringArray(R.array.drawer_tabs);
    }


}
