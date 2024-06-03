package com.example.librecord;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class BookmarkFragment extends Fragment {

    private static final String PREF_NAME = "MyBookmarks";
    private static final String KEY_BOOKMARKED = "isBookmarked";

    private ImageButton profileButton;
    private boolean isBookmarked;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookmark, container, false);

        // Initialize SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        isBookmarked = sharedPreferences.getBoolean(KEY_BOOKMARKED, false);

        // Set up profileButton
        profileButton = view.findViewById(R.id.bookmark_profileButton);
        updateBookmarkUI(); // Update UI based on current bookmark state

        // Handle click events for bookmarking
        profileButton.setOnClickListener(v -> {
            toggleBookmarkState();
            updateBookmarkUI();
        });

        return view;
    }

    private void toggleBookmarkState() {
        // Toggle the bookmark state
        isBookmarked = !isBookmarked;

        // Save the updated bookmark state to SharedPreferences
        sharedPreferences.edit().putBoolean(KEY_BOOKMARKED, isBookmarked).apply();

        // Show a toast indicating the bookmark status
        String toastMessage = isBookmarked ? "Bookmarked" : "Removed from bookmarks";
        Toast.makeText(requireContext(), toastMessage, Toast.LENGTH_SHORT).show();
    }

    private void updateBookmarkUI() {
        // Update the UI based on the current bookmark state
        if (isBookmarked) {
            profileButton.setImageResource(R.drawable.profile); // Use built-in star icon for bookmarked
        } else {
            profileButton.setImageResource(R.drawable.profile); // Use built-in star outline for not bookmarked
        }
    }
}






