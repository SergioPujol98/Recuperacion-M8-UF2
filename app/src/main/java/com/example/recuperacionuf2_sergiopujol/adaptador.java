package com.example.recuperacionuf2_sergiopujol;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import java.io.File;
import java.util.ArrayList;

public class adaptador extends ArrayAdapter<items> {

    private Context fcontext;
    private ArrayList<items> alItems;
    public adaptador(@NonNull Context context, ArrayList<items> list) {
        super(context, 0, list);
        fcontext = context;
        alItems = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View lvItem = convertView;
        if(lvItem == null)
            lvItem = LayoutInflater.from(fcontext).inflate(R.layout.adapter,parent,false);

        items nItem = alItems.get(position);

        ImageView imagen = lvItem.findViewById(R.id.imageView_foto);
        imagen.setImageURI(FileProvider.getUriForFile(fcontext, "com.example.recuperacionuf2_sergiopujol", new File(nItem.getFoto())));

        TextView tComentario = lvItem.findViewById(R.id.textView_coment);
        tComentario.setText(nItem.getComentario());
        return lvItem;
    }
}
