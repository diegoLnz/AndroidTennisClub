package com.example.firsttry;

import static com.example.firsttry.enums.ValidatorType.Date;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.firsttry.extensions.ValidatedCompatActivity;
import com.example.firsttry.extensions.ValidatedEditText;
import com.example.firsttry.models.Report;
import com.example.firsttry.models.User;
import com.example.firsttry.utilities.ActivityHandler;
import com.example.firsttry.utilities.DatabaseHandler;
import com.example.firsttry.utilities.DateTimeExtensions;

public class UserReportActivity extends ValidatedCompatActivity
{
    private static final String extraKey = "userId";
    private TextView reportLabel;
    private ValidatedEditText reportText;
    private User targetUser;

    private Button sendReportButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_report);
        checkAuthenticated();
        setCurrentUser();

        String userId = getIntent()
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
    }

    private void setFields()
    {
        reportLabel = findViewById(R.id.report_label);
        reportLabel.append(" " + targetUser.getUsername());
        reportText = findViewById(R.id.report_text_field);
        reportText.setRequired(true);
    }

    private void setReportButton()
    {
        sendReportButton = findViewById(R.id.btn_report_profile);
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
                .thenAccept(savedReport -> ActivityHandler.LinkToWithPreviousToast(
                        this,
                        UserDetailActivity.class,
                        "userId",
                        targetUser.getId(),
                        "Segnalazione inviata con successo!"));
    }
}
