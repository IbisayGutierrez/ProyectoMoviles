package com.example.proyectomoviles;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuAdapter extends BaseAdapter {

    private Context context;
    private String[] titulos;
    private int[] iconos;

    // Constructor
    public MenuAdapter(Context context, String[] titulos, int[] iconos) {
        this.context = context;
        this.titulos = titulos;
        this.iconos = iconos;
    }

    @Override
    public int getCount() {
        return titulos.length;
    }

    @Override
    public Object getItem(int position) {
        return titulos[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.listview_menu, parent, false);
        }

        ImageView icono = view.findViewById(R.id.imageViewContacto);
        TextView titulo = view.findViewById(R.id.textViewNombre);

        icono.setImageResource(iconos[position]);
        titulo.setText(titulos[position]);

        return view;
    }
}
