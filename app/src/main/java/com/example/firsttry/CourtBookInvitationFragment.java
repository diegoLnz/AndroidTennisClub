package com.example.firsttry;

import static com.example.firsttry.utilities.DateTimeExtensions.now;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firsttry.businesslogic.CourtsBookBl;
import com.example.firsttry.extensions.ValidatedFragment;
import com.example.firsttry.extensions.ValidatedEditText;
import com.example.firsttry.extensions.adapters.SearchedUserAdapterForBooks;
import com.example.firsttry.models.CourtBook;
import com.example.firsttry.models.CourtBookRequest;
import com.example.firsttry.models.User;
import com.example.firsttry.utilities.AccountManager;
import com.example.firsttry.utilities.Array;
import com.example.firsttry.utilities.DatabaseHandler;
import com.example.firsttry.utilities.NotificationSender;
import com.example.firsttry.utilities.Repository;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class CourtBookInvitationFragment
        extends ValidatedFragment
        implements SearchedUserAdapterForBooks.OnUserActionListener
{
    private final String extrakey = "courtBookId";

    private CourtBook currentCourtBook;

    private RecyclerView recyclerView;
    private SearchedUserAdapterForBooks adapter;
    private ValidatedEditText _searchEditText;
    private Button _searchButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        currentView = inflater.inflate(R.layout.fragment_court_book_invitation, container, false);

        recyclerView = currentView.findViewById(R.id.searchedUsersRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        setCurrentUser();
        setSearchEditText();

        User.list().thenAccept(users -> {
            users.orderByDescending(User::getScore);
            users = users.where(user -> !user.getId().equals(CurrentUser.getId()));
            setRecyclerView(users);
        });

        getCurrentCourtBook().thenAccept(courtBook -> {
            currentCourtBook = courtBook;
            setSearchButton();
        });

        return currentView;
    }

    private CompletableFuture<CourtBook> getCurrentCourtBook()
    {
        String courtBookId = getArguments().getString(extrakey);
        return new CourtBook().getById(courtBookId);
    }

    private void setSearchEditText()
    {
        _searchEditText = currentView.findViewById(R.id.edit_search);
        _searchEditText.setRequired(false);
    }

    private void setSearchButton()
    {
        _searchButton = currentView.findViewById(R.id.btn_search);
        _searchButton.setOnClickListener(v -> loadSearchedUsers());
    }

    private void setRecyclerView(Array<User> users)
    {
        adapter = new SearchedUserAdapterForBooks(users, this);
        recyclerView.setAdapter(adapter);
    }

    private void loadSearchedUsers()
    {
        String searchText = _searchEditText
                .getText()
                .toString();

        if (searchText.isEmpty())
        {
            setRecyclerView(new Array<>());
            return;
        }

        CourtsBookBl.getNotInvitedUsersByCourtBook(currentCourtBook).thenAccept(notInvitedUsers -> Objects.requireNonNull(AccountManager.getCurrentAccount()).thenAccept(account ->
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
                                    .where(user -> notInvitedUsers.any(user1 -> user1.getId().equals(user.getId())))
                                    .orderBy(User::getUsername)
                                    .remove(user -> user.getUsername().equals(account.getUsername()));

                            setRecyclerView(foundUsers);
                        })));

    }

    private void resetSearch()
    {
        _searchEditText.setText("");
        CourtsBookBl.getNotInvitedUsersByCourtBook(currentCourtBook).thenAccept(users -> {
            users.orderByDescending(User::getScore);
            users = users.where(user -> !user.getId().equals(CurrentUser.getId()));
            setRecyclerView(users);
        });
    }

    @Override
    public void onInvitation(User user)
    {
        CourtBookRequest request = new CourtBookRequest(
                CurrentUser.getId(),
                user.getId(),
                currentCourtBook.getId(),
                now()
        );
        request.save()
                .thenAccept(res -> {
                    sendInvitationNotification(res);
                    Toast.makeText(requireActivity(), "Invito mandato con successo", Toast.LENGTH_SHORT).show();
                    resetSearch();
                });
    }

    private void sendInvitationNotification(CourtBookRequest request)
    {
        NotificationSender.sendNotification(
                request.getTargetUserId(),
                "Hai ricevuto un invito!",
                "Controlla la lista degli inviti");
    }
}
