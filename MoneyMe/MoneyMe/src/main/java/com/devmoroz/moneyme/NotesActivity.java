package com.devmoroz.moneyme;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.devmoroz.moneyme.adapters.TodosAdapter;
import com.devmoroz.moneyme.helpers.DBHelper;
import com.devmoroz.moneyme.logging.L;
import com.devmoroz.moneyme.models.Todo;
import com.devmoroz.moneyme.utils.Constants;
import com.devmoroz.moneyme.widgets.EmptyRecyclerView;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class NotesActivity extends AppCompatActivity {

    private static final int REQUEST_TODO_ITEM = 101281;

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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.todos_toolbar);
        setSupportActionBar(mToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
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
            public void onModelClick(View view, Todo todo, int position) {

            }

            @Override
            public void onDeleteClick(Todo todo, int position) {

            }
        });

        recyclerView.setAdapter(mAdapter);
        mAdapter.setList(todos);
    }

    private void loadTodos() {
        try {
            DBHelper dbHelper = MoneyApplication.getInstance().GetDBHelper();
            todos = dbHelper.getTodoDAO().queryForAllSorted();
        } catch (SQLException ex) {
            L.T(NotesActivity.this, "Something went wrong. Please, try again.");
        }
    }

    private void addEditTodo(Todo mTodo) {
        Intent intentTodo = new Intent(NotesActivity.this, TodoActivity.class);
        Todo item = new Todo("", "", new Date(), false);

        intentTodo.putExtra(Constants.EXTRA_TODO_ITEM, mTodo != null ? mTodo : item);
        startActivityForResult(intentTodo, REQUEST_TODO_ITEM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_CANCELED && requestCode == REQUEST_TODO_ITEM) {
            Todo item = data.getParcelableExtra(Constants.EXTRA_TODO_ITEM);

            boolean existed = false;

            if (item.isHasReminder()) {
            }

            loadTodos();
            mAdapter.setList(todos);
        } else {
            loadTodos();
            mAdapter.setList(todos);
        }
    }
}
