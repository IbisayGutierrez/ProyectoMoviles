package com.example.proyectomoviles;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CrearCliente extends AppCompatActivity {
    EditText txtCedula, txtNombre, txtContra, txtTelefono, txtCorreo;


    boolean modoEdicion=false;
    String cedulaOriginal;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_crear_cliente);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        txtCedula=findViewById(R.id.txtCedula);
        txtNombre=findViewById(R.id.txtNombre);
        txtContra=findViewById(R.id.txtContra);
        txtTelefono=findViewById(R.id.txtTelefono);
        txtCorreo=findViewById(R.id.txtCorreo);

        Intent intent = getIntent();
        if (intent != null && intent.getBooleanExtra("modoEdicion", false)) {
            modoEdicion = true;
            cedulaOriginal = intent.getStringExtra("cedula");

            // Rellenamos los campos con los datos del cliente
            txtCedula.setText(intent.getStringExtra("cedula"));
            txtNombre.setText(intent.getStringExtra("nombre"));
            txtTelefono.setText(intent.getStringExtra("telefono"));
            txtCorreo.setText(intent.getStringExtra("correo"));

            // Opcional: no permitir cambiar cédula
            txtCedula.setEnabled(false);

        }
    }



    public void Insertar(View view) {
        String cedula, nombre, contra, telefono, correo;
        cedula = txtCedula.getText().toString();
        nombre = txtNombre.getText().toString();
        contra = txtContra.getText().toString();
        telefono = txtTelefono.getText().toString();
        correo = txtCorreo.getText().toString();
        if (modoEdicion) {
            // 🔹 MODO EDICIÓN: solo actualizamos teléfono, correo y opcionalmente contraseña
            Actualizar();
            finish(); // volvemos a la lista
        } else {
            // 🔹 MODO CREACIÓN: validamos que todo tenga datos
            if (!cedula.isEmpty() && !nombre.isEmpty() && !contra.isEmpty() && !telefono.isEmpty() && !correo.isEmpty()) {
                Registrar(cedula, nombre, contra, telefono, correo);
                txtCedula.setText("");
                txtNombre.setText("");
                txtContra.setText("");
                txtTelefono.setText("");
                txtCorreo.setText("");
            } else {
                Toast.makeText(getApplicationContext(), "Por favor inserte todos los datos", Toast.LENGTH_LONG).show();
            }
        }

    }

    public void Registrar(String cedula, String nombre, String contra, String telefono, String correo)
    {
        AdminDB admin = new AdminDB (this, "Proyecto", null, 2);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();
        Cursor fila = BaseDeDatos.rawQuery("select cedula from cliente where cedula='"+cedula+"'", null);
        if (fila.moveToFirst()){
            Toast.makeText(getApplicationContext(),"Ya existe un cliente con esa cedula", Toast.LENGTH_LONG).show();
        }else {
            ContentValues registro = new ContentValues();
            registro.put("cedula", cedula);
            registro.put("nombre", nombre);
            registro.put("contraseña", contra);
            registro.put("telefono", telefono);
            registro.put("correo", correo);
            BaseDeDatos.insert("cliente", null, registro);
            Toast.makeText(this, "Se ha registrado el cliente con exito", Toast.LENGTH_LONG).show();
        }
        fila.close();
        BaseDeDatos.close();
    }

    public void Actualizar() {
        AdminDB admin = new AdminDB(this, "Proyecto", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        String nuevoNombre = txtNombre.getText().toString().trim();
        String nuevoTelefono = txtTelefono.getText().toString().trim();
        String nuevoCorreo   = txtCorreo.getText().toString().trim();
        String nuevaContra   = txtContra.getText().toString().trim();

        ContentValues registro = new ContentValues();

        // 🔹 Siempre actualizamos nombre, teléfono y correo
        registro.put("nombre", nuevoNombre);
        registro.put("telefono", nuevoTelefono);
        registro.put("correo", nuevoCorreo);

        // 🔹 La contraseña SOLO se actualiza si el usuario escribió algo
        if (!nuevaContra.isEmpty()) {
            registro.put("contraseña", nuevaContra);
        }

        int filas = BaseDeDatos.update("cliente", registro, "cedula=?", new String[]{cedulaOriginal});

        if (filas > 0) {
            Toast.makeText(this, "Cliente actualizado correctamente", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "No se pudo actualizar el cliente", Toast.LENGTH_LONG).show();
        }

        BaseDeDatos.close();
    }
    public void Regresar(View view) {
        finish();
    }


}