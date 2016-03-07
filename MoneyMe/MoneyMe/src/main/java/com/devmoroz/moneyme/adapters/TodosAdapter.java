package com.devmoroz.moneyme.adapters;


import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.devmoroz.moneyme.R;
import com.devmoroz.moneyme.models.Todo;
import com.devmoroz.moneyme.utils.CustomColorTemplate;
import com.devmoroz.moneyme.utils.FormatUtils;
import com.devmoroz.moneyme.utils.datetime.TimeUtils;

import java.util.ArrayList;
import java.util.List;

public class TodosAdapter extends RecyclerView.Adapter<TodosAdapter.MainViewHolder> {

    private Context mContext;
    private List<Todo> list;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onModelClick(Todo todo);
        void onDeleteClick(Todo todo, int position);
        void onShareClick(Todo todo);
    }

    public TodosAdapter(Context mContext, List<Todo> list, OnItemClickListener listener) {
        this.mContext = mContext;
        this.list = new ArrayList<>(list);
        this.listener = listener;
    }

    public void add(Todo e) {
        this.list.add(0, e);
        notifyItemInserted(0);
    }

    public void update(Todo e, int fromPosition, int toPosition){
        this.list.remove(fromPosition);
        this.list.add(toPosition, e);
        if (fromPosition == toPosition){
            notifyItemChanged(fromPosition);
        }else {
            notifyItemRemoved(fromPosition);
            notifyItemInserted(toPosition);
        }
    }

    public void update(Todo e, int fromPosition){
        update(e, fromPosition, 0);
    }

    public void update(Todo e){
        int fromPosition = this.list.indexOf(e);
        update(e, fromPosition);
    }

    public void remove(Todo e) {
        int position = list.indexOf(e);
        remove(position);
    }

    public void remove(int position) {
        this.list.remove(position);
        notifyItemRemoved(position);
    }

    public void setList(List<Todo> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public List<Todo> getList() {
        return list;
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MainViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_todo, parent, false));
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {
        Todo model = list.get(position);
        holder.bind(mContext,model,position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MainViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView mTodoTitleTextView;
        private final TextView mTodoContentTextView;
        private final TextView mTodoTimeTextView;
        private final ImageView mTodoIconHasReminder;
        private final ImageButton mTodoButtonMore;
        private final View card;
        private Todo todo;
        private int position;

        public MainViewHolder(View itemView) {
            super(itemView);
            mTodoTitleTextView = (TextView) itemView.findViewById(R.id.todo_title_text);
            mTodoContentTextView = (TextView) itemView.findViewById(R.id.todo_content_text);
            mTodoTimeTextView = (TextView) itemView.findViewById(R.id.todo_last_edit_text);
            mTodoIconHasReminder = (ImageView) itemView.findViewById(R.id.todo_icon_hasReminder);
            mTodoButtonMore = (ImageButton) itemView.findViewById(R.id.todo_more);
            card = itemView;
            itemView.setOnClickListener(this);
            mTodoButtonMore.setOnClickListener(this);
        }

        public void bind(Context context, Todo model, int position) {
            this.todo = model;
            this.position = position;

            setTextView(mTodoTitleTextView, model.getTitle());
            setTextView(mTodoContentTextView, model.getContent());
            mTodoTimeTextView.setText(TimeUtils.formatHumanFriendlyShortDateTime(context, model.getUpdatedDateLong()));
            card.setBackgroundColor(CustomColorTemplate.TODO_COLORS[model.getColor()]);
            if (model.isShowReminderIcon()) {
                mTodoIconHasReminder.setVisibility(View.VISIBLE);
            } else {
                mTodoIconHasReminder.setVisibility(View.GONE);
            }
        }

        private void setTextView(TextView view, String text){
            if (view == null )
                return;
            if (FormatUtils.isEmpty(text))
                view.setVisibility(View.GONE);
            view.setText(text);
        }

        public void showPopUp(View v, Todo todo, int position){
            PopupMenu popup = new PopupMenu(mContext, v);
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                       case R.id.remove_todo:
                            listener.onDeleteClick(todo,position);
                            return true;
                        case R.id.share_todo:
                            listener.onShareClick(todo);
                            return true;
                        default:
                            return false;
                    }
                }
            });
            popup.inflate(R.menu.menu_popup_todo);
            popup.show();
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.todo_item_card:
                    listener.onModelClick(todo);
                    break;
                case R.id.todo_more:
                    showPopUp(v,todo,position);
                    break;
            }

        }
    }
}
