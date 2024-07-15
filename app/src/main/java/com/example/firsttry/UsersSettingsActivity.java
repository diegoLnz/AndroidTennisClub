package com.example.firsttry;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firsttry.extensions.adapters.UserAdapter;
import com.example.firsttry.extensions.ValidatedCompatActivity;
import com.example.firsttry.models.User;
import com.example.firsttry.utilities.AccountManager;
import com.example.firsttry.utilities.DatabaseHandler;

import java.util.Objects;

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
        checkAuthenticated();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadUsers();
    }

    private void loadUsers()
    {
        Objects.requireNonNull(AccountManager.getCurrentAccount()).thenAccept(account ->
                DatabaseHandler.list(new User().tableName(), User.class)
                        .thenAccept(users -> runOnUiThread(() -> {
                            users.orderBy(User::getUsername)
                                    .remove(userToRemove -> Objects.equals(userToRemove.getId(), account.getId()));
                            adapter = new UserAdapter(users, this);
                            recyclerView.setAdapter(adapter);
                        })));
    }

    @Override
    public void onEdit(User user)
    {
        Intent intent = new Intent(this, UserEditActivity.class);
        intent.putExtra("userId", user.getId());
        startActivity(intent);
        finish();
    }
}
