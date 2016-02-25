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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.devmoroz.moneyme.adapters.ColorChooserDialogAdapter;
import com.devmoroz.moneyme.fragments.custom.DatePickerFragment;
import com.devmoroz.moneyme.fragments.custom.TimePickerFragment;
import com.devmoroz.moneyme.helpers.DBHelper;
import com.devmoroz.moneyme.logging.L;
import com.devmoroz.moneyme.models.Todo;
import com.devmoroz.moneyme.utils.Constants;
import com.devmoroz.moneyme.utils.CustomColorTemplate;
import com.devmoroz.moneyme.utils.FormatUtils;
import com.devmoroz.moneyme.utils.datetime.TimeUtils;

import java.sql.SQLException;
import java.util.Date;

public class TodoActivity extends AppCompatActivity implements View.OnClickListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        final Todo intentTodo = getIntent().getParcelableExtra(Constants.EXTRA_TODO_ITEM);
        final Todo parcelableTodo;
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
        mTodoContentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editTodo.setContent(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initStartState() {
        mTodoTitleEditText.setText(editTodo.getTitle());
        mTodoContentEditText.setText(editTodo.getContent());
        setTodoColor();
        mTodoReminderSwitch.setChecked(editTodo.isHasReminder());
        setDateTimeText();
        mDateButton.setText(TimeUtils.formatShortDate(TodoActivity.this, editTodo.getDateLong()));
        mTimeButton.setText(TimeUtils.formatShortTime(TodoActivity.this, editTodo.getDateLong()));
        if (editTodo.isHasReminder()) {
            toggleDateButtonsConatinerVisibility(true);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_remove_todo:
                removeTodo();
                return true;
            case R.id.change_todo_color:
                showColorPickerDialog();
                return true;
            case android.R.id.home:
                makeResult(RESULT_CANCELED);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void removeTodo() {
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
                                DBHelper dbHelper = MoneyApplication.getInstance().GetDBHelper();
                                dbHelper.getTodoDAO().delete(editTodo);
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
        makeResult(RESULT_OK);
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
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setDateTimeText() {
        mDateTextView.setText(TimeUtils.formatHumanFriendlyShortDateTime(TodoActivity.this, editTodo.getDateLong()));
    }

    public void showDatePickerDialog() {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setDateButton(mDateButton);
        newFragment.setCallback(new DatePickerFragment.Callback() {
            @Override
            public void onDateSet(Date date) {
                editTodo.setDate(date);
                setDateTimeText();
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
                editTodo.setDate(date);
                setDateTimeText();
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
        if (editTodo.getTitle().isEmpty()) {

        } else {
            Intent i = new Intent();
            i.putExtra(Constants.EXTRA_TODO_ITEM, editTodo);
            setResult(result, i);
            finish();
        }
    }

    public void toggleDateButtonsConatinerVisibility(boolean checked) {
        if (checked) {
            mTodoDateButtonsContainer.setVisibility(View.VISIBLE);
        } else {
            mTodoDateButtonsContainer.setVisibility(View.INVISIBLE);
        }
    }

    public void animateDateButtonsConatinerVisibility(boolean checked) {
        if (checked) {
            mTodoDateButtonsContainer.animate().alpha(1.0f).setDuration(500).setListener(
                    new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            mTodoDateButtonsContainer.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
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
            mTodoDateButtonsContainer.animate().alpha(0.0f).setDuration(500).setListener(
                    new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

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
                makeResult(RESULT_OK);
                break;
        }
    }
}
