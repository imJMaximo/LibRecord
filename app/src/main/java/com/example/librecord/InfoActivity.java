package com.example.librecord;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.concurrent.*;

public class InfoActivity extends AppCompatActivity {
    TextView title, author, year, category, publisher, isbn, language;
    ImageView book;
    Button reserve;
    ImageButton bookmarkButton;
    private String user_id;
    DatePickerDialog datePickerDialog;
    Calendar calendar;
    Connect connection;
    Connection conn;
    String username;

    private String userId, name, email, checkerErr = "good";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        connection = new Connect();

        SharedPreferences sharedPreferences = getSharedPreferences("userDetails", MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", "");
        email = sharedPreferences.getString("email", "");
        name = sharedPreferences.getString("name", "");

        if (userId.isEmpty()) {
            Toast.makeText(this, "User ID is missing. Please log in again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        user_id = userId;

        book = findViewById(R.id.bookimage);
        title = findViewById(R.id.title);
        author = findViewById(R.id.author);
        year = findViewById(R.id.year);
        category = findViewById(R.id.category);
        publisher = findViewById(R.id.publisher);
        isbn = findViewById(R.id.isbn);
        language = findViewById(R.id.language);
        reserve = findViewById(R.id.reserve);
        bookmarkButton = findViewById(R.id.bookmark);

        ImageView backButton = findViewById(R.id.info_back_btn);
        backButton.setOnClickListener(v -> onBackPressed());

        Intent intent = getIntent();
        int bookId = intent.getIntExtra("bookid", -1);

        if (bookId != -1) {
            fetchBookDetails(bookId);
        } else {
            Toast.makeText(this, "Book ID not found", Toast.LENGTH_SHORT).show();
        }

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                conn = connection.CONN();
                String query = "SELECT * FROM accountdata WHERE id = ?";
                PreparedStatement preparedStatement = conn.prepareStatement(query);
                preparedStatement.setString(1, user_id);
                ResultSet rs = preparedStatement.executeQuery();

                if (rs.next()) {
                    username = rs.getString("username");
                }
            } catch (SQLException e) {
                Log.e("InfoActivity", "Database error: ", e);
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        reserve.setOnClickListener(v -> showCustomDatePickerDialog());
        bookmarkButton.setOnClickListener(this::onBookmarkClick);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void fetchBookDetails(int bookId) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            String query = "SELECT * FROM books WHERE bookid = ?";
            try (Connection conn = connection.CONN();
                 PreparedStatement preparedStatement = conn.prepareStatement(query)) {

                preparedStatement.setInt(1, bookId);
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        String rsbookName = rs.getString("BookName");
                        String rsauthorName = rs.getString("AuthorName");
                        String rsyear = rs.getString("Year");
                        String rscategory = rs.getString("Category");
                        String rspublisher = rs.getString("Publisher");
                        String rsisbn = rs.getString("ISBN");
                        String rslanguage = rs.getString("Language");
                        byte[] imageBytes = rs.getBytes("BookImages");

                        runOnUiThread(() -> {
                            title.setText(rsbookName);
                            author.setText(rsauthorName);
                            year.setText(rsyear);
                            category.setText(rscategory);
                            publisher.setText(rspublisher);
                            isbn.setText(rsisbn);
                            language.setText(rslanguage);

                            if (imageBytes != null) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                                book.setImageBitmap(bitmap);
                            } else {
                                book.setImageResource(R.drawable.calcu4);
                            }
                        });
                    }
                }
            } catch (SQLException e) {
                Log.e("InfoActivity", "Database error: ", e);
            } finally {
                executorService.shutdown();
            }
        });
    }

    // Added method to check if the user has already bookmarked the book
    private void checkIfBookmarked() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                conn = connection.CONN();
                String query = "SELECT COUNT(*) AS count FROM bookmark WHERE bookid = ? AND id = ?";
                PreparedStatement preparedStatement = conn.prepareStatement(query);
                preparedStatement.setInt(1, getIntent().getIntExtra("bookid", -1));
                preparedStatement.setString(2, user_id);
                ResultSet rs = preparedStatement.executeQuery();

                if (rs.next()) {
                    int count = rs.getInt("count");
                    if (count > 0) {
                        runOnUiThread(() -> Toast.makeText(this, "You already bookmarked this book.", Toast.LENGTH_SHORT).show());
                    } else {
                        // Proceed with bookmarking the book
                        bookmarkBook();
                    }
                }
            } catch (SQLException e) {
                Log.d("Connection Error", "error", e);
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void onBookmarkClick(View view) {
        checkIfBookmarked();
    }

    // Modified method to bookmark the book
    private void bookmarkBook() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                conn = connection.CONN();
                String query = "INSERT INTO bookmark (bookid, id, username) VALUES (?, ?, ?)";
                PreparedStatement preparedStatement = conn.prepareStatement(query);
                preparedStatement.setInt(1, getIntent().getIntExtra("bookid", -1));
                preparedStatement.setString(2, user_id);
                preparedStatement.setString(3, username);
                preparedStatement.executeUpdate();
                runOnUiThread(() -> Toast.makeText(this, "Bookmarked successfully", Toast.LENGTH_SHORT).show());
            } catch (SQLException e) {
                Log.d("Connection Error", "error", e);
                runOnUiThread(() -> Toast.makeText(this, "Failed to bookmark", Toast.LENGTH_SHORT).show());
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showCustomDatePickerDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_date_picker);

        DatePicker datePicker = dialog.findViewById(R.id.datePicker);
        Button confirmButton = dialog.findViewById(R.id.confirmButton);
        Button cancelButton = dialog.findViewById(R.id.cancelButton);

        Calendar c = Calendar.getInstance();
        long minDate = c.getTimeInMillis();
        c.add(Calendar.YEAR, 1);
        long maxDate = c.getTimeInMillis();
        datePicker.setMinDate(minDate);
        datePicker.setMaxDate(maxDate);

        confirmButton.setOnClickListener(v -> {
            int day = datePicker.getDayOfMonth();
            int month = datePicker.getMonth();
            int year = datePicker.getYear();

            String bookTitle = title.getText().toString();
            if (checkBookifAvailable(bookTitle)) {
                if (checkReservation(user_id)) {
                    if (checkDateReservation(bookTitle, year, month, day)) {
                        // Proceed with reservation only if the user has not exceeded the reservation count
                        int reservedCount = countReservedBooks(user_id);
                        if (reservedCount >= 3) {
                            Toast.makeText(this, "Please return a book first to continue reserving a book.", Toast.LENGTH_SHORT).show();
                        } else {
                            processReservation(username, bookTitle, year, month, day);
                            Executors.newSingleThreadExecutor().execute(() -> {
                                try {
                                    Thread.sleep(3000); // Wait for reservation process to complete
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                runOnUiThread(() -> {
                                    if (checkerErr.equals("good")) {
                                        Intent reserveActivity = new Intent(InfoActivity.this, ReservationCompleteActivity.class);
                                        startActivity(reserveActivity);
                                    }
                                    dialog.dismiss();
                                });
                            });
                        }
                    } else {
                        Toast.makeText(this, "Please select another date", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    Toast.makeText(this, "Please return a book first to make a reservation", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                Toast.makeText(this, "The book is currently reserved", Toast.LENGTH_SHORT).show();
                return;
            }
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    public boolean checkDateReservation(String title, int year, int month, int day) {
        String date = String.format("%04d-%02d-%02d", year, month + 1, day);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Boolean> future = executorService.submit(() -> {
            try {
                conn = connection.CONN();
                String query = "SELECT COUNT(*) as count FROM reservationrecord WHERE title = ? AND ((date <= ? AND date_return >= ?))";
                PreparedStatement preparedStatement = conn.prepareStatement(query);
                preparedStatement.setString(1, title);
                preparedStatement.setString(2, date);
                preparedStatement.setString(3, date);
                ResultSet rs = preparedStatement.executeQuery();

                if (rs.next()) {
                    int count = rs.getInt("count");
                    return count == 0;
                }
            } catch (SQLException e) {
                Log.d("Connection Error", "error", e);
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return false;
        });

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return false;
        } finally {
            executorService.shutdown();
        }
    }

    public boolean checkReservation(String id) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Boolean> future = executorService.submit(() -> {
            try {
                conn = connection.CONN();
                String query = "SELECT COUNT(*) as count FROM reservationrecord WHERE id = ? AND status = 'Borrowed'";
                PreparedStatement preparedStatement = conn.prepareStatement(query);
                preparedStatement.setString(1, id);
                ResultSet rs = preparedStatement.executeQuery();

                if (rs.next()) {
                    int count = rs.getInt("count");
                    return count == 0;
                }
            } catch (SQLException e) {
                Log.d("Connection Error", "error", e);
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return false;
        });

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return false;
        } finally {
            executorService.shutdown();
        }
    }

    public boolean checkBookifAvailable(String title) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Boolean> future = executorService.submit(() -> {
            try {
                conn = connection.CONN();
                String query = "SELECT COUNT(*) as count FROM reservationrecord WHERE title = ? AND status = 'Borrowed'";
                PreparedStatement preparedStatement = conn.prepareStatement(query);
                preparedStatement.setString(1, title);
                ResultSet rs = preparedStatement.executeQuery();

                if (rs.next()) {
                    int count = rs.getInt("count");
                    return count == 0;
                }
            } catch (SQLException e) {
                Log.d("Connection Error", "error", e);
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return false;
        });

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return false;
        } finally {
            executorService.shutdown();
        }
    }

    public void processReservation(String username, String title, int year, int month, int day) {
        String date = String.format("%04d-%02d-%02d", year, month + 1, day);
        String date_return = String.format("%04d-%02d-%02d", year, month + 1, day + 3);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                conn = connection.CONN();
                String query = "INSERT INTO reservationrecord (id, username, title, date, status, date_return) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement preparedStatement = conn.prepareStatement(query);
                preparedStatement.setString(1, user_id);
                preparedStatement.setString(2, username);
                preparedStatement.setString(3, title);
                preparedStatement.setString(4, date);
                preparedStatement.setString(5, "Reserved");
                preparedStatement.setString(6, date_return);
                preparedStatement.executeUpdate();
                checkerErr = "good";
            } catch (SQLException e) {
                Log.d("Connection Error", "error", e);
                checkerErr = "error";
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private int countReservedBooks(String userId) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Integer> future = executorService.submit(() -> {
            int count = 0;
            try {
                conn = connection.CONN();
                String query = "SELECT COUNT(*) AS count FROM reservationrecord WHERE id = ? AND status = 'Reserved'";
                PreparedStatement preparedStatement = conn.prepareStatement(query);
                preparedStatement.setString(1, userId);
                ResultSet rs = preparedStatement.executeQuery();

                if (rs.next()) {
                    count = rs.getInt("count");
                }
            } catch (SQLException e) {
                Log.d("Connection Error", "error", e);
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return count;
        });

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return 0;
        } finally {
            executorService.shutdown();
        }
    }

}
