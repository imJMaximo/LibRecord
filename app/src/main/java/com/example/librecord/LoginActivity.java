package com.example.librecord;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.*;
import android.os.Bundle;
import android.content.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {
    Button signup, signin;
    EditText email, password;
    Intent signupPage;
    Connect connection;
    Connection conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        connection = new Connect();

        signup = (Button) findViewById(R.id.signup);
        signin = (Button) findViewById(R.id.signin);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signupPage = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(signupPage);
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent homeAct = new Intent(LoginActivity.this, HomeActivity.class);
//                startActivity(homeAct);

                SignIn(email, password);
            }
        });
    }

    public void SignIn(EditText email, EditText password){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(()->{
            try {
                conn = Connect.CONN();
                String query = "SELECT * FROM AccountData WHERE email = \""+email.getText().toString()+"\" AND password = \""+
                        password.getText().toString()+"\"";
                PreparedStatement preparedStatement = conn.prepareStatement(query);
                ResultSet rs = preparedStatement.executeQuery();

                if (rs.next()){
                    runOnUiThread(()->{
                        Intent homeAct = new Intent(LoginActivity.this, HomeActivity.class);
                        try {
                            String user_id = String.valueOf(rs.getInt("id"));
                            String username = String.valueOf(rs.getString("username"));
                            saveUserDetails(user_id, email.getText().toString(),username);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                        startActivity(homeAct);
                    });

                    email.setText("");
                    password.setText("");
                } else {
                    runOnUiThread(()->{
                        Toast.makeText(this,"Invalid Email or Password", Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (SQLException e){
                Log.d("SQL Error", "Check your SQL Syntax");
            }
        });
    }


    private void saveUserDetails(String userId, String email, String name) {
        SharedPreferences.Editor editor = getSharedPreferences("userDetails", MODE_PRIVATE).edit();
        editor.putString("userId", userId);
        editor.putString("email", email);
        editor.putString("name", name);
        editor.apply();
    }
}