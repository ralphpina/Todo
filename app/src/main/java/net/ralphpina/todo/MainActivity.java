package net.ralphpina.todo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.parse.ParseQuery;

import net.ralphpina.todo.model.Todo;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Bind(R.id.toolbar)
    public Toolbar mToolbar;
    @Bind(R.id.recycler_view)
    RecyclerView         mRecyclerView;
    @Bind(R.id.fab)
    FloatingActionButton mFab;
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
        // Todo load todos
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
        // TODO show dialog
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

    private void getTodosNotFinished() {
        final int myUpdateNumber = ++mMostRecentUpdate;
        ParseQuery<Todo> query = Todo.getQuery();
        query.include("user");
        query.whereEqualTo("done", false);
        query.orderByDescending("createdAt");
        // Kick off the query in the background
        query.findInBackground(new FindCallback<Todo>() {
            @Override
            public void done(List<Todo> todos, ParseException e) {
                Log.e(TAG, "=== findInBackground === records = " + todos);
                if (e != null) {
                    Log.e(TAG, "=== findInBackground === records = " + todos);
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
            }
        });
    }

    // ===== RECYCLER VIEW =========================================================================

    private class TodoAdapter extends RecyclerView.Adapter<ViewHolder> {

        private List<Todo> mTodos;

        public void addData(List<Todo> todos) {
            mTodos = todos;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                                   .inflate(R.layout.list_item_todo, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.configure(mTodos.get(position));
        }

        @Override
        public int getItemCount() {
            if (mTodos == null) {
                return 0;
            } else {
                return mTodos.size();
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.text_description)
        TextView mDescription;
        @Bind(R.id.checkbox)
        CheckBox mCheckBox;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // if checked animate out
                }
            });
        }

        public void configure(Todo todo) {
            mDescription.setText(todo.getDescription());
            mCheckBox.setChecked(todo.getDone());
        }
    }
}
