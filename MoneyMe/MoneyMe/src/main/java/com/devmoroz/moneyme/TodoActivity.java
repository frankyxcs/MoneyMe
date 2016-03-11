package com.devmoroz.moneyme;


import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.devmoroz.moneyme.adapters.ColorChooserDialogAdapter;
import com.devmoroz.moneyme.fragments.custom.DatePickerFragment;
import com.devmoroz.moneyme.fragments.custom.TimePickerFragment;
import com.devmoroz.moneyme.helpers.DBHelper;
import com.devmoroz.moneyme.logging.L;
import com.devmoroz.moneyme.models.Todo;
import com.devmoroz.moneyme.notification.MoneyMeScheduler;
import com.devmoroz.moneyme.utils.Constants;
import com.devmoroz.moneyme.utils.CustomColorTemplate;
import com.devmoroz.moneyme.utils.FormatUtils;
import com.devmoroz.moneyme.utils.datetime.TimeUtils;

import java.sql.SQLException;
import java.util.Date;

import it.feio.android.checklistview.ChecklistManager;
import it.feio.android.checklistview.exceptions.ViewNotSupportedException;
import it.feio.android.checklistview.interfaces.CheckListChangedListener;
import it.feio.android.checklistview.utils.AlphaManager;

public class TodoActivity extends AppCompatActivity implements View.OnClickListener, CheckListChangedListener {

    private EditText mTodoTitleEditText;
    private EditText mTodoContentEditText;

    private SwitchCompat mTodoReminderSwitch;
    private LinearLayout mTodoDateButtonsContainer;
    private TextView mDateTextView;
    private Button mDateButton;
    private Button mTimeButton;
    private View colorView;

    private FloatingActionButton fab;

    private Todo editTodo;
    private ChecklistManager mChecklistManager;
    View toggleChecklistView;
    ScrollView scrollView;
    private int contentLineCounter = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        Todo intentTodo = getIntent().getParcelableExtra(Constants.EXTRA_TODO_ITEM);
        Todo parcelableTodo;
        if (savedInstanceState == null) {
            editTodo = intentTodo;
        } else {
            parcelableTodo = savedInstanceState.getParcelable(Constants.STATE_TODO_ITEM);
            if (parcelableTodo != null) {
                editTodo = parcelableTodo;
            }
        }

        mTodoTitleEditText = (EditText) findViewById(R.id.toDoTitle);
        mTodoContentEditText = (EditText) findViewById(R.id.toDoContent);
        colorView = findViewById(R.id.todo_color_marker);

        mTodoReminderSwitch = (SwitchCompat) findViewById(R.id.toDoHasDateSwitch);
        mTodoDateButtonsContainer = (LinearLayout) findViewById(R.id.toDoDateLinearLayout);
        mDateTextView = (TextView) findViewById(R.id.toDoDateTimeTextView);
        mDateButton = (Button) findViewById(R.id.toDoDateButton);
        mTimeButton = (Button) findViewById(R.id.toDoTimeButton);
        fab = (FloatingActionButton) findViewById(R.id.saveToDoFAB);
        scrollView = (ScrollView) findViewById(R.id.content_container);

