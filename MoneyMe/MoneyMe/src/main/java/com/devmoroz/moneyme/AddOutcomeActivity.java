package com.devmoroz.moneyme;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.devmoroz.moneyme.adapters.CategorySpinnerWithIconsAdapter;
import com.devmoroz.moneyme.helpers.DBHelper;
import com.devmoroz.moneyme.helpers.GoogleApiHelper;
import com.devmoroz.moneyme.helpers.PermissionsHelper;
import com.devmoroz.moneyme.logging.L;
import com.devmoroz.moneyme.models.Account;
import com.devmoroz.moneyme.models.Category;
import com.devmoroz.moneyme.models.CreatedItem;
import com.devmoroz.moneyme.models.Currency;
import com.devmoroz.moneyme.models.Tag;
import com.devmoroz.moneyme.models.Location;
import com.devmoroz.moneyme.models.Transaction;
import com.devmoroz.moneyme.models.TransactionEdit;
import com.devmoroz.moneyme.models.TransactionType;
import com.devmoroz.moneyme.utils.Constants;
import com.devmoroz.moneyme.utils.CurrencyCache;
import com.devmoroz.moneyme.utils.CustomColorTemplate;
import com.devmoroz.moneyme.utils.FormatUtils;
import com.devmoroz.moneyme.utils.ThemeUtils;
import com.devmoroz.moneyme.utils.datetime.TimeUtils;
import com.devmoroz.moneyme.widgets.CalculatorDialog;
import com.devmoroz.moneyme.widgets.DecimalDigitsInputFilter;
import com.devmoroz.moneyme.widgets.PlacesAutoCompleteAdapter;
import com.devmoroz.moneyme.widgets.TextBackgroundSpan;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.devmoroz.moneyme.utils.PhotoUtil.extractImageUrlFromGallery;
import static com.devmoroz.moneyme.utils.PhotoUtil.setImageWithGlide;

