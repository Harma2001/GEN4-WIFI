package com.yana.yanagen4.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yana.yanagen4.R;


public class FTPCustomListView extends ArrayAdapter<String>
{
    private final Activity context;
    private final String[] web;
    private final int imageId;

    public FTPCustomListView(Activity context, String[] web, int imageId) {
        super(context, R.layout.ftplistitems, web);
        this.context = context;
        this.web = web;
        this.imageId = imageId;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.ftplistitems, null, true);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        TextView txtTitle =  rowView.findViewById(R.id.txt);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        ImageView imageView =  rowView.findViewById(R.id.img);
        txtTitle.setText(web[position]);

        imageView.setImageResource(R.drawable.hex_grid);
        return rowView;
    }
}
