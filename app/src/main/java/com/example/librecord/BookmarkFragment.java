package com.example.librecord;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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
    private ImageButton editButton;
    private Button removeButton;
    private Connect connection;
    private ExecutorService executorService;
    private GridView bookGridView;
    private String userId, name, email;
    private List<Book> bookmarkedBooks = new ArrayList<>();
    private GridAdapter gridAdapter;
    private boolean isEditMode = false;

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
        editButton = view.findViewById(R.id.edit_button);
        removeButton = view.findViewById(R.id.remove_button);

        profileButton1.setOnClickListener(v -> {
            DrawerLayout drawerLayout = view.getRootView().findViewById(R.id.drawerLayout);
            drawerLayout.openDrawer(GravityCompat.START);
        });

        editButton.setOnClickListener(v -> {
            isEditMode = !isEditMode;
            if (isEditMode) {
                removeButton.setVisibility(View.VISIBLE);
                gridAdapter.setEditMode(true);
            } else {
                removeButton.setVisibility(View.GONE);
                gridAdapter.setEditMode(false);
            }
        });

        removeButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("Are you sure you want to remove these books from your bookmark library?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        List<Book> selectedBooks = gridAdapter.getSelectedBooks();
                        if (selectedBooks.size() > 0) {
                            removeBooksFromBookmark(selectedBooks);
                        }
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
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
                        bookmarkedBooks.clear();
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
                            gridAdapter = new GridAdapter(getActivity(), bookmarkedBooks);
                            bookGridView.setAdapter(gridAdapter);
                            bookGridView.setOnItemClickListener((parent, view, position, id) -> {
                                if (!isEditMode) {
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
                                } else {
                                    gridAdapter.toggleSelection(position);
                                }
                            });
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void removeBooksFromBookmark(List<Book> booksToRemove) {
        executorService.execute(() -> {
            try (Connection conn = connection.CONN()) {
                if (conn == null) {
                    getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Connection is null", Toast.LENGTH_SHORT).show());
                    return;
                }

                String query = "DELETE FROM bookmark WHERE id = ? AND bookid = ?";
                try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
                    for (Book book : booksToRemove) {
                        preparedStatement.setString(1, userId);
                        preparedStatement.setInt(2, book.getBookId());
                        preparedStatement.addBatch();
                    }
                    int[] affectedRows = preparedStatement.executeBatch();

                    getActivity().runOnUiThread(() -> {
                        if (affectedRows.length > 0) {
                            Toast.makeText(getActivity(), "Selected books removed from bookmark library", Toast.LENGTH_SHORT).show();
                            bookmarkedBooks.removeAll(booksToRemove);
                            gridAdapter.setBooks(bookmarkedBooks);
                            gridAdapter.notifyDataSetChanged(); // Ensure the adapter is notified
                        } else {
                            Toast.makeText(getActivity(), "Failed to remove selected books", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        executorService.shutdown();
    }
}
