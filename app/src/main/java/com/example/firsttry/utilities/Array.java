package com.example.firsttry.utilities;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class Array<T>
{
    private final List<T> list;

    @SafeVarargs
    public Array(T... array) {
        this.list = new ArrayList<>();
        list.addAll(Arrays.asList(array));
    }

    public void add(T item) {
        list.add(item);
    }

    public Array<T> where(Function<T, Boolean> predicate) {
        Array<T> result = new Array<>();
        for (T item : list) {
            if (predicate.apply(item)) {
                result.add(item);
            }
        }
        return result;
    }

    public <R> Array<R> select(Function<T, R> selector) {
        Array<R> selectedItems = new Array<>();
        for (T item : list) {
            R selected = selector.apply(item);
            selectedItems.add(selected);
        }
        return selectedItems;
    }

    public void forEach(Function<T, Void> action) {
        try {
            for (T item : list) {
                action.apply(item);
            }
        } catch (Exception e) {
            Log.e("forEach", e.getMessage(), e);
        }
    }
}
