package com.example.grouptaskandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.android.volley.VolleyError;
import com.example.grouptaskandroid.fragments.GroupViewModel;
import com.example.grouptaskandroid.fragments.MainViewModel;
import com.example.grouptaskandroid.login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";

    private MainViewModel mainViewModel;
    private GroupViewModel groupViewModel;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainViewModel = new ViewModelProvider(MainActivity.this).get(MainViewModel.class);
        groupViewModel = new ViewModelProvider(MainActivity.this).get(GroupViewModel.class);
        groupViewModel.getSelectedGroupPk().observe(MainActivity.this,
                new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer pk) {
                        if (pk != GroupViewModel.DEFAULT_SELECTED_PK) {
                            Log.d(TAG, "onChanged: " + pk);
                        }
                    }
                });
        groupViewModel.getError().observe(MainActivity.this,
                new Observer<VolleyError>() {
                    @Override
                    public void onChanged(VolleyError volleyError) {
                        mainViewModel.logout();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                }
        );

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.main_nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        BottomNavigationView bottomNav = findViewById(R.id.main_bottom_nav);
        NavigationUI.setupWithNavController(bottomNav, navController);

//        logoutButton = findViewById(R.id.main_logout);
//        logoutButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mainViewModel.logout();
//                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//                startActivity(intent);
//            }
//        });
    }
}