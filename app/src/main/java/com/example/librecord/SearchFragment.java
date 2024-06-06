package com.example.librecord;

import android.os.Bundle;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SearchFragment extends Fragment {

    private View view;
    private ImageButton profileButton1;
    private EditText searchBar;
    private ImageButton search;
    private GridAdapter gridAdapter;
    private List<Book> books;
    private ExecutorService executorService;
    private Connect connection;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_search, container, false);

        searchBar = view.findViewById(R.id.searchBar);
        search = view.findViewById(R.id.search);

        profileButton1 = view.findViewById(R.id.search_profileButton);
        profileButton1.setOnClickListener(v -> {
            DrawerLayout drawerLayout = view.getRootView().findViewById(R.id.drawerLayout);
            drawerLayout.openDrawer(GravityCompat.START);
        });

        books = new ArrayList<>();
        gridAdapter = new GridAdapter(getActivity(), books);
        GridView gridView = view.findViewById(R.id.libraries);
        gridView.setAdapter(gridAdapter);

        executorService = Executors.newSingleThreadExecutor();
        connection = new Connect();

        fetchBooksFromDatabase();

        search.setOnClickListener(v -> {
            String query = searchBar.getText().toString().trim(); // Get the search query
            if (!query.isEmpty()) {
                gridAdapter.getFilter().filter(query); // Call the filtering method with the search query
            } else {
                // If the query is empty, show the original data
                gridAdapter.getFilter().filter(null);
            }
        });

        return view;
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
                        book.setBookId(rs.getInt("bookid"));
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
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
