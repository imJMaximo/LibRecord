package com.example.librecord;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.os.Bundle;

import com.google.android.material.navigation.NavigationView;

import org.w3c.dom.Text;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    ImageButton homeButton, searchButton, bookmarkButton, reserveButton;
    TextView username;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    Connect connection;
    Connection conn;
    NavigationView navigationView;
    private String userId,name,email;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Bundle bundle = new Bundle();

        connection = new Connect();
        intent = getIntent();
        HomeFragment homeFragment = new HomeFragment();

        SharedPreferences sharedPreferences = getSharedPreferences("userDetails", MODE_PRIVATE);
         userId = sharedPreferences.getString("userId", "");
         email = sharedPreferences.getString("email", "");
        name = sharedPreferences.getString("name", "");


        bundle.putInt("id", Integer.parseInt(userId));
        Log.d("ID",  ""+intent.getIntExtra("id",0));

        homeButton = (ImageButton) findViewById(R.id.homeButton);
        searchButton = (ImageButton) findViewById(R.id.searchButton);
        bookmarkButton = (ImageButton) findViewById(R.id.bookmarkButton);
        reserveButton = (ImageButton) findViewById(R.id.reserveButton);
        navigationView = findViewById(R.id.navbar);


        username = (TextView) getLayoutInflater().inflate(R.layout.accountlayout, (ViewGroup) navigationView).findViewById(R.id.username);

        getUsername(username);
        homeFragment.setArguments(bundle);

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainFragment, homeFragment);
        fragmentTransaction.commit();

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(homeFragment);
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new SearchFragment());
            }
        });

        bookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new BookmarkFragment());
            }
        });

        reserveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new ReservationFragment());
            }
        });

        navigationView.setNavigationItemSelectedListener(this);
    }

    private void replaceFragment(Fragment fragment){
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainFragment, fragment);
        fragmentTransaction.commit();
    }

    public void getUsername(TextView uname){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(()->{
            try {
                conn = connection.CONN();
                String query = "SELECT * FROM AccountData WHERE id="+userId;
                PreparedStatement preparedStatement = conn.prepareStatement(query);
                ResultSet rs = preparedStatement.executeQuery();

                if (rs.next()){
                    uname.setText(rs.getString("username").toUpperCase());
                }
            } catch (SQLException e){
                Log.d("SQL Error", "Check your SQL Syntax");
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout){
            Intent logoutActivity = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(logoutActivity);
            finish();
        }

        return true;
    }
}