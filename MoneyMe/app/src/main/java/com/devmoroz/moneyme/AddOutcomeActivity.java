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
import android.text.InputType;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.codetroopers.betterpickers.datepicker.DatePickerBuilder;
import com.codetroopers.betterpickers.datepicker.DatePickerDialogFragment;
import com.codetroopers.betterpickers.numberpicker.NumberPickerBuilder;
import com.codetroopers.betterpickers.numberpicker.NumberPickerDialogFragment;
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

public class AddOutcomeActivity extends AppCompatActivity implements DatePickerDialogFragment.DatePickerDialogHandler,NumberPickerDialogFragment.NumberPickerDialogHandler {

    private static final int PREVIEW_REQUEST_CODE = 1;
    private static final int SAVE_REQUEST_CODE = 2;
    private String photoPath;
    private File photoFile;

    private EditText amount;
    private EditText name;
    private AutoCompleteTextView description;
    private Button buttonAdd;
    private EditText date;
    private Toolbar toolbar;
    private ImageView chequeImage;
    private DBHelper dbHelper;

    private int actionType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_outcome);

        toolbar = (Toolbar) findViewById(R.id.add_outcome_toolbar);
        actionType = getIntent().getIntExtra("toolbar_header_text", 1);
        if (toolbar != null) {
            toolbar.setTitle(getIntent().getIntExtra("toolbar_header_text", R.string.default_add_toolbar_name));
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        name = (EditText) findViewById(R.id.add_outcome_name);
        amount = (EditText) findViewById(R.id.add_outcome_amount);
        description = (AutoCompleteTextView) findViewById(R.id.add_outcome_note);
        date = (EditText) findViewById(R.id.add_outcome_date);
        buttonAdd = (Button) findViewById(R.id.add_outcome_save);

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
                        setResult(RESULT_OK, intent);
                        finish();
                        return;
                    case R.string.outcome_toolbar_name:
                        try {
                            addOutcome();
                        } catch (SQLException ex) {
                        }
                        intent = new Intent(getApplicationContext(), MainActivity.class);
                        setResult(RESULT_OK, intent);
                        finish();
                        return;
                }
            }
        });

        initEditText();
    }

    private void initEditText() {
        amount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NumberPickerBuilder npb = new NumberPickerBuilder()
                        .setFragmentManager(getSupportFragmentManager());
                npb.show();
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerBuilder dpb = new DatePickerBuilder()
                        .setFragmentManager(getSupportFragmentManager());
                dpb.show();
            }
        });

        amount.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                EditText edittext = (EditText) v;
                int inType = edittext.getInputType();
                edittext.setInputType(InputType.TYPE_NULL);
                edittext.onTouchEvent(event);
                edittext.setInputType(inType);
                return true;
            }
        });

        date.setOnTouchListener(new View.OnTouchListener() {
            @Override public boolean onTouch(View v, MotionEvent event) {
                EditText edittext = (EditText) v;
                int inType = edittext.getInputType();
                edittext.setInputType(InputType.TYPE_NULL);
                edittext.onTouchEvent(event);
                edittext.setInputType(inType);
                return true;
            }
        });
        date.setInputType(date.getInputType() | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        amount.setInputType(amount.getInputType() | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS );
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void addIncome() throws java.sql.SQLException {
        Income income = new Income("", description.getText().toString(), new Date(), Double.parseDouble(amount.getText().toString()), 1);
        dbHelper = MoneyApplication.getInstance().GetDBHelper();
        dbHelper.getIncomeDAO().create(income);
        BusProvider.getInstance().post(new WalletChangeEvent());
    }

    private void addOutcome() throws java.sql.SQLException {
        Outcome outcome = new Outcome(name.getText().toString(), description.getText().toString(), new Date(), Double.parseDouble(amount.getText().toString()), 1);
        dbHelper = MoneyApplication.getInstance().GetDBHelper();
        dbHelper.getOutcomeDAO().create(outcome);
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

    @Override
    public void onDialogDateSet(int reference, int year, int monthOfYear, int dayOfMonth) {
        date.setText(dayOfMonth+"-"+monthOfYear+"-"+year);
    }

    @Override
    public void onDialogNumberSet(int reference, int number, double decimal, boolean isNegative, double fullNumber) {
        amount.setText(String.valueOf(fullNumber));
    }

}
