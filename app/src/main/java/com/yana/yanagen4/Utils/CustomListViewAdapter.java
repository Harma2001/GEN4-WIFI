package com.yana.yanagen4.Utils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import com.yana.yanagen4.R;
public class CustomListViewAdapter extends ArrayAdapter<String>
{
    private final Activity context;
    private final String[] web;
    private final String[] web2;

    private final int imageId;

    public CustomListViewAdapter(Activity context, String[] web, String[] web2, int imageId) {
        super(context, R.layout.pairedlist_item, web);
        this.context = context;
        this.web = web;
        this.web2 = web2;

        this.imageId = imageId;
    }

    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.pairedlist_item, null, true);
        TextView txtTitle = rowView.findViewById(R.id.txt);
        TextView txtTitle2 = rowView.findViewById(R.id.txt2);

        ImageView imageView = rowView.findViewById(R.id.img);
        txtTitle.setText(web[position]);
        txtTitle2.setText(web2[position]);

        imageView.setImageResource(R.drawable.ic_bluetooth);
        return rowView;
    }
}