public class AddOutcomeActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int FROM_GALLERY_REQUEST_CODE = 1;
    private static final int SAVE_REQUEST_CODE = 2;
    private static final int TAGS_REQUEST_CODE = 3;

    String defaultCategory;
    private String photoFileName;
    private static TransactionEdit transactionEdit;

    private EditText amount;
    private EditText description;
    private TextInputLayout floatingAmountLabel;
    private Button date;
    private Button locationButton;
    private Button tagsButton;
    private Spinner categorySpin;
    private ImageView categoryColor;
    private Spinner accountSpin;
    private ViewGroup ll_album;
    private View add;

    private Toolbar toolbar;
    private ImageView chequeImage;
    private ImageView imageDeletePhoto;

    protected GoogleApiClient mGoogleApiClient;
    private GoogleApiHelper mGoogleApiHelper;
    private PlacesAutoCompleteAdapter mAdapter;


    private DBHelper dbHelper;
    private List<Category> categories;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        defaultCategory = intent.getStringExtra(Constants.OUTCOME_DEFAULT_CATEGORY);
        setTheme(R.style.AppDefaultOutcome);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_outcome);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        mGoogleApiHelper = new GoogleApiHelper(this, mGoogleApiClient);


        toolbar = (Toolbar) findViewById(R.id.add_outcome_toolbar);


        amount = (EditText) findViewById(R.id.add_outcome_amount);
        amount.setFilters(new InputFilter[]{new DecimalDigitsInputFilter()});
        description = (EditText) findViewById(R.id.add_outcome_note);
        date = (Button) findViewById(R.id.add_outcome_date);
        tagsButton = (Button) findViewById(R.id.add_outcome_tags);
        locationButton = (Button) findViewById(R.id.add_outcome_location);
        categorySpin = (Spinner) findViewById(R.id.add_outcome_category);
        categoryColor = (ImageView) findViewById(R.id.add_outcome_category_color);
        accountSpin = (Spinner) findViewById(R.id.add_outcome_account);
        date.setText(TimeUtils.formatShortDate(getApplicationContext(), new Date()));
        floatingAmountLabel = (TextInputLayout) findViewById(R.id.text_input_layout_out_amount);
        ll_album = (ViewGroup) findViewById(R.id.ll_album);
        add = View.inflate(this, R.layout.item_photo, null);
        chequeImage = (ImageView) add.findViewById(R.id.add_outcome_cheque);
        imageDeletePhoto = (ImageView) add.findViewById(R.id.delete_outcome_cheque);
        ll_album.addView(add);

        if (savedInstanceState == null) {
            transactionEdit = new TransactionEdit();
        } else {
            transactionEdit = savedInstanceState.getParcelable(Constants.STATE_TRANSACTION_EDIT);
        }

        initToolbar();
        initCategorySpinner();
        initAccountSpinner();
        setupFloatingLabelError();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (transactionEdit != null) {
            outState.putParcelable(Constants.STATE_TRANSACTION_EDIT, transactionEdit);
        }
        super.onSaveInstanceState(outState);
    }

    private void saveNewOutcomeTransaction() {
        if (amount.getText().toString().isEmpty()) {
            floatingAmountLabel.setError(getString(R.string.outcome_amount_required));
            floatingAmountLabel.setErrorEnabled(true);
            return;
        }
        Intent intent;
        CreatedItem info = new CreatedItem("", "", 0, "");
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
        try {
            dbHelper = MoneyApplication.getInstance().GetDBHelper();
            categories = dbHelper.getCategoryDAO().getSortedCategories();
        } catch (SQLException ex) {

        }
        String[] defaults = getResources().getStringArray(R.array.outcome_categories);
        CategorySpinnerWithIconsAdapter adapter = new CategorySpinnerWithIconsAdapter(this, R.layout.category_row, defaults, categories);
        categorySpin.setAdapter(adapter);
        categoryColor.setColorFilter(categories.get(0).getColor());
        if (FormatUtils.isNotEmpty(defaultCategory)) {
            for (Category category : categories) {
                if (defaultCategory.equals(category.getTitle())) {
                    categoryColor.setColorFilter(category.getColor());
                    categorySpin.setSelection(category.getOrder());
                    break;
                }
            }
        }
        categorySpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position <= CustomColorTemplate.PIECHART_COLORS.length) {
                    categoryColor.setColorFilter(CustomColorTemplate.PIECHART_COLORS[position]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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

    public void showDatePickerDialog() {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setDate(date);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private CreatedItem addOutcome() throws java.sql.SQLException {
        dbHelper = MoneyApplication.getInstance().GetDBHelper();
        Date dateAdded;
        String outcomeNote = description.getText().toString();
        double outcomeAmount = Double.parseDouble(amount.getText().toString());
        Category selectedCategory = categories.get(categorySpin.getSelectedItemPosition());

        dateAdded = new Date(transactionEdit.getDate());

        int id = accountSpin.getSelectedItemPosition();
        Account account = dbHelper.getAccountDAO().queryForAll().get(id);
        account.setBalance(account.getBalance() - outcomeAmount);

        Transaction outcome = new Transaction(TransactionType.OUTCOME, outcomeNote, selectedCategory, dateAdded, outcomeAmount, account);
        outcome.setPhoto(transactionEdit.getPhotoPath());

        dbHelper.getTransactionDAO().create(outcome);
        dbHelper.getAccountDAO().update(account);

        return new CreatedItem(outcome.getId(), selectedCategory.getTitle(), outcomeAmount, account.getId());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case (android.R.id.home):
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case (R.id.save_outcome_toolbar):
                saveNewOutcomeTransaction();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addPictureAttachment() {
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
    }

    private void openCalculatorDialog() {
        final MaterialDialog dialog = new MaterialDialog.Builder(AddOutcomeActivity.this)
                .customView(R.layout.dialog_calculator, false).build();
        CalculatorDialog calc = new CalculatorDialog(dialog.getCustomView(), new CalculatorDialog.Callback() {
            @Override
            public void onCloseClick(String result) {
                amount.setText(result);
                amount.setSelection(result.length());
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case FROM_GALLERY_REQUEST_CODE:
                    transactionEdit.setPhotoPath(extractImageUrlFromGallery(this, data));
                    L.appendLog("from gallery");
                    L.appendLog(transactionEdit.getPhotoPath());
                    setPic();
                    break;
                case SAVE_REQUEST_CODE:
                    File f = new File(transactionEdit.getPhotoPath());
                    Uri contentUri = Uri.fromFile(f);
                    L.appendLog("take photo");
                    L.appendLog(transactionEdit.getPhotoPath());
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intent.setData(contentUri);
                    this.sendBroadcast(intent);
                    setPic();
                    break;
                case TAGS_REQUEST_CODE:
                    transactionEdit.setTags(getTagsExtra(data));
                    setTags(transactionEdit.getTags());
                    break;
            }
        }
    }

    private void setTags(List<Tag> tags){
        final int tagBackgroundColor = ThemeUtils.getColor(AddOutcomeActivity.this, R.attr.backgroundColorSecondary);
        final float tagBackgroundRadius = getResources().getDimension(R.dimen.tag_radius);
        if (tags == null) {
            tags = Collections.emptyList();
        }
        final SpannableStringBuilder ssb = new SpannableStringBuilder();
        for (Tag tag : tags) {
            ssb.append(tag.getTitle());
            ssb.setSpan(new TextBackgroundSpan(tagBackgroundColor, tagBackgroundRadius), ssb.length() - tag.getTitle().length(), ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.append(" ");
        }
        tagsButton.setText(ssb);
    }

    private void startTagsActivity(List<Tag> selectedTags){
        Intent intent = new Intent(this, TagsActivity.class);
        final Parcelable[] parcelables = new Parcelable[selectedTags.size()];
        int index = 0;
        for (Tag tag : selectedTags) {
            parcelables[index++] = tag;
        }
        intent.putExtra(Constants.EXTRA_SELECTED_TAGS, parcelables);
        startActivityForResult(intent, TAGS_REQUEST_CODE);
    }

    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
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
        File photoDir =
                new File(Environment.getExternalStorageDirectory() + "/MoneyMe/Receipts");
        if (!photoDir.exists()) {
            photoDir.mkdirs();
        }
        File image = new File(photoDir, photoFileName);
        transactionEdit.setPhotoPath("file:" + image.getAbsolutePath());
        return image;
    }

    private void setPic() {
        if (FormatUtils.isNotEmpty(transactionEdit.getPhotoPath())) {
            setImageWithGlide(getApplicationContext(), transactionEdit.getPhotoPath(), chequeImage);
            imageDeletePhoto.setVisibility(View.VISIBLE);
        }
    }

    private void removePic() {
        if (chequeImage == null) {
            return;
        }
        if (transactionEdit.getPhotoPath() != null && imageDeletePhoto != null) {
            imageDeletePhoto.setVisibility(View.INVISIBLE);
            photoFileName = null;
            transactionEdit.setPhotoPath(null);
            chequeImage.setImageResource(R.drawable.ic_paperclip);
        }
    }

    private void displayLocationDialog() {
        mGoogleApiHelper.start();
        PermissionsHelper.requestPermission(AddOutcomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION, R.string
                .permission_coarse_location, AddOutcomeActivity.this.findViewById(R.id.coordinator_outcome), () -> {
            LayoutInflater inflater = AddOutcomeActivity.this.getLayoutInflater();
            View v = inflater.inflate(R.layout.dialog_location, null);
            final AutoCompleteTextView autoCompView = (AutoCompleteTextView) v.findViewById(R.id
                    .auto_complete_location);
            autoCompView.setHint(getString(R.string.search_location));
            mAdapter = new PlacesAutoCompleteAdapter(AddOutcomeActivity.this, mGoogleApiClient, null, null);
            autoCompView.setAdapter(mAdapter);
            autoCompView.setOnItemClickListener(mAutocompleteClickListener);

            final MaterialDialog dialog = new MaterialDialog.Builder(AddOutcomeActivity.this)
                    .customView(autoCompView, false)
                    .positiveText(R.string.confirm)
                    .onPositive((materialDialog, dialogAction) -> {
                        if (TextUtils.isEmpty(autoCompView.getText().toString())) {
                            transactionEdit.setLocation(null);
                        } else {
                            locationButton.setText(transactionEdit.getLocation().getName());
                        }
                    })
                    .build();

            autoCompView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }


                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 0) {
                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            dialog.show();
        });

    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);


            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

            Toast.makeText(getApplicationContext(), "Clicked: " + primaryText,
                    Toast.LENGTH_SHORT).show();

        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);
            Location location = new Location();
            location.setId(place.getId());
            location.setName(place.getName().toString());
            location.setLatLng(place.getLatLng());
            location.setAddress(place.getAddress().toString());
            transactionEdit.setLocation(location);

            places.release();
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiHelper.stop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_outcome_date:
                showDatePickerDialog();
                break;
            case R.id.add_outcome_tags:
                startTagsActivity(transactionEdit.getTags() != null ? transactionEdit.getTags() : Collections.<Tag>emptyList());
                break;
            case R.id.add_outcome_location:
                displayLocationDialog();
                break;
            case R.id.add_outcome_cheque:
                addPictureAttachment();
                break;
            case R.id.delete_outcome_cheque:
                removePic();
                break;
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
            transactionEdit.setDate(c.getTime().getTime());

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.DAY_OF_MONTH, day);
            transactionEdit.setDate(cal.getTime().getTime());
            date.setText(TimeUtils.formatShortDate(getContext(), cal.getTime()));
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

    public void showMessage(int messageResId) {
        runOnUiThread(() -> Toast.makeText(this, getString(messageResId) + "", Toast.LENGTH_LONG).show());
    }

    public static List<Tag> getTagsExtra(Intent data) {
        final Parcelable[] parcelables = data.getParcelableArrayExtra(Constants.RESULT_EXTRA_TAGS);
        final List<Tag> tags = new ArrayList<>();
        for (Parcelable parcelable : parcelables) {
            tags.add((Tag) parcelable);
        }
        return tags;
    }

}