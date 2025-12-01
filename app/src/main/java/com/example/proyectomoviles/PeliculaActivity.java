package com.example.proyectomoviles;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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
    Button editar, eliminar;
    EditText txtBuscar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pelicula);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tomarFoto), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        lista = findViewById(R.id.ListViewMenu);
        txtBuscar = findViewById(R.id.txtBuscar);
        inicializarBtn();

        datos = new ArrayList<>();
        adapter = new CustomAdapterPelicula(this, datos);
        lista.setAdapter(adapter);

        cargarPeliculas();

        lista.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            itemseleccionado = position;

            for (int i = 0; i < lista.getChildCount(); i++) {
                lista.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
            }
            view.setBackgroundColor(Color.GRAY);
            activarBtn();
        });
    }

    public void Agregar(View view) {
        Intent intent = new Intent(this, CrearPelicula.class);
        startActivity(intent);

    }

    private void cargarPeliculas() {
        AdminDB admin = new AdminDB(this, "Proyecto", null, 2);
        SQLiteDatabase bd = admin.getReadableDatabase();

        Cursor fila = bd.rawQuery("SELECT * FROM pelicula", null);
        datos.clear();
        if (fila.moveToFirst()) {
            do {
                int codigo = fila.getInt(0);
                String titulo = fila.getString(1);
                int duracion = fila.getInt(2);
                String genero = fila.getString(3);
                byte[] imagen = fila.getBlob(4);
                byte[] audio = fila.getBlob(5);
                String latitud = fila.getString(6);
                String longitud = fila.getString(7);

                Pelicula p = new Pelicula(
                        codigo, titulo, duracion, genero,
                        imagen, audio, latitud, longitud
                );

                datos.add(p);

            } while (fila.moveToNext());
        } else {
            Toast.makeText(this, getString(R.string.toast_noregistro), Toast.LENGTH_LONG).show();
        }

        fila.close();
        bd.close();
        adapter.notifyDataSetChanged();
    }

    public void Eliminar(View view) {
        if (itemseleccionado >= 0) {
            Pelicula peliculaSeleccionada = (Pelicula) adapter.getItem(itemseleccionado);

            EliminarPorCodigo(peliculaSeleccionada.getCodigo());

            adapter.remove(peliculaSeleccionada);
            adapter.notifyDataSetChanged();

            View itemresaltado = lista.getChildAt(itemseleccionado);
            if (itemresaltado != null) {
                itemresaltado.setBackgroundColor(0);
            }
            itemseleccionado = -1;
            editar.setEnabled(false);
            eliminar.setEnabled(false);

        } else {
            Toast.makeText(this, getString(R.string.toast_seleccionedatos), Toast.LENGTH_LONG).show();
        }
    }

    public void EliminarPorCodigo(int codigo) {
        AdminDB admin = new AdminDB(this, "Proyecto", null, 2);
        SQLiteDatabase BaseDatos = admin.getWritableDatabase();
        String codigoString = String.valueOf(codigo);
        if (codigo > 0) {
            int registrosEliminados = BaseDatos.delete("pelicula", "codigo=?", new String[]{codigoString});
            BaseDatos.close();

            if (registrosEliminados > 0) {
                Toast.makeText(this, getString(R.string.toast_seelimino), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.toast_seleccionedatos), Toast.LENGTH_LONG).show();
        }
    }

    private void inicializarBtn() {
        editar = findViewById(R.id.btnEditar);
        eliminar = findViewById(R.id.btnEliminar);
        editar.setEnabled(false);
        eliminar.setEnabled(false);
    }

    private void activarBtn() {
        editar.setEnabled(true);
        eliminar.setEnabled(true);

    }

    public void Buscar(View view) {
        String nombretxt = txtBuscar.getText().toString().trim();
        AdminDB admin = new AdminDB(this, "Proyecto", null, 2);
        SQLiteDatabase BaseDatos = admin.getWritableDatabase();

        Cursor fila;
        if (nombretxt.isEmpty()) {
            fila = BaseDatos.rawQuery("SELECT * FROM pelicula", null);
        } else {
            fila = BaseDatos.rawQuery(
                    "SELECT * FROM pelicula WHERE titulo = ?",
                    new String[]{nombretxt}
            );
        }

        adapter.clear();

        if (fila.moveToFirst()) {
            do {
                int codigo = fila.getInt(0);
                String titulo = fila.getString(1);
                int duracion = fila.getInt(2);
                String genero = fila.getString(3);
                byte[] imagen = fila.getBlob(4);
                byte[] audio = fila.getBlob(5);
                String latitud = fila.getString(6);
                String longitud = fila.getString(7);

                Pelicula p = new Pelicula(codigo, titulo, duracion, genero, imagen, audio, latitud, longitud);
                adapter.add(p);

            } while (fila.moveToNext());

            Toast.makeText(this, getString(R.string.toast_registoencontrado), Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(this, getString(R.string.toast_noregistro), Toast.LENGTH_LONG).show();
        }
        fila.close();
        BaseDatos.close();
    }


    public void Editar(View view) {
        if (itemseleccionado >= 0) {
            Pelicula peliculaSeleccionada = (Pelicula) adapter.getItem(itemseleccionado);

            Intent intent = new Intent(this, CrearPelicula.class);
            intent.putExtra("codigo", peliculaSeleccionada.getCodigo());
            intent.putExtra("titulo", peliculaSeleccionada.getTitulo());
            intent.putExtra("duracion", peliculaSeleccionada.getDuracion());
            intent.putExtra("genero", peliculaSeleccionada.getGenero());
            intent.putExtra("modoEdicion", true);

            startActivity(intent);
        } else {
            Toast.makeText(this, getString(R.string.toast_seleccionedatos), Toast.LENGTH_LONG).show();
        }
    }
    protected void onResume() {
        super.onResume();
        datos.clear();
        cargarPeliculas();
    }
}