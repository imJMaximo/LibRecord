package com.example.librecord;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

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
                String query = "SELECT * FROM accountdata WHERE id = " + user_id;
                PreparedStatement preparedStatement = conn.prepareStatement(query);
                ResultSet rs = preparedStatement.executeQuery();

                if (rs.next()) {
                    username = rs.getString("username");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        Log.d("InfoActivity", "username " + username);

        reserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomDatePickerDialog();
            }
        });

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

    private void showCustomDatePickerDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_date_picker);

        DatePicker datePicker = dialog.findViewById(R.id.datePicker);
        Button confirmButton = dialog.findViewById(R.id.confirmButton);
        Button cancelButton = dialog.findViewById(R.id.cancelButton);

        datePickerDialog = new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> { },
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

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
            if(checkBookifAvailable(bookTitle)){
                if(checkReservation(user_id)){
                    if(checkDateReservation(bookTitle, year, month, day)){
                        processReservation(username, bookTitle, year, month, day);
                    }else{
                        Toast.makeText(this, "Please select another date", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }else{
                    Toast.makeText(this, "Please return a book first to make a reservation", Toast.LENGTH_SHORT).show();
                    return;
                }
            }else{
                Toast.makeText(this, "The book is currently reserved", Toast.LENGTH_SHORT).show();
                return;
            }

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
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    public boolean checkReservation(String user_id) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Boolean> future = executorService.submit(() -> {
            try {
                conn = connection.CONN();
                String query = "SELECT COUNT(*) as count FROM reservationrecord WHERE id = ? AND status = 'Reserved'";
                PreparedStatement preparedStatement = conn.prepareStatement(query);
                preparedStatement.setString(1, user_id);

                ResultSet rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    int count = rs.getInt("count");
                    return count < 3;
                }
            } catch (SQLException e) {
                Log.d("Connection Error", "error", e);
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

    public boolean checkDateReservation(String title, int year, int month, int day) {
        String date = String.format("%04d-%02d-%02d", year, month + 1, day);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Boolean> future = executorService.submit(() -> {
            try {
                conn = connection.CONN();
                String query = "SELECT COUNT(*) as count FROM reservationrecord WHERE title = ? AND ((date >= ? AND date <= DATE_ADD(?, INTERVAL 3 DAY)) OR (date_return >= ? AND date_return <= DATE_ADD(?, INTERVAL 3 DAY)))";
                PreparedStatement preparedStatement = conn.prepareStatement(query);
                preparedStatement.setString(1, title);
                preparedStatement.setString(2, date);
                preparedStatement.setString(3, date);
                preparedStatement.setString(4, date);
                preparedStatement.setString(5, date);
                ResultSet rs = preparedStatement.executeQuery();

                if (rs.next()) {
                    int count = rs.getInt("count");
                    return count == 0;
                }
            } catch (SQLException e) {
                Log.d("Connection Error", "error", e);
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
                String query = "SELECT COUNT(*) as count FROM reservationrecord WHERE title = ? AND status = 'Reserved' AND username = ?";
                PreparedStatement preparedStatement = conn.prepareStatement(query);
                preparedStatement.setString(1, title);
                preparedStatement.setString(2, username);
                ResultSet rs = preparedStatement.executeQuery();

                if (rs.next()) {
                    int count = rs.getInt("count");
                    return count == 0;
                }
            } catch (SQLException e) {
                Log.d("Connection Error", "error", e);
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

    public void processReservation(String name, String bookTitle, int year, int month, int day) {
        String date = String.format("%04d-%02d-%02d", year, month + 1, day);
        String returnDate = calculateReturnDate(year, month, day);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                conn = connection.CONN();
                String query = "INSERT INTO reservationrecord (id, username, title, date, status, date_return) VALUES (?, ?, ?, STR_TO_DATE(?, '%Y-%m-%d'), 'Reserved', STR_TO_DATE(?, '%Y-%m-%d'))";
                try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
                    preparedStatement.setString(1, user_id);
                    preparedStatement.setString(2, name);
                    preparedStatement.setString(3, bookTitle);
                    preparedStatement.setString(4, date);
                    preparedStatement.setString(5, returnDate);
                    preparedStatement.execute();
                    checkerErr = "good";
                }
            } catch (SQLException e) {
                checkerErr = "error";
                Log.d("Connection Error", "error", e);
            }
        });
    }

    private String calculateReturnDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        calendar.add(Calendar.DAY_OF_MONTH, 3);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(calendar.getTime());
    }

    public void onBookmarkClick(View view) {
        saveBookmark();
        openBookmarkFragment();
    }

    private void saveBookmark() {
        String bookTitle = title.getText().toString();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                MySQLHelperBookmark.addBookmark(user_id, bookTitle, name);
                runOnUiThread(() -> Toast.makeText(InfoActivity.this, "Bookmark Saved", Toast.LENGTH_SHORT).show());
            } catch (SQLException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(InfoActivity.this, "Error Saving Bookmark", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void openBookmarkFragment() {
        BookmarkFragment fragment = new BookmarkFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.activityInfo, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
