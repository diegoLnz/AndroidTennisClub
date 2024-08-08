package com.example.firsttry.extensions;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import androidx.appcompat.widget.AppCompatEditText;

import com.example.firsttry.utilities.Result;
import com.example.firsttry.utilities.StringValidator;
import com.example.firsttry.utilities.ValidatorType;

import java.util.Objects;

public class ValidatedEditText extends androidx.appcompat.widget.AppCompatEditText
{
    private boolean isRequired;
    private ValidatorType validatorType = ValidatorType.NONE;

    public ValidatedEditText(Context context) { super(context); }

    public ValidatedEditText(Context context, AttributeSet attrs) { super(context, attrs); }

    public ValidatedEditText(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); }

    public boolean isRequired() { return isRequired; }
    public void setRequired(boolean required) { isRequired = required; }
    public ValidatorType getValidatorType() { return validatorType; }
    public void setValidatorType(ValidatorType validatorType) { this.validatorType = validatorType; }

    public boolean validate()
    {
        String value = Objects.requireNonNull(getText())
                .toString()
                .trim();

        boolean isEmpty = value
                .isEmpty();

        if (isRequired && isEmpty)
        {
            setError("Il campo è obbligatorio");
            return false;
        }

        if (validatorType != ValidatorType.NONE)
        {
            setError("Il campo non rispetta il formato corretto");
            return StringValidator.match(value, validatorType);
        }

        return true;
    }
}
