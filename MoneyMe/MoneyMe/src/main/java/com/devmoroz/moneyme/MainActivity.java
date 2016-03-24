package com.devmoroz.moneyme;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ViewFlipper;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.devmoroz.moneyme.adapters.TabsPagerFragmentAdapter;
import com.devmoroz.moneyme.eventBus.AppInitCompletedEvent;
import com.devmoroz.moneyme.eventBus.BusProvider;
import com.devmoroz.moneyme.eventBus.ChartSliceClickedEvent;
import com.devmoroz.moneyme.eventBus.SearchCanceled;
import com.devmoroz.moneyme.eventBus.SearchTriggered;
import com.devmoroz.moneyme.eventBus.WalletChangeEvent;
import com.devmoroz.moneyme.export.BackupRestoreHelper;
import com.devmoroz.moneyme.export.ExportAsyncTask;
import com.devmoroz.moneyme.export.ExportParams;
import com.devmoroz.moneyme.helpers.DBHelper;
import com.devmoroz.moneyme.logging.L;
import com.devmoroz.moneyme.models.Account;
import com.devmoroz.moneyme.models.CreatedItem;
import com.devmoroz.moneyme.models.Transaction;
import com.devmoroz.moneyme.utils.Constants;
import com.devmoroz.moneyme.utils.CurrencyCache;
import com.devmoroz.moneyme.utils.FormatUtils;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.kobakei.ratethisapp.RateThisApp;
import com.squareup.otto.Subscribe;

