package com.devmoroz.moneyme;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.devmoroz.moneyme.eventBus.BusProvider;
import com.devmoroz.moneyme.eventBus.WalletChangeEvent;
import com.devmoroz.moneyme.helpers.DBHelper;
import com.devmoroz.moneyme.models.Income;
import com.devmoroz.moneyme.models.Outcome;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddItemActivity extends AppCompatActivity {

    private static final int PREVIEW_REQUEST_CODE = 1;
    private static final int SAVE_REQUEST_CODE = 2;
    private String photoPath;
    private File photoFile;

    private EditText amount;
    private EditText description;
    private Button buttonAdd;
    private Toolbar toolbar;
    private ImageView chequeImage;
    private DBHelper dbHelper;

    private int actionType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        toolbar = (Toolbar) findViewById(R.id.add_activity_toolbar);
        actionType = getIntent().getIntExtra("toolbar_header_text", 1);
        if (toolbar != null) {
            toolbar.setTitle(getIntent().getIntExtra("toolbar_header_text", R.string.default_add_toolbar_name));
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        amount = (EditText) findViewById(R.id.add_activity_amount);
        description = (EditText) findViewById(R.id.add_activity_description);
        buttonAdd = (Button) findViewById(R.id.add_activity_save);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                switch (actionType) {
                    case R.string.income_toolbar_name:
                        try {
                            addIncome();
                        } catch (SQLException ex) {
                        }
                        intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        return;
                    case R.string.outcome_toolbar_name:
                        try {
                            addOutcome();
                        } catch (SQLException ex) {
                        }
                        intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        return;
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    private void addIncome() throws java.sql.SQLException {
        Income income = new Income("", description.getText().toString(), new Date(), Double.parseDouble(amount.getText().toString()), 1);
        dbHelper = MoneyApplication.getInstance().GetDBHelper();
        dbHelper.getIncomeDAO().create(income);
        BusProvider.getInstance().post(new WalletChangeEvent());
    }

    private void addOutcome() throws java.sql.SQLException {
        Outcome outcome = new Outcome("", description.getText().toString(), new Date(), Double.parseDouble(amount.getText().toString()), 1);
        dbHelper = MoneyApplication.getInstance().GetDBHelper();
        dbHelper.getOutcomeDAO().create(outcome);
        BusProvider.getInstance().post(new WalletChangeEvent());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
        if (requestCode == PREVIEW_REQUEST_CODE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            chequeImage.setImageBitmap(imageBitmap);
        } else if (requestCode == SAVE_REQUEST_CODE && resultCode == RESULT_OK) {
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(photoFile);
            intent.setData(contentUri);
            this.sendBroadcast(intent);
        }
    }

    private void takePhoto() {
        Intent takePictureIntent = new
                Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(
                getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = filename();
            } catch (IOException ex) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "No SD card",
                        Toast.LENGTH_SHORT);
                toast.show();
            }
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent,
                        SAVE_REQUEST_CODE);
            }
        }
    }

    private File filename() throws IOException {
        String time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String file = time;
        File dir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(file, ".jpg", dir);
        photoPath = "file:" + image.getAbsolutePath();
        return image;
    }

}
