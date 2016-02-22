package com.devmoroz.moneyme;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.devmoroz.moneyme.helpers.DBHelper;
import com.devmoroz.moneyme.helpers.PermissionsHelper;
import com.devmoroz.moneyme.models.Currency;
import com.devmoroz.moneyme.models.Location;
import com.devmoroz.moneyme.models.MapDataType;
import com.devmoroz.moneyme.models.MapMode;
import com.devmoroz.moneyme.models.Transaction;
import com.devmoroz.moneyme.models.TransactionType;
import com.devmoroz.moneyme.utils.Constants;
import com.devmoroz.moneyme.utils.CurrencyCache;
import com.devmoroz.moneyme.utils.FormatUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MapMode mode;
    private String details;
    private MapDataType dataType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Intent intent = getIntent();
        mode = (MapMode) intent.getSerializableExtra(Constants.EXTRA_MAP_MODE);
        if (mode != MapMode.View) {
            details = intent.getStringExtra(Constants.EXTRA_MAP_ITEM_DETAILS);
            dataType = (MapDataType) intent.getSerializableExtra(Constants.EXTRA_MAP_DATA_TYPE);
        }

        initToolbar();

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_map);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case (android.R.id.home):
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mode != MapMode.View) {
            addMarkersToMap();
        }

        PermissionsHelper.requestPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION, R.string
                .permission_coarse_location, MapActivity.this.findViewById(R.id.coordinator_map), () -> mMap.setMyLocationEnabled(true));
    }

    private void addMarkersToMap() {
        DBHelper dbHelper = MoneyApplication.getInstance().GetDBHelper();
        Currency c = CurrencyCache.getCurrencyOrEmpty();
        try {
            if (mode == MapMode.Single && FormatUtils.isNotEmpty(details)) {
                Transaction t = dbHelper.getTransactionDAO().queryForId(UUID.fromString(details));
                LatLng latLng = Location.getCoordinatesFromString(t.getLocation());
                MarkerOptions mOptions = new MarkerOptions()
                        .position(latLng)
                        .title(t.getLocationName())
                        .snippet(t.getMarkerSnippet(getApplicationContext(), c));
                mMap.addMarker(mOptions).showInfoWindow();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
            } else {
                List<Transaction> transactions;
                if (FormatUtils.isNotEmpty(details)) {
                    transactions = dbHelper.getTransactionDAO().queryTransactionsWithLocationForCategory(details);
                } else {
                    transactions = dbHelper.getTransactionDAO().queryTransactionsWithLocation();
                }
                if (!transactions.isEmpty()) {
                    for (Transaction t : transactions) {
                        MarkerOptions mOptions = new MarkerOptions()
                                .position(Location.getCoordinatesFromString(t.getLocation()))
                                .title(t.getLocationName())
                                .snippet(t.getMarkerSnippet(getApplicationContext(), c));
                        mMap.addMarker(mOptions);
                    }
                }
            }
        } catch (SQLException ex) {

        }

    }
}
