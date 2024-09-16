package com.example.firsttry.utilities;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.firsttry.R;

import java.io.Serializable;
import java.util.HashMap;

public class FragmentHandler
{
    public static void replaceFragment(FragmentActivity fragmentActivity, Fragment fragment)
    {
        FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public static void replaceFragmentWithNoHistory(FragmentActivity fragmentActivity, Fragment fragment)
    {
        FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    public static <T extends Serializable> void replaceFragmentWithArguments(FragmentActivity fragmentActivity, Fragment fragment, HashMap<String, T> args)
    {
        Bundle argsBundle = new Bundle();
        for (String key : args.keySet())
        {
            argsBundle.putSerializable(key, args.get(key));
        }
        fragment.setArguments(argsBundle);
        replaceFragment(fragmentActivity, fragment);
    }

    public static void refreshFragment()
    {

    }
}
