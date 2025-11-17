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

        conn = new AdminDB(this, "Proyecto", null, 2);

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

        listDetalleFuncion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
            }
        });
    }

    public void seleccionarFecha(View view) {
        Calendar c = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker dp, int y, int m, int d) {
                txtFecha.setText(d + "/" + (m + 1) + "/" + y);
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    public void seleccionarHora(View view) {
        Calendar c = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker tp, int h, int m) {
                txtHora.setText(h + ":" + String.format("%02d", m));
            }
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

    public void btnBuscarPelicula_Click(View view) {
        String codigo = txtCodigoPelicula.getText().toString().trim();

        if (codigo.isEmpty()) {
            Toast.makeText(this, getString(R.string.ingrese_codigo), Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT codigo, titulo FROM pelicula WHERE codigo=?",
                new String[]{codigo});

        if (cursor.moveToFirst()) {
            peliculaCodigo = cursor.getInt(0);
            peliculaTitulo = cursor.getString(1);
            txtTituloPelicula.setText(peliculaTitulo);
            Toast.makeText(this, getString(R.string.pelicula_encontrada), Toast.LENGTH_SHORT).show();
        } else {
            txtTituloPelicula.setText(getString(R.string.pelicula_no_encontrada));
            peliculaCodigo = -1;
            peliculaTitulo = "";
            Toast.makeText(this, getString(R.string.no_existe_pelicula), Toast.LENGTH_SHORT).show();
        }

        cursor.close();
        db.close();
    }

    public void btnBuscarCliente_Click(View view) {
        String cedula = txtCedula.getText().toString().trim();

        if (cedula.isEmpty()) {
            Toast.makeText(this, getString(R.string.ingrese_cedula), Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT nombre FROM cliente WHERE cedula=?",
                new String[]{cedula});

        if (cursor.moveToFirst()) {
            txtNombreCliente.setText(cursor.getString(0));
        } else {
            txtNombreCliente.setText(getString(R.string.cliente_no_existe));
            Toast.makeText(this, getString(R.string.no_existe_cliente), Toast.LENGTH_SHORT).show();
        }

        cursor.close();
        db.close();
    }

    public void btnAgregar_Click(View view) {
        String codigoPelicula = txtCodigoPelicula.getText().toString().trim();
        String fecha = txtFecha.getText().toString().trim();
        String hora = txtHora.getText().toString().trim();
        String cedula = txtCedula.getText().toString().trim();
        String nombreCliente = txtNombreCliente.getText().toString().trim();
        String asiento = txtAsiento.getText().toString().trim();

        if (codigoPelicula.isEmpty()) {
            Toast.makeText(this, getString(R.string.ingrese_codigo), Toast.LENGTH_SHORT).show();
            return;
        }

        if (fecha.isEmpty() || hora.isEmpty()) {
            Toast.makeText(this, getString(R.string.seleccione_fecha_hora), Toast.LENGTH_SHORT).show();
            return;
        }

        if (peliculaCodigo == -1 || peliculaTitulo.isEmpty() ||
                peliculaTitulo.equals(getString(R.string.titulo_pelicula_placeholder)) ||
                peliculaTitulo.equals(getString(R.string.pelicula_no_encontrada))) {
            Toast.makeText(this, getString(R.string.busque_pelicula_valida), Toast.LENGTH_SHORT).show();
            return;
        }

        if (cedula.isEmpty()) {
            Toast.makeText(this, getString(R.string.ingrese_cedula), Toast.LENGTH_SHORT).show();
            return;
        }

        if (nombreCliente.isEmpty() || nombreCliente.equals(getString(R.string.nombre_cliente))
                || nombreCliente.equals(getString(R.string.cliente_no_existe))) {
            Toast.makeText(this, getString(R.string.busque_cliente_valido), Toast.LENGTH_SHORT).show();
            return;
        }

        if (asiento.isEmpty()) {
            Toast.makeText(this, getString(R.string.ingrese_asiento), Toast.LENGTH_SHORT).show();
            return;
        }

        for (ItemFuncion item : listaFunciones) {
            if (item.cedula.equals(cedula) && item.fecha.equals(fecha)
                    && item.hora.equals(hora) && item.codigoPelicula == peliculaCodigo) {
                Toast.makeText(this, getString(R.string.cedula_repetida), Toast.LENGTH_LONG).show();
                return;
            }
        }

        for (ItemFuncion item : listaFunciones) {
            if (item.asiento.equalsIgnoreCase(asiento) && item.fecha.equals(fecha)
                    && item.hora.equals(hora) && item.codigoPelicula == peliculaCodigo) {
                Toast.makeText(this, getString(R.string.asiento_ocupado), Toast.LENGTH_SHORT).show();
                return;
            }
        }

        ItemFuncion nuevaFuncion = new ItemFuncion(fecha, hora, cedula, nombreCliente,
                peliculaCodigo, peliculaTitulo, asiento);
        listaFunciones.add(nuevaFuncion);
        listaParaMostrar.add(nuevaFuncion.toString());
        adapter.notifyDataSetChanged();

        Toast.makeText(this, getString(R.string.funcion_agregada), Toast.LENGTH_SHORT).show();

        txtCedula.setText("");
        txtNombreCliente.setText(getString(R.string.nombre_cliente));
        txtAsiento.setText("");
    }

    public void btnEliminar_Click(View view) {
        if (posicionSeleccionada == -1) {
            Toast.makeText(this, getString(R.string.seleccione_funcion), Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.titulo_eliminar));
        builder.setMessage(getString(R.string.mensaje_eliminar));
        builder.setPositiveButton(getString(R.string.si), new AlertDialog.OnClickListener() {
            @Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                listaFunciones.remove(posicionSeleccionada);
                listaParaMostrar.remove(posicionSeleccionada);
                adapter.notifyDataSetChanged();
                posicionSeleccionada = -1;
                limpiarCampos();
                Toast.makeText(Funcion.this, getString(R.string.funcion_eliminada), Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(getString(R.string.no), null);
        builder.show();
    }

    public void btnEditar_Click(View view) {
        if (posicionSeleccionada == -1) {
            Toast.makeText(this, getString(R.string.seleccione_para_editar), Toast.LENGTH_SHORT).show();
            return;
        }

        String codigoPelicula = txtCodigoPelicula.getText().toString().trim();
        String fecha = txtFecha.getText().toString().trim();
        String hora = txtHora.getText().toString().trim();
        String cedula = txtCedula.getText().toString().trim();
        String nombreCliente = txtNombreCliente.getText().toString().trim();
        String asiento = txtAsiento.getText().toString().trim();

        if (codigoPelicula.isEmpty()) {
            Toast.makeText(this, getString(R.string.ingrese_codigo), Toast.LENGTH_SHORT).show();
            return;
        }

        if (fecha.isEmpty() || hora.isEmpty() || asiento.isEmpty()) {
            Toast.makeText(this, getString(R.string.complete_campos), Toast.LENGTH_SHORT).show();
            return;
        }

        if (peliculaCodigo == -1) {
            Toast.makeText(this, getString(R.string.busque_pelicula_valida), Toast.LENGTH_SHORT).show();
            return;
        }

        if (cedula.isEmpty()) {
            Toast.makeText(this, getString(R.string.ingrese_cedula), Toast.LENGTH_SHORT).show();
            return;
        }

        if (nombreCliente.equals(getString(R.string.cliente_no_existe))
                || nombreCliente.equals(getString(R.string.nombre_cliente))) {
            Toast.makeText(this, getString(R.string.busque_cliente_valido), Toast.LENGTH_SHORT).show();
            return;
        }

        for (int i = 0; i < listaFunciones.size(); i++) {
            if (i != posicionSeleccionada) {
                ItemFuncion item = listaFunciones.get(i);
                if (item.cedula.equals(cedula) && item.fecha.equals(fecha)
                        && item.hora.equals(hora) && item.codigoPelicula == peliculaCodigo) {
                    Toast.makeText(this, getString(R.string.cedula_repetida), Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

        for (int i = 0; i < listaFunciones.size(); i++) {
            if (i != posicionSeleccionada) {
                ItemFuncion item = listaFunciones.get(i);
                if (item.asiento.equalsIgnoreCase(asiento) && item.fecha.equals(fecha)
                        && item.hora.equals(hora) && item.codigoPelicula == peliculaCodigo) {
                    Toast.makeText(this, getString(R.string.asiento_ocupado), Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }

        ItemFuncion funcionActualizada = new ItemFuncion(fecha, hora, cedula, nombreCliente,
                peliculaCodigo, peliculaTitulo, asiento);
        listaFunciones.set(posicionSeleccionada, funcionActualizada);
        listaParaMostrar.set(posicionSeleccionada, funcionActualizada.toString());
        adapter.notifyDataSetChanged();

        Toast.makeText(this, getString(R.string.funcion_actualizada), Toast.LENGTH_SHORT).show();
        posicionSeleccionada = -1;
        limpiarCampos();
    }

    public void btnGuardarFuncion_Click(View view) {
        if (listaFunciones.isEmpty()) {
            Toast.makeText(this, getString(R.string.agregar_una_funcion), Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.titulo_guardar));
        builder.setMessage(String.format(getString(R.string.mensaje_guardar), listaFunciones.size()));
        builder.setPositiveButton(getString(R.string.si), new AlertDialog.OnClickListener() {
            @Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                int funcionesGuardadas = 0;

                for (ItemFuncion item : listaFunciones) {
                    long idFuncion = conn.insertarFuncion(item.fecha, item.hora, item.codigoPelicula);

                    if (idFuncion != -1) {
                        conn.insertarDetalle((int) idFuncion, item.cedula, item.asiento);
                        funcionesGuardadas++;
                    }
                }

                Toast.makeText(Funcion.this, String.format(getString(R.string.funciones_guardadas_msg), funcionesGuardadas), Toast.LENGTH_LONG).show();
                limpiarTodo();
            }
        });
        builder.setNegativeButton(getString(R.string.no), null);
        builder.show();
    }

    public void btnConsultar_Click(View view) {
        Intent intent = new Intent(this, ConsultarFunciones.class);
        startActivity(intent);
    }

    public void btnLimpiar_Click(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.titulo_limpiar));
        builder.setMessage(getString(R.string.mensaje_limpiar));
        builder.setPositiveButton(getString(R.string.si), new AlertDialog.OnClickListener() {
            @Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                limpiarTodo();
            }
        });
        builder.setNegativeButton(getString(R.string.no), null);
        builder.show();
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
        txtTituloPelicula.setText(getString(R.string.titulo_pelicula_placeholder));
        txtNombreCliente.setText(getString(R.string.nombre_cliente));
    }

    public void btnSalir_Click(View view) {
        if (!listaFunciones.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.titulo_salir));
            builder.setMessage(getString(R.string.mensaje_salir));
            builder.setPositiveButton(getString(R.string.si), new AlertDialog.OnClickListener() {
                @Override
                public void onClick(android.content.DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setNegativeButton(getString(R.string.no), null);
            builder.show();
        } else {
            finish();
        }
    }
}