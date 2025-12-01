package com.example.proyectomoviles;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.google.android.material.appbar.MaterialToolbar;

public class CrearPelicula extends AppCompatActivity {

    EditText txtCodigo, txtTitulo,txtDuracion, txtGenero,txtlatitud, txtlongitud;

    // NUEVOS BOTONES DE AUDIO
    Button btnIniciarGrabacion, btnDetenerGrabacion, btnReproducirAudio;

    static final int REQ_UBICACION = 100;
    private static final int REQUEST_PERMISSION_CODE = 1000; // NUEVO

    boolean modoEdicion = false;
    int codigoOriginal;
    private ActivityResultLauncher<Intent> lanzadorTomarFoto;
    private Bitmap imagenBitmap;
    private ImageView vistaImagen;


    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private boolean isRecording = false;
    private boolean isPlaying = false;
    private String outputFile;
    private byte[] audioData = null;

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
        txtlatitud = findViewById(R.id.txtlatitud);
        txtlongitud = findViewById(R.id.txtlongitud);


        btnIniciarGrabacion = findViewById(R.id.btnIniciarGrabacion);
        btnDetenerGrabacion = findViewById(R.id.btnDetenerGrabacion);
        btnReproducirAudio = findViewById(R.id.btnReproducirAudio);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_PERMISSION_CODE);
        }


        outputFile = getExternalFilesDir(null).getAbsolutePath() + "/audio_pelicula.3gp";

        mediaRecorder = new MediaRecorder();
        mediaPlayer = new MediaPlayer();


        btnDetenerGrabacion.setEnabled(false);
        btnReproducirAudio.setEnabled(false);

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
            cargarUbicacion();
            cargarAudio();
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

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });
    }


    public void iniciarGrabacion(View view) {
        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(outputFile);
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;

            btnIniciarGrabacion.setEnabled(false);
            btnDetenerGrabacion.setEnabled(true);
            btnReproducirAudio.setEnabled(false);
            Toast.makeText(this, "Grabación iniciada", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al iniciar grabación: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void detenerGrabacion(View view) {
        if (mediaRecorder != null && isRecording) {
            try {
                mediaRecorder.stop();
                mediaRecorder.reset();
                isRecording = false;


                File audioFile = new File(outputFile);
                audioData = new byte[(int) audioFile.length()];
                FileInputStream fis = new FileInputStream(audioFile);
                fis.read(audioData);
                fis.close();

                btnIniciarGrabacion.setEnabled(true);
                btnDetenerGrabacion.setEnabled(false);
                btnReproducirAudio.setEnabled(true);
                Toast.makeText(this, "Audio grabado exitosamente", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al detener grabación", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void reproducirAudio(View view) {
        try {
            if (audioData != null) {

                File tempFile = new File(getExternalFilesDir(null), "temp_audio.3gp");
                FileOutputStream fos = new FileOutputStream(tempFile);
                fos.write(audioData);
                fos.close();

                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(tempFile.getAbsolutePath());
                mediaPlayer.prepare();
                mediaPlayer.start();
                isPlaying = true;

                btnReproducirAudio.setEnabled(false);
                Toast.makeText(this, "Reproduciendo audio", Toast.LENGTH_SHORT).show();


                mediaPlayer.setOnCompletionListener(mp -> {
                    btnReproducirAudio.setEnabled(true);
                    isPlaying = false;
                });
            } else {
                Toast.makeText(this, "No hay audio para reproducir", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al reproducir audio", Toast.LENGTH_SHORT).show();
        }
    }

    public void detenerReproduccion(View view) {
        if (mediaPlayer != null && isPlaying) {
            try {
                mediaPlayer.stop();
                mediaPlayer.reset();
                isPlaying = false;

                btnReproducirAudio.setEnabled(true);
                Toast.makeText(this, "Reproducción detenida", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void cargarAudio() {
        AdminDB admin = new AdminDB(this, "Proyecto", null, 2);
        SQLiteDatabase BaseDatos = admin.getReadableDatabase();

        Cursor cursor = BaseDatos.rawQuery(
                "SELECT audio FROM pelicula WHERE codigo = ?",
                new String[]{String.valueOf(codigoOriginal)}
        );

        if (cursor.moveToFirst()) {
            audioData = cursor.getBlob(0);
            if (audioData != null && audioData.length > 0) {
                btnReproducirAudio.setEnabled(true);
                Toast.makeText(this, "Audio cargado", Toast.LENGTH_SHORT).show();
            }
        }
        cursor.close();
        BaseDatos.close();
    }



    public void AbrirMapa(View view) {
        Intent i = new Intent(this, UbicacionActivity.class);

        String lat = txtlatitud.getText().toString().trim();
        String lon = txtlongitud.getText().toString().trim();

        if (!lat.isEmpty() && !lon.isEmpty()) {
            i.putExtra("latitud_actual", lat);
            i.putExtra("longitud_actual", lon);
        }

        startActivityForResult(i, REQ_UBICACION);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_UBICACION && resultCode == RESULT_OK && data != null) {
            String lat = data.getStringExtra("latitud");
            String lon = data.getStringExtra("longitud");

            txtlatitud.setText(lat);
            txtlongitud.setText(lon);
        }
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

    public void cargarUbicacion() {
        AdminDB admin = new AdminDB(this, "Proyecto", null, 2);
        SQLiteDatabase BaseDatos = admin.getReadableDatabase();

        Cursor cursor = BaseDatos.rawQuery(
                "SELECT latitud, longitud FROM pelicula WHERE codigo = ?",
                new String[]{String.valueOf(codigoOriginal)}
        );

        if (cursor.moveToFirst()) {
            String lat = cursor.getString(0);
            String lon = cursor.getString(1);

            if (lat != null) txtlatitud.setText(lat);
            if (lon != null) txtlongitud.setText(lon);
        }

        cursor.close();
        BaseDatos.close();
    }

    public void Insertar(View view) {

        String codigoStr = txtCodigo.getText().toString().trim();
        String titulo = txtTitulo.getText().toString().trim();
        String duracionStr = txtDuracion.getText().toString().trim();
        String genero = txtGenero.getText().toString().trim();
        String latitudStr = txtlatitud.getText().toString().trim();
        String longitudStr = txtlongitud.getText().toString().trim();

        if (codigoStr.isEmpty() || titulo.isEmpty() || duracionStr.isEmpty() || genero.isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_insertartodo), Toast.LENGTH_LONG).show();
            return;
        }

        if (latitudStr.isEmpty() || longitudStr.isEmpty()) {
            Toast.makeText(this, "Seleccione una ubicación en el mapa", Toast.LENGTH_LONG).show();
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
            txtlatitud.setText("");
            txtlongitud.setText("");
            audioData = null;


            btnIniciarGrabacion.setEnabled(true);
            btnDetenerGrabacion.setEnabled(false);
            btnReproducirAudio.setEnabled(false);

        } else {
            Toast.makeText(this, getString(R.string.toast_insertartodo), Toast.LENGTH_LONG).show();
        }
    }


    public void Registrar(int codigo, String titulo, int duracion, String genero)
    {
        String latitud = txtlatitud.getText().toString().trim();
        String longitud = txtlongitud.getText().toString().trim();
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
            registro.put("latitud", latitud);
            registro.put("longitud", longitud);

            if (imagenBitmap != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                imagenBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                registro.put("imagen", stream.toByteArray());
            } else {
                registro.put("imagen", (byte[]) null);
            }


            if (audioData != null) {
                registro.put("audio", audioData);
            } else {
                registro.put("audio", (byte[]) null);
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
        String nuevaLatitud = txtlatitud.getText().toString().trim();
        String nuevaLongitud = txtlongitud.getText().toString().trim();

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
        registro.put("latitud", nuevaLatitud);
        registro.put("longitud", nuevaLongitud);

        if (imagenBitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imagenBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            registro.put("imagen", stream.toByteArray());
        }


        if (audioData != null) {
            registro.put("audio", audioData);
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}