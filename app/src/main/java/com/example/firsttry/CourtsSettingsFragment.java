package com.example.firsttry;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firsttry.enums.CourtType;
import com.example.firsttry.extensions.ValidatedFragment;
import com.example.firsttry.extensions.ValidatedEditText;
import com.example.firsttry.extensions.adapters.AddedCourtAdapter;
import com.example.firsttry.models.Court;
import com.example.firsttry.utilities.Array;
import com.example.firsttry.utilities.DatabaseHandler;

import java.util.ArrayList;
import java.util.List;

public class CourtsSettingsFragment
        extends ValidatedFragment
        implements AddedCourtAdapter.OnUserActionListener
{
    private RecyclerView recyclerView;
    private AddedCourtAdapter adapter;

    private ValidatedEditText _name;
    private Button addButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        currentView = inflater.inflate(R.layout.fragment_courts_settings, container, false);
        checkAuthenticated();

        setNameEditText();
        setDefaultAdapter();
        setCourtTypesSpinner();
        setAddButton();
        recyclerView = currentView.findViewById(R.id.addedCourtsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        return currentView;
    }

    private void setDefaultAdapter()
    {
        DatabaseHandler.list(new Court().tableName(), Court.class).thenAccept(list -> {
            Array<Court> activeCourts = list
                    .where(item -> !item.getIsDeleted());
            adapter = new AddedCourtAdapter(activeCourts, this);
            recyclerView.setAdapter(adapter);
        });
    }

    private void setNameEditText()
    {
        _name = currentView.findViewById(R.id.edit_name);
        _name.setRequired(true);
    }

    private void setAddButton()
    {
        addButton = currentView.findViewById(R.id.btn_add);
        addButton.setOnClickListener(v -> {
            if (!validateFields())
                return;

            String name = _name
                    .getText()
                    .toString();

            String type = ((Spinner) currentView.findViewById(R.id.types_view_select))
                    .getSelectedItem()
                    .toString();

            CourtType courtType = CourtType.valueOf(type);

            Court court = new Court(name, courtType);
            court.save().thenAccept(res -> {
                Toast.makeText(requireActivity(), "Campo registrato con successo!", Toast.LENGTH_SHORT).show();
                DatabaseHandler.list(new Court().tableName(), Court.class).thenAccept(list -> {
                    Array<Court> activeCourts = list
                            .where(item -> !item.getIsDeleted());
                    adapter = new AddedCourtAdapter(activeCourts, this);
                    recyclerView.setAdapter(adapter);
                });
            });
        });
    }

    private void setCourtTypesSpinner()
    {
        List<String> types = new ArrayList<>();
        for (CourtType type : CourtType.values())
            types.add(type.name());

        Spinner spinner = currentView.findViewById(R.id.types_view_select);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    @Override
    public void onDelete(Court court)
    {
        court.setIsDeleted(true);
        court.save().thenAccept(res -> {
            Toast.makeText(requireActivity(), "Campo eliminato con successo!", Toast.LENGTH_SHORT).show();
            DatabaseHandler.list(new Court().tableName(), Court.class).thenAccept(list -> {
                Array<Court> activeCourts = list
                        .where(item -> !item.getIsDeleted());
                adapter = new AddedCourtAdapter(activeCourts, this);
                recyclerView.setAdapter(adapter);
            });
        });
    }
}
