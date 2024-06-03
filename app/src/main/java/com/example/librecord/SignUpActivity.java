package com.example.librecord;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.widget.*;
import android.content.*;
import android.os.Bundle;
import android.util.*;
import java.sql.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SignUpActivity extends AppCompatActivity {
    Button backButton, signup;
    Intent loginPage;
    Connect connection;
    Connection conn;
    String tableName;
    EditText username, email, password, libID;
    TextView status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        connection = new Connect();

        BackToLogin();
    }

    public void BackToLogin(){
        backButton = (Button) findViewById(R.id.back);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginPage = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(loginPage);
                finish();
            }
        });

        username = (EditText) findViewById(R.id.username);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        libID = (EditText) findViewById(R.id.libid);

        signup = (Button) findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUp(username, email, password, libID);
            }
        });
    }

    public void SignUp(EditText username, EditText email, EditText password, EditText libID) {
        tableName = "AccountData";
        status = (TextView) findViewById(R.id.status);
        Log.d("SignUpActivity", "Username: " + username.getText().toString());
        Log.d("SignUpActivity", "Email: " + email.getText().toString());
        Log.d("SignUpActivity", "Password: " + password.getText().toString());
        Log.d("SignUpActivity", "LibID: " + libID.getText().toString());

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            Connection conn = null;
            PreparedStatement preparedStatement = null;

            try {
                conn = connection.CONN();
                if (conn == null) {
                    Log.d("Connection Error", "The connection is null");
                    runOnUiThread(() -> {
                        status.setText("SIGN UP UNSUCCESSFUL");
                        status.setTextColor(Color.RED);
                    });
                    return;
                }

                String query = "INSERT INTO " + tableName + " (username, email, password, LibID) VALUES (?, ?, ?, ?)";
                preparedStatement = conn.prepareStatement(query);
                preparedStatement.setString(1, username.getText().toString());
                preparedStatement.setString(2, email.getText().toString());
                preparedStatement.setString(3, password.getText().toString());
                preparedStatement.setString(4, libID.getText().toString());

                int rowsInserted = preparedStatement.executeUpdate();
                if (rowsInserted > 0) {
                    runOnUiThread(() -> {
                        status.setText("SIGN UP SUCCESSFUL!");
                        status.setTextColor(Color.GREEN);
                        new Handler().postDelayed(() -> {
                            Intent loginPage = new Intent(SignUpActivity.this, LoginActivity.class);
                            startActivity(loginPage);
                            finish();
                        }, 1000); // 2000 milliseconds = 2 seconds
                    });
                } else {
                    runOnUiThread(() -> {
                        status.setText("SIGN UP UNSUCCESSFUL");
                        status.setTextColor(Color.RED);
                    });
                }

            } catch (SQLException e) {
                runOnUiThread(() -> {
                    status.setText("SIGN UP UNSUCCESSFUL");
                    status.setTextColor(Color.RED);
                });
                Log.e("SQL Error", "SQL Error: " + e.getMessage(), e);
            } finally {
                try {
                    if (preparedStatement != null) {
                        preparedStatement.close();
                    }
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    Log.e("SQL Error", "Error closing resources: " + e.getMessage(), e);
                }
            }
        });
    }

}