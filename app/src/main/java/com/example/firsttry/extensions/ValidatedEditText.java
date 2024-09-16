package com.example.firsttry.extensions;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;

import com.example.firsttry.R;
import com.example.firsttry.utilities.Array;
import com.example.firsttry.utilities.StringValidator;
import com.example.firsttry.enums.ValidatorType;

import java.util.HashMap;
import java.util.Objects;
import java.util.function.Predicate;

public class ValidatedEditText extends androidx.appcompat.widget.AppCompatEditText
{
    private Boolean isRequired = false;
    private final Array<String> errors = new Array<>();
    private HashMap<Predicate<String>, String> validationConditions = new HashMap<>();
    private ValidatorType validatorType = ValidatorType.None;

    @SuppressLint("ResourceAsColor")
    public ValidatedEditText(Context context) { super(context); setTextColor(R.color.black); }

    @SuppressLint("ResourceAsColor")
    public ValidatedEditText(Context context, AttributeSet attrs) { super(context, attrs); setTextColor(R.color.black); }

    @SuppressLint("ResourceAsColor")
    public ValidatedEditText(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); setTextColor(R.color.black); }

    public Boolean isRequired() { return isRequired; }
    public void setRequired(Boolean required) { isRequired = required; }
    public ValidatorType getValidatorType() { return validatorType; }
    public void setValidatorType(ValidatorType validatorType) { this.validatorType = validatorType; }
    public Array<String> getErrors() { return errors; }

    public Boolean hasErrors() { return !errors.isEmpty(); }

    public boolean validate()
    {
        errors.clear();

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

        for (HashMap.Entry<Predicate<String>, String> entry : validationConditions.entrySet())
        {
            if (entry.getKey().test(value)) {
                this.errors.add(error = entry.getValue());
                setError(error);
                return false;
            }
        }

        if (validatorType != ValidatorType.None)
        {
            this.errors.add(error = "Il campo non rispetta il formato corretto");
            setError(error);
            return StringValidator.match(value, validatorType);
        }

        return true;
    }

    public void addValidationCondition(Predicate<String> condition, String errorMessage)
    {
        validationConditions.put(condition, errorMessage);
    }
}
