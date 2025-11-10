package com.example.proyectomoviles;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    ListView listMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listMenu = findViewById(R.id.ListViewMenu);

        String[] titulos = {"Clientes", "Peliculas", "Funciones"};

        int[] iconos = {
                R.drawable.client,
                R.drawable.movie,
                R.drawable.function
        };

        MenuAdapter adapter = new MenuAdapter(this, titulos, iconos);
        listMenu.setAdapter(adapter);

        listMenu.setOnItemClickListener((AdapterView<?> parent, android.view.View view, int position, long id) -> {
            switch (position) {
                case 0:
                    Toast.makeText(this, "Abrir módulo Estudiante", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(this, "Abrir módulo Curso", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(this, "Abrir módulo Matrícula", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }
}
