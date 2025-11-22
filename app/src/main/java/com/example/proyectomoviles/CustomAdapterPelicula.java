package com.example.proyectomoviles;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Locale;

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
        TextView TextViewDuracion;
        TextView TextViewTitulo;
        TextView TextViewLongitud;
        TextView TextViewLatitud;

        Pelicula p = lst.get(i);
        if (view == null)
            view = LayoutInflater.from(context).inflate(R.layout.listview_personalizado, null);

        ImageViewPelicula = view.findViewById(R.id.imageViewContacto);
        TextViewDuracion = view.findViewById(R.id.textViewDes);
        TextViewTitulo = view.findViewById(R.id.textViewNombre);
        TextViewLongitud = view.findViewById(R.id.textViewLon);
        TextViewLatitud = view.findViewById(R.id.textViewLat);
        byte[] imagenBytes = p.getImagen();
        if (imagenBytes != null && imagenBytes.length > 0) {
            Bitmap bmp = BitmapFactory.decodeByteArray(imagenBytes, 0, imagenBytes.length);
            ImageViewPelicula.setImageBitmap(bmp);
        } else {
            ImageViewPelicula.setImageResource(R.drawable.cineverse);
        }
        TextViewDuracion.setText(p.getDuracion() + " " + context.getString(R.string.minutos));
        TextViewTitulo.setText(p.getTitulo());
        try {
            double lat = Double.parseDouble(p.getLatitud());
            double lon = Double.parseDouble(p.getLongitud());

            String lat4 = String.format(Locale.US, "%.4f", lat);
            String lon4 = String.format(Locale.US, "%.4f", lon);

            TextViewLatitud.setText("Lat: " + lat4);
            TextViewLongitud.setText("Lon: " + lon4);
        } catch (NumberFormatException e) {

            TextViewLatitud.setText("Lat: " + p.getLatitud());
            TextViewLongitud.setText("Lon: " + p.getLongitud());
        }
        return view;
    }

    public void remove(Pelicula pelicula) {
        lst.remove(pelicula);
        notifyDataSetChanged();
    }

    public void clear() {
        lst.clear();
        notifyDataSetChanged();
    }

    public void add(Pelicula pelicula) {
        lst.add(pelicula);
        notifyDataSetChanged();
    }

}
