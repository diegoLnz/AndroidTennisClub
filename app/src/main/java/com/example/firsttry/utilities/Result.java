package com.example.firsttry.utilities;
import java.util.function.Function;

public final class Result<TValue, TError>
{
    private final TValue value;
    private final TError error;
    private final boolean isError;

    private Result(TValue value, TError error, boolean isError)
    {
        this.value = value;
        this.error = error;
        this.isError = isError;
    }

    public static <TValue, TError> Result<TValue, TError> success(TValue value) {
        return new Result<>(value, null, false);
    }

    public static <TValue, TError> Result<TValue, TError> failure(TError error) {
        return new Result<>(null, error, true);
    }

    public <TResult> TResult match(
            Function<? super TValue, ? extends TResult> success,
            Function<? super TError, ? extends TResult> failure)
    {
        return isError
                ? failure.apply(error)
                : success.apply(value);
    }
}
