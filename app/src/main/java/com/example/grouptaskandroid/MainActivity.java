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

import com.example.grouptaskandroid.exception.network.AuthenticationFailedException;
import com.example.grouptaskandroid.exception.network.BadRequestException;
import com.example.grouptaskandroid.exception.network.NoNetworkResponseException;
import com.example.grouptaskandroid.exception.network.NotFoundException;
import com.example.grouptaskandroid.fragments.GroupDetailViewModel;
import com.example.grouptaskandroid.fragments.GroupFragmentDirections;
import com.example.grouptaskandroid.fragments.GroupViewModel;
import com.example.grouptaskandroid.fragments.TaskViewModel;
import com.example.grouptaskandroid.login.LoginActivity;
import com.example.grouptaskandroid.model.Group;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";

    private MainViewModel mainViewModel;
    private GroupViewModel groupViewModel;
    private TaskViewModel taskViewModel;
    private GroupDetailViewModel groupDetailViewModel;

    private Toolbar toolbar;

    private NavHostFragment navHostFragment;
    private NavController navController;
    AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupNavigationController();
        setupToolBar();

        mainViewModel = new ViewModelProvider(MainActivity.this).get(MainViewModel.class);
        groupViewModel = new ViewModelProvider(MainActivity.this).get(GroupViewModel.class);
        groupDetailViewModel = new ViewModelProvider(MainActivity.this).get(GroupDetailViewModel.class);
        taskViewModel = new ViewModelProvider(MainActivity.this).get(TaskViewModel.class);

        groupViewModel.getSelectedGroup().observe(MainActivity.this,
                new Observer<Group>() {
                    @Override
                    public void onChanged(Group group) {
                        if (!group.equals(GroupViewModel.DEFAULT_SELECTED_GROUP)) {

                            GroupFragmentDirections.ActionGroupFragmentToGroupDetailFragment action
                                    = GroupFragmentDirections.actionGroupFragmentToGroupDetailFragment();
                            action.setGroupId(group.getPk());
                            action.setTitle(group.getName());
                            navController.navigate(action);

                            Log.d(TAG, "onChanged: " + group);
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

        groupDetailViewModel.getError().getGroupDetailError().observe(MainActivity.this,
                new Observer<Exception>() {
                    @Override
                    public void onChanged(Exception e) {
                        handleAPIException(e);
                    }
                }
        );

        groupDetailViewModel.getError().getAddRemoveMemberError().observe(MainActivity.this,
                new Observer<Exception>() {
                    @Override
                    public void onChanged(Exception e) {
                        handleAPIException(e);
                    }
                }
        );

        groupDetailViewModel.getError().getConvertUsernameToIdError().observe(MainActivity.this,
                new Observer<Exception>() {
                    @Override
                    public void onChanged(Exception e) {
                        handleAPIException(e);
                    }
                }
        );
    }

    private void setupToolBar() {
        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        appBarConfiguration = new AppBarConfiguration.Builder(R.id.groupFragment, R.id.taskFragment).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
    }

    private void setupNavigationController() {
        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.main_nav_host_fragment);
        navController = navHostFragment.getNavController();
        BottomNavigationView bottomNav = findViewById(R.id.main_bottom_nav);
        NavigationUI.setupWithNavController(bottomNav, navController);
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
        } else if (exception instanceof BadRequestException) {
            Toast.makeText(
                    MainActivity.this,
                    ((BadRequestException) exception).getErrorMsg(),
                    Toast.LENGTH_LONG
            ).show();
        } else if (exception instanceof NotFoundException) {
            Toast.makeText(
                    MainActivity.this,
                    ((NotFoundException) exception).getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}