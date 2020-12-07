package com.example.grouptaskandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.android.volley.VolleyError;
import com.example.grouptaskandroid.fragments.GroupFragment;
import com.example.grouptaskandroid.fragments.GroupViewModel;
import com.example.grouptaskandroid.fragments.MainViewModel;
import com.example.grouptaskandroid.fragments.TaskFragment;
import com.example.grouptaskandroid.login.LoginActivity;

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

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.main_groupFragment, TaskFragment.class, null)
                    .commit();
        }

        logoutButton = findViewById(R.id.main_logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainViewModel.logout();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}