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
    }

    public void Insertar(View view) {
        String cedula, nombre, contra, telefono, correo;
        cedula = txtCedula.getText().toString();
        nombre = txtNombre.getText().toString();
        contra = txtContra.getText().toString();
        telefono = txtTelefono.getText().toString();
        correo = txtCorreo.getText().toString();
        if (!cedula.isEmpty() || !nombre.isEmpty() || !contra.isEmpty() || !telefono.isEmpty() || !correo.isEmpty()) {
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

    public void Registrar(String cedula, String nombre, String contra, String telefono, String correo)
    {
        AdminDB admin = new AdminDB (this, "Proyecto", null, 1);
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
    public void Regresar(View view) {
        finish();
    }


}