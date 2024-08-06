package com.example.firsttry;

import static com.example.firsttry.utilities.DateTimeExtensions.convertToDate;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firsttry.businesslogic.CourtsBookBl;
import com.example.firsttry.enums.UserRoles;
import com.example.firsttry.extensions.ValidatedEditText;
import com.example.firsttry.extensions.adapters.AvailableCourtAdapter;
import com.example.firsttry.models.Court;
import com.example.firsttry.models.CourtBook;
import com.example.firsttry.utilities.AccountManager;
import com.example.firsttry.utilities.DatabaseHandler;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class HomeFragment
        extends Fragment
        implements AvailableCourtAdapter.OnUserActionListener
{
    private ValidatedEditText day;
    private ValidatedEditText time;
    private Date requestedDate;

    private RecyclerView recyclerView;
    private AvailableCourtAdapter adapter;
    private View _currentView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        redirectIfAdmin();
        _currentView = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = _currentView.findViewById(R.id.availableCourtsRecycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        setFields();
        setSearchAvailableCourtsButton();
        return _currentView;
    }

    private void redirectIfAdmin()
    {
        Objects.requireNonNull(AccountManager.getCurrentAccount()).thenAccept(user -> {
            if (user.getRole().equals(UserRoles.Admin))
            {
                startActivity(new Intent(getActivity(), HomeAdminFragment.class));
                requireActivity().finish();
            }
        });
    }

    private void setFields()
    {
        day = _currentView.findViewById(R.id.edit_date);
        time = _currentView.findViewById(R.id.edit_time);
    }

    private void setSearchAvailableCourtsButton()
    {
        Button searchAvailableCourtsBtn = _currentView.findViewById(R.id.btn_search_available_courts);
        searchAvailableCourtsBtn.setOnClickListener(v -> searchAvailableCourts());
    }

    private void searchAvailableCourts()
    {
        String day = Objects.requireNonNull(this.day.getText()).toString();
        String time = Objects.requireNonNull(this.time.getText()).toString();

        requestedDate = convertToDate(day, time);

        CourtsBookBl.getAvailableCourts(requestedDate).thenAccept(courts ->
        {
            adapter = new AvailableCourtAdapter(courts, requestedDate, this);
            recyclerView.setAdapter(adapter);
        });
    }

    @Override
    public void onClick(Court court)
    {
        Objects.requireNonNull(AccountManager.getCurrentAccount()).thenAccept(user -> getCourtBook(court)
                .thenAccept(courtBook -> {
                    if (courtBook == null) {
                        courtBook = new CourtBook();
                        courtBook.setCourt(court.getId());
                        courtBook.setStartsAt(requestedDate);
                    }
                    courtBook.addUserId(user.getId());
                    courtBook.save();
                }));
    }

    private CompletableFuture<CourtBook> getCourtBook(Court court)
    {
        return DatabaseHandler.list(new CourtBook().tableName(), CourtBook.class).thenApply(courtBooks -> courtBooks.where(courtBook
                        -> courtBook.getCourtId().equals(court.getId())
                        && courtBook.getStartsAt().equals(requestedDate))
                .firstOrDefault());
    }
}