package com.example.firsttry;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.example.firsttry.enums.UserRole;
import com.example.firsttry.extensions.ValidatedActivity;
import com.example.firsttry.models.User;
import com.example.firsttry.utilities.AccountManager;
import com.example.firsttry.utilities.FragmentHandler;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class MainActivity extends ValidatedActivity {
    private static boolean introCompleted = false;
    private static User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCurrentUser();
    }

    @Override
    public void setCurrentUser()
    {
        Objects.requireNonNull(AccountManager.getCurrentAccount()).thenAccept(cUser -> {
            user = cUser;
            if (introduced())
                checkAuthenticated();
        });
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

        Fragment fragment = user.getRole().equals(UserRole.Admin)
                ? new HomeAdminFragment()
                : new HomeFragment();

        loadFragment(fragment);
        return true;
    }

    private boolean mapFragment(int itemId) {
        if (itemId == R.id.nav_home) {
            return user.getRole().equals(UserRole.Admin)
                    ? loadFragment(new HomeAdminFragment())
                    : loadFragment(new HomeFragment());
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

        FragmentHandler.replaceFragment(this, fragment);
        return true;
    }

    public static void setIntroCompleted(boolean completed)
    {
        introCompleted = completed;
    }
}


