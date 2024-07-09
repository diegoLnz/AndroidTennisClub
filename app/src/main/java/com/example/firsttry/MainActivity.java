package com.example.firsttry;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.firsttry.extensions.ValidatedCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends ValidatedCompatActivity {
    private static boolean introCompleted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (introduced())
            checkAuthenticated();
    }

    private boolean introduced()
    {
        if (!introCompleted)
        {
            startActivity(new Intent(MainActivity.this, IntroActivity.class));
            finish();
            return false;
        }

        setContentView(R.layout.activity_main);
        setBottomNavView();
        loadFragment(new HomeFragment());
        return true;
    }

    private boolean mapFragment(int itemId) {
        if (itemId == R.id.nav_home) {
            return loadFragment(new HomeFragment());
        }
        else if (itemId == R.id.nav_search) {
            return loadFragment(new SearchFragment());
        }
        else if (itemId == R.id.nav_profile) {
            return loadFragment(new ProfileFragment());
        }
        else if (itemId == R.id.nav_book_lesson) {
            return loadFragment(new BookLessonFragment());
        }
        return false;
    }

    private void setBottomNavView()
    {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> mapFragment(item.getItemId()));
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment == null)
            return false;

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
        return true;
    }

    public static void setIntroCompleted(boolean completed)
    {
        introCompleted = completed;
    }
}


