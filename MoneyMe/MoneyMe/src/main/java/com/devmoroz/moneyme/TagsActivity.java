package com.devmoroz.moneyme;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.devmoroz.moneyme.adapters.TagsAdapter;
import com.devmoroz.moneyme.helpers.DBHelper;
import com.devmoroz.moneyme.models.Tag;
import com.devmoroz.moneyme.utils.Constants;
import com.devmoroz.moneyme.widgets.DividerItemDecoration;
import com.devmoroz.moneyme.widgets.EmptyRecyclerView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class TagsActivity extends AppCompatActivity {

    private EmptyRecyclerView recyclerView;
    private LinearLayout mStateEmpty;
    private TagsAdapter mAdapter;
    private List<Tag> tags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags);

        final Parcelable[] selectedTags = getIntent().getParcelableArrayExtra(Constants.EXTRA_SELECTED_TAGS);

        final Set<Tag> selectedTagSet = new HashSet<>();
        final Parcelable[] parcelables;
        if (savedInstanceState == null) {
            parcelables = selectedTags;
        } else {
            final List<Parcelable> parcelableList = savedInstanceState.getParcelableArrayList(Constants.STATE_SELECTED_TAGS);
            parcelables = new Parcelable[parcelableList.size()];
            parcelableList.toArray(parcelables);
        }
        if (parcelables != null) {
            for (Parcelable parcelable : parcelables) {
                selectedTagSet.add((Tag) parcelable);
            }
        }
        initToolbar();
        initEditButtons();
        initRecyclerWithAdapter(selectedTagSet);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tags_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initEditButtons() {
        View editButtonsContainerView = findViewById(R.id.editButtonsContainerView);
        editButtonsContainerView.setVisibility(View.VISIBLE);
        Button saveButton = (Button)editButtonsContainerView.findViewById(R.id.saveButton);
        Button cancelButton = (Button)editButtonsContainerView.findViewById(R.id.cancelButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMultipleTagsSelected(mAdapter.getSelectedTags());
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTagsSelectCanceled();
            }
        });
    }

    private void initRecyclerWithAdapter(Set<Tag> selectedTagSet) {
        mStateEmpty = (LinearLayout) findViewById(R.id.tagsEmpty);
        recyclerView = (EmptyRecyclerView) findViewById(R.id.tagsRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setEmptyView(mStateEmpty);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mAdapter = new TagsAdapter((view, tag, position, isSelected) -> mAdapter.toggleTagSelected(tag, position));
        tags = getAllTags();
        recyclerView.setAdapter(mAdapter);
        mAdapter.setSelectedTags(selectedTagSet);
        mAdapter.setTags(tags);
    }

    private List<Tag> getAllTags() {
        List<Tag> tags = Collections.emptyList();
        DBHelper dbHelper = MoneyApplication.getInstance().GetDBHelper();
        try {
            tags = dbHelper.getTagDAO().getSortedTags();
            return tags;
        } catch (SQLException ex) {
            return tags;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tags, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new_tag:
                showAddTagDialog();
                return true;
            case android.R.id.home:
                onTagsSelectCanceled();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putParcelableArrayList(Constants.STATE_SELECTED_TAGS, new ArrayList<Parcelable>(mAdapter.getSelectedTags()));
    }

    private void addNewTag(String title) {
        DBHelper dbHelper = MoneyApplication.getInstance().GetDBHelper();
        try {
            Tag tag = new Tag(title);
            if (!dbHelper.getTagDAO().createIfNotExist(tag)) {
                showMessage(R.string.tag_with_current_name_exist);
            } else {
                updateRecycler();
            }
        } catch (SQLException ex) {

        }
    }

    private void showAddTagDialog(){
        new MaterialDialog.Builder(this)
                .title(R.string.add_tag_dialog_title)
                .inputRangeRes(1, 15, R.color.holo_red_dark)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .negativeText(R.string.cancel)
                .positiveText(R.string.save)
                .positiveColorRes(R.color.colorPrimary)
                .negativeColorRes(R.color.colorPrimary)
                .input(null, null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        // Do something
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        String tagTitle = materialDialog.getInputEditText().getText().toString();
                        addNewTag(tagTitle);
                    }
                })
                .show();
    }

    private void onMultipleTagsSelected(Set<Tag> selectedTags) {
        final Intent data = new Intent();
        final Parcelable[] parcelables = new Parcelable[selectedTags.size()];
        int index = 0;
        for (Tag tag : selectedTags) {
            parcelables[index++] = tag;
        }
        data.putExtra(Constants.RESULT_EXTRA_TAGS, parcelables);
        setResult(Activity.RESULT_OK, data);
        finish();
        overridePendingTransition(R.anim.activity_open_scale,R.anim.activity_close_translate);
    }

    private void onTagsSelectCanceled() {
        setResult(Activity.RESULT_CANCELED);
        finish();
        overridePendingTransition(R.anim.activity_open_scale,R.anim.activity_close_translate);
    }

    private void updateRecycler() {
        tags = getAllTags();
        mAdapter.setTags(tags);
    }

    public void showMessage(int messageResId) {
        runOnUiThread(() -> Toast.makeText(this, getString(messageResId) + "", Toast.LENGTH_LONG).show());
    }
}
