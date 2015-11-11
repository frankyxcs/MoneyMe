package com.devmoroz.moneyme;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.devmoroz.moneyme.helpers.DBHelper;
import com.devmoroz.moneyme.logging.L;
import com.devmoroz.moneyme.models.CreatedItem;
import com.devmoroz.moneyme.models.Outcome;
import com.devmoroz.moneyme.utils.Constants;
import com.devmoroz.moneyme.utils.CurrencyCache;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddOutcomeActivity extends AppCompatActivity {

    private static final int PREVIEW_REQUEST_CODE = 1;
    private static final int SAVE_REQUEST_CODE = 2;
    private DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    private String photoPath;
    private File photoFile;


    private EditText amount;
    private EditText description;
    private FloatingActionButton buttonAdd;
    private TextInputLayout floatingAmountLabel;
    private TextView date;
    private Spinner categorySpin;

    private Toolbar toolbar;
    private ImageView chequeImage;


    private DBHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefaultOutcome);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_outcome);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fab_grow);

        toolbar = (Toolbar) findViewById(R.id.add_outcome_toolbar);
        initToolbar();

        amount = (EditText) findViewById(R.id.add_outcome_amount);
        description = (EditText) findViewById(R.id.add_outcome_note);
        date = (TextView) findViewById(R.id.add_outcome_date);
        categorySpin = (Spinner) findViewById(R.id.add_outcome_category);
        date.setText(dateFormat.format(new Date()));
        buttonAdd = (FloatingActionButton) findViewById(R.id.add_outcome_save);
        floatingAmountLabel = (TextInputLayout) findViewById(R.id.text_input_layout_out_amount);
        chequeImage = (ImageView) findViewById(R.id.add_outcome_cheque);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (amount.getText().toString().isEmpty()) {
                    floatingAmountLabel.setError(getString(R.string.outcome_amount_required));
                    floatingAmountLabel.setErrorEnabled(true);
                    return;
                }
                Intent intent;
                CreatedItem info = new CreatedItem(-1, "", 0);
                try {
                    info = addOutcome();
                } catch (SQLException ex) {
                }
                intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra(Constants.CREATED_ITEM_ID, info.getItemId());
                intent.putExtra(Constants.CREATED_ITEM_CATEGORY, info.getCategory());
                intent.putExtra(Constants.CREATED_ITEM_AMOUNT, info.getAmount());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        initCategorySpinner();
        setupFloatingLabelError();
        buttonAdd.startAnimation(animation);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_outcome, menu);
        return true;
    }

    private void initToolbar() {
        if (toolbar != null) {
            toolbar.setTitle(R.string.outcome_toolbar_name);
            toolbar.setTitleTextColor(Color.WHITE);
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.inflateMenu(R.menu.menu_outcome);
        }
    }

    private void initCategorySpinner() {
        final String[] categories = getResources().getStringArray(R.array.transaction_categories);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        categorySpin.setAdapter(adapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void showDatePickerDialog(View v) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setDate(date);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private CreatedItem addOutcome() throws java.sql.SQLException {
        Date dateAdded;
        String outcomeNote = description.getText().toString();
        double outcomeAmount = Double.parseDouble(amount.getText().toString());
        String selectedCategory = categorySpin.getSelectedItem().toString();
        String account = "Наличные";
        try {
            dateAdded = dateFormat.parse(date.getText().toString());
        } catch (ParseException ex) {
            dateAdded = new Date();
        }

        Outcome outcome = new Outcome(outcomeNote, dateAdded, outcomeAmount, selectedCategory, account);
        dbHelper = MoneyApplication.getInstance().GetDBHelper();
        dbHelper.getOutcomeDAO().create(outcome);

        return new CreatedItem(outcome.getId(), selectedCategory, outcomeAmount);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case (android.R.id.home):
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case (R.id.image_outcome_toolbar):
                takePhoto();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PREVIEW_REQUEST_CODE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            chequeImage.setImageBitmap(imageBitmap);
        } else if (requestCode == SAVE_REQUEST_CODE && resultCode == RESULT_OK) {
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(photoFile);
            intent.setData(contentUri);
            this.sendBroadcast(intent);
            setPic();
        }
    }

    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            photoFile = null;
            try {
                photoFile = filename();
            } catch (IOException ex) {
                L.t(getApplicationContext(), "No SD card");
            }
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, SAVE_REQUEST_CODE);
            }
        }
    }

    private File filename() throws IOException {
        String time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String file = time;
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(file, ".jpg", dir);
        photoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = chequeImage.getWidth();
        int targetH = chequeImage.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, bmOptions);
        chequeImage.setImageBitmap(bitmap);
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        private TextView date;

        public void setDate(TextView date) {
            this.date = date;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            month += 1;
            date.setText(day + "-" + month + "-" + year);
        }
    }

    private void setupFloatingLabelError() {
        String currencyName = CurrencyCache.getCurrencyOrEmpty().getName();
        String labelHint = String.format("%s, %s:", getString(R.string.amount), currencyName);
        floatingAmountLabel.setHint(labelHint);
        floatingAmountLabel.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if (text.length() > 0) {
                    floatingAmountLabel.setErrorEnabled(false);
                } else {
                    floatingAmountLabel.setError(getString(R.string.outcome_amount_required));
                    floatingAmountLabel.setErrorEnabled(true);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 0) {
                    floatingAmountLabel.setError(getString(R.string.outcome_amount_required));
                    floatingAmountLabel.setErrorEnabled(true);
                }
            }
        });
    }

}
