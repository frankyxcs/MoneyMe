package com.devmoroz.moneyme;


import android.content.Intent;
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

import com.getbase.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FloatingActionButton fabIn;
    private FloatingActionButton fabOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        fabIn = (FloatingActionButton) findViewById(R.id.fab_main_income);
        fabOut = (FloatingActionButton) findViewById(R.id.fab_main_outcome);

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
        TabsPagerFragmentAdapter adapter = new TabsPagerFragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initFloatingActionMenu() {
        fabIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAddActivity(R.string.income_toolbar_name);
            }
        });

        fabOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAddActivity(R.string.outcome_toolbar_name);
            }
        });
    }

    private void startAddActivity(int headerText){
        Intent intent = new Intent(this, AddItemActivity.class);
        intent.putExtra("toolbar_header_text",headerText);
        startActivity(intent);
    }
}
