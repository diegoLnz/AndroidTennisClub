package com.example.firsttry.utilities;

import java.util.HashMap;

public class HashMapExtensions
{
    public static <TKey, TValue> HashMap<TKey, TValue> from (TKey key, TValue value)
    {
        HashMap<TKey, TValue> map = new HashMap<>();
        map.put(key, value);
        return map;
    }
}
