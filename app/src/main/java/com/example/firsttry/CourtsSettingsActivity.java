package com.example.firsttry;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firsttry.enums.CourtTypes;
import com.example.firsttry.enums.UserRoles;
import com.example.firsttry.extensions.ValidatedCompatActivity;
import com.example.firsttry.extensions.ValidatedEditText;
import com.example.firsttry.extensions.adapters.AddedCourtAdapter;
import com.example.firsttry.extensions.adapters.UserAdapter;
import com.example.firsttry.models.Court;
import com.example.firsttry.utilities.Array;
import com.example.firsttry.utilities.DatabaseHandler;

import java.util.ArrayList;
import java.util.List;

public class CourtsSettingsActivity
        extends ValidatedCompatActivity
        implements AddedCourtAdapter.OnUserActionListener
{
    private RecyclerView recyclerView;
    private AddedCourtAdapter adapter;

    private ValidatedEditText _name;
    private Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courts_settings);
        checkAuthenticated();

        setNameEditText();
        setDefaultAdapter();
        setCourtTypesSpinner();
        setAddButton();
        recyclerView = findViewById(R.id.addedCourtsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setDefaultAdapter()
    {
        DatabaseHandler.list(new Court().tableName(), Court.class).thenAccept(list -> {
            Array<Court> activeCourts = list
                    .where(item -> !item.getDeleted());
            adapter = new AddedCourtAdapter(activeCourts, this);
            recyclerView.setAdapter(adapter);
        });
    }

    private void setNameEditText()
    {
        _name = findViewById(R.id.edit_name);
        _name.setRequired(true);
    }

    private void setAddButton()
    {
        addButton = findViewById(R.id.btn_add);
        addButton.setOnClickListener(v -> {
            validateFields();
            if (!getIsValid())
                return;

            String name = _name
                    .getText()
                    .toString();

            String type = ((Spinner) findViewById(R.id.types_view_select))
                    .getSelectedItem()
                    .toString();

            CourtTypes courtType = CourtTypes.valueOf(type);

            Court court = new Court(name, courtType);
            court.save().thenAccept(res -> {
                Toast.makeText(CourtsSettingsActivity.this, "Campo registrato con successo!", Toast.LENGTH_SHORT).show();
                DatabaseHandler.list(new Court().tableName(), Court.class).thenAccept(list -> {
                    Array<Court> activeCourts = list
                            .where(item -> !item.getDeleted());
                    adapter = new AddedCourtAdapter(activeCourts, this);
                    recyclerView.setAdapter(adapter);
                });
            });
        });
    }

    private void setCourtTypesSpinner()
    {
        List<String> types = new ArrayList<>();
        for (CourtTypes type : CourtTypes.values())
            types.add(type.name());

        Spinner spinner = findViewById(R.id.types_view_select);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    @Override
    public void onDelete(Court court)
    {
        court.setDeleted(true);
        court.save().thenAccept(res -> {
            Toast.makeText(CourtsSettingsActivity.this, "Campo eliminato con successo!", Toast.LENGTH_SHORT).show();
            DatabaseHandler.list(new Court().tableName(), Court.class).thenAccept(list -> {
                Array<Court> activeCourts = list
                        .where(item -> !item.getDeleted());
                adapter = new AddedCourtAdapter(activeCourts, this);
                recyclerView.setAdapter(adapter);
            });
        });
    }
}
