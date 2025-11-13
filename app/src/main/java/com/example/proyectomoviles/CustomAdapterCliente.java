package com.example.proyectomoviles;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CustomAdapterCliente extends BaseAdapter {
    Context context;
    List<Cliente> lst;

    public CustomAdapterCliente(Context context, List<Cliente> lst) {
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
        ImageView ImageViewCliente;
        TextView TextViewCedula;
        TextView TextViewNombre;

        Cliente c=lst.get(i);

        if (view==null)
            view= LayoutInflater.from(context).inflate(R.layout.listview_personalizado,null);

        ImageViewCliente=view.findViewById(R.id.imageViewContacto);
        TextViewCedula=view.findViewById(R.id.textViewNombre);
        TextViewNombre=view.findViewById(R.id.textViewDes);

        ImageViewCliente.setImageResource(R.drawable.client);
        TextViewCedula.setText(String.valueOf(c.getCedula()));
        TextViewNombre.setText(c.getNombre());

        return view;

    }
    public void remove(Cliente cliente) {
        lst.remove(cliente);
        notifyDataSetChanged();
    }

    public void clear() {
        lst.clear();
        notifyDataSetChanged();
    }

    public void add(Cliente cliente) {
        lst.add(cliente);
        notifyDataSetChanged();
    }
}
