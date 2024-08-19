package com.example.firsttry.extensions;

import android.content.Context;
import android.util.AttributeSet;

import com.example.firsttry.utilities.Array;
import com.example.firsttry.utilities.StringValidator;
import com.example.firsttry.enums.ValidatorType;

import java.util.Objects;

public class ValidatedEditText extends androidx.appcompat.widget.AppCompatEditText
{
    private Boolean isRequired = false;
    private final Array<String> errors = new Array<>();
    private ValidatorType validatorType = ValidatorType.None;

    public ValidatedEditText(Context context) { super(context); }

    public ValidatedEditText(Context context, AttributeSet attrs) { super(context, attrs); }

    public ValidatedEditText(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); }

    public Boolean isRequired() { return isRequired; }
    public void setRequired(Boolean required) { isRequired = required; }
    public ValidatorType getValidatorType() { return validatorType; }
    public void setValidatorType(ValidatorType validatorType) { this.validatorType = validatorType; }
    public Array<String> getErrors() { return errors; }

    public Boolean hasErrors() { return !errors.isEmpty(); }

    public boolean validate()
    {
        String error;
        String value = Objects.requireNonNull(getText())
                .toString()
                .trim();

        boolean isEmpty = value
                .isEmpty();

        if (isRequired && isEmpty)
        {
            this.errors.add(error = "Il campo è obbligatorio");
            setError(error);
            return false;
        }

        if (validatorType != ValidatorType.None)
        {
            this.errors.add(error = "Il campo non rispetta il formato corretto");
            setError(error);
            return StringValidator.match(value, validatorType);
        }

        return true;
    }
}
