package com.example.proyectomoviles;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.ByteArrayOutputStream;

public class CrearPelicula extends AppCompatActivity {

    EditText txtCodigo, txtTitulo,txtDuracion, txtGenero;
    boolean modoEdicion = false;
    int codigoOriginal;
    private ActivityResultLauncher<Intent> lanzadorTomarFoto;
    private Bitmap imagenBitmap;
    private ImageView vistaImagen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_crear_pelicula);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tomarFoto), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        txtCodigo = findViewById(R.id.txtCodigo);
        txtTitulo = findViewById(R.id.txtTitulo);
        txtDuracion = findViewById(R.id.txtDuracion);
        txtGenero = findViewById(R.id.txtGenero);
        vistaImagen = findViewById(R.id.imagen);

        Intent intent = getIntent();
        if (intent != null && intent.getBooleanExtra("modoEdicion", false)) {

            modoEdicion = true;
            codigoOriginal = intent.getIntExtra("codigo", -1);
            txtCodigo.setText(String.valueOf(codigoOriginal));
            txtTitulo.setText(intent.getStringExtra("titulo"));
            txtDuracion.setText(String.valueOf(intent.getIntExtra("duracion", 0)));
            txtGenero.setText(intent.getStringExtra("genero"));
            txtCodigo.setEnabled(false);
            cargarImagen();
        }

        lanzadorTomarFoto = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                resultado -> {
                    if (resultado.getResultCode() == RESULT_OK) {
                        imagenBitmap = (Bitmap) resultado.getData().getExtras().get("data");
                        vistaImagen.setImageBitmap(imagenBitmap);
                    }
                }
        );
    }

    public void tomarFoto(View vista) {
        Intent intentTomarFoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        lanzadorTomarFoto.launch(intentTomarFoto);
    }

    public void cargarImagen() {
        AdminDB admin = new AdminDB(this, "Proyecto", null, 2);
        SQLiteDatabase BaseDatos = admin.getReadableDatabase();

        Cursor cursor = BaseDatos.rawQuery(
                "SELECT imagen FROM pelicula WHERE codigo = ?",
                new String[]{String.valueOf(codigoOriginal)}
        );
        if (cursor.moveToFirst()) {
            byte[] byteImagen = cursor.getBlob(0);

            if (byteImagen != null) {
                imagenBitmap = android.graphics.BitmapFactory.decodeByteArray(byteImagen, 0, byteImagen.length);
                vistaImagen.setImageBitmap(imagenBitmap);
            }
        }
        cursor.close();
        BaseDatos.close();
    }

    public void Insertar(View view) {

        String codigoStr = txtCodigo.getText().toString().trim();
        String titulo = txtTitulo.getText().toString().trim();
        String duracionStr = txtDuracion.getText().toString().trim();
        String genero = txtGenero.getText().toString().trim();

        if (codigoStr.isEmpty() || titulo.isEmpty() || duracionStr.isEmpty() || genero.isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_insertartodo), Toast.LENGTH_LONG).show();
            return;
        }

        int codigo = Integer.parseInt(codigoStr);
        int duracion = Integer.parseInt(duracionStr);
        if (!modoEdicion && imagenBitmap == null) {
            Toast.makeText(this, getString(R.string.toast_foto), Toast.LENGTH_LONG).show();
            return;
        }

        if (modoEdicion) {
            boolean actualizo = Actualizar();
            if (actualizo) finish();
            return;
        }
        if (codigo > 0 && duracion > 0) {
            Registrar(codigo, titulo, duracion, genero);
            txtCodigo.setText("");
            txtTitulo.setText("");
            txtDuracion.setText("");
            txtGenero.setText("");
            vistaImagen.setImageBitmap(null);
            imagenBitmap = null;

        } else {
            Toast.makeText(this, getString(R.string.toast_insertartodo), Toast.LENGTH_LONG).show();
        }
    }


    public void Registrar(int codigo, String titulo, int duracion, String genero)
    {
        AdminDB admin = new AdminDB (this, "Proyecto", null, 2);
        SQLiteDatabase BaseDatos = admin.getWritableDatabase();

        Cursor fila = BaseDatos.rawQuery("SELECT codigo FROM pelicula WHERE codigo = " + codigo, null);
        if (fila.moveToFirst()) {
            Toast.makeText(this, getString(R.string.toast_existecodigo), Toast.LENGTH_LONG).show();
        } else {
            ContentValues registro = new ContentValues();
            registro.put("codigo", codigo);
            registro.put("titulo", titulo);
            registro.put("duracion", duracion);
            registro.put("genero", genero);

            if (imagenBitmap != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                imagenBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                registro.put("imagen", stream.toByteArray());
            } else {
                registro.put("imagen", (byte[]) null);
            }

            BaseDatos.insert("pelicula", null, registro);
            Toast.makeText(this, getString(R.string.toast_registroexitoso), Toast.LENGTH_LONG).show();
        }
        fila.close();
        BaseDatos.close();
    }


    public boolean Actualizar() {

        String nuevoTitulo = txtTitulo.getText().toString().trim();
        String nuevaDuracionTxt = txtDuracion.getText().toString().trim();
        String nuevoGenero = txtGenero.getText().toString().trim();

        if (nuevoTitulo.isEmpty() || nuevaDuracionTxt.isEmpty() || nuevoGenero.isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_insertartodo), Toast.LENGTH_LONG).show();
            return false;
        }

        int nuevaDuracion = Integer.parseInt(nuevaDuracionTxt);

        AdminDB admin = new AdminDB(this, "Proyecto", null, 2);
        SQLiteDatabase BaseDatos = admin.getWritableDatabase();

        ContentValues registro = new ContentValues();
        registro.put("titulo", nuevoTitulo);
        registro.put("duracion", nuevaDuracion);
        registro.put("genero", nuevoGenero);
        if (imagenBitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imagenBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            registro.put("imagen", stream.toByteArray());
        }
        int filas = BaseDatos.update("pelicula", registro, "codigo=?", new String[]{String.valueOf(codigoOriginal)});
        BaseDatos.close();
        if (filas > 0) {
            Toast.makeText(this, getString(R.string.toast_actualizacionexitosa), Toast.LENGTH_LONG).show();
            return true;
        } else {
            Toast.makeText(this, getString(R.string.toast_noactualizacion), Toast.LENGTH_LONG).show();
            return false;
        }
    }


    public void Regresar(View view) {
        finish();
    }

}