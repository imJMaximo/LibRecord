package com.example.librecord;

import static android.content.Context.MODE_PRIVATE;

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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BookmarkFragment extends Fragment {

    private View view;
    private ImageButton profileButton1;
    private Connect connection;
    private ExecutorService executorService;
    private GridView bookGridView;
    private String userId, name, email;
    private List<Book> bookmarkedBooks = new ArrayList<>();

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

        bookGridView = view.findViewById(R.id.bookmark_libraries);
        profileButton1 = view.findViewById(R.id.bookmark_profileButton);
        profileButton1.setOnClickListener(v -> {
            DrawerLayout drawerLayout = view.getRootView().findViewById(R.id.drawerLayout);
            drawerLayout.openDrawer(GravityCompat.START);
        });

        fetchBookmarksFromDatabase();

        return view;
    }

    private void fetchBookmarksFromDatabase() {
        executorService.execute(() -> {
            try (Connection conn = connection.CONN()) {
                if (conn == null) {
                    getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Connection is null", Toast.LENGTH_SHORT).show());
                    return;
                }

                String query = "SELECT b.* FROM books b INNER JOIN bookmark bm ON b.bookid = bm.bookid WHERE bm.id = ?";
                try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
                    preparedStatement.setString(1, userId);
                    try (ResultSet rs = preparedStatement.executeQuery()) {
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
                            book.setImage(rs.getBytes("BookImages"));

                            bookmarkedBooks.add(book);
                        }

                        getActivity().runOnUiThread(() -> {
                            // Initialize and set adapter for the GridView
                            GridAdapter gridAdapter = new GridAdapter(getActivity(), bookmarkedBooks);
                            bookGridView.setAdapter(gridAdapter);
                            // Set click listener for the GridView items
                            bookGridView.setOnItemClickListener((parent, view, position, id) -> {
                                Book selectedBook = bookmarkedBooks.get(position);
                                Intent infoIntent = new Intent(getActivity(), InfoActivity.class);
                                infoIntent.putExtra("bookid", selectedBook.getBookId());
                                infoIntent.putExtra("title", selectedBook.getTitle());
                                infoIntent.putExtra("author", selectedBook.getAuthor());
                                infoIntent.putExtra("year", selectedBook.getYear());
                                infoIntent.putExtra("category", selectedBook.getCategory());
                                infoIntent.putExtra("publisher", selectedBook.getPublisher());
                                infoIntent.putExtra("isbn", selectedBook.getIsbn());
                                infoIntent.putExtra("language", selectedBook.getLanguage());
                                startActivity(infoIntent);
                            });
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
