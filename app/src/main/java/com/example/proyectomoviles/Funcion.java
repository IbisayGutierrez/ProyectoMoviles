package com.example.proyectomoviles;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Calendar;

public class Funcion extends AppCompatActivity {

    EditText txtCodigoPelicula, txtFecha, txtHora, txtCedula, txtAsiento;
    TextView txtTituloPelicula, txtNombreCliente;
    ListView listDetalleFuncion;

    ArrayList<ItemFuncion> listaFunciones;
    ArrayAdapter<String> adapter;
    ArrayList<String> listaParaMostrar;

    int posicionSeleccionada = -1;
    String peliculaTitulo = "";
    int peliculaCodigo = -1;

    AdminDB conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_funcion);

        conn = new AdminDB(this, "Proyecto", null, 1);

        txtCodigoPelicula = findViewById(R.id.txtCodigoPelicula);
        txtFecha = findViewById(R.id.txtFecha);
        txtHora = findViewById(R.id.txtHora);
        txtCedula = findViewById(R.id.txtCedula);
        txtAsiento = findViewById(R.id.txtAsiento);

        txtTituloPelicula = findViewById(R.id.txtTituloPelicula);
        txtNombreCliente = findViewById(R.id.txtNombreCliente);

        listDetalleFuncion = findViewById(R.id.listDetalleFuncion);

        listaFunciones = new ArrayList<>();
        listaParaMostrar = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, listaParaMostrar);
        listDetalleFuncion.setAdapter(adapter);
        listDetalleFuncion.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        listDetalleFuncion.setOnItemClickListener((parent, view, position, id) -> {
            posicionSeleccionada = position;
            ItemFuncion item = listaFunciones.get(position);

            txtCodigoPelicula.setText(String.valueOf(item.codigoPelicula));
            txtTituloPelicula.setText(item.tituloPelicula);
            txtFecha.setText(item.fecha);
            txtHora.setText(item.hora);
            txtCedula.setText(item.cedula);
            txtNombreCliente.setText(item.nombreCliente);
            txtAsiento.setText(item.asiento);

            peliculaCodigo = item.codigoPelicula;
            peliculaTitulo = item.tituloPelicula;
        });
    }

    public void seleccionarFecha(View view) {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (dp, y, m, d) ->
                txtFecha.setText(d + "/" + (m + 1) + "/" + y),
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    public void seleccionarHora(View view) {
        Calendar c = Calendar.getInstance();
        new TimePickerDialog(this, (tp, h, m) ->
                txtHora.setText(h + ":" + String.format("%02d", m)),
                c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true)
                .show();
    }

    public void btnBuscarPelicula_Click(View view) {
        String codigo = txtCodigoPelicula.getText().toString().trim();

        if (codigo.isEmpty()) {
            Toast.makeText(this, "Ingrese un código de película", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT codigo, titulo FROM pelicula WHERE codigo=?",
                new String[]{codigo});

        if (cursor.moveToFirst()) {
            peliculaCodigo = cursor.getInt(0);
            peliculaTitulo = cursor.getString(1);
            txtTituloPelicula.setText(peliculaTitulo);
            Toast.makeText(this, "Película encontrada", Toast.LENGTH_SHORT).show();
        } else {
            txtTituloPelicula.setText("Película no encontrada");
            peliculaCodigo = -1;
            peliculaTitulo = "";
            Toast.makeText(this, "No existe película con ese código", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
        db.close();
    }

    public void btnBuscarCliente_Click(View view) {
        String cedula = txtCedula.getText().toString().trim();

        if (cedula.isEmpty()) {
            Toast.makeText(this, "Ingrese una cédula", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT nombre FROM cliente WHERE cedula=?",
                new String[]{cedula});

        if (cursor.moveToFirst()) {
            txtNombreCliente.setText(cursor.getString(0));
        } else {
            txtNombreCliente.setText("Cliente no existe");
            Toast.makeText(this, "No existe cliente con esa cédula", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
        db.close();
    }

    public void btnAgregar_Click(View view) {
        String fecha = txtFecha.getText().toString().trim();
        String hora = txtHora.getText().toString().trim();
        String cedula = txtCedula.getText().toString().trim();
        String nombreCliente = txtNombreCliente.getText().toString().trim();
        String asiento = txtAsiento.getText().toString().trim();

        if (fecha.isEmpty() || hora.isEmpty()) {
            Toast.makeText(this, "Seleccione fecha y hora", Toast.LENGTH_SHORT).show();
            return;
        }

        if (peliculaCodigo == -1 || peliculaTitulo.isEmpty()) {
            Toast.makeText(this, "Primero busque una película válida", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cedula.isEmpty() || nombreCliente.isEmpty() || nombreCliente.equals("Nombre cliente")
                || nombreCliente.equals("Cliente no existe")) {
            Toast.makeText(this, "Primero busque un cliente válido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (asiento.isEmpty()) {
            Toast.makeText(this, "Ingrese el número de asiento", Toast.LENGTH_SHORT).show();
            return;
        }

        for (ItemFuncion item : listaFunciones) {
            if (item.asiento.equalsIgnoreCase(asiento) && item.fecha.equals(fecha)
                    && item.hora.equals(hora) && item.codigoPelicula == peliculaCodigo) {
                Toast.makeText(this, "Este asiento ya está ocupado para esta función", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        ItemFuncion nuevaFuncion = new ItemFuncion(fecha, hora, cedula, nombreCliente,
                peliculaCodigo, peliculaTitulo, asiento);
        listaFunciones.add(nuevaFuncion);
        listaParaMostrar.add(nuevaFuncion.toString());
        adapter.notifyDataSetChanged();

        Toast.makeText(this, "Función agregada a la lista", Toast.LENGTH_SHORT).show();

        txtCedula.setText("");
        txtNombreCliente.setText("Nombre cliente");
        txtAsiento.setText("");
    }

    public void btnEliminar_Click(View view) {
        if (posicionSeleccionada == -1) {
            Toast.makeText(this, "Seleccione una función de la lista", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Eliminar")
                .setMessage("¿Desea eliminar esta función de la lista?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    listaFunciones.remove(posicionSeleccionada);
                    listaParaMostrar.remove(posicionSeleccionada);
                    adapter.notifyDataSetChanged();
                    posicionSeleccionada = -1;
                    limpiarCampos();
                    Toast.makeText(this, "Función eliminada de la lista", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .show();
    }

    public void btnEditar_Click(View view) {
        if (posicionSeleccionada == -1) {
            Toast.makeText(this, "Seleccione una función de la lista para editar", Toast.LENGTH_SHORT).show();
            return;
        }

        String fecha = txtFecha.getText().toString().trim();
        String hora = txtHora.getText().toString().trim();
        String cedula = txtCedula.getText().toString().trim();
        String nombreCliente = txtNombreCliente.getText().toString().trim();
        String asiento = txtAsiento.getText().toString().trim();

        if (fecha.isEmpty() || hora.isEmpty() || asiento.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (peliculaCodigo == -1) {
            Toast.makeText(this, "Busque una película válida", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cedula.isEmpty() || nombreCliente.equals("Cliente no existe")) {
            Toast.makeText(this, "Busque un cliente válido", Toast.LENGTH_SHORT).show();
            return;
        }

        for (int i = 0; i < listaFunciones.size(); i++) {
            if (i != posicionSeleccionada) {
                ItemFuncion item = listaFunciones.get(i);
                if (item.asiento.equalsIgnoreCase(asiento) && item.fecha.equals(fecha)
                        && item.hora.equals(hora) && item.codigoPelicula == peliculaCodigo) {
                    Toast.makeText(this, "Este asiento ya está ocupado para esta función", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }

        ItemFuncion funcionActualizada = new ItemFuncion(fecha, hora, cedula, nombreCliente,
                peliculaCodigo, peliculaTitulo, asiento);
        listaFunciones.set(posicionSeleccionada, funcionActualizada);
        listaParaMostrar.set(posicionSeleccionada, funcionActualizada.toString());
        adapter.notifyDataSetChanged();

        Toast.makeText(this, "Función actualizada", Toast.LENGTH_SHORT).show();
        posicionSeleccionada = -1;
        limpiarCampos();
    }

    public void btnGuardarFuncion_Click(View view) {
        if (listaFunciones.isEmpty()) {
            Toast.makeText(this, "Agregue al menos una función antes de guardar", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Guardar Funciones")
                .setMessage("¿Desea guardar " + listaFunciones.size() + " función(es) en la base de datos?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    int funcionesGuardadas = 0;

                    for (ItemFuncion item : listaFunciones) {
                        long idFuncion = conn.insertarFuncion(item.fecha, item.hora, item.codigoPelicula);

                        if (idFuncion != -1) {
                            conn.insertarDetalle((int) idFuncion, item.cedula, item.asiento);
                            funcionesGuardadas++;
                        }
                    }

                    Toast.makeText(this, "✅ " + funcionesGuardadas + " función(es) guardada(s)", Toast.LENGTH_LONG).show();
                    limpiarTodo();
                })
                .setNegativeButton("No", null)
                .show();
    }

    public void btnConsultar_Click(View view) {
        Intent intent = new Intent(this, ConsultarFunciones.class);
        startActivity(intent);
    }

    public void btnLimpiar_Click(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Limpiar")
                .setMessage("¿Desea limpiar todos los campos y la lista?")
                .setPositiveButton("Sí", (dialog, which) -> limpiarTodo())
                .setNegativeButton("No", null)
                .show();
    }

    private void limpiarTodo() {
        limpiarCampos();
        listaFunciones.clear();
        listaParaMostrar.clear();
        adapter.notifyDataSetChanged();
        posicionSeleccionada = -1;
        peliculaCodigo = -1;
        peliculaTitulo = "";
    }

    private void limpiarCampos() {
        txtCodigoPelicula.setText("");
        txtFecha.setText("");
        txtHora.setText("");
        txtCedula.setText("");
        txtAsiento.setText("");
        txtTituloPelicula.setText("Título película");
        txtNombreCliente.setText("Nombre cliente");
    }

    public void btnSalir_Click(View view) {
        if (!listaFunciones.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle("Salir")
                    .setMessage("Hay funciones sin guardar. ¿Desea salir de todos modos?")
                    .setPositiveButton("Sí", (dialog, which) -> finish())
                    .setNegativeButton("No", null)
                    .show();
        } else {
            finish();
        }
    }
}