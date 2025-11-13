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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
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
            view.setBackgroundColor(Color.GREEN);
            activarBtn();
        });
    }

    public void Regresar(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }

    public void Agregar(View view) {
        Intent intent = new Intent(this, CrearPelicula.class);
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
        } else {
            Toast.makeText(getApplicationContext(), "Debe seleccionar un item", Toast.LENGTH_SHORT).show();
        }
    }

    public void EliminarPorCodigo(int codigo) {
        AdminDB admin = new AdminDB(this, "Proyecto", null, 1);
        SQLiteDatabase BaseDatos = admin.getWritableDatabase();
        String codigoString = String.valueOf(codigo);
        if (codigo > 0) {
            int registrosEliminados = BaseDatos.delete("pelicula", "codigo=?", new String[]{codigoString});
            BaseDatos.close();

            if (registrosEliminados > 0) {
                Toast.makeText(getApplicationContext(), "Registros eliminados correctamente", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Falta el nombre para eliminar", Toast.LENGTH_LONG).show();
        }
    }

    private void inicializarBtn() {
        editar = findViewById(R.id.btnEditar);
        eliminar = findViewById(R.id.btnEliminar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            editar.setForeground(getDrawable(R.drawable.edit_disable));
            eliminar.setForeground(getDrawable(R.drawable.delete_disable));
        }
        editar.setEnabled(false);
        eliminar.setEnabled(false);
    }

    private void activarBtn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            editar.setForeground(getDrawable(R.drawable.edit_enable));
            eliminar.setForeground(getDrawable(R.drawable.delete_enable));
        }
        editar.setEnabled(true);
        eliminar.setEnabled(true);

    }

    public void Buscar(View view) {
        String nombretxt = txtBuscar.getText().toString().trim();
        AdminDB admin = new AdminDB(this, "Proyecto", null, 1);
        SQLiteDatabase BaseDatos = admin.getWritableDatabase();

        Cursor fila;
        if (nombretxt.isEmpty()) {
            fila = BaseDatos.rawQuery("SELECT * FROM pelicula", null);
        } else {
            fila = BaseDatos.rawQuery("SELECT * FROM pelicula WHERE titulo='" + nombretxt + "'", null);
        }

        adapter.clear();

        if (fila.moveToFirst()) {
            do {
                int codigo = fila.getInt(0);
                String titulo = fila.getString(1);
                int duracion = fila.getInt(2);
                String genero = fila.getString(3);

                Pelicula p = new Pelicula(codigo, titulo, duracion, genero);
                adapter.add(p);
            } while (fila.moveToNext());
            Toast.makeText(getApplicationContext(), "Registros encontrados", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "No se encontraron registros", Toast.LENGTH_LONG).show();
        }

        fila.close();
        BaseDatos.close();
    }
}