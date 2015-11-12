package net.ralphpina.todo.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("Todo")
public class Todo extends ParseObject {

    // ==== USER ===================================================================================

    public ParseUser getUser() {
        return getParseUser("user");
    }

    public void setUser(ParseUser value) {
        put("user", value);
    }

    // ==== DESCRIPTION ===============================================================================

    public String getDescription() {
        return getString("description");
    }

    public void setDescription(String value) {
        put("description", value);
    }

    // ==== DONE ===============================================================================

    public boolean getDone() {
        return getBoolean("done");
    }

    public void setDone(boolean done) {
        put("done", done);
    }

    // ==== QUERY ==================================================================================

    public static ParseQuery<Todo> getQuery() {
        return ParseQuery.getQuery(Todo.class);
    }
}
