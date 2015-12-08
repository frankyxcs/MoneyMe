package com.devmoroz.moneyme;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.devmoroz.moneyme.helpers.DBHelper;
import com.devmoroz.moneyme.logging.L;
import com.devmoroz.moneyme.models.Account;
import com.devmoroz.moneyme.models.CreatedItem;
import com.devmoroz.moneyme.models.Currency;
import com.devmoroz.moneyme.models.Outcome;
import com.devmoroz.moneyme.utils.Constants;
import com.devmoroz.moneyme.utils.CurrencyCache;
import com.devmoroz.moneyme.utils.FormatUtils;
import com.devmoroz.moneyme.utils.datetime.TimeUtils;
import com.devmoroz.moneyme.widgets.DecimalDigitsInputFilter;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.devmoroz.moneyme.utils.PhotoUtil.PICTURES_DIR;
import static com.devmoroz.moneyme.utils.PhotoUtil.extractImageUrlFromGallery;
import static com.devmoroz.moneyme.utils.PhotoUtil.checkExistAndTakePath;
import static com.devmoroz.moneyme.utils.PhotoUtil.setImageWithPicasso;

public class AddOutcomeActivity extends AppCompatActivity {

    private static final int FROM_GALLERY_REQUEST_CODE = 1;
    private static final int SAVE_REQUEST_CODE = 2;

    private String photoFileName;
    private String photoPath;
    private File photoFile;
    private static Date outcomeDate = new Date();

    private EditText amount;
    private EditText description;
    private FloatingActionButton buttonAdd;
    private TextInputLayout floatingAmountLabel;
    private Button date;
    private Spinner categorySpin;
    private Spinner accountSpin;
    private RelativeLayout photoWrapper;

    private Toolbar toolbar;
    private ImageView chequeImage;
    private ImageView imageDeletePhoto;


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
        amount.setFilters(new InputFilter[]{new DecimalDigitsInputFilter()});
        description = (EditText) findViewById(R.id.add_outcome_note);
        date = (Button) findViewById(R.id.add_outcome_date);
        categorySpin = (Spinner) findViewById(R.id.add_outcome_category);
        accountSpin = (Spinner) findViewById(R.id.add_outcome_account);
        date.setText(TimeUtils.formatShortDate(getApplicationContext(), new Date()));
        buttonAdd = (FloatingActionButton) findViewById(R.id.add_outcome_save);
        floatingAmountLabel = (TextInputLayout) findViewById(R.id.text_input_layout_out_amount);
        chequeImage = (ImageView) findViewById(R.id.add_outcome_cheque);
        imageDeletePhoto = (ImageView) findViewById(R.id.delete_outcome_cheque);
        photoWrapper = (RelativeLayout) findViewById(R.id.photoWrapper);
        imageDeletePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removePic();
            }
        });
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (amount.getText().toString().isEmpty()) {
                    floatingAmountLabel.setError(getString(R.string.outcome_amount_required));
                    floatingAmountLabel.setErrorEnabled(true);
                    return;
                }
                Intent intent;
                CreatedItem info = new CreatedItem(-1, "", 0, -1);
                try {
                    info = addOutcome();
                } catch (SQLException ex) {
                    L.t(AddOutcomeActivity.this, "Something went wrong.Please,try again.");
                }
                intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra(Constants.CREATED_ITEM_ID, info.getItemId());
                intent.putExtra(Constants.CREATED_ITEM_CATEGORY, info.getCategory());
                intent.putExtra(Constants.CREATED_ITEM_AMOUNT, info.getAmount());
                intent.putExtra(Constants.CREATED_ITEM_ACCOUNT, info.getAccountId());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        initCategorySpinner();
        initAccountSpinner();
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
        String[] categories = getResources().getStringArray(R.array.transaction_categories);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        categorySpin.setAdapter(adapter);
    }

    private void initAccountSpinner() {
        List<Account> accountList = MoneyApplication.accounts;
        Currency c = CurrencyCache.getCurrencyOrEmpty();
        if (accountList != null) {
            String[] accountsWithBalance = new String[accountList.size()];
            for (int i = 0; i < accountList.size(); i++) {
                Account acc = accountList.get(i);
                accountsWithBalance[i] = FormatUtils.attachAmountToText(acc.getName(), c, acc.getBalance(), false);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, accountsWithBalance);
            accountSpin.setAdapter(adapter);
        }
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
        dbHelper = MoneyApplication.getInstance().GetDBHelper();
        Date dateAdded;
        String outcomeNote = description.getText().toString();
        double outcomeAmount = Double.parseDouble(amount.getText().toString());
        String selectedCategory = categorySpin.getSelectedItem().toString();

        dateAdded = outcomeDate == null ? new Date():outcomeDate;

        int id = accountSpin.getSelectedItemPosition();
        Account account = dbHelper.getAccountDAO().queryForAll().get(id);
        account.setBalance(account.getBalance() - outcomeAmount);

        Outcome outcome = new Outcome(outcomeNote, dateAdded, outcomeAmount, selectedCategory, account);
        outcome.setPhoto(photoPath);

        dbHelper.getOutcomeDAO().create(outcome);
        dbHelper.getAccountDAO().update(account);

        return new CreatedItem(outcome.getId(), selectedCategory, outcomeAmount, account.getId());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case (android.R.id.home):
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case (R.id.image_outcome_toolbar):
                final String[] items = new String[]{getString(R.string.new_picture),
                        getString(R.string.select_from_gallery)};

                new MaterialDialog.Builder(this)
                        .title(R.string.attach_picture)
                        .items(items)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                switch (which) {
                                    case (0):
                                        dialog.dismiss();
                                        takePhoto();
                                        break;
                                    case (1): {
                                        dialog.dismiss();
                                        chosePhotoFromGallery();
                                        break;
                                    }
                                }
                            }
                        })
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == FROM_GALLERY_REQUEST_CODE) {
                photoPath = extractImageUrlFromGallery(this, data);
            } else if (requestCode == SAVE_REQUEST_CODE) {
                photoPath = checkExistAndTakePath(photoFileName);
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(photoFile);
                intent.setData(contentUri);
                this.sendBroadcast(intent);
            }
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

    private void chosePhotoFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, getString(R.string.choose_photo)), FROM_GALLERY_REQUEST_CODE);
    }

    private File filename() throws IOException {
        String time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        photoFileName = time + ".jpg";
        return new File(PICTURES_DIR, photoFileName);
    }

    private void setPic() {
        if (FormatUtils.isNotEmpty(photoPath)) {
            setImageWithPicasso(getApplicationContext(), photoPath, chequeImage);
            photoWrapper.setVisibility(View.VISIBLE);
        }
    }

    private void removePic() {
        if (chequeImage == null) {
            return;
        }
        if (photoPath != null && photoWrapper != null) {
            photoWrapper.setVisibility(View.GONE);
            photoFileName = null;
            photoPath = null;
            chequeImage.setImageBitmap(null);
        }
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        private Button date;

        public void setDate(Button date) {
            this.date = date;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH,month);
            cal.set(Calendar.DAY_OF_MONTH, day);
            outcomeDate = cal.getTime();
            date.setText(TimeUtils.formatShortDate(getContext(),outcomeDate));
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
