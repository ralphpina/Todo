package net.ralphpina.todo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;

import net.ralphpina.todo.model.Todo;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    public Toolbar mToolbar;
    @Bind(R.id.recycler_view)
    RecyclerView         mRecyclerView;
    @Bind(R.id.nue_message)
    TextView             mNue;

    private int         mMostRecentUpdate;
    private TodoAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        setUpRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTodosNotFinished();
    }

    private void setUpRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new TodoAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    @OnClick(R.id.fab)
    public void onClickFab() {
        startActivity(new Intent(this, AddTodoActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // ===== Query Todos ===========================================================================

    private void loadTodosNotFinished() {
        final int myUpdateNumber = ++mMostRecentUpdate;
        Todo.getQuery()
            .fromLocalDatastore()
            .whereEqualTo("done", false)
                .orderByDescending("createdAt")
                        // Kick off the query in the background
                .findInBackground(new FindCallback<Todo>() {
                    @Override
                    public void done(List<Todo> todos, ParseException e) {
                        if (e != null) {
                            return;
                        }
                        /*
                         * Make sure we're processing results from
                         * the most recent update, in case there
                         * may be more than one in progress.
                         */
                        if (myUpdateNumber != mMostRecentUpdate) {
                            return;
                        }
                        mAdapter.addData(todos);
                        if (todos.size() > 0) {
                            mRecyclerView.setVisibility(VISIBLE);
                            mNue.setVisibility(GONE);
                        } else {
                            mRecyclerView.setVisibility(GONE);
                            mNue.setVisibility(VISIBLE);
                        }
                    }
                });
    }

    // ===== RECYCLER VIEW =========================================================================

    public final static int TODO   = 0;
    public final static int FOOTER = 1;

    @Retention(RetentionPolicy.CLASS)
    @IntDef({TODO,
             FOOTER})
    public @interface ListType {
    }

    private class TodoAdapter extends RecyclerView.Adapter<TodoViewHolder> {

        private List<Todo> mTodos;

        public void addData(List<Todo> todos) {
            mTodos = todos;
            notifyDataSetChanged();
        }

        public void remove(int position) {
            mTodos.remove(position);
            notifyItemRemoved(position);
        }

        @ListType
        @Override
        public int getItemViewType(int position) {
            if (position == mTodos.size()) {
                return FOOTER;
            }
            return TODO;
        }

        @Override
        public TodoViewHolder onCreateViewHolder(ViewGroup parent, @ListType int viewType) {
            if (viewType == TODO) {
                View v = LayoutInflater.from(parent.getContext())
                                  .inflate(R.layout.list_item_todo, parent, false);
                return new ViewHolder(v);
            } else {
                View v = LayoutInflater.from(parent.getContext())
                                  .inflate(R.layout.list_item_footer, parent, false);
                return new FooterViewHolder(v);
            }
        }

        @Override
        public void onBindViewHolder(TodoViewHolder holder, int position) {
            if (position == mTodos.size()) {
                holder.configure(null);
            } else {
                holder.configure(mTodos.get(position));
            }
        }

        @Override
        public int getItemCount() {
            if (mTodos == null) {
                return 0;
            } else {
                return mTodos.size() + 1;
            }
        }
    }

    public class ViewHolder extends TodoViewHolder {

        @Bind(R.id.text_description)
        TextView mDescription;
        @Bind(R.id.checkbox)
        CheckBox mCheckBox;

        private Todo todo;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    todo.setDone(true);
                    todo.pinInBackground();
                    mAdapter.remove(getAdapterPosition());
                }
            });
        }

        public void configure(Todo todo) {
            this.todo = todo;
            mDescription.setText(todo.getDescription());
            mCheckBox.setChecked(todo.getDone());
        }
    }

    public class FooterViewHolder extends TodoViewHolder {

        public FooterViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void configure(Todo todo) {
            // noop
        }
    }

    public abstract class TodoViewHolder extends RecyclerView.ViewHolder {

        public TodoViewHolder(View itemView) {
            super(itemView);
        }

        abstract public void configure(Todo todo);
    }
}
