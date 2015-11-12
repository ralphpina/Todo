package net.ralphpina.todo;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

import net.ralphpina.todo.model.Todo;

public class TodoApplication extends Application {

    private static TodoApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        Parse.enableLocalDatastore(this);
        ParseObject.registerSubclass(Todo.class);
        Parse.initialize(this, "L5m1esO1TvzoezSJwCliAAAegBUR8VuB4NXUijtw",
                         "V0Xkf8t0RckGcbUlonKqTEv5VpuxZbLgIH8GiTxY");
    }

    public static TodoApplication getInstance() {
        return mInstance;
    }
}
