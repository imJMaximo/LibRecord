package com.example.librecord;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.*;
import java.util.*;
import java.util.ArrayList;
import java.util.List;

public class GridAdapter extends BaseAdapter implements Filterable {

    Context context;
    int bookImages[];
    String bookTitle[], bookAuthor[];
    LayoutInflater inflater;
    List<String> originalData;
    List<String> filteredData;

    public GridAdapter(Context context, int[] bookImages, String[] bookTitle, String[] bookAuthor) {
        this.context = context;
        this.bookImages = bookImages;
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        originalData = new ArrayList<>(Arrays.asList(bookTitle));
        filteredData = new ArrayList<>(originalData);
    }


    @Override
    public int getCount() {
        return filteredData.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null){
            convertView = inflater.inflate(R.layout.book_layout, null);
        }

        ImageView imageView = convertView.findViewById(R.id.bookimage);
        TextView title = convertView.findViewById(R.id.booktitle);
        TextView author = convertView.findViewById(R.id.author);

        String currentTitle = filteredData.get(position);
        int originalPosition = originalData.indexOf(currentTitle);

        imageView.setImageResource(bookImages[originalPosition]);
        title.setText(bookTitle[originalPosition]);
        author.setText(bookAuthor[originalPosition]);
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<String> filteredList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(originalData);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (String item : originalData) {
                        if (item.toLowerCase().contains(filterPattern)) {
                            filteredList.add(item);
                        }
                    }
                }

                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredData.clear();
                filteredData.addAll((List<String>) results.values);
                notifyDataSetChanged();
            }
        };
    }
}
