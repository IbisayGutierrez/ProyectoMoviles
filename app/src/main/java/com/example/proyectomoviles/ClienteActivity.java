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

public class ClienteActivity extends AppCompatActivity {
    ListView lista;
    CustomAdapterCliente adapter;
    ArrayList<Cliente> datos;
    int itemseleccionado = -1;
    Button editar, eliminar;

    EditText txtBuscar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cliente);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        lista = findViewById(R.id.ListViewCliente);
        inicializarBtn();
        txtBuscar = findViewById(R.id.txtBuscar2);

        datos = new ArrayList<>();
        adapter = new CustomAdapterCliente(this, datos);
        lista.setAdapter(adapter);

        cargarClientes();

        lista.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            itemseleccionado = position;

            for (int i = 0; i < lista.getChildCount(); i++) {
                lista.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
            }
            view.setBackgroundColor(0x9934B5E4);
            activarBtn();
        });


    }

    protected void onResume() {
        super.onResume();
        datos.clear();
        cargarClientes();
    }

    public void Regresar(View view)
    {
        Intent intent= new Intent(this,MainActivity.class);
        startActivity(intent);

    }

    public void Agregar(View view)
    {
        Intent intent= new Intent(this,CrearCliente.class);
        startActivity(intent);

    }

    private void cargarClientes() {
        AdminDB admin = new AdminDB(this,"Proyecto", null, 1);
        SQLiteDatabase bd = admin.getReadableDatabase();
        Cursor fila = bd.rawQuery("SELECT cedula, nombre, telefono, correo FROM cliente", null);
        if (fila.moveToFirst()){
            do {
                String cedula = fila.getString(0);
                String nombre = fila.getString(1);
                String telefono = fila.getString(2);
                String correo = fila.getString(3);
                Cliente c = new Cliente(cedula, nombre, telefono, correo);
                datos.add(c);
            } while (fila.moveToNext());
        }else {
            Toast.makeText(this, "No hay clientes registrados", Toast.LENGTH_LONG).show();
        }
        fila.close();
        bd.close();
        adapter.notifyDataSetChanged();
    }

    public void Eliminar(View view) {
        if (itemseleccionado >= 0) {
            Cliente clienteSeleccionado = (Cliente) adapter.getItem(itemseleccionado);
            EliminarPorCedula(clienteSeleccionado.getCedula());

            adapter.remove(clienteSeleccionado);
            adapter.notifyDataSetChanged();

            View itemresaltado = lista.getChildAt(itemseleccionado);
            if (itemresaltado != null) {
                itemresaltado.setBackgroundColor(0);
            }
            itemseleccionado = -1;
        } else {
            Toast.makeText(this, "Por favor seleccione un cliente para eliminar", Toast.LENGTH_LONG).show();
        }

    }

    public void EliminarPorCedula(String cedula) {
        AdminDB admin = new AdminDB(this, "Proyecto", null, 1);
        SQLiteDatabase BaseDatos = admin.getWritableDatabase();

        if (cedula != null && !cedula.isEmpty()) {
            int registrosEliminados = BaseDatos.delete("cliente", "cedula=?", new String[]{cedula});
            BaseDatos.close();

            if (registrosEliminados > 0) {
                Toast.makeText(getApplicationContext(), "Registros eliminados correctamente", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "No se encontró ningún registro con esa cedula", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Falta la cedula para eliminar", Toast.LENGTH_LONG).show();
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
            fila = BaseDatos.rawQuery("SELECT * FROM cliente", null);
        } else {
            fila = BaseDatos.rawQuery("SELECT * FROM cliente WHERE nombre='" + nombretxt + "'", null);
        }

        adapter.clear();

        if (fila.moveToFirst()) {
            do {
                String cedula = fila.getString(0);
                String nombre = fila.getString(1);
                String telefono = fila.getString(2);
                String correo = fila.getString(3);

                Cliente c = new Cliente(cedula, nombre, telefono, correo);
                adapter.add(c);
            } while (fila.moveToNext());
            Toast.makeText(getApplicationContext(), "Registros encontrados", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "No se encontraron registros", Toast.LENGTH_LONG).show();
        }

        fila.close();
        BaseDatos.close();
    }

    public void Editar(View view) {
        if (itemseleccionado >= 0) {
            Cliente clienteSeleccionado = (Cliente) adapter.getItem(itemseleccionado);

            Intent intent = new Intent(this, CrearCliente.class);
            intent.putExtra("cedula", clienteSeleccionado.getCedula());
            intent.putExtra("nombre", clienteSeleccionado.getNombre());
            intent.putExtra("telefono", clienteSeleccionado.getTelefono());
            intent.putExtra("correo", clienteSeleccionado.getCorreo());
            // Indicamos que estamos en modo edición
            intent.putExtra("modoEdicion", true);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Por favor seleccione un cliente para editar", Toast.LENGTH_LONG).show();
        }
    }

}