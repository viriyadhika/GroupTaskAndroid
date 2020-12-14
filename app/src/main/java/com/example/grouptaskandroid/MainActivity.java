package com.example.grouptaskandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.grouptaskandroid.exception.AuthenticationFailedException;
import com.example.grouptaskandroid.exception.NoNetworkResponseException;
import com.example.grouptaskandroid.fragments.GroupFragmentDirections;
import com.example.grouptaskandroid.fragments.GroupViewModel;
import com.example.grouptaskandroid.fragments.TaskViewModel;
import com.example.grouptaskandroid.login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";

    private MainViewModel mainViewModel;
    private GroupViewModel groupViewModel;
    private TaskViewModel taskViewModel;

    private Toolbar toolbar;

    private NavHostFragment navHostFragment;
    private NavController navController;
    AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.main_nav_host_fragment);
        navController = navHostFragment.getNavController();
        BottomNavigationView bottomNav = findViewById(R.id.main_bottom_nav);
        NavigationUI.setupWithNavController(bottomNav, navController);

        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        appBarConfiguration = new AppBarConfiguration.Builder(R.id.groupFragment, R.id.taskFragment).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        mainViewModel = new ViewModelProvider(MainActivity.this).get(MainViewModel.class);
        groupViewModel = new ViewModelProvider(MainActivity.this).get(GroupViewModel.class);
        taskViewModel = new ViewModelProvider(MainActivity.this).get(TaskViewModel.class);

        groupViewModel.getSelectedGroupPk().observe(MainActivity.this,
                new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer pk) {
                        if (pk != GroupViewModel.DEFAULT_SELECTED_PK) {

                            GroupFragmentDirections.ActionGroupFragmentToGroupDetailFragment action
                                    = GroupFragmentDirections.actionGroupFragmentToGroupDetailFragment();
                            action.setGroupId(pk);
                            navController.navigate(action);

                            Log.d(TAG, "onChanged: " + pk);
                        }
                    }
                });
        groupViewModel.getError().observe(MainActivity.this,
                new Observer<Exception>() {
                    @Override
                    public void onChanged(Exception exception) {
                        handleAPIException(exception);
                    }
                }
        );

        taskViewModel.getError().observe(MainActivity.this,
                new Observer<Exception>() {
                    @Override
                    public void onChanged(Exception exception) {
                        handleAPIException(exception);
                    }
                }
        );



    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_logoutbutton:
                mainViewModel.logout();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.main_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void handleAPIException(Exception exception) {
        if (exception instanceof AuthenticationFailedException) {
            mainViewModel.logout();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        } else if (exception instanceof NoNetworkResponseException) {
            //Should be try to detect the network first
            Log.d(TAG, "onChanged: " + "No Network!");
            Toast.makeText(
                    MainActivity.this,
                    ((NoNetworkResponseException) exception).getErrorMsg(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}