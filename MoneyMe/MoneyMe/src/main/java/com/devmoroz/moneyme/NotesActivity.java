package com.devmoroz.moneyme;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.devmoroz.moneyme.adapters.TodosAdapter;
import com.devmoroz.moneyme.helpers.DBHelper;
import com.devmoroz.moneyme.logging.L;
import com.devmoroz.moneyme.models.Todo;
import com.devmoroz.moneyme.notification.MoneyMeScheduler;
import com.devmoroz.moneyme.utils.Constants;
import com.devmoroz.moneyme.widgets.EmptyRecyclerView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class NotesActivity extends AppCompatActivity {

    private static final int REQUEST_TODO_ITEM = 1012;

    private EmptyRecyclerView recyclerView;
    private LinearLayout mStateEmpty;
    private TodosAdapter mAdapter;

    private List<Todo> todos = Collections.emptyList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        initToolbar();
        loadTodos();
        initRecyclerWithAdapter();
        handleIntents();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_new_note:
                addEditTodo(null);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notes, menu);
        return true;
    }

    private void initToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.todos_toolbar);
        setSupportActionBar(mToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initRecyclerWithAdapter() {
        mStateEmpty = (LinearLayout) findViewById(R.id.todosEmpty);
        recyclerView = (EmptyRecyclerView) findViewById(R.id.todoRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setEmptyView(mStateEmpty);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        mAdapter = new TodosAdapter(NotesActivity.this, todos, new TodosAdapter.OnItemClickListener() {
            @Override
            public void onModelClick(Todo todo) {
                addEditTodo(todo);
            }

            @Override
            public void onDeleteClick(Todo todo, int position) {
                deleteTodo(todo,position);
            }

            @Override
            public void onShareClick(Todo todo) {
                shareTodo(todo);
            }
        });

        recyclerView.setAdapter(mAdapter);
        mAdapter.setList(todos);
    }

    private void handleIntents(){
        Intent i = getIntent();

        if (i.getAction() == null) return;

        if (Constants.ACTION_LAUNCH_TODO.equals(i.getAction())) {
            addEditTodo(null);
            return;
        }

    }

    private void loadTodos() {
        try {
            DBHelper dbHelper = MoneyApplication.GetDBHelper();
            todos = dbHelper.getTodoDAO().queryForAllSorted();
        } catch (SQLException ex) {
            L.T(NotesActivity.this, "Something went wrong. Please, try again.");
        }
    }

    private void deleteTodo(Todo todo, int position) {
        MoneyMeScheduler scheduler = new MoneyMeScheduler();
        new MaterialDialog.Builder(NotesActivity.this)
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
                            dbHelper.getTodoDAO().delete(todo);
                            mAdapter.remove(position);
                            scheduler.removeReminder(MoneyApplication.getAppContext(), todo);
                        } catch (SQLException ex) {
                            L.T(NotesActivity.this, "Something went wrong.Please, try again.");
                        }
                    }
                })
                .show();
    }

    private void addEditTodo(Todo mTodo) {
        Intent intentTodo = new Intent(this, TodoActivity.class);
        Todo item = new Todo("", "", new Date(), false, false);

        intentTodo.putExtra(Constants.EXTRA_TODO_ITEM, mTodo != null ? mTodo : item);
        startActivityForResult(intentTodo, REQUEST_TODO_ITEM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        loadTodos();
        mAdapter.setList(todos);
    }

    private void shareTodo(Todo todo) {

        String titleText = todo.getTitle();

        String contentText = titleText
                + System.getProperty("line.separator")
                + todo.getContent();

        Intent shareIntent = new Intent();

        // Prepare sharing intent with only text
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");

        shareIntent.putExtra(Intent.EXTRA_SUBJECT, titleText);
        shareIntent.putExtra(Intent.EXTRA_TEXT, contentText);

        startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.share_chooser_message)));
    }
}
