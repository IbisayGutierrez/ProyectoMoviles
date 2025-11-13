package com.example.proyectomoviles;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class InicioSesionActivity extends AppCompatActivity {
    private EditText txtCedulaIS, txtContraIS;
    private Button btnIniciar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inicio_sesion);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        txtCedulaIS = findViewById(R.id.txtCedulaIS);
        txtContraIS = findViewById(R.id.txtContraIS);
        btnIniciar = findViewById(R.id.btnIniciar);

        }

    public void IniciarSesion(View view) {
        validarLogin();
    }

    private void validarLogin() {
        String cedula = txtCedulaIS.getText().toString().trim();
        String contraseña = txtContraIS.getText().toString().trim();

        if (cedula.isEmpty() || contraseña.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        AdminDB admin = new AdminDB(this, "Proyecto", null, 1);
        SQLiteDatabase db = admin.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM cliente WHERE cedula = ? AND contraseña = ?",
                new String[]{cedula, contraseña}
        );

        if (cursor.moveToFirst()) {
            Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Cédula o contraseña incorrecta", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
        db.close();
    }

    public void Registar(View view)
    {
        Intent intent= new Intent(this, CrearCliente.class);
        startActivity(intent);

    }

}