import java.sql.SQLException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    final int REQUEST_CODE_INCOME = 918;
    final int REQUEST_CODE_OUTCOME = 1218;
    final int REQUEST_CODE_TRANSFER = 1941;

    public static final int VIEW_SPLASH = 0;
    public static final int VIEW_CONTENT = 1;

    public static RateThisApp.Config rateAppConfig = new RateThisApp.Config(7, 21);

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FloatingActionButton fabIn;
    private FloatingActionButton fabOut;
    private FloatingActionsMenu fab;
    private View coordinator;
    private ViewFlipper viewFlipper;

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
        viewFlipper = (ViewFlipper) findViewById(R.id.mainViewFlipper);

        checkForFirstRun();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (BuildConfig.CAN_REQUEST_RATING) {
            RateThisApp.init(rateAppConfig);
            RateThisApp.onStart(this);
            RateThisApp.showRateDialogIfNeeded(this);
        }
    }

    private void checkForFirstRun() {
        SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());

        boolean isFirstStart = getPrefs.getBoolean(getString(R.string.pref_first_time_run), true);
        if (isFirstStart) {
            //  Launch app intro
            Intent i = new Intent(MainActivity.this, FirstRunIntro.class);
            startActivity(i);
            finish();
        } else {
            start();
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
        handleIntents();
    }

    private void switchToSplashView() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        viewFlipper.setDisplayedChild(VIEW_SPLASH);
    }

    private void switchToContentView() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        viewFlipper.setDisplayedChild(VIEW_CONTENT);

        if (hasNewFeatures()){
            showWhatsNewDialog(this);
        }
    }

    @Subscribe
    public void onAppInitCompleted(AppInitCompletedEvent event) {
        initialize();
        switchToContentView();
    }

    @Subscribe
    public void onChartSliceClicked(ChartSliceClickedEvent event) {
        startAddActivity(Constants.OUTCOME_ACTIVITY, event.Category);
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

    private void handleIntents() {
        Intent i = getIntent();

        if (i.getAction() == null) return;

        if (Constants.ACTION_LAUNCH_INCOME.equals(i.getAction())) {
            startAddActivity(Constants.INCOME_ACTIVITY, null);
            return;
        }

        if (Constants.ACTION_LAUNCH_OUTCOME.equals(i.getAction())) {
            startAddActivity(Constants.OUTCOME_ACTIVITY, null);
            return;
        }

        if (Constants.ACTION_LAUNCH_TRANSFER.equals(i.getAction())) {
            startAddActivity(Constants.TRANSFER_ACTIVITY, null);
            return;
        }

    }

    private void initToolbar() {
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case (R.id.synchronize):

                        return true;
                    default:
                        return true;
                }
            }
        });
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
                    case R.id.item_navigation_drawer_notes:
                        navigate(NotesActivity.class);
                        return true;
                    case R.id.item_navigation_drawer_settings:
                        navigate(SettingsActivity.class);
                        return true;
                    case R.id.item_navigation_drawer_backup:
                        showBackupDialog();
                        return true;
                    case R.id.item_navigation_drawer_export:
                        showCSVExportDialog();
                        return true;
                }
                return true;
            }
        });

    }

    private void showBackupDialog() {
        final String[] items = new String[]{getString(R.string.db_backup),
                getString(R.string.db_restore)};

        new MaterialDialog.Builder(this)
                .title(R.string.backup)
                .items(items)
                .widgetColorRes(R.color.colorPrimary)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        BackupRestoreHelper helper = new BackupRestoreHelper(MainActivity.this);
                        switch (which) {
                            case (0):
                                dialog.dismiss();
                                helper.Backup(MainActivity.this, coordinator);
                                break;
                            case (1): {
                                dialog.dismiss();
                                helper.Restore(MainActivity.this, coordinator);
                                break;
                            }
                        }
                    }
                })
                .show();
    }

    private void showCSVExportDialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.csv_export)
                .content(R.string.csv_export_dialog_content)
                .positiveText(R.string.ok_continue)
                .negativeText(R.string.cancel)
                .positiveColorRes(R.color.colorPrimary)
                .negativeColorRes(R.color.colorPrimary)
                .widgetColorRes(R.color.colorPrimary)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                        final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
                        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        dialog.setCancelable(false);
                        dialog.setMessage(getString(R.string.csv_export_inprogress));
                        dialog.show();
                        ExportParams exportParams = new ExportParams(ExportParams.ExportTarget.SHARING, ExportParams.ExportType.CSV);
                        new ExportAsyncTask(MainActivity.this, dialog, new ExportAsyncTask.CompletionListener() {
                            @Override
                            public void onExportComplete() {
                                dialog.dismiss();
                                L.T(MainActivity.this, getString(R.string.csv_export_completed));
                            }

                            @Override
                            public void onError(int errorCode) {
                                L.T(MainActivity.this, "Бляха, что-то не получилось");
                                dialog.dismiss();
                            }
                        }).execute(exportParams);
                    }
                })
                .show();
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
                startAddActivity(Constants.INCOME_ACTIVITY, null);
            }
        });

        fabOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAddActivity(Constants.OUTCOME_ACTIVITY, null);
            }
        });
    }

    public void startAddActivity(String activity, String category) {
        fab.collapse();
        Intent intent;
        switch (activity) {
            case Constants.INCOME_ACTIVITY:
                intent = new Intent(this, AddIncomeActivity.class);
                startActivityForResult(intent, REQUEST_CODE_INCOME);
                break;
            case Constants.OUTCOME_ACTIVITY:
                intent = new Intent(this, AddOutcomeActivity.class);
                if (FormatUtils.isNotEmpty(category)) {
                    intent.putExtra(Constants.OUTCOME_DEFAULT_CATEGORY, category);
                }
                startActivityForResult(intent, REQUEST_CODE_OUTCOME);
                break;
            case Constants.TRANSFER_ACTIVITY:
                intent = new Intent(this, AddTransferActivity.class);
                startActivityForResult(intent, REQUEST_CODE_TRANSFER);
                break;
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            CreatedItem createdItem = data.getParcelableExtra(Constants.CREATED_TRANSACTION_DETAILS);
            if (!createdItem.isEmpty()) {
                String sign = CurrencyCache.getCurrencyOrEmpty().getSymbol();
                String info = "";
                if (requestCode == REQUEST_CODE_INCOME) {
                    info = getString(R.string.added_income, createdItem.getCategory(), createdItem.getAmount(), sign);
                } else if (requestCode == REQUEST_CODE_OUTCOME) {
                    info = getString(R.string.added_outcome, createdItem.getCategory(), createdItem.getAmount(), sign);
                } else if (requestCode == REQUEST_CODE_TRANSFER) {
                    info = getString(R.string.added_transfer, createdItem.getCategory(), createdItem.getAmount(), sign);
                }
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
                            DBHelper dbhelper = MoneyApplication.GetDBHelper();
                            Transaction t = dbhelper.getTransactionDAO().queryForId(UUID.fromString(createdItem.getItemId()));
                            if (requestCode == REQUEST_CODE_INCOME) {
                                Account acc = t.getAccountTo();
                                acc.setBalance(acc.getBalance() - createdItem.getAmount());
                                dbhelper.getAccountDAO().update(acc);
                            } else if (requestCode == REQUEST_CODE_OUTCOME) {
                                Account acc = t.getAccountFrom();
                                acc.setBalance(acc.getBalance() + createdItem.getAmount());
                                dbhelper.getAccountDAO().update(acc);
                            } else if (requestCode == REQUEST_CODE_TRANSFER) {
                                Account aFrom = t.getAccountFrom();
                                Account aTo = t.getAccountTo();
                                aFrom.setBalance(aFrom.getBalance() + createdItem.getAmount());
                                aTo.setBalance(aTo.getBalance() - createdItem.getAmount());
                                dbhelper.getAccountDAO().update(aFrom);
                                dbhelper.getAccountDAO().update(aTo);
                            }
                            dbhelper.getTransactionDAO().delete(t);
                            BusProvider.postOnMain(new WalletChangeEvent());
                        } catch (SQLException ex) {
                            L.t(MainActivity.this, "Something went wrong.Please,try again.");
                        }
                    }
                })
                        .setActionTextColor(Color.RED);
                snackBar.show();
            } else {
                L.t(this, "Something went wrong.Please,try again.");
            }
        } else {

        }
    }


    private String[] getTabsTitle() {
        return getResources().getStringArray(R.array.drawer_tabs);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView search = (SearchView) menu.findItem(R.id.search_main).getActionView();

        search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                String term = query.trim();
                if (FormatUtils.isNotEmpty(term)) {
                    jumpToTab(Constants.TAB_HISTORY);
                    BusProvider.postOnMain(new SearchTriggered(term));
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                String term = query.trim();
                if (FormatUtils.isNotEmpty(term)) {
                    jumpToTab(Constants.TAB_HISTORY);
                    BusProvider.postOnMain(new SearchTriggered(term));
                }
                return false;

            }

        });

        search.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                BusProvider.postOnMain(new SearchCanceled());
                return false;
            }
        });

        return true;
    }

    private boolean hasNewFeatures(){
        String minorVersion = getResources().getString(R.string.app_minor_version);
        int currentMinor = Integer.parseInt(minorVersion);

        SharedPreferences prefs = android.preference.PreferenceManager.getDefaultSharedPreferences(this);
        int previousMinor = prefs.getInt(getString(R.string.key_previous_minor_version), 0);
        if (currentMinor > previousMinor){
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(getString(R.string.key_previous_minor_version), currentMinor);
            editor.apply();
            return true;
        }
        return false;
    }

    public static AlertDialog showWhatsNewDialog(Context context){
        Resources resources = context.getResources();
        StringBuilder releaseTitle = new StringBuilder(resources.getString(R.string.whats_new_title));
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            releaseTitle.append(" - v").append(packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {

        }

        return new AlertDialog.Builder(context)
                .setTitle(releaseTitle.toString())
                .setMessage(R.string.whats_new_content)
                .setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }
}
