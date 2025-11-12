package com.example.proyectomoviles;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CustomAdapterPelicula extends BaseAdapter {

    Context context;
    List<Pelicula> lst;

    public CustomAdapterPelicula(Context context, List<Pelicula> lst) {
        this.context = context;
        this.lst = lst;
    }

    @Override
    public int getCount() {
        return lst.size();
    }

    @Override
    public Object getItem(int position) {
        return lst.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        ImageView ImageViewPelicula;
        TextView TextViewCodigo;
        TextView TextViewTitulo;

        Pelicula p=lst.get(i);

        if (view==null)
            view= LayoutInflater.from(context).inflate(R.layout.listview_personalizado,null);

        ImageViewPelicula=view.findViewById(R.id.imageViewContacto);
        TextViewCodigo=view.findViewById(R.id.textViewNombre);
        TextViewTitulo=view.findViewById(R.id.textViewDes);

        ImageViewPelicula.setImageResource(R.drawable.cineverse);
        TextViewCodigo.setText(String.valueOf(p.getCodigo()));
        TextViewTitulo.setText(p.getTitulo());

        return view;

    }
    public void remove(Pelicula pelicula) {
        lst.remove(pelicula);
        notifyDataSetChanged();
    }

}
