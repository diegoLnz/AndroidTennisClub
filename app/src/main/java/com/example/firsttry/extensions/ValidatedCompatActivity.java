package com.example.firsttry.extensions;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.firsttry.LoginActivity;
import com.example.firsttry.utilities.AccountManager;
import com.example.firsttry.utilities.Array;
import java.lang.reflect.Field;

public abstract class ValidatedCompatActivity extends AppCompatActivity
{
    private boolean isValid = true;

    public void checkAuthenticated()
    {
        if (AccountManager.isLogged())
            return;

        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    public boolean getIsValid() { return isValid; }
    private void setIsValid(boolean isValid) { this.isValid = isValid; }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void validateFields()
    {
        Array<Field> fields = new Array<>(getClass().getDeclaredFields());
        fields.select(this::getFieldObjInstance)
                .where(value -> value instanceof ValidatedEditText)
                .forEach(this::validateSingleField);
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
