package com.example.grouptaskandroid.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.grouptaskandroid.MainActivity;
import com.example.grouptaskandroid.R;
import com.example.grouptaskandroid.exception.RequiredFieldEmptyException;

public class LoginActivity extends AppCompatActivity {

    Button loginButton;
    EditText usernameText;
    EditText passwordText;
    ProgressBar loginProgress;
    TextView loginProgressDec;
    TextView errorText;

    LoginViewModel loginViewModel;

    private static final String TAG = "login.LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginViewModel = new ViewModelProvider(LoginActivity.this).get(LoginViewModel.class);
        loginViewModel.getExceptionState().observe(LoginActivity.this, new Observer<Exception>() {
            @Override
            public void onChanged(Exception e) {
                unshowProgressBar();
                errorText.setText("Username or Password is wrong");
            }
        });
        loginViewModel.getIsLoggedIn().observe(LoginActivity.this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoggedIn) {
                if (isLoggedIn) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });

        loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    loginViewModel.login(usernameText.getText().toString(), passwordText.getText().toString());
                    showProgressBar();
                } catch (RequiredFieldEmptyException e) {
                    errorText.setText("Username and password must be filled!");
                }
            }
        });

        usernameText = findViewById(R.id.login_username);
        passwordText = findViewById(R.id.login_password);
        loginProgress = findViewById(R.id.login_progress);
        loginProgressDec = findViewById(R.id.login_progress_desc);
        errorText = findViewById(R.id.login_error);
    }

    private void showProgressBar() {
        loginProgress.setVisibility(View.VISIBLE);
        loginProgressDec.setVisibility(View.VISIBLE);
        usernameText.setVisibility(View.INVISIBLE);
        passwordText.setVisibility(View.INVISIBLE);
        errorText.setVisibility(View.INVISIBLE);
        loginButton.setVisibility(View.INVISIBLE);
    }

    private void unshowProgressBar() {
        loginProgress.setVisibility(View.INVISIBLE);
        loginProgressDec.setVisibility(View.INVISIBLE);
        usernameText.setVisibility(View.VISIBLE);
        passwordText.setVisibility(View.VISIBLE);
        errorText.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.VISIBLE);
    }

}