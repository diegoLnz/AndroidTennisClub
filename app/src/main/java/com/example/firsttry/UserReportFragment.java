package com.example.firsttry;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.firsttry.extensions.ValidatedFragment;
import com.example.firsttry.extensions.ValidatedEditText;
import com.example.firsttry.models.Report;
import com.example.firsttry.models.User;
import com.example.firsttry.utilities.DatabaseHandler;
import com.example.firsttry.utilities.DateTimeExtensions;
import com.example.firsttry.utilities.FragmentHandler;
import com.example.firsttry.utilities.HashMapExtensions;

public class UserReportFragment extends ValidatedFragment
{
    private static final String extraKey = "userId";
    private TextView reportLabel;
    private ValidatedEditText reportText;
    private User targetUser;

    private Button sendReportButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        currentView = inflater.inflate(R.layout.activity_user_report, container, false);
        checkAuthenticated();
        setCurrentUser();

        String userId = requireActivity()
                .getIntent()
                .getStringExtra(extraKey);

        DatabaseHandler.getById(userId, new User().tableName(), User.class)
                .thenAccept(user -> {
                    targetUser = user;
                    setFields();
                    setReportButton();
                })
                .exceptionally(ex -> {
                    System.err.println("Failed to retrieve user: " + ex.getMessage());
                    return null;
                });
        return currentView;
    }

    private void setFields()
    {
        reportLabel = currentView.findViewById(R.id.report_label);
        reportLabel.append(" " + targetUser.getUsername());
        reportText = currentView.findViewById(R.id.report_text_field);
        reportText.setRequired(true);
    }

    private void setReportButton()
    {
        sendReportButton = currentView.findViewById(R.id.btn_report_profile);
        sendReportButton.setOnClickListener(v -> sendReport());
    }

    private void sendReport()
    {
        if (!validateFields())
            return;

        Report report = new Report(
                reportText.getText().toString(),
                targetUser.getId(),
                CurrentUser.getId(),
                DateTimeExtensions.now());

        report.save()
                .thenAccept(savedReport -> {
                    Toast.makeText(requireActivity(), "Segnalazione inviata con successo!", Toast.LENGTH_SHORT).show();
                    FragmentHandler.replaceFragmentWithArguments(
                            requireActivity(),
                            new UserDetailFragment(),
                            HashMapExtensions.from("userId", targetUser.getId())
                    );
                });
    }
}
