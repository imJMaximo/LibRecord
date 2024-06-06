package com.example.librecord;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BookmarkFragment extends Fragment {

    private View view;
    private ImageButton profileButton1;
    private Connect connection;
    private ExecutorService executorService;
    //private GridAdapter gridAdapter;
    private GridView bookGridView;
    private String userId, name, email;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bookmark, container, false);

        connection = new Connect();
        executorService = Executors.newSingleThreadExecutor();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userDetails", MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", "");
        email = sharedPreferences.getString("email", "");
        name = sharedPreferences.getString("name", "");

        Log.d("BookmarkFragment","name:"+name);

        bookGridView = view.findViewById(R.id.bookmark_libraries);
        profileButton1 = view.findViewById(R.id.bookmark_profileButton);
        profileButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawerLayout = view.getRootView().findViewById(R.id.drawerLayout);
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        return view;
    }

}






