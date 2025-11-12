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

public class CrearPelicula extends AppCompatActivity {

    EditText txtCodigo, txtTitulo,txtDuracion, txtGenero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_crear_pelicula);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        txtCodigo = findViewById(R.id.txtCodigo);
        txtTitulo = findViewById(R.id.txtTitulo);
        txtDuracion = findViewById(R.id.txtDuracion);
        txtGenero = findViewById(R.id.txtGenero);
    }

    public void Insertar(View view) {
        String titulo, genero;
        int codigo,duracion;
        codigo = Integer.parseInt(txtCodigo.getText().toString());
        titulo = txtTitulo.getText().toString();
        duracion = Integer.parseInt(txtDuracion.getText().toString());
        genero = txtGenero.getText().toString();
        if (codigo > 0||!titulo.isEmpty()||duracion > 0||!genero.isEmpty()) {
            Registrar(codigo,titulo,duracion,genero);
            txtCodigo.setText("");
            txtTitulo.setText("");
            txtDuracion.setText("");
            txtGenero.setText("");

        } else {
            Toast.makeText(getApplicationContext(), "Por favor inserte todos los datos", Toast.LENGTH_LONG).show();
        }

    }

    public void Registrar(int codigo, String titulo, int duracion, String genero)
    {
        AdminDB admin = new AdminDB (this, "Proyecto", null, 1);
        SQLiteDatabase BaseDatos = admin.getWritableDatabase();
        Cursor fila = BaseDatos.rawQuery("SELECT codigo FROM pelicula WHERE codigo = " + codigo, null);
        if (fila.moveToFirst()) {
            Toast.makeText(getApplicationContext(), "Ya existe una película con ese código", Toast.LENGTH_LONG).show();
        } else {
            ContentValues registro = new ContentValues();
            registro.put("codigo", codigo);
            registro.put("titulo", titulo);
            registro.put("duracion", duracion);
            registro.put("genero", genero);
            BaseDatos.insert("pelicula", null, registro);
            Toast.makeText(getApplicationContext(), "La película se insertó correctamente", Toast.LENGTH_LONG).show();
        }
        fila.close();
        BaseDatos.close();
    }

    public void Regresar(View view)
    {
        Intent intent= new Intent(this, PeliculaActivity.class);
        startActivity(intent);

    }

}