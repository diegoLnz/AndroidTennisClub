package com.example.firsttry;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firsttry.enums.UserRole;
import com.example.firsttry.enums.UserStatus;
import com.example.firsttry.extensions.ValidatedFragment;
import com.example.firsttry.extensions.adapters.UserAdapter;
import com.example.firsttry.models.User;
import com.example.firsttry.utilities.AccountManager;
import com.example.firsttry.utilities.Array;
import com.example.firsttry.utilities.DatabaseHandler;
import com.example.firsttry.utilities.FragmentHandler;
import com.example.firsttry.utilities.HashMapExtensions;

import java.util.Objects;

public class TeacherManagerFragment
        extends ValidatedFragment
        implements UserAdapter.OnUserActionListener
{
    private RecyclerView recyclerView;
    private UserAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        currentView = inflater.inflate(R.layout.fragment_teacher_manager, container, false);
        checkAuthenticated();

        recyclerView = currentView.findViewById(R.id.teachersRecyclerView);
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
                                    .where(user -> user.getRole().equals(UserRole.Teacher))
                                    .remove(userToRemove -> Objects.equals(userToRemove.getId(), account.getId()));
                            adapter = new UserAdapter(filteredUsers, this, true);
                            recyclerView.setAdapter(adapter);
                        })));
    }

    @Override
    public void onEdit(User user)
    {
        FragmentHandler.replaceFragmentWithArguments(requireActivity(),new TeacherScheduleManagerFragment(), HashMapExtensions.from("userId", user.getId()));
    }
}
