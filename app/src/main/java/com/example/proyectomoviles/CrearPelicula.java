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
    boolean modoEdicion = false;
    int codigoOriginal;

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

        Intent intent = getIntent();
        if (intent != null && intent.getBooleanExtra("modoEdicion", false)) {

            modoEdicion = true;
            codigoOriginal = intent.getIntExtra("codigo", -1);
            txtCodigo.setText(String.valueOf(codigoOriginal));
            txtTitulo.setText(intent.getStringExtra("titulo"));
            txtDuracion.setText(String.valueOf(intent.getIntExtra("duracion", 0)));
            txtGenero.setText(intent.getStringExtra("genero"));
            txtCodigo.setEnabled(false);
        }
    }

    public void Insertar(View view) {
        String titulo, genero;
        int codigo,duracion;
        codigo = Integer.parseInt(txtCodigo.getText().toString());
        titulo = txtTitulo.getText().toString();
        duracion = Integer.parseInt(txtDuracion.getText().toString());
        genero = txtGenero.getText().toString();
        if (modoEdicion) {
            boolean actualizo = Actualizar();
            if (actualizo) {
                finish();
            }
        } else if (codigo > 0 && !titulo.isEmpty() && duracion > 0 && !genero.isEmpty()) {
            Registrar(codigo, titulo, duracion, genero);
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
        AdminDB admin = new AdminDB (this, "Proyecto", null, 2);
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

    public boolean Actualizar() {

        String nuevoTitulo = txtTitulo.getText().toString().trim();
        String nuevaDuracionTxt = txtDuracion.getText().toString().trim();
        String nuevoGenero = txtGenero.getText().toString().trim();


        if (nuevoTitulo.isEmpty() || nuevaDuracionTxt.isEmpty() || nuevoGenero.isEmpty()) {
            Toast.makeText(this, "Debe completar todos los campos", Toast.LENGTH_LONG).show();
            return false;
        }
        int nuevaDuracion = Integer.parseInt(nuevaDuracionTxt);

        AdminDB admin = new AdminDB(this, "Proyecto", null, 2);
        SQLiteDatabase BaseDatos = admin.getWritableDatabase();

        ContentValues registro = new ContentValues();
        registro.put("titulo", nuevoTitulo);
        registro.put("duracion", nuevaDuracion);
        registro.put("genero", nuevoGenero);

        int filas = BaseDatos.update("pelicula", registro, "codigo=?", new String[]{String.valueOf(codigoOriginal)});
        BaseDatos.close();
        if (filas > 0) {
            Toast.makeText(this, "Película actualizada correctamente", Toast.LENGTH_LONG).show();
            return true;
        } else {
            Toast.makeText(this, "No se pudo actualizar la película", Toast.LENGTH_LONG).show();
            return false;
        }

    }

    public void Regresar(View view) {
        finish();
    }

}