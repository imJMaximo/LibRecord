package com.example.librecord;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class GridAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private List<Book> books;
    private List<Book> originalData;
    private List<Book> filteredData;
    private LayoutInflater inflater;

    public GridAdapter(Context context, List<Book> books) {
        this.context = context;
        this.books = books;
        this.originalData = new ArrayList<>(books);
        this.filteredData = new ArrayList<>(books);
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setBooks(List<Book> books) {
        this.books = books;
        this.originalData = new ArrayList<>(books);
        this.filteredData = new ArrayList<>(books);
        notifyDataSetChanged();
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
        if (inflater == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.book_layout, null);
        }

        ImageView imageView = convertView.findViewById(R.id.bookimage);
        TextView title = convertView.findViewById(R.id.booktitle);
        TextView author = convertView.findViewById(R.id.author);

        Book book = filteredData.get(position);

        if (book.getImage() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(book.getImage(), 0, book.getImage().length);
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageResource(R.drawable.calcu4);
        }
        title.setText(book.getTitle());
        author.setText(book.getAuthor());

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<Book> filteredList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(originalData);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Book book : originalData) {
                        if (book.getTitle().toLowerCase().contains(filterPattern) ||
                                book.getAuthor().toLowerCase().contains(filterPattern)) {
                            filteredList.add(book);
                        }
                    }
                }

                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredData.clear();
                filteredData.addAll((List<Book>) results.values);
                notifyDataSetChanged();
            }
        };
    }
}
