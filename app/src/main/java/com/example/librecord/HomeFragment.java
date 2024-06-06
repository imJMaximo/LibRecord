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

    private ArrayList<Book> books = new ArrayList<>();

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
        gridAdapter = new GridAdapter(getActivity(), books);
        gridView = view.findViewById(R.id.libraries);
        gridView.setAdapter(gridAdapter);

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            Book selectedBook = (Book) gridAdapter.getItem(position);
            infoIntent = new Intent(getActivity(), InfoActivity.class);
            infoIntent.putExtra("bookid", selectedBook.getId());
            infoIntent.putExtra("title", selectedBook.getTitle());
            infoIntent.putExtra("author", selectedBook.getAuthor());
            infoIntent.putExtra("year", selectedBook.getYear());
            infoIntent.putExtra("category", selectedBook.getCategory());
            infoIntent.putExtra("publisher", selectedBook.getPublisher());
            infoIntent.putExtra("isbn", selectedBook.getIsbn());
            infoIntent.putExtra("language", selectedBook.getLanguage());
            startActivity(infoIntent);
        });
    }

    private void setupCategoryButtons() {
        Button buttonProgramming = view.findViewById(R.id.button_programming);
        Button buttonCalculus = view.findViewById(R.id.button_calculus);
        Button buttonHistory = view.findViewById(R.id.button_history);
        Button buttonScience = view.findViewById(R.id.button_science);

        buttonProgramming.setOnClickListener(v -> filterBooksByCategory("Programming"));
        buttonCalculus.setOnClickListener(v -> filterBooksByCategory("Calculus"));
        buttonHistory.setOnClickListener(v -> filterBooksByCategory("History"));
        buttonScience.setOnClickListener(v -> filterBooksByCategory("Science"));
    }


    //Filter Books by category and clear the existing list of filtered books before adding new ones
    private void filterBooksByCategory(String category) {
        ArrayList<Book> filteredBooks = new ArrayList<>();
        for (Book book : books) {
            if (book.getCategory().equalsIgnoreCase(category)) {
                filteredBooks.add(book);
            }
        }
        gridAdapter.setBooks(filteredBooks);
    }

    private void fetchBooksFromDatabase() {
        executorService.execute(() -> {
            try (Connection conn = connection.CONN()) {
                if (conn == null) {
                    getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Connection is null", Toast.LENGTH_SHORT).show());
                    return;
                }

                String query = "SELECT * FROM books";
                try (PreparedStatement preparedStatement = conn.prepareStatement(query);
                     ResultSet rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        Book book = new Book();
                        book.setId(rs.getInt("bookid"));
                        book.setTitle(rs.getString("BookName"));
                        book.setAuthor(rs.getString("AuthorName"));
                        book.setYear(rs.getInt("Year"));
                        book.setCategory(rs.getString("Category"));
                        book.setPublisher(rs.getString("Publisher"));
                        book.setIsbn(rs.getString("ISBN"));
                        book.setLanguage(rs.getString("Language"));
                        book.setImage(rs.getBytes("BookImages")); // Fetch image data

                        books.add(book);
                    }

                    getActivity().runOnUiThread(() -> gridAdapter.setBooks(books));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchBooksFromDatabase();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}