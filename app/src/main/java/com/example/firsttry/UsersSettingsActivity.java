package com.example.firsttry;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firsttry.extensions.UserAdapter;
import com.example.firsttry.extensions.ValidatedCompatActivity;
import com.example.firsttry.models.User;
import com.example.firsttry.utilities.DatabaseHandler;

public class UsersSettingsActivity
        extends ValidatedCompatActivity
        implements UserAdapter.OnUserActionListener
{
    private RecyclerView recyclerView;
    private UserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_settings);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadUsers();
    }

    private void loadUsers()
    {
        DatabaseHandler.list(new User().tableName(), User.class)
                .thenAccept(users -> runOnUiThread(() -> {
                    users.orderBy(User::getUsername);
                    adapter = new UserAdapter(users, this);
                    recyclerView.setAdapter(adapter);
                }));
    }

    @Override
    public void onEdit(User user) {

    }
}
