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

public class SearchFragment extends Fragment {

    View view;
    ImageButton profileButton1;
    EditText searchBar;
    ImageButton search;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_search, container, false);

        searchBar = view.findViewById(R.id.searchBar);
        search = view.findViewById(R.id.search);

        profileButton1 = (ImageButton) view.findViewById(R.id.search_profileButton);
        profileButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawerLayout = view.getRootView().findViewById(R.id.drawerLayout);

                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        int[] bookImages = {
                R.drawable.histo1, R.drawable.histo2, R.drawable.prog3, R.drawable.calcu4, R.drawable.histo5,
                R.drawable.histo6, R.drawable.histo7, R.drawable.histo8, R.drawable.histo9, R.drawable.histo10,
                R.drawable.histo11, R.drawable.calcu12, R.drawable.calcu13, R.drawable.calcu14, R.drawable.calcu15,
                R.drawable.calcu16, R.drawable.sci17, R.drawable.sci18, R.drawable.sci19, R.drawable.sci20,
                R.drawable.sci22, R.drawable.prog23, R.drawable.prog24, R.drawable.prog25, R.drawable.prog26,
                R.drawable.prog27
        };
        String[] bookTitles = {
                "Appeasement", "Destiny Disrupted", "SamTeach Yourself HTML, CSS, and JavaScript", "Two-Dimensional Calculus", "The Communist Manifesto",
                "The Plantagenets", "A Photohistory of World War One", "Orientalism", "Democracy", "Lies My Teacher Told Me",
                "Guns, Germs and Steel", "A First Course in Calculus", "Advance Calculus", "Differential Calculus", "Inside Calculus",
                "Calculus and Its Origins", "The Creation of the Universe", "Theories of the Universe", "A Brief History of Time", "Science, society, and the search for life in the universe",
                "Cosmos", "Code: The Hidden Language of Computer Hardware and Software", "Windows Forms Programming in C#", "HTML and CSS",
                "Learning SQL", "Python Programming: An Introduction to Computer Science"

        };

        String[] bookAuthors = {
                "Tim Bouverie", "Tamim Ansary", "Julie C. Meloni", "Robert Osserman", "Karl Marx, Friedrich Engels, and David McLellan",
                "Dan Jones", "Haythornthwaite, Philip J.", "Edward W. Said", "Paul Cartledge", "James W. Loewen",
                "Jared Diamond", "Serge Lang", "Patrick Fitzpatrick", "S Balachandra Rao", "George R. Exner",
                "David Perkins", "George Gamow", "Milton K. Munitz", "Stephen Hawking", "Bruce M. Jakosky",
                "Carl Sagan", "Charles Petzold", "Chris Sells", "Jon Duckett", "Alan Beaulieu",
                "John M. Zelle"

        };

        GridAdapter gridAdapter = new GridAdapter(getActivity(),bookImages, bookTitles, bookAuthors);
        GridView gridView = (GridView) view.findViewById(R.id.libraries);
        gridView.setAdapter(gridAdapter);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchBar.getText().toString().trim(); // Get the search query
                if (!query.isEmpty()) {
                    gridAdapter.getFilter().filter(query); // Call the filtering method with the search query
                } else {
                    // If the query is empty, show the original data
                    gridAdapter.getFilter().filter(null);
                }
            }
        });

        return view;
    }
}