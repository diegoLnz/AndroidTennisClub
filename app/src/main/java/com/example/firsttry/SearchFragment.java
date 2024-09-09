package com.example.firsttry;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.firsttry.extensions.ValidatedEditText;
import com.example.firsttry.extensions.adapters.SearchedUserAdapter;
import com.example.firsttry.models.User;
import com.example.firsttry.utilities.AccountManager;
import com.example.firsttry.utilities.ActivityHandler;
import com.example.firsttry.utilities.Array;
import com.example.firsttry.utilities.DatabaseHandler;

import java.util.Objects;
import java.util.function.Function;

public class SearchFragment
        extends Fragment
        implements SearchedUserAdapter.OnUserActionListener
{
    private View _currentView;

    private RecyclerView recyclerView;
    private SearchedUserAdapter adapter;
    private ValidatedEditText _searchEditText;
    private Button _searchButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        _currentView = inflater.inflate(R.layout.fragment_search, container, false);

        recyclerView = _currentView.findViewById(R.id.searchedUsersRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        setSearchEditText();
        setSearchButton();
        return _currentView;
    }

    private void setSearchEditText()
    {
        _searchEditText = _currentView.findViewById(R.id.edit_search);
        _searchEditText.setRequired(false);
    }

    private void setSearchButton()
    {
        _searchButton = _currentView.findViewById(R.id.btn_search);
        _searchButton.setOnClickListener(v -> loadSearchedUsers());
    }

    private void loadSearchedUsers()
    {
        String searchText = _searchEditText
                .getText()
                .toString();

        if (searchText.isEmpty())
        {
            adapter = new SearchedUserAdapter(new Array<>(), this);
            recyclerView.setAdapter(adapter);
            return;
        }

        Objects.requireNonNull(AccountManager.getCurrentAccount()).thenAccept(account ->
                DatabaseHandler.list(new User().tableName(), User.class)
                        .thenAccept(users -> {
                            Function<User, Boolean> searchFunc = user ->
                                    user.getUsername()
                                            .toLowerCase()
                                            .contains(searchText.toLowerCase())
                                            || user.getEmail()
                                            .toLowerCase()
                                            .contains(searchText.toLowerCase());

                            Array<User> foundUsers = users
                                    .where(searchFunc)
                                    .orderBy(User::getUsername)
                                    .remove(user -> user.getUsername().equals(account.getUsername()));

                            adapter = new SearchedUserAdapter(foundUsers, this);
                            recyclerView.setAdapter(adapter);
                        }));
    }

    @Override
    public void onDetail(User user)
    {
        ActivityHandler.LinkToWithExtra(requireActivity(), UserDetailFragment.class, "userId", user.getId());
    }
}