        initToolbar();
        initStartState();
        initTextWatchersAndListeners();
    }

    private void initTextWatchersAndListeners() {
        fab.setOnClickListener(this);
        mDateButton.setOnClickListener(this);
        mTimeButton.setOnClickListener(this);

        mTodoReminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editTodo.setHasReminder(isChecked);
                animateDateButtonsConatinerVisibility(isChecked);
            }
        });
        mTodoTitleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editTodo.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initStartState() {
        mTodoTitleEditText.setText(editTodo.getTitle());
        initContent();
        setTodoColor();
        mTodoReminderSwitch.setChecked(editTodo.isHasReminder());
        setDateTimeText();
        setDateTimeButtonsText();
        toggleDateButtonsContainerVisibility(editTodo.isHasReminder());
    }

    private void initContent() {
        mTodoContentEditText.setText(editTodo.getContent());
        toggleChecklistView = mTodoContentEditText;
        if (editTodo.isCheckList()) {
            editTodo.setCheckList(false);
            AlphaManager.setAlpha(toggleChecklistView, 0);
            toggleChecklist2(true, true);
        }
    }

    private void setDateTimeButtonsText() {
        if (editTodo.isHasReminder()) {
            mDateButton.setText(TimeUtils.formatShortDate(TodoActivity.this, editTodo.getAlarmDateLong()));
            mTimeButton.setText(TimeUtils.formatShortTime(TodoActivity.this, editTodo.getAlarmDateLong()));
        } else {
            long now = System.currentTimeMillis();
            mDateButton.setText(TimeUtils.formatShortDate(TodoActivity.this, now));
            mTimeButton.setText(TimeUtils.formatShortTime(TodoActivity.this, now));
        }
    }

    private void setTodoColor() {
        if (editTodo.getColor() <= CustomColorTemplate.TODO_COLORS.length) {
            colorView.setBackgroundColor(CustomColorTemplate.TODO_COLORS[editTodo.getColor()]);
        } else {
            colorView.setBackgroundColor(CustomColorTemplate.TODO_COLORS[0]);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_todo, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_checklist_on).setVisible(!editTodo.isCheckList());
        menu.findItem(R.id.menu_checklist_off).setVisible(editTodo.isCheckList());

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_remove_todo:
                removeTodo();
                return true;
            case R.id.change_todo_color:
                showColorPickerDialog();
                return true;
            case R.id.menu_checklist_on:
                toggleChecklist();
                return true;
            case R.id.menu_checklist_off:
                toggleChecklist();
                return true;
            case android.R.id.home:
                makeResult(RESULT_CANCELED);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void removeTodo() {
        MoneyMeScheduler scheduler = new MoneyMeScheduler();
        if (FormatUtils.isNotEmpty(editTodo.getId())) {
            new MaterialDialog.Builder(TodoActivity.this)
                    .content(R.string.remove_item_confirm)
                    .negativeText(R.string.cancel)
                    .positiveText(R.string.remove)
                    .positiveColorRes(R.color.colorPrimary)
                    .negativeColorRes(R.color.colorPrimary)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                            try {
                                DBHelper dbHelper = MoneyApplication.GetDBHelper();
                                dbHelper.getTodoDAO().delete(editTodo);
                                scheduler.removeReminder(MoneyApplication.getAppContext(), editTodo);
                            } catch (SQLException ex) {
                                L.T(TodoActivity.this, "Something went wrong.Please, try again.");
                            }
                            makeResult(RESULT_CANCELED);
                        }
                    })
                    .show();

        } else {
            makeResult(RESULT_CANCELED);
        }
    }

    @Override
    public void onBackPressed() {
        makeResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putParcelable(Constants.STATE_TODO_ITEM, editTodo);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.todo_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setDateTimeText() {
        if (FormatUtils.isNotEmpty(editTodo.getId())) {
            String text = getString(R.string.last_update) + TimeUtils.formatHumanFriendlyShortDateTime(TodoActivity.this, editTodo.getUpdatedDateLong());
            mDateTextView.setText(text);
            mDateTextView.setVisibility(View.VISIBLE);
        } else {
            mDateTextView.setVisibility(View.INVISIBLE);
        }
    }

    public void showDatePickerDialog() {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setDateButton(mDateButton);
        newFragment.setCallback(new DatePickerFragment.Callback() {
            @Override
            public void onDateSet(Date date) {
                editTodo.setAlarmDate(date);
            }
        });
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog() {
        TimePickerFragment newFragment = new TimePickerFragment();
        newFragment.setTimeButton(mTimeButton);
        newFragment.setCallback(new TimePickerFragment.Callback() {
            @Override
            public void onTimeSet(Date date) {
                editTodo.setAlarmDate(date);
            }
        });
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    private void showColorPickerDialog() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(TodoActivity.this);
        builder.title(R.string.choose_todo_color);
        ColorChooserDialogAdapter adapter = new ColorChooserDialogAdapter(TodoActivity.this, R.layout.dialog_colors_item, CustomColorTemplate.TODO_COLORS);
        adapter.setCheckItem(editTodo.getColor());

        GridView gridView = (GridView) LayoutInflater.from(TodoActivity.this).inflate(R.layout.dialog_colors_layout, null);
        gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        gridView.setCacheColorHint(0);
        gridView.setAdapter(adapter);
        builder.customView(gridView, false);

        final MaterialDialog dialog = builder.show();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.dismiss();
                editTodo.setColor(position);
                setTodoColor();
            }
        });
    }

    public void makeResult(int result) {
        final Intent i = new Intent();
        if (result != RESULT_CANCELED) {
            try {
                long now = System.currentTimeMillis();
                if (FormatUtils.isEmpty(editTodo.getId())) {
                    editTodo.setCreatedDateLong(now);
                }
                editTodo.setUpdatedDateLong(now);
                editTodo.setContent(getNoteContent());
                DBHelper dbHelper = MoneyApplication.GetDBHelper();
                dbHelper.getTodoDAO().createOrUpdate(editTodo);
                if (editTodo.isHasReminder()) {
                    MoneyMeScheduler scheduler = new MoneyMeScheduler();
                    scheduler.removeReminder(MoneyApplication.getAppContext(), editTodo);
                    scheduler.scheduleTodoAlarm(MoneyApplication.getAppContext(), editTodo, now + 1000);
                }
            } catch (SQLException ex) {

            }
            i.putExtra(Constants.EXTRA_TODO_ITEM, editTodo);
        }
        setResult(result, i);
        finish();
    }

    public void toggleDateButtonsContainerVisibility(boolean checked) {
        if (checked) {
            mTodoDateButtonsContainer.setVisibility(View.VISIBLE);
        } else {
            mTodoDateButtonsContainer.setVisibility(View.INVISIBLE);
        }
    }

    public void animateDateButtonsConatinerVisibility(boolean checked) {
        if (checked) {
            mTodoDateButtonsContainer.animate().alpha(1.0f).setDuration(300).setListener(
                    new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            mTodoDateButtonsContainer.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mTodoDateButtonsContainer.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    }
            );
        } else {
            mTodoDateButtonsContainer.animate().alpha(0.0f).setDuration(300).setListener(
                    new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            mTodoDateButtonsContainer.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mTodoDateButtonsContainer.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    }
            );
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toDoDateButton:
                showDatePickerDialog();
                break;
            case R.id.toDoTimeButton:
                showTimePickerDialog();
                break;
            case R.id.saveToDoFAB:
                if (FormatUtils.isEmpty(mTodoTitleEditText)) {
                    Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
                    mTodoTitleEditText.startAnimation(shake);
                } else {
                    makeResult(RESULT_OK);
                }
                break;
        }
    }

    private void toggleChecklist() {
        if (!editTodo.isCheckList()) {
            toggleChecklist2(true, true);
            return;
        }

        if (mChecklistManager.getCheckedCount() == 0) {
            toggleChecklist2(true, false);
            return;
        }

        toggleChecklist2(true, true);
    }

    private void toggleChecklist2(final boolean keepChecked, final boolean showChecks) {

        mChecklistManager = ChecklistManager.getInstance(TodoActivity.this);
        mChecklistManager.setMoveCheckedOnBottom(0);
        mChecklistManager.setNewEntryHint(getString(R.string.checklist_item_hint));


        mChecklistManager.setKeepChecked(keepChecked);
        mChecklistManager.setShowChecks(showChecks);
        mChecklistManager.setCheckListChangedListener(this);


        mChecklistManager.setDragVibrationEnabled(false);

        View newView = null;
        try {
            newView = mChecklistManager.convert(toggleChecklistView);
        } catch (ViewNotSupportedException e) {

        }

        // Switches the views
        if (newView != null) {
            if (newView instanceof EditText) {
                ((android.widget.EditText) newView).setHint(R.string.content);
            }
            mChecklistManager.replaceViews(toggleChecklistView, newView);
            toggleChecklistView = newView;
            editTodo.setCheckList(!editTodo.isCheckList());
        }
    }

    private String getNoteContent() {
        String contentText = "";
        if (!editTodo.isCheckList()) {
            contentText = ((android.widget.EditText) toggleChecklistView).getText().toString();
        } else {
            if (mChecklistManager != null) {
                mChecklistManager.setKeepChecked(true);
                mChecklistManager.setShowChecks(true);
                contentText = mChecklistManager.getText();
            }
        }
        return contentText;
    }

    @Override
    public void onCheckListChanged() {
        scrollContent();
    }


    private void scrollContent() {
        if (editTodo.isCheckList()) {
            if (mChecklistManager.getCount() > contentLineCounter) {
                scrollView.scrollBy(0, 60);
            }
            contentLineCounter = mChecklistManager.getCount();
        } else {
            if (mTodoContentEditText.getLineCount() > contentLineCounter) {
                scrollView.scrollBy(0, 60);
            }
            contentLineCounter = mTodoContentEditText.getLineCount();
        }
    }
}
