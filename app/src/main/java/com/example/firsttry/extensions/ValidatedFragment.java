package com.example.firsttry.extensions;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.firsttry.LoginActivity;
import com.example.firsttry.R;
import com.example.firsttry.models.User;
import com.example.firsttry.utilities.AccountManager;
import com.example.firsttry.utilities.ActivityHandler;
import com.example.firsttry.utilities.Array;

import java.lang.reflect.Field;
import java.util.Objects;

public abstract class ValidatedFragment extends Fragment
{
    private boolean isValid = true;
    protected User CurrentUser = null;
    protected View currentView;
//    private boolean HasBackButton = false;

//    protected Button backButton;

//    public boolean hasBackButton() {
//        return HasBackButton;
//    }

//    public void setBackButton(Class backActivityClass) {
//        setupBackButton(backActivityClass);
//    }

    public void checkAuthenticated()
    {
        if (AccountManager.isLogged())
            return;

        startActivity(new Intent(requireActivity(), LoginActivity.class));
        requireActivity().finish();
    }

    public void setCurrentUser()
    {
        Objects.requireNonNull(AccountManager.getCurrentAccount())
                .thenAccept(user -> CurrentUser = user);
    }

    public boolean getIsValid() { return isValid; }
    private void setIsValid(boolean isValid) { this.isValid = isValid; }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return currentView;
    }

//    private void setupBackButton(Class backActivityClass)
//    {
//        backButton = findViewById(R.id.back_button);
//        backButton.setOnClickListener(v -> ActivityHandler.LinkTo(this, backActivityClass));
//    }

    public boolean validateFields()
    {
        setIsValid(true);
        Array<Field> fields = new Array<>(getClass().getDeclaredFields());
        fields.select(this::getFieldObjInstance)
                .where(value -> value instanceof ValidatedEditText)
                .forEach(this::validateSingleField);

        return getIsValid();
    }

    private Object getFieldObjInstance(Field field)
    {
        try
        {
            field.setAccessible(true);
            return field.get(this);
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private Void validateSingleField(Object field)
    {
        ValidatedEditText editText = (ValidatedEditText)field;
        if (!editText.validate() && getIsValid())
            setIsValid(false);

        return null;
    }
}
