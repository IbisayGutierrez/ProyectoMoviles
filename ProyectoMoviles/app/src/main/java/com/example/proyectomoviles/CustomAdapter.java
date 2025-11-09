package com.example.proyectomoviles;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CustomAdapter extends BaseAdapter {

    Context context;
    List<Pelicula> lst;

    public CustomAdapter(Context context, List<Pelicula> lst) {
        this.context = context;
        this.lst = lst;
    }

    @Override
    public int getCount() {
        return lst.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        ImageView ImageViewContacto;
        TextView TextViewNombre;
        TextView TextViewDes;

        Pelicula p=lst.get(i);

        if (view==null)
            view= LayoutInflater.from(context).inflate(R.layout.listview,null);

        ImageViewContacto=view.findViewById(R.id.imageViewContacto);
        TextViewNombre=view.findViewById(R.id.textViewNombre);
        TextViewDes=view.findViewById(R.id.textViewDes);

        //ImageViewContacto.setImageResource(p.imagen);
        //TextViewNombre.setText(p.nombre);
       // TextViewDes.setText(p.Des);

        return view;

    }
}
