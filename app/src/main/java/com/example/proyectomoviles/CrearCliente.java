package com.example.proyectomoviles;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

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
    public void Regresar(View view)
    {
        Intent intent= new Intent(this,Cliente.class);
        startActivity(intent);

    }

    public void login(View view)
    {
        Intent intent= new Intent(this,InicioSesion.class);
        startActivity(intent);

    }
}