package com.example.firsttry;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firsttry.enums.UserStatus;
import com.example.firsttry.extensions.adapters.UserAdapter;
import com.example.firsttry.extensions.ValidatedFragment;
import com.example.firsttry.models.User;
import com.example.firsttry.utilities.AccountManager;
import com.example.firsttry.utilities.Array;
import com.example.firsttry.utilities.DatabaseHandler;
import com.example.firsttry.utilities.FragmentHandler;

import java.util.HashMap;
import java.util.Objects;

public class UsersSettingsFragment
        extends ValidatedFragment
        implements UserAdapter.OnUserActionListener
{
    private RecyclerView recyclerView;
    private UserAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        currentView = inflater.inflate(R.layout.fragment_users_settings, container, false);
        checkAuthenticated();

        recyclerView = currentView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        loadUsers();
        return currentView;
    }

    private void loadUsers()
    {
        Objects.requireNonNull(AccountManager.getCurrentAccount()).thenAccept(account ->
                DatabaseHandler.list(new User().tableName(), User.class)
                        .thenAccept(users -> requireActivity().runOnUiThread(() -> {
                            Array<User> filteredUsers = users.orderBy(User::getUsername)
                                    .where(user -> !user.getStatus().equals(UserStatus.BANDITED))
                                    .remove(userToRemove -> Objects.equals(userToRemove.getId(), account.getId()));
                            adapter = new UserAdapter(filteredUsers, this);
                            recyclerView.setAdapter(adapter);
                        })));
    }

    @Override
    public void onEdit(User user)
    {
        HashMap<String, String> args = new HashMap<>();
        args.put("userId", user.getId());

        FragmentHandler.replaceFragmentWithArguments(requireActivity(), new UserEditFragment(), args);
    }
}
