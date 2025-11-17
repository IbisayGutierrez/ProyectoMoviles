package com.example.proyectomoviles;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ConsultarFunciones extends AppCompatActivity {

    ListView listFunciones, listDetalles;
    EditText txtBuscarFecha;

    ArrayList<FuncionCompleta> listaFunciones;
    ArrayList<String> listaParaMostrar;
    ArrayAdapter<String> adapterFunciones;

    ArrayList<String> listaDetalles;
    ArrayAdapter<String> adapterDetalles;

    int idFuncionSeleccionada = -1;
    int posicionSeleccionada = -1;

    AdminDB conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_consultar_funciones);

            conn = new AdminDB(this, "Proyecto", null, 1);

            inicializarVistas();
            cargarTodasLasFunciones();
        } catch (Exception e) {
            Toast.makeText(this, "Error al iniciar: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
            finish();
        }
    }

    private void inicializarVistas() {
        listFunciones = findViewById(R.id.listFunciones);
        listDetalles = findViewById(R.id.listDetalles);
        txtBuscarFecha = findViewById(R.id.txtBuscarFecha);

        listaFunciones = new ArrayList<>();
        listaParaMostrar = new ArrayList<>();
        adapterFunciones = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, listaParaMostrar);
        listFunciones.setAdapter(adapterFunciones);
        listFunciones.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        listaDetalles = new ArrayList<>();
        adapterDetalles = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaDetalles);
        listDetalles.setAdapter(adapterDetalles);

        listFunciones.setOnItemClickListener((parent, view, position, id) -> {
            try {
                posicionSeleccionada = position;
                FuncionCompleta funcion = listaFunciones.get(position);
                idFuncionSeleccionada = funcion.id;
                cargarDetallesFuncion(funcion.id);
            } catch (Exception e) {
                Toast.makeText(this, "Error al seleccionar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        });
    }

    private void cargarTodasLasFunciones() {
        listaFunciones.clear();
        listaParaMostrar.clear();

        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = conn.getReadableDatabase();
            cursor = db.rawQuery(
                    "SELECT f.id, f.fecha, f.hora, f.codigo_pelicula, p.titulo " +
                            "FROM funcion f " +
                            "INNER JOIN pelicula p ON f.codigo_pelicula = p.codigo " +
                            "ORDER BY f.id DESC", null);

            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(0);
                    String fecha = cursor.getString(1);
                    String hora = cursor.getString(2);
                    int codigoPelicula = cursor.getInt(3);
                    String tituloPelicula = cursor.getString(4);

                    FuncionCompleta funcion = new FuncionCompleta(id, fecha, hora, codigoPelicula, tituloPelicula);
                    listaFunciones.add(funcion);
                    listaParaMostrar.add("ID: " + id + " | " + fecha + " - " + hora + "\n" + tituloPelicula);
                } while (cursor.moveToNext());

                adapterFunciones.notifyDataSetChanged();
                Toast.makeText(this, "Se encontraron " + listaFunciones.size() + " funciones", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No hay funciones guardadas", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar funciones: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
    }

    private void cargarDetallesFuncion(int idFuncion) {
        listaDetalles.clear();

        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = conn.getReadableDatabase();
            cursor = db.rawQuery(
                    "SELECT d.cedula, c.nombre, d.asiento " +
                            "FROM detalle_funcion d " +
                            "INNER JOIN cliente c ON d.cedula = c.cedula " +
                            "WHERE d.idfuncion = ?",
                    new String[]{String.valueOf(idFuncion)});

            if (cursor.moveToFirst()) {
                do {
                    String cedula = cursor.getString(0);
                    String nombre = cursor.getString(1);
                    String asiento = cursor.getString(2);


                    if (cedula == null) cedula = "Sin cédula";
                    if (nombre == null) nombre = "Sin nombre";
                    if (asiento == null) asiento = "Sin asiento";

                    listaDetalles.add(cedula + " - " + nombre + " | Asiento: " + asiento);
                } while (cursor.moveToNext());

                adapterDetalles.notifyDataSetChanged();
                Toast.makeText(this, "Clientes: " + listaDetalles.size(), Toast.LENGTH_SHORT).show();
            } else {
                listaDetalles.add("No hay clientes registrados en esta función");
                adapterDetalles.notifyDataSetChanged();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar detalles: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
            listaDetalles.add("Error al cargar clientes");
            adapterDetalles.notifyDataSetChanged();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
    }

    public void btnBuscar_Click(View view) {
        String fechaBuscar = txtBuscarFecha.getText().toString().trim();

        if (fechaBuscar.isEmpty()) {
            Toast.makeText(this, "Ingrese una fecha para buscar", Toast.LENGTH_SHORT).show();
            return;
        }

        listaFunciones.clear();
        listaParaMostrar.clear();

        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = conn.getReadableDatabase();
            cursor = db.rawQuery(
                    "SELECT f.id, f.fecha, f.hora, f.codigo_pelicula, p.titulo " +
                            "FROM funcion f " +
                            "INNER JOIN pelicula p ON f.codigo_pelicula = p.codigo " +
                            "WHERE f.fecha LIKE ? " +
                            "ORDER BY f.id DESC",
                    new String[]{"%" + fechaBuscar + "%"});

            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(0);
                    String fecha = cursor.getString(1);
                    String hora = cursor.getString(2);
                    int codigoPelicula = cursor.getInt(3);
                    String tituloPelicula = cursor.getString(4);

                    FuncionCompleta funcion = new FuncionCompleta(id, fecha, hora, codigoPelicula, tituloPelicula);
                    listaFunciones.add(funcion);
                    listaParaMostrar.add("ID: " + id + " | " + fecha + " - " + hora + "\n" + tituloPelicula);
                } while (cursor.moveToNext());

                adapterFunciones.notifyDataSetChanged();
                Toast.makeText(this, "Se encontraron " + listaFunciones.size() + " funciones", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No se encontraron funciones con esa fecha", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error al buscar: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
    }

    public void btnMostrarTodas_Click(View view) {
        txtBuscarFecha.setText("");
        listaDetalles.clear();
        adapterDetalles.notifyDataSetChanged();
        idFuncionSeleccionada = -1;
        posicionSeleccionada = -1;
        cargarTodasLasFunciones();
    }

    public void btnEliminarFuncion_Click(View view) {
        if (idFuncionSeleccionada == -1) {
            Toast.makeText(this, "Seleccione una función para eliminar", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Eliminar Función")
                .setMessage("¿Está seguro de eliminar esta función y todos sus detalles?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    SQLiteDatabase db = null;
                    try {
                        db = conn.getWritableDatabase();


                        db.delete("detalle_funcion", "idfuncion=?",
                                new String[]{String.valueOf(idFuncionSeleccionada)});


                        db.delete("funcion", "id=?",
                                new String[]{String.valueOf(idFuncionSeleccionada)});

                        Toast.makeText(this, "Función eliminada correctamente", Toast.LENGTH_SHORT).show();

                        listaDetalles.clear();
                        adapterDetalles.notifyDataSetChanged();
                        idFuncionSeleccionada = -1;
                        posicionSeleccionada = -1;
                        cargarTodasLasFunciones();
                    } catch (Exception e) {
                        Toast.makeText(this, "Error al eliminar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    } finally {
                        if (db != null) db.close();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    public void btnVolver_Click(View view) {
        finish();
    }

    private class FuncionCompleta {
        int id;
        String fecha;
        String hora;
        int codigoPelicula;
        String tituloPelicula;

        FuncionCompleta(int id, String fecha, String hora, int codigoPelicula, String tituloPelicula) {
            this.id = id;
            this.fecha = fecha;
            this.hora = hora;
            this.codigoPelicula = codigoPelicula;
            this.tituloPelicula = tituloPelicula;
        }
    }
}