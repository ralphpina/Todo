package net.ralphpina.todo;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.SaveCallback;

import net.ralphpina.todo.model.Todo;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddTodoActivity extends AppCompatActivity {

    @Bind(R.id.todo_description)
    EditText mDescription;

    private Dialog mDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDialog = new Dialog(this, R.style.Theme_Todo_Modal);
        mDialog.setContentView(R.layout.activity_add_todo);
        ButterKnife.bind(this, mDialog);
        showKeyboard();
        setDimissAndCancel();
        mDialog.show();
    }

    private void setDimissAndCancel() {
        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });
        mDialog.setCanceledOnTouchOutside(true);
    }

    private void showKeyboard() {
        mDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mDialog.getWindow()
                           .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        mDialog.dismiss();
        super.onDestroy();
    }

    @OnClick(R.id.add_button)
    public void onClickAddButton() {
        final String description = mDescription.getText().toString();
        if (description.isEmpty()) {
            Toast.makeText(this, R.string.enter_todo, Toast.LENGTH_SHORT).show();
            return;
        }
        Todo todo = new Todo(description, false);
        todo.pinInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                finish();

            }
        });
    }
}
