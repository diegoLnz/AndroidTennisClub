package com.example.firsttry;

import static com.example.firsttry.utilities.DateTimeExtensions.addHours;
import static com.example.firsttry.utilities.DateTimeExtensions.convertToDate;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

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
import com.example.firsttry.utilities.StringValidator;
import com.example.firsttry.utilities.ValidatorType;

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
        day.setValidatorType(ValidatorType.DATE);

        time = _currentView.findViewById(R.id.edit_time);
        time.setValidatorType(ValidatorType.TIME);
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

        if (!StringValidator.matchDate(day))
        {
            Toast.makeText(requireActivity(), "Data non valida", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!StringValidator.matchTime(time))
        {
            Toast.makeText(requireActivity(), "Ora non valida", Toast.LENGTH_SHORT).show();
            return;
        }

        requestedDate = convertToDate(day, time, true);

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
                        courtBook.setCourtId(court.getId());
                        courtBook.setStartsAt(requestedDate);
                        courtBook.setEndsAt(addHours(requestedDate, 1));
                    }
                    courtBook.addUserId(user.getId());
                    courtBook.save().thenAccept(result ->
                            Toast.makeText(requireActivity(), "Disponibilità registrata con successo!", Toast.LENGTH_SHORT).show()
                    );
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