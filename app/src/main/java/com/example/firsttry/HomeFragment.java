package com.example.firsttry;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.firsttry.businesslogic.CourtsBookBl;
import com.example.firsttry.enums.UserRoles;
import com.example.firsttry.extensions.ValidatedEditText;
import com.example.firsttry.extensions.adapters.AddedCourtAdapter;
import com.example.firsttry.extensions.adapters.AvailableCourtAdapter;
import com.example.firsttry.models.Court;
import com.example.firsttry.utilities.AccountManager;
import com.example.firsttry.utilities.DatabaseHandler;
import com.example.firsttry.utilities.FragmentHandler;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class HomeFragment
        extends Fragment
        implements AvailableCourtAdapter.OnUserActionListener
{
    private ValidatedEditText day;
    private ValidatedEditText time;
    private Button searchAvailableCourtsBtn;

    private RecyclerView recyclerView;
    private AvailableCourtAdapter adapter;
    private View _currentView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
                getActivity().finish();
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
        searchAvailableCourtsBtn = _currentView.findViewById(R.id.btn_search_available_courts);
        searchAvailableCourtsBtn.setOnClickListener(v -> searchAvailableCourts());
    }

    private void searchAvailableCourts()
    {
        String day = Objects.requireNonNull(this.day.getText()).toString();
        String time = Objects.requireNonNull(this.time.getText()).toString();

        Date date = convertToDate(day, time);

        CourtsBookBl.getAvailableCourts(date).thenAccept(courts ->
        {
            adapter = new AvailableCourtAdapter(courts, this);
            recyclerView.setAdapter(adapter);
        });
    }

    private Date convertToDate(String dateStr, String timeStr)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        Date date = null;
        try
        {
            date = dateFormat.parse(dateStr + " " + timeStr);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return date;
    }



    @Override
    public void onClick(Court court)
    {

    }
}