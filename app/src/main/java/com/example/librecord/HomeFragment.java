package com.example.librecord;

import android.content.Intent;
import android.os.Bundle;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {
    private View view;
    private ImageButton profileButton1;
    private Connect connection;
    private Intent infoIntent;
    private ExecutorService executorService;
    private GridAdapter gridAdapter;
    private GridView gridView;

    private static final int[] BOOK_IMAGES = {
            R.drawable.histo1, R.drawable.histo2, R.drawable.prog3, R.drawable.calcu4, R.drawable.histo5,
            R.drawable.histo6, R.drawable.histo7, R.drawable.histo8, R.drawable.histo9, R.drawable.histo10,
            R.drawable.histo11, R.drawable.calcu12, R.drawable.calcu13, R.drawable.calcu14, R.drawable.calcu15,
            R.drawable.calcu16, R.drawable.sci17, R.drawable.sci18, R.drawable.sci19, R.drawable.sci20,
            R.drawable.sci22, R.drawable.prog23, R.drawable.prog24, R.drawable.prog25, R.drawable.prog26,
            R.drawable.prog27
    };

    private static final String[] BOOK_TITLES = {
            "Appeasement", "Destiny Disrupted", "SamTeach Yourself HTML, CSS, and JavaScript", "Two-Dimensional Calculus", "The Communist Manifesto",
            "The Plantagenets", "A Photohistory of World War One", "Orientalism", "Democracy", "Lies My Teacher Told Me",
            "Guns, Germs and Steel", "A First Course in Calculus", "Advance Calculus", "Differential Calculus", "Inside Calculus",
            "Calculus and Its Origins", "The Creation of the Universe", "Theories of the Universe", "A Brief History of Time", "Science, society, and the search for life in the universe",
            "Cosmos", "Code: The Hidden Language of Computer Hardware and Software", "Windows Forms Programming in C#", "HTML and CSS",
            "Learning SQL", "Python Programming: An Introduction to Computer Science"
    };

    private static final String[] BOOK_AUTHORS = {
            "Tim Bouverie", "Tamim Ansary", "Julie C. Meloni", "Robert Osserman", "S. Balachandra Rao and C.K. Shantha",
            "Dan Jones", "Mike Sheil", "Edward Said", "David A. Moss", "James W. Loewen",
            "Jared Diamond", "Serge Lang", "Richard Courant", "Shanti Narayan", "Gerald J. Janusz",
            "Derek Holton", "Isaac Asimov", "Stephen Hawking", "John R. Gribbin", "Chris Impey",
            "Carl Sagan", "Charles Petzold", "Chris Sells", "Jon Duckett", "Alan Beaulieu",
            "John M. Zelle"
    };

    private static final String[] CATEGORIES = {
            "History", "Calculus", "Programming", "Science"
    };

    private static final String[][] CATEGORY_BOOKS = {
            {"Appeasement", "Destiny Disrupted", "The Communist Manifesto", "The Plantagenets", "A Photohistory of World War One", "Orientalism", "Democracy", "Lies My Teacher Told Me", "Guns, Germs and Steel"},
            {"Two-Dimensional Calculus", "A First Course in Calculus", "Advance Calculus", "Differential Calculus", "Inside Calculus", "Calculus and Its Origins"},
            {"SamTeach Yourself HTML, CSS, and JavaScript", "Code: The Hidden Language of Computer Hardware and Software", "Windows Forms Programming in C#", "HTML and CSS", "Learning SQL", "Python Programming: An Introduction to Computer Science"},
            {"The Creation of the Universe", "Theories of the Universe", "A Brief History of Time", "Science, society, and the search for life in the universe", "Cosmos"}
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        connection = new Connect();
        executorService = Executors.newSingleThreadExecutor();

        setupProfileButton();
        setupGridView();
        setupCategoryButtons();

        return view;
    }

    private void setupProfileButton() {
        profileButton1 = view.findViewById(R.id.home_profileButton);
        profileButton1.setOnClickListener(v -> {
            DrawerLayout drawerLayout = view.getRootView().findViewById(R.id.drawerLayout);
            drawerLayout.openDrawer(GravityCompat.START);
        });
    }

    private void setupGridView() {
        gridAdapter = new GridAdapter(getActivity(), BOOK_IMAGES, BOOK_TITLES, BOOK_AUTHORS);
        gridView = view.findViewById(R.id.libraries);
        gridView.setAdapter(gridAdapter);

        gridView.setOnItemClickListener((parent, view, position, id) -> {

            String bookTitle = BOOK_TITLES[position];
            String bookAuthor = BOOK_AUTHORS[position];

//            Toast.makeText(getActivity(), "test: "+position, Toast.LENGTH_SHORT).show();
            infoIntent = new Intent(getActivity(), InfoActivity.class);
            int bookId = 16 + position; // Assuming book IDs are sequential and start from 16
            Log.d("Book ID", String.valueOf(bookId));

            getConnect("SELECT * FROM Books WHERE bookid = " + bookId);

            if (getArguments() != null) {
                infoIntent.putExtra("id", position);
                infoIntent.putExtra("bookTitle", bookTitle);

                Log.d("ID", String.valueOf(getArguments().getInt("id")));
            }
            startActivity(infoIntent);
        });
    }

    private void setupCategoryButtons() {
        Button buttonProgramming = view.findViewById(R.id.button_programming);
        Button buttonCalculus = view.findViewById(R.id.button_calculus);
        Button buttonHistory = view.findViewById(R.id.button_history);
        Button buttonScience = view.findViewById(R.id.button_science);

        buttonProgramming.setOnClickListener(v -> filterBooksByCategory(2));
        buttonCalculus.setOnClickListener(v -> filterBooksByCategory(1));
        buttonHistory.setOnClickListener(v -> filterBooksByCategory(0));
        buttonScience.setOnClickListener(v -> filterBooksByCategory(3));
    }

    private void filterBooksByCategory(int categoryIndex) {
        String[] filteredBookTitles = CATEGORY_BOOKS[categoryIndex];
        ArrayList<Integer> filteredBookImages = new ArrayList<>();
        ArrayList<String> filteredBookAuthors = new ArrayList<>();

        for (String title : filteredBookTitles) {
            for (int i = 0; i < BOOK_TITLES.length; i++) {
                if (BOOK_TITLES[i].equals(title)) {
                    filteredBookImages.add(BOOK_IMAGES[i]);
                    filteredBookAuthors.add(BOOK_AUTHORS[i]);
                }
            }
        }

        gridAdapter = new GridAdapter(getActivity(),
                filteredBookImages.stream().mapToInt(i -> i).toArray(),
                filteredBookTitles,
                filteredBookAuthors.toArray(new String[0])
        );
        gridView.setAdapter(gridAdapter);
    }

    private void getConnect(String query) {
        executorService.execute(() -> {
            try (Connection conn = connection.CONN()) {
                if (conn == null) {
                    getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Connection is null", Toast.LENGTH_SHORT).show());
                    return;
                }

                try (PreparedStatement preparedStatement = conn.prepareStatement(query);
                     ResultSet rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        infoIntent.putExtra("title", rs.getString("bookname"));
                        infoIntent.putExtra("author", rs.getString("authorname"));
                        infoIntent.putExtra("year", rs.getInt("year"));
                        infoIntent.putExtra("category", rs.getString("category"));
                        infoIntent.putExtra("publisher", rs.getString("publisher"));
                        infoIntent.putExtra("isbn", rs.getString("isbn"));
                        infoIntent.putExtra("language", rs.getString("language"));
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
