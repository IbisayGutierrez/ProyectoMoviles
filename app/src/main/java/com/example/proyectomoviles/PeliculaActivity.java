package com.example.proyectomoviles;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class PeliculaActivity extends AppCompatActivity {

    ListView lista;
    CustomAdapterPelicula adapter;
    ArrayList<Pelicula> datos;
    int itemseleccionado = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pelicula);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        lista = findViewById(R.id.ListViewMenu);

        datos = new ArrayList<>();
        adapter = new CustomAdapterPelicula(this, datos);
        lista.setAdapter(adapter);
        cargarPeliculas();
        lista.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            itemseleccionado = position;

            for (int i = 0; i < lista.getChildCount(); i++) {
                lista.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
            }

            view.setBackgroundColor(Color.GREEN);
        });
    }

    public void Regresar(View view)
    {
        Intent intent= new Intent(this,MainActivity.class);
        startActivity(intent);

    }

    public void Agregar(View view)
    {
        Intent intent= new Intent(this,CrearPelicula.class);
        startActivity(intent);

    }

    private void cargarPeliculas() {
        AdminDB admin = new AdminDB(this, "Proyecto", null, 1);
        SQLiteDatabase bd = admin.getReadableDatabase();
        Cursor fila = bd.rawQuery("SELECT codigo, titulo, duracion, genero FROM pelicula", null);
        if (fila.moveToFirst()) {
            do {
                int codigo = fila.getInt(0);
                String titulo = fila.getString(1);
                int duracion = fila.getInt(2);
                String genero = fila.getString(3);
                Pelicula p = new Pelicula(codigo, titulo, duracion, genero);
                datos.add(p);
            } while (fila.moveToNext());
        } else {
            Toast.makeText(this, "No hay películas registradas", Toast.LENGTH_SHORT).show();
        }
        fila.close();
        bd.close();
        adapter.notifyDataSetChanged();
    }


}