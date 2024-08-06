package com.example.firsttry.extensions;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import androidx.appcompat.widget.AppCompatEditText;

import com.example.firsttry.utilities.Result;

import java.util.Objects;

public class ValidatedEditText extends androidx.appcompat.widget.AppCompatEditText
{
    private boolean isRequired;

    public ValidatedEditText(Context context) { super(context); }

    public ValidatedEditText(Context context, AttributeSet attrs) { super(context, attrs); }

    public ValidatedEditText(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); }

    public boolean isRequired() { return isRequired; }
    public void setRequired(boolean required) { isRequired = required; }

    public boolean validate()
    {
        boolean isEmpty = Objects.requireNonNull(getText())
                .toString()
                .trim()
                .isEmpty();

        if (isRequired && isEmpty)
        {
            setError("Il campo è obbligatorio");
            return false;
        }

        return true;
    }
}
