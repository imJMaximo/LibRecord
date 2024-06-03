package com.example.librecord;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReservationHistoryFragment extends Fragment {

    private LinearLayout reservationContainer;
    private ExecutorService executorService;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservation, container, false);

        reservationContainer = view.findViewById(R.id.constraintLayout);

        executorService = Executors.newSingleThreadExecutor();

        loadReservations();

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }

    private void loadReservations() {
        executorService.execute(() -> {
            List<Reservation> reservations = fetchReservationsFromDatabase();

            if (!reservations.isEmpty() && getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    for (Reservation reservation : reservations) {
                        View reservationView = createReservationView(reservation);
                        reservationContainer.addView(reservationView);
                    }
                });
            }
        });
    }

    private List<Reservation> fetchReservationsFromDatabase() {
        List<Reservation> reservations = new ArrayList<>();
        try (Connection connection = Connect.CONN();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT title, date, date_return, status FROM reservationrecord")) {

            while (resultSet.next()) {
                String bookReserved = resultSet.getString("title");
                String reservationDate = resultSet.getString("date");
                String returnDate = resultSet.getString("date_return");
                String status = resultSet.getString("status");
                reservations.add(new Reservation(bookReserved, reservationDate, returnDate, status));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception or log it
        }
        return reservations;
    }

    private View createReservationView(Reservation reservation) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.item_reservation, reservationContainer, false);

        TextView bookReservedTextView = view.findViewById(R.id.book_reserved);
        TextView reservationDateTextView = view.findViewById(R.id.reservation_date);
        TextView returnDateTextView = view.findViewById(R.id.return_date);
        TextView statusTextView = view.findViewById(R.id.status);

        bookReservedTextView.setText(reservation.getBookReserved());
        reservationDateTextView.setText(reservation.getReservationDate());
        returnDateTextView.setText(reservation.getReturnDate());
        statusTextView.setText(reservation.getStatus());

        return view;
    }

    private static class Reservation {
        private final String bookReserved;
        private final String reservationDate;
        private final String returnDate;
        private final String status;

        public Reservation(String bookReserved, String reservationDate, String returnDate, String status) {
            this.bookReserved = bookReserved;
            this.reservationDate = reservationDate;
            this.returnDate = returnDate;
            this.status = status;
        }

        public String getBookReserved() {
            return bookReserved;
        }

        public String getReservationDate() {
            return reservationDate;
        }

        public String getReturnDate() {
            return returnDate;
        }

        public String getStatus() {
            return status;
        }
    }
}