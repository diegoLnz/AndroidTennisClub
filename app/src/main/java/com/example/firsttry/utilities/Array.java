package com.example.firsttry.utilities;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class Array<T>
{
    private List<T> list;

    @SafeVarargs
    public Array(T... array)
    {
        this.list = new ArrayList<>();
        list.addAll(Arrays.asList(array));
    }

    public Array(List<T> list)
    {
        this.list = list;
    }

    public void add(T item) {
        list.add(item);
    }

    public Array<T> where(Function<T, Boolean> predicate)
    {
        Array<T> result = new Array<>();
        for (T item : list) {
            if (predicate.apply(item)) {
                result.add(item);
            }
        }
        return result;
    }

    public <R> Array<R> select(Function<T, R> selector)
    {
        Array<R> selectedItems = new Array<>();
        for (T item : list) {
            R selected = selector.apply(item);
            selectedItems.add(selected);
        }
        return selectedItems;
    }

    public void forEach(Function<T, Void> action)
    {
        try {
            for (T item : list) {
                action.apply(item);
            }
        } catch (Exception e) {
            Log.e("forEach", e.getMessage(), e);
        }
    }

    public T firstOrDefault()
    {
        return list.isEmpty()
                ? null
                : list.get(0);
    }

    public T firstOrDefault(Function<T, Boolean> predicate)
    {
        Array<T> result = new Array<>();
        for (T item : list) {
            if (predicate.apply(item)) {
                result.add(item);
            }
        }
        return result.get(0);
    }

    public T get(Integer pos)
    {
        return list.size() >= pos
                ? list.get(pos)
                : null;
    }

    public Array<T> orderBy(Function<T, Comparable> selector)
    {
        List<T> sortedList = new ArrayList<>(list);
        sortedList.sort((item1, item2) -> {
            Comparable value1 = selector.apply(item1);
            Comparable value2 = selector.apply(item2);
            return value1.compareTo(value2);
        });
        this.list = sortedList;
        return new Array<>(sortedList);
    }

    public Array<T> orderByDescending(Function<T, Comparable> selector)
    {
        List<T> sortedList = new ArrayList<>(list);
        sortedList.sort((item1, item2) -> {
            Comparable value1 = selector.apply(item1);
            Comparable value2 = selector.apply(item2);
            return value2.compareTo(value1);
        });
        this.list = sortedList;
        return new Array<>(sortedList);
    }

    public Array<T> remove(Function<T, Boolean> predicate)
    {
        list.removeIf(predicate::apply);
        return this;
    }

    public Integer size() { return list.size(); }

    public List<T> getList() { return list; }
